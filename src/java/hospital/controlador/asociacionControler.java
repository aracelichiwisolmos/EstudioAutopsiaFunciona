package hospital.controlador;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import hospital.asociacion.model.asociacionUtilidades;
import hospital.asociacion.model.reglas;
import hospital.clasificacion.model.clasificacionUtilidades;
import hospital.datos.AccesoDatos;
import hospital.modelo.medico;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import weka.associations.Apriori;
import weka.associations.FPGrowth;
import weka.associations.PredictiveApriori;
import weka.associations.Tertius;
import weka.core.Instances;

@ManagedBean(name = "oAsociacionJB")
@ViewScoped
public class asociacionControler implements Serializable {

    private asociacionUtilidades oAsociacionUtilidades;
    private configuracionAppControler oConfApp;
    private List<String> stringReglas;
    private int modeloSeleccionado;
    private int claseSeleccionada;
    private String corpusSeleccionado;
    private int cantReglas;
    private int literales;
    private double minConfianza;
    private double minSoporte;
    private List<reglas> reglas;
    private int nuevasEncuestas;
    private final medico oMedico;
    private String pathModelo = "";
    private String texto;
    private String columna1;
    private String columna2;

    public asociacionControler() throws Exception {
        this.oMedico = new medico();
        this.oAsociacionUtilidades = new asociacionUtilidades();
        this.oConfApp = new configuracionAppControler();
        this.stringReglas = null;
        this.modeloSeleccionado = 1;
        this.corpusSeleccionado = null;
        this.cantReglas = 10;
        this.minConfianza = 0.9;
        this.minSoporte = 0.1;
        this.reglas = null;
        this.literales = 2;
        this.nuevasEncuestas = oMedico.cantEncuestas() - oAsociacionUtilidades.cantInstanciasC();
        this.columna1 = "";
        this.columna2 = "";
    }

    public void actualizarColumnas(int modeloSeleccionado) {
        switch (modeloSeleccionado) {
            case 4:
                this.columna1 = "Confirmación";
                this.columna2 = "Frecuencia";
                break;
            default:
                this.columna1 = "Confianza";
                this.columna2 = "Soporte";
                break;
        }
    }

    public void devuelveInstancias() {
        try {
            String query = "SELECT * FROM vista_minable;";
            oAsociacionUtilidades.devuelveInstancias(query);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void obtenerReglas(int modeloSeleccionado) throws Exception {
        this.modeloSeleccionado = modeloSeleccionado;
        try {

            Instances dataInstances;
            String query_C = "SELECT * FROM matriz_binaria;";
            String query_D = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
                    + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                    + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                    + "       com_sug_op\n"
                    + "  FROM vista_minable;";

            switch (corpusSeleccionado) {
                case "c":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_C);
                    switch (modeloSeleccionado) {
                        case 1:
                            reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstances, cantReglas, minSoporte, minConfianza);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                        case 2:
                            reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstances, cantReglas, minSoporte, minConfianza);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                        case 3:
                            reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstances, cantReglas);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                        case 4:
                            reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstances, cantReglas, literales, minConfianza);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                    }

                    break;
                case "d":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_D);
                    switch (modeloSeleccionado) {
                        case 1:
                            reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstances, cantReglas, minSoporte, minConfianza);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                        case 4:
                            reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstances, cantReglas, literales, minConfianza);
                            this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                            break;
                    }
                    break;
            }
            actualizarColumnas(modeloSeleccionado);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

//--------------------------------------------por clase
    public void obtenerReglasPorClase(int modeloSeleccionado) throws Exception {
        this.modeloSeleccionado = modeloSeleccionado;
        this.claseSeleccionada = claseSeleccionada;
        try {

            switch (claseSeleccionada) {
                case 1:// Fmr_aut falta
                    Instances dataInstancesFmr;
                    String query_CFmr = "SELECT * FROM matriz_binaria;";
                    String query_DFmr = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
                            + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                            + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                            + "       com_sug_op\n"
                            + "  FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesFmr = oAsociacionUtilidades.devuelveInstancias(query_CFmr);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesFmr, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesFmr, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesFmr, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesFmr, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesFmr = oAsociacionUtilidades.devuelveInstancias(query_DFmr);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesFmr, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesFmr, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 2: //med_aut
                    Instances dataInstancesMed;
                    String query_CMed = "SELECT * FROM matriz_binaria;";
                    String query_DMed = "SELECT area, categoria, ult_grado, esc_med_gral, "
                            + "esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem,"
                            + " mcc_aut, mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, fmr_sol_aut\n"
                            + "FROM vista_minable; ";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesMed = oAsociacionUtilidades.devuelveInstancias(query_CMed);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMed, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesMed, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesMed, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMed, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesMed = oAsociacionUtilidades.devuelveInstancias(query_DMed);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMed, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMed, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 3: //Per_sol_aut
                    Instances dataInstancesPer;
                    String query_CPer = "SELECT * FROM matriz_binaria;";
                    String query_DPer = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, "
                            + "anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, "
                            + "rechazo_fam, no_hosp, fmr_sol_aut, med_aut\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesPer = oAsociacionUtilidades.devuelveInstancias(query_CPer);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesPer, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesPer, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesPer, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesPer, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesPer = oAsociacionUtilidades.devuelveInstancias(query_DPer);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesPer, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesPer, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 4: //No_hosp
                    Instances dataInstancesNo;
                    String query_CNo = "SELECT * FROM matriz_binaria;";
                    String query_DNo = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, "
                            + "anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, rechazo_fam, fmr_sol_aut, med_aut, per_sol_aut\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesNo = oAsociacionUtilidades.devuelveInstancias(query_CNo);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesNo, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesNo, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesNo, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesNo, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesNo = oAsociacionUtilidades.devuelveInstancias(query_DNo);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesNo, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesNo, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 5://Rechazo_fam
                    Instances dataInstancesRec;
                    String query_CRec = "SELECT * FROM matriz_binaria;";
                    String query_DRec = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac, casos,"
                            + " hall_disc, hall_arb, hall_dem, mcc_aut, mcc_no_aut, fmr_sol_aut, med_aut, per_sol_aut, no_hosp\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesRec = oAsociacionUtilidades.devuelveInstancias(query_CRec);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesRec, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesRec, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesRec, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesRec, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesRec = oAsociacionUtilidades.devuelveInstancias(query_DRec);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesRec, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesRec, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 6: //mcc_no_aut
                    Instances dataInstancesMccn;
                    String query_CMccn = "SELECT * FROM matriz_binaria;";
                    String query_DMccn = "SELECT area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac,"
                            + " casos, hall_disc, hall_arb, hall_dem, mcc_aut, fmr_sol_aut, med_aut, per_sol_aut, no_hosp, rechazo_fam\n"
                            + "FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesMccn = oAsociacionUtilidades.devuelveInstancias(query_CMccn);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMccn, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesMccn, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesMccn, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMccn, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesMccn = oAsociacionUtilidades.devuelveInstancias(query_DMccn);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMccn, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMccn, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
                case 7: //mcc_aut
                    Instances dataInstancesMcc;
                    String query_CMcc = "SELECT * FROM matriz_binaria;";
                    String query_DMcc = "SELECT   area, categoria, ult_grado, esc_med_gral, esc_esp, anios_prac, casos, hall_disc,"
                            + " hall_arb, hall_dem, fmr_sol_aut, med_aut, per_sol_aut, no_hosp, rechazo_fam, mcc_no_aut\n"
                            + " FROM vista_minable;";

                    switch (corpusSeleccionado) {
                        case "c":
                            dataInstancesMcc = oAsociacionUtilidades.devuelveInstancias(query_CMcc);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMcc, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 2:
                                    reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstancesMcc, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 3:
                                    reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstancesMcc, cantReglas);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMcc, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }

                            break;
                        case "d":
                            dataInstancesMcc = oAsociacionUtilidades.devuelveInstancias(query_DMcc);
                            switch (modeloSeleccionado) {
                                case 1:
                                    reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstancesMcc, cantReglas, minSoporte, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                                case 4:
                                    reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstancesMcc, cantReglas, literales, minConfianza);
                                    this.stringReglas = oAsociacionUtilidades.describeReglas(reglas);
                                    break;
                            }
                            break;
                    }
                    actualizarColumnas(modeloSeleccionado);
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public asociacionUtilidades getoAsociacionUtilidades() {
        return oAsociacionUtilidades;
    }

    public void actualizaDatasets() {
        try {
            clasificacionUtilidades oUtilidadesClasificacion = new clasificacionUtilidades();
            oUtilidadesClasificacion.actualizarModeloPregAbiertas();
            oAsociacionUtilidades.actualizarDatasetD();
            oAsociacionUtilidades.actualizarDatasetC();
            this.nuevasEncuestas = oMedico.cantEncuestas() - oAsociacionUtilidades.cantInstanciasC();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void guardarModelo(int modeloSeleccionado) throws Exception {
        try {
            Instances dataInstances;
            String modelo;
            String query_C = "SELECT * FROM matriz_binaria;";
            String query_D = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
                    + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                    + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                    + "       com_sug_op\n"
                    + "  FROM vista_minable;";

            switch (corpusSeleccionado) {
                case "c":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_C);
                    switch (modeloSeleccionado) {
                        case 1:
                            modelo = oConfApp.getPahtModel_c_Apriori();
                            Apriori oApriori = oAsociacionUtilidades.obterenModeloApriori(dataInstances, cantReglas, minSoporte, minConfianza);
                            oAsociacionUtilidades.guardarModelo(oApriori, modelo);
                            break;
                        case 2:
                            modelo = oConfApp.getPahtModel_c_FPGrowth();
                            FPGrowth oFPGrowth = oAsociacionUtilidades.obterenModeloFPGrowth(dataInstances, cantReglas, minSoporte, minConfianza);
                            oAsociacionUtilidades.guardarModelo(oFPGrowth, modelo);
                            break;
                        case 3:
                            modelo = oConfApp.getPahtModel_c_PredictiveApriori();
                            PredictiveApriori oPredictive = oAsociacionUtilidades.obterenModeloPredictiveApriori(dataInstances, cantReglas);
                            oAsociacionUtilidades.guardarModelo(oPredictive, modelo);
                            break;
                        case 4:
                            modelo = oConfApp.getPahtModel_c_Tertius();
                            Tertius oTertius = oAsociacionUtilidades.obterenModeloTertius(dataInstances, cantReglas, literales, minConfianza);
                            oAsociacionUtilidades.guardarModelo(oTertius, modelo);
                            break;
                    }

                    break;
                case "d":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_D);
                    switch (modeloSeleccionado) {
                        case 1:
                            modelo = oConfApp.getPahtModel_d_Apriori();
                            Apriori oApriori = oAsociacionUtilidades.obterenModeloApriori(dataInstances, cantReglas, minSoporte, minConfianza);
                            oAsociacionUtilidades.guardarModelo(oApriori, modelo);
                            break;
                        case 4:
                            modelo = oConfApp.getPahtModel_d_Tertius();
                            Tertius oTertius = oAsociacionUtilidades.obterenModeloTertius(dataInstances, cantReglas, literales, minConfianza);
                            oAsociacionUtilidades.guardarModelo(oTertius, modelo);
                            break;
                    }
                    break;
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void setoAsociacionUtilidades(asociacionUtilidades oAsociacionUtilidades) {
        this.oAsociacionUtilidades = oAsociacionUtilidades;
    }

    public configuracionAppControler getoConfApp() {
        return oConfApp;
    }

    public void mostrarResultados() throws Exception {
        try {
            Instances dataInstances;
            String query_C = "SELECT * FROM matriz_binaria;";
            String query_D = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
                    + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                    + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                    + "       com_sug_op\n"
                    + "  FROM vista_minable;";
            switch (corpusSeleccionado) {
                case "c":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_C);
                    switch (modeloSeleccionado) {
                        case 1:
                            pathModelo = oConfApp.getPahtModel_c_Apriori();
                            reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstances, pathModelo);
                            break;
                        case 2:
                            pathModelo = oConfApp.getPahtModel_c_FPGrowth();
                            reglas = oAsociacionUtilidades.obterenReglasFPGrowth(dataInstances, pathModelo);
                            break;
                        case 3:
                            pathModelo = oConfApp.getPahtModel_c_PredictiveApriori();
                            reglas = oAsociacionUtilidades.obterenReglasPredictiveApriori(dataInstances, cantReglas);
                            break;
                        case 4:
                            pathModelo = oConfApp.getPahtModel_c_Tertius();
                            reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstances, pathModelo);
                            break;
                    }

                    break;
                case "d":
                    dataInstances = oAsociacionUtilidades.devuelveInstancias(query_D);
                    switch (modeloSeleccionado) {
                        case 1:
                            pathModelo = oConfApp.getPahtModel_d_Apriori();
                            reglas = oAsociacionUtilidades.obterenReglasApriori(dataInstances, pathModelo);
                            break;
                        case 4:
                            pathModelo = oConfApp.getPahtModel_d_Tertius();
                            reglas = oAsociacionUtilidades.obterenReglasTertius(dataInstances, pathModelo);
                            break;
                    }
                    break;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        Phrase phrase = new Phrase();

        try {
            switch (corpusSeleccionado) {
                case "c":
                    switch (modeloSeleccionado) {
                        case 1:
                            phrase.add("Conjunto de datos: C" + "\n");
                            phrase.add("Algoritmo: Apriori" + "\n");
                            phrase.add("Confianza: " + this.minConfianza + "\n");
                            phrase.add("Soporte: " + this.minSoporte + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                        case 2:
                            phrase.add("Conjunto de datos: C" + "\n");
                            phrase.add("Algoritmo: FPGrowth" + "\n");
                            phrase.add("Confianza: " + this.minConfianza + "\n");
                            phrase.add("Soporte: " + this.minSoporte + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                        case 3:
                            phrase.add("Conjunto de datos: C" + "\n");
                            phrase.add("Algoritmo: PredictiveApriori" + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                        case 4:
                            phrase.add("Conjunto de datos: C" + "\n");
                            phrase.add("Algoritmo: Tertius");
                            phrase.add("Frecuencia: " + this.minSoporte + "\n");
                            phrase.add("Literales: " + this.literales + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                    }

                    break;
                case "d":
                    switch (modeloSeleccionado) {
                        case 1:
                            phrase.add("Conjunto de datos: D" + "\n");
                            phrase.add("Algoritmo: Apriori" + "\n");
                            phrase.add("Confianza: " + this.minConfianza + "\n");
                            phrase.add("Soporte: " + this.minSoporte + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                        case 4:
                            phrase.add("Conjunto de datos: D" + "\n");
                            phrase.add("Algoritmo: Tertius" + "\n");
                            phrase.add("Frecuencia: " + this.minSoporte + "\n");
                            phrase.add("Literales: " + this.literales + "\n");
                            phrase.add("Reglas: " + this.cantReglas + "\n");
                            break;
                    }
                    break;
            }
            pdf.add(phrase);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void setoConfApp(configuracionAppControler oConfApp) {
        this.oConfApp = oConfApp;
    }

    public List<String> getStringReglas() {
        return stringReglas;
    }

    public void setStringReglas(List<String> stringReglas) {
        this.stringReglas = stringReglas;
    }

    public int getClaseSeleccionada() {
        return claseSeleccionada;
    }

    public void setClaseSeleccionada(int claseSeleccionada) {
        this.claseSeleccionada = claseSeleccionada;
    }

    public int getModeloSeleccionado() {
        return modeloSeleccionado;
    }

    public void setModeloSeleccionado(int modeloSeleccionado) {
        this.modeloSeleccionado = modeloSeleccionado;
    }

    public String getCorpusSeleccionado() {
        return corpusSeleccionado;
    }

    public void setCorpusSeleccionado(String corpusSeleccionado) {
        this.corpusSeleccionado = corpusSeleccionado;
    }

    public int getCantReglas() {
        return cantReglas;
    }

    public void setCantReglas(int cantReglas) {
        this.cantReglas = cantReglas;
    }

    public double getMinConfianza() {
        return minConfianza;
    }

    public void setMinConfianza(double minConfianza) {
        this.minConfianza = minConfianza;
    }

    public double getMinSoporte() {
        return minSoporte;
    }

    public void setMinSoporte(double minSoporte) {
        this.minSoporte = minSoporte;
    }

    public List<reglas> getReglas() {
        return reglas;
    }

    public void setReglas(List<reglas> reglas) {
        this.reglas = reglas;
    }

    public int getLiterales() {
        return literales;
    }

    public void setLiterales(int literales) {
        this.literales = literales;
    }

    public int getNuevasEncuestas() {
        return nuevasEncuestas;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getColumna1() {
        return columna1;
    }

    public void setColumna1(String columna1) {
        this.columna1 = columna1;
    }

    public String getColumna2() {
        return columna2;
    }

    public void setColumna2(String columna2) {
        this.columna2 = columna2;
    }

    public void InsertaTexto(int idPregunta) throws Exception {
        String[] respuestas = texto.split("\\.");
        String clase;
        String respuesta;
        String clase_respuesta;
        String[] clase_texto;

        for (String respuesta1 : respuestas) {
            clase_respuesta = respuesta1;
            try {
                clase_respuesta = clase_respuesta.trim();
                clase_texto = clase_respuesta.split("&");
                clase = clase_texto[0];
                respuesta = clase_texto[1];
                insertaEntrenamiento(clase, respuesta, idPregunta);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
            }
        }

    }

    public void insertaEntrenamiento(String clase, String respuesta, int idPreg) {
        AccesoDatos oAD = null;
        try {
            String sQuery = "INSERT INTO entrenamiento_texto(texto, clasificado, "
                    + "id_pregunta) VALUES ('" + respuesta + "', '" + clase + "', " + idPreg + ");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }

    }

    public void configurarAlgoritmos(int modeloSeleccionado) throws Exception {
        try {

            switch (corpusSeleccionado) {
                case "c":
                    switch (modeloSeleccionado) {
                        case 1:
                            minConfianza = 0.9;
                            minSoporte = 0.4;
                            cantReglas = 15;
                            break;
                        case 2:
                            minConfianza = 0.9;
                            minSoporte = 0.4;
                            cantReglas = 8;
                            break;
                        case 3:
                            cantReglas = 12;
                            break;
                        case 4:
                            minConfianza = 0.5;
                            cantReglas = 12;
                            break;
                    }

                    break;
                case "d":
                    switch (modeloSeleccionado) {
                        case 1:
                            minConfianza = 0.9;
                            minSoporte = 0.5;
                            cantReglas = 10;
                            break;
                        case 4:
                            minConfianza = 0.5;
                            cantReglas = 10;
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

}
