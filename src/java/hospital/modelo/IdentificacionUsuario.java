package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.model.SelectItem;

public class IdentificacionUsuario {

    //private String clave;
    private String clave3;

    public String getClave3() {
        return clave3;
    }

    public void setClave3(String clave3) {
        this.clave3 = clave3;
    }
    private String clave2;
    private String clave = "";
    private String centro_hospitalario = "";

    private String institucion = "";
    private String localidad;
    private String municipio;
    private int medico;

    private String sQuery;
    private AccesoDatos oAD;
    private ArrayList resultado;
    private String conteo = "";
    private ArrayList vRowTemp;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    private String total = "";

    ArrayList res5 = null;
    // private IdentificacionUsuario oId;
    private centroHospitalario oCeHo;
    private institucion oIns;
    private municipio oMun;
    private localidad oLo;
    private medico oMedico;

    private int clIns;
    private int clMun;
    private int clLoc;
    private int clCh;

    // IdentificacionUsuario oId = new IdentificacionUsuario();
    public IdentificacionUsuario() {
        //  oId = new IdentificacionUsuario();

        oCeHo = new centroHospitalario();
        oIns = new institucion();
        oMun = new municipio();
        oLo = new localidad();
        oMedico = new medico();
        // this.centro_hospitalario = null;
        // this.clave = null;
//        this.conteo = "";
//        this.total = "";
        this.centro_hospitalario = null;
        this.institucion = null;
        this.localidad = null;
        this.municipio = null;
        this.medico = 0;

    }

    public IdentificacionUsuario buscaIdUsuarioPorDescCorta(String descCorta) throws Exception {
        IdentificacionUsuario oIdUsuario = null;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from identificacion_usuario('" + descCorta + "');";
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
                oIdUsuario = new IdentificacionUsuario();
                //  oIdUsuario.setIdCentro(((Double) vRowTemp.get(0)).intValue());
                oIdUsuario.setCentroHospitalario((String) vRowTemp.get(0));
                oIdUsuario.setInstitucion((String) vRowTemp.get(1));
                oIdUsuario.setLocalidad((String) vRowTemp.get(2));
                oIdUsuario.setMunicipio((String) vRowTemp.get(3));
                // oIdUsuario.localidad(((Double) vRowTemp.get(2)).intValue());

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return oIdUsuario;
    }

    public List<IdentificacionUsuario> buscaTodos() throws Exception {
        List<IdentificacionUsuario> lista = null;
        IdentificacionUsuario oIU;
        ArrayList vRowTemp;

        try {
            sQuery = "Select * from todosidusuario();";
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
                    oIU = new IdentificacionUsuario();
                    oIU.setClave3((String) vRowTemp.get(0));
                    oIU.setCentroHospitalario((String) vRowTemp.get(1));
                    oIU.setInstitucion((String) vRowTemp.get(2));
                    oIU.setLocalidad((String) vRowTemp.get(3));
                    oIU.setMunicipio((String) vRowTemp.get(4));
                    oIU.setMedico(((Double) vRowTemp.get(5)).intValue());

                    // oGrado.setDescCorta((String) vRowTemp.get(2));
                    lista.add(oIU);
                }

            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("lista usuario" + lista);
        return lista;
    }

    public List<SelectItem> getIdUsuario() throws Exception {
        List<SelectItem> lResp = null;
        SelectItem item;
        ArrayList rst = null;

        sQuery = "Select * from idusauriotodos();";
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
                //item.setValue(((Double) vRowTemp.get(0)).intValue());
                item.setLabel((String) vRowTemp.get(0) + " - " + (String) vRowTemp.get(1) + " - " + (String) vRowTemp.get(2) + " - " + (String) vRowTemp.get(3));
                lResp.add(item);
            }
        }
        return lResp;

    }

    public int insertar() throws Exception {
//numeros aleatorios
        Random aleatorio = new Random(System.currentTimeMillis());
        int intAletorio = aleatorio.nextInt(100);
        clave2 = String.valueOf(intAletorio);
        System.out.println("" + clave2 + "  " + centro_hospitalario);
        for (int x = 0; x < centro_hospitalario.length(); x++) {
            char c = centro_hospitalario.charAt(x);

            if (Character.isUpperCase(c)) {
                clave = clave + String.valueOf(c);
            }
        }
        clave3 = clave + clave2;
        System.out.println("clave 3:" + clave3 + " Clave" + clave);
        //  return clave;

        int nRet = 0;
        ArrayList rst = null;
        String sQuery = "";

        System.out.println(
                "" + clave3 + " " + getCentroHospitalario() + " " + getInstitucion() + " " + getMunicipio() + " " + getLocalidad());
        //comprueba si esta cadena está vacía o no. Devuelve verdadero , si la longitud de la cadena es 0, de lo contrario es falso . En otras palabras, se devuelve verdadero si la cadena
        //está vacía; de lo contrario, devuelve falso.
        if (getCentroHospitalario().isEmpty() || this.getInstitucion().isEmpty() || this.getLocalidad().isEmpty() || this.getMunicipio().isEmpty()) {

            throw new Exception("IdentificacionUSuario.insertar: error de programación, faltan datos");
        } else {

            System.out.println("-------------paso 1-------------");
            String q1 = "select snombre_ch from centro_hospitalario2 where snombre_ch='" + this.getCentroHospitalario() + "'";
            String q2 = "select snombre_ins from institucion2 where snombre_ins='" + this.getInstitucion() + "'";
            String q3 = "select snombre_mun from municipio2 where snombre_mun='" + this.getMunicipio() + "'";
            String q4 = "select snombre_lo from localidad4 where snombre_lo='" + this.getLocalidad() + "'";
            // String q5 = null;
            oAD = new AccesoDatos();
            System.out.println("-------------paso arraylist-------------");
            ArrayList res1 = null;
            ArrayList res2 = null;
            ArrayList res3 = null;
            ArrayList res4 = null;
            ArrayList res5 = null;
            if (oAD.conectar()) {
                System.out.println("-------------paso ejecutar consulta-------------");
                res1 = oAD.ejecutarConsulta(q1);
                res2 = oAD.ejecutarConsulta(q2);
                res3 = oAD.ejecutarConsulta(q3);
                res4 = oAD.ejecutarConsulta(q4);
                //res5 = oAD.ejecutarConsulta(q5);

                System.out.println("res4" + res4);
                oAD.desconectar();
                System.out.println("-------------paso ejecutar consulta termino-------------");
            }
            ArrayList idMun = null;
            ArrayList idCH = null;
            ArrayList idIns = null;
            ArrayList idLoc = null;
            int a1 = 0, b1 = 0, c1 = 0, d1 = 0;
            //if (res1 == null || res2 == null || res3 == null || res4 == null) {
            if (res1.isEmpty()) {
                System.out.println("-----------------paso ch----------------");
                String a = "select * from insertarCH('" + this.getCentroHospitalario() + "')";
                if (oAD.conectar()) {
                    idCH = oAD.ejecutarConsulta(a);
                    oAD.desconectar();
                }
                a1 = 1;
            }
            if (res2.isEmpty()) {
                System.out.println("------------paso ins-----------");
                String a = "select * from insertarinstitucion('" + this.getInstitucion() + "')";
                if (oAD.conectar()) {
                    idIns = oAD.ejecutarConsulta(a);
                    oAD.desconectar();
                }
                b1 = 1;
            }
            if (res3.isEmpty()) {
                System.out.println("-----------paso mun-------------");
                String a = "select * from insertarmunicipio('" + this.getMunicipio() + "')";
                if (oAD.conectar()) {
                    idMun = oAD.ejecutarConsulta(a);
                    oAD.desconectar();
                }
                c1 = 1;
            }
            if (res4.isEmpty()) {
                System.out.println("------------paso LO-----------");
                String a = "";
                if (c1 == 0) {
                    System.out.println("paso no existe");
                    String b = "select id_municipio from municipio2 where snombre_mun='" + this.getMunicipio() + "'";
                    ArrayList idex = null;
                    if (oAD.conectar()) {
                        idex = oAD.ejecutarConsulta(b);
                        oAD.desconectar();
                    }
                    a = "select * from insertarlocalidad('" + this.getLocalidad() + "'::varchar," + ((ArrayList) idex.get(0)).get(0) + "::integer)";
                } else {
                    System.out.println("----------paso lo si ya existe mun------------");
                    a = "select * from insertarlocalidad('" + this.getLocalidad() + "'::varchar," + ((ArrayList) idMun.get(0)).get(0) + "::integer)";

                }
                if (oAD.conectar()) {
                    idLoc = oAD.ejecutarConsulta(a);
                    // System.out.println("    IIIIIDDDDD DE LOC" + ((ArrayList) idLoc.get(0)).get(0));
                    oAD.desconectar();
                }
                d1 = 1;
            }
            if (a1 == 0) {
                if (oAD.conectar()) {
                    System.out.println("----------paso a==0------------");
                    idCH = oAD.ejecutarConsulta("select clave_ch from centro_hospitalario2 where snombre_ch='" + this.getCentroHospitalario() + "'");
                    oAD.desconectar();
                }
            }
            if (b1 == 0) {
                if (oAD.conectar()) {
                    System.out.println("----------paso b==0 ------------");
                    idIns = oAD.ejecutarConsulta("select clave_ins from institucion2 where snombre_ins='" + this.getInstitucion() + "'");
                    oAD.desconectar();
                }
            }
            if (c1 == 0) {
                if (oAD.conectar()) {
                    System.out.println("----------paso c1------------");
                    idMun = oAD.ejecutarConsulta("select id_municipio from municipio2 where snombre_mun='" + this.getMunicipio() + "'");
                    oAD.desconectar();
                }
            }
            if (d1 == 0) {
                if (oAD.conectar()) {
                    idLoc = oAD.ejecutarConsulta("select id_localidad from localidad4 where snombre_lo='" + this.getLocalidad() + "'");
                    oAD.desconectar();
                }
            }
            //aqui va nueva funcion insertaridusuario con foraneas
            System.out.println("----------paso funcion completa-------------");
            //String q5 = "SELECT  COUNT(*) FROM identificacion_usuario4 WHERE  centro_hospitalario=" + ((ArrayList) idCH.get(0)).get(0) + "::integer" + ";";

            this.setClIns(((Double) (((ArrayList) idIns.get(0)).get(0))).intValue());
            this.setClCh(((Double) ((((ArrayList) idCH.get(0)).get(0)))).intValue());
            this.setClLoc(((Double) ((((ArrayList) idLoc.get(0)).get(0)))).intValue());
            this.setClMun(((Double) ((((ArrayList) idMun.get(0)).get(0)))).intValue());
            // System.out.println("Van: " + q5 + " encuentas de :" + this.getCentroHospitalario());
            /*sQuery = "SELECT * FROM insertaridusuariobuena( "
                    + "'" + clave3 + "'::character varying, "
                    + ((ArrayList) idCH.get(0)).get(0) + "::integer, "
                    + ((ArrayList) idIns.get(0)).get(0) + "::integer,"
                    + ((ArrayList) idMun.get(0)).get(0) + "::integer,"
                    + ((ArrayList) idLoc.get(0)).get(0) + "::integer)"
                    + ";";*/

            if (oAD.conectar()) {
                //String q5 = "SELECT  COUNT(*) FROM identificacion_usuario4 WHERE  centro_hospitalario=" + ((ArrayList) idCH.get(0)).get(0) + "::integer" + ";";
                String q5 = "SELECT * from contarencuestas(" + ((ArrayList) idCH.get(0)).get(0) + "::integer" + ")" + ";";
                res5 = oAD.ejecutarConsulta(q5);
                conteo = clave + "/" + ((ArrayList) res5.get(0)).get(0);
                System.out.println("resultado: " + conteo);
                this.Resultado(conteo);
                //rst = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();

            }

            if (rst != null && rst.size() == 1) {
                ArrayList vRowTemp = (ArrayList) rst.get(0);
                nRet = ((Double) vRowTemp.get(0)).intValue();
            }
        }

        return nRet;
    }

    //....................................
//    public List<String> consultarCentroHospitalario(int idMedico) throws Exception {
//        try {
//            List<String> lista = new ArrayList<>();
//            String item;
//            sQuery = "SELECT * from consultar_centro_hospitalario(" + idMedico + ");";
//
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
//                for (int i = 0; i < resultado.size(); i++) {
//                    vRowTemp = (ArrayList) resultado.get(i);
//                    item = String.valueOf(((Double) vRowTemp.get(0)).intValue());
//                    lista.add(item);
//                }
//
//            }
//            return lista;
//        } catch (Exception e) {
//            throw e;
//        }
//    }
    public int consultarCentroHospitalario(int idMedico) throws Exception {
        try {
            int item = 0;
            // String item = "";
            sQuery = "SELECT * from consultar_centro_hospitalario(" + idMedico + ");";

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
                //item = ((String) vRowTemp.get(0));
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarInstitucion(int idMedico) throws Exception {
        try {
            int item = 0;
            // String item = "";
            sQuery = "SELECT * from consultar_institucion(" + idMedico + ");";

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
                //item = ((String) vRowTemp.get(0));
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarLocalidad(int idMedico) throws Exception {
        try {
            int item = 0;
            // String item = "";
            sQuery = "SELECT * from consultar_localidad(" + idMedico + ");";

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
                //item = ((String) vRowTemp.get(0));
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarMunicipio(int idMedico) throws Exception {
        try {
            int item = 0;
            System.out.println("Medico id ________" + idMedico);
            // String item = "";
            sQuery = "SELECT * from consultar_municipio(" + idMedico + ");";

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
                //item = ((String) vRowTemp.get(0));
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }
//.............
    //......................................

    public void Resultado(String conteo) {
        // System.out.println("set conteo" + conteo);
        total = conteo;
        System.out.println("total" + total);
        //this.conteo = conteo;
    }

    public String getResultado() {
        System.out.println("get conteo" + total);
        return total;
    }

    public void setCentroHospitalario(String centro_hospitalario) {
        this.centro_hospitalario = centro_hospitalario;
    }

    public String getCentroHospitalario() {
        return centro_hospitalario;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    /**
     * @return the clIns
     */
    public int getClIns() {
        return clIns;
    }

    /**
     * @param clIns the clIns to set
     */
    public void setClIns(int clIns) {
        this.clIns = clIns;
    }

    /**
     * @return the clMun
     */
    public int getClMun() {
        return clMun;
    }

    /**
     * @param clMun the clMun to set
     */
    public void setClMun(int clMun) {
        this.clMun = clMun;
    }

    /**
     * @return the clLoc
     */
    public int getClLoc() {
        return clLoc;
    }

    /**
     * @param clLoc the clLoc to set
     */
    public void setClLoc(int clLoc) {
        this.clLoc = clLoc;
    }

    /**
     * @return the clCh
     */
    public int getClCh() {
        return clCh;
    }

    /**
     * @param clCh the clCh to set
     */
    public void setClCh(int clCh) {
        this.clCh = clCh;
    }

    public int getMedico() {
        return medico;
    }

    public void setMedico(int medico) {
        this.medico = medico;
    }

}
