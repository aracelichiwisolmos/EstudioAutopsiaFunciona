package hospital.controlador;

import hospital.clasificacion.model.clasificacionUtilidades;
import hospital.clasificacion.model.clasificacionWeka;
import hospital.clasificacion.model.modeloWeka;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oClasifica")
public class clasificacionControler implements Serializable {

    private modeloWeka oModelo;
    private clasificacionWeka oClacificacionWeka;
    private String texto;
    private String resultado;
    private final clasificacionUtilidades oClasificacionUtilidades;
    private configuracionAppControler oConf;

    public clasificacionControler() {
        texto = "";
        resultado = "";
        oClasificacionUtilidades = new clasificacionUtilidades();
        oConf = new configuracionAppControler();
    }

    public void generarModelo(String pahtCorpus, String pahtModel) throws Exception {
        try {
            oModelo = new modeloWeka(pahtCorpus);
            oModelo.generarModelo(pahtModel);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void clasificar(String texto, String model, String corpus) throws Exception {
        try {
            oClacificacionWeka = new clasificacionWeka(corpus, model);
            resultado = oClacificacionWeka.clasificar(texto, new configuracionAppControler().getPahtModel_com_op_sug());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void clasificar() throws Exception {
        try {
            resultado = oClasificacionUtilidades.clasifica(texto, "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=14;");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void generarModeloUltimaVersion(int preg) throws Exception {
        try {
            String options = null;
            String pathModel = null;
            String sQuery = null;

            if (preg == 6) {
                options = oConf.getOptions_mcc_aut();
                pathModel = oConf.getPahtModel_mcc_aut();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=14;";
            }
            if (preg == 7) {
                options = oConf.getOptions_mcc_no_aut();
                pathModel = oConf.getPahtModel_mcc_no_aut();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=16;";
            }
            if (preg == 13) {
                options = oConf.getOptions_com_op_sug();
                pathModel = oConf.getPahtModel_com_op_sug();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=22;";
            }
            oClasificacionUtilidades.generarModelo(sQuery, options, pathModel);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void clasificaUtimaVersion(int preg) throws Exception {
        try {
            String pathModel = null;
            String sQuery = null;

            if (preg == 6) {
                pathModel = oConf.getPahtModel_mcc_aut();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=14;";
            }
            if (preg == 7) {
                pathModel = oConf.getPahtModel_mcc_no_aut();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=16;";
            }
            if (preg == 13) {
                pathModel = oConf.getPahtModel_com_op_sug();
                sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=22;";
            }
            resultado = oClasificacionUtilidades.clasificarNuevaInstancia(texto, pathModel, sQuery);

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void grafico() throws Exception {
        oClasificacionUtilidades.obtenerGrafico();
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

}
