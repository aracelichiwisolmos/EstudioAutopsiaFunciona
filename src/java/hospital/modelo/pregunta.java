package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class pregunta {

    private int idPregunta;
    private String numPregunta;
    private String pregunta;
    private String nomCortoPregunta;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public pregunta() {
        this.idPregunta = 0;
        this.nomCortoPregunta = "";
        this.numPregunta = "";
        this.pregunta = "";
        this.resultado = null;
    }

    public pregunta selectPreguntaPorDescCorta(String desc_corta) throws Exception {
        pregunta oPregunta = null;
        try {
            sQuery = "Select * From pregunta_x_desc_corta('" + desc_corta + "');";
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
                oPregunta = new pregunta();
                oPregunta.setIdPregunta(((Double) vRowTemp.get(0)).intValue());
                oPregunta.setNumPregunta((String) vRowTemp.get(1));
                oPregunta.setPregunta((String) vRowTemp.get(2));
                oPregunta.setNomCortoPregunta((String) vRowTemp.get(3));

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oPregunta;
    }

    public pregunta selectPreguntaPorId(int idPregunta) throws Exception {
        pregunta oPregunta = null;
        try {
            sQuery = "Select * From preguntaxid(" + idPregunta + ");";
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
                oPregunta = new pregunta();
                oPregunta.setIdPregunta(((Double) vRowTemp.get(0)).intValue());
                oPregunta.setNumPregunta((String) vRowTemp.get(1));
                oPregunta.setPregunta((String) vRowTemp.get(2));
                oPregunta.setNomCortoPregunta((String) vRowTemp.get(3));

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oPregunta;
    }

    public List<pregunta> buscaTodos() throws Exception {
        List<pregunta> lista = null;
        pregunta oPregunta;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from preguntastodos() ;";
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
                    oPregunta = new pregunta();
                    oPregunta.setIdPregunta(((Double) vRowTemp.get(0)).intValue());
                    oPregunta.setNumPregunta((String) vRowTemp.get(1));
                    oPregunta.setPregunta((String) vRowTemp.get(2));
                    oPregunta.setNomCortoPregunta((String) vRowTemp.get(3));
                    lista.add(oPregunta);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }
    
    public List<pregunta> infoPregunta() throws Exception {
        List<pregunta> lista = null;
        pregunta oPregunta;
        ArrayList vRowTemp;

        try {
            sQuery = "SELECT * from  infoPregunta();";
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
                    oPregunta = new pregunta();
                    oPregunta.setNomCortoPregunta((String) vRowTemp.get(0));
                    oPregunta.setPregunta((String) vRowTemp.get(1));
                    lista.add(oPregunta);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }
    

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getNumPregunta() {
        return numPregunta;
    }

    public void setNumPregunta(String numPregunta) {
        this.numPregunta = numPregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getNomCortoPregunta() {
        return nomCortoPregunta;
    }

    public void setNomCortoPregunta(String nomCortoPregunta) {
        this.nomCortoPregunta = nomCortoPregunta;
    }

}
