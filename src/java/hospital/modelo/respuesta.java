package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class respuesta implements Serializable {

    private String sIdResp;
    private int idPreg;
    private String respuesta;
    private int nIdResp;
    private String descCorta;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public respuesta() {
        this.sIdResp = "";
        this.idPreg = 0;
        this.respuesta = "";
        this.descCorta = "";
        this.nIdResp = 0;
    }

    public List<respuesta> respuestasPorPregunta(int codPregunta) throws Exception {
        List<respuesta> lista = null;
        respuesta oRespuesta;
        ArrayList vRowTemp;

        try {
            sQuery = "select * from respuestasporpreguntaPrueba(" + codPregunta + ");";
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
                    oRespuesta = new respuesta();
                    oRespuesta.setsIdResp((String) vRowTemp.get(0));
                    oRespuesta.setIdPreg(((Double) vRowTemp.get(1)).intValue());
                    oRespuesta.setRespuesta((String) vRowTemp.get(2));
                    oRespuesta.setDescCorta((String) vRowTemp.get(3));
                    oRespuesta.setNIdResp(((Double) vRowTemp.get(4)).intValue());
                    lista.add(oRespuesta);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    public respuesta respuestasPorPregunta_DescCorta(int codPregunta, String descCorta) throws Exception {
        respuesta oRespuesta = null;
        ArrayList vRowTemp;

        try {
            sQuery = "select * from respuestas_x_pregunta_respcorta(" + codPregunta + ",'" + descCorta + "');";
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
                oRespuesta = new respuesta();
                oRespuesta.setsIdResp((String) vRowTemp.get(0));
                oRespuesta.setIdPreg(((Double) vRowTemp.get(1)).intValue());
                oRespuesta.setRespuesta((String) vRowTemp.get(2));
                oRespuesta.setDescCorta((String) vRowTemp.get(3));
                oRespuesta.setNIdResp(((Double) vRowTemp.get(4)).intValue());

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oRespuesta;
    }

    public List<respuesta> infoRespuesta() throws Exception {
        List<respuesta> lista = null;
        respuesta oRespuesta;
        ArrayList vRowTemp;

        try {
            sQuery = "SELECT * from  infoRespuesta();";
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
                    oRespuesta = new respuesta();
                    oRespuesta.setRespuesta((String) vRowTemp.get(1));
                    oRespuesta.setDescCorta((String) vRowTemp.get(0));
                    lista.add(oRespuesta);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    public String getsIdResp() {
        return sIdResp;
    }

    public void setsIdResp(String sIdResp) {
        this.sIdResp = sIdResp;
    }

    public int getIdPreg() {
        return idPreg;
    }

    public void setIdPreg(int idPreg) {
        this.idPreg = idPreg;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getNIdResp() {
        return nIdResp;
    }

    public void setNIdResp(int nIdResp) {
        this.nIdResp = nIdResp;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }
}
