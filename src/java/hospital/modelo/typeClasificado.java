package hospital.modelo;

public class typeClasificado {

    private int id_medico;
    private int id_preg;
    private String texto;
    private String clasificado;
    private int idclasificado;

    public typeClasificado() {
        this.id_medico = 0;
        this.id_preg = 0;
        this.texto = "";
        this.clasificado = "";
        this.idclasificado = 0;
    }

    public typeClasificado(String texto, String clasificado) {
        this.id_medico = 0;
        this.id_preg = 0;
        this.texto = texto;
        this.clasificado = clasificado;
        this.idclasificado = 0;
    }

    public int getId_medico() {
        return id_medico;
    }

    public void setId_medico(int id_medico) {
        this.id_medico = id_medico;
    }

    public int getId_preg() {
        return id_preg;
    }

    public void setId_preg(int id_preg) {
        this.id_preg = id_preg;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getClasificado() {
        return clasificado;
    }

    public void setClasificado(String clasificado) {
        this.clasificado = clasificado;
    }

    public int getIdclasificado() {
        return idclasificado;
    }

    public void setIdclasificado(int idclasificado) {
        this.idclasificado = idclasificado;
    }

}
