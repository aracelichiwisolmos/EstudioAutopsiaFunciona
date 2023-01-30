package hospital.controlador;

import hospital.modelo.usuario;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

@ManagedBean(name = "oUsuarioJb")
@ViewScoped
public class usuarioJb implements Serializable {

    private usuario usuario;
    private List<usuario> usuarios;

    @PostConstruct
    public void init() {
        setUsuario(new usuario());
        setUsuarios(usuario.buscaTodos());
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public List<usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
