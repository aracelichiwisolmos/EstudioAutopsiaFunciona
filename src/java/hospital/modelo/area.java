
package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;


public class area {

    int idArea;
    private String area;
    private String descCorta;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public area() {
        this.idArea = 0;
        this.area = "";
        this.descCorta = "";
    }
    
    public List<area> buscaTodos() throws Exception {
        List<area> lista = null;
        area oArea;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from areastodos() ;";
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
                    oArea = new area();
                    oArea.setIdArea(((Double) vRowTemp.get(0)).intValue());
                    oArea.setArea((String) vRowTemp.get(1));
                    oArea.setDescCorta((String) vRowTemp.get(2));
                    lista.add(oArea);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    public area buscaAreaPorDescCorta(String descCorta) throws Exception {
        area oArea = null;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from areas_x_desccorta('"+descCorta+"');";
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
                oArea = new area();
                oArea.setIdArea(((Double) vRowTemp.get(0)).intValue());
                oArea.setArea((String) vRowTemp.get(1));
                oArea.setDescCorta((String) vRowTemp.get(2));
            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oArea;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    
}
