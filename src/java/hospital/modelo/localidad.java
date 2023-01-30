package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.model.SelectItem;

public class localidad {

    private String snombre_lo;
    private int id_localidad;

    private int idmunicipio;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public localidad() {
        // this.centro_hospitalario = null;
        this.id_localidad = 0;
        this.snombre_lo = null;

    }

    public List<localidad> buscaTodos() throws Exception {
        List<localidad> ListaLo = null;
        localidad oLo;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from todosLocalidad();";
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
                ListaLo = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oLo = new localidad();
                    oLo.setIdLocalidad(((Double) vRowTemp.get(0)).intValue());
                    oLo.setNombreLo((String) vRowTemp.get(1));
                    oLo.setIdMun(((Double) vRowTemp.get(2)).intValue());
                    // oGrado.setDescCorta((String) vRowTemp.get(2));
                    ListaLo.add(oLo);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return ListaLo;
    }

    public void setIdLocalidad(int id_localidad) {
        this.id_localidad = id_localidad;

    }

    public int getIdLocalidad() {
        return id_localidad;
    }

    public void setNombreLo(String snombre_lo) {
        this.snombre_lo = snombre_lo;
    }

    public String getNombreLo() {
        return snombre_lo;
    }

    public void setIdMun(int idmunicipio) {
        this.idmunicipio = idmunicipio;

    }

    public int getIdMun() {
        return idmunicipio;
    }

}
