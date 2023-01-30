package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.model.SelectItem;

public class institucion {

    private String snombre_ins;
    private int clave_ins = 0;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    public institucion() {
        // this.centro_hospitalario = null;
        this.clave_ins = 0;
        this.snombre_ins = null;

    }

    public List<institucion> buscaTodos() throws Exception {
        List<institucion> ListaIns = null;
        institucion oIns;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from todosInstitucion();";
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
                ListaIns = new ArrayList<>();
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    oIns = new institucion();
                    oIns.setClaveIns(((Double) vRowTemp.get(0)).intValue());
                    oIns.setNombreIns((String) vRowTemp.get(1));
                    // oGrado.setDescCorta((String) vRowTemp.get(2));
                    ListaIns.add(oIns);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return ListaIns;
    }

    public int insertar() throws Exception {
//numeros aleatorios

        int nRet = 0;
        ArrayList rst = null;
        String sQuery = "";
        System.out.println("" + getNombreIns() + "");
        //comprueba si esta cadena está vacía o no. Devuelve verdadero , si la longitud de la cadena es 0, de lo contrario es falso . En otras palabras, se devuelve verdadero si la cadena
        //está vacía; de lo contrario, devuelve falso.
        if (getNombreIns().isEmpty()) {
            //if (getDescripcion().isEmpty()) {
            throw new Exception("IdentificacionUSuario.insertar: error de programación, faltan datos");
        } else {
            sQuery = "SELECT * FROM insertarinstitucion( "
                    + "'" + getNombreIns() + "'::character varying"
                    + ";";
            System.out.println(sQuery);
            oAD = new AccesoDatos();
            if (oAD.conectar()) {
                rst = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (rst != null && rst.size() == 1) {
                ArrayList vRowTemp = (ArrayList) rst.get(0);
                nRet = ((Double) vRowTemp.get(0)).intValue();
            }
        }
        return nRet;
    }

    public void setClaveIns(int clave_ins) {
        this.clave_ins = clave_ins;

    }

    public int getClaveIns() {
        return clave_ins;
    }

    public void setNombreIns(String snombre_ins) {
        this.snombre_ins = snombre_ins;
    }

    public String getNombreIns() {
        return snombre_ins;
    }

}
