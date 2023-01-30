package hospital.controlador;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import hospital.clasificacion.model.ElementoAtributoGrafo;
import hospital.clasificacion.model.ParentSetNodo;
import hospital.clasificacion.model.clasificacionUtilidades;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

@ManagedBean(name = "oBayesNetJB")
@ViewScoped
public class BayesNetControler implements Serializable {

    private boolean disable;
    private final String query_D = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
            + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
            + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
            + "       com_sug_op\n"
            + "  FROM vista_minable;";
    private final clasificacionUtilidades oClasificaUtilidades;

    private final Instances data;
    private List<SelectItem> listAtributosDataSet;
    private int clase;
    private BayesNet bayes;
    private String nodosDataSet;
    private String arcosDataSet;
    private List<SelectItem> listaPadres;
    private int selectItemPadre;
    private List<SelectItem> listaValorNodo;
    private int selectItemValorNodo;
    private List<SelectItem> listaValorPadre;
    private int selectItemValorPadre;
    private String seleccionPadres;
    private final List<Integer> listaPadresAgregados;
    private String filtro;
    private String orden;
    private boolean collapsed;
    private String presicion;
    private int indiceProb;
    private double valorProb;

    private List<ParentSetNodo> parentSets;
    private int n_columnas;
    private String x;
    private ParentSetNodo selectParentSet;

    private String idNodo;
    private SelectItem claseSelec;

    public BayesNetControler() throws Exception {
        this.disable = true;
        oClasificaUtilidades = new clasificacionUtilidades();
        this.data = oClasificaUtilidades.devuelveInstancias(query_D);
        this.listAtributosDataSet = oClasificaUtilidades.obtenerAtributosDataset(data);
        this.clase = 0;
        this.bayes = new BayesNet();
        this.nodosDataSet = null;
        this.arcosDataSet = null;
        this.listaPadres = new ArrayList<>();
        this.selectItemPadre = -1;
        this.listaValorNodo = new ArrayList<>();
        this.selectItemValorNodo = -1;
        this.listaValorPadre = new ArrayList<>();
        this.selectItemValorPadre = -1;
        this.seleccionPadres = "";
        this.listaPadresAgregados = new ArrayList<>();
        this.filtro = "incluir";
        this.orden = "default";
        this.collapsed = true;
        this.claseSelec = null;
        this.presicion = "";
        this.n_columnas = 3;
        this.indiceProb = -1;
        this.valorProb = 0;

        this.selectParentSet = new ParentSetNodo();
        this.idNodo = "";
    }

    public void generarModelo() {
        try {

            BayesNet clasificadorBayesNet = oClasificaUtilidades.generarBayesNet(data, clase);
            this.bayes = clasificadorBayesNet;
            this.nodosDataSet = oClasificaUtilidades.datasetGrafo(this.bayes).getNodos();
            this.arcosDataSet = oClasificaUtilidades.datasetGrafo(this.bayes).getArcos();
            this.disable = false;
            this.listaPadresAgregados.clear();
            this.collapsed = false;
            this.presicion = evaluarBayesnet(bayes);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    private String evaluarBayesnet(BayesNet modelo) throws Exception {
        String presicionModelo;
        Evaluation evalua = new Evaluation(data);
        evalua.evaluateModel(modelo, data);
        presicionModelo = "Presición: " + Math.round(evalua.weightedPrecision() * 100) + "%";
        return presicionModelo;
    }

    public String dataset(int position) throws Exception {
        Instances instances = oClasificaUtilidades.devuelveInstancias(query_D);
        return oClasificaUtilidades.datasetGrafo(instances).get(position);
    }

    public void guardarModelo() {
        try {
            oClasificaUtilidades.guardarModelo(configuracionAppControler.getPahtModel_d_BayesNet(), bayes);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
            this.disable = true;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }

    public void obtenerTCP() {
        try {
            int indexNodo = Integer.parseInt(this.idNodo) - 1;
            this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayes, data);
            String nombreNodo = parentSets.get(0).getElementoNodo().getNombreNodo();
            this.listaPadres = oClasificaUtilidades.obtenerPadres(indexNodo, bayes);
            this.listaValorNodo = oClasificaUtilidades.obtenerValoresNodo(indexNodo, nombreNodo, bayes);
            String nombrePadre = "";
            for (SelectItem itemvalorPadre : listaPadres) {
                if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                    nombrePadre = itemvalorPadre.getLabel();
                }
            }
            this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayes);
            valoresPorDefecto();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    public void obtenerValoresPadre() throws Exception {
        String nombrePadre = "";
        for (SelectItem itemvalorPadre : listaPadres) {
            if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                nombrePadre = itemvalorPadre.getLabel();
            }
        }
        this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayes);
        this.selectItemValorPadre = -1;
        this.selectItemPadre = -1;
        this.filtro = "incluir";
        this.orden = "defaut";
    }

    public void adicionarPadre() {
        if (this.listaPadres.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Nodo raíz, no tiene padre."));
        } else if (selectItemPadre == -1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Debe seleccionar un nodo padre."));
        } else if (selectItemValorPadre == -1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Debe seleccionar un valor para el nodo padre."));
        } else if ("".equals(seleccionPadres)) {
            this.seleccionPadres += selectItemPadre + ": " + selectItemValorPadre;
            this.listaPadresAgregados.add(selectItemPadre);
            this.selectItemPadre = -1;
            this.selectItemValorPadre = -1;
        } else if (listaPadresAgregados.contains(selectItemPadre)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Ya ha seleccionado un valor para ese nodo padre."));
        } else {
            this.seleccionPadres += "; " + selectItemPadre + ": " + selectItemValorPadre;
            this.listaPadresAgregados.add(selectItemPadre);
            this.selectItemPadre = -1;
            this.selectItemValorPadre = -1;
        }
    }

    public void filtrar() throws Exception {
        filtrarElementos();
        if (this.indiceProb != -1 && this.valorProb > 0) {
            filtrarPorProbabilidad();
        }
//        valoresPorDefecto();
    }

    public void filtrarElementos() throws Exception {

        int indexNodo = Integer.parseInt(this.idNodo) - 1;
        this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayes, data);
        this.listaPadres = oClasificaUtilidades.obtenerPadres(indexNodo, bayes);
        String nombreNodo = parentSets.get(0).getElementoNodo().getNombreNodo();
        this.listaValorNodo = oClasificaUtilidades.obtenerValoresNodo(indexNodo, nombreNodo, bayes);
        String nombrePadre = "";
        for (SelectItem itemvalorPadre : listaPadres) {
            if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                nombrePadre = itemvalorPadre.getLabel();
            }
        }
        this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayes);

        if (selectItemValorNodo != -1 && listaPadresAgregados.isEmpty()) {
            filtrarPorNodo();
            //valoresPorDefecto();
        } else if (selectItemValorNodo != -1 && listaPadresAgregados.size() > 1) {
            filtrarPorNodoPadres();
            //valoresPorDefecto();
        } else if (selectItemValorNodo != -1 && listaPadresAgregados.size() == 1) {
            filtrarPorNodoPadre();
            //valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.isEmpty()) {
            this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayes, data);
            // valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.size() > 1) {
            filtrarPorPadres();
            // valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.size() == 1) {
            filtrarPorPadre();
            //valoresPorDefecto();
        }

    }

    private void filtrarPorNodo() {
        try {
            List<ParentSetNodo> resultadoFiltro = new ArrayList<>();
            String valorAtt = "";
            for (SelectItem itemvalorNodo : listaValorNodo) {
                if (itemvalorNodo.getValue().equals(this.selectItemValorNodo)) {
                    valorAtt = itemvalorNodo.getLabel();
                }
            }
            for (ParentSetNodo parentSet : this.parentSets) {
                if (parentSet.getElementoNodo().getValorElemento().equals(valorAtt)) {
                    resultadoFiltro.add(parentSet);
                }
            }

            this.parentSets = resultadoFiltro;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    private void filtrarPorPadre() {
        try {
            List<ParentSetNodo> resultadoFiltro = new ArrayList<>();
            String[] auxPadre = this.seleccionPadres.split(":");
            String nombrePadre = "";
            for (SelectItem itemvalorPadre : listaPadres) {
                if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                    nombrePadre = itemvalorPadre.getLabel();
                }
            }
            List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
            String valorPadre = "";
            for (SelectItem itemvalorPadre : auxValorPadre) {
                if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                    valorPadre = itemvalorPadre.getLabel();
                }
            }
            ElementoAtributoGrafo padre = new ElementoAtributoGrafo();
            padre.setNombreNodo(nombrePadre);
            padre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
            padre.setValorElemento(valorPadre);

            for (ParentSetNodo parentSet : this.parentSets) {
                for (ElementoAtributoGrafo elementoPadre : parentSet.getPadres()) {
                    if (elementoPadre.getNombreNodo().equals(padre.getNombreNodo()) && elementoPadre.getValorElemento().equals(padre.getValorElemento())) {
                        resultadoFiltro.add(parentSet);
                    }
                }
            }
            this.parentSets = resultadoFiltro;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    private void filtrarPorNodoPadres() {
        try {
            switch (filtro) {
                case "incluir":
                    List<ParentSetNodo> resultadoFiltro = new ArrayList<>();
                    List<ElementoAtributoGrafo> listPadresSelec = new ArrayList<>();
                    Integer[] control = new Integer[parentSets.size()];
                    for (int i = 0; i < control.length; i++) {
                        control[i] = 0;
                    }

                    for (String padre : seleccionPadres.split(";")) {
                        String[] auxPadre = padre.split(":");
                        String nombrePadre = "";

                        for (SelectItem itemvalorPadre : listaPadres) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                                nombrePadre = itemvalorPadre.getLabel();
                            }
                        }
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
                        String valorPadre = "";
                        for (SelectItem itemvalorPadre : auxValorPadre) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                                valorPadre = itemvalorPadre.getLabel();
                            }
                        }
                        ElementoAtributoGrafo oPadre = new ElementoAtributoGrafo();
                        oPadre.setNombreNodo(nombrePadre);
                        oPadre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
                        oPadre.setValorElemento(valorPadre);

                        listPadresSelec.add(oPadre);
                    }
                    String valorAtt = "";
                    for (SelectItem itemvalorNodo : listaValorNodo) {
                        if (itemvalorNodo.getValue().equals(this.selectItemValorNodo)) {
                            valorAtt = itemvalorNodo.getLabel();
                        }
                    }
                    for (int j = 0; j < parentSets.size(); j++) {
                        ParentSetNodo parentSet = parentSets.get(j);
                        for (ElementoAtributoGrafo oPadre : parentSet.getPadres()) {
                            for (int i = 0; i < listPadresSelec.size(); i++) {
                                ElementoAtributoGrafo oPadreSelecc = listPadresSelec.get(i);
                                if (oPadre.getNumNodo() == oPadreSelecc.getNumNodo() && oPadre.getValorElemento().equals(oPadreSelecc.getValorElemento()) && parentSet.getElementoNodo().getValorElemento().equals(valorAtt)) {
                                    control[j]++;
                                }
                            }

                        }
                    }
                    for (int i = 0; i < control.length; i++) {
                        if (control[i] == listPadresSelec.size()) {
                            resultadoFiltro.add(parentSets.get(i));
                        }
                    }
                    this.parentSets = resultadoFiltro;
                    break;
                case "excluir":
                    List<ParentSetNodo> resultadoFiltro1 = new ArrayList<>();
                    List<ElementoAtributoGrafo> listPadresSelec1 = new ArrayList<>();
                    Integer[] control1 = new Integer[parentSets.size()];
                    for (int i = 0; i < control1.length; i++) {
                        control1[i] = 0;
                    }

                    for (String padre : seleccionPadres.split(";")) {
                        String[] auxPadre = padre.split(":");
                        String nombrePadre = "";

                        for (SelectItem itemvalorPadre : listaPadres) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                                nombrePadre = itemvalorPadre.getLabel();
                            }
                        }
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
                        String valorPadre = "";
                        for (SelectItem itemvalorPadre : auxValorPadre) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                                valorPadre = itemvalorPadre.getLabel();
                            }
                        }
                        ElementoAtributoGrafo oPadre = new ElementoAtributoGrafo();
                        oPadre.setNombreNodo(nombrePadre);
                        oPadre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
                        oPadre.setValorElemento(valorPadre);

                        listPadresSelec1.add(oPadre);
                    }
                    String valorAtt1 = "";
                    for (SelectItem itemvalorNodo : listaValorNodo) {
                        if (itemvalorNodo.getValue().equals(this.selectItemValorNodo)) {
                            valorAtt1 = itemvalorNodo.getLabel();
                        }
                    }
                    for (int j = 0; j < parentSets.size(); j++) {
                        ParentSetNodo parentSet = parentSets.get(j);
                        for (ElementoAtributoGrafo oPadre : parentSet.getPadres()) {
                            for (int i = 0; i < listPadresSelec1.size(); i++) {
                                ElementoAtributoGrafo oPadreSelecc = listPadresSelec1.get(i);
                                if (oPadre.getNumNodo() == oPadreSelecc.getNumNodo() && oPadre.getValorElemento().equals(oPadreSelecc.getValorElemento()) || parentSet.getElementoNodo().getValorElemento().equals(valorAtt1)) {
                                    control1[j]++;
                                }
                            }

                        }
                    }
                    for (int i = 0; i < control1.length; i++) {
                        if (control1[i] > 0) {
                            resultadoFiltro1.add(parentSets.get(i));
                        }
                    }
                    this.parentSets = resultadoFiltro1;
                    break;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    private void filtrarPorPadres() {
        try {
            switch (filtro) {
                case "incluir":
                    List<ParentSetNodo> resultadoFiltro = new ArrayList<>();
                    List<ElementoAtributoGrafo> listPadresSelec = new ArrayList<>();
                    Integer[] control = new Integer[parentSets.size()];
                    for (int i = 0; i < control.length; i++) {
                        control[i] = 0;
                    }

                    for (String padre : seleccionPadres.split(";")) {
                        String[] auxPadre = padre.split(":");
                        String nombrePadre = "";

                        for (SelectItem itemvalorPadre : listaPadres) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                                nombrePadre = itemvalorPadre.getLabel();
                            }
                        }
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
                        String valorPadre = "";
                        for (SelectItem itemvalorPadre : auxValorPadre) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                                valorPadre = itemvalorPadre.getLabel();
                            }
                        }
                        ElementoAtributoGrafo oPadre = new ElementoAtributoGrafo();
                        oPadre.setNombreNodo(nombrePadre);
                        oPadre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
                        oPadre.setValorElemento(valorPadre);

                        listPadresSelec.add(oPadre);
                    }
                    for (int j = 0; j < parentSets.size(); j++) {
                        ParentSetNodo parentSet = parentSets.get(j);
                        for (ElementoAtributoGrafo oPadre : parentSet.getPadres()) {
                            for (int i = 0; i < listPadresSelec.size(); i++) {
                                ElementoAtributoGrafo oPadreSelecc = listPadresSelec.get(i);
                                if (oPadre.getNumNodo() == oPadreSelecc.getNumNodo() && oPadre.getValorElemento().equals(oPadreSelecc.getValorElemento())) {
                                    control[j]++;
                                }
                            }

                        }
                    }
                    for (int i = 0; i < control.length; i++) {
                        if (control[i] == listPadresSelec.size()) {
                            resultadoFiltro.add(parentSets.get(i));
                        }
                    }
                    this.parentSets = resultadoFiltro;
                    break;
                case "excluir":
                    List<ParentSetNodo> resultadoFiltro1 = new ArrayList<>();
                    List<ElementoAtributoGrafo> listPadresSelec1 = new ArrayList<>();
                    Integer[] control1 = new Integer[parentSets.size()];
                    for (int i = 0; i < control1.length; i++) {
                        control1[i] = 0;
                    }

                    for (String padre : seleccionPadres.split(";")) {
                        String[] auxPadre = padre.split(":");
                        String nombrePadre = "";

                        for (SelectItem itemvalorPadre : listaPadres) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                                nombrePadre = itemvalorPadre.getLabel();
                            }
                        }
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
                        String valorPadre = "";
                        for (SelectItem itemvalorPadre : auxValorPadre) {
                            if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                                valorPadre = itemvalorPadre.getLabel();
                            }
                        }
                        ElementoAtributoGrafo oPadre = new ElementoAtributoGrafo();
                        oPadre.setNombreNodo(nombrePadre);
                        oPadre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
                        oPadre.setValorElemento(valorPadre);

                        listPadresSelec1.add(oPadre);
                    }
                    for (int j = 0; j < parentSets.size(); j++) {
                        ParentSetNodo parentSet = parentSets.get(j);
                        for (ElementoAtributoGrafo oPadre : parentSet.getPadres()) {
                            for (int i = 0; i < listPadresSelec1.size(); i++) {
                                ElementoAtributoGrafo oPadreSelecc = listPadresSelec1.get(i);
                                if (oPadre.getNumNodo() == oPadreSelecc.getNumNodo() && oPadre.getValorElemento().equals(oPadreSelecc.getValorElemento())) {
                                    control1[j]++;
                                }
                            }

                        }
                    }
                    for (int i = 0; i < control1.length; i++) {
                        if (control1[i] > 0) {
                            resultadoFiltro1.add(parentSets.get(i));
                        }
                    }
                    this.parentSets = resultadoFiltro1;

                    break;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    private void filtrarPorNodoPadre() {
        try {
            switch (filtro) {
                case "incluir":

                    List<ParentSetNodo> resultadoFiltro = new ArrayList<>();
                    String[] auxPadre = this.seleccionPadres.split(":");
                    String nombrePadre = "";
                    for (SelectItem itemvalorPadre : listaPadres) {
                        if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[0].trim()))) {
                            nombrePadre = itemvalorPadre.getLabel();
                        }
                    }
                    List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayes);
                    String valorPadre = "";
                    for (SelectItem itemvalorPadre : auxValorPadre) {
                        if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre[1].trim()))) {
                            valorPadre = itemvalorPadre.getLabel();
                        }
                    }
                    ElementoAtributoGrafo padre = new ElementoAtributoGrafo();
                    padre.setNombreNodo(nombrePadre);
                    padre.setNumNodo(Integer.parseInt(auxPadre[0].trim()));
                    padre.setValorElemento(valorPadre);

                    String valorAtt = "";
                    for (SelectItem itemvalorNodo : listaValorNodo) {
                        if (itemvalorNodo.getValue().equals(this.selectItemValorNodo)) {
                            valorAtt = itemvalorNodo.getLabel();
                        }
                    }

                    for (ParentSetNodo parentSet : this.parentSets) {
                        for (ElementoAtributoGrafo elementoPadre : parentSet.getPadres()) {
                            if (elementoPadre.getNombreNodo().equals(padre.getNombreNodo()) && elementoPadre.getValorElemento().equals(padre.getValorElemento()) && parentSet.getElementoNodo().getValorElemento().equals(valorAtt)) {
                                resultadoFiltro.add(parentSet);
                            }
                        }
                    }
                    this.parentSets = resultadoFiltro;
                    break;
                case "excluir":
                    List<ParentSetNodo> resultadoFiltro1 = new ArrayList<>();
                    String[] auxPadre1 = this.seleccionPadres.split(":");
                    String nombrePadre1 = "";
                    for (SelectItem itemvalorPadre : listaPadres) {
                        if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre1[0].trim()))) {
                            nombrePadre1 = itemvalorPadre.getLabel();
                        }
                    }
                    List<SelectItem> auxValorPadre1 = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre1[0].trim()), nombrePadre1, bayes);
                    String valorPadre1 = "";
                    for (SelectItem itemvalorPadre : auxValorPadre1) {
                        if (itemvalorPadre.getValue().equals(Integer.parseInt(auxPadre1[1].trim()))) {
                            valorPadre1 = itemvalorPadre.getLabel();
                        }
                    }
                    ElementoAtributoGrafo padre1 = new ElementoAtributoGrafo();
                    padre1.setNombreNodo(nombrePadre1);
                    padre1.setNumNodo(Integer.parseInt(auxPadre1[0].trim()));
                    padre1.setValorElemento(valorPadre1);

                    String valorAtt1 = "";
                    for (SelectItem itemvalorNodo : listaValorNodo) {
                        if (itemvalorNodo.getValue().equals(this.selectItemValorNodo)) {
                            valorAtt1 = itemvalorNodo.getLabel();
                        }
                    }

                    for (ParentSetNodo parentSet : this.parentSets) {
                        for (ElementoAtributoGrafo elementoPadre : parentSet.getPadres()) {
                            if (elementoPadre.getNombreNodo().equals(padre1.getNombreNodo()) && elementoPadre.getValorElemento().equals(padre1.getValorElemento()) && parentSet.getElementoNodo().getValorElemento().equals(valorAtt1)) {
                                resultadoFiltro1.add(parentSet);
                            }
                        }
                    }
                    this.parentSets = resultadoFiltro1;
                    break;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }

    private void filtrarPorProbabilidad() {
        Predicate<ParentSetNodo> predicate;
        switch (this.indiceProb) {
            case 1:
                predicate = ParentSetNodo -> ParentSetNodo.getProbabilidad() > this.valorProb;
                parentSets.removeIf(predicate);
                break;
            case 2:
                predicate = ParentSetNodo -> ParentSetNodo.getProbabilidad() < this.valorProb;
                parentSets.removeIf(predicate);
                break;
        }

    }

    public void valoresPorDefecto() {
        this.selectItemPadre = -1;
        this.selectItemValorPadre = -1;
        this.selectItemValorNodo = -1;
        this.filtro = "incluir";
        this.orden = "defaut";
        this.collapsed = true;
        this.seleccionPadres = "";
        this.listaPadresAgregados.clear();
        this.indiceProb = -1;
        this.valorProb = 0;

    }

    public void ordenarDataset() {
        switch (this.orden) {
            case "ascendente":
                parentSets.sort(Comparator.comparing(ParentSetNodo::getProbabilidad));
                break;
            case "descendente":
                parentSets.sort(Comparator.comparing(ParentSetNodo::getProbabilidad).reversed());
                break;
        }

    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        Phrase phrase = new Phrase();
//        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//        String logo = externalContext.getRealPath("") + File.separator + "resources" + File.separator + "images" + File.separator + "medico.png";
//        pdf.add(Image.getInstance(logo));
        for (SelectItem item : listAtributosDataSet) {

            if (item.getValue().equals(this.clase)) {
                phrase.add("- Clase de la red bayesiana: " + item.getDescription() + "\n");
            }
            if (item.getLabel().equals(parentSets.get(0).getElementoNodo().getNombreNodo())) {
                phrase.add("- Pregunta de análisis:" + item.getDescription() + "\n");
//                for (SelectItem valorNodo : listaValorNodo) {
//                    if (valorNodo.getValue().equals(this.selectItemValorNodo)) {
//                        phrase.add("=> " + valorNodo.getDescription() + "\n");
            }
//                }

        }
        if (listaPadres.size() > 0) {
            phrase.add("- Preguntas relacionadas "+ "\n");
            int count=0;
            for (SelectItem item : this.listaPadres)                 
                if (item.getDescription() != null) {
                    count++;
                    phrase.add(count+"- "+item.getDescription() + "\n");
                }
            }

        pdf.add(phrase);
//                    
    }

    public void limpiarPadreSelec() {
        this.seleccionPadres = "";
        this.listaPadresAgregados.clear();
    }

    public List<ParentSetNodo> getParentSets() {
        return parentSets;
    }

    public void setParentSets(List<ParentSetNodo> parentSets) {
        this.parentSets = parentSets;
    }

    public int getN_columnas() {
        return n_columnas;
    }

    public void setN_columnas(int n_columnas) {
        this.n_columnas = n_columnas;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public ParentSetNodo getSelectParentSet() {
        return selectParentSet;
    }

    public void setSelectParentSet(ParentSetNodo selectParentSet) {
        this.selectParentSet = selectParentSet;
    }

    public String getIdNodo() {
        return idNodo;
    }

    public void setIdNodo(String idNodo) {
        this.idNodo = idNodo;
    }

    public void prueba() {
        this.idNodo = this.idNodo.toUpperCase();
    }

    public String getNodosDataSet() {
        return nodosDataSet;
    }

    public void setNodosDataSet(String nodosDataSet) {
        this.nodosDataSet = nodosDataSet;
    }

    public String getArcosDataSet() {
        return arcosDataSet;
    }

    public void setArcosDataSet(String arcosDataSet) {
        this.arcosDataSet = arcosDataSet;
    }

    public List<SelectItem> getListAtributosDataSet() {
        return listAtributosDataSet;
    }

    public void setListAtributosDataSet(List<SelectItem> listAtributosDataSet) {
        this.listAtributosDataSet = listAtributosDataSet;
    }

    public int getClase() {
        return clase;
    }

    public void setClase(int clase) {
        this.clase = clase;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public List<SelectItem> getListaPadres() {
        return listaPadres;
    }

    public void setListaPadres(List<SelectItem> listaPadres) {
        this.listaPadres = listaPadres;
    }

    public int getSelectItemPadre() {
        return selectItemPadre;
    }

    public void setSelectItemPadre(int selectItemPadre) {
        this.selectItemPadre = selectItemPadre;
    }

    public List<SelectItem> getListaValorNodo() {
        return listaValorNodo;
    }

    public void setListaValorNodo(List<SelectItem> listaValorNodo) {
        this.listaValorNodo = listaValorNodo;
    }

    public int getSelectItemValorNodo() {
        return selectItemValorNodo;
    }

    public void setSelectItemValorNodo(int selectItemValorNodo) {
        this.selectItemValorNodo = selectItemValorNodo;
    }

    public List<SelectItem> getListaValorPadre() {
        return listaValorPadre;
    }

    public void setListaValorPadre(List<SelectItem> listaValorPadre) {
        this.listaValorPadre = listaValorPadre;
    }

    public int getSelectItemValorPadre() {
        return selectItemValorPadre;
    }

    public void setSelectItemValorPadre(int selectItemValorPadre) {
        this.selectItemValorPadre = selectItemValorPadre;
    }

    public String getSeleccionPadres() {
        return seleccionPadres;
    }

    public void setSeleccionPadres(String seleccionPadres) {
        this.seleccionPadres = seleccionPadres;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public SelectItem getClaseSelec() {
        return claseSelec;
    }

    public void setClaseSelec(SelectItem claseSelec) {
        this.claseSelec = claseSelec;
    }

    public String getPresicion() {
        return presicion;
    }

    public void setPresicion(String presicion) {
        this.presicion = presicion;
    }

    public int getIndiceProb() {
        return indiceProb;
    }

    public void setIndiceProb(int indiceProb) {
        this.indiceProb = indiceProb;
    }

    public double getValorProb() {
        return valorProb;
    }

    public void setValorProb(double valorProb) {
        this.valorProb = valorProb;
    }

}
