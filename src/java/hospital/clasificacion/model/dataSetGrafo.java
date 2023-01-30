package hospital.clasificacion.model;

public class dataSetGrafo {
    String nodos;
    String arcos;

    public dataSetGrafo(String nodos, String arcos) {
        this.nodos = nodos;
        this.arcos = arcos;
    }

    public String getNodos() {
        return nodos;
    }

    public void setNodos(String nodos) {
        this.nodos = nodos;
    }

    public String getArcos() {
        return arcos;
    }

    public void setArcos(String arcos) {
        this.arcos = arcos;
    }
    
    
}
