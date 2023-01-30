package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;


public class centroHospitalario {

    private String nombreCH;
    private int clave_ch = 0;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public centroHospitalario() {
        // this.centro_hospitalario = null;
        this.clave_ch = 0;
        this.nombreCH = null;

    }

    public List<centroHospitalario> buscaTodos() throws Exception {
        List<centroHospitalario> lista = null;
        centroHospitalario oCeHo;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from todosCH();";
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
                    oCeHo = new centroHospitalario();
                    oCeHo.setClaveCH(((Double) vRowTemp.get(0)).intValue());
                    oCeHo.setNombreCH((String) vRowTemp.get(1));
                    // oGrado.setDescCorta((String) vRowTemp.get(2));
                    lista.add(oCeHo);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }
    
     @Override
    public String toString() {
        return "H"+this.getClaveCH()  ;
    }

    public void setClaveCH(int clave_ch) {
        this.clave_ch = clave_ch;

    }

    public int getClaveCH() {
        return clave_ch;
    }

    public void setNombreCH(String nombreCH) {
        this.nombreCH = nombreCH;
    }

    public String getNombreCH() {
        return nombreCH;
    }

}
