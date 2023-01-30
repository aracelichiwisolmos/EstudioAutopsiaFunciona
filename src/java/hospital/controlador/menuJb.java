package hospital.controlador;

import hospital.modelo.menu;
import hospital.modelo.usuario;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@ManagedBean(name = "oMenuJb")
@SessionScoped
public class menuJb implements Serializable {

    private List<menu> lista;
    private menu menu;
    private MenuModel model;

    @PostConstruct
    public void init() {
        this.menuAplicacion();
        setModel(new DefaultMenuModel());
        this.establecerPermisos();

    }

    public void menuAplicacion() {
        try {
            menu = new menu();
            lista = menu.buscaTodos();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void establecerPermisos() {
        try {
            String tipo;
            usuario us = (usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            if (us != null) {
                tipo = us.getTipo();
            } else {
                tipo = "D";
            }

            lista.stream().forEach((m) -> {
                if (m.getTipo_opcion().equals("S") && m.getTipo_usuario().equals(tipo)) {
                    DefaultSubMenu firstSubmenu = new DefaultSubMenu(m.getOpcion());
                    firstSubmenu.setIcon(m.getIcono());
                    lista.stream().forEach((i) -> {
                        menu submenu = i.getSubmenu();
                        if (submenu != null) {
                            if (submenu.getIditem() == m.getIditem()) {
                                DefaultMenuItem item = new DefaultMenuItem(i.getOpcion());
                                item.setOutcome(i.getUrl());
                                item.setIcon(i.getIcono());
                                firstSubmenu.addElement(item);
                            }
                        }
                    });
                    model.addElement(firstSubmenu);

                } else if (m.getSubmenu() == null && m.getTipo_usuario().equals(tipo)) {
                    DefaultMenuItem item = new DefaultMenuItem(m.getOpcion());
                    item.setIcon(m.getIcono());
                    item.setOutcome(m.getUrl());
                    getModel().addElement(item);
                }
            });
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }
}
