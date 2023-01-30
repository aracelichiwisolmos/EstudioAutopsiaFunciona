package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class estado {

    int idEstado;
    private String sdescEs;
    private int idPais;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public estado() {
        this.idEstado = 0;
        this.sdescEs = "";
        this.idPais = 0;
    }

    public List<SelectItem> getEstados() throws Exception {
        List<SelectItem> lResp = null;
        SelectItem item;
        ArrayList rst = null;

        sQuery = "Select * from estadostodos();";
        if (oAD == null) {
            oAD = new AccesoDatos();
            if (oAD.conectar()) {
                rst = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            oAD = null;
        } else {
            rst = oAD.ejecutarConsulta(sQuery);
            oAD.desconectar();
        }
        if (rst != null) {
            lResp = new ArrayList<>();

            for (int i = 0; i < rst.size(); i++) {
                item = new SelectItem();
                ArrayList vRowTemp = (ArrayList) rst.get(i);
                item.setValue(((Double) vRowTemp.get(0)).intValue());
                item.setLabel((String) vRowTemp.get(1) + " - " + (String) vRowTemp.get(2) + " - " + (String) vRowTemp.get(3));
                lResp.add(item);
            }
        }
        return lResp;

    }

    public List<estado> buscaTodos() throws Exception {
        List<estado> lista = null;
        //SelectItem item;
        estado oEstado;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from estadostodos() ;";
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
                    oEstado = new estado();
                    oEstado.setIdEstado(((Double) vRowTemp.get(0)).intValue());
                    oEstado.setSdescEs((String) vRowTemp.get(1));
                    oEstado.setIdPais(((Double) vRowTemp.get(2)).intValue());
                    lista.add(oEstado);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getSdescEs() {

        return sdescEs;
    }

    public void setSdescEs(String sdescEs) {
        this.sdescEs = sdescEs;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

}
