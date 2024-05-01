package com.apiecommerce.tokoto.product;

import com.apiecommerce.tokoto.categories.Categories;
import com.apiecommerce.tokoto.categories.CategoriesRepository;
import com.apiecommerce.tokoto.categories.CategoriesRequest;
import com.apiecommerce.tokoto.config.JwtService;
import com.apiecommerce.tokoto.email.EmailDetails;
import com.apiecommerce.tokoto.email.EmailService;
import com.apiecommerce.tokoto.exception.UnauthorizationException;
//import com.apiecommerce.tokoto.product.order.*;
import com.apiecommerce.tokoto.storage.StorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.apiecommerce.tokoto.token.TokenRepository;
import com.apiecommerce.tokoto.user.User;
import com.apiecommerce.tokoto.user.UserRepository;
import com.apiecommerce.tokoto.utils.ImageUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing products.
 */
@Service
@CacheConfig(cacheNames = "tokotoCache")
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketNames;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${host}")
    private String HOST;

    @CacheEvict(value = "tokotoCache", allEntries = true)
    @Transactional
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> files) throws IOException, ServletException {
        // Mengambil token yang berada di Bearer Token
        String token = extractTokenFromBearerHeader();
        if (token == null) {
            throw new UnauthorizationException("Bearer token not found in Authorization Header");
        }

        String userEmail = getUserDetailsFromToken(token);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        User user = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<byte[]> imageDatas = new ArrayList<>();
        List<String> imageNames = new ArrayList<>();
        List<String> imageTypes = new ArrayList<>();

        for (MultipartFile file : files) {
            byte[] imageData = file.getBytes();
            imageDatas.add(imageData);
            imageNames.add(ImageUtils.hashFileName(file.getOriginalFilename(), file.getBytes()));
            imageTypes.add(file.getContentType());

            // Simpan gambar ke direktori upload
            Path imagePath = Paths.get(uploadPath, imageNames.get(imageNames.size() - 1));
            Files.write(imagePath, imageData);
            storageService.uploadImageToMinio(new ByteArrayInputStream(imageData), imageNames.get(imageNames.size() - 1), file.getContentType());
        }
        List<CategoriesRequest> categories = request.getCategories();
        List<Categories> categoryList = new ArrayList<>();
        for (CategoriesRequest categoriesRequest : categories) {
            Categories existingCategory = categoriesRepository.findByNameCategory(categoriesRequest.getNameCategory());
            if (existingCategory != null) {
                categoryList.add(existingCategory);
            } else {
                throw new RuntimeException("Category dengan nama : " + categoriesRequest.getNameCategory() + " tidak ditemukan!");
            }
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Harga tidak boleh negatif!");
        }

        Product product = Product.builder()
                .title(request.getTitle())
                .rating(request.getRating())
                .price(request.getPrice())
                .qty(request.getQty())
                .discount(request.getDiscount())
                .description(request.getDescription())
                .details(request.getDetails())
                .categories(categoryList)
                .units(request.getUnits())
		        .imageName(imageNames)
		        .imageData(imageDatas)
		        .imageType(imageTypes)
                .uploadedBy(user)
                .build();

        productRepository.save(product);

        if (product == null) {
            throw new RuntimeException("Produk gagal dibuat, periksa kembali request yang anda masukkan!");
        }

        return toProductResponse(product);
    }

    private String getUserDetailsFromToken(String token) {
        String userEmail = jwtService.extractUsername(token);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(token, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return userEmail;
    }

    private String extractTokenFromBearerHeader() throws ServletException, IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = null;
        FilterChain filterChain = null;
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return null;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
		return jwt;
    }

    @Transactional(readOnly = true)
    public byte[] downloadImageFromMinioBucket(String objectName) throws IOException {
        try {
            String bucketName = bucketNames;
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("Error occurred while downloading image from Minio: " + e.getMessage());
        }
    }

    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest request, List<MultipartFile> files) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product dengan ID : " + productId + " tidak ditemukan!"));

	        if (files != null && !files.isEmpty()) {
		        for (String imageName : product.getImageName()) {
			        Path imagePath = Paths.get(uploadPath, imageName);
			        Files.deleteIfExists(imagePath);
		        }

		        // Tambahkan gambar baru
		        for (MultipartFile file : files) {
			        String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename(), file.getBytes());
			        byte[] imageData = ImageUtils.compressImage(file.getBytes());

			        product.getImageName().add(hashedFileName);
			        product.getImageType().add(file.getContentType());
			        product.getImageData().add(imageData);

			        // Simpan gambar baru ke direktori upload
			        Path imagePath = Paths.get(uploadPath, hashedFileName);
			        Files.write(imagePath, imageData);
		        }
	        }

            if (request.getRating() != null) {
                product.setRating(request.getRating());
            }

            if (request.getUnits() != null) {
                product.setUnits(request.getUnits());
            } else {
                List<Unit> existingUnits = product.getUnits();
                if (existingUnits != null) {
                    product.setUnits(existingUnits);
                }
            }

            if (request.getTitle() != null) {
                product.setTitle(request.getTitle());
            }

            if (request.getPrice() != null) {
                product.setPrice(request.getPrice());
            }

            if (request.getQty() != null) {
                product.setQty(request.getQty());
            }

            if (request.getDiscount() != null) {
                product.setDiscount(request.getDiscount());
            }

            if (request.getDescription() != null) {
                product.setDescription(request.getDescription());
            }

            List<CategoriesRequest> categoriesRequestList = request.getCategories();
            if (categoriesRequestList != null) {
                List<Categories> categoriesList = new ArrayList<>();
                for (CategoriesRequest categoriesRequest : categoriesRequestList) {
                    String categoryName = categoriesRequest.getNameCategory();
                    Categories existingCategory = categoriesRepository.findByNameCategory(categoryName);
                    if (existingCategory != null) {
                        categoriesList.add(existingCategory);
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category dengan nama : " + categoryName + " tidak ditemukan!");
                    }
                }
                product.setCategories(categoriesList);
            }

            productRepository.save(product);

            return toProductResponse(product);
        } catch (IOException e) {
            throw new RuntimeException("Gagal mengupdate produk: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product With ID : " + id + " Not Found!"));

        productRepository.delete(product);
    }

    @Cacheable(value = "tokotoCache", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product With ID : " + id + " Not Found!"));

        return toProductResponse(product);
    }

    @Cacheable("tokotoCache")
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProduct() {
        List<Product> product = productRepository.findAll();
        return product.stream().map(this::toProductResponse).toList();
    }

    @Cacheable(value = "tokotoCache", key = "#keyword")
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllByTitle(String keyword) {
        List<Product> products = productRepository.findAllByTitleContaining(keyword);
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllByCategory(String keyword) {
        List<Categories> categories = categoriesRepository.findByNameCategoryContaining(keyword);

        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = productRepository.findAllByCategoriesIn(categories);

        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByPrice(BigDecimal price) {
        List<Product> products = productRepository.findAllByPrice(price);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product With Price : " + price + " Not Found!");
        }
        return products.stream().map(this::toProductResponse).toList();
    }

    private ProductResponse toProductResponse(Product product) {
        List<Categories> categories = product.getCategories();

        return ProductResponse.builder()
                .units(product.getUnits().stream().map(Unit::toString).toList())
                .id(product.getId())
                .title(product.getTitle())
                .rating(product.getRating())
                .price(product.getPrice())
                .qty(product.getQty())
                .discount(product.getDiscount())
                .sold(product.getSold())
                .description(product.getDescription())
                .details(product.getDetails())
                .categories(categories.stream().map(Categories::getNameCategory).toList())
                .imageName(product.getImageName())
		        .uploadedBy(product.getUploadedBy().getEmail())
                .build();
    }

    public byte[] exportProductsToCSV() throws IOException {
        List<Product> products = productRepository.findAll();
        StringWriter writer = new StringWriter();
        writeToCSV(products, writer);
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void writeToCSV(List<Product> products, Writer writer) throws IOException {
        CSVWriter csvWriter = new CSVWriter(writer);
        String[] header = { "ID", "Title", "Rating", "Price", "Qty", "Description", "Categories", "Details", "Uploaded_By" };
        csvWriter.writeNext(header);

        for (Product product : products) {
            String[] rowData = {
                    String.valueOf(product.getId()),
                    product.getTitle(),
                    String.valueOf(product.getRating()),
                    String.valueOf(product.getPrice()),
                    String.valueOf(product.getQty()),
                    product.getDescription(),
                    product.getCategories().stream().map(Categories::getNameCategory).collect(Collectors.joining(",")),
//                    product.getUploadedBy();
            };
            csvWriter.writeNext(rowData);
        }
        csvWriter.close();
    }

    @Transactional
    public void importProductsFromCSV(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Product product = new Product();
                product.setTitle(nextRecord[1]);
                product.setPrice(new BigDecimal(nextRecord[2]));
                product.setQty(Integer.parseInt(nextRecord[3]));
                product.setDescription(nextRecord[4]);
                String categoryString = nextRecord[5];
                Categories category = categoriesRepository.findByNameCategory(categoryString);
                if (category == null) {
                    category = new Categories();
                    category.setNameCategory(categoryString);
                    categoriesRepository.save(category);
                }
                List<Categories> categoriesList = new ArrayList<>();
                categoriesList.add(category);
                product.setCategories(categoriesList);
                productRepository.save(product);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public void checkStockProduct(String productId) {
        Product product = findById(productId).toEntity();
        if (product.getQty() <= 10) {
            String subject = "Low Stock Product Notification for " + product.getTitle();
            String recipient = "dearlyfebrianoi@gmail.com";
            String msgBody = "Product " + product.getTitle() + " with ID " + product.getId()
                    + " is running low in stock. Only " + product.getQty() + " left in stock.";

            EmailDetails emailDetails = new EmailDetails(recipient, msgBody, subject);
            emailService.sendSimpleMail(emailDetails);
        }
    }

    @CacheEvict(value = "tokotoCache", allEntries = true)
    public void clearAllCache() {
        System.out.println("***** CLEAR ALL CACHE *****");
    }

    /**
     *
     * Demo Feature Order Item Product
     *
     * 2 Feature
     * 1. Menambahkan order kedalam keranjang
     * 2. Membuka keranjang dan menampilkan item product yang di dimasukkan (order) ke dalam keranjang
     *
     */
//
//    @Transactional
//    public void addToCart(OrderDetailRequest request, String productId) throws ServletException, IOException {
//        Product product = findById(productId).toEntity();
//
//        // Check apakah product masih memiliki quantity lebih dari 0
//        if (product.getQty() <= 0) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tidak bisa menambahkan data product kedalam order dikarenakan data product tersebut telah habis!");
//        }
//
//        // Check siapakah user yang memasukkan data product kedalam order
//        // Mengambil token yang berada di Bearer Token
//        String token = extractTokenFromBearerHeader();
//        if (token == null) {
//            throw new UnauthorizationException("Bearer token not found in Authorization Header");
//        }
//
//        String userEmail = getUserDetailsFromToken(token);
//        Optional<User> userOptional = userRepository.findByEmail(userEmail);
//        User user = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        OrderDetail orderDetail = new OrderDetail();
//        orderDetail.setProduct(product);
//        orderDetail.setQuantity(request.getQuantity());
//        orderDetail.setUser(user);
//
//        orderDetailrepository.save(orderDetail);
//    }
//
//    @Transactional(readOnly = true)
//    public List<OrderResponse> getOrderList() throws ServletException, IOException {
//        String token = extractTokenFromBearerHeader();
//        if (token == null) {
//            throw new UnauthorizationException("Bearer token not found in Authorization Header");
//        }
//
//        String userEmail = getUserDetailsFromToken(token);
//        Optional<User> userOptional = userRepository.findByEmail(userEmail);
//        User user = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        List<Order> orders = orderRepository.findByUser(user);
//
//        return orders.stream()
//                .map(order -> {
//                    List<OrderDetail> orderDetails = order.getOrderDetails();
//                    List<OrderDetailsResponse> orderDetailsResponses = orderDetails.stream()
//                            .map(orderDetail -> {
//                                Product product = orderDetail.getProduct();
//                                return OrderDetailsResponse.builder()
//                                        .id(orderDetail.getId())
//                                        .productId(product.getId())
//                                        .quantity(orderDetail.getQuantity())
//                                        .build();
//                            })
//                            .collect(Collectors.toList());
//
//                    return OrderResponse.builder()
//                            .id(order.getId())
//                            .totalPrice(OrderUtils.calculateTotalPrice(order))
//                            .orderDetailsResponses(orderDetailsResponses)
//                            .user(user.getEmail())
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
}