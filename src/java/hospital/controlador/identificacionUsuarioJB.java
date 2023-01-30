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

@ManagedBean(name = "oIdUsuarioJB")
@ViewScoped
public class identificacionUsuarioJB implements Serializable {

    //  private String clave;
    private String centro_hospitalario;
    private String institucion;
    private String localidad;
    private String municipio;
    private String conteo;
    private boolean visible1 = true;
    private IdentificacionUsuario idUsuario;
//_____________________________________________
    private centroHospitalario oCeHo;
    private List<centroHospitalario> lCeHo;
    private int idCHSeleccionado;
//________________________________________________

    private institucion oIns;
    private List<institucion> lIns;
    private int idInsSeleccionado;

    //________________________________
    private municipio oMun;
    private List<municipio> lMun;
    private int idMunSeleccionado;
    //_______________
    private localidad oLo;
    private List<localidad> lLo;
    private int idLoSeleccionado;

    public identificacionUsuarioJB() throws Exception {
        idUsuario = new IdentificacionUsuario();
        this.idUsuario.getResultado();
        // this.idUsuario.toString();
        oCeHo = new centroHospitalario();
        //  oCeHo.setNombreCH("Hugo");
        this.lCeHo = new ArrayList<>();
        //this.lCeHo.add(oCeHo);
        this.lCeHo = oCeHo.buscaTodos();

        oIns = new institucion();
        //  oCeHo.setNombreCH("Hugo");
        this.lIns = new ArrayList<>();
        //this.lCeHo.add(oCeHo);
        this.lIns = oIns.buscaTodos();

        oMun = new municipio();
        //  oCeHo.setNombreCH("Hugo");
        this.lMun = new ArrayList<>();
        //this.lCeHo.add(oCeHo);
        this.lMun = oMun.buscaTodos();

        oLo = new localidad();
        //  oCeHo.setNombreCH("Hugo");
        this.lLo = new ArrayList<>();
        //this.lCeHo.add(oCeHo);
        this.lLo = oLo.buscaTodos();

        this.centro_hospitalario = null;
        this.institucion = null;
        this.localidad = null;
        this.municipio = null;

    }

    public IdentificacionUsuario getIU() {
        return idUsuario;
    }

    public IdentificacionUsuario setIU() {
        return idUsuario;
    }

//    public String getClave() {
//        return clave;
//    }
//
//    public void setClave(String clave) {
//        this.clave = clave;
//    }
    public void setCentroHospitalario(String centro_hospitalario) {
        this.centro_hospitalario = centro_hospitalario;
    }

    public String getCentroHospitalario() {
        return centro_hospitalario;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public boolean getVisible1() {
        return visible1;
    }

    public void setVisible1(boolean v) {
        visible1 = v;
    }

    //-----------------Resultado_---
    public String getResultdo() {
        return conteo;
    }

    public void setResultdo(String conteo) {
        this.conteo = conteo;
    }

    public List<centroHospitalario> getListaCH() {
//        for (centroHospitalario ch : lCeHo) {
//            System.out.println(ch.getNombreCH());
//        }
        return lCeHo;
    }

    public void setListaCH(List<centroHospitalario> lCeHo) {
        this.lCeHo = lCeHo;
    }

    public int getIdCHSeleccionado() {
        return idCHSeleccionado;
    }

    public void setIdCHSeleccionado(int idCHSeleccionado) {
        this.idCHSeleccionado = idCHSeleccionado;
    }

    //_________________institucion_____________
    public List<institucion> getListaIns() {
//        for (institucion ins : lIns) {
//            System.out.println(ins.getNombreIns());
//        }
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
    //_________________municipio_____________
    public List<municipio> getListaMun() {
//        for (municipio mun : lMun) {
//            System.out.println(mun.getNombreMun());
//        }
        return lMun;
    }

    public void setListaMun(List<municipio> lMun) {
        this.lMun = lMun;
    }

    public int getIdMunSeleccionado() {
        return idMunSeleccionado;
    }

    public void setIdMunSeleccionado(int idMunSeleccionado) {
        this.idMunSeleccionado = idMunSeleccionado;
    }

    //-------------------------------
    //_________________localidad_____________
    public List<localidad> getListaLo() {
//        for (localidad lo : lLo) {
//            System.out.println(lo.getNombreLo());
//        }
        return lLo;
    }

    public void setListaLo(List<localidad> lLo) {
        this.lLo = lLo;
    }

    public int getIdLoSeleccionado() {
        return idLoSeleccionado;
    }

    public void setIdLoSeleccionado(int idLoSeleccionado) {
        this.idLoSeleccionado = idLoSeleccionado;
    }

    public String almacena() {
        // setVisible1(true);
        int res = 0;
        try {
            idUsuario.insertar();
            setVisible1(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "COMPLETADO", "Se insertó correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "al insertar"));
            e.printStackTrace();
        }
        //  cargalista();
        return "nueva_encuesta.xhtml";
    }

}
