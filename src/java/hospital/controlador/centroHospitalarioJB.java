package hospital.controlador;

import hospital.clasificacion.model.clasificacionUtilidades;
import hospital.modelo.IdentificacionUsuario;
import hospital.modelo.centroHospitalario;
import hospital.modelo.clasificadora;
import hospital.modelo.encuesta;
import hospital.modelo.respuesta;
import hospital.modelo.typeClasificado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oCentroHospitalario")
@ViewScoped
public class centroHospitalarioJB implements Serializable {

    //  private String clave;
    private String nombreCH;
    private centroHospitalario centroHos;

    public centroHospitalarioJB() throws Exception {
        //idUsuario = new IdentificacionUsuario();
        centroHos = new centroHospitalario();
        this.nombreCH = null;

    }

    public centroHospitalario getCH() {
        return centroHos;
    }

    public centroHospitalario setCH() {
        return centroHos;
    }

    /*public List<centro_hospitalario> getListaCH() {
        return lch;
    }

    public void setCentroHospitalario(String centro_hospitalario) {
        this.centro_hospitalario = centro_hospitalario;
    }
     */
}
