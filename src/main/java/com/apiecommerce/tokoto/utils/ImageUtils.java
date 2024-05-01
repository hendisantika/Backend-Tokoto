package com.apiecommerce.tokoto.utils;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {

    /**
     * Mengompresi gambar menjadi array byte[]
     * @param data Data dari gambar dalam bentuk array byte[]
     * @return Data gambar yang telah dikompresi dalam bentuk array byte[]
     */
    public static byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    /**
     * Mendekompresi gambar dari array byte[]
     * @param data Data gambar yang dikompresi dalam bentuk array byte[]
     * @return Data gambar yang telah dikompresi dalam bentuk array byte[]
     */
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    /**
     * Generates a hashed file name based on the original file name and its content.
     * @param originalFileName The original file name.
     * @param fileContent The content of the file.
     * @return The hashed file name.
     */
    public static String hashFileName(String originalFileName, byte[] fileContent) {
        try {
            String extension = "";
            int lastIndexOfDot = originalFileName.lastIndexOf('.');
            if (lastIndexOfDot > 0) {
                extension = originalFileName.substring(lastIndexOfDot);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Include file content in the hash calculation
            md.update(fileContent);

            byte[] digest = md.digest();

            // Convert Digest to hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Take the first 8 characters of the hexadecimal string
            String hashedFileName = hexString.toString().substring(0, 8);

            if (!hashedFileName.endsWith(extension)) {
                hashedFileName += extension;
            }

            return hashedFileName;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}