/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.controlador;

import hospital.modelo.encuesta;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author dell
 */
@ManagedBean(name="oencuestaJb")
@SessionScoped
public class encuestaJb implements Serializable{
    
    List<encuesta> lEncuesta;
    
    
}
