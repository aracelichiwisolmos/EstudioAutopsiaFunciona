package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;

public class categoria {

    private int idCategoria;
    private String categoria;
    private String descCorta;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public categoria() {
        this.idCategoria = -1;
        this.categoria = null;
        this.descCorta = null;
    }

    public categoria buscaCategoriaPorDescCorta(String descCorta) throws Exception {
        categoria oCategoria = null;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from categoria_x_desccorta('" + descCorta + "');";
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
                oCategoria = new categoria();
                oCategoria.setIdCategoria(((Double) vRowTemp.get(0)).intValue());
                oCategoria.setCategoria((String) vRowTemp.get(1));
                oCategoria.setDescCorta((String) vRowTemp.get(2));
            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

}
