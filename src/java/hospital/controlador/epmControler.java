package hospital.controlador;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import framework.GUI.GUI;
import hospital.epm.model.epmUtilidades;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import framework.GUI.Model;
import hospital.epm.model.Reglas;
import hospital.epm.model.Confianza;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ManagedBean(name = "oEPMJB")
@ViewScoped
public class epmControler extends Model {

    private epmUtilidades oEPMUtilidades;
    private configuracionAppControler oConfApp;
    private int modeloSeleccionado;
    private int claseSeleccionada;
    private String corpusSeleccionado;
    //Parametros
    private double minConfianza;
    private double minSoporte;
    private double growthRate;
    private double chiSquaret;
    //Listas
    private String algorithm;
    private List<String> algorithms;
    private List<String> reglas;
    private List<Reglas> reglas2;
    private List<String> reglasPDFA;
    private List<Confianza> confianzaAll;

    private List<Reglas> confianzaReglas;

    private String pathModelo = "";

    @SuppressWarnings("empty-statement")
    public epmControler() throws Exception {
        this.oEPMUtilidades = new epmUtilidades();
        this.oConfApp = new configuracionAppControler();
        this.modeloSeleccionado = 1;
        this.corpusSeleccionado = null;

        this.minConfianza = 0.6;
        this.minSoporte = 0.01;
        this.growthRate = 2;
        this.chiSquaret = 3.84;

        this.algorithms = new ArrayList<>();
        this.algorithm = null;
        this.reglas = new ArrayList<>();
        this.reglas2 = new ArrayList<>();
        this.reglasPDFA = new ArrayList<>();
        this.confianzaAll = new ArrayList<>();
        this.confianzaReglas = new ArrayList<>();

    }

    public epmUtilidades getoEPMUtilidades() {
        return oEPMUtilidades;
    }

    //Crea el modelo que se enviara al framework
    public void guardarModelo(int modeloSeleccionado) throws Exception {
        this.modeloSeleccionado = modeloSeleccionado;
        //System.out.println("modelo" + modeloSeleccionado + "clase" + claseSeleccionada);
        try {
//Inicia switch para la clase seleccionada
            switch (claseSeleccionada) {   
                case 1:// Fmr_sol_aut
//querys
                    String query_CFmr = "SELECT * FROM matriz_binaria;";

                    String query_DFmr = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac, casos, hall_disc,"
                            + " hall_arb, hall_dem, mcc_aut, mcc_no_aut,"
                            + " rechazo_fam, no_hosp, per_sol_aut,med_aut, fmr_sol_aut\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan para el conjunto c
                        case "c":
                            System.out.println("Entro a C con Fmr");
                            //Envia la clase seleccionada y la query, en este caso es la clase Fmr_sol_aut
                            oEPMUtilidades.crearModeloCiEPMiner(1, query_CFmr);
                            oEPMUtilidades.crearModeloCDGCP(1, query_CFmr);
                            //ruta en donde se encuentra el modelo
                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Frm_sol_aut\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Frm_sol_aut\\modelo.dat";

                            switch (modeloSeleccionado) { //algoritmo

                                case 1://iEPMiner - envia los parametros y el modelo.dat al metodo que trabjara con el framework
                                    System.out.println("Entro iEPMiner");
                                  //---enviar con los parametros requeridos ****************
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                                case 2: //DGCP - envia los parametros y el modelo.dat al metodo

                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    
                                   
                                    break;
                                    
                            }

                            break;
                        case "d":
                            System.out.println("Entro a d ");

                            oEPMUtilidades.crearModeloDiEPMiner(1, query_DFmr);
                            oEPMUtilidades.crearModeloDDGCP(1, query_DFmr);

                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Frm_sol_aut\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Frm_sol_aut\\modelo.dat";

                            System.out.println("Entro en D");
                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;

                            }
                            break;
                    }
                    break;

                case 2: //med_aut

                    System.out.println("Entro a med_aut");

                    String query_CMed = "SELECT * FROM matriz_binaria;";
                    String query_DMed = "SELECT area, categoria, ult_grado, esc_med_gral, "
                            + "esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem,"
                            + " mcc_aut, mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, fmr_sol_aut, med_aut\n"
                            + "FROM vista_minable; ";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan

                        case "c":
                            System.out.println("Entro a c");
                            oEPMUtilidades.crearModeloCiEPMiner(2, query_CMed);
                            oEPMUtilidades.crearModeloCDGCP(2, query_CMed);

                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Med_aut\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Med_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Entro a d");
                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Med_aut\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Med_aut\\modelo.dat";
                            oEPMUtilidades.crearModeloDiEPMiner(2, query_DMed);
                            oEPMUtilidades.crearModeloDDGCP(2, query_DMed);

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;

                            }
                            break;
                    }
                    break;
                case 3: //Per_sol_aut

                    String query_CPer = "SELECT * FROM matriz_binaria;";
                    String query_DPer = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, "
                            + "anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, "
                            + "rechazo_fam, no_hosp, fmr_sol_aut, med_aut, per_sol_aut\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan
                        case "c":
                            oEPMUtilidades.crearModeloCiEPMiner(3, query_CPer);
                            oEPMUtilidades.crearModeloCDGCP(3, query_CPer);

                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Per_sol_aut\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Per_sol_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            oEPMUtilidades.crearModeloDiEPMiner(3, query_DPer);
                            oEPMUtilidades.crearModeloDDGCP(3, query_DPer);
                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Per_sol_aut\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Per_sol_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;

                            }
                            break;
                    }
                    break;

                case 4: //No_hosp

                    String query_CNo = "SELECT * FROM matriz_binaria;";
                    String query_DNo = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, "
                            + "anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, rechazo_fam, fmr_sol_aut, med_aut, per_sol_aut, no_hosp\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan
                        case "c":
                            //dataInstanceNo = oEPMUtilidades.devuelveInstancias(query_CNo);
                            oEPMUtilidades.crearModeloCiEPMiner(4, query_CNo);
                            oEPMUtilidades.crearModeloCDGCP(4, query_CNo);
                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\No_hosp\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\No_hosp\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            oEPMUtilidades.crearModeloDiEPMiner(4, query_DNo);
                            oEPMUtilidades.crearModeloDDGCP(4, query_DNo);

                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\No_hosp\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\No_hosp\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                            }
                            break;
                    }
                    break;
                case 5://rechazo_fam

                    String query_CRechazo = "SELECT * FROM matriz_binaria;";
                    String query_DRechazo = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac, casos,"
                            + " hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, fmr_sol_aut, med_aut,"
                            + " per_sol_aut, no_hosp, rechazo_fam\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan
                        case "c":

                            oEPMUtilidades.crearModeloCiEPMiner(5, query_CRechazo);
                            oEPMUtilidades.crearModeloCDGCP(5, query_CRechazo);
                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Rechazo_fam\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Rechazo_fam\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            oEPMUtilidades.crearModeloDiEPMiner(5, query_DRechazo);
                            oEPMUtilidades.crearModeloDDGCP(5, query_DRechazo);
                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Rechazo_fam\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Rechazo_fam\\modelo.dat";

                            //dataInstanceNo = oEPMUtilidades.devuelveInstancias(query_DRechazo);
                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                            }
                            break;
                    }
                    break;
                case 6: //mcc_no_aut
                    //Instance dataInstanceMccn;
                    String query_CMccn = "SELECT * FROM matriz_binaria;";
                    String query_DMccn = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac,"
                            + " casos, hall_disc, hall_arb, hall_dem, mcc_aut, fmr_sol_aut, med_aut, per_sol_aut, no_hosp, rechazo_fam, mcc_no_aut\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        //solo 2 algotimos funcionan
                        case "c":
                            oEPMUtilidades.crearModeloCiEPMiner(6, query_CMccn);
                            oEPMUtilidades.crearModeloCDGCP(6, query_CMccn);

                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_no_aut\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_no_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            oEPMUtilidades.crearModeloDiEPMiner(6, query_DMccn);
                            oEPMUtilidades.crearModeloDDGCP(6, query_DMccn);

                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_no_aut\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_no_aut\\modelo.dat";

                            switch (modeloSeleccionado) {

                                case 1:
                                    System.out.println("Entro a iEPMiner");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                            }
                            break;
                    }
                    break;
                case 7: //mcc_aut

                    String query_CMcc = "SELECT * FROM matriz_binaria;";
                    String query_DMcc = "SELECT   area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac, casos, hall_disc,"
                            + " hall_arb, hall_dem, fmr_sol_aut, med_aut, per_sol_aut, no_hosp, rechazo_fam, mcc_no_aut, mcc_aut\n"
                            + " FROM vista_minable;";
                    switch (corpusSeleccionado) {

                        case "c":
                            oEPMUtilidades.crearModeloCiEPMiner(7, query_CMcc);
                            oEPMUtilidades.crearModeloCDGCP(7, query_CMcc);

                            String rutaTra = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_aut\\modelo.dat";
                            String rutaTraDG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");

                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTraDG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC1(confianzaReglas);
                                    break;

                            }

                            break;
                        case "d":
                            oEPMUtilidades.crearModeloDiEPMiner(7, query_DMcc);
                            oEPMUtilidades.crearModeloDDGCP(7, query_DMcc);
                            String rutaTra2 = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_aut\\modelo.dat";
                            String rutaTra2DG = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_aut\\modelo.dat";

                            switch (modeloSeleccionado) {
                                case 1:
                                    System.out.println("Entro a iEPMiner D");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloiEPMiner(rutaTra2, minSoporte, growthRate, chiSquaret);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                                case 2:
                                    System.out.println("Entro DGCP");
                                    confianzaReglas = oEPMUtilidades.obtenerModeloDGCP(rutaTra2DG, minSoporte);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD1(confianzaReglas);
                                    break;
                            }//Cierra switch Modelo Seleccionado
                            break;
                    }
                    break;//Cierra switch corpusSeleccionado

            }//cierra el switch claseSeleccionada
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
        //cierra guardarModelo, en este método se crea y se guarda el  modelo y los archivos generados
    }

//Metodo que muestra los resultados para el especialista
    public void mostrarResultados(int modeloSeleccionado) {
        try {

            switch (claseSeleccionada) {
                case 1://Frm_sol_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner1();

                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo); //guarda todas las reglas sin interpretar
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIF);//guarda la confianza de cada regla
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);//guarda la regla interpretada junto con la confianza
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);//guarda la regla sin interpretar y la confianza

//
                                    crearPDF(modeloSeleccionado, confianzaAll);

                                    break;
                                case 2:
                                    String confianzaCDF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP1();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDF);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);

                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    String confianzaDIF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";

                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner1();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIF);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

//
                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP1();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDF);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

//
                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;
                case 2: //Med_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner2();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMed);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaCDMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP2();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMed);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    String confianzaDIMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner2();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMed);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP2();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMed);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 3://Per_sol_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner3();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIP);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2://DGCP
                                    String confianzaCDP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP3();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDP);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1://iEPMiner
                                    String confianzaDIP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner3();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIP);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2://DGCP
                                    String confianzaDDP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP3();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDP);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 4: //No_hosp
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMine
                                    String confianzaCIN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner4();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIN);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2://DCGP
                                    String confianzaCDN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP4();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDN);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    String confianzaDIN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner4();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIN);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP4();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDN);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                            }
                            break;
                    }
                    break;

                case 5: //Rechazo_fam
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner5();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIR);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaCDR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP5();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDR);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    String confianzaDIR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner5();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIR);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP5();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDR);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                            }
                            break;
                    }
                    break;

                case 6: //Mcc_no_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner6();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMCCNO);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2://DGCP
                                    String confianzaCDMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP6();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMCCNO);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1://iEPMiner
                                    String confianzaDIMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner6();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMCCNO);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP6();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMCCNO);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 7://Mcc_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner
                                    String confianzaCIMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner7();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMCC);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaCDMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_DGCP7();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMCC);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    String confianzaDIMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner7();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMCC);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;
                                case 2:
                                    String confianzaDDMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_DGCP7();
                                    reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMCC);
                                    reglasPDFA = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    //confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);
                                    confianzaReglas = oEPMUtilidades.describeReglasConfianza(reglas2, confianzaAll);

                                    crearPDF(modeloSeleccionado, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }
//Metodo que muestra los resultados para cualquier usuario

    public void mostrarResultadosEPM() {
        try {

            switch (claseSeleccionada) {
                case 1://Frm_sol_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {

                                case 1: //iEPMiner
                                    System.out.println("modelo" + modeloSeleccionado);
                                    String confianzaCIF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner1(); //Ruta del modelo.dat

                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIF);//guarda la confianza de cada regla
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);//guarda las reglas interpretadas junto con su confianza

                                    break;
                                case 2:
                                    System.out.println("modelo" + modeloSeleccionado);
                                    pathModelo = oConfApp.getPahtModel_c_DGCP1();

                                    String confianzaCDF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDF);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);

                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner1();
                                    String confianzaDIF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIF);

                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP1();

                                    String confianzaDDF = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Frm_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDF);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;
                case 2: //Med_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner2();

                                    String confianzaCIMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMed);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP2();
                                    String confianzaCDMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMed);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner2();
                                    String confianzaDIMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMed);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP2();
                                    String confianzaDDMed = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Med_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMed);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;
                case 3://Per_sol_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner3();

                                    String confianzaCIP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIP);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP3();
                                    String confianzaCDP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDP);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner3();
                                    //reglas2 = oEPMUtilidades.obtenerReglasSI(pathModelo);
                                    String confianzaDIP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIP);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP3();

                                    String confianzaDDP = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Per_sol_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDP);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 4: //No_hosp
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner4();

                                    String confianzaCIN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIN);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP4();

                                    String confianzaCDN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDN);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner4();

                                    String confianzaDIN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIN);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP4();

                                    String confianzaDDN = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\No_hosp\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDN);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 5: //Rechazo_fam
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner5();

                                    String confianzaCIR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIR);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP5();

                                    String confianzaCDR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDR);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner5();

                                    String confianzaDIR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIR);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP5();

                                    String confianzaDDR = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Rechazo_fam\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDR);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

                case 6: //Mcc_no_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner6();

                                    String confianzaCIMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMCCNO);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP6();

                                    String confianzaCDMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMCCNO);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner6();

                                    String confianzaDIMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner6();
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMCCNO);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP6();

                                    String confianzaDDMCCNO = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_no_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMCCNO);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;
                case 7://Mcc_aut
                    switch (corpusSeleccionado) {
                        case "c":

                            switch (modeloSeleccionado) {
                                case 1: //iEPMiner

                                    pathModelo = oConfApp.getPahtModel_c_iEPMiner7();

                                    String confianzaCIMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\iEPMiner\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCIMCC);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_c_DGCP7();

                                    String confianzaCDMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\C\\DGCP\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaCDMCC);
                                    reglas = oEPMUtilidades.obtenerReglasC(pathModelo, confianzaAll);
                                    break;

                            }

                            break;
                        case "d":

                            switch (modeloSeleccionado) {
                                case 1:
                                    pathModelo = oConfApp.getPahtModel_d_iEPMiner7();

                                    String confianzaDIMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\iEPMiner\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDIMCC);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;
                                case 2:
                                    pathModelo = oConfApp.getPahtModel_d_DGCP7();

                                    String confianzaDDMCC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\EPM\\D\\DGCP\\Mcc_aut\\TRA_QUAC_CONFIDENCE0.txt";
                                    confianzaAll = oEPMUtilidades.obtenerConfianza(confianzaDDMCC);
                                    reglas = oEPMUtilidades.obtenerReglasD(pathModelo, confianzaAll);
                                    break;

                            }
                            break;
                    }
                    break;

            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        Phrase phrase = new Phrase();
        int algoritmos = getModeloSeleccionado();
        System.out.println("Algoritmo: " + algoritmos);
        try {
            switch (claseSeleccionada) {
                case 1://Frm_sol_aut

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("__________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                  Conjunto de datos: " + corpusSeleccionado + "\n");
                                    phrase.add("                  Algoritmo: iEPMiner" + "\n");

                                    phrase.add("                  Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                  Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                  Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("___________________Reglas interpretadas en lenguaje Natural_____________" + "\n" + "\n");
                                    phrase.add("                   Conjunto de datos: C" + "\n");
                                    phrase.add("                   Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                   Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("__________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                  Conjunto de datos: " + corpusSeleccionado + "\n");
                                    phrase.add("                  Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                  Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                  Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                  Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("                  Conjunto de datos: D" + "\n");
                                    phrase.add("                  Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                  Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;
                case 2:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("____________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                    Conjunto de datos: " + corpusSeleccionado + "\n");
                                    phrase.add("                    Algoritmo: iEPMiner" + "\n");

                                    phrase.add("                    Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                    Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                    Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("____________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                    Conjunto de datos: C" + "\n");
                                    phrase.add("                    Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                    Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");

                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;

                case 3:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (modeloSeleccionado) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");

                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;

                case 4:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;
                case 5:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;
                case 6:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;
                case 7:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: C" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");

                                    break;

                            }

                            break;
                        case "d":
                            switch (algoritmos) {
                                case 1:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: iEPMiner" + "\n");
                                    phrase.add("                        Tasa de crecimiento: " + this.growthRate + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    phrase.add("                        Chi-squared: " + this.chiSquaret + "\n");
                                    break;
                                case 2:
                                    phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                        Conjunto de datos: D" + "\n");
                                    phrase.add("                        Algoritmo: DGCP-Tree" + "\n");
                                    phrase.add("                        Soporte: " + this.minSoporte + "\n");
                                    break;
                            }
                            break;

                    }
                    break;

            }
            phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
            pdf.add(phrase);

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    //Este método es llamado cuando muestra las reglas y crea el pdf con los parámetros, clase y conjunto de datos seleccionado
    public void crearPDF(int modeloseleccionado, List<Confianza> confianzaList) throws DocumentException, FileNotFoundException {

        //FileOutputStream pdf = new FileOutputStream("D:\\Escritorio\\Reglas\\Reglas.pdf");
        FileOutputStream pdf = new FileOutputStream("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\Reglas\\Reglas.pdf");
        int algoritmo = modeloseleccionado;
        System.out.println("corpues : " + corpusSeleccionado + "  Algoritmo: " + algoritmo);
//
        try {
            switch (claseSeleccionada) {
                case 1:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;
                                    Confianza conf = new Confianza();

                                    // List<Confianza> c = new ArrayList<>();
                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {

                                        conf.setCONF(confianzaList.get(i).getCONF());

                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                //___________________
                case 2:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                case 3:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + modeloSeleccionado);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                case 4:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                case 5:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                case 6:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + modeloSeleccionado);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                case 7:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpues: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a C iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a C DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            }

                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D iEPMiner" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: iEPMiner" + "\n");
                                    texto.add("Growth Rate: " + this.growthRate + "\n");
                                    texto.add("Soporte: " + this.minSoporte + "\n");
                                    texto.add("Chi-squared: " + this.chiSquaret + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D DGCP-Tree: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando cool");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: DGCP-Tree" + "\n");
                                    texto1.add("Soporte: " + this.minSoporte + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                            }
                            break;
                    }
                    break;

                //--------------------------------
            }
            //pdf.add(phrase);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void leerXML() {
        try {
            //documento = readXML("algorithms.xml");
            // se obtiene el DocumentBuilder DocumentBuilderFactory contiene
            //la API para obtener instancias de documentos DOM de un documento XML.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //El parse()método analiza el archivo XML en un Document.
            org.w3c.dom.Document documen = builder.parse(new File("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\epm-framework-master\\algorithmsLC.xml"));
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
                //  actual_fully_qualified_class = segundo.getElementsByTagName("class").item(0).getTextContent();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //-------------------------------------------------------
    public void setoConfApp(configuracionAppControler oConfApp) {
        this.oConfApp = oConfApp;
    }

    public int getModeloSeleccionado() {
        return modeloSeleccionado;
    }

    public void setModeloSeleccionado(int modeloSeleccionado) {
        this.modeloSeleccionado = modeloSeleccionado;
        //  System.out.println("Modelo seleccionado: " + modeloSeleccionado);
    }

    public String getCorpusSeleccionado() {
        //System.out.println("seleccionado: " + corpusSeleccionado);
        return corpusSeleccionado;
    }

    public void setCorpusSeleccionado(String corpusSeleccionado) {
        this.corpusSeleccionado = corpusSeleccionado;
        // System.out.println("Corpus seleccionado: " + corpusSeleccionado);
    }
    //XML----------------------------------
//options

    public String getPathModelo() {
        return pathModelo;
    }

    public void setPathModelo(String pathModelo) {
        this.pathModelo = pathModelo;
    }

    public int getClaseSeleccionada() {
        return claseSeleccionada;
    }

    public void setClaseSeleccionada(int claseSeleccionada) {
        this.claseSeleccionada = claseSeleccionada;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        // System.out.println("Modelo seleccionado: " + algorithm);
    }

    public List<String> getAlgorithms() {
        leerXML();
        // obtnerParametros();
        //  addParamsToPanel();
        // LearnButtonActionPerformed();
////
        return algorithms;
    }

    public void setAlgorithms(ArrayList<String> algorithms) {
        this.algorithms = algorithms;

        // System.out.println("Set" + algorithms);
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(double growthRate) {
        this.growthRate = growthRate;
    }

    public double getChiSquaret() {
        return chiSquaret;
    }

    public void setChiSquaret(double chiSquaret) {
        this.chiSquaret = chiSquaret;
    }

    public double getMinConfianza() {
        return minConfianza;
    }

    public void setMinConfianza(double minConfianza) {
        this.minConfianza = minConfianza;
    }

    public double getMinSoporte() {
        // leeXML();

        return minSoporte;
    }

    public void setMinSoporte(double minSoporte) {
        this.minSoporte = minSoporte;
    }

    public List<String> getReglas() {
        return reglas;
    }

    public void setReglas(List<String> reglas) {
        this.reglas = reglas;
    }

    public List<String> getReglasPDFA() {
        return reglasPDFA;
    }

    public void setReglasPDFA(ArrayList<String> reglasPDFA) {
        this.reglasPDFA = reglasPDFA;
    }

    public List<Confianza> getConfianzaAll() {
        //for (Confianza conf : Confianaza) {
        return confianzaAll;
    }

    public void setConfianzaAll(List<Confianza> confianzaAll) {
        this.confianzaAll = confianzaAll;
    }

    public List<Reglas> getConfianzaReglas() {
        return confianzaReglas;
    }

    public void setConfianzaReglas(List<Reglas> confianzaReglas) {
        this.confianzaReglas = confianzaReglas;
    }

    public void setoEPMUtilidades(epmUtilidades oEPMUtilidades) {
        this.oEPMUtilidades = oEPMUtilidades;
    }

    public configuracionAppControler getoConfApp() {
        return oConfApp;
    }

}
