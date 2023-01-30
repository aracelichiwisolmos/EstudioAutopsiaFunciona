package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class ciudad {

    int idCiudad;
    private String sdesCi;
    private int idEstado;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public ciudad() {
        // this.idCiudad = 0;
        this.sdesCi = null;
        //this.idEstado = 0;
    }

    public List<ciudad> buscaTodos() throws Exception {
        List<ciudad> listaCiudad = null;
        //SelectItem item;
        ciudad oCiudad;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from ciudadestodas();";
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
                listaCiudad = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oCiudad = new ciudad();
                    oCiudad.setIdCiudad(((Double) vRowTemp.get(0)).intValue());
                    oCiudad.setDesCi((String) vRowTemp.get(1));
                    oCiudad.setIdEstado(((Double) vRowTemp.get(2)).intValue());
                    listaCiudad.add(oCiudad);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("Ciudad " + listaCiudad);
        return listaCiudad;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getDesCi() {
        System.out.println("get descripcion ciudad" + sdesCi);
        return sdesCi;
    }

    public void setDesCi(String sdesCi) {
        this.sdesCi = sdesCi;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

}
