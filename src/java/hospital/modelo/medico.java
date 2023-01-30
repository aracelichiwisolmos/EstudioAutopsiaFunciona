package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;

public class medico {

    private int nIdMedico;
    private String sGrado;
    private String sArea;
    private String sCategoria;
    private String sEspecialidad;
    //--------
    private String ch;
    private String ins;
    private String mun;
    private String loc;
    //---------
    private int nControl;
    private AccesoDatos oAD;
    private String sQuery;
    ArrayList resultado = null;

    public medico() {
        this.nIdMedico = 0;
        this.sGrado = "";
        this.sArea = "";
        this.sCategoria = "";
        this.sEspecialidad = "";
        this.ch = "";
        this.nControl = 0;

    }

    public List<medico> buscaTodos() throws Exception {
        List<medico> lista = null;

        medico oMedico;

        ArrayList vRowTemp;

        try {
            sQuery = "SELECT * From datostodos();";
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
                    oMedico = new medico();

                    oMedico.setIdMedico(((Double) vRowTemp.get(0)).intValue());
                    oMedico.setControl(((Double) vRowTemp.get(5)).intValue());
                    oMedico.setGrado((String) vRowTemp.get(6));
                    oMedico.setArea((String) vRowTemp.get(7));
                    oMedico.setCategoria((String) vRowTemp.get(8));
                    oMedico.setEspecialidad((String) vRowTemp.get(9));
                    oMedico.setCh((String) vRowTemp.get(14));
                    oMedico.setIns((String) vRowTemp.get(15));
                    oMedico.setMun((String) vRowTemp.get(16));
                    oMedico.setLoc((String) vRowTemp.get(17));
                    // oMedico.setCh((String) vRowTemp.get(10));
                    lista.add(oMedico);
                    //System.out.println("Lista medico" + lista);
                }

            }
        } catch (Exception e) {
            throw e;
        }
        //  System.out.println("Lista medico" + lista);
        return lista;

    }

    public int cantEncuestas() throws Exception {
        int cantidad = -1;
        ArrayList vRowTemp;

        try {
            sQuery = "SELECT count(*) From medicostodos();";
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
                cantidad = (((Double) vRowTemp.get(0)).intValue());
            }
        } catch (Exception e) {
            throw e;
        }
        return cantidad;

    }

    public List<medico> buscaNoControl(int ncontrol) throws Exception {
        List<medico> lista = null;

        medico oMedico;

        ArrayList vRowTemp;

        try {
            sQuery = "  select * from medicoxnocontrol2(" + ncontrol + ");";
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
                    oMedico = new medico();
                    oMedico.setIdMedico(((Double) vRowTemp.get(0)).intValue());
                    oMedico.setControl(((Double) vRowTemp.get(5)).intValue());
                    oMedico.setGrado((String) vRowTemp.get(6));
                    oMedico.setArea((String) vRowTemp.get(7));
                    oMedico.setCategoria((String) vRowTemp.get(8));
                    oMedico.setEspecialidad((String) vRowTemp.get(9));
                    oMedico.setCh((String) vRowTemp.get(14));
                    oMedico.setIns((String) vRowTemp.get(15));
                    oMedico.setMun((String) vRowTemp.get(16));
                    oMedico.setLoc((String) vRowTemp.get(17));
                    // oIu.setCentroHospitalario((String) vRowTemp.get(10));
                    // oMedico.setCh((String) vRowTemp.get(10));
                    lista.add(oMedico);
                }

            }
        } catch (Exception e) {
            throw e;
        }

        return lista;

    }

    public int getIdMedico() {
        return nIdMedico;
    }

    public void setIdMedico(int nIdMedico) {
        this.nIdMedico = nIdMedico;
    }

    public String getGrado() {
        return sGrado;
    }

    public void setGrado(String sGrado) {
        this.sGrado = sGrado;
    }

    public String getArea() {
        return sArea;
    }

    public void setArea(String sArea) {
        this.sArea = sArea;
    }

    public String getCategoria() {
        return sCategoria;
    }

    public void setCategoria(String sCategoria) {
        this.sCategoria = sCategoria;
    }

    public String getEspecialidad() {
        return sEspecialidad;
    }

    public void setEspecialidad(String sEspecialidad) {
        this.sEspecialidad = sEspecialidad;
    }

    public int getControl() {
        return nControl;
    }

    public void setControl(int nControl) {
        this.nControl = nControl;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getIns() {
        return ins;
    }

    public void setIns(String ins) {
        this.ins = ins;
    }

    public String getMun() {
        return mun;
    }

    public void setMun(String mun) {
        this.mun = mun;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

}
