package com.apiecommerce.tokoto.config;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseBackup {

    // Konfigurasi database
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_NAME = "inventaris";

    // Konfigurasi direktori untuk menyimpan backup
    private static final String BACKUP_DIR = "C:/Management/backup/";

    public static void main(String[] args) {
        // Membuat ScheduledExecutorService dengan satu thread
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Menjadwalkan backup untuk pertama kali dan setiap 1 minggu berikutnya
        scheduler.scheduleAtFixedRate(DatabaseBackup::backupDatabase, 0, 7, TimeUnit.DAYS);
    }

    public static void backupDatabase() {
        try {
            // Generate nama file backup dengan timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String backupFileName = DB_NAME + "_backup_" + timestamp + ".sql";

            // Jalankan perintah untuk melakukan backup menggunakan perintah mysqldump
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mysqldump",
                    "-u" + DB_USER,
                    "-p" + DB_PASSWORD,
                    DB_NAME
            );

            // Redirect output ke file
            processBuilder.redirectOutput(new File(BACKUP_DIR + backupFileName));

            // Eksekusi perintah
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            // Cek apakah proses berhasil atau tidak
            if (exitCode == 0) {
                System.out.println("Backup database berhasil disimpan di: " + BACKUP_DIR + backupFileName);
            } else {
                System.out.println("Backup database gagal.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}