package hospital.controlador;

import hospital.modelo.usuario;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oLoginJb")
@SessionScoped
public class loginJB implements Serializable {

    private usuario usuario;
    private String usuarioActivo;
    private FacesContext contexto;

    @PostConstruct
    public void init() {
        setUsuario(new usuario());
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") == null) {
            usuarioActivo = "Anónimo";
        } else {
            usuarioActivo = ((usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getNombre();
        }
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public String iniciarSesion() {
        String redireccion = "";
        try {
            this.usuario = usuario.iniciarSesion(usuario);
            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Credenciales Incorrectas."));
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                usuarioActivo = ((usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getNombre();
                if (((usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getTipo().equals("A")) {
                    redireccion = "/Views/administrador/usuario?faces-redirect=true";
                } else if (((usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getTipo().equals("E")) {
                    redireccion = "/Views/especialista/encuesta?faces-redirect=true";
                }

            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
        return redireccion;
    }

    public String cerrarSesion() {
        String redirect = "";
        try {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            usuarioActivo = "Anónimo";
            redirect = "/Views/inicio?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso", "Error: " + e.getMessage()));
        }
        return redirect;
    }

    public String cancelar() throws IOException {

        String redirect = "/Views/inicio?faces-redirect=true";
        return redirect;
    }

    public String getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(String usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}
