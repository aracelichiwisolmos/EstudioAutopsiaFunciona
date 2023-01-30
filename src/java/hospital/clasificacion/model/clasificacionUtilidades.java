package hospital.clasificacion.model;

import hospital.controlador.configuracionAppControler;
import hospital.datos.AccesoDatos;
import hospital.modelo.area;
import hospital.modelo.categoria;
import hospital.modelo.centroEstudio;
import hospital.modelo.grado;
import hospital.modelo.pregunta;
import hospital.modelo.respuesta;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;

public class clasificacionUtilidades {

    private String sUrl = null;
    private String sUsr = null;
    private String sPwd = null;
    private String sDriver = null;
    private configuracionAppControler config = null;
    private AccesoDatos oAD;

    public clasificacionUtilidades() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
        sUrl = params.get("urldb");
        sUsr = params.get("usrdb");
        sPwd = params.get("pwddb");
        sDriver = params.get("driverdb");
        config = new configuracionAppControler();
    }

    public Instances devuelveInstancias(String sQuery) throws Exception {
        Instances data;
        InstanceQuery query = new InstanceQuery();
        query.setDatabaseURL(sUrl);
        query.setUsername(sUsr);
        query.setPassword(sPwd);
        query.setQuery(sQuery);
        query.connectToDatabase();
        data = query.retrieveInstances();
        query.disconnectFromDatabase();
        return data;
    }

    public String clasifica(String texto, String query) throws Exception {
        FilteredClassifier oFilterClasifier = new FilteredClassifier();
        oFilterClasifier.setOptions(weka.core.Utils.splitOptions("weka.classifiers.meta.FilteredClassifier -F \"weka.filters.MultiFilter -F \\\"weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 1000 -prune-rate -1.0 -N 0 -L -stemmer weka.core.stemmers.NullStemmer -M 1 -O -tokenizer \\\\\\\"weka.core.tokenizers.WordTokenizer -delimiters \\\\\\\\\\\\\\\" \\\\\\\\\\\\\\\\r\\\\\\\\\\\\\\\\n\\\\\\\\\\\\\\\\t.,;:\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\'\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\"()?!\\\\\\\\\\\\\\\"\\\\\\\"\\\" -F \\\"weka.filters.supervised.attribute.AttributeSelection -E \\\\\\\"weka.attributeSelection.InfoGainAttributeEval \\\\\\\" -S \\\\\\\"weka.attributeSelection.Ranker -T 0.0 -N -1\\\\\\\"\\\"\" -W weka.classifiers.functions.MultilayerPerceptron -- -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a"));
        NominalToString nominalToString = new NominalToString();
        nominalToString.setOptions(weka.core.Utils.splitOptions("weka.filters.unsupervised.attribute.NominalToString -C first"));
        Instances data = devuelveInstancias(query);
        nominalToString.setInputFormat(data);
        Instances newData = Filter.useFilter(data, nominalToString);
        newData.setClassIndex(newData.numAttributes() - 1);
        Instance instance = devuelveInstance(newData, texto);
        oFilterClasifier.buildClassifier(newData);
        double predicted = oFilterClasifier.classifyInstance(instance);
        return newData.classAttribute().value((int) predicted);
    }

    public Instance devuelveInstance(Instances data, String texto) {
        Instance instance = new Instance(2);
        Attribute atributo = data.attribute("texto");
        instance.setValue(atributo, texto);
        instance.setDataset(data);
        return instance;
    }

    public void generarModelo(String sQuery, String options, String pathModelo) throws Exception {
        try {
            Instances data = devuelveInstancias(sQuery);
            FilteredClassifier filterClasifier = new FilteredClassifier();
            filterClasifier.setOptions(weka.core.Utils.splitOptions(options));
            NominalToString nominalToString = new NominalToString();
            nominalToString.setOptions(weka.core.Utils.splitOptions("weka.filters.unsupervised.attribute.NominalToString -C first"));
            nominalToString.setInputFormat(data);
            Instances newData = Filter.useFilter(data, nominalToString);
            newData.setClassIndex(newData.numAttributes() - 1);
            filterClasifier.buildClassifier(newData);
            guardarModelo(pathModelo, filterClasifier);
        } catch (Exception e) {
            throw e;
        }
    }

    public void guardarModelo(String pathModelo, FilteredClassifier filterClasifier) throws Exception {
        try {
            weka.core.SerializationHelper.write(pathModelo, filterClasifier);
        } catch (Exception e) {
            throw e;
        }
    }

    public FilteredClassifier cargarModelo(String pathModelo) throws Exception {
        try {
            return (FilteredClassifier) weka.core.SerializationHelper.read(pathModelo);
        } catch (Exception e) {
            throw e;
        }
    }

    public String clasificarNuevaInstancia(String texto, String pathModelo, String sQuery) throws Exception {
        try {
            Instances data = devuelveInstancias(sQuery);
            NominalToString nominalToString = new NominalToString();
            nominalToString.setOptions(weka.core.Utils.splitOptions("weka.filters.unsupervised.attribute.NominalToString -C first"));
            nominalToString.setInputFormat(data);
            Instances newData = Filter.useFilter(data, nominalToString);
            newData.setClassIndex(newData.numAttributes() - 1);
            FilteredClassifier filterClasifier = cargarModelo(pathModelo);
            Instance instance = devuelveInstance(newData, texto);
            double predicted = filterClasifier.classifyInstance(instance);
            return newData.classAttribute().value((int) predicted);

        } catch (Exception e) {
            throw e;
        }
    }

    public void obtenerGrafico() throws Exception {
        Instances data = devuelveInstancias("SELECT area, categoria, ult_grado, esc_med_gral, \n"
                + "       esc_esp, anios_prac, casos, hall_disc, hall_arb, hall_dem, mcc_aut, \n"
                + "       mcc_no_aut, rechazo_fam, no_hosp, per_sol_aut, med_aut, fmr_sol_aut, \n"
                + "       com_sug_op\n"
                + "  FROM vista_minable;");
        data.setClassIndex(0);
        BayesNet bayesnet = new BayesNet();
        String options = "-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        bayesnet.setOptions(weka.core.Utils.splitOptions(options));
        bayesnet.buildClassifier(data);
    }

    public String interpretaAtributo(String preguntaCorta, String valorRespuesta) throws Exception {
        String interpretacionAtributo = null;
        ArrayList resultado = null;
        try {
            String sQuery = "SELECT interpreta_atributo('" + preguntaCorta + "','" + valorRespuesta + "');";
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
                ArrayList vRowTemp = (ArrayList) resultado.get(0);

                interpretacionAtributo = ((String) vRowTemp.get(0));

            }
        } catch (Exception e) {
            throw e;
        }
        return interpretacionAtributo;
    }

    public List<String> datasetGrafo(Instances data) throws IOException, Exception {

        data.setClassIndex(0);
        BayesNet bayesnet = new BayesNet();
        String options = "-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        bayesnet.setOptions(weka.core.Utils.splitOptions(options));
        bayesnet.buildClassifier(data);
        String nodos = "";
        String arcos = "[";
        List<String> elementosGrafo = new ArrayList();
        for (int nodeIndex = 0; nodeIndex < bayesnet.getNrOfNodes(); nodeIndex++) {
            if (nodeIndex == 0) {
                nodos = "[";
            }
            if (nodeIndex < bayesnet.getNrOfNodes() - 1) {
                nodos += "{id: " + (nodeIndex + 1) + ", label: '" + bayesnet.getNodeName(nodeIndex) + "'},";
            } else {
                nodos += "{id: " + (nodeIndex + 1) + ", label: '" + bayesnet.getNodeName(nodeIndex) + "'}]";
            }
            for (int i = 0; i < bayesnet.getNrOfParents(nodeIndex); i++) {
                if (i == bayesnet.getNrOfParents(nodeIndex) - 1 && nodeIndex == bayesnet.getNrOfNodes() - 1) {
                    arcos += "{from: " + (bayesnet.getParent(nodeIndex, i) + 1) + ", to: " + (nodeIndex + 1) + ", arrows:'to'}]";
                } else {
                    arcos += "{from: " + (bayesnet.getParent(nodeIndex, i) + 1) + ", to: " + (nodeIndex + 1) + ", arrows:'to'},";
                }

            }

        }
        elementosGrafo.add(nodos);
        elementosGrafo.add(arcos);
        return elementosGrafo;
    }

    public BayesNet generarBayesNet(Instances data, int clase) throws Exception {
        try {

            data.setClassIndex(clase);
            BayesNet bayesnet = new BayesNet();
            String options = configuracionAppControler.getOptions_bayesNet();
            bayesnet.setOptions(weka.core.Utils.splitOptions(options));
            bayesnet.buildClassifier(data);
            return bayesnet;
        } catch (Exception e) {
            throw e;
        }
    }

    public dataSetGrafo datasetGrafo(BayesNet bayesnet) throws IOException, Exception {
        try {
            String nodos = "";
            String arcos = "[";
            for (int nodeIndex = 0; nodeIndex < bayesnet.getNrOfNodes(); nodeIndex++) {
               if (nodeIndex == 0) {
                    nodos = "[";
                }
                if (nodeIndex < bayesnet.getNrOfNodes() - 1) {
                    nodos += "{id: " + (nodeIndex + 1) + ", label: '" + bayesnet.getNodeName(nodeIndex) + "'},";
                } else {
                    nodos += "{id: " + (nodeIndex + 1) + ", label: '" + bayesnet.getNodeName(nodeIndex) + "'}]";
                }
                for (int i = 0; i < bayesnet.getNrOfParents(nodeIndex); i++) {
                    if(!arcos.equals("["))
                        arcos += ",";
                    arcos += "{from: " + (bayesnet.getParent(nodeIndex, i) + 1) + ", to: " + (nodeIndex + 1) + ", arrows:'to'}";
//                                   if (i == bayesnet.getNrOfParents(nodeIndex) - 1 && nodeIndex == bayesnet.getNrOfNodes() - 1) {
//                        arcos += "{from: " + (bayesnet.getParent(nodeIndex, i) + 1) + ", to: " + (nodeIndex + 1) + ", arrows:'to'}]";
//                    } else {
//                        arcos += "{from: " + (bayesnet.getParent(nodeIndex, i) + 1) + ", to: " + (nodeIndex + 1) + ", arrows:'to'},";
//                    }

                }
                

            }
            arcos +="]";
            return new dataSetGrafo(nodos, arcos);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<SelectItem> obtenerAtributosDataset(Instances data) throws Exception {
        pregunta oPregunta = new pregunta();
        try {
            List<SelectItem> atributos = new ArrayList();
            for (int i = 0; i < data.numAttributes(); i++) {
                String descCorta = data.attribute(i).name();
                String pregunta = oPregunta.selectPreguntaPorDescCorta(descCorta).getPregunta();
                SelectItem item = new SelectItem(i, data.attribute(i).name(), pregunta);
                atributos.add(item);
            }
            return atributos;
        } catch (Exception e) {
            throw e;
        }
    }

    public void guardarModelo(String pathModelo, BayesNet clasifier) throws Exception {
        try {
            weka.core.SerializationHelper.write(pathModelo, clasifier);
        } catch (Exception e) {
            throw e;
        }
    }

    public BayesNet cargarModeloBayes(String pathModelo) throws Exception {
        try {
            return (BayesNet) weka.core.SerializationHelper.read(pathModelo);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ParentSetNodo> obtenerTCP(int nodeIndex, BayesNet bayesnet, Instances data) {
        try {
            List<ParentSetNodo> listParentSet = new ArrayList();
            ParentSet x = bayesnet.getParentSet(nodeIndex);

            for (int j = 0; j < bayesnet.getParentCardinality(nodeIndex); j++) {
                int res = j;
                String vatt = "";
                List<ElementoAtributoGrafo> listElementosPadres = new ArrayList();
                for (int padre = x.getNrOfParents() - 1; padre >= 0; padre--) {
                    Attribute attribute = data.attribute(x.getParent(padre));
                    int va = res % attribute.numValues();

                    vatt = attribute.value(va) + "\t" + vatt;
                    res = res / attribute.numValues();

                    //creando propio
                    String valor = attribute.value(va);
                    String nombre = attribute.name();
                    int numNodo = x.getParent(padre);

                    ElementoAtributoGrafo elementoGrafo = new ElementoAtributoGrafo();
                    elementoGrafo.setNombreNodo(nombre);
                    elementoGrafo.setNumNodo(numNodo);
                    elementoGrafo.setValorElemento(valor);

                    listElementosPadres.add(elementoGrafo);
                }

                for (int k = 0; k < bayesnet.getCardinality(nodeIndex); k++) {

                    String valor = bayesnet.getNodeValue(nodeIndex, k);
                    String nombre = bayesnet.getNodeName(nodeIndex);
                    int numNodo = nodeIndex;
                    double probabilidad = bayesnet.getProbability(nodeIndex, j, k);

                    ElementoAtributoGrafo elementoGrafo = new ElementoAtributoGrafo();
                    elementoGrafo.setNombreNodo(nombre);
                    elementoGrafo.setNumNodo(numNodo);
                    elementoGrafo.setValorElemento(valor);

                    ParentSetNodo parentSet = new ParentSetNodo();
                    parentSet.setElementoNodo(elementoGrafo);
                    parentSet.setPadres(listElementosPadres);
                    parentSet.setProbabilidad((double) Math.round(probabilidad * 1000000d) / 1000000d);
                    parentSet.setCardinalidad_elementoNodo(bayesnet.getCardinality(nodeIndex));

                    listParentSet.add(parentSet);

                }

            }
            return listParentSet;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<SelectItem> obtenerPadres(int indexNodo, BayesNet bayesnet) throws Exception {
        List<SelectItem> listaPadres = new ArrayList<>();
        SelectItem item;
        int cantPadres;
        int indexPadre;
        String nomPadre;
        String descripcion;
        pregunta oPregunta = new pregunta();
        try {
            cantPadres = bayesnet.getNrOfParents(indexNodo);
            listaPadres.add(new SelectItem("-1", "Seleccionar..."));
            for (int i = 0; i < cantPadres; i++) {
                item = new SelectItem();
                indexPadre = bayesnet.getParent(indexNodo, i);
                nomPadre = bayesnet.getNodeName(indexPadre);
                item.setLabel(nomPadre);
                item.setValue(indexPadre);
                descripcion = oPregunta.selectPreguntaPorDescCorta(nomPadre).getPregunta();
                item.setDescription(descripcion);
                listaPadres.add(item);
            }

            return listaPadres;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<SelectItem> obtenerValoresNodo(int indexNodo, String nombreNodo, BayesNet bayesnet) throws Exception {
        List<SelectItem> listaPadres = new ArrayList<>();
        SelectItem item;
        int cardinalidadNodo;
        String valorNodo;
        try {

            listaPadres.add(new SelectItem(-1, "Seleccionar..."));

            if (indexNodo > -1) {
                cardinalidadNodo = bayesnet.getCardinality(indexNodo);
                for (int i = 0; i < cardinalidadNodo; i++) {
                    String resp;
                    item = new SelectItem();
                    valorNodo = bayesnet.getNodeValue(indexNodo, i);
                    item.setLabel(valorNodo);
                    item.setValue(i);
                    pregunta oPregunta = new pregunta();
                    respuesta oRespuesta = new respuesta();
                    int idPreg = oPregunta.selectPreguntaPorDescCorta(nombreNodo).getIdPregunta();
                    switch (idPreg) {
                        case 23:
                            area oArea = new area();
                            resp = oArea.buscaAreaPorDescCorta(valorNodo).getArea();
                            break;
                        case 24:
                            grado ogrado = new grado();
                            resp = ogrado.buscaGradoPorDescCorta(valorNodo).getGrado();
                            break;
                        case 25:
                            centroEstudio oCentroMG = new centroEstudio();
                            resp = oCentroMG.buscaCentroEstudioPorDescCorta(valorNodo).getNombreCentro();
                            break;
                        case 26:
                            centroEstudio oCentroEsp = new centroEstudio();
                            resp = oCentroEsp.buscaCentroEstudioPorDescCorta(valorNodo).getNombreCentro();
                            break;
                        case 27:
                            categoria oCategoria = new categoria();
                            resp = oCategoria.buscaCategoriaPorDescCorta(valorNodo).getCategoria();
                            break;
                        default:
                            resp = oRespuesta.respuestasPorPregunta_DescCorta(idPreg, valorNodo).getRespuesta();
                            break;
                    }
                    item.setDescription(resp);

                    listaPadres.add(item);
                }
            }

            return listaPadres;
        } catch (Exception e) {
            throw e;
        }
    }

    public void actualizarModeloPregAbiertas() throws Exception {
        try {
            Integer[] preguntasAbiertas = {6, 7, 13};
            for (int preg : preguntasAbiertas) {

                String options = null;
                String pathModel = null;
                String sQuery = null;

                if (preg == 6) {
                    options = config.getOptions_mcc_aut();
                    pathModel = config.getPahtModel_mcc_aut();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=14;";
                }
                if (preg == 7) {
                    options = config.getOptions_mcc_no_aut();
                    pathModel = config.getPahtModel_mcc_no_aut();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=16;";
                }
                if (preg == 13) {
                    options = config.getOptions_com_op_sug();
                    pathModel = config.getPahtModel_com_op_sug();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=22;";
                }
                generarModelo(sQuery, options, pathModel);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
