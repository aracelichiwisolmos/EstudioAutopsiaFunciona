package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class menu {

    private int iditem;
    private String opcion;
    private String tipo_opcion;
    private String tipo_usuario;
    private int codigo_submenu;
    private String estado;
    private String url;
    private String icono;

    private AccesoDatos oAD;
    private String sQuery;
    private ArrayList resultado = null;

    public menu() {
        this.iditem = 0;
        this.opcion = null;
        this.tipo_opcion = null;
        this.tipo_usuario = null;
        this.codigo_submenu = 0;
        this.estado = null;
        this.url = null;
        this.icono = null;
    }

    public List<menu> buscaTodos() {
        List<menu> lista = null;
        menu omenu;
        ArrayList vRowTemp;

        try {
            sQuery = "Select m.opcion, m.tipo_opcion, m.tipo_usuario, m.codigo_submenu, m.estado, m.url, m.icono, m.iditem From menu as m ;";
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
                    omenu = new menu();
                    omenu.setOpcion((String) vRowTemp.get(0));
                    omenu.setTipo_opcion((String) vRowTemp.get(1));
                    omenu.setTipo_usuario((String) vRowTemp.get(2));
                    omenu.setCodigo_submenu(((Double) vRowTemp.get(3)).intValue());
                    omenu.setEstado((String) vRowTemp.get(4));
                    omenu.setUrl((String) vRowTemp.get(5));
                    omenu.setIcono((String) vRowTemp.get(6));
                    omenu.setIditem(((Double) vRowTemp.get(7)).intValue());
                    lista.add(omenu);
                }

            } else {
            }
        } catch (Exception e) {

        }
        return lista;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getTipo_opcion() {
        return tipo_opcion;
    }

    public void setTipo_opcion(String tipo_opcion) {
        this.tipo_opcion = tipo_opcion;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public int getCodigo_submenu() {
        return codigo_submenu;
    }

    public void setCodigo_submenu(int codigo_submenu) {
        this.codigo_submenu = codigo_submenu;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public AccesoDatos getoAD() {
        return oAD;
    }

    public void setoAD(AccesoDatos oAD) {
        this.oAD = oAD;
    }

    public String getsQuery() {
        return sQuery;
    }

    public void setsQuery(String sQuery) {
        this.sQuery = sQuery;
    }

    public ArrayList getResultado() {
        return resultado;
    }

    public void setResultado(ArrayList resultado) {
        this.resultado = resultado;
    }

    public int getIditem() {
        return iditem;
    }

    public void setIditem(int iditem) {
        this.iditem = iditem;
    }

    public menu getSubmenu() {
        menu omenu = null;

        try {
            sQuery = "Select m.opcion, m.tipo_opcion, m.tipo_usuario, m.codigo_submenu, m.estado, m.url, m.icono, m.iditem From menu as m Where m.iditem=" + this.getCodigo_submenu() + ";";
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
                omenu = new menu();
                omenu.setOpcion((String) vRowTemp.get(0));
                omenu.setTipo_opcion((String) vRowTemp.get(1));
                omenu.setTipo_usuario((String) vRowTemp.get(2));
                omenu.setCodigo_submenu(((Double) vRowTemp.get(3)).intValue());
                omenu.setEstado((String) vRowTemp.get(4));
                omenu.setUrl((String) vRowTemp.get(5));
                omenu.setIcono((String) vRowTemp.get(6));
                omenu.setIditem(((Double) vRowTemp.get(7)).intValue());

            }
        } catch (Exception e) {

        }
        return omenu;
    }
}
