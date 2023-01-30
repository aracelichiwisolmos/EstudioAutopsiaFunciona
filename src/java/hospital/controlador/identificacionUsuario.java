package hospital.controlador;

import hospital.modelo.area;
import hospital.modelo.centroEstudio;
import hospital.modelo.grado;
import hospital.modelo.pregunta;
import hospital.modelo.respuesta;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

//@ManagedBean(name = "oPreguntaJB")
@ViewScoped
public class identificacionUsuario implements Serializable {

    private String preg1;
    private String preg2;
    private String preg3;
    private String preg4;
    private String preg5;
    private String preg6;
    private String preg7;
    private String preg8;
    private String preg9;
    private String preg10;
    private String preg11;
    private String preg12;
    private String preg13;
    private String preg14;
    private String preg15;
    private String preg16;
    private String preg17;
    private pregunta oPregunta;
    private List<pregunta> lPreguntas;
    private area oArea;
    private List<area> lAreas;
    private int idAreaSeleccionado;
    private grado oGrado;
    private List<grado> lGrado;
    private int idGradoSeleccionado;
    private respuesta oRespuesta;
    private centroEstudio oCentro;
    private List<pregunta> lInfoPregunta;

    public List<area> getListaAreas() {
        return lAreas;
    }

    public void setListaAreas(List<area> lAreas) {
        this.lAreas = lAreas;
    }

    public List<pregunta> getListaPreguntas() {
        return lPreguntas;
    }

    public void setListaPreguntas(List<pregunta> lPreguntas) {
        this.lPreguntas = lPreguntas;
    }

    public area getArea() {
        return oArea;
    }

    public void setArea(area oArea) {
        this.oArea = oArea;
    }

    public String getPreg1() {
        return preg1;
    }

    public void setPreg1(String preg1) {
        this.preg1 = preg1;
    }

    public String getPreg2() {
        return preg2;
    }

    public void setPreg2(String preg2) {
        this.preg2 = preg2;
    }

    public String getPreg3() {
        return preg3;
    }

    public void setPreg3(String preg3) {
        this.preg3 = preg3;
    }

    public String getPreg4() {
        return preg4;
    }

    public void setPreg4(String preg4) {
        this.preg4 = preg4;
    }

    public String getPreg5() {
        return preg5;
    }

    public void setPreg5(String preg5) {
        this.preg5 = preg5;
    }

    public String getPreg6() {
        return preg6;
    }

    public void setPreg6(String preg6) {
        this.preg6 = preg6;
    }

    public String getPreg7() {
        return preg7;
    }

    public void setPreg7(String preg7) {
        this.preg7 = preg7;
    }

    public String getPreg8() {
        return preg8;
    }

    public void setPreg8(String preg8) {
        this.preg8 = preg8;
    }

    public String getPreg9() {
        return preg9;
    }

    public void setPreg9(String preg9) {
        this.preg9 = preg9;
    }

    public String getPreg10() {
        return preg10;
    }

    public void setPreg10(String preg10) {
        this.preg10 = preg10;
    }

    public String getPreg11() {
        return preg11;
    }

    public void setPreg11(String preg11) {
        this.preg11 = preg11;
    }

    public String getPreg12() {
        return preg12;
    }

    public void setPreg12(String preg12) {
        this.preg12 = preg12;
    }

    public String getPreg13() {
        return preg13;
    }

    public void setPreg13(String preg13) {
        this.preg13 = preg13;
    }

    public String getPreg14() {
        return preg14;
    }

    public void setPreg14(String preg14) {
        this.preg14 = preg14;
    }

    public String getPreg15() {
        return preg15;
    }

    public void setPreg15(String preg15) {
        this.preg15 = preg15;
    }

    public String getPreg16() {
        return preg16;
    }

    public void setPreg16(String preg16) {
        this.preg16 = preg16;
    }

    public String getPreg17() {
        return preg17;
    }

    public void setPreg17(String preg17) {
        this.preg17 = preg17;
    }

    public pregunta getPregunta() {
        return oPregunta;
    }

    public void setPregunta(pregunta oPregunta) {
        this.oPregunta = oPregunta;
    }

    public grado getGrado() {
        return oGrado;
    }

    public void setGrado(grado oGrado) {
        this.oGrado = oGrado;
    }

    public List<grado> getListaGrado() {
        return lGrado;
    }

    public void setListaGrado(List<grado> lGrado) {
        this.lGrado = lGrado;
    }

    public int getIdGradoSeleccionado() {
        return idGradoSeleccionado;
    }

    public void setIdGradoSeleccionado(int idGradoSeleccionado) {
        this.idGradoSeleccionado = idGradoSeleccionado;
    }

    public respuesta getRespuesta() {
        return oRespuesta;
    }

    public void setRespuesta(respuesta oRespuesta) {
        this.oRespuesta = oRespuesta;
    }

    public centroEstudio getCentro() {
        return oCentro;
    }

    public void setCentro(centroEstudio oCentro) {
        this.oCentro = oCentro;
    }

    public identificacionUsuario() throws Exception {
        this.oPregunta = new pregunta();
        this.oArea = new area();
        this.oGrado = new grado();
        this.oRespuesta = new respuesta();
        this.oCentro = new centroEstudio();
        this.lPreguntas = oPregunta.buscaTodos();
        this.lAreas = oArea.buscaTodos();
        this.lGrado = oGrado.buscaTodos();
        this.preg1 = lPreguntas.get(0).getPregunta();
        this.preg2 = lPreguntas.get(1).getPregunta();
        this.preg3 = lPreguntas.get(2).getPregunta();
        this.preg4 = lPreguntas.get(3).getPregunta();
        this.preg5 = lPreguntas.get(4).getPregunta();
        this.preg6 = lPreguntas.get(5).getPregunta();
        this.preg7 = lPreguntas.get(6).getPregunta();
        this.preg8 = lPreguntas.get(7).getPregunta();
        this.preg9 = lPreguntas.get(8).getPregunta();
        this.preg10 = lPreguntas.get(9).getPregunta();
        this.preg11 = lPreguntas.get(10).getPregunta();
        this.preg12 = lPreguntas.get(11).getPregunta();
        this.preg13 = lPreguntas.get(12).getPregunta();
        this.preg14 = "Área a la que perteneces: ";//lpreguntas.get(13).getPregunta();
        this.preg15 = "Último grado de estudios: "; //lpreguntas.get(14).getPregunta();
        this.preg16 = "¿Dónde efectuaste tus estudios de medicina general?"; //lpreguntas.get(15).getPregunta();
        this.preg17 = "Escuela/Hospital donde estudiaste la especialidad que ejerces: "; //lpreguntas.get(16).getPregunta();
        this.lInfoPregunta = oPregunta.infoPregunta();
    }

    public int getIdAreaSeleccionado() {
        return idAreaSeleccionado;
    }

    public void setIdAreaSeleccionado(int idAreaSeleccionado) {
        this.idAreaSeleccionado = idAreaSeleccionado;
    }

    public List<pregunta> getLinfoPregunta() {
        return lInfoPregunta;
    }

    public void setLinfoPregunta(List<pregunta> lInfoPregunta) {
        this.lInfoPregunta = lInfoPregunta;
    }

}
