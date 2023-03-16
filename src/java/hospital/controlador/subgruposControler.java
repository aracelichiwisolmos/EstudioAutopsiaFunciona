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
import hospital.modelo.encuesta;
import hospital.subgrupos.model.ReglasSG;
import hospital.subgrupos.model.datosGrafica;
import hospital.subgrupos.model.encuestaSG;
import hospital.subgrupos.model.subgruposUtilidades;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.console;
import javax.faces.validator.ValidatorException;
import javax.faces.component.UIComponent;
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
    private List<datosGrafica> reglasPDFA;
    private List<centroHospitalario> lista_hospitales;
    private centroHospitalario oCH;
    private HorizontalBarChartModel barModel;
    private BarChartModel barModel2;
    private BarChartModel barModel3;
    private BarChartModel barModel4;
    private BarChartModel barModel5;
    private BarChartModel barModel6;
    private BarChartModel barModel7;
    private HorizontalBarChartModel barModel8;
    private BarChartModel barModel9;
    private BarChartModel barModel10;
    private BarChartModel barModel11;
    private String alturaGrafica;

    private List<encuestaSG> datosEncuestas;
    private List<encuestaSG> datosEncuestas2;
    private List<encuestaSG> opciones_preg14;
    private List<encuestaSG> opciones_preg18;
    private List<encuestaSG> opciones_preg19;

    @SuppressWarnings("empty-statement")
    public subgruposControler() throws Exception {
        this.oAD = new AccesoDatos();
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
        this.datosEncuestas = this.cantInstancias_hospital();
        this.datosEncuestas2 = this.cantInstancias_hospital2();

    }

    public subgruposUtilidades getoSGUtilidades() {
        return oSGUtilidades;

    }

    //--------------------------------Datos para graficar--------------------------------------------------------
    public List<encuestaSG> cantInstancias_hospital() throws Exception {

        int cantidad = 0;
        ArrayList resultado = null;
        ArrayList arr = null;
        ArrayList<encuestaSG> datos = new ArrayList<>();

        try {
            String sQuery = "	select\n"
                    + "		CASE \n"
                    + "			WHEN i.centro_hospitalario <= 10 THEN 'H'|| 1 \n"
                    + "			ELSE 'H' || i.centro_hospitalario \n"
                    + "		END as centro,\n"
                    + "		e.idresp,\n"
                    + "		e.idpreg,\n"
                    + "		COUNT(*),\n"
                    + " (select sresp from respuesta r where r.idpreg = e.idpreg and r.idresp = e.idresp) as sresp \n"
                    + "	from encuesta e\n"
                    + "	inner join identificacion_usuario4 i\n"
                    + "	on e.idmedico = i.clave_med\n"
                    + "	where e.idpreg in (3,4,7,8,11)\n"
                    + "	group by centro, e.idresp, e.idpreg\n"
                    + "	order by e.idpreg, centro, e.idresp;";
           
                
                if (oAD.conectar()) {
                    arr = oAD.ejecutarConsulta(sQuery);
                   
                    if (arr != null && !arr.isEmpty()) {
                        encuestaSG item;

                        for (int i = 0; i < arr.toArray().length; i++) {
                            item = new encuestaSG();
                            item.setCentro(((ArrayList) arr.get(i)).get(0).toString());
                            item.setResp(((ArrayList) arr.get(i)).get(1).toString());
                            item.setPregunta((int) Math.floor((double) ((ArrayList) arr.get(i)).get(2)));
                            item.setCant((int) Math.floor((double) ((ArrayList) arr.get(i)).get(3)));
                            item.setSresp(((ArrayList) arr.get(i)).get(4).toString());
                            datos.add(item);
                        }
                    }

                    return datos;
                }
                
        } catch (Exception e) {
            throw e;
        }
        finally{
            oAD.desconectar();
        }

        return datos;
    }

    public List<encuestaSG> cantInstancias_hospital2() throws Exception {
        int cantidad = 0;
        ArrayList arr = null;
        ArrayList<encuestaSG> datos = new ArrayList<>();

        try {
            String sQuery = "select \n"
                    + "	CASE \n"
                    + "			WHEN c.centro_hospitalario <= 10 THEN 'H'|| 1 \n"
                    + "			ELSE 'H' || c.centro_hospitalario \n"
                    + "		END as centro,\n"
                    + "		a.idresp,\n"
                    + "		a.idpreg,\n"
                    + "		COUNT(*),\n"
                    + "		b.sresp\n"
                    + "from encuesta a\n"
                    + "	inner join respuesta b\n"
                    + "	on (a.idresp= b.idresp and a.idpreg = b.idpreg)\n"
                    + "	inner join identificacion_usuario4 c \n"
                    + "	on c.clave_med=a.idmedico\n"
                    + "	WHERE b.idpreg in (14,17,18, 19,21)\n"
                    + "group by centro, a.idresp, a.idpreg, b.sresp\n"
                    + "order by a.idpreg, centro, a.idresp;";
            
           
                if (oAD.conectar()) {
                    arr = oAD.ejecutarConsulta(sQuery);
                    
                    if (arr != null && !arr.isEmpty()) {
                        encuestaSG item;

                        for (int i = 0; i < arr.toArray().length; i++) {
                            item = new encuestaSG();
                            item.setCentro(((ArrayList) arr.get(i)).get(0).toString());
                            item.setResp(((ArrayList) arr.get(i)).get(1).toString());
                            item.setPregunta((int) Math.floor((double) ((ArrayList) arr.get(i)).get(2)));
                            item.setCant((int) Math.floor((double) ((ArrayList) arr.get(i)).get(3)));
                            item.setSresp(((ArrayList) arr.get(i)).get(4).toString());
                            datos.add(item);
                        }
                    }

                    return datos;
                }
               
        } catch (Exception e) {
            throw e;
        }
        finally{
            oAD.desconectar();
        }
        

        return datos;
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
            if (reglasSeleccionadas == 20) {
                setAlturaGrafica("600px");
            } else {
                setAlturaGrafica("1200px");
            }

            barModel = new HorizontalBarChartModel();
            ChartSeries reglas = new ChartSeries();
            reglas.setLabel("Reglas");
            System.out.println("---------------REGLAS" + reglasPDFA);
            for (datosGrafica i : reglasPDFA) {
                System.out.println("--------------- valor regla:" + i.getRegla() + "valor :  " + i.getValor());
                reglas.set(i.getRegla(), i.getValor());

            }
            barModel.addSeries(reglas);
            barModel.setLegendPosition("ne");
            barModel.setBarPadding(10);
            barModel.setBarMargin(0);
            barModel.setShadow(false);
            barModel.setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
            //barModel.setStacked(true);
            Axis YAxis = barModel.getAxis(AxisType.Y);
            YAxis.setLabel("Regla");
            Axis XAxis = barModel.getAxis(AxisType.X);
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

    public List<datosGrafica> getReglasPDFA() {
        return reglasPDFA;
    }

    public void setReglasPDFA(ArrayList<datosGrafica> reglasPDFA) {
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
        barModel2 = new BarChartModel();
        barModel3 = new BarChartModel();
        barModel4 = new BarChartModel();
        barModel5 = new BarChartModel();
        barModel6 = new BarChartModel();
        barModel7 = new HorizontalBarChartModel();
        barModel8 = new HorizontalBarChartModel();
        barModel9 = new BarChartModel();
        barModel10 = new HorizontalBarChartModel();

        this.crearGraficaEncuestas();
        this.crearGraficaEncuestasPreg4();
        this.crearGraficaEncuestasPreg7();
        this.crearGraficaEncuestasPreg8();
        this.crearGraficaEncuestasPreg11();
        this.crearGraficaEncuestasPreg14();
        this.crearGraficaEncuestasPreg17();
        this.crearGraficaEncuestasPreg18();
        this.crearGraficaEncuestasPreg19();


    }

    public void crearGraficaEncuestas() {
        //setBarModel2(new BarChartModel());
        this.barModel2 = new BarChartModel();

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 3)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel2.addSeries(h1);
        this.barModel2.addSeries(h11);
        this.barModel2.addSeries(h12);

        this.barModel2.setLegendPosition("ne");
        //getBarModel2().setBarPadding(10);
        //getBarModel2().setBarMargin(0);
        this.barModel2.setShadow(false);
        //getBarModel2().setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        //barModel.setStacked(true);
        Axis YAxis = this.barModel2.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel2.getAxis(AxisType.X);
        XAxis.setLabel("Años de práctica");
    }

    //----pregunta 4
    public void crearGraficaEncuestasPreg4() {
        //setBarModel2(new BarChartModel());
        this.setBarModel3(new BarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 4)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel3.addSeries(h1);
        this.barModel3.addSeries(h11);
        this.barModel3.addSeries(h12);

        this.barModel3.setLegendPosition("ne");
        //getBarModel2().setBarPadding(10);
        //getBarModel2().setBarMargin(0);
        this.barModel3.setShadow(false);
        //getBarModel2().setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        //barModel.setStacked(true);
        Axis YAxis = this.barModel3.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel3.getAxis(AxisType.X);
        XAxis.setLabel("Experiencia en casos de autopsia");
    }

    //----pregunta 7
    public void crearGraficaEncuestasPreg7() {
        //setBarModel2(new BarChartModel());
        this.setBarModel4(new BarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 7)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel4.addSeries(h1);
        this.barModel4.addSeries(h11);
        this.barModel4.addSeries(h12);

        this.barModel4.setLegendPosition("ne");
        //getBarModel2().setBarPadding(10);
        //getBarModel2().setBarMargin(0);
        this.barModel4.setShadow(false);
        //getBarModel2().setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        //barModel.setStacked(true);
        Axis YAxis = this.barModel4.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel4.getAxis(AxisType.X);
        XAxis.setLabel("Discrepancias encontradas en las autopsias");
    }

    //----pregunta 7
    public void crearGraficaEncuestasPreg8() {
        //setBarModel2(new BarChartModel());
        this.setBarModel5(new BarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 8)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel5.addSeries(h1);
        this.barModel5.addSeries(h11);
        this.barModel5.addSeries(h12);

        this.barModel5.setLegendPosition("ne");
        //getBarModel2().setBarPadding(10);
        //getBarModel2().setBarMargin(0);
        this.barModel5.setShadow(false);
        //getBarModel2().setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        //barModel.setStacked(true);
        Axis YAxis = this.barModel5.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel5.getAxis(AxisType.X);
        XAxis.setLabel("Dan origen a casos de arbitraje");
    }

    //----pregunta 11
    public void crearGraficaEncuestasPreg11() {
        //setBarModel2(new BarChartModel());
        this.setBarModel6(new BarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 11)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel6.addSeries(h1);
        this.barModel6.addSeries(h11);
        this.barModel6.addSeries(h12);

        this.barModel6.setLegendPosition("ne");
        //getBarModel2().setBarPadding(10);
        //getBarModel2().setBarMargin(0);
        this.barModel6.setShadow(false);
        //getBarModel2().setBarWidth(15);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        //barModel.setStacked(true);
        Axis YAxis = this.barModel6.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel6.getAxis(AxisType.X);
        XAxis.setLabel("Dan origen a casos de demandas");
    }

    //----pregunta 14
    public void crearGraficaEncuestasPreg14() {
         
        setOpciones_preg14(new ArrayList<>());
        this.setBarModel7(new BarChartModel());
        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");
      
        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 14)).forEachOrdered((e) -> {
            if (null !=e.getCentro()) {
              boolean exist= getOpciones_preg14().stream().anyMatch(i -> i.getResp().equals(e.getResp()));
              if(!exist){
                  getOpciones_preg14().add(e);
              }
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getResp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getResp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getResp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel7.addSeries(h1);
        this.barModel7.addSeries(h11);
        this.barModel7.addSeries(h12);
        //getBarModel7().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        getBarModel7().setBarWidth(20);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.barModel7.setLegendPosition("ne");
        this.barModel7.setShadow(false);
        Axis YAxis = this.barModel7.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel7.getAxis(AxisType.X);
        XAxis.setLabel("Opciones");
    }
    
     //----pregunta 17
    public void crearGraficaEncuestasPreg17() {

        this.setBarModel8(new HorizontalBarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 17)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel8.addSeries(h1);
        this.barModel8.addSeries(h11);
        this.barModel8.addSeries(h12);
        getBarModel8().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        //getBarModel8().setBarWidth(25);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.barModel8.setLegendPosition("ne");
        this.barModel8.setShadow(false);
        Axis YAxis = this.barModel8.getAxis(AxisType.Y);
        //YAxis.setLabel("");
        Axis XAxis = this.barModel8.getAxis(AxisType.X);
        XAxis.setLabel("Cantidad de respuestas");
    }
    
    //----pregunta 18
    public void crearGraficaEncuestasPreg18() {
         
        setOpciones_preg18(new ArrayList<>());
        this.setBarModel9(new BarChartModel());
        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");
      
        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 18 )).forEachOrdered((e) -> {
            if (null !=e.getCentro()) {
              boolean exist= getOpciones_preg18().stream().anyMatch(i -> i.getResp().equals(e.getResp()));
              if(!exist){
                  getOpciones_preg18().add(e);
              }
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getResp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getResp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getResp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel9.addSeries(h1);
        this.barModel9.addSeries(h11);
        this.barModel9.addSeries(h12);
        //getBarModel7().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        getBarModel9().setBarWidth(20);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.barModel9.setLegendPosition("ne");
        this.barModel9.setShadow(false);
        Axis YAxis = this.barModel9.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel9.getAxis(AxisType.X);
        XAxis.setLabel("Opciones");
    }
  
      //----pregunta 19
    public void crearGraficaEncuestasPreg19() {
         
        setOpciones_preg19(new ArrayList<>());
        this.setBarModel10(new BarChartModel());
        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");
      
        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 19 )).forEachOrdered((e) -> {
            if (null !=e.getCentro()) {
              boolean exist= getOpciones_preg19().stream().anyMatch(i -> i.getResp().equals(e.getResp()));
              if(!exist){
                  getOpciones_preg19().add(e);
              }
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getResp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getResp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getResp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.barModel10.addSeries(h1);
        this.barModel10.addSeries(h11);
        this.barModel10.addSeries(h12);
        //getBarModel7().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        getBarModel10().setBarWidth(20);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.barModel10.setLegendPosition("ne");
        this.barModel10.setShadow(false);
        Axis YAxis = this.barModel10.getAxis(AxisType.Y);
        YAxis.setLabel("Cantidad de respuestas");
        Axis XAxis = this.barModel10.getAxis(AxisType.X);
        XAxis.setLabel("Opciones");
    }
    
    
    
    /*
//----pregunta 19
    public void crearGraficaEncuestasPreg19() {

        this.setBarModel10(new HorizontalBarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 19)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.getBarModel10().addSeries(h1);
        this.getBarModel10().addSeries(h11);
        this.getBarModel10().addSeries(h12);
        getBarModel10().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        getBarModel10().setBarWidth(20);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.getBarModel10().setLegendPosition("ne");
        this.getBarModel10().setShadow(false);
        Axis YAxis = this.barModel10.getAxis(AxisType.Y);
        //YAxis.setLabel("");
        Axis XAxis = this.barModel10.getAxis(AxisType.X);
        XAxis.setLabel("Cantidad de respuestas");
    }
    */
            //----pregunta 21
    public void crearGraficaEncuestasPreg21() {

        this.setBarModel11(new HorizontalBarChartModel());

        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de Río Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.getDatosEncuestas2().stream().filter((e) -> (e.getPregunta() == 19)).forEachOrdered((e) -> {
            if (null != e.getCentro()) {
                switch (e.getCentro()) {
                    case "H1":
                        h1.set(e.getSresp(), e.getCant());
                        break;
                    case "H11":
                        h11.set(e.getSresp(), e.getCant());
                        break;
                    case "H12":
                        h12.set(e.getSresp(), e.getCant());
                        break;
                    default:
                        break;
                }
            }
        });

        this.getBarModel11().addSeries(h1);
        this.getBarModel11().addSeries(h11);
        this.getBarModel11().addSeries(h12);
        getBarModel11().setBarPadding(10);
        //getBarModel7().setBarMargin(0);
        getBarModel11().setBarWidth(20);//ancho barraaaaaaaaaaaaaaaaaaaaaaaas ;D 
        this.getBarModel11().setLegendPosition("ne");
        this.getBarModel11().setShadow(false);
        Axis YAxis = this.barModel11.getAxis(AxisType.Y);
        //YAxis.setLabel("");
        Axis XAxis = this.barModel11.getAxis(AxisType.X);
        XAxis.setLabel("Cantidad de respuestas");
    }
    
    

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

    public BarChartModel getBarModel2() {
        return barModel2;
    }

    public void setBarModel2(BarChartModel barModel2) {
        this.barModel2 = barModel2;
    }

    public BarChartModel getBarModel3() {
        return barModel3;
    }

    public void setBarModel3(BarChartModel barModel3) {
        this.barModel3 = barModel3;
    }

    public BarChartModel getBarModel4() {
        return barModel4;
    }

    public void setBarModel4(BarChartModel barModel4) {
        this.barModel4 = barModel4;
    }

    public BarChartModel getBarModel5() {
        return barModel5;
    }

    public void setBarModel5(BarChartModel barModel5) {
        this.barModel5 = barModel5;
    }

    public BarChartModel getBarModel6() {
        return barModel6;
    }

    public void setBarModel6(BarChartModel barModel6) {
        this.barModel6 = barModel6;
    }

    public BarChartModel getBarModel7() {
        return barModel7;
    }

    public void setBarModel7(BarChartModel barModel7) {
        this.barModel7 = barModel7;
    }

    public BarChartModel getBarModel8() {
        return barModel8;
    }

    public void setBarModel8(HorizontalBarChartModel barModel8) {
        this.barModel8 = barModel8;
    }

    public BarChartModel getBarModel9() {
        return barModel9;
    }

    public void setBarModel9(BarChartModel barModel9) {
        this.barModel9 = barModel9;
    }

    public BarChartModel getBarModel10() {
        return barModel10;
    }

    public void setBarModel10(BarChartModel barModel10) {
        this.barModel10 = barModel10;
    }

    public BarChartModel getBarModel11() {
        return barModel11;
    }

    public void setBarModel11(BarChartModel barModel11) {
        this.barModel11 = barModel11;
    }

    /**
     * @return the datosEncuestas2
     */
    public List<encuestaSG> getDatosEncuestas2() {
        return datosEncuestas2;
    }

    /**
     * @param datosEncuestas2 the datosEncuestas2 to set
     */
    public void setDatosEncuestas2(List<encuestaSG> datosEncuestas2) {
        this.datosEncuestas2 = datosEncuestas2;
    }

    /**
     * @return the opciones_preg14
     */
    public List<encuestaSG> getOpciones_preg14() {
        return opciones_preg14;
    }

    /**
     * @param opciones_preg14 the opciones_preg14 to set
     */
    public void setOpciones_preg14(List<encuestaSG> opciones_preg14) {
        this.opciones_preg14 = opciones_preg14;
    }

    /**
     * @return the opciones_preg18
     */
    public List<encuestaSG> getOpciones_preg18() {
        return opciones_preg18;
    }

    /**
     * @param opciones_preg18 the opciones_preg18 to set
     */
    public void setOpciones_preg18(List<encuestaSG> opciones_preg18) {
        this.opciones_preg18 = opciones_preg18;
    }

    /**
     * @return the opciones_preg19
     */
    public List<encuestaSG> getOpciones_preg19() {
        return opciones_preg19;
    }

    /**
     * @param opciones_preg19 the opciones_preg19 to set
     */
    public void setOpciones_preg19(List<encuestaSG> opciones_preg19) {
        this.opciones_preg19 = opciones_preg19;
    }

}
