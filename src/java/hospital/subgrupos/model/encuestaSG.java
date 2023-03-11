/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.subgrupos.model;

/**
 *
 * @author Araceli
 */

public class encuestaSG {

    /**
     * @return the centro
     */
    public String getCentro() {
        return centro;
    }

    /**
     * @param cetro the centro to set
     */
    public void setCentro(String centro) {
        this.centro = centro;
    }

    /**
     * @return the pregunta
     */
    public int getPregunta() {
        return pregunta;
    }

    /**
     * @param pregunta the pregunta to set
     */
    public void setPregunta(int pregunta) {
        this.pregunta = pregunta;
    }

    /**
     * @return the resp
     */
    public String getResp() {
        return resp;
    }

    /**
     * @param resp the resp to set
     */
    public void setResp(String resp) {
        this.resp = resp;
    }

    /**
     * @return the cant
     */
    public int getCant() {
        return cant;
    }

    /**
     * @param cant the cant to set
     */
    public void setCant(int cant) {
        this.cant = cant;
    }

    private String centro;
    private int pregunta;
    private String resp;
    private int cant;
    private String sresp;
    
    public encuestaSG(){}
    
    public encuestaSG(String centro, int pregunta,String resp, int cant){
        this.centro=centro;
        this.pregunta=pregunta;
        this.resp=resp;       
        this.cant=cant;
    }
    
    @Override
    public String toString() {
        return "| " + this.centro + " | " + this.pregunta + " | " + this.resp + " | " + this.cant + " | " + this.sresp + " |";
    }

    /**
     * @return the sresp
     */
    public String getSresp() {
        return sresp;
    }

    /**
     * @param sresp the sresp to set
     */
    public void setSresp(String sresp) {
        this.sresp = sresp;
    }
    
    
  

    
    
}
