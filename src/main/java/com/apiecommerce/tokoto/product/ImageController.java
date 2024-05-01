package com.apiecommerce.tokoto.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ProductService productService;

    /**
     * Mengambil dan mengembalikan gambar dari sistem berdasarkan nama file.
     * @param imageName Nama file gambar yang ingin diambil.
     * @return Byte array yang mewakili konten gambar.
     * @throws IOException Jika terjadi kesalahan saat membaca gambar.
     */
    @GetMapping(path = "/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImageFromFileSystem(@PathVariable String imageName) throws IOException {
        return productService.downloadImageFromMinioBucket(imageName);
    }
}