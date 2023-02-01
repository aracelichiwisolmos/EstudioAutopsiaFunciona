/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.subgrupos.model;
/**
 *
 * @author Araceli
 */

import framework.GUI.GUI;
import framework.GUI.Model;
import framework.exceptions.IllegalActionException;
import framework.utils.Utils;
import hospital.controlador.configuracionAppControler;
import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import hospital.epm.model.Reglas;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static jdk.nashorn.internal.objects.NativeString.replace;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.creators.DataFactory;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.quality.functions.BinomialQF;
import org.vikamine.kernel.subgroup.quality.functions.ChiSquareQF;
import org.vikamine.kernel.subgroup.quality.functions.WRAccQF;
import org.vikamine.kernel.subgroup.search.BSD;
import org.vikamine.kernel.subgroup.search.SDBeamSearch;
import org.vikamine.kernel.subgroup.search.SDMap;
import org.vikamine.kernel.subgroup.search.SDSimpleTask;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.target.SelectorTarget;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import weka.experiment.InstanceQuery;

public class subgruposUtilidades {

    private String sUrl = null;
    private String sUsr = null;
    private String sPwd = null;
    private String sDriver = null;
    private configuracionAppControler config = null;
    //private String ruta;

    private AccesoDatos oAD;
    private String actual_fully_qualified_class;
    private String algorithm;
    private List<String> algorithms;
    private int modeloSeleccionado;
    private String reglas;
    private List<ReglasSG> reglasSG;

    private ArrayList resultado;
    private String resultados;
    private String sQuery;

    Object im;
    String todo;

    public subgruposUtilidades() {//?
        this.algorithms = new ArrayList<>();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
        sUrl = params.get("urldb");
        sUsr = params.get("usrdb");
        sPwd = params.get("pwddb");
        sDriver = params.get("driverdb");
        config = new configuracionAppControler();
    
    }
/*
     public subgruposUtilidades(org.vikamine.kernel.data.Ontology ontology){
        //constructor with one argument of type org.vikamine.kernel.data.Ontology
    }
*/

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
    
    
    
    //--------------------------------------OBTENCIÓN DE LOS MODELOS------------------------------------------------

    
     public List<ReglasSG> obtenerModelo(String ruta, int claseSeleccionada, int etiquetaSeleccionada, int numeroSeleccionado, int reglasSeleccionadas, int medidaSeleccionada, int modeloSeleccionado) throws Exception {
        System.out.println("\nENTRÉ A 'obtenerModelo'\n");
         System.out.println("_________Obteniendo modelo...___________");
       List<ReglasSG> reglas = new ArrayList<>();
       
        System.out.println("_________RUTA RECIBIDA___________"+ruta);
        
       //creando una nueva instancia de la clase "Ontology" utilizando el método "createOntology" 
       //de la clase "DataFactory" pasando una ruta como parámetro.    
        Ontology onto = DataFactory.createOntology(new File(ruta));//recibe ruta
               
        System.out.println("Atributos en el conjunto de datos: " + onto.getNumAttributes());
        System.out.println("Instancias en conjunto de datos: " + onto.getNumInstances());

        //creando una nueva instancia de la clase "SDSimpleTask" 
        //y pasando la instancia de Ontology como argumento al constructor.
         SDSimpleTask task = new SDSimpleTask(onto);
        
         String str = Integer.toString(claseSeleccionada);
         String clase="H"+str;
         System.out.println(clase);

        //Establecemos objetivo o concepto (propiedad de interés)
        task.setTarget(new SelectorTarget(new DefaultSGSelector(onto, "centro_hospitalario", clase))); 

        List<String> attributeIDList = new ArrayList<String>();
        for (Attribute a : onto.getAttributes()) {
            attributeIDList.add(a.getId());
        }
        
        String[] attributeIDS = new String[]{};
        task.setIgnoreDefaultValues(false);
        task.setAttributes(attributeIDList.toArray(attributeIDS), true, 5);
        
         try {
             switch (medidaSeleccionada) {
                 case 1:
                     // establece la función de calidad:
                     task.setQualityFunction(new WRAccQF());
                     System.out.println("_________entro con la función  WRAccQF___________");
                     break;
                     
                 case 2:
                     task.setQualityFunction(new ChiSquareQF());
                     System.out.println("_________entro con la función  ChiSquareQF___________");
                     break;
                     
                 case 3:
                      task.setQualityFunction(new BinomialQF());
                      System.out.println("_________entro con la función  BinomialQF___________");
                     break;                     
             }
            
        }catch(Exception e) {
        e.printStackTrace();
        }
        
            try {
             switch (medidaSeleccionada) {
                 case 1:
                     // establece el algoritmo de búsqueda:
                     task.setMethodType(SDMap.class);
                     System.out.println("_________entro con el algoritmo SDMap___________");
                     break;

                 case 2:
                     task.setMethodType(BSD.class);
                     System.out.println("_________entro con el algoritmo BSD___________");
                     break;
                     
                 case 3:
                      task.setMethodType(SDBeamSearch.class);
                      System.out.println("_________entro con el algoritmo BeamSearch___________");
                     break;                     
             }
            
        }catch(Exception e) {
        e.printStackTrace();
        }
        
        

        //Establecemos restricciones a la búsqueda.
        task.setMaxSGCount(reglasSeleccionadas);
        System.out.println("_________num. reglas seleccionadas_________: "+reglasSeleccionadas);
        task.setMaxSGDSize(numeroSeleccionado);
         System.out.println("_________num. atributos seleccionados_________: "+numeroSeleccionado);

        // ejecutar tarea
        //obtengo subgrupos
        SGSet result = task.performSubgroupDiscovery();

        System.out.println("\nimprimiendo conjunto de resulatdos\n");
        System.out.println("=======");
        // imprimimos el resultado:
      for (SG sg : result.toSortedList(false)) {
        
            System.out.println(sg.getSGDescription().getSelectors().toString()+ " - "//buscar una alternativa 
                    + sg.getStatistics().getSubgroupSize() + "("
                    + sg.getQuality() + ")");
            
            // guardo todas las reglas generadas 
            ReglasSG r =new ReglasSG();//---
            r.setRegla(sg.getSGDescription().getSelectors().toString()+" - "+sg.getStatistics().getSubgroupSize()+"("+sg.getQuality()+ ")");//para poder iterar las reglas           
            r.setDescripcion(sg.getSGDescription().toString());
            r.setTamaño(sg.getStatistics().getSubgroupSize());
            r.setValFuncion(sg.getQuality());
             reglas.add(r);//_------
           
        }
        System.out.println("=======");
        
      
        
    return reglas;
}

//--------------Termina la obteción de los modelos---------------------------

    
     
         //--------------------------Aquí empieza la obtencion de reglas conjunto C------------------------
//Este método recibe la lista de reglas para poder interpretarlas
public List<String> obtenerReglasC1(int claseSeleccionada, int medidaSeleccionada,List<ReglasSG> reglasSGList) throws Exception, Throwable {
        ArrayList<String> interpretaciones = new ArrayList<>();
        // ReglasSG rsg = new ReglasSG();
        System.out.println("\nENTRÉ A 'ObtenerReglasC1'\n");

        System.out.println("____Reglas recibidas____: " + reglasSGList + "\n");

        try {
            System.out.println("Entro a en Reglas C---\n");
            int contador1 = 0;
            for (ReglasSG r : reglasSGList) {           
                //contador1++;

                String interpretacion = "Si los médicos   ";
                //String linea = ran.readLine();

                //System.out.println("Regla: "+contador1);
                int indice = r.getRegla().indexOf("-");
                String valorAntes = r.getRegla().substring(0, indice).trim();
                // System.out.println("Valor antes del caracter -: " + valorAntes);

                int indice1 = r.getRegla().indexOf("(");
                String valorEntre = r.getRegla().substring(indice + 1, indice1).trim();
                // System.out.println("Valor entre el caracter - y (: " + valorEntre);

                String valorDespues = r.getRegla().substring(indice1 + 1, r.getRegla().length() - 1).trim();
                // System.out.println("Valor después del caracter (: " + valorDespues);

                String predicados = valorAntes + "-" + valorEntre + "(" + valorDespues + ")";

             //   System.out.println("\npredicados: " + predicados + "\n\n\n");

                //los divide cuando encuentra una ,
                String[] e = predicados.split(",");

                for (int i = 0; i < e.length; i++) {
                    String[] g = e[i].split("=");                       
                    String column = g[0].trim().toLowerCase().replace("[", "").replace("]", "");                   
                    String value = g[1].trim().toUpperCase().split("-")[0].replace("[", "").replace("]", "");

                     //System.out.println("" + g);
                    AccesoDatos acc = new AccesoDatos();
                    ArrayList arr = null;
                    if (acc.conectar()) {
                        String[] splitValue = g[1].split("-");                       
                        String q = "SELECT antecedente FROM interpretacion WHERE descc_columna='" + column + "' AND valor_atributo='" + value + "'";
                      
                        arr = acc.ejecutarConsulta(q);
                        System.out.println("-"+q);
                        acc.desconectar();
                        //System.out.println("consulta res: "+arr);
                    }
                    if (arr != null && !arr.isEmpty()) {
                        interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";

                    }
                }
                String entonces = r.getRegla().substring(r.getRegla().indexOf("-") + 4);
                String str = Integer.toString(claseSeleccionada);
                String clase = "H" + str;

                switch (claseSeleccionada) {
                    case 1:
                        clase = "Hospital Regional de Río Blanco.";
                        break;
                    case 11:
                        clase = "Hospital General San Juan Bautista Tuxtepec.";
                        break;
                    case 12:
                        clase = "Hospital General de Zona 53.";
                        break;
                        default:
                       clase="Otro hospital";
                    }    
               String sonDe = clase;
                if (interpretacion.length() > 3) {
                    interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                }
                interpretacion += ", entonces son del hospital" + " " + sonDe;
                    DecimalFormat df = new DecimalFormat("#.####");
                    double deviacion = r.getValFuncion();
                    String truncatedValFuncion = df.format(deviacion);
                    
                    String medida = null;
                    
                    switch (medidaSeleccionada) {
                    case 1:
                        medida = "\nEsta regla tiene una presición relativa ponderada de: ";
                        break;
                    case 2:
                        medida = "\nEsta regla tiene una estadística Chi-cuadrada de: ";
                        break;
                    case 3:
                        medida = "\nEsta regla tiene una distribución binomial de: ";
                        break;
                        
                    }  
                interpretaciones.add(interpretacion + medida + truncatedValFuncion);//cambiar por el valor de la finción
            
            }

        } catch (IOException e) {            
        }
        return interpretaciones;
    }
     
     
           //--------------------------Aquí empieza la obtencion de reglas conjunto D------------------------
   public List<String> obtenerReglasD1(int claseSeleccionada, int medidaSeleccionada,List<ReglasSG> reglasSGList) throws Exception {
        ArrayList<String> interpretaciones = new ArrayList<>();
        // ReglasSG rsg = new ReglasSG();
        System.out.println("\nENTRÉ A 'ObtenerReglasD1'\n");

        System.out.println("____Reglas recibidas____: " + reglasSGList + "\n");

        try {
            System.out.println("Entro a en Reglas D---\n");
            int contador1 = 0;
            for (ReglasSG r : reglasSGList) {           
                //contador1++;

                String interpretacion = "Si los médicos   ";
                //String linea = ran.readLine();

                //System.out.println("Regla: "+contador1);
                int indice = r.getRegla().indexOf("-");
                String valorAntes = r.getRegla().substring(0, indice).trim();
                // System.out.println("Valor antes del caracter -: " + valorAntes);

                int indice1 = r.getRegla().indexOf("(");
                String valorEntre = r.getRegla().substring(indice + 1, indice1).trim();
                // System.out.println("Valor entre el caracter - y (: " + valorEntre);

                String valorDespues = r.getRegla().substring(indice1 + 1, r.getRegla().length() - 1).trim();
                // System.out.println("Valor después del caracter (: " + valorDespues);

                String predicados = valorAntes + "-" + valorEntre + "(" + valorDespues + ")";

             //   System.out.println("\npredicados: " + predicados + "\n\n\n");

                //los divide cuando encuentra una ,
                String[] e = predicados.split(",");

                for (int i = 0; i < e.length; i++) {
                    String[] g = e[i].split("=");                       
                    String column = g[0].trim().toLowerCase().replace("[", "").replace("]", "");                   
                    String value = g[1].trim().toLowerCase().split("-")[0].replace("[", "").replace("]", "");

                    // System.out.println("" + g);
                    AccesoDatos acc = new AccesoDatos();
                    ArrayList arr = null;
                    if (acc.conectar()) {
                        String[] splitValue = g[1].split("-");                       
                        String q = "SELECT antecedente FROM interpretacion WHERE descc_columna='" + column + "' AND valor_atributo='" + value + "'";
                      
                        arr = acc.ejecutarConsulta(q);
                        System.out.println("-"+q);                        
                        acc.desconectar();
                    }
                    if (arr != null && !arr.isEmpty()) {
                        interpretacion += ((ArrayList) arr.get(0)).get(0) + " y ";
 
                    }

                }
                  String entonces = r.getRegla().substring(r.getRegla().indexOf("-") + 4);
                  
                AccesoDatos acc = new AccesoDatos();
                ArrayList a = null;
                if (acc.conectar()) {
                    String q = "select antecedente from interpretacion where atributo='" + entonces.trim() + "'";
                    a = acc.ejecutarConsulta(q);
                    acc.desconectar();
                }
                String str = Integer.toString(claseSeleccionada);
                String clase = "H" + str;

                switch (claseSeleccionada) {
                    case 1:
                        clase = "Hospital Regional de Río Blanco.";
                        break;
                    case 11:
                        clase = "Hospital General San Juan Bautista Tuxtepec.";
                        break;
                    case 12:
                        clase = "Hospital General de Zona 53.";
                        break;
                        default:
                       clase="Otro hospital";
                    }    
               String sonDe = clase;
                if (interpretacion.length() > 3) {
                    interpretacion = interpretacion.substring(0, interpretacion.length() - 3);
                    
                }
                interpretacion += ", entonces son del hospital" + " " + sonDe;
                    DecimalFormat df = new DecimalFormat("#.####");
                    double deviacion = r.getValFuncion();
                    String truncatedValFuncion = df.format(deviacion);
                    
                if (a != null && !a.isEmpty()) {
                    interpretacion += ", entonces " + ((ArrayList) a.get(0)).get(0);
                }
                
                    String medida = null;
                    
                    switch (medidaSeleccionada) {
                    case 1:
                        medida = "\nEsta regla tiene una presición relativa ponderada de: ";
                        break;
                    case 2:
                        medida = "\nEsta regla tiene una estadística Chi-cuadrada de: ";
                        break;
                    case 3:
                        medida = "\nEsta regla tiene una distribución binomial de: ";
                        break;
                        
                    }  
                  interpretaciones.add(interpretacion + medida + truncatedValFuncion);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return interpretaciones;
    }
    //------------------------Aquí termina todo de reglas-----------------------

 
    public void crearModelo(String clase, String query) throws Exception {
        System.out.println("Entro a crear modelo C ");

        FileWriter fichero = null;
        PrintWriter pw = null;

        switch (modeloSeleccionado) {
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
            
        }

    }

    
    
    
    
    
    
}
