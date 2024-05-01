# Kebijakan Penggunaan Proyek

Selamat datang di proyek ini! Untuk memastikan kolaborasi yang produktif dan lingkungan yang menyenangkan bagi semua pengguna, kami mmeiliki beberapa kebijakan penggunaan yang perlu diperhatikan: 

1. **Kode Etik**:
- Kami menganjurkan semua kontributor untuk berperilaku sopan dan menghargai pandangan dan kontribusi dari orang lain.
- Hindari penggunaan proyek untuk tujuan yang melanggar hukum atau merugikan orang lain.
- Setiap penggunaan proyek yang tidak sesuai dengan kode etik atau ketentuan lisensi dapat mengakibatkan tindakan hukum atau pembatalan lisensi.
2. **Pembelian Proyek**:
- Proyek ini tersedia untuk dibeli dengan lisensi yang telah ditetapkan. Pastikan untuk membaca dan memahami syarat dan ketentuan lisensi sebelum melakukan pembelian.
- Setiap pembelian proyek akan memberikan akses ke sumber kode dan dokumen yang diperlukan sesuai dengan perjanjian lisensi yang disepakati.
3. **Instalasi dan Penggunaan**:
- Lakukan Extract File management-inventaris.zip ke directory anda.
- Buka proyek management-inventaris menggunakan code editor yang biasa anda gunakan.
- Lakukan beberapa configurasi seperti database, server host / port di  `src/main/resources/application.properties`
- Sekarang jika anda ingin melakukan running, anda bisa melakukan running dari code editor secara langsung maupun dari Command Prompt, berikut caranya: 
  - (Menggunakan code editor) Buka `src/main/java/com.apimanagemenet.management/ManagementAplication.java` jika sudah dibuka maka anda bisa mencoba melakukan running di situ
  - (Menggunakan CMD) Buka cmd, arahkan cmd ke directory tempat anda menyimpan project ini, jika sudah maka anda bisa mengetik ``mvn spring-boot:run`` untuk melakukan running, dan `mvn spring-boot --debug` untuk mealakukan running beserta melakukan debugging / mencari bug dalam skala besar maupun kecil
4. **Kebijakan Lisensi**:
- Penggunaan proyek ini tunduk pada ketentuan lisensi yang telah ditetapkan. Pastikan untuk mematuhi semua ketentuan lisensi yang berlaku.
- Setiap penggunaan atau distribusi proyek harus sesuai dengan jenis lisensi yang ditetapkan dan tidak melanggar hak cipta atau ketentuan lisensi yang berlaku.
- Diharapkan anda tidak menyebarkan licensi yang diberikan Admin / Author kepada pihak ke-3 dikarenakan bisa merguikan diri sendiri dan juga pemilik proyek tersebut.
5. **Dukungan dan Pembaruan**:
- Setiap pembelian proyek mungkin disertai dengan periode dukungan dan pembaruan tertentu. Pastikan untuk memahami periode dukungan yang disediakan dan bagaimana cara mendapatkan pembaruan perangkat lunak.
- Jika Anda membutuhkan dukungan tambahan atau pembaruan setelah periode dukungan berakhir, Anda dapat mempertimbangkan untuk memperpanjang periode dukungan atau menggunakan sumber daya alternatif yang tersedia.
6. **Tanggung Jawab Pengguna**:
- Pengguna bertanggung jawab sepenuhnya atas penggunaan proyek ini dan dampaknya.
- Pengguna harus melindungi informasi sensitif, mengamankan akses ke proyek, dan mengambil langkah-langkah pencegahan yang diperlukan untuk mencegah penyalahgunaan atau akses yang tidak sah.
7. **Kontak Dukungan**:
- Jika Anda memiliki pertanyaan, masalah, atau membutuhkan bantuan selama penggunaan proyek, jangan ragu untuk menghubungi tim dukungan proyek melalui kontak yang disediakan.

**Terima kasih atas minat dan dukungan Anda terhadap proyek ini!** Jika Anda memiliki pertanyaan tambahan atau memerlukan bantuan lebih lanjut, jangan ragu untuk menghubungi kami.

**Selamat Menggunakan Proyek Ini!**

# _**Demo Feature Maintenance**_
## **_1.) Order Product List_**
### 1. Feature Order yang akan masuk ke dalam Cart (Keranjang) User berdasarkan email user yang diambil relasinya dari Token.
### 2. Membuka cart dan menampilkan seluruh prodcut yang tadinya di order dan ditampilkan ke cart user berdasarkan email.
### 3. Checkout dan melakukan pembayaran ke pemilik product.
## **_2.) Message To Seller_**
### 1. Memilih kontak yang akan di message.
### 2. Pastikan user menginputkan message content nya, jika kosong atau spam spasi, maka akan muncul notifikasi dari API tidak di izinkan untuk mengirim pesan
### 3. Customer bisa mengirim Attachment kepada seller