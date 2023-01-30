package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class usuario {

    private String nombre;
    private String usuario;
    private String clave;
    private String aPaterno;
    private String aMaterno;
    private String tipo;
    private int idUsuario;
    private AccesoDatos oAD;
    private String sQuery;
    ArrayList resultado = null;

    public usuario() {
        this.nombre = "";
        this.usuario = "";
        this.clave = "";
        this.aPaterno = "";
        this.aMaterno = "";
        this.tipo = "";
        this.idUsuario = 0;
    }

    public void insertar(usuario usuario) {
        try {
            sQuery = "SELECT insertar_usuario('" + usuario.nombre + "', '"
                    + usuario.aPaterno + "', '" + usuario.aMaterno + "', '" + usuario.usuario + "', MD5('"
                    + usuario.clave + "'), '" + usuario.tipo + "'" + ");";
            System.out.println("----------------------"+sQuery);
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void actualizar(usuario usuario) throws Exception {
        try {
            sQuery = "SELECT actualizar_usuario1('" + usuario.nombre + "', '"
                    + usuario.aPaterno + "', '" + usuario.aMaterno + "', '" + usuario.usuario
                    + "', '" + usuario.tipo + "', " + usuario.idUsuario + ");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void cambiarPassword(usuario usuario) throws Exception {
        try {
            sQuery = "SELECT cambiar_pass('"+usuario.clave+"',"+usuario.idUsuario+");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void eliminar(int id) {
        try {
            sQuery = "SELECT eliminar_usuario(" + id + ");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public usuario iniciarSesion(usuario usuario) throws Exception {
        usuario oUsuario = null;
        try {
            sQuery = "Select u.nombre, u.paterno, u.materno, u.usuario, u.clave, u.tipo, u.idusuario From usuario as u Where u.clave=MD5('" + usuario.getClave() + "') and u.usuario='" + usuario.getUsuario() + "';";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    resultado = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                resultado = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (resultado != null) {
                ArrayList vRowTemp = (ArrayList) resultado.get(0);
                oUsuario = new usuario();
                oUsuario.setNombre((String) vRowTemp.get(0));
                oUsuario.setPaterno((String) vRowTemp.get(1));
                oUsuario.setMaterno((String) vRowTemp.get(2));
                oUsuario.setUsuario((String) vRowTemp.get(3));
                oUsuario.setClave((String) vRowTemp.get(4));
                oUsuario.setTipo((String) vRowTemp.get(5));
                oUsuario.setIdUsuario(((Double) vRowTemp.get(6)).intValue());
            } else {
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
        return oUsuario;
    }

    public List<usuario> buscaTodos() {
        List<usuario> lista = null;
        usuario oUsuario;
        ArrayList vRowTemp;

        try {
            sQuery = "Select u.nombre, u.paterno, u.materno, u.usuario, u.clave, u.tipo, u.idusuario From usuario as u ;";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    resultado = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                resultado = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (resultado != null) {
                lista = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oUsuario = new usuario();
                    oUsuario.setNombre((String) vRowTemp.get(0));
                    oUsuario.setPaterno((String) vRowTemp.get(1));
                    oUsuario.setMaterno((String) vRowTemp.get(2));
                    oUsuario.setUsuario((String) vRowTemp.get(3));
                    oUsuario.setClave((String) vRowTemp.get(4));
                    oUsuario.setTipo((String) vRowTemp.get(5));
                    oUsuario.setIdUsuario(((Double) vRowTemp.get(6)).intValue());
                    lista.add(oUsuario);
                }

            } else {
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
        return lista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String contrasenia) {
        this.clave = contrasenia;
    }

    public String getPaterno() {
        return aPaterno;
    }

    public void setPaterno(String paterno) {
        this.aPaterno = paterno;
    }

    public String getMaterno() {
        return aMaterno;
    }

    public void setMaterno(String materno) {
        this.aMaterno = materno;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

}
