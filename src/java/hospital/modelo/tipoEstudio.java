package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class tipoEstudio {

    int idTipo;
    private String sdescription;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public tipoEstudio() {
        // this.idTipo = 1;
        this.sdescription = null;

    }

    public List<tipoEstudio> buscaTodos() throws Exception {
        List<tipoEstudio> listaCE = null;
        tipoEstudio oCE;
        ArrayList vRowTemp;
        // ArrayList idCE = null;
        // idCE=oAD.ejecutarConsulta(a);
        //idCE = oAD.ejecutarConsulta("select idtipo from tipo where sdes='" + this.getDescription() + "'");

        try {
            sQuery = "select * from todostipo();";
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
                listaCE = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oCE = new tipoEstudio();
                    oCE.setIdTipo(((Double) vRowTemp.get(0)).intValue());
                    oCE.setDescription((String) vRowTemp.get(1));

                    listaCE.add(oCE);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("Lista" + listaCE);
        return listaCE;
    }

//    public tipoEstudio buscaAreaPorDescCorta(String descCorta) throws Exception {
//        tipoEstudio oArea = null;
//        ArrayList vRowTemp;
//
//        try {
//            sQuery = "Select * from areas_x_desccorta('" + descCorta + "');";
//            if (oAD == null) {
//                oAD = new AccesoDatos();
//                if (oAD.conectar()) {
//                    resultado = oAD.ejecutarConsulta(sQuery);
//                    oAD.desconectar();
//                }
//                oAD = null;
//            } else {
//                resultado = oAD.ejecutarConsulta(sQuery);
//                oAD.desconectar();
//            }
//            if (resultado != null) {
//                vRowTemp = (ArrayList) resultado.get(0);
//                oArea = new tipoEstudio();
//                oArea.setIdArea(((Double) vRowTemp.get(0)).intValue());
//                oArea.setArea((String) vRowTemp.get(1));
//                oArea.setDescCorta((String) vRowTemp.get(2));
//            } else {
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//        return oArea;
//    }
    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getDescription() {
        return sdescription;
    }

    public void setDescription(String sdescription) {
        this.sdescription = sdescription;
    }

}
