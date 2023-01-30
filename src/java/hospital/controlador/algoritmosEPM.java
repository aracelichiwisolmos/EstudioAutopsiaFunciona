/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.controlador;

import java.util.List;

/**
 *
 * @author mscay
 */
public class algoritmosEPM {

    private List<parametros> parametros;
    private String name;
    private String classepm;

    public algoritmosEPM() {

    }

    public List<parametros> getParametros() {
        return parametros;
    }

    public void setParametros(List<parametros> parametros) {
        this.parametros = parametros;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassepm() {
        return classepm;
    }

    public void setClassepm(String classepm) {
        this.classepm = classepm;
    }

}
