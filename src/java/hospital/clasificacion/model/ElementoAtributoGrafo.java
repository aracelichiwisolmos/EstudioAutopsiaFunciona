package hospital.clasificacion.model;

public class ElementoAtributoGrafo {
    
    private String nombreNodo;
    private int numNodo;
    private String valorElemento;

    public ElementoAtributoGrafo() {
        this.nombreNodo = "";
        this.numNodo = -1;
        this.valorElemento = "";
    }

    public String getNombreNodo() {
        return nombreNodo;
    }

    public void setNombreNodo(String nombreNodo) {
        this.nombreNodo = nombreNodo;
    }

    public int getNumNodo() {
        return numNodo;
    }

    public void setNumNodo(int numNodo) {
        this.numNodo = numNodo;
    }

    public String getValorElemento() {
        return valorElemento;
    }

    public void setValorElemento(String valorElemento) {
        this.valorElemento = valorElemento;
    }
    
    
       
}
