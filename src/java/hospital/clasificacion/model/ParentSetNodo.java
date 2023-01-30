
package hospital.clasificacion.model;

import java.util.List;


public class ParentSetNodo {

    private List<ElementoAtributoGrafo> padres;
    private ElementoAtributoGrafo elementoNodo;
    private double probabilidad;
    private int cardinalidad_elementoNodo;
    private String interpretacion;
    private final clasificacionUtilidades oUtilidades;
    private String regla;

    public ParentSetNodo() {
        this.padres = null;
        this.elementoNodo = null;
        this.probabilidad = -1;
        this.cardinalidad_elementoNodo = -1;
        this.interpretacion = "";
        oUtilidades = new clasificacionUtilidades();
        this.regla = null;
    }

    public String Interpretacion() throws Exception {
        String resultado = "";
        try {
            if (padres != null && elementoNodo != null && padres.size() > 0 && probabilidad > -1) {
                resultado = "La probabilidad de los médicos que " + oUtilidades.interpretaAtributo(elementoNodo.getNombreNodo(), elementoNodo.getValorElemento()) + " es de un " + Math.round(probabilidad * 100) + "%" + ", dado que ";
                for (int i = 0; i < padres.size(); i++) {
                    ElementoAtributoGrafo oAtributo = padres.get(i);
                    if (i < padres.size() - 1) {
                        if (i == padres.size() - 2) {
                            resultado += oUtilidades.interpretaAtributo(oAtributo.getNombreNodo(), oAtributo.getValorElemento()) + " y ";
                        } else {
                            resultado += oUtilidades.interpretaAtributo(oAtributo.getNombreNodo(), oAtributo.getValorElemento()) + ", ";
                        }
                    } else {
                        resultado += oUtilidades.interpretaAtributo(oAtributo.getNombreNodo(), oAtributo.getValorElemento());
                    }
                }
            } else if (padres != null && elementoNodo != null && padres.isEmpty() && probabilidad > -1) {
                resultado = "La probabilidad de que los médicos " + oUtilidades.interpretaAtributo(elementoNodo.getNombreNodo(), elementoNodo.getValorElemento()) + " es de un " + Math.round(probabilidad * 100) + "%";

            }

        } catch (Exception e) {
            throw e;
        }
        return resultado + ".";
    }

    public String regla() throws Exception {
        String resultado = "";
        try {
            if (padres != null && padres.size() > 0 && elementoNodo != null && probabilidad > -1) {
                for (int i = 0; i < padres.size(); i++) {
                    ElementoAtributoGrafo oAtributo = padres.get(i);
                    if (i < padres.size() - 1) {
                        resultado += oAtributo.getNombreNodo() + ": " + oAtributo.getValorElemento() + ", ";

                    } else {
                        resultado += oAtributo.getNombreNodo() + ": " + oAtributo.getValorElemento();
                    }
                }
                resultado += " ==> " + elementoNodo.getNombreNodo() + ": " + elementoNodo.getValorElemento();
            } else if (padres != null && elementoNodo != null && padres.isEmpty() && probabilidad > -1) {
                resultado += elementoNodo.getNombreNodo() + ": " + elementoNodo.getValorElemento();
            }
            return resultado;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<ElementoAtributoGrafo> getPadres() {
        return padres;
    }

    public void setPadres(List<ElementoAtributoGrafo> padres) {
        this.padres = padres;
    }

    public ElementoAtributoGrafo getElementoNodo() {
        return elementoNodo;
    }

    public void setElementoNodo(ElementoAtributoGrafo elementoNodo) {
        this.elementoNodo = elementoNodo;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }

    public int getCardinalidad_elementoNodo() {
        return cardinalidad_elementoNodo;
    }

    public void setCardinalidad_elementoNodo(int cardinalidad_elementoNodo) {
        this.cardinalidad_elementoNodo = cardinalidad_elementoNodo;
    }

    public String getInterpretacion() throws Exception {
        this.interpretacion = Interpretacion();
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = interpretacion;
    }

    public String getRegla() {
        return regla;
    }

    public void setRegla(String regla) {
        this.regla = regla;
    }

}
