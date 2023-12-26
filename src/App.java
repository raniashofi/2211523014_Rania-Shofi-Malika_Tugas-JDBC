import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class App {
    // Mengkoneksikan Database
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    //Membuat ArrayList
    private static List<Barang> barangList = new ArrayList<>();

    private static Connection connection;
    public static void main(String[] args) {
        try {

            // inisialisasi database
            initDatabase();

            // Pilihan untuk CRUD yang bisa dilakukan oleh admin
            System.out.println("======================================");
            System.out.println("PILIH PENGINPUTAN");
            System.out.println("1. CREATE");
            System.out.println("2. UPDATE");
            System.out.println("3. DELETE");
            System.out.println("4. READ");
            System.out.println("5. SELESAI");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Pilihan: ");
                int adminChoice = scanner.nextInt();

                switch (adminChoice) {
                    case 1:
                        createBarang(scanner);
                        break;
                    case 2:
                        updateBarang(scanner);
                        break;
                    case 3:
                        deleteBarang(scanner);
                        break;
                    case 4:
                        readBarang(scanner);
                        break;
                    case 5:
                        System.out.println("Penginputan selesai.");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid");
                }

                if (adminChoice == 5) {
                    break; // loop akan keluar jika admin memilih 5 (selesai)
                }
            }
            System.out.println("======================================");
            System.out.println("     kode, nama, dan harga barang");
            System.out.println("--------------------------------------");
            
            //untuk memanggil barang yang sudah diinputkan dan disimpan di ArrayList tadi
            for (Barang barang : barangList) {
                System.out.println(barang.getKode() + " " + barang.getNama() + " = " + barang.getHarga());
            }

            System.out.println("======================================");
            System.out.println(" ");
            System.out.println("SILAHKAN MASUKKAN DETAIL BELANJA");
            System.out.println(" ");

            scanner.nextLine();

            // Input data pelanggan
            System.out.print("Nama Pelanggan : ");
            String namaPelanggan = scanner.nextLine();
            System.out.print("No. HP         : ");
            String noHP = scanner.nextLine();
            System.out.print("Alamat         : ");
            String alamat = scanner.nextLine();

            Pelanggan pelanggan = new Pelanggan(namaPelanggan, noHP, alamat);

            // Input data pembelian barang
            System.out.print("Kode Barang    : ");
            String kodeBarang = scanner.nextLine();
            System.out.print("Jumlah Beli    : ");
            int jumlahBeli = scanner.nextInt();

            // Perform database operations
            Barang barang = getBarangByKode(kodeBarang);

            // Tampilkan hasil
            tampilkanStruk(pelanggan, barang, jumlahBeli);

            // Hitung total bayar
            double totalBayar = barang.getHarga() * jumlahBeli;

            // Tampilkan hasil
            tampilkanStruk(pelanggan, barang, jumlahBeli, totalBayar);

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi databse
            closeDatabase();
        }
    }

    //method membuat barang
    private static void createBarang(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("======================================");
            System.out.println("CREATE BARANG");
            System.out.print("Kode Barang: ");
            String kode = scanner.next();
            System.out.print("Nama Barang: ");
            String nama = scanner.next();
            System.out.print("Harga Barang: ");
            double harga = scanner.nextDouble();
    
            // Menambah data ke database
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?)")) {
                statement.setString(1, kode);
                statement.setString(2, nama);
                statement.setDouble(3, harga);
                statement.executeUpdate();
            }
    
            // menambah barang baru ke List
            Barang newBarang = new Produk(kode, nama, harga);
            barangList.add(newBarang);
    
            System.out.println("Barang berhasil ditambahkan.");
    
            System.out.print("Apakah barang sudah selesai diinput? (Y/N): ");
            String selesaiInput = scanner.next().toUpperCase();
            if ("Y".equals(selesaiInput)) {
                break; // exit dari loop saat diinput "Y"
            }
        }
    }

    //method memperbarui barang
    private static void updateBarang(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("======================================");
            System.out.println("UPDATE HARGA BARANG");
            System.out.print("Kode Barang: ");
            String kode = scanner.next();
            System.out.print("Harga Baru: ");
            double newHarga = scanner.nextDouble();
    
            // Update data di database
            try (PreparedStatement statement = connection.prepareStatement("UPDATE products SET harga = ? WHERE kode = ?")) {
                statement.setDouble(1, newHarga);
                statement.setString(2, kode);
                int rowsAffected = statement.executeUpdate();
    
                if (rowsAffected > 0) {
                    System.out.println("Harga barang berhasil diupdate.");
    
                    // Update data yg sesuai ke dalam List
                    for (Barang barang : barangList) {
                        if (barang.getKode().equals(kode)) {
                            barang.setHarga(newHarga);
                            break;
                        }
                    }
                } else {
                    System.out.println("Kode barang tidak ditemukan.");
                }
            }
    
            System.out.print("Apakah barang sudah selesai diupdate? (Y/N): ");
            String selesaiUpdate = scanner.next().toUpperCase();
            if ("Y".equals(selesaiUpdate)) {
                break; // exit dari loop saat diinput "Y"
            }
        }
    }

    //method menghapus barang
    private static void deleteBarang(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("======================================");
            System.out.println("DELETE BARANG");
            System.out.print("Kode Barang: ");
            String kode = scanner.next();
    
            // menghapus data dari database
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE kode = ?")) {
                statement.setString(1, kode);
                int rowsAffected = statement.executeUpdate();
    
                if (rowsAffected > 0) {
                    System.out.println("Barang berhasil dihapus.");
    
                    // menghapus data yg sesuai ke dalam List
                    barangList.removeIf(barang -> barang.getKode().equals(kode));
                } else {
                    System.out.println("Kode barang tidak ditemukan.");
                }
    
            }
    
            System.out.print("Apakah barang sudah selesai dihapus? (Y/N): ");
            String selesaiHapus = scanner.next().toUpperCase();
            if ("Y".equals(selesaiHapus)) {
                break; // exit dari loop saat diinput "Y"
            }
        }
    }

    //method menampilkan barang
    private static void readBarang(Scanner scanner) throws SQLException {
        System.out.println("======================================");
        System.out.println("MENAMPILKAN NAMA DAN HARGA BARANG");
    
        while (true) {
            System.out.print("Masukkan Kode Barang (ketik 'Y' jika sudah selesai): ");
            String kode = scanner.next();
    
            if ("Y".equalsIgnoreCase(kode)) {
                break; // exit dari loop saat diinput "Y"
            }
    
            // Menampilkan data dari database
            try (PreparedStatement statement = connection.prepareStatement("SELECT nama, harga FROM products WHERE kode = ?")) {
                statement.setString(1, kode);
                ResultSet resultSet = statement.executeQuery();
    
                if (resultSet.next()) {
                    String nama = resultSet.getString("nama");
                    double harga = resultSet.getDouble("harga");
    
                    System.out.println("Nama Barang: " + nama);
                    System.out.println("Harga Barang: " + harga);
                } else {
                    System.out.println("Kode barang tidak ditemukan.");
                }
            }
        }
    }

    //method untuk menampilkan struk
    private static void tampilkanStruk(Pelanggan pelanggan, Barang barang, int jumlahBeli, double totalBayar) {
        Date date = new Date();
        Date time = new Date();
        SimpleDateFormat fd = new SimpleDateFormat("hh:mm::ss");
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(" ");
        System.out.println("=========== RANIA CLOTHING ===========");
        System.out.println("Tanggal             : " + fd.format(date));
        System.out.println("Waktu               : " + ft.format(time));
        System.out.println("======================================");
        System.out.println("            DATA PELANGGAN");
        System.out.println("--------------------------------------");
        System.out.println("Nama Pelanggan      : " + pelanggan.getNama());
        System.out.println("No. HP              : " + pelanggan.getNoHP());
        System.out.println("Alamat              : " + pelanggan.getAlamat());
        System.out.println("--------------------------------------");
        System.out.println("        DATA PEMBELIAN BARANG");
        System.out.println("--------------------------------------");
        System.out.println("Kode Barang         : " + barang.getKode());
        System.out.println("Nama Barang         : " + barang.getNama());
        System.out.println("Harga Barang        : " + barang.getHarga());
        System.out.println("Jumlah Beli         : " + jumlahBeli);
        System.out.println("--------------------------------------");
        System.out.println("TOTAL PEMBAYARAN    : " + totalBayar);
        System.out.println("--------------------------------------");
        System.out.println("Kasir             : Rania Shofi Malika");
        System.out.println("-------------TERIMA KASIH-------------");
    }

    private static void initDatabase() throws SQLException {

        try {
            // Register the H2 JDBC driver
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

        // Create table if not exists
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS products (" +
                    "kode VARCHAR(3) PRIMARY KEY," +
                    "nama VARCHAR(255)," +
                    "harga DOUBLE)";
            statement.execute(createTableQuery);
        }
    }

    private static void closeDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Barang getBarangByKode(String kodeBarang) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE kode = ?")) {
            statement.setString(1, kodeBarang);
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String kode = resultSet.getString("kode");
                String nama = resultSet.getString("nama");
                double harga = resultSet.getDouble("harga");
                return new Produk(kode, nama, harga);
            } else {
                throw new IllegalArgumentException("Kode barang tidak valid");
            }
        }
    }

    
    // Tampilkan hasil
    private static void tampilkanStruk(Pelanggan pelanggan, Barang barang, int jumlahBeli) {
    }

}