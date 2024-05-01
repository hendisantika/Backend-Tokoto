package com.apiecommerce.tokoto.product;

//import com.apiecommerce.tokoto.product.order.OrderDetailRequest;
//import com.apiecommerce.tokoto.product.order.OrderResponse;
import com.apiecommerce.tokoto.product.supply.SupplyRequest;
import com.apiecommerce.tokoto.product.supply.SupplyResponse;
import com.apiecommerce.tokoto.product.supply.SupplyService;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Kelas ini bertanggung jawab untuk menangani permintaan terkait produk.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplyService supplyService;

    @Value("${host}")
    private String HOST;


    /**
     * Metode ini digunakan untuk membuat produk baru
     *
     * @param request => Model Attribute Berguna untuk menambahkan data yang berada pada Model Product Request
     * @param files        Berkas gambar baru untuk produk (opsional).
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @ModelAttribute ProductRequest request,
            @RequestPart("file") List<MultipartFile> files
    ) throws IOException, ServletException {
        ProductResponse response = productService.createProduct(request, files);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Memperbarui produk yang ada.
     *
     * @param request => Model Attribute Berguna untuk menambahkan data yang berada pada Model Product Request
     * @param files        Berkas gambar baru untuk produk (opsional).
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang berhasil diperbarui.
     * @throws IOException jika terjadi kesalahan saat membaca atau menyimpan gambar.
     */
    @PatchMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> update(
            @PathVariable String id,
            @ModelAttribute ProductRequest request,
            @RequestPart(name = "file", required = false) List<MultipartFile> files
    ) throws IOException {
        ProductResponse response;
        if (files != null && !files.isEmpty()) {
            response = productService.updateProduct(id, request, files);
        } else {
            response = productService.updateProduct(id, request, null);
        }

        return ResponseEntity.ok().body(response);
    }


    /**
     * Menghapus produk berdasarkan ID.
     *
     * @param id ID produk yang akan dihapus.
     * @return ResponseEntity tanpa isi jika produk berhasil dihapus.
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@RequestParam String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Menemukan produk berdasarkan ID.
     *
     * @param id ID produk yang akan ditemukan.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang ditemukan.
     */
    @GetMapping(path = "/find/byId/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> findById(@RequestParam String id) {
        ProductResponse response = productService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<ProductResponse>> list() {
        List<ProductResponse> response = productService.findAllProduct();
        return WebResponse.<List<ProductResponse>>builder().data(response).build();
    }

    /**
     * Menemukan produk berdasarkan nama produk.
     *
     * @param keyword Judul produk yang akan ditemukan.
     * @return ResponseEntity yang berisi objek ProductResponse dari produk yang ditemukan.
     */
    @GetMapping(path = "/find/byTitleKey/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> findAllByTitle(@RequestParam String keyword) {
        List<ProductResponse> listResponse = productService.findAllByTitle(keyword);
        return ResponseEntity.ok().body(listResponse);
    }

    @GetMapping(path = "/clearAllCache")
    public String clearAllCache() {
        productService.clearAllCache();
        return "Data Successfully cleared all cache";
    }

    /**
     *
     * @param keyword Mencari Product yang memiliki category yang sama
     * @return mengembalikan response List dalam bentuk Array Object (JSON)
     */
    @GetMapping(path = "/find/byCategoryList/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> findAllByCategory(@RequestParam String keyword) {
        List<ProductResponse> responseList = productService.findAllByCategory(keyword);
        return ResponseEntity.ok().body(responseList);
    }

    /**
     * Menemukan produk berdasarkan harga.
     *
     * @param price Harga produk yang akan dicari.
     * @return ResponseEntity yang berisi daftar objek ProductResponse dari produk dengan harga yang sesuai.
     */
    @GetMapping(path = "/find/byPrice/{price}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> findByPrice(@RequestParam BigDecimal price) {
        List<ProductResponse> response = productService.findByPrice(price);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Mengekspor produk ke dalam bentuk CSV.
     *
     * @return ResponseEntity yang berisi byte array dari CSV
     */
    @PostMapping(path = "/export")
    public ResponseEntity<byte[]> exportProductToCSV() {
        try {
            byte[] csvData = productService.exportProductsToCSV();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=products.csv")
                    .body(csvData);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to export products", e);
        }
    }

    /**
     * Mengimpor produk dari CSV ke dalam database.
     *
     * @param file berkas file baru
     * @return ResponseEntity yang berisi daftar object ProductResponse dari produk yang diimpor
     */
    @PostMapping(path = "/import")
    public ResponseEntity<String> importProductsFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            productService.importProductsFromCSV(file);
            return ResponseEntity.ok().body("Products imported successfully");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to import products", e);
        }
    }

    /**
     *
     * @param page menentukan di page manakah setiap data yanga ada
     * @param size menentukan paginate dari seluruh data
     * @return mengembalikan response dalam bentuk page ResponseEntity
     */
    @GetMapping(path = "/supply/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SupplyResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SupplyResponse> responses = supplyService.findAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping(path = "/supply/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupplyResponse> newSupply(
            @PathVariable String productId,
            @RequestBody SupplyRequest request
    ) {
        SupplyResponse response = supplyService.updatedSupply(productId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/supply/pay/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupplyResponse> removeQuantity(
            @PathVariable String productId,
            @RequestBody SupplyRequest request
    ) {
        SupplyResponse response = supplyService.removeQuantity(productId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/checkQuantity")
    public ResponseEntity<Void> checkStockProduct(@RequestParam String productId) {
        productService.checkStockProduct(productId);
        return ResponseEntity.ok().build();
    }

    // Method Controlller Order Methods
//    @PostMapping(path = "/addToCart/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> addToCart(
//            @PathVariable String productId,
//            @RequestBody OrderDetailRequest request
//    ) throws ServletException, IOException {
//        productService.addToCart(request, productId);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping(path = "/listOrderCart", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<OrderResponse>> listOrderCart() throws ServletException, IOException {
//        List<OrderResponse> response = productService.getOrderList();
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
}