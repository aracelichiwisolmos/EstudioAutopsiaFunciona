package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
//import java.util.Random;
//import javax.faces.model.SelectItem;

public class municipio {

    private String snombre_mun;
    private int id_munucipio;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public municipio() {
        // this.centro_hospitalario = null;
        this.id_munucipio = 0;
        this.snombre_mun = null;

    }

    public List<municipio> buscaTodos() throws Exception {
        List<municipio> ListaMun = null;
        municipio oMun;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from todosMunicipio();";
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
                ListaMun = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oMun = new municipio();
                    oMun.setIdMun(((Double) vRowTemp.get(0)).intValue());
                    oMun.setNombreMun((String) vRowTemp.get(1));
                    // oGrado.setDescCorta((String) vRowTemp.get(2));
                    ListaMun.add(oMun);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return ListaMun;
    }

    public void setIdMun(int id_municipio) {
        //  this.id_munucipio = id_munucipio;
        this.id_munucipio = id_municipio;

    }

    public int getIdMun() {
        return id_munucipio;
    }

    public void setNombreMun(String snombre_mun) {
        this.snombre_mun = snombre_mun;
    }

    public String getNombreMun() {
        return snombre_mun;
    }

}
