/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.controlador;

import hospital.graficas.grafica;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author dell
 */
@ManagedBean(name="oGraficaJB")
@ViewScoped
public class graficaControler implements Serializable{
    String items;
    String grupos;
    grafica oGrafica;
    

    public graficaControler() throws Exception {
        oGrafica=new grafica();
        this.items = oGrafica.itemsBarras();
        this.grupos = oGrafica.groupsBarras();
    }
    

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getGrupos() {
        return grupos;
    }

    public void setGrupos(String grupos) {
        this.grupos = grupos;
    }
    
}
