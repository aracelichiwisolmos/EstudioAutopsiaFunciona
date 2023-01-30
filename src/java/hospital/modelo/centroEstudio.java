package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class centroEstudio {

    private int idCentro;
    private String nombreCentro;
    private int idTipo;
    private String nombreCorto;
    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;

    private int idCiudad;
    private tipoEstudio oCE;
    private ciudad oCiudad;
    private String sdescription;
    private String sdesCi;

    public centroEstudio() {
        this.idCentro = 0;
        this.nombreCentro = null;

        oCE = new tipoEstudio();
        this.sdescription = null;

        oCiudad = new ciudad();
        this.sdesCi = null;
        // this.idTipo = 0;
        //this.nombreCorto = null;
    }

    public centroEstudio buscaCentroEstudioPorDescCorta(String descCorta) throws Exception {
        centroEstudio oCentro = null;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from centroestudio_x_desccorta('" + descCorta + "');";
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
                oCentro = new centroEstudio();
                oCentro.setIdCentro(((Double) vRowTemp.get(0)).intValue());
                oCentro.setNombreCentro((String) vRowTemp.get(1));
                oCentro.setNombreCorto((String) vRowTemp.get(2));
                oCentro.setIdTipo(((Double) vRowTemp.get(3)).intValue());

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oCentro;
    }

    public List<SelectItem> getCentros() throws Exception {
        List<SelectItem> lResp = null;
        SelectItem item;
        ArrayList rst = null;

        sQuery = "Select * from centrostodos();";
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

    public int insertar() throws Exception {
//numeros aleatorios

        int nRet = 0;
        ArrayList rst = null;
        String sQuery = "";
        System.out.println("Nombre: " + getNombreCentro() + "tipo" + getDescription() + "ciudad" + getDesCi());

        if (getNombreCentro().isEmpty()) {
            //if (getDescripcion().isEmpty()) {
            throw new Exception("centroEstudio.insertar: error de programación, faltan datos");
        } else {
            sQuery = "SELECT * FROM insertarCE2( "
                    + "'" + getNombreCentro() + "'::character varying,"
                    + getDescription() + "::integer,"
                    + getDesCi() + "::integer)"
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

    public int getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(int idCentro) {
        this.idCentro = idCentro;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }
//-----------------

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    //---
    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getDescription() {
        return sdescription;
    }

    public void setDescription(String sdescription) {
        this.sdescription = sdescription;
    }

    public String getDesCi() {
        return sdesCi;
    }

    public void setDesCi(String sdesCi) {
        this.sdesCi = sdesCi;
    }

}
