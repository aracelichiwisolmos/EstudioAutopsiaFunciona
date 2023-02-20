package hospital.asociacion.model;

import hospital.controlador.configuracionAppControler;
import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import weka.associations.AbstractAssociator;
import weka.associations.Apriori;
import weka.associations.AprioriItemSet;
import weka.associations.FPGrowth;
import weka.associations.FPGrowth.AssociationRule;
import weka.associations.ItemSet;
import weka.associations.PredictiveApriori;
import weka.associations.Tertius;
import weka.associations.tertius.Rule;
import weka.associations.tertius.SimpleLinkedList;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

public class asociacionUtilidades {

    private String sUrl = null;
    private String sUsr = null;
    private String sPwd = null;
    private String sDriver = null;
    private configuracionAppControler config = null;
    private Apriori apriori = null;
    private FPGrowth fPGrowth = null;
    private PredictiveApriori predictive = null;
    private Tertius tertius = null;
    private AccesoDatos oAD;

    public asociacionUtilidades() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
        sUrl = params.get("urldb");
        sUsr = params.get("usrdb");
        sPwd = params.get("pwddb");
        sDriver = params.get("driverdb");
        config = new configuracionAppControler();
        apriori = new Apriori();
        fPGrowth = new FPGrowth();
        predictive = new PredictiveApriori();
        tertius = new Tertius();
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

    public List<reglas> obterenReglasApriori(Instances data, int cantReglas, double minSoporte, double minConfianza) throws Exception {

        try {
            List<reglas> reglas = null;
            FastVector[] reglasApriori;
            int numReglas;
            reglas oReglas;
            Instances instances;

            apriori.setLowerBoundMinSupport(minSoporte);
            apriori.setMinMetric(minConfianza);
            apriori.setNumRules(cantReglas);
            data.setClassIndex(data.numAttributes() - 1);
            apriori.buildAssociations(data);
            instances = apriori.getInstancesNoClass();
            reglasApriori = apriori.getAllTheRules();
            numReglas = reglasApriori[0].size();

            if (numReglas > 0) {
                reglas = new ArrayList();

                for (int i = 0; i < numReglas; i++) {

                    oReglas = new reglas();
                    double soporte;
                    double confianza;

                    AprioriItemSet antecedentes = (AprioriItemSet) reglasApriori[0].elementAt(i);
                    AprioriItemSet consecuentes = (AprioriItemSet) reglasApriori[1].elementAt(i);

                    int[] itemsAntecedentes = antecedentes.items();
                    int[] itemsConsecuente = consecuentes.items();

                    elementoRegla[] oElementosRegla;
                    List<elementoRegla> auxiliar = new ArrayList();

                    for (int j = 0; j < itemsAntecedentes.length; j++) {
                        if (antecedentes.itemAt(j) != -1) {
                            Attribute attribute = instances.attribute(j);
                            int k = antecedentes.itemAt(j);
                            elementoRegla oElement = new elementoRegla();
                            oElement.setNombreColumna(attribute.name());
                            oElement.setValor(attribute.value(k));
                            auxiliar.add(oElement);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setPrecedente(auxiliar.toArray(oElementosRegla));
                    auxiliar.clear();

                    for (int n = 0; n < itemsConsecuente.length; n++) {
                        if (consecuentes.itemAt(n) != -1) {
                            Attribute attribute = instances.attribute(n);
                            int k = consecuentes.itemAt(n);
                            elementoRegla ele = new elementoRegla();
                            ele.setNombreColumna(attribute.name());
                            ele.setValor(attribute.value(k));
                            auxiliar.add(ele);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setConsecuente(auxiliar.toArray(oElementosRegla));

                    confianza = (double) reglasApriori[2].elementAt(i);
                    int soporteConsecuente = consecuentes.support();
                    int filas = data.numInstances();
                    soporte = (double) soporteConsecuente / filas;

                    oReglas.setConfianza(confianza);
                    oReglas.setSoporte(soporte);
                    reglas.add(oReglas);
                }

            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<reglas> obterenReglasApriori(Instances data, String pathModelo) throws Exception {

        try {
            List<reglas> reglas = null;
            FastVector[] reglasApriori;
            int numReglas;
            reglas oReglas;
            Instances instances;

            apriori = (Apriori) weka.core.SerializationHelper.read(pathModelo);
            instances = apriori.getInstancesNoClass();
            //instances = LC.readModel(sUrl);
            reglasApriori = apriori.getAllTheRules();
            numReglas = reglasApriori[0].size();

            if (numReglas > 0) {
                reglas = new ArrayList();

                for (int i = 0; i < numReglas; i++) {

                    oReglas = new reglas();
                    double soporte;
                    double confianza;

                    AprioriItemSet antecedentes = (AprioriItemSet) reglasApriori[0].elementAt(i);
                    AprioriItemSet consecuentes = (AprioriItemSet) reglasApriori[1].elementAt(i);

                    int[] itemsAntecedentes = antecedentes.items();
                    int[] itemsConsecuente = consecuentes.items();

                    elementoRegla[] oElementosRegla;
                    List<elementoRegla> auxiliar = new ArrayList();

                    for (int j = 0; j < itemsAntecedentes.length; j++) {
                        if (antecedentes.itemAt(j) != -1) {
                            Attribute attribute = instances.attribute(j);
                            int k = antecedentes.itemAt(j);
                            elementoRegla oElement = new elementoRegla();
                            oElement.setNombreColumna(attribute.name());
                            oElement.setValor(attribute.value(k));
                            auxiliar.add(oElement);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setPrecedente(auxiliar.toArray(oElementosRegla));
                    auxiliar.clear();

                    for (int n = 0; n < itemsConsecuente.length; n++) {
                        if (consecuentes.itemAt(n) != -1) {
                            Attribute attribute = instances.attribute(n);
                            int k = consecuentes.itemAt(n);
                            elementoRegla ele = new elementoRegla();
                            ele.setNombreColumna(attribute.name());
                            ele.setValor(attribute.value(k));
                            auxiliar.add(ele);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setConsecuente(auxiliar.toArray(oElementosRegla));

                    confianza = (double) reglasApriori[2].elementAt(i);
                    int soporteConsecuente = consecuentes.support();
                    int filas = data.numInstances();
                    soporte = (double) soporteConsecuente / filas;

                    oReglas.setConfianza(confianza);
                    oReglas.setSoporte(soporte);
                    reglas.add(oReglas);
                }

            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<reglas> obterenReglasFPGrowth(Instances data, int cantReglas, double minSoporte, double minConfianza) throws Exception {
        List<reglas> reglas = null;
        List<AssociationRule> reglaFPGrowth;
        reglas oReglas;
        elementoRegla oElement;
        List<elementoRegla> listaElementosRegla;

        double soporte;
        double confianza;
        try {
            fPGrowth.setLowerBoundMinSupport(minSoporte);
            fPGrowth.setMinMetric(minConfianza);
            fPGrowth.setNumRulesToFind(cantReglas);
            fPGrowth.buildAssociations(data);
            reglaFPGrowth = fPGrowth.getAssociationRules();

            if (reglaFPGrowth != null) {
                reglas = new ArrayList();

                for (AssociationRule reglita : reglaFPGrowth) {
                    elementoRegla[] arrayElementosRegla;
                    listaElementosRegla = new ArrayList();
                    oReglas = new reglas();
                    soporte = (double) (reglita.getTotalSupport()) / data.numInstances();
                    confianza = reglita.getMetricValue();

                    String[] precedentes = reglita.getPremise().toString().replace("[", "").replace("]", "").split(",");
                    for (String precedente : precedentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = precedente.split("=");
                        oElement.setNombreColumna(oElementos[0]);
                        oElement.setValor(oElementos[1]);
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setPrecedente(listaElementosRegla.toArray(arrayElementosRegla));
                    listaElementosRegla.clear();

                    String[] consecuentes = reglita.getConsequence().toString().replace("[", "").replace("]", "").split(",");
                    for (String consecuente : consecuentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = consecuente.split("=");
                        oElement.setNombreColumna(oElementos[0]);
                        oElement.setValor(oElementos[1]);
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setConsecuente(listaElementosRegla.toArray(arrayElementosRegla));

                    oReglas.setSoporte(soporte);
                    oReglas.setConfianza(confianza);
                    reglas.add(oReglas);
                }
            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<reglas> obterenReglasFPGrowth(Instances data, String pathModelo) throws Exception {
        List<reglas> reglas = null;
        List<AssociationRule> reglaFPGrowth;
        reglas oReglas;
        elementoRegla oElement;
        List<elementoRegla> listaElementosRegla;

        double soporte;
        double confianza;
        try {
            fPGrowth = (FPGrowth) weka.core.SerializationHelper.read(pathModelo);
            reglaFPGrowth = fPGrowth.getAssociationRules();

            if (reglaFPGrowth != null) {
                reglas = new ArrayList();

                for (AssociationRule reglita : reglaFPGrowth) {
                    elementoRegla[] arrayElementosRegla;
                    listaElementosRegla = new ArrayList();
                    oReglas = new reglas();
                    soporte = (double) (reglita.getTotalSupport()) / data.numInstances();
                    confianza = reglita.getMetricValue();

                    String[] precedentes = reglita.getPremise().toString().replace("[", "").replace("]", "").split(",");
                    for (String precedente : precedentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = precedente.split("=");
                        oElement.setNombreColumna(oElementos[0]);
                        oElement.setValor(oElementos[1]);
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setPrecedente(listaElementosRegla.toArray(arrayElementosRegla));
                    listaElementosRegla.clear();

                    String[] consecuentes = reglita.getConsequence().toString().replace("[", "").replace("]", "").split(",");
                    for (String consecuente : consecuentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = consecuente.split("=");
                        oElement.setNombreColumna(oElementos[0]);
                        oElement.setValor(oElementos[1]);
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setConsecuente(listaElementosRegla.toArray(arrayElementosRegla));

                    oReglas.setSoporte(soporte);
                    oReglas.setConfianza(confianza);
                    reglas.add(oReglas);
                }
            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<reglas> obterenReglasPredictiveApriori(Instances data, int cantReglas) throws Exception {
        try {
            List<reglas> reglas = null;
            FastVector[] reglasApriori;
            int numReglas;
            reglas oReglas;
            Instances instances;

            predictive.setNumRules(cantReglas);
            data.setClassIndex(data.numAttributes() - 1);
            predictive.buildAssociations(data);
            instances = predictive.getInstancesNoClass();
            reglasApriori = predictive.getAllTheRules();
            numReglas = reglasApriori[0].size();

            if (numReglas > 0) {
                reglas = new ArrayList();

                for (int i = 0; i < numReglas; i++) {

                    oReglas = new reglas();
                    double soporte;
                    double confianza;

                    ItemSet antecedentes = (ItemSet) reglasApriori[0].elementAt(i);
                    ItemSet consecuentes = (ItemSet) reglasApriori[1].elementAt(i);

                    int[] itemsAntecedentes = antecedentes.items();
                    int[] itemsConsecuente = consecuentes.items();

                    elementoRegla[] oElementosRegla;
                    List<elementoRegla> auxiliar = new ArrayList();

                    for (int j = 0; j < itemsAntecedentes.length; j++) {
                        if (antecedentes.itemAt(j) != -1) {
                            Attribute attribute = instances.attribute(j);
                            int k = antecedentes.itemAt(j);
                            elementoRegla oElement = new elementoRegla();
                            oElement.setNombreColumna(attribute.name());
                            oElement.setValor(attribute.value(k));
                            auxiliar.add(oElement);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setPrecedente(auxiliar.toArray(oElementosRegla));
                    auxiliar.clear();

                    for (int n = 0; n < itemsConsecuente.length; n++) {
                        if (consecuentes.itemAt(n) != -1) {
                            Attribute attribute = instances.attribute(n);
                            int k = consecuentes.itemAt(n);
                            elementoRegla ele = new elementoRegla();
                            ele.setNombreColumna(attribute.name());
                            ele.setValor(attribute.value(k));
                            auxiliar.add(ele);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setConsecuente(auxiliar.toArray(oElementosRegla));

                    confianza = (double) reglasApriori[2].elementAt(i);
                    int soporteConsecuente = consecuentes.support();
                    int filas = data.numInstances();
                    soporte = (double) soporteConsecuente / filas;

                    oReglas.setConfianza(confianza);
                    oReglas.setSoporte(soporte);
                    reglas.add(oReglas);
                }

            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<reglas> obterenReglasPredictiveApriori(Instances data, String pathModelo) throws Exception {
        try {
            List<reglas> reglas = null;
            FastVector[] reglasApriori;
            int numReglas;
            reglas oReglas;
            Instances instances;

            instances = predictive.getInstancesNoClass();
            predictive = (PredictiveApriori) weka.core.SerializationHelper.read(pathModelo);
            reglasApriori = predictive.getAllTheRules();
            numReglas = reglasApriori[0].size();

            if (numReglas > 0) {
                reglas = new ArrayList();

                for (int i = 0; i < numReglas; i++) {

                    oReglas = new reglas();
                    double soporte;
                    double confianza;

                    ItemSet antecedentes = (ItemSet) reglasApriori[0].elementAt(i);
                    ItemSet consecuentes = (ItemSet) reglasApriori[1].elementAt(i);

                    int[] itemsAntecedentes = antecedentes.items();
                    int[] itemsConsecuente = consecuentes.items();

                    elementoRegla[] oElementosRegla;
                    List<elementoRegla> auxiliar = new ArrayList();

                    for (int j = 0; j < itemsAntecedentes.length; j++) {
                        if (antecedentes.itemAt(j) != -1) {
                            Attribute attribute = instances.attribute(j);
                            int k = antecedentes.itemAt(j);
                            elementoRegla oElement = new elementoRegla();
                            oElement.setNombreColumna(attribute.name());
                            oElement.setValor(attribute.value(k));
                            auxiliar.add(oElement);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setPrecedente(auxiliar.toArray(oElementosRegla));
                    auxiliar.clear();

                    for (int n = 0; n < itemsConsecuente.length; n++) {
                        if (consecuentes.itemAt(n) != -1) {
                            Attribute attribute = instances.attribute(n);
                            int k = consecuentes.itemAt(n);
                            elementoRegla ele = new elementoRegla();
                            ele.setNombreColumna(attribute.name());
                            ele.setValor(attribute.value(k));
                            auxiliar.add(ele);
                        }
                    }
                    oElementosRegla = new elementoRegla[auxiliar.size()];
                    oReglas.setConsecuente(auxiliar.toArray(oElementosRegla));

                    confianza = (double) reglasApriori[2].elementAt(i);
                    int soporteConsecuente = consecuentes.support();
                    int filas = data.numInstances();
                    soporte = (double) soporteConsecuente / filas;

                    oReglas.setConfianza(confianza);
                    oReglas.setSoporte(soporte);
                    reglas.add(oReglas);
                }

            }
            return reglas;
        } catch (Exception e) {
            throw e;
        }

    }

    public List<reglas> obterenReglasTertius(Instances data, int cantReglas, int literales, double minConfianza) throws Exception {
        List<reglas> reglas = null;
        Rule regla;
        String[] partesRegla;
        String sPrecedentes;
        String sConsecuentes;
        elementoRegla oElement;
        reglas oReglas;
        List<elementoRegla> listaElementosRegla;
        SimpleLinkedList.LinkedListIterator reglasTertius;
        int count = 0;

        try {
            String[] options = new String[8];
            options[0] = "-I"; //valores faltantes
            options[1] = "1";
            options[2] = "-L"; //literales
            options[3] = String.valueOf(literales);
            options[4] = "-F"; // frecuencia
            options[5] = String.valueOf(minConfianza);
            options[6] = "-K";// cant. reglas
            options[7] = String.valueOf(cantReglas);

            tertius.setOptions(options);
            tertius.buildAssociations(data);

            reglasTertius = tertius.getResults().iterator();

            if (reglasTertius != null) {
                reglas = new ArrayList<>();
                while (reglasTertius.hasNext()) {
                    regla = (Rule) reglasTertius.next();
                    partesRegla = regla.toString().split("==>");
                    sPrecedentes = partesRegla[0].trim();
                    sConsecuentes = partesRegla[1].trim();
                    listaElementosRegla = new ArrayList();
                    oReglas = new reglas();
                    elementoRegla[] arrayElementosRegla;

                    if (!(sPrecedentes.equals("FALSE") || sPrecedentes.equals("TRUE")
                            || sConsecuentes.equals("FALSE") || sConsecuentes.equals("TRUE"))) {

                        count++;
                        String[] precedentes = sPrecedentes.split(" or ");
                        for (String precedente : precedentes) {
                            oElement = new elementoRegla();
                            String[] oElementos = precedente.split("=");
                            oElement.setNombreColumna(oElementos[0].trim());
                            oElement.setValor(oElementos[1].trim());
                            listaElementosRegla.add(oElement);
                        }
                        arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                        oReglas.setPrecedente(listaElementosRegla.toArray(arrayElementosRegla));
                        listaElementosRegla.clear();

                        String[] consecuentes = sConsecuentes.split(" or ");
                        for (String consecuente : consecuentes) {
                            oElement = new elementoRegla();
                            String[] oElementos = consecuente.split("=");
                            oElement.setNombreColumna(oElementos[0].trim());
                            oElement.setValor(oElementos[1].trim());
                            listaElementosRegla.add(oElement);
                        }
                        arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                        oReglas.setConsecuente(listaElementosRegla.toArray(arrayElementosRegla));
                        oReglas.setSoporte(regla.getObservedFrequency());
                        oReglas.setConfianza(regla.getConfirmation());
                        reglas.add(oReglas);
                    }
                }
            }
            if (count == 0) {
                reglas = null;
            }

            return reglas;

        } catch (Exception e) {
            throw e;
        }

    }

    public List<reglas> obterenReglasTertius(Instances data, String pathModelo) throws Exception {
        List<reglas> reglas = null;
        Rule regla;
        String[] partesRegla;
        String sPrecedentes;
        String sConsecuentes;
        elementoRegla oElement;
        reglas oReglas;
        List<elementoRegla> listaElementosRegla;
        SimpleLinkedList.LinkedListIterator reglasTertius;

        try {

            tertius = (Tertius) weka.core.SerializationHelper.read(pathModelo);
            reglasTertius = tertius.getResults().iterator();

            if (reglasTertius != null) {
                reglas = new ArrayList<>();
                while (reglasTertius.hasNext()) {
                    regla = (Rule) reglasTertius.next();
                    partesRegla = regla.toString().split("==>");
                    sPrecedentes = partesRegla[0];
                    sConsecuentes = partesRegla[1];
                    listaElementosRegla = new ArrayList();
                    oReglas = new reglas();
                    elementoRegla[] arrayElementosRegla;

                    String[] precedentes = sPrecedentes.split(" or ");
                    for (String precedente : precedentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = precedente.split("=");
                        oElement.setNombreColumna(oElementos[0].trim());
                        oElement.setValor(oElementos[1].trim());
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setPrecedente(listaElementosRegla.toArray(arrayElementosRegla));
                    listaElementosRegla.clear();

                    String[] consecuentes = sConsecuentes.split(" or ");
                    for (String consecuente : consecuentes) {
                        oElement = new elementoRegla();
                        String[] oElementos = consecuente.split("=");
                        oElement.setNombreColumna(oElementos[0].trim());
                        oElement.setValor(oElementos[1].trim());
                        listaElementosRegla.add(oElement);
                    }
                    arrayElementosRegla = new elementoRegla[listaElementosRegla.size()];
                    oReglas.setConsecuente(listaElementosRegla.toArray(arrayElementosRegla));
                    oReglas.setSoporte(regla.getObservedFrequency());
                    oReglas.setConfianza(regla.getConfirmation());
                    reglas.add(oReglas);

                }
            }
            return reglas;

        } catch (Exception e) {
            throw e;
        }

    }

    public List<String> describeReglas(List<reglas> reglas) {
        String resultado;
        List<String> lReglas = null;
        if (reglas != null) {
            lReglas = new ArrayList();
            for (reglas oRegla : reglas) {
                resultado = "";
                for (int i = 0; i < oRegla.getPrecedente().length; i++) {

                    if (i < oRegla.getPrecedente().length - 1) {
                        resultado += oRegla.getPrecedente()[i].getNombreColumna() + "=" + oRegla.getPrecedente()[i].getValor() + ", ";
                    } else {
                        resultado += oRegla.getPrecedente()[i].getNombreColumna() + "=" + oRegla.getPrecedente()[i].getValor() + "==> ";
                    }
                }

                for (int i = 0; i < oRegla.getConsecuente().length; i++) {
                    if (i < oRegla.getConsecuente().length - 1) {
                        resultado += oRegla.getConsecuente()[i].getNombreColumna() + "=" + oRegla.getConsecuente()[i].getValor() + ", ";
                    } else {
                        resultado += oRegla.getConsecuente()[i].getNombreColumna() + "=" + oRegla.getConsecuente()[i].getValor();
                    }
                }

                resultado += " Confianza: " + (int) oRegla.getConfianza() + "%";
                resultado += " Soporte: " + (int) oRegla.getSoporte() + "%";
                lReglas.add(resultado);
            }

        }
        return lReglas;
    }

    public List<String> interpretaReglas(List<reglas> reglas) throws Exception {
        String resultado;
        List<String> lReglas = null;
        if (reglas != null) {
            lReglas = new ArrayList();
            for (reglas oRegla : reglas) {
                resultado = "El " + oRegla.getConfianza() + "% de los encuestados que ";

                for (int i = 0; i < oRegla.getPrecedente().length; i++) {

                    if (i < oRegla.getPrecedente().length - 1) {
                        if (i == oRegla.getPrecedente().length - 2) {
                            resultado += interpretaAtributo(oRegla.getPrecedente()[i].getNombreColumna(), oRegla.getPrecedente()[i].getValor()) + " y ";
                        } else {
                            resultado += interpretaAtributo(oRegla.getPrecedente()[i].getNombreColumna(), oRegla.getPrecedente()[i].getValor()) + ", ";
                        }
                    } else {
                        resultado += interpretaAtributo(oRegla.getPrecedente()[i].getNombreColumna(), oRegla.getPrecedente()[i].getValor()) + ", también ";
                    }
                }

                for (int i = 0; i < oRegla.getConsecuente().length; i++) {
                    if (i < oRegla.getConsecuente().length - 1) {
                        if (i == oRegla.getConsecuente().length - 2) {
                            resultado += interpretaAtributo(oRegla.getConsecuente()[i].getNombreColumna(), oRegla.getConsecuente()[i].getValor()) + " y ";
                        } else {
                            resultado += interpretaAtributo(oRegla.getConsecuente()[i].getNombreColumna(), oRegla.getConsecuente()[i].getValor()) + ", ";
                        }
                    } else {
                        resultado += interpretaAtributo(oRegla.getConsecuente()[i].getNombreColumna(), oRegla.getConsecuente()[i].getValor()) + ". ";
                    }
                }

                resultado += "Esta regla aparece con una frecuencia del " + oRegla.getSoporte() + "%.";

                lReglas.add(resultado);
            }

        }
        return lReglas;
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

    public atributo descripcionAtributo(String atributo) throws Exception {
        atributo oAtributo = new atributo();
        oAtributo.descripciones(atributo);
        return oAtributo;
    }

    public int cantInstancias_dadoColumna(String column) throws Exception {
        int cantidad = 0;
        ArrayList resultado = null;
        try {
            String sQuery = "SELECT count(*) FROM matriz_binaria where " + column + "='S';";
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
                cantidad = (((Double) vRowTemp.get(0)).intValue());

            }

            return cantidad;
        } catch (Exception e) {
            throw e;
        }
    }

    public int cantInstanciasC() throws Exception {
        int cantidad = 0;
        ArrayList resultado = null;
        try {
            String sQuery = "SELECT count(*)  FROM matriz_binaria;";
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

                cantidad = (((Double) vRowTemp.get(0)).intValue());

            }
            return cantidad;
        } catch (Exception e) {
            throw e;
        }
    }

    public void actualizarDatasetD() throws Exception {
        ArrayList resultado = null;
        ArrayList resultadoSG = null;
        try {
            String sQuery = "SELECT crear_vista_minable();";
            String sQuerySG = "SELECT crear_vista_minable2();";//PARA SUBGRUPOS
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    resultado = oAD.ejecutarConsulta(sQuery);
                    resultadoSG = oAD.ejecutarConsulta(sQuerySG);
                    System.out.println("se actualizó DatasetD");                    
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                resultado = oAD.ejecutarConsulta(sQuery);
                resultadoSG = oAD.ejecutarConsulta(sQuerySG);
                 System.out.println("___se actualizó DatasetD");
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    

    public void actualizarDatasetC() throws Exception {
        try {
            String sQuery = "SELECT genera_matriz();";
            String sQuerySG = "SELECT genera_matriz3();";//PARA SUBGRUPOS
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {                    
                    oAD.ejecutarConsulta(sQuery);
                    oAD.ejecutarConsulta(sQuerySG);                     
                     System.out.println("se actualizó DatasetC");
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.ejecutarConsulta(sQuerySG);
                System.out.println("___se actualizó DatasetC");
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Apriori obterenModeloApriori(Instances data, int cantReglas, double minSoporte, double minConfianza) throws Exception {

        try {
            apriori.setLowerBoundMinSupport(minSoporte);
            apriori.setMinMetric(minConfianza);
            apriori.setNumRules(cantReglas);
            data.setClassIndex(data.numAttributes() - 1);
            apriori.buildAssociations(data);
            return apriori;
        } catch (Exception e) {
            throw e;
        }

    }

    public FPGrowth obterenModeloFPGrowth(Instances data, int cantReglas, double minSoporte, double minConfianza) throws Exception {

        try {

            fPGrowth.setLowerBoundMinSupport(minSoporte);
            fPGrowth.setMinMetric(minConfianza);
            fPGrowth.setNumRulesToFind(cantReglas);
            fPGrowth.buildAssociations(data);
            return fPGrowth;
        } catch (Exception e) {
            throw e;
        }

    }

    public PredictiveApriori obterenModeloPredictiveApriori(Instances data, int cantReglas) throws Exception {

        try {

            predictive.setNumRules(cantReglas);
            data.setClassIndex(data.numAttributes() - 1);
            predictive.buildAssociations(data);
            return predictive;
        } catch (Exception e) {
            throw e;
        }

    }

    public Tertius obterenModeloTertius(Instances data, int cantReglas, int literales, double minConfianza) throws Exception {

        try {

            String[] options = new String[8];
            options[0] = "-I"; //valores faltantes
            options[1] = "1";
            options[2] = "-L"; //literales
            options[3] = String.valueOf(literales);
            options[4] = "-F"; // frecuencia
            options[5] = String.valueOf(minConfianza);
            options[6] = "-K";// cant. reglas
            options[7] = String.valueOf(cantReglas);

            tertius.setOptions(options);
            tertius.buildAssociations(data);
            return tertius;
        } catch (Exception e) {
            throw e;
        }

    }

    public void guardarModelo(AbstractAssociator modelo, String rutaDestino) throws Exception {
        weka.core.SerializationHelper.write(rutaDestino, modelo);
    }

}
