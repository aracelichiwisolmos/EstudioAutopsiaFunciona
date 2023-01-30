package hospital.controlador;

/**
 *
 * @author Araceli
 */
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import framework.GUI.Model;
import hospital.datos.AccesoDatos;
import hospital.modelo.centroHospitalario;
import hospital.subgrupos.model.ReglasSG;
import hospital.subgrupos.model.subgruposUtilidades;
import javax.faces.validator.ValidatorException;
import javax.faces.component.UIComponent;


@ManagedBean(name="oSGJB")
@ViewScoped
public class subgruposControler extends Model {
 
      
    private String sQuery;
    private String ruta;
    private AccesoDatos oAD;
    private ArrayList resultado;
    private subgruposUtilidades oSGUtilidades;
    private configuracionAppControler oConfApp;
    private int modeloSeleccionado;    
    private String corpusSeleccionado;
    private int claseSeleccionada;
    private String claseSeleccionada2;
    private int etiquetaSeleccionada;
    private int numeroSeleccionado;
    private int reglasSeleccionadas;
    private int medidaSeleccionada;    
    private List<String> reglas;
    private List<ReglasSG> reglas2;
    private List<String> reglasPDFA;
    private List<centroHospitalario> lista_hospitales;
    private centroHospitalario oCH;
    
    @SuppressWarnings("empty-statement")
    public subgruposControler() throws Exception{
        this.oSGUtilidades = new subgruposUtilidades();
        this.oConfApp = new configuracionAppControler();
        this.modeloSeleccionado = 1;
        this.corpusSeleccionado = null;
        this.claseSeleccionada = 1;
        this.etiquetaSeleccionada = 1;
        this.numeroSeleccionado =1;
        this.reglasSeleccionadas =1;
        this.medidaSeleccionada=1;
        this.reglasPDFA = new ArrayList<>();
        this.reglas = new ArrayList<>();
        this.oCH=new centroHospitalario();
     
    
    }
    
    public subgruposUtilidades getoSGUtilidades() {
             return oSGUtilidades;
                
    }  
    
    //Metodo que muestra los resultados para cualquier usuario
     public void mostrarResultadosSG() {
    try {        
                switch (corpusSeleccionado) {
                    case "c":
                        switch (modeloSeleccionado) {
                            case 1: //SDMap
                                System.out.println("modelo" + modeloSeleccionado); 
                                ruta="C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";
                               setReglas2(oSGUtilidades.obtenerModelo(ruta,claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                       reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));//guardar reglas sini interpretar
                                reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2());//guardar reglas interpretadas
                                
                                  //crearPDF(modeloSeleccionado);
                                
                                break;
                            case 2://BSD
                                System.out.println("modelo" + modeloSeleccionado);
                                ruta="C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BSD\\centro_hospitalario\\matriz_binaria3.arff";
                               oSGUtilidades.obtenerModelo(ruta,claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado);
                               reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2());
                               
                                break;
                            case 3://BS
                                System.out.println("modelo" + modeloSeleccionado);
                                ruta="C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BeamSearch\\centro_hospitalario\\matriz_binaria3.arff";
                               oSGUtilidades.obtenerModelo(ruta,claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado);
                               reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                
                                break;
                        }
                        break;
                    case "d":
                        switch (modeloSeleccionado) {
                            case 1: //SDMap
                                System.out.println("modelo" + modeloSeleccionado);
                                ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\SDMap\\centro_hospitalario\\vista_minable2.arff";
                                setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                                reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());

                                break;
                            case 2://BSD
                                System.out.println("modelo" + modeloSeleccionado);                                
                                ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BSD\\centro_hospitalario\\vista_minable2.arff";
                                setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                                reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());

                                break;
                            case 3://BS
                                System.out.println("modelo" + modeloSeleccionado);
                                ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BeamSearch\\centro_hospitalario\\vista_minable2.arff";
                                setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                                reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());
                                
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
     
         //Crea el modelo que se enviara al framework
    public void guardarModelo(int calseSeleccionada, int modeloSeleccionado) throws Exception {//recibir reglas  
        
        System.out.println("\nmodelo Seleccionado: " + getModeloSeleccionado()+" " + "clase: " + getClaseSeleccionada()+" \n");
        List<centroHospitalario> lista = new ArrayList<>();
        
        try {
            System.out.println("____ENTRO A CREAR MODELO____");
            /*lista = this.oCH.buscaTodos();             
      
               System.out.println("____hospitales_ "+lista+"\n");
           */
                switch (getClaseSeleccionada()) { //Inicia switch para la clase seleccionada 
                case 1:               
                    switch (corpusSeleccionado) {
                        case "c":
                            System.out.println("_____Entro a C con el modelo seleccionado: "+getModeloSeleccionado());
                            //Envia la clase seleccionada volviéndola categórica
                            claseSeleccionada2="H"+claseSeleccionada;
                            System.out.println("_____la clase seleccionada es: "+claseSeleccionada2+"____");
                            System.out.println("___CREANDO MODELO___");                          
                            //oSGUtilidades.crearModelo(claseSeleccionada2);//PARARÍA RUTA DEL ARCHIVO
                            
                            //ruta en donde se encuentra el modelo                        
                            String rutaC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";
                                               
                            switch (getModeloSeleccionado()) { //algoritmo
                                  case 1://SDMap - envia los parametros y el modelo al metodo que trabjara con el framework
                                    System.out.println("Entro para SDMap");
                                  //---enviar con los parametros requeridos ****************
                                    setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                            reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));//paso ruta y parámetros
                                    reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2()); //asignar el valor a reglas2
                                    break;

                                case 2: //BSD - envia los parametros y el modelo.dat al metodo

                                    System.out.println("Entro con BSD");
                                    setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                            reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                    break;
                                    
                                    case 3: //BeamSearch - envia los parametros y el modelo.dat al metodo

                                    System.out.println("Entro con BeamSearch");
                                  setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                          reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                    break;

                                }//algoritmo 
                                break;
                            case "d":
                                System.out.println("_____Entro a D con el modelo seleccionado: "+getModeloSeleccionado());

                                
                                String rutaD = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\\\SD\\D\\SDMap\\centro_hospitalario\\vista_minable2.arff";
                               
                                
                                System.out.println("Entro en D");
                                switch (getModeloSeleccionado()) {
                                 case 1://SDMap - envia los parametros y el modelo al método que trabjara con el framework
                                     
                                    System.out.println("Entro con SDMap");                                 
                                   setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                           reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());
                                    break;

                                 case 2: 

                                    System.out.println("Entro con BSD");
                                    setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                            reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());
                                    break;
                                    
                                 case 3: 

                                    System.out.println("Entro con BeamSearch");
                                   setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                           reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasD1(getReglas2());
                                    break;

                                }
                                break;
                        }
                        break;
                }

            

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

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
    
    
        public int getClaseSeleccionada() {
        return claseSeleccionada;
    }

    public void setClaseSeleccionada(int claseSeleccionada) {
        this.claseSeleccionada = claseSeleccionada;
    }

    public int getEtiquetaSeleccionada() {
        return etiquetaSeleccionada;
    }

    public void setEtiquetaSeleccionada(int etiquetaSeleccionada) {
        this.etiquetaSeleccionada = etiquetaSeleccionada;
    }

    public int getNumeroSeleccionado() {
        return numeroSeleccionado;
    }

    public void setNumeroSeleccionado(int numeroSeleccionado) {
        this.numeroSeleccionado = numeroSeleccionado;
    }

    public int getReglasSeleccionadas() {
        return reglasSeleccionadas;
    }

    public void setReglasSeleccionadas(int reglasSeleccionadas) {
        this.reglasSeleccionadas = reglasSeleccionadas;
    }

    public int getMedidaSeleccionada() {
        return medidaSeleccionada;
    }
    
    public void setMedidaSeleccionada(int medidaSeleccionada) {
        this.medidaSeleccionada = medidaSeleccionada;
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
         //traeemos la lista de todos los hospitales en la BD para moestrarlos al usuario de manera dinámica
    public List<centroHospitalario> getLista_hospitales() {
        List<centroHospitalario> lista = new ArrayList<>();
        try {
            lista = this.oCH.buscaTodos();
        } catch (Exception e) {
            System.err.println(e);
        }
        return lista;
    }
    
    public void setLista_hospitales(List<centroHospitalario> lista_hospitales) {
        this.lista_hospitales = lista_hospitales;
    }
     public List<ReglasSG> getReglas2() {
        return reglas2;
    }

    public void setReglas2(List<ReglasSG> reglas2) {
        this.reglas2 = reglas2;
    }
    
    //XML----------------------------------
  public void setoSGUtilidades(subgruposUtilidades oSGUtilidades) {
        this.oSGUtilidades = oSGUtilidades;
    }

    public configuracionAppControler getoConfApp() {
        return oConfApp;
    }
    
    
}
