package com.apiecommerce.tokoto.categories;

import com.apiecommerce.tokoto.storage.StorageService;
import com.apiecommerce.tokoto.utils.ImageUtils;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Kelas layanan untuk menangani operasi yang terkait dengan Kategori.
 */
@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Value("${minio.bucket.name}")
    private String bucketNames;

    @Autowired
    private StorageService storageService;

    @Autowired
    private MinioClient minioClient;

    /**
     * Membuat kategori baru berdasarkan permintaan yang diberikan.
     *
     * @param  categoriesRequest  Objek permintaan yang berisi nama kategori.
     * @return                    Objek respons yang mewakili kategori yang dibuat.
     */
    @Transactional
    public CategoriesResponse createCategories(CategoriesRequest categoriesRequest, MultipartFile file) throws IOException {
        String hashedFileName = ImageUtils.hashFileName(file.getOriginalFilename(), file.getBytes());
        byte[] imageData = file.getBytes();
        byte[] imageDatas = ImageUtils.compressImage(imageData);

        Categories categories = new Categories();
        categories.setNameCategory(categoriesRequest.getNameCategory());
        categories.setImageName(hashedFileName);
        categories.setImageType(file.getContentType());
        categories.setImageData(imageDatas);

        storageService.uploadImageToMinio(new ByteArrayInputStream(imageData), hashedFileName, file.getContentType());

        categoriesRepository.save(categories);

        return toCategoriesResponse(categories);
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


    /**
     * Memperbarui kategori yang ada berdasarkan id dan permintaan yang diberikan.
     *
     * @param  id                 Id dari kategori yang akan diperbarui.
     * @param  categoriesRequest  Objek permintaan yang berisi nama kategori baru.
     * @return                    Objek respons yang mewakili kategori yang diperbarui.
     */
    @Transactional
    public CategoriesResponse updateCategories(Long id, CategoriesRequest categoriesRequest) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        if (categories.getNameCategory() != null) {
            categories.setNameCategory(categoriesRequest.getNameCategory());

        }

        Categories updatedCategories = categoriesRepository.save(categories);
        return toCategoriesResponse(updatedCategories);
    }

    /**
     * Menghapus kategori berdasarkan id yang diberikan.
     *
     * @param  id  Id dari kategori yang akan dihapus.
     */
    @Transactional
    public void deleteCategories(Long id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);

        categoriesRepository.delete(categories);
    }

    /**
     * Mendapatkan kategori berdasarkan id yang diberikan.
     *
     * @param  id  Id dari kategori yang dicari.
     * @return     Objek respons yang mewakili kategori yang dicari.
     */
    @Transactional
    public CategoriesResponse getCategoriesById(Long id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        return toCategoriesResponse(categories);
    }

    /**
     * Menampilkan daftar kategori.
     *
     * @param  page  Nomor halaman yang akan ditampilkan.
     * @param  size  Jumlah kategori per halaman.
     * @return       Halaman yang berisi daftar objek respons kategori.
     */
    @Transactional(readOnly = true)
    public Page<CategoriesResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Categories> categoriesPage = categoriesRepository.findAll(pageable);
        return categoriesPage.map(this::toCategoriesResponse);
    }

    /**
     * Mengubah objek Kategori menjadi objek Respons Kategori.
     *
     * @param  categories  Objek Kategori yang akan diubah.
     * @return             Objek Respons Kategori hasil konversi.
     */
    private CategoriesResponse toCategoriesResponse(Categories categories) {
        return CategoriesResponse.builder()
                .id(categories.getId())
                .nameCategory(categories.getNameCategory())
                .imageName(categories.getImageName())
                .build();
    }
}