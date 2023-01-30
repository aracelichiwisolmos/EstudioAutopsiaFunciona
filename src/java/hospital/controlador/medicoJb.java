package hospital.controlador;

import hospital.modelo.encuesta;
import hospital.modelo.medico;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oMedicoJb")
@ViewScoped
public class medicoJb implements Serializable {

    private medico oMedico;
    private List<medico> lmedicos;
    private int ncontrol;

    public medicoJb() {
        try {
            this.oMedico = new medico();
            this.lmedicos = oMedico.buscaTodos();
            this.ncontrol = 0;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void buscaTodosMedicos() {
        try {
            this.lmedicos = this.oMedico.buscaTodos();
            this.ncontrol = 0;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void buscaNoControl() {
        try {
            this.lmedicos = this.oMedico.buscaNoControl(this.getNcontrol());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void eliminarEncuesta(int idControl) throws Exception {
        try {
            System.out.println("id control: " + idControl);
            encuesta oencuesta = new encuesta();
            oencuesta.eliminarEncuesta(idControl);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
            buscaTodosMedicos();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public medico getMedico() {
        return oMedico;
    }

    public void setMedico(medico oMedico) {
        this.oMedico = oMedico;
    }

    public List<medico> getListaMedicos() {
        for (medico me : lmedicos) {
           // System.out.println("lista medico" + me.getControl() + me.getCh());//******
        }
        return lmedicos;
    }

    public void setListaMedicos(List<medico> lmedicos) {
        this.lmedicos = lmedicos;
    }

    public int getNcontrol() {
        return ncontrol;
    }

    public void setNcontrol(int ncontrol) {
        this.ncontrol = ncontrol;
    }

}
