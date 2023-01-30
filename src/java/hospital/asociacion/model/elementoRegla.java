package hospital.asociacion.model;

public class elementoRegla {

    private String nombreColumna;
    private String valor;

    public elementoRegla() {
        this.nombreColumna = null;
        this.valor = null;
    }

    public elementoRegla(String nombreColumna, String valor) {
        this.nombreColumna = nombreColumna;
        this.valor = valor;
    }

    public String getNombreColumna() {
        return nombreColumna;
    }

    public void setNombreColumna(String nombreColumna) {
        this.nombreColumna = nombreColumna;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
