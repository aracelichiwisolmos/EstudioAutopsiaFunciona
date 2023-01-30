package hospital.controlador;

import hospital.clasificacion.model.ElementoAtributoGrafo;
import hospital.clasificacion.model.ParentSetNodo;
import hospital.clasificacion.model.clasificacionUtilidades;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

@ManagedBean(name = "oResultadoBayesJb")
@ViewScoped
public class resultadoBayesControler implements Serializable {

    private BayesNet bayesnet;
    private clasificacionUtilidades oClasificaUtilidades;
    private String nodosDataSet;
    private String arcosDataSet;
    private List<ParentSetNodo> parentSets;
    private Instances data;
    private String query_D;
    private String idNodo;
    private String orden;
    private String filtro;
    private List<SelectItem> listaPadres;
    private int selectItemPadre;
    private List<SelectItem> listaValorNodo;
    private int selectItemValorNodo;
    private List<SelectItem> listaValorPadre;
    private int selectItemValorPadre;
    private String seleccionPadres;
    private List<Integer> listaPadresAgregados;
    private List<SelectItem> listAtributosDataSet;
    private boolean collapsed;
    private int indiceProb;
    private double valorProb;

    public resultadoBayesControler() throws Exception {
        this.query_D = "SELECT area, categoria, ult_grado, esc_med_gral, \n"
                + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                + "       com_sug_op\n"
                + "  FROM vista_minable;";
        this.oClasificaUtilidades = new clasificacionUtilidades();
        this.bayesnet = oClasificaUtilidades.cargarModeloBayes(configuracionAppControler.pahtModel_d_BayesNet);
        this.nodosDataSet = oClasificaUtilidades.datasetGrafo(this.bayesnet).getNodos();
        this.arcosDataSet = oClasificaUtilidades.datasetGrafo(this.bayesnet).getArcos();
        this.data = oClasificaUtilidades.devuelveInstancias(query_D);
        this.orden = "default";
        this.filtro = "incluir";
        this.listAtributosDataSet = oClasificaUtilidades.obtenerAtributosDataset(data);
        this.listaPadres = new ArrayList<>();
        this.selectItemPadre = -1;
        this.listaValorNodo = new ArrayList<>();
        this.selectItemValorNodo = -1;
        this.listaValorPadre = new ArrayList<>();
        this.selectItemValorPadre = -1;
        this.seleccionPadres = "";
        this.listaPadresAgregados = new ArrayList<>();
        this.collapsed = false;
        this.indiceProb = -1;
        this.valorProb = 0;

    }

    public void obtenerTCP() {
        try {
            int indexNodo = Integer.parseInt(this.idNodo) - 1;
            this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayesnet, data);
            String nombreNodo = parentSets.get(0).getElementoNodo().getNombreNodo();
            this.listaPadres = oClasificaUtilidades.obtenerPadres(indexNodo, bayesnet);
            this.listaValorNodo = oClasificaUtilidades.obtenerValoresNodo(indexNodo, nombreNodo, bayesnet);
            String nombrePadre = "";
            for (SelectItem itemvalorPadre : listaPadres) {
                if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                    nombrePadre = itemvalorPadre.getLabel();
                }
            }
            this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayesnet);
            valoresPorDefecto();
            this.collapsed = true;

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
        }
    }
    
     public void filtrar() throws Exception {
        filtrarElementos();
        if (this.indiceProb != -1 && this.valorProb > 0) {
            filtrarPorProbabilidad();
        }
        valoresPorDefecto();
    }


    public void filtrarElementos() throws Exception {

        int indexNodo = Integer.parseInt(this.idNodo) - 1;
        this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayesnet, data);
        this.listaPadres = oClasificaUtilidades.obtenerPadres(indexNodo, bayesnet);
        String nombreNodo = parentSets.get(0).getElementoNodo().getNombreNodo();
        this.listaValorNodo = oClasificaUtilidades.obtenerValoresNodo(indexNodo, nombreNodo, bayesnet);
        String nombrePadre = "";
        for (SelectItem itemvalorPadre : listaPadres) {
            if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                nombrePadre = itemvalorPadre.getLabel();
            }
        }
        this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayesnet);

        if (selectItemValorNodo != -1 && listaPadresAgregados.isEmpty()) {
            filtrarPorNodo();
            //valoresPorDefecto();
        } else if (selectItemValorNodo != -1 && listaPadresAgregados.size() > 1) {
            filtrarPorNodoPadres();
           // valoresPorDefecto();
        } else if (selectItemValorNodo != -1 && listaPadresAgregados.size() == 1) {
            filtrarPorNodoPadre();
            //valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.isEmpty()) {
            this.parentSets = oClasificaUtilidades.obtenerTCP(indexNodo, bayesnet, data);
            //valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.size() > 1) {
            filtrarPorPadres();
            //valoresPorDefecto();
        } else if (selectItemValorNodo == -1 && listaPadresAgregados.size() == 1) {
            filtrarPorPadre();
            //valoresPorDefecto();
        }
        this.collapsed = true;
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
            List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                        List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                    List<SelectItem> auxValorPadre = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre[0].trim()), nombrePadre, bayesnet);
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
                    List<SelectItem> auxValorPadre1 = oClasificaUtilidades.obtenerValoresNodo(Integer.parseInt(auxPadre1[0].trim()), nombrePadre1, bayesnet);
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
                predicate = ParentSetNodo -> ParentSetNodo.getProbabilidad()< this.valorProb;
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
        this.seleccionPadres = "";
        this.listaPadresAgregados.clear();
                this.indiceProb=-1;
        this.valorProb=0;
    }

    public void limpiarPadreSelec() {
        this.seleccionPadres = "";
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

    public void obtenerValoresPadre() throws Exception {
        String nombrePadre = "";
        for (SelectItem itemvalorPadre : listaPadres) {
            if (itemvalorPadre.getValue().equals(selectItemPadre)) {
                nombrePadre = itemvalorPadre.getLabel();
            }
        }
        this.listaValorPadre = oClasificaUtilidades.obtenerValoresNodo(selectItemPadre, nombrePadre, bayesnet);
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

    public List<ParentSetNodo> getParentSets() {
        return parentSets;
    }

    public void setParentSets(List<ParentSetNodo> parentSets) {
        this.parentSets = parentSets;
    }

    public String getIdNodo() {
        return idNodo;
    }

    public void setIdNodo(String idNodo) {
        this.idNodo = idNodo;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
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

    public List<Integer> getListaPadresAgregados() {
        return listaPadresAgregados;
    }

    public void setListaPadresAgregados(List<Integer> listaPadresAgregados) {
        this.listaPadresAgregados = listaPadresAgregados;
    }

    public List<SelectItem> getListAtributosDataSet() {
        return listAtributosDataSet;
    }

    public void setListAtributosDataSet(List<SelectItem> listAtributosDataSet) {
        this.listAtributosDataSet = listAtributosDataSet;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
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
