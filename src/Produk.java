class Produk implements Barang {
    private String kode;
    private String nama;
    private double harga;

    public Produk(String kode, String nama, double harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getNama() {
        return nama;
    }

    @Override
    public double getHarga() {
        return harga;
    }

    @Override
    public void setHarga(double harga) {
        this.harga = harga;
    }

}