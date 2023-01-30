/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.subgrupos.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Araceli
 */
public class ReglasSG {

    private String regla;
    private String Descripcion;
    private double Tamaño;
    private double ValFuncion;
    private int idRegla;
    
       List<ReglasSG> reglas = new ArrayList<>();
       
       public ReglasSG() {
           
        this.regla = "";//asegurar que la propiedad "regla" no sea nula antes de intentar utilizarla.
        
    }
 
       
       
    public String getRegla() {
        return regla;
    }

    public void setRegla(String regla) {
        this.regla = regla;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public double getTamaño() {
        return Tamaño;
    }

    public void setTamaño(double Tamaño) {
        this.Tamaño = Tamaño;
    }

    public double getValFuncion() {
        return ValFuncion;
    }

    public void setValFuncion(double ValFuncion) {
        this.ValFuncion = Math.round(ValFuncion * 1000.0) / 1000.0;
    }

    public List<ReglasSG> getReglas() {
        return reglas;
    }

    public void setReglas(ArrayList<ReglasSG> reglas) {
        this.reglas = reglas;
    }
      public int getIdRegla() {
        return idRegla;
    }

    public void setIdRegla(int idRegla) {
        this.idRegla = idRegla;
    }

    
}
