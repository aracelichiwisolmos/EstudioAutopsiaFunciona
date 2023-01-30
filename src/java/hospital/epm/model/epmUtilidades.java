package hospital.epm.model;

import hospital.controlador.configuracionAppControler;
import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import framework.GUI.GUI;
import framework.GUI.Model;
import framework.exceptions.IllegalActionException;
import framework.utils.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import weka.experiment.InstanceQuery;

public class epmUtilidades {

    private String sUrl = null;
    private String sUsr = null;
    private String sPwd = null;
    private String sDriver = null;
    private configuracionAppControler config = null;

    private AccesoDatos oAD;
    private String actual_fully_qualified_class;
    private String algorithm;
    private List<String> algorithms;
    private String confianza;
    private String reglas;
    private List<Reglas> reglasConf;

    private ArrayList resultado;
    private String resultados;
    private String sQuery;

    Object im;
    String todo;

    public epmUtilidades() {
        this.algorithms = new ArrayList<>();
        this.reglasConf = new ArrayList<>();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
        sUrl = params.get("urldb");
        sUsr = params.get("usrdb");
        sPwd = params.get("pwddb");
        sDriver = params.get("driverdb");
        config = new configuracionAppControler();

    }

    public Instance devuelveInstancias(String sQuery) throws Exception {
        Instance data = null;
        InstanceQuery query = new InstanceQuery();
        query.setDatabaseURL(sUrl);
        query.setUsername(sUsr);
        query.setPassword(sPwd);
        query.setQuery(sQuery);
        query.connectToDatabase();
        //  data = query.retrieveInstances();
        query.disconnectFromDatabase();
//        System.out.println("data" + data);
        return data;

    }
//--------------------------------------Todo iEPMiner------------------------------------------------

    public void crearModeloDiEPMiner(int clase, String query) throws Exception {
        //System.out.println("Entro a crear modelo");
        FileWriter fichero = null;
        PrintWriter pw = null;
        switch (clase) {
            case 1: //Frm_sol_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Frm_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Per_sol_aut, Med_aut \n"
                        + "@outputs Fmr_sol_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet + "\n");

                    for (int x = 0; x < resultado.size(); x++) {

                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 2://med_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Med_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet1 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Per_sol_aut, Fmr_sol_aut \n"
                        + "@outputs Med_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet1 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        //  System.out.println(modelo3);
                        pw.println(modelo3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;

            case 3://per_sol_aut

                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Per_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet2 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Fmr_sol_aut, Med_aut\n"
                        + "@outputs Per_sol_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet2 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;

            case 4://no_hosp
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\No_hosp\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet3 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, Fmr_sol_aut, Med_aut, Per_sol_aut\n"
                        + "@outputs No_hosp\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet3 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;

            case 5://rechazo_fam
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Rechazo_fam\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet4 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp\n"
                        + "@outputs Rechazo_fam\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet4 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        //im = resultado.spliterator();
                        //  if(im.toString().equalsIgnoreCase("[")){
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 6://Mcc_no_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_no_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet5 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z,17z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp, Rechazo_fam\n"
                        + "@outputs Mcc_no_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet5 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 7://mcc_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_aut\\modelo.dat");
                pw = new PrintWriter(fichero);

                String dataSet6 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp, Rechazo_fam, Mcc_no_aut\n"
                        + "@outputs Mcc_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet6 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        //System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
        }

    }

    public void crearModeloCiEPMiner(int clase, String query) throws Exception {
        System.out.println("Entro a crear modelo C iEPMiner");

        FileWriter fichero = null;
        PrintWriter pw = null;

        switch (clase) {
            case 1: //Frm_sol_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Frm_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }

//  //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 2://med_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Med_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }

//  //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;
            case 3://per_sol_aut

                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Per_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute P" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;

            case 4://no_hosp
                fichero = new FileWriter("DC:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\No_hosp\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute P" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;

            case 5://rechazo_fam
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Rechazo_fam\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute P" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;
            case 6://Mcc_no_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_no_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute P" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;
            case 7://mcc_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute P" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;
        }

    }

    //public void obtenerModeloiEPMiner(String rutaTra, double minSoporte, double growthRate, double chiSquaret) throws Exception {
    public List<Reglas> obtenerModeloiEPMiner(String rutaTra, double minSoporte, double growthRate, double chiSquaret) throws Exception {
        System.out.println("Obtener modelo iEPMiner");
        ArrayList<Reglas> reglas = new ArrayList<>();

        try {

            //la API para obtener instancias de documentos DOM de un documento XML.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //System.out.println("2");
            //El parse()método analiza el archivo XML en un Document.
            org.w3c.dom.Document documen = builder.parse(new File("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\epm-framework-master\\algorithms.xml"));
            NodeList nodes = documen.getElementsByTagName("algorithm");
            for (int j = 0; j < nodes.getLength(); j++) {
                Node raiz2 = nodes.item(j);
                Element segundo = (Element) raiz2;
                //System.out.println("3");
                //agrega la lista con los nombres de los algoritmos
                algorithms.add(segundo.getElementsByTagName("name").item(0).getTextContent());
                //System.out.println("4" + algorithms);
                // soporte
                //String u = segundo.getElementsByTagName("default").item(0).getTextContent();
                // parametros.add(u);
                //System.out.println("5");
                // Element node = (Element) nodes.item(index);
                actual_fully_qualified_class = segundo.getElementsByTagName("class").item(0).getTextContent();
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Reading parameters and files...");

        HashMap<String, String> paramsiEPMiner = new HashMap<>();

        InstanceSet training = new InstanceSet();
//                                    InstanceSet test = new InstanceSet();
        //llena el hashmap de paramiEPMiner
        // paramsiEPMiner.put("", String.valueOf(cantReglas));
        paramsiEPMiner.put("Minimum Support", String.valueOf(minSoporte));
        paramsiEPMiner.put("Minimum Growth Rate", String.valueOf(growthRate));
        paramsiEPMiner.put("Minimum Chi-Squared", String.valueOf(chiSquaret));
        System.out.println(" " + minSoporte + " " + " " + growthRate + " " + chiSquaret);
        // Dinamically calls the method learn of the method: VERY INTERESTING FUNCTION!
        try {

            if (rutaTra.equals("")) {
                throw new framework.exceptions.IllegalActionException("ERROR: You must specify a training file.");
            }

            //Ejecute la tarea en segundo plano para actualizar el área de texto
            //SwingWorker worker;
            //worker = new SwingWorker() {
            // @Override
            //  protected Object doInBackground() throws Exception {
            Attributes.clearAll();
            try {
                training.readSet(rutaTra, true);
            } catch (DatasetException | HeaderFormatException | NullPointerException ex) {
                throw new IllegalActionException("ERROR: Format error on training file.");
            }
            training.setAttributesAsNonStatic();

            // System.out.println("Executing " + (String) algorithms.get(0));
            System.out.println("Executing " + algorithms);
            // //Primero: instanciar la clase seleccionada con el nombre completo
            // System.out.println("paso 1");
            Object newObject;
            // System.out.println("paso 2");

            Class clase = Class.forName(actual_fully_qualified_class);
            //System.out.println("paso 3" + clase);
            newObject = clase.newInstance();
            //System.out.println("paso 4");
            //Primero: instanciar la clase seleccionada con el nombre completo
            ((Model) newObject).patterns = new ArrayList<>();
            //System.out.println("paso 5");
            ((Model) newObject).patternsFilteredMinimal = new ArrayList<>();
            ((Model) newObject).patternsFilteredMaximal = new ArrayList<>();
            ((Model) newObject).patternsFilteredByMeasure = new ArrayList<>();
            //  System.out.println("paso 6");
            // Segundo:obtener los argumentos de la clase
            Class[] args = new Class[2];
            args[0] = InstanceSet.class;
            args[1] = HashMap.class;

            System.out.println("Learning Model...");
            // Tercero: Obtén el método 'learn' de la clase e invocalo. (cambiar "new InstanceSet" por el training)
            clase.getMethod("learn", args).invoke(newObject, training, paramsiEPMiner);

            System.out.println("Filtering patterns and calculating descriptive measures...");

            // Obtiene patrones aprendidos, filtra y calcula medidas para el entrenamiento
            ArrayList<HashMap<String, Double>> Measures = Utils.calculateDescriptiveMeasures(training, ((Model) newObject).getPatterns(), true);
            //Filtre los patrones, devolviendo las medidas de calidad promedio para cada conjunto de patrones

            ArrayList<HashMap<String, Double>> filterPatterns = Utils.filterPatterns((Model) newObject, "CONF", 0.6f);
            for (int i = 0; i < filterPatterns.size(); i++) {
                // // Agrega a Masures para escribir más tarde los resultados promedio en el archivo.
                Measures.add(filterPatterns.get(i));
            }

            System.out.println("Calculating precision for training...");
            args = new Class[1];
            args[0] = InstanceSet.class;
            String[][] predictionsTra = (String[][]) clase.getMethod("predict", args).invoke(newObject, training);
            Utils.calculatePrecisionMeasures(predictionsTra, training, training, Measures);

            // Guardar medidas de entrenamiento en un archivo.
            System.out.println("Save results in a file...");

            Utils.saveMeasures(new File(rutaTra).getParentFile(), (Model) newObject, Measures, true, 0);

            System.out.println("Done learning model.");
            //--------------------------------------------------

            //obtiene las reglas y la confianza
            for (int i = 0; i < ((Model) newObject).getPatterns().size(); i++) {
                Reglas oRegla = new Reglas();
                oRegla.setIdRegla(i + 1);
                oRegla.setRegla(((Model) newObject).getPatterns().get(i).toString());
                String o = String.valueOf(((Model) newObject).getPatterns().get(i).getTraMeasure("CONF"));
                double confi = Double.parseDouble(o);
                double p = Math.round(confi * 100);
                String confianza = String.valueOf(p);
                oRegla.setCONF(confianza);
                reglas.add(oRegla);
                //System.out.println("Reglas..............." + oRegla.getCONF() + " " + oRegla.getRegla());
            }

        } catch (IllegalActionException ex) {
            System.out.println(ex.getReason());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return reglas;
    }
    //------------------------Aquí termina todo de iEPMiner-------

    //--------------------------Aquí empieza la obtencion de reglas------------------------
//Este método recibe la lista de reglas en donde se encuentran las reglas y su confianza, para poder interpretarlas
    public List<String> obtenerReglasC1(List<Reglas> reglasList) throws Exception {
        ArrayList<String> interpretaciones = new ArrayList<>();

        try {
            System.out.println("Entro a en Reglas C---");

            for (Reglas r : reglasList) {
                Reglas t = new Reglas();
                String interpretacion = "Si los médicos ";
                //String linea = ran.readLine();
                String predicados = r.getRegla().substring(r.getRegla().indexOf("IF") + 2, r.getRegla().indexOf("THEN"));
                // System.out.println("predicados" + predicados);// imprime a partir de p
                //los divide cuando encuentra un AND
                String[] e = predicados.split("AND");

                for (int i = 0; i < e.length; i++) {
                    //los divide cuando encuentran un =
                    String[] g = e[i].split("=");
                    // System.out.println("" + g);
                    AccesoDatos acc = new AccesoDatos();
                    ArrayList arr = null;
                    if (acc.conectar()) {
                        String q = "select antecedente from interpretacion where descc_columna='" + g[0].trim().toLowerCase() + "' and valor_atributo='" + g[1].trim().toUpperCase() + "'";

                        arr = acc.ejecutarConsulta(q);
                        acc.desconectar();
                    }
                    if (arr != null && !arr.isEmpty()) {
                        interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";

                    }

                }
                String entonces = r.getRegla().substring(r.getRegla().indexOf("THEN") + 4);
                if (interpretacion.length() > 3) {
                    interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                }
                interpretacion += ", entonces " + "son del hospital " + entonces;

                interpretaciones.add(interpretacion + ". \n Esta regla tiene una confianza de:" + r.getCONF() + "%");

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return interpretaciones;
    }

    public List<String> obtenerReglasD1(List<Reglas> reglasList) throws Exception {
        ArrayList<String> interpretaciones = new ArrayList<>();

        try {
            //  System.out.println("Entro a en Reglas D---");

            for (Reglas r : reglasList) {
                String interpretacion = "Si los médicos ";
                //String linea = ran.readLine();
                String predicados = r.getRegla().substring(r.getRegla().indexOf("IF") + 2, r.getRegla().indexOf("THEN"));
                // System.out.println("predicados" + predicados);// imprime a partir de p
                //los divide cuando encuentra un AND
                String[] e = predicados.split("AND");
                for (int i = 0; i < e.length; i++) {
                    //los divide cuando encuentran un =
                    String[] g = e[i].split("=");
                    // System.out.println("" + g);
                    AccesoDatos acc = new AccesoDatos();
                    ArrayList arr = null;
                    if (acc.conectar()) {
                        String q = "select antecedente from interpretacion where descc_columna='" + g[0].trim().toLowerCase() + "' and valor_atributo='" + g[1].trim().toLowerCase() + "'";

                        arr = acc.ejecutarConsulta(q);
                        acc.desconectar();
                    }
                    if (arr != null && !arr.isEmpty()) {
                        interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";

                    }

                }
                String entonces = r.getRegla().substring(r.getRegla().indexOf("THEN") + 4);
                AccesoDatos acc = new AccesoDatos();
                ArrayList a = null;
                if (acc.conectar()) {
                    String q = "select antecedente from interpretacion where atributo='" + entonces.trim() + "'";
                    a = acc.ejecutarConsulta(q);
                    acc.desconectar();
                }
                if (interpretacion.length() > 3) {
                    interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                }
                if (a != null && !a.isEmpty()) {
                    interpretacion += ", entonces " + ((ArrayList) a.get(0)).get(0);
                }
                interpretaciones.add(interpretacion + ". \n Esta regla tiene una confianza de:" + r.getCONF() + "%");

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return interpretaciones;
    }

    public ArrayList<String> obtenerReglasD(String pathModelo, List<Confianza> confianzaList) {
        ArrayList<String> interpretaciones = new ArrayList<>();
        Confianza conf = new Confianza();
        try {
            System.out.println("Entro a c y iEPMiner en Reglas ---" + pathModelo);

            RandomAccessFile ran = new RandomAccessFile(pathModelo, "rw");
            // System.out.println(ran);
            //System.out.println("");
            //----
            FileWriter fichero = null;
            PrintWriter pw = null;
            //-----
            try {
                //_-------
                fichero = new FileWriter("C:/Users/Araceli/Desktop/MAESTRÍA/EstudioAutopsiaFunciona/corpus/reglas.doc");
                pw = new PrintWriter(fichero);
                pw.println("____________________________Reglas obtenidas__________________________" + "\n\n");
                //----------
                int y = 0;
                ran.seek(0);
                while (ran.getFilePointer() < ran.length()) {
                    String interpretacion = "Si los médicos ";
                    String linea = ran.readLine();
                    String predicados = linea.substring(linea.indexOf("IF") + 2, linea.indexOf("THEN"));

                    String[] e = predicados.split("AND");
                    for (int i = 0; i < e.length; i++) {
                        String[] g = e[i].split("=");

                        AccesoDatos acc = new AccesoDatos();
                        ArrayList arr = null;
                        if (acc.conectar()) {
                            String q = "select antecedente from interpretacion where descc_columna='" + g[0].trim().toLowerCase() + "' and valor_atributo='" + g[1].trim().toLowerCase() + "'";
                            arr = acc.ejecutarConsulta(q);
                            acc.desconectar();
                        }
                        if (arr != null && !arr.isEmpty()) {
                            interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";
                        }
                    }
                    String entonces = linea.substring(linea.indexOf("THEN") + 4);
                    AccesoDatos acc = new AccesoDatos();
                    ArrayList a = null;
                    if (acc.conectar()) {
                        String q = "select antecedente from interpretacion where atributo='" + entonces.trim() + "'";
                        a = acc.ejecutarConsulta(q);
                        acc.desconectar();
                    }
                    if (interpretacion.length() > 3) {
                        interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                    }
                    if (a != null && !a.isEmpty()) {
                        interpretacion += ", entonces " + ((ArrayList) a.get(0)).get(0);
                    }

                    interpretaciones.add(interpretacion);
//
//                    for (int o = 0; o < interpretaciones.size(); o++) {
//                        y = o;
//                    }
//                    pw.println("Regla" + y + " :" + interpretacion + "\n" + confianza);
                }
                for (int i = 0; i < confianzaList.size(); i++) {

                    conf.setCONF(confianzaList.get(i).getCONF());
                    double confi = Double.parseDouble(conf.getCONF());
                    double p = Math.round(confi / 100);
                    String confianza = String.valueOf(p);
                    interpretaciones.set(i, interpretaciones.get(i) + ". \n Esta regla tiene una confianza de: " + confianza + "%");

                }

            } catch (FileNotFoundException e) {
                try {

                    // Nuevamente aprovechamos el finally para
                    // asegurarnos que se cierra el fichero.
                    if (null != fichero) {
                        fichero.close();
                    }
                } catch (IOException e2) {
                }

            } catch (Exception ex) {
            }

        } catch (FileNotFoundException ex) {
        }
        return interpretaciones;
    }
//recibe el path en donde se encuentra el archivo con las reglas sin interpretar y las lista de confianza, para poder interpretarlas

    public List<String> obtenerReglasC(String pathModelo, List<Confianza> confianzaList) throws Exception {
        ArrayList<String> interpretaciones = new ArrayList<>();
        Confianza conf = new Confianza();

        try {
            //System.out.println("Entro a en Reglas ---" + pathModelo);

            RandomAccessFile ran = new RandomAccessFile(pathModelo, "rw");
//            System.out.println(ran);
//            System.out.println("");
            FileWriter fichero = null;
            PrintWriter pw = null;

            try {
                //_-------
                fichero = new FileWriter("C:/Users/Araceli/Desktop/MAESTRÍA/EstudioAutopsiaFunciona/corpus/reglas.doc");
                pw = new PrintWriter(fichero);
                //----------
                int y = 0;

                ran.seek(0);
                pw.println("____________________________Reglas obtenidas__________________________" + "\n\n");
                while (ran.getFilePointer() < ran.length()) {
                    String interpretacion = "Si los médicos ";
                    String linea = ran.readLine();
                    String predicados = linea.substring(linea.indexOf("IF") + 2, linea.indexOf("THEN"));

                    // System.out.println("predicados" + predicados);// imprime a partir de p
                    //los divide cuando encuentra un AND
                    String[] e = predicados.split("AND");

                    for (int i = 0; i < e.length; i++) {

                        //los divide cuando encuentran un =
                        String[] g = e[i].split("=");
                        // System.out.println("" + g);

                        AccesoDatos acc = new AccesoDatos();
                        ArrayList arr = null;
                        if (acc.conectar()) {
                            String q = "select antecedente from interpretacion where descc_columna='" + g[0].trim().toLowerCase() + "' and valor_atributo='" + g[1].trim().toUpperCase() + "'";

                            arr = acc.ejecutarConsulta(q);
                            acc.desconectar();
                        }
                        if (arr != null && !arr.isEmpty()) {
                            interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";

                        }

                    }

                    String entonces = linea.substring(linea.indexOf("THEN") + 4);
                    if (interpretacion.length() > 3) {
                        interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                    }
                    interpretacion += ", entonces " + "son del hospital " + entonces;
                    interpretaciones.add(interpretacion);

                }
                for (int i = 0; i < confianzaList.size(); i++) {

                    conf.setCONF(confianzaList.get(i).getCONF());
                    double confi = Double.parseDouble(conf.getCONF());
                    double p = Math.round(confi / 100);
                    String confianza = String.valueOf(p);
                    interpretaciones.set(i, interpretaciones.get(i) + ". \n Esta regla tiene una confianza de: " + confianza + "%");
                    pw.println("Regla" + i + 1 + " :" + interpretaciones.get(i) + ". \n Esta regla tiene una confianza de: " + confianza + "%");

                }

            } catch (FileNotFoundException e) {
                try {

                    // Nuevamente aprovechamos el finally para
                    // asegurarnos que se cierra el fichero.
                    if (null != fichero) {
                        fichero.close();
                    }
                } catch (IOException e2) {
                }

            } catch (Exception ex) {
            }

        } catch (FileNotFoundException ex) {
        }
        return interpretaciones;
    }
    //------------------------Aquí termina todo de reglas-----------------------

    //-----------------------Aquí inicia todo lo de DGCP-Tree
    // public void obtenerModeloDGCP(String rutaTra, double minSoporte) throws Exception {
    public List<Reglas> obtenerModeloDGCP(String rutaTra, double minSoporte) throws Exception {
        ArrayList<Reglas> reglas = new ArrayList<>();
        try {
            System.out.println("Entro  a DGCP con C y Fmr");

            // se obtiene el DocumentBuilder DocumentBuilderFactory contiene
            //la API para obtener instancias de documentos DOM de un documento XML.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //El parse()método analiza el archivo XML en un Document.
            org.w3c.dom.Document documen = builder.parse(new File("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\epm-framework-master\\algorithmsDGCP.xml"));
            NodeList nodes = documen.getElementsByTagName("algorithm");
            for (int j = 0; j < nodes.getLength(); j++) {
                Node raiz2 = nodes.item(j);
                Element segundo = (Element) raiz2;
                //agrega la lista con los nombres de los algoritmos
                algorithms.add(segundo.getElementsByTagName("name").item(0).getTextContent());
                // soporte
                String u = segundo.getElementsByTagName("default").item(0).getTextContent();
                // parametros.add(u);

                // Element node = (Element) nodes.item(index);
                actual_fully_qualified_class = segundo.getElementsByTagName("class").item(0).getTextContent();
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Reading parameters and files...");

        HashMap<String, String> paramsDGCP = new HashMap<>();

        InstanceSet training2 = new InstanceSet();
//                                    InstanceSet test2 = new InstanceSet();
        //llena el hashmap de DGCP-Tree
        paramsDGCP.put("Min Support", String.valueOf(minSoporte));
        System.out.println("Datos: " + minSoporte);

        // Dinamically calls the method learn of the method: VERY INTERESTING FUNCTION!
        try {

            if (rutaTra.equals("")) {
                throw new framework.exceptions.IllegalActionException("ERROR: You must specify a training file.");
            }

            //Ejecute la tarea en segundo plano para actualizar el área de texto
//
            Attributes.clearAll();
            try {
                training2.readSet(rutaTra, true);
            } catch (DatasetException | HeaderFormatException | NullPointerException ex) {
                throw new IllegalActionException("ERROR: Format error on training file.");
            }
            training2.setAttributesAsNonStatic();

            // System.out.println("Executing " + (String) algorithms.get(0));
            System.out.println("Executing " + algorithms);
            // //Primero: instanciar la clase seleccionada con el nombre completo
            // System.out.println("paso 1");
            Object newObject;
            // System.out.println("paso 2");

            Class clase = Class.forName(actual_fully_qualified_class);
            //System.out.println("paso 3" + clase);
            newObject = clase.newInstance();
            //System.out.println("paso 4");
            //Primero: instanciar la clase seleccionada con el nombre completo
            ((Model) newObject).patterns = new ArrayList<>();
            //System.out.println("paso 5");
            ((Model) newObject).patternsFilteredMinimal = new ArrayList<>();
            ((Model) newObject).patternsFilteredMaximal = new ArrayList<>();
            ((Model) newObject).patternsFilteredByMeasure = new ArrayList<>();
            //  System.out.println("paso 6");
            // Segundo:obtener los argumentos de la clase
            Class[] args = new Class[2];
            args[0] = InstanceSet.class;
            args[1] = HashMap.class;

            System.out.println("Learning Model...");
            // Tercero: Obtén el método 'learn' de la clase e invocalo. (cambiar "new InstanceSet" por el training)
            clase.getMethod("learn", args).invoke(newObject, training2, paramsDGCP);

            System.out.println("Filtering patterns and calculating descriptive measures...");

            // Obtiene patrones aprendidos, filtra y calcula medidas para el entrenamiento
            ArrayList<HashMap<String, Double>> Measures = Utils.calculateDescriptiveMeasures(training2, ((Model) newObject).getPatterns(), true);
            //Filtre los patrones, devolviendo las medidas de calidad promedio para cada conjunto de patrones

            ArrayList<HashMap<String, Double>> filterPatterns = Utils.filterPatterns((Model) newObject, "CONF", 0.6f);
            for (int i = 0; i < filterPatterns.size(); i++) {
                // // Agrega a Masures para escribir más tarde los resultados promedio en el archivo.
                Measures.add(filterPatterns.get(i));
            }

            System.out.println("Calculating precision for training...");
            args = new Class[1];
            args[0] = InstanceSet.class;
            String[][] predictionsTra = (String[][]) clase.getMethod("predict", args).invoke(newObject, training2);
            Utils.calculatePrecisionMeasures(predictionsTra, training2, training2, Measures);

            // Guardar medidas de entrenamiento en un archivo.
            System.out.println("Save results in a file...");

            Utils.saveMeasures(new File(rutaTra).getParentFile(), (Model) newObject, Measures, true, 0);

            System.out.println("Done learning model.");

            //obtiene las reglas y la confianza
            for (int i = 0; i < ((Model) newObject).getPatterns().size(); i++) {
                Reglas oRegla = new Reglas();
                oRegla.setIdRegla(i + 1);
                oRegla.setRegla(((Model) newObject).getPatterns().get(i).toString());
                String o = String.valueOf(((Model) newObject).getPatterns().get(i).getTraMeasure("CONF"));
                double confi = Double.parseDouble(o);
                double p = Math.round(confi * 100);
                String confianza = String.valueOf(p);
                oRegla.setCONF(confianza);
                reglas.add(oRegla);
                //System.out.println("Reglas..............." + oRegla.getCONF() + " " + oRegla.getRegla());
            }
//
        } catch (IllegalActionException ex) {
            System.out.println(ex.getReason());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return reglas;
    }

    public void crearModeloDDGCP(int clase, String query) throws Exception {
        System.out.println("Entro a crear modelo");
        FileWriter fichero = null;
        PrintWriter pw = null;
        switch (clase) {
            case 1: //Frm_sol_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Frm_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Per_sol_aut, Med_aut \n"
                        + "@outputs Fmr_sol_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet + "\n");
                    for (int x = 0; x < resultado.size(); x++) {

                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");
                        // System.out.println(modelo3);
                        pw.println(modelo3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 2://med_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Med_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet1 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Per_sol_aut, Fmr_sol_aut \n"
                        + "@outputs Med_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet1 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 3://per_sol_aut

                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Per_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet2 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, No_hosp, Fmr_sol_aut, Med_aut\n"
                        + "@outputs Per_sol_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet2 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;

            case 4://no_hosp
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\No_hosp\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet3 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Rechazo_fam, Fmr_sol_aut, Med_aut, Per_sol_aut\n"
                        + "@outputs No_hosp\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet3 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        //im = resultado.spliterator();
                        //  if(im.toString().equalsIgnoreCase("[")){
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                break;

            case 5://rechazo_fam
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Rechazo_fam\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet4 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Mcc_no_aut, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp\n"
                        + "@outputs Rechazo_fam\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet4 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        // System.out.println(modelo3);
                        pw.println(modelo3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 6://Mcc_no_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_no_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                String dataSet5 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Mcc_aut, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp, Rechazo_fam\n"
                        + "@outputs Mcc_no_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet5 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        //System.out.println(modelo3);
                        pw.println(modelo3);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 7://mcc_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_aut\\modelo.dat");
                pw = new PrintWriter(fichero);

                String dataSet6 = "@relation encuesta\n"
                        + "\n"
                        + "@attribute Area {a1,a2,a3}\n"
                        + "@attribute Categoria {c1,c2,c3,c4,c5}\n"
                        + "@attribute Ult_grado {g1,g2,g7,g8}\n"
                        + "@attribute Esc_med_gral {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Esc_esp {c1,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c25,c26,c27,c28,c29,c30,c31,c32,c33,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56}\n"
                        + "@attribute Anios_prac {3e,3c,3a,3d,3b}\n"
                        + "@attribute Casos {4a,4c,4e,4b,4d}\n"
                        + "@attribute Hall_disc {7c,7a,7d,7e,7b}\n"
                        + "@attribute Hall_arb {8b,8c,8a,8e,8d}\n"
                        + "@attribute Hall_dem {11a,11b,11c,11d,11e,11z}\n"
                        + "@attribute Fmr_sol_aut {21a,21b,21c,21d,21e,21f,21j,21z}\n"
                        + "@attribute Med_aut {20a,20b,20c,20d,20e,20f,20g,20h,20i,20j,20z}\n"
                        + "@attribute Per_sol_aut {19a,19b,19c,19d,19e,19f,19z}\n"
                        + "@attribute No_hosp {18a,18b,18c,18d,18e,18f,18g,18h,18z}\n"
                        + "@attribute Rechazo_fam {17a,17b,17c,17d,17e,17f,17g,17z}\n"
                        + "@attribute Mcc_no_aut {16a,16b,16c,16d,16e,16f,16g,16h,16i,16j,16k,16l,16m,16n,16ñ,16o,16p,16q,16r,16s,16t,16u,16z}\n"
                        + "@attribute Mcc_aut {14a,14b,14c,14d,14e,14f,14g,14h,14i,14z}\n"
                        + "@inputs Area,Categoria, Ult_grado, Esc_med_gral, Esc_esp, Anios_prac, Casos, Hall_disc, Hall_arb, Hall_dem, Fmr_sol_aut, Med_aut, Per_sol_aut, No_hosp, Rechazo_fam, Mcc_no_aut\n"
                        + "@outputs Mcc_aut\n"
                        + "@data";
                try {
                    sQuery = query;
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
                    pw.print(dataSet6 + "\n");

                    for (int x = 0; x < resultado.size(); x++) {
                        // System.out.println("resull" + resultado);
                        im = resultado.get(x);
                        String modelo = im.toString().replaceAll("\\[", "");
                        String modelo2 = modelo.replaceAll("\\]", "");
                        String modelo3 = modelo2.replaceAll(" ", "");

                        //  System.out.println(modelo3);
                        pw.println(modelo3);

                    }

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
        }

    }

    public void crearModeloCDGCP(int clase, String query) throws Exception {
        System.out.println("Entro a crear modelo");
        FileWriter fichero = null;
        PrintWriter pw = null;
        switch (clase) {
            case 1: //Frm_sol_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Frm_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");
//conjunto 1
                    for (int i = 0; i < 86; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    //conjunto 2
                    //  for (int j = 88; j < res1.size(); j++) {
                    for (int j = 86; j < 93; j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 2://med_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Med_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //  System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        // System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 3://per_sol_aut

                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Per_sol_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        // System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;

            case 4://no_hosp
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\No_hosp\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;

            case 5://rechazo_fam
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Rechazo_fam\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
                    //System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
                        // System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 6://Mcc_no_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_no_aut\\modelo.dat");
                pw = new PrintWriter(fichero);
                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
//                    System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
//                        System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }

                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//
//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
            case 7://mcc_aut
                fichero = new FileWriter("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_aut\\modelo.dat");
                pw = new PrintWriter(fichero);

                try {
                    sQuery = query;

                    ArrayList res1 = null;
                    ArrayList atributos = null;
                    if (oAD == null) {
                        oAD = new AccesoDatos();

                        if (oAD.conectar()) {
                            atributos = oAD.atributos("encuesta", "matriz_binaria");
                            resultado = oAD.ejecutarConsulta(sQuery);
                            res1 = oAD.ejecutarConsulta(sQuery);
                            oAD.desconectar();
                        }
                        oAD = null;
                    } else {
                        atributos = oAD.atributos("encuesta", "matriz_binaria");
                        resultado = oAD.ejecutarConsulta(sQuery);
                        res1 = oAD.ejecutarConsulta(sQuery);
                        oAD.desconectar();
                    }
                    String cabecera = "";
                    if (atributos != null) {
                        for (int u = 1; u < 18; u++) {
                            int y = 0;
                            for (int i = 0; i < atributos.size(); i++) {
                                String[] t = ("" + atributos.get(i)).split(":");
                                if (t[0].contains("p" + u + "_")) {
                                    y++;
                                }
                            }
                            for (int i = 1; i < (y + 1); i++) {
                                cabecera += "@attribute p" + u + "_" + i + " {S,N}\n";
                            }
                        }
                    }
//                    System.out.println(cabecera);
                    String dataSet = "@relation InformaciónEncuestas(84)-weka.filters.unsupervised.attribute.Remove-R116\n\n";
                    dataSet += cabecera;
                    dataSet += "@attribute clase {1,2,3}\n\n@data\n";
                    pw.print(dataSet + "\n");

                    for (int i = 0; i < 88; i++) {
                        ArrayList im2 = (ArrayList) res1.get(i);
                        String modelo6 = "";
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "1");

                    }
                    for (int j = 88; j < res1.size(); j++) {
                        ArrayList im2 = (ArrayList) res1.get(j);
                        String modelo6 = "";
//                        System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "2");
                    }
                    //conjunto 3
                    for (int y = 93; y < res1.size(); y++) {
                        ArrayList im2 = (ArrayList) res1.get(y);
                        String modelo6 = "";
                        //System.out.println(im2.size());
                        for (int p = 0; p < (im2).size(); p++) {

                            modelo6 += "" + ((("" + (im2.get(p))).isEmpty()) ? "?" : "S") + ",";
                        }

                        pw.println(modelo6 + "3");
                    }
//

//
                    //   pw.println(dataSet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Nuevamente aprovechamos el finally para
                        // asegurarnos que se cierra el fichero.
                        if (null != fichero) {
                            fichero.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                break;
        }

    }

    //---------------------Obtiene las reglas sin interpretar
    public List<Reglas> obtenerReglasSI(String pathModelo) throws Exception {
        System.out.println("Entro a obtenerReglas");
        File file = new File(pathModelo);
        ArrayList lista = new ArrayList<>();

        Scanner scanner;

        try {

            scanner = new Scanner(file);
            Reglas e = new Reglas();
            Confianza c = new Confianza();

            while (scanner.hasNextLine()) {
                e = new Reglas();

                String linea = scanner.nextLine();

                String r = linea.substring(linea.indexOf("IF"));
                e.setRegla(r);

                lista.add(e);

            }

            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lista;
    }
//Este método recibe la lista de reglas y de confianza y asigna los valores en la clase Reglas, para poder traer los valores en la vista

    public List<Reglas> describeReglasConfianza(List<Reglas> reglasList, List<Confianza> confList) {
//        for (Reglas a : reglasList) {
//            //  System.out.println(((Reglas) a).getRegla());
//        }
        List lista = new ArrayList<>();

        for (int i = 0; i < confList.size(); i++) {
            Reglas oRegla = new Reglas();
            oRegla.setIdRegla(i + 1);
            oRegla.setRegla(reglasList.get(i).getRegla());
            oRegla.setCONF(confList.get(i).getCONF());
            lista.add(oRegla);

        }
        return lista;
    }

//    public List<Reglas> describeRC(List<String> reglasList) {
//        List TFinal = new ArrayList<>();
//        int o = 1;
//        for (String r : reglasList) {
//            Reglas t = new Reglas();
//            t.setIdRegla(o);
//            t.setRegla(r.substring(r.indexOf("IF"), r.indexOf("Confianza")));
//            t.setCONF("" + (Double.parseDouble(r.substring(r.indexOf("Confianza") + 10, r.length()))) / 10000);
//            TFinal.add(t);
//            o++;
//        }
//        return TFinal;
//    }  Este método recibe el path en donde se encuntra el archivo de confianza y obtiene solo la confinza de cada regla
    public List<Confianza> obtenerConfianza(String pathModelo) throws Exception {
        System.out.println("Entro a obtener confianza");
        File file = new File(pathModelo);
        ArrayList lista = new ArrayList<>();
        Scanner scanner;
        try {
            //se pasa el flujo al objeto scanner
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                // el objeto scanner lee linea a linea desde el archivo
                String linea = scanner.nextLine();
                String linea2 = linea.replaceAll("\\s", ",");
                Scanner confianza = new Scanner(linea2);
                Confianza c = new Confianza();

                confianza.useDelimiter("\\s*,\\s*");

                c.setRULE_NUMBER(confianza.next());
                c.setN_VARS(confianza.next());
                c.setTP(confianza.next());
                c.setTN(confianza.next());
                c.setFP(confianza.next());
                c.setFN(confianza.next());
                c.setACC(confianza.next());
                c.setSUPP(confianza.next());
                c.setFPR(confianza.next());
                c.setGR(confianza.next());
                c.setSUPDIFF(confianza.next());
                c.setNRULES(confianza.next());
                c.setAUC(confianza.next());
                c.setWRACC(confianza.next());
                c.setTPR(confianza.next());
                c.setCONF(confianza.next());

                lista.add(c);
                for (int i = 0; i < lista.size(); i++) {

                    c.setIdConfianza(i);

                }
            }
            lista.remove(0);
            lista.remove(lista.size() - 1);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }

    public String getResultados() {
        return resultados;
    }

    public void setResultados(String resultados) {
        this.resultados = resultados;
    }

    public String getConfianza() {
        return confianza;
    }

    public void setConfianza(String confianza) {
        this.confianza = confianza;
    }

    public String getReglas() {
        return reglas;
    }

    public void setReglas(String reglas) {
        this.reglas = reglas;
    }

    public List<Reglas> getReglasConf() {
        return reglasConf;
    }

    public void setReglasConf(List<Reglas> reglasConf) {
        this.reglasConf = reglasConf;
    }

}
