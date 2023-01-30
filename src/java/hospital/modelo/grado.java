package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class grado {

    private int idGrado;
    private String grado;
    private String descCorta;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public List<grado> buscaTodos() throws Exception {
        List<grado> lista = null;
        grado oGrado;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from gradostodos() ;";
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
                    oGrado = new grado();
                    oGrado.setIdGrado(((Double) vRowTemp.get(0)).intValue());
                    oGrado.setGrado((String) vRowTemp.get(1));
                    oGrado.setDescCorta((String) vRowTemp.get(2));
                    lista.add(oGrado);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    public grado buscaGradoPorDescCorta(String descCorta) throws Exception {
        grado oGrado = null;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from grado_x_desccorta('" + descCorta + "');";
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
                vRowTemp = (ArrayList) resultado.get(0);
                oGrado = new grado();
                oGrado.setIdGrado(((Double) vRowTemp.get(0)).intValue());
                oGrado.setGrado((String) vRowTemp.get(1));
                oGrado.setDescCorta((String) vRowTemp.get(2));

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oGrado;
    }

    public grado() {
        this.idGrado = 0;
        this.grado = "";
        this.descCorta = "";
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

}
