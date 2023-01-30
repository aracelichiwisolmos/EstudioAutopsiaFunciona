
package hospital.controlador;

import hospital.modelo.usuario;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

@ManagedBean(name = "oeditarUsuarioJb")
@ViewScoped
public class editarUsuarioJb implements Serializable{

    private usuario usuario;
    private boolean colapse;
    private usuarioJb oUsuarioJB;

    @PostConstruct
    public void init() {
        setUsuario(new usuario());
        this.colapse=true;
        oUsuarioJB=new usuarioJb();
    }
    
      public void reset() {
        this.usuario.setNombre("");
        this.usuario.setUsuario("");
        this.usuario.setClave ("");
        this.usuario.setPaterno("");
        this.usuario.setMaterno("");
        this.usuario.setTipo("");
        this.usuario.setIdUsuario(0);
    }
    
     public void cancelarNuevoUsuario() {
        reset();
        this.colapse=true;
    }
     
      public String cancelarModificarUsuario() {
        reset();
        return "usuario";
    }

    public void actualizar(usuario ousuario) {
        this.setUsuario(ousuario);
    }

    public void eliminarConfirmado(usuario ousuario) {
        this.setUsuario(ousuario);
        this.usuario.eliminar(usuario.getIdUsuario());
        usuario usuarioVacio=new usuario();
        oUsuarioJB.setUsuarios(usuarioVacio.buscaTodos());
        reset();
        this.colapse=true;
    }

    public String cambioConfirmado() {
        String redirect="";
        try {
            getUsuario().actualizar(getUsuario());
            reset();
            redirect="usuario";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        reset();
        }
        return redirect;
    }
    
     public String cambioContraseña() {
        String redirect="";
        try {
            getUsuario().cambiarPassword(getUsuario());
            reset();
            redirect="usuario";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        reset();
        }
        return redirect;
    }

    public void guardarConfirmado() {
        try {
            getUsuario().insertar(getUsuario());
            reset();
            this.colapse=true;            
            } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:"+ e.getMessage()));
        }
    }

    
    public usuario getUsuario() {
        return usuario;
    }

    
    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isColapse() {
        return colapse;
    }

    public void setColapse(boolean colapse) {
        this.colapse = colapse;
    }
    
}
