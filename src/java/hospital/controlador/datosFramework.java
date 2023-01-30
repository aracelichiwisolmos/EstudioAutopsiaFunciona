/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.controlador;

import framework.GUI.GUI;
import static framework.GUI.GUI.readXML;
import framework.GUI.Model;
import framework.exceptions.IllegalActionException;
import framework.utils.Utils;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.InstanceSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mscay
 */
public class datosFramework {

    private Vector<String> algorithms = new Vector<>();
    //private Vector<JPanel> paramPanels = new Vector<>();
    //private DefaultComboBoxModel modelo;
    private Document doc;
    private String actual_fully_qualified_class;
    private String preds;
    HashMap<String, Double> qualityMeasures = new HashMap<>();
    File lastDirectory;
    String rutaTra = "D:\\Documentos\\Tesis\\3er semestre\\Conjuntos de datos\\Nuevo conjunto\\InformaciónEncuestas2.dat";

    public datosFramework() {
        doc = readXML("algorithms.xml");
        preds = "";
        File f = new File("options.txt");

        if (f.exists()) {
            try {
                BufferedReader bf = new BufferedReader(new FileReader(f));
                lastDirectory = new File(bf.readLine());
            } catch (IOException ex) {
                lastDirectory = new File(System.getProperty("user.home"));
            }
        } else {
            lastDirectory = new File(System.getProperty("user.home"));
        }

        //   initComponents();
        //inicalizar medidas de calidad hash map
        resetMeasures();

        // Agrega los nombres de los algoritmos
        NodeList nodes = doc.getElementsByTagName("algorithm");
        for (int i = 0;
                i < nodes.getLength();
                i++) {
            Element node = (Element) nodes.item(i);

            //Analiza el nombre del método
            algorithms.add(node.getElementsByTagName("name").item(0).getTextContent());
        }

        // // Establece los primeros parámetros del algoritmo
        // addParamsToPanel(doc, 0, ParametersPanel);
        //addParamsToPanel(doc, 0, ParametersPanel1);
        // Agrega la lista de algoritmos a la lista de anuncios, establece el primero como predeterminado
        //modelo = new DefaultComboBoxModel(algorithms);
        //AlgorithmList.setModel(modelo);
        //AlgorithmList1.setModel(modelo);
    }

    /**
     * Restablecer / inicializar el mapa hash de medidas de calidad
     */
    private void resetMeasures() {
        qualityMeasures.put("WRACC", 0.0);  // Normalized Unusualness
        qualityMeasures.put("NVAR", 0.0);  // Number of variables
        qualityMeasures.put("NRULES", 0.0);  // Number of rules
        qualityMeasures.put("GAIN", 0.0);  // Information Gain
        qualityMeasures.put("CONF", 0.0);   // Confidence
        qualityMeasures.put("GR", 0.0);     // Growth Rate
        qualityMeasures.put("TPR", 0.0);    // True positive rate
        qualityMeasures.put("FPR", 0.0);    // False positive rate
        qualityMeasures.put("SUPDIFF", 0.0);     // Support Diference
        qualityMeasures.put("FISHER", 0.0); // Fishers's test
        qualityMeasures.put("HELLINGER", 0.0); // Hellinger Distance
        qualityMeasures.put("ACC", 0.0); // Accuracy
        qualityMeasures.put("AUC", 0.0); // ROC Curve
    }

    @SuppressWarnings("unchecked")

    private void LearnButtonActionPerformed() {
        //  ExecutionInfoLearn.setEditable(true);
        // Reads the parameters of the user
        //appendToPane(ExecutionInfoLearn, "Reading parameters and files...", Color.BLUE);

        //HashMap<String, String> params = readParameters(ParametersPanel);
        HashMap<String, String> paramsiEPMiner = new HashMap<>();
        InstanceSet training = new InstanceSet();
        InstanceSet test = new InstanceSet();
        Object minSoporte = null;
        paramsiEPMiner.put("Minimum Support", String.valueOf(minSoporte));
        Object growthRate = null;
        paramsiEPMiner.put("Minimum Growth Rate", String.valueOf(growthRate));
        Object chiSquaret = null;
        paramsiEPMiner.put("Minimum Chi-Squared", String.valueOf(chiSquaret));
        // modelo
        // Dinamically calls the method learn of the method: VERY INTERESTING FUNCTION!
        try {
            // if (SaveModelCheckbox.isSelected() && rutaModel.getText().equals("")) {
            //   throw new framework.exceptions.IllegalActionException("ERROR: You must specify a path to save the model.");
            //}
            if (rutaTra.equals("")) {
                throw new framework.exceptions.IllegalActionException("ERROR: You must specify a training file.");
            }

            // Execute the task in background to update the text area.
            //Execute the task in background to update the text area
            SwingWorker worker;
            worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    // Reads training and test file
                    Attributes.clearAll();
                    try {
                        training.readSet(rutaTra, true);
                    } catch (DatasetException | HeaderFormatException | NullPointerException ex) {
                        throw new IllegalActionException("ERROR: Format error on training file.");
                    }
                    training.setAttributesAsNonStatic();
//                    if (!rutaTst.getText().equals("")) {
//                        test.readSet(rutaTst.getText(), false);
//                        test.setAttributesAsNonStatic();
//                    }

                    //appendToPane(ExecutionInfoLearn, "Executing " + (String) AlgorithmList.getSelectedItem() + " algorithm... (This may take a while)", Color.BLUE);
                    System.out.println("Executing " + (String) algorithms.get(0));
                    // System.out.println("Executing " + (String) AlgorithmList.getSelectedItem());

                    //First: instantiate the class selected with the fully qualified name   //Primero: instanciar la clase seleccionada con el nombre completo
                    Object newObject;
                    Class clase = Class.forName(actual_fully_qualified_class);
                    newObject = clase.newInstance();
                    ((Model) newObject).patterns = new ArrayList<>();
                    ((Model) newObject).patternsFilteredMinimal = new ArrayList<>();
                    ((Model) newObject).patternsFilteredMaximal = new ArrayList<>();
                    ((Model) newObject).patternsFilteredByMeasure = new ArrayList<>();

                    // Second: get the argument class
                    Class[] args = new Class[2];
                    args[0] = InstanceSet.class;
                    args[1] = HashMap.class;

                    System.out.println("Learning Model...");
                    // Third: Get the method 'learn' of the class and invoke it. (cambiar "new InstanceSet" por el training)
                    clase.getMethod("learn", args).invoke(newObject, training, paramsiEPMiner);
                    //appendToPane(ExecutionInfoLearn, "Filtering patterns and calculating descriptive measures...", Color.BLUE);
                    System.out.println("Filtering patterns and calculating descriptive measures...");

                    // Get learned patterns, filter, and calculate measures for training Obtenga patrones aprendidos, filtre y calcule medidas para el entrenamiento
                    ArrayList<HashMap<String, Double>> Measures = Utils.calculateDescriptiveMeasures(training, ((Model) newObject).getPatterns(), true);
//Filtre los patrones, devolviendo las medidas de calidad promedio para cada conjunto de patrones
                    // Filter the patterns, returning the average quality measures for each set of patterns
                    ArrayList<HashMap<String, Double>> filterPatterns = Utils.filterPatterns((Model) newObject, "CONF", 0.6f);
                    for (int i = 0; i < filterPatterns.size(); i++) {
                        // Adds to Masures to write later the average results in the file.
                        Measures.add(filterPatterns.get(i));
                    }

                    // Call predict method for ACC and AUC for training
                    //appendToPane(ExecutionInfoLearn, "Calculate precision for training...", Color.BLUE);
                    System.out.println("Calculating precision for training...");
                    args = new Class[1];
                    args[0] = InstanceSet.class;
                    String[][] predictionsTra = (String[][]) clase.getMethod("predict", args).invoke(newObject, training);
                    Utils.calculatePrecisionMeasures(predictionsTra, training, training, Measures);

                    // Save training measures in a file.
                    System.out.println("Save results in a file...");
                    //appendToPane(ExecutionInfoLearn, "Save result in a file...", Color.BLUE);
                    Utils.saveMeasures(new File(rutaTra).getParentFile(), (Model) newObject, Measures, true, 0);
                    // appendToPane(ExecutionInfoLearn, "Done", Color.BLUE);
                    System.out.println("Done learning model.");

                    // If there is a test set call the method "predict" to make the test phase.
//                    if (!rutaTst.getText().equals("")) {
//                        // Calculate descriptive measures for test
//                        appendToPane(ExecutionInfoLearn, "Testing instances...", Color.BLUE);
//                        System.out.println("Testing instances...");
//
//                        // Calculate test measures for unfiltered and filtered patterns
//                        Measures = Utils.calculateDescriptiveMeasures(test, ((Model) newObject).getPatterns(), false);
//                        Measures.add(Utils.calculateDescriptiveMeasures(test, ((Model) newObject).getPatternsFilteredMinimal(), false).get(0));
//                        Measures.add(Utils.calculateDescriptiveMeasures(test, ((Model) newObject).getPatternsFilteredMaximal(), false).get(0));
//                        Measures.add(Utils.calculateDescriptiveMeasures(test, ((Model) newObject).getPatternsFilteredByMeasure(), false).get(0));
//
//                        args = new Class[1];
//                        args[0] = InstanceSet.class;
//// Call predict method
//                        String[][] predictions = (String[][]) clase.getMethod("predict", args).invoke(newObject, test);
//
//                        // Calculate predictions
//                        Utils.calculatePrecisionMeasures(predictions, test, training, Measures);
//                        // Save Results
//                        //Utils.saveResults(new File(rutaTst.getText()).getParentFile(), Measures.get(0), Measures.get(1), Measures.get(2), 1);
//                        Utils.saveMeasures(new File(rutaTst.getText()).getParentFile(), (Model) newObject, Measures, false, 0);
//                        appendToPane(ExecutionInfoLearn, "Done. Results of quality measures saved in " + new File(rutaTst.getText()).getParentFile().getAbsolutePath(), Color.BLUE);
//                        System.out.println("Done. Results of quality measures saved in " + new File(rutaTst.getText()).getParentFile().getAbsolutePath());
//
//                    }
                    // Invoke saveModel method if neccesary
//                    if (SaveModelCheckbox.isSelected()) {
//                        appendToPane(ExecutionInfoLearn, "Saving Model...", Color.BLUE);
//                        args = new Class[1];
//                        args[0] = String.class;
//                        clase.getMethod("saveModel", args).invoke(newObject, rutaModel.getText());
//                        appendToPane(ExecutionInfoLearn, "Done", Color.BLUE);
//                    }
                    //   ExecutionInfoLearn.setEditable(false);
                    return null;

                }

                protected void done() {
                    try {
                        get();

                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUI.class
                                .getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    } catch (ExecutionException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IllegalActionException) {
                            System.out.println(((IllegalActionException) cause).getReason());
                        } else {
                            System.out.println("An unexpected error has ocurred: " + cause.toString());
                        }
                        ex.printStackTrace();
                    }
                }

            };

            worker.execute();

        } catch (IllegalActionException ex) {
            System.out.println(ex.getReason());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

//    private void AlgorithmListActionPerformed() {
//        // TODO add your handling code here:
//        int value = AlgorithmList.getSelectedIndex();
//        if (value != -1 || value > paramPanels.size()) {
//            addParamsToPanel(doc, value, ParametersPanel);
//        }
//    }
    public static Document readXML(String path) {
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            return doc;

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(GUI.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *      * Poner en el panel de parámetros los parámetros del algoritmo
     * seleccionado      * *      * @param doc Un objeto DOM con las
     * definiciones de algoritmos y algorthims (Un      * objeto DOM con los
     * algoritmos y las variables de parámetros)      * @param index La posición
     * del algoritmo en el archivo (posición del      * algoritmo en el archivo)
     *      
     */
//    private void addParamsToPanel() {
//        //Get algorithm
//        NodeList nodes = doc.getElementsByTagName("algorithm");
//        Element node = (Element) nodes.item(1);
//        actual_fully_qualified_class = node.getElementsByTagName("class").item(0).getTextContent();
//        //Clear the actual panel
//     //   ParametersPanel.removeAll();
//
//        /* Spinners:
//            jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(6.0f) ,  // Valor por defecto
//            Float.valueOf(0.0f),   // minimo
//            Float.valueOf(100.0f), // Maximo
//            Float.valueOf(0.5f)));  Paso */
//        // Now, get the parameters
//        try {
//            NodeList parameters = node.getElementsByTagName("parameter");
//            for (int j = 0; j < parameters.getLength(); j++) {
//                Element nodeParam = (Element) parameters.item(j);
//                switch (nodeParam.getElementsByTagName("type").item(0).getTextContent()) {
//                    case "integer":
//                       // ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
//                        Integer defect,
//                         min,
//                         max;
//                        defect = Integer.parseInt(nodeParam.getElementsByTagName("default").item(0).getTextContent());
//                        Element domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
//                        min = Integer.parseInt(domain.getElementsByTagName("min").item(0).getTextContent());
//                        max = Integer.parseInt(domain.getElementsByTagName("max").item(0).getTextContent());
//                        //ParametersPanel.add(new JSpinner(new SpinnerNumberModel(defect.intValue(), min.intValue(), max.intValue(), 1)));
//                        break;
//
//                    case "real":
//                       // ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
//                        Float defecto,
//                         mini,
//                         maxi;
//                        defecto = Float.parseFloat(nodeParam.getElementsByTagName("default").item(0).getTextContent());
//                        domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
//                        mini = Float.parseFloat(domain.getElementsByTagName("min").item(0).getTextContent());
//                        maxi = Float.parseFloat(domain.getElementsByTagName("max").item(0).getTextContent());
//                        //ParametersPanel.add(new JSpinner(new SpinnerNumberModel(defecto.floatValue(), mini.floatValue(), maxi.floatValue(), (float) 0.01)));
//                        break;
//
//                    case "nominal":
//                        //ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
//                        domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
//                        Vector<String> values = new Vector<>();
//                        NodeList list = domain.getElementsByTagName("item");
//                        for (int i = 0; i < list.getLength(); i++) {
//                            values.add(list.item(i).getTextContent());
//                        }
//                        //DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(values);
//                        // Sets the default element:
//                      //  comboBoxModel.setSelectedItem(values.get(Integer.parseInt(nodeParam.getElementsByTagName("default").item(0).getTextContent()) - 1));
//                        //JComboBox combo = new JComboBox(comboBoxModel);
//                        //ParametersPanel.add(combo);
//
//                }
//
//            }
//
//            // Update the panel
////            ParametersPanel.setLayout(new GridLayout(parameters.getLength(), 2));
////            ParametersPanel.validate();
////            ParametersPanel.repaint();
//            //jScrollPane2.setViewportView(ParametersPanel);
//
//        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
//            // If algorithms.xml has an error disable all the interface.
//           // this.setEnabled(false);
//            //ExecutionInfoLearn.setText("");
//            //ExecutionInfoLearn.setText("FATAL ERROR: algorithms.xml has an error, interface blocked.");
//        }
//    }
    /**
     *       * Lee los parámetros del algoritmo especificado por el usuario en
     * el       * panel de parámetros       * *       * @return A HashMap
     * <String, String> con clave el nombre del parámetro y       * valor el
     * valor del parámetro.      
     */
    private void addParamsToPanel() {
        //Get algorithm
        NodeList nodes = doc.getElementsByTagName("algorithm");
        Element node = (Element) nodes.item(1);
        actual_fully_qualified_class = node.getElementsByTagName("class").item(0).getTextContent();
        //Clear the actual panel
        //ParametersPanel.removeAll();

        /* Spinners:
            jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(6.0f) ,  // Valor por defecto
            Float.valueOf(0.0f),   // minimo
            Float.valueOf(100.0f), // Maximo
            Float.valueOf(0.5f)));  Paso */
        // Now, get the parameters
        try {
            NodeList parameters = node.getElementsByTagName("parameter");
            for (int j = 0; j < parameters.getLength(); j++) {
                Element nodeParam = (Element) parameters.item(j);
                switch (nodeParam.getElementsByTagName("type").item(0).getTextContent()) {
                    case "integer":
                        //ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
                        Integer defect,
                         min,
                         max;
                        defect = Integer.parseInt(nodeParam.getElementsByTagName("default").item(0).getTextContent());
                        Element domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
                        min = Integer.parseInt(domain.getElementsByTagName("min").item(0).getTextContent());
                        max = Integer.parseInt(domain.getElementsByTagName("max").item(0).getTextContent());
                        //ParametersPanel.add(new JSpinner(new SpinnerNumberModel(defect.intValue(), min.intValue(), max.intValue(), 1)));
                        break;

                    case "real":
                        //ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
                        Float defecto,
                         mini,
                         maxi;
                        defecto = Float.parseFloat(nodeParam.getElementsByTagName("default").item(0).getTextContent());
                        domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
                        mini = Float.parseFloat(domain.getElementsByTagName("min").item(0).getTextContent());
                        maxi = Float.parseFloat(domain.getElementsByTagName("max").item(0).getTextContent());
                        //   ParametersPanel.add(new JSpinner(new SpinnerNumberModel(defecto.floatValue(), mini.floatValue(), maxi.floatValue(), (float) 0.01)));
                        break;

                    case "nominal":
                        //ParametersPanel.add(new JLabel(nodeParam.getElementsByTagName("name").item(0).getTextContent() + ": "));
                        domain = (Element) nodeParam.getElementsByTagName("domain").item(0);
                        Vector<String> values = new Vector<>();
                        NodeList list = domain.getElementsByTagName("item");
                        for (int i = 0; i < list.getLength(); i++) {
                            values.add(list.item(i).getTextContent());
                        }
                    //DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(values);
                    // Sets the default element:
                    //comboBoxModel.setSelectedItem(values.get(Integer.parseInt(nodeParam.getElementsByTagName("default").item(0).getTextContent()) - 1));
                    //JComboBox combo = new JComboBox(comboBoxModel);
                    //ParametersPanel.add(combo);

                }

            }

            // Update the panel
//            ParametersPanel.setLayout(new GridLayout(parameters.getLength(), 2));
//            ParametersPanel.validate();
//            ParametersPanel.repaint();
            //jScrollPane2.setViewportView(ParametersPanel);
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
            // If algorithms.xml has an error disable all the interface.
            // this.setEnabled(false);
            System.out.println("");
            System.out.println("FATAL ERROR: algorithms.xml has an error, interface blocked.");
        }
    }

    /**
     *      * Lee los parámetros del algoritmo especificado por el usuario en el
     *      * panel de parámetros      * *      
     *
     * @return A HashMap
     * <String, String> con clave el nombre del parámetro y valor el valor del
     * parámetro.      
     */
//    private HashMap<String, String> readParameters() {
//        String key = "";
//        HashMap<String, String> parameters = new HashMap<>();
//        for (int i = 0; i < ParametersPanel.getComponentCount(); i++) {
//            if (ParametersPanel.getComponent(i) instanceof JComboBox) {
//                // Cast the component and add the value of the JLabel substracting the ': ' elements
//                JComboBox element = (JComboBox) ParametersPanel.getComponent(i);
//                parameters.put(key.substring(0, key.length() - 2), (String) element.getSelectedItem());
//            } else if (ParametersPanel.getComponent(i) instanceof JSpinner) {
//                JSpinner element = (JSpinner) ParametersPanel.getComponent(i);
//                try {
//                    parameters.put(key.substring(0, key.length() - 2), Integer.toString((Integer) element.getValue()));
//                } catch (java.lang.ClassCastException ex) {
//                    parameters.put(key.substring(0, key.length() - 2), Double.toString((Double) element.getValue()));
//                }
//            } else if (ParametersPanel.getComponent(i) instanceof JLabel) {
//                JLabel element = (JLabel) ParametersPanel.getComponent(i);
//                key = element.getText();
//            }
//        }
//        return parameters;
//    }
    private void updateMeasuresCV(HashMap<String, Double> measures) {
        if (measures.containsKey("WRACC")) {
            qualityMeasures.put("WRACC", qualityMeasures.get("WRACC") + measures.get("WRACC"));
        }
        if (measures.containsKey("NVAR")) {
            qualityMeasures.put("NVAR", qualityMeasures.get("NVAR") + measures.get("NVAR"));
        }
        if (measures.containsKey("NRULES")) {
            qualityMeasures.put("NRULES", qualityMeasures.get("NRULES") + measures.get("NRULES"));
        }
        if (measures.containsKey("GAIN")) {
            qualityMeasures.put("GAIN", qualityMeasures.get("GAIN") + measures.get("GAIN"));
        }
        if (measures.containsKey("CONF")) {
            qualityMeasures.put("CONF", qualityMeasures.get("CONF") + measures.get("CONF"));
        }
        if (measures.containsKey("GR")) {
            qualityMeasures.put("GR", qualityMeasures.get("GR") + measures.get("GR"));
        }
        if (measures.containsKey("TPR")) {
            qualityMeasures.put("TPR", qualityMeasures.get("TPR") + measures.get("TPR"));
        }
        if (measures.containsKey("FPR")) {
            qualityMeasures.put("FPR", qualityMeasures.get("FPR") + measures.get("FPR"));
        }
        if (measures.containsKey("SUPDIFF")) {
            qualityMeasures.put("SUPDIFF", qualityMeasures.get("SUPDIFF") + measures.get("SUPDIFF"));
        }
        if (measures.containsKey("FISHER")) {
            qualityMeasures.put("FISHER", qualityMeasures.get("FISHER") + measures.get("FISHER"));
        }
        if (measures.containsKey("HELLINGER")) {
            qualityMeasures.put("HELLINGER", qualityMeasures.get("HELLINGER") + measures.get("HELLINGER"));
        }
        if (measures.containsKey("ACC")) {
            qualityMeasures.put("ACC", qualityMeasures.get("ACC") + measures.get("ACC"));
        }
        if (measures.containsKey("AUC")) {
            qualityMeasures.put("AUC", qualityMeasures.get("AUC") + measures.get("AUC"));
        }

    }
}
