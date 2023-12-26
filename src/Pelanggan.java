public class Pelanggan {
    private String nama;
    private String noHP;
    private String alamat;

    public Pelanggan(String nama, String noHP, String alamat) {
        this.nama = nama;
        this.noHP = noHP;
        this.alamat = alamat;
    }

    public String getNama() {
        return nama;
    }

    public String getNoHP() {
        return noHP;
    }

    public String getAlamat() {
        return alamat;
    }
}
