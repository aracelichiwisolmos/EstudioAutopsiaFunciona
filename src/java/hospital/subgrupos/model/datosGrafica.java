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
public class datosGrafica {



    private int regla;
    private int valor;
    private String interpretacion;
    
     
       public datosGrafica() {
         
    }
       
    public int getRegla() {
        return regla;
    }

    public void setRegla(int regla) {
        this.regla = regla;
    }

  
    public int getValor() {
        return valor;
    }

  
    public void setValor(int valor) {
        this.valor = valor;
    }
    
    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = interpretacion;
    }

    
}
