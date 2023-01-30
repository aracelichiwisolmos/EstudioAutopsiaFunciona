package hospital.controlador;

import hospital.clasificacion.model.clasificacionUtilidades;
import hospital.modelo.IdentificacionUsuario;
import hospital.modelo.centroHospitalario;
import hospital.modelo.clasificadora;
import hospital.modelo.encuesta;
import hospital.modelo.institucion;
import hospital.modelo.localidad;
import hospital.modelo.municipio;
import hospital.modelo.respuesta;
import hospital.modelo.typeClasificado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oInstitucionJB")
@ViewScoped
public class institucionJB implements Serializable {

    //  private String clave;
    private int clave_ins;
    private String snombre_ins;
    //
    // private boolean visible1 = true;
    //private IdentificacionUsuario idUsuario;
//_____________________________________________

//________________________________________________
    private institucion oIns;
    private List<institucion> lIns;
    private int idInsSeleccionado;

    public institucionJB() throws Exception {

        oIns = new institucion();
        //  oCeHo.setNombreCH("");
        this.lIns = new ArrayList<>();
        //this.lCeHo.add(oCeHo);
        this.lIns = oIns.buscaTodos();

        this.clave_ins = 0;
        this.snombre_ins = null;

    }

    public institucion getIns() {
        return oIns;
    }

    public institucion setIns() {
        return oIns;
    }

    public void setNombreIns(String snombre_ins) {
        this.snombre_ins = snombre_ins;
    }

    public String getNombreIns() {
        return snombre_ins;
    }

    public int getClaveIns() {
        return clave_ins;
    }

    public void setClaveIns(int clave_ins) {
        this.clave_ins = clave_ins;
    }

    //-----------------CEntro hospitalario_---
    //_________________institucion_____________
    public List<institucion> getListaIns() {
        for (institucion ins : lIns) {
            System.out.println(ins.getNombreIns());
        }
        return lIns;
    }

    public void setListaIns(List<institucion> lIns) {
        this.lIns = lIns;
    }

    public int getIdInsSeleccionado() {
        return idInsSeleccionado;
    }

    public void setIdInsSeleccionado(int idInsSeleccionado) {
        this.idInsSeleccionado = idInsSeleccionado;
    }

    //-------------------------------
    //-------------------------------
    //_________________localidad_____________
    public String almacena() {

        int res = 0;
        try {
            res = oIns.insertar();
            //return visible1=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (res != 0) {
            //setVisible1(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "COMPLETADO", "Se insertó correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "al insertar"));
        }
        //  cargalista();
        return "nueva_encuesta.xhtml";
    }

}
