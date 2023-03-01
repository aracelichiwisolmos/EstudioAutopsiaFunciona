package hospital.controlador;

/**
 *
 * @author Araceli
 */
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
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import framework.GUI.Model;
import hospital.datos.AccesoDatos;
import hospital.epm.model.Confianza;
import hospital.modelo.centroHospitalario;
import hospital.subgrupos.model.ReglasSG;
import hospital.subgrupos.model.datosGrafica;
import hospital.subgrupos.model.subgruposUtilidades;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.console;
import javax.faces.validator.ValidatorException;
import javax.faces.component.UIComponent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import javax.annotation.PostConstruct;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.HorizontalBarChartModel;

@ManagedBean(name = "oSGJB")
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
    private HorizontalBarChartModel barModel;
    private String alturaGrafica ;

    @SuppressWarnings("empty-statement")
    public subgruposControler() throws Exception {
        this.oSGUtilidades = new subgruposUtilidades();
        this.oConfApp = new configuracionAppControler();
        this.modeloSeleccionado = 1;
        this.corpusSeleccionado = null;
        this.claseSeleccionada = 1;
        this.etiquetaSeleccionada = 1;
        this.numeroSeleccionado = 1;
        this.reglasSeleccionadas = 1;
        this.medidaSeleccionada = 1;
        this.reglasPDFA = new ArrayList<>();
        this.reglas = new ArrayList<>();
        this.oCH = new centroHospitalario();

    }

    public subgruposUtilidades getoSGUtilidades() {
        return oSGUtilidades;

    }

    //Metodo que muestra los resultados para cualquier usuario
    public void mostrarResultadosSG() throws Throwable {
        try {
            switch (corpusSeleccionado) {
                case "c":
                    switch (modeloSeleccionado) {
                        case 1: //SDMap
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));//guardar reglas sini interpretar
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());//guardar reglas interpretadas

                            break;
                        case 2://BSD
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BSD\\centro_hospitalario\\matriz_binaria3.arff";
                            oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado);
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 3://BS
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BeamSearch\\centro_hospitalario\\matriz_binaria3.arff";
                            oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado);
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());

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
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 2://BSD
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BSD\\centro_hospitalario\\vista_minable2.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 3://BS
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BeamSearch\\centro_hospitalario\\vista_minable2.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

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
    public void guardarModelo(int calseSeleccionada, int modeloSeleccionado) throws Exception, Throwable {//recibir reglas  

        System.out.println("\nmodelo Seleccionado: " + getModeloSeleccionado() + " " + "clase: " + getClaseSeleccionada() + " \n");
        List<centroHospitalario> lista = new ArrayList<>();

        try {
            System.out.println("____ENTRO A GUARDAR MODELO____");
            /*lista = this.oCH.buscaTodos();             
      
               System.out.println("____hospitales_ "+lista+"\n");
             */
            switch (corpusSeleccionado) {
                case "c":
                    System.out.println("_____Entro a C con el modelo seleccionado: " + getModeloSeleccionado());
                    //Envia la clase seleccionada volviéndola categórica
                    claseSeleccionada2 = "H" + claseSeleccionada;
                    System.out.println("_____la clase seleccionada es: " + claseSeleccionada2 + "____");
                    System.out.println("___GUARDANDO MODELO___");
                    //oSGUtilidades.crearModelo(claseSeleccionada2);//PARARÍA RUTA DEL ARCHIVO

                    //ruta en donde se encuentra el modelo                        
                    String rutaC = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";

                    switch (getModeloSeleccionado()) { //algoritmo
                        case 1://SDMap - envia los parametros y el modelo al metodo que trabjara con el framework
                            System.out.println("Entro para SDMap");
                            //---enviar con los parametros requeridos ****************
                            setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));//paso ruta y parámetros
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2()); //asignar el valor a reglas2
                            break;

                        case 2: //BSD - envia los parametros y el modelo.dat al metodo

                            System.out.println("Entro con BSD");
                            setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());
                            break;

                        case 3: //BeamSearch - envia los parametros y el modelo.dat al metodo

                            System.out.println("Entro con BeamSearch");
                            setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());
                            break;

                    }//algoritmo 
                    break;

                case "d":
                    System.out.println("_____Entro a D con el modelo seleccionado: " + getModeloSeleccionado());

                    String rutaD = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\\\SD\\D\\SDMap\\centro_hospitalario\\vista_minable2.arff";

                    System.out.println("Entro en D");
                    switch (getModeloSeleccionado()) {
                        case 1://SDMap - envia los parametros y el modelo al método que trabjara con el framework

                            System.out.println("Entro con SDMap");
                            setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());
                            break;

                        case 2:

                            System.out.println("Entro con BSD");
                            setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());
                            break;

                        case 3:

                            System.out.println("Entro con BeamSearch");
                            setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());
                            break;

                    }
                    break;
            }
            if(reglasSeleccionadas  ==20){
                setAlturaGrafica("400px");
            }else{
                setAlturaGrafica("800px");
            }
            
            barModel = new HorizontalBarChartModel();
            List<datosGrafica> datos = oSGUtilidades.obtenerDatosGrafico(claseSeleccionada, medidaSeleccionada, getReglas2());
            ChartSeries reglas = new ChartSeries();
            reglas.setLabel("Reglas");

            for (datosGrafica i : datos) {
                //System.out.println("--------------- valor regla:"+i.getRegla()+"valor :  "+i.getValor());  
                reglas.set(i.getRegla(), i.getValor());
            }

            barModel.addSeries(reglas);
            barModel.setLegendPosition("ne");
            barModel.setBarPadding(0);
            barModel.setBarMargin(100 );
            barModel.setBarWidth(5);
            barModel.setStacked(true);
            Axis YAxis=barModel.getAxis(AxisType.Y);
            YAxis.setLabel("Regla");                 
            Axis XAxis=barModel.getAxis(AxisType.X);
            XAxis.setLabel("%");
            
            
            
            
        } catch (Exception e) {
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
            phrase.add("____________________Detalle de las reglas obtenidas______________________" + "\n" + "\n");

            switch (corpusSeleccionado) {
                case "c":
                    phrase.add("                  Conjunto de datos: C" + "\n");
                    switch (modeloSeleccionado) {
                        case 1:

                            phrase.add("                  Algoritmo: SDMap" + "\n");
                            break;

                        case 2:
                            phrase.add("                   Algoritmo: BSD" + "\n");
                            break;

                        case 3:
                            phrase.add("                   Algoritmo: BeamSearch" + "\n");

                            break;

                    }
                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  Medida de interés: WRAccQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 2:
                            phrase.add("                  Medida de interés: ChiSquareQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 3:
                            phrase.add("                  Medida de interés: BinomialQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                    }
                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  \n");
                            phrase.add("                  Nota: La precisión relativa ponderada mide qué tan inusual es una regla, se define " + "\n");
                            phrase.add("                  como el balance entre su cobertura (porcentaje de médicos con estas opiniones) y su " + "\n");
                            phrase.add("                  ganancia de precisión. Se calcula con la siguiente ecuación: " + "\n");
                            phrase.add("                  WRAccQF= n/N * (p-p0))" + "\n");
                            phrase.add("                  Donde p es la frecuencia relativa de la variable de interés (hospital seleccionado) " + "\n");
                            phrase.add("                  en el subgrupo (médicos con esas opiniones), p0 es la frecuencia relativa de la  " + "\n");
                            phrase.add("                  variable de interés en la población total, N es el tamaño de la población total y n es " + "\n");
                            phrase.add("                  el tamaño del subgrupo." + "\n\n");

                            break;

                        case 2:
                            phrase.add("                   \n");
                            phrase.add("                   Nota: La función de calidad basada en Chi cuadrada se obtiene con la siguiente  " + "\n");
                            phrase.add("                   ecuación: ChiSquareQF=n/N-n * cuadrado(p-p0)" + "\n");
                            phrase.add("                   Donde n es el tamaño del subgrupo, N es el tamaño de la población total. p es el  \n");
                            phrase.add("                   valor promedio de un concepto de interés dado (hospital seleccionado) en el  \n");
                            phrase.add("                   subgrupo identificado por las características (opiniones) y p0 es el valor promedio   \n");
                            phrase.add("                   de un concepto de interés dado en la población general, cuadrado se refiere a la   \n");
                            phrase.add("                   elevación al cuadrado.  \n\n");
                            break;

                        case 3:
                            phrase.add("                 \n");
                            phrase.add("                  Nota: La Función de calidad de prueba binomial compara las frecuencias " + "\n");
                            phrase.add("                  observadas (opiniones) de las dos categorías de una variable dicotómica (hospital   " + "\n");
                            phrase.add("                  seleccionado vs otros hospitales) con las frecuencias esperadas en una distribución   " + "\n");
                            phrase.add("                  binomial con un parámetro de probabilidad especificado de 0.5 (opiniones iguales). " + "\n");
                            phrase.add("                  BinomialQF=((p-p0) * raíz(n))/raíz(p0*(1-p0))*raíz(N/(N-n))  " + "\n");
                            phrase.add("                  Donde n es el tamaño del subgrupo, N es el tamaño de la población total. p es el  " + "\n");
                            phrase.add("                  valor promedio de un concepto de interés dado (hospital seleccionado) en el " + "\n");
                            phrase.add("                  subgrupo identificado por las características (opiniones) y p0 es el valor promedio  " + "\n");
                            phrase.add("                  de un concepto de interés dado en la población general, raíz se refiere a la raíz  " + "\n");
                            phrase.add("                  cuadrada.  " + "\n\n");
                            break;

                    }

                    break;
                case "d":
                    phrase.add("                  Conjunto de datos: D" + "\n");
                    switch (modeloSeleccionado) {
                        case 1:

                            phrase.add("                  Algoritmo: SDMap" + "\n");
                            break;

                        case 2:
                            phrase.add("                   Algoritmo: BSD" + "\n");
                            break;

                        case 3:
                            phrase.add("                   Algoritmo: BeamSearch" + "\n");

                            break;

                    }
                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  Medida de interés: WRAccQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 2:
                            phrase.add("                  Medida de interés: ChiSquareQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");
                            break;

                        case 3:
                            phrase.add("                  Medida de interés: BinomialQF" + "\n");
                            phrase.add("                  Número de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  Número de atributos: " + this.numeroSeleccionado + "\n");
                            break;

                    }

                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  \n");
                            phrase.add("                  Nota: La precisión relativa ponderada mide qué tan inusual es una regla, se define " + "\n");
                            phrase.add("                  como el balance entre su cobertura (porcentaje de médicos con estas opiniones) y su " + "\n");
                            phrase.add("                  ganancia de precisión. Se calcula con la siguiente ecuación: " + "\n");
                            phrase.add("                  WRAccQF= n/N * (p-p0))" + "\n");
                            phrase.add("                  Donde p es la frecuencia relativa de la variable de interés (hospital seleccionado) " + "\n");
                            phrase.add("                  en el subgrupo (médicos con esas opiniones), p0 es la frecuencia relativa de la  " + "\n");
                            phrase.add("                  variable de interés en la población total, N es el tamaño de la población total y n es " + "\n");
                            phrase.add("                  el tamaño del subgrupo." + "\n\n");

                            break;

                        case 2:
                            phrase.add("                   \n");
                            phrase.add("                   Nota: La función de calidad basada en Chi cuadrada se obtiene con la siguiente  " + "\n");
                            phrase.add("                   ecuación: ChiSquareQF=n/N-n * cuadrado(p-p0)" + "\n");
                            phrase.add("                   Donde n es el tamaño del subgrupo, N es el tamaño de la población total. p es el  \n");
                            phrase.add("                   valor promedio de un concepto de interés dado (hospital seleccionado) en el  \n");
                            phrase.add("                   subgrupo identificado por las características (opiniones) y p0 es el valor promedio   \n");
                            phrase.add("                   de un concepto de interés dado en la población general, cuadrado se refiere a la   \n");
                            phrase.add("                   elevación al cuadrado.  \n\n");
                            break;

                        case 3:
                            phrase.add("                 \n");
                            phrase.add("                  Nota: La Función de calidad de prueba binomial compara las frecuencias " + "\n");
                            phrase.add("                  observadas (opiniones) de las dos categorías de una variable dicotómica (hospital   " + "\n");
                            phrase.add("                  seleccionado vs otros hospitales) con las frecuencias esperadas en una distribución   " + "\n");
                            phrase.add("                  binomial con un parámetro de probabilidad especificado de 0.5 (opiniones iguales). " + "\n");
                            phrase.add("                  BinomialQF=((p-p0) * raíz(n))/raíz(p0*(1-p0))*raíz(N/(N-n))  " + "\n");
                            phrase.add("                  Donde n es el tamaño del subgrupo, N es el tamaño de la población total. p es el  " + "\n");
                            phrase.add("                  valor promedio de un concepto de interés dado (hospital seleccionado) en el " + "\n");
                            phrase.add("                  subgrupo identificado por las características (opiniones) y p0 es el valor promedio  " + "\n");
                            phrase.add("                  de un concepto de interés dado en la población general, raíz se refiere a la raíz  " + "\n");
                            phrase.add("                  cuadrada.  " + "\n\n");
                            break;

                    }

            }

            phrase.add("_______________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
            pdf.add(phrase);

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
    
      public String getAlturaGrafica() {
        return alturaGrafica;
    }

    
    public void setAlturaGrafica(String alturaGrafica) {
        this.alturaGrafica = alturaGrafica;
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

    @PostConstruct
    public void init() {
        //      createBarModel();
        barModel = new HorizontalBarChartModel();

    }

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

}
