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
    private String alturaGrafica;
    
    private List<encuestaSG> datosEncuestas;
    

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
        
        this.datosEncuestas = this.cantInstancias_hospital();

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
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    arr = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                    if (arr != null && !arr.isEmpty()) {
                        encuestaSG item;
                        
                        for(int i = 0; i < arr.toArray().length; i++) {
                            item = new encuestaSG();
                            item.setCentro(((ArrayList) arr.get(i)).get(0).toString());
                            item.setResp(((ArrayList) arr.get(i)).get(1).toString());
                            item.setPregunta((int)Math.floor((double) ((ArrayList) arr.get(i)).get(2)));
                            item.setCant((int)Math.floor((double) ((ArrayList) arr.get(i)).get(3)));
                            item.setSresp(((ArrayList) arr.get(i)).get(4).toString());
                            datos.add(item);
                        }
                    }
                    
                    return datos;
                }
                oAD = null;
            } else {
                resultado = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            
           
            
        } catch (Exception e) {
            throw e;
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
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));//guardar reglas sini interpretar
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());//guardar reglas interpretadas

                            break;
                        case 2://BSD
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BSD\\centro_hospitalario\\matriz_binaria3.arff";
                            oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado);
                            reglasPDFA = oSGUtilidades.obtenerReglasC1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 3://BS
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\BeamSearch\\centro_hospitalario\\matriz_binaria3.arff";
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
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\SDMap\\centro_hospitalario\\vista_minable2.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 2://BSD
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BSD\\centro_hospitalario\\vista_minable2.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                        case 3://BS
                            System.out.println("modelo" + modeloSeleccionado);
                            ruta = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BeamSearch\\centro_hospitalario\\vista_minable2.arff";
                            setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                            reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada, medidaSeleccionada, getReglas2());

                            break;
                    }
                    break;
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operaci??n exitosa."));
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
                    //Envia la clase seleccionada volvi??ndola categ??rica
                    claseSeleccionada2 = "H" + claseSeleccionada;
                    System.out.println("_____la clase seleccionada es: " + claseSeleccionada2 + "____");
                    System.out.println("___GUARDANDO MODELO___");
                    //oSGUtilidades.crearModelo(claseSeleccionada2);//PARAR??A RUTA DEL ARCHIVO

                    //ruta en donde se encuentra el modelo                        
                    String rutaC = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";

                    switch (getModeloSeleccionado()) { //algoritmo
                        case 1://SDMap - envia los parametros y el modelo al metodo que trabjara con el framework
                            System.out.println("Entro para SDMap");
                            //---enviar con los parametros requeridos ****************
                            setReglas2(oSGUtilidades.obtenerModelo(rutaC, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                    reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));//paso ruta y par??metros
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

                    String rutaD = "C:\\Users\\Araceli\\Desktop\\MAESTR??A\\EstudioAutopsiaFunciona\\corpus\\\\SD\\D\\SDMap\\centro_hospitalario\\vista_minable2.arff";

                    System.out.println("Entro en D");
                    switch (getModeloSeleccionado()) {
                        case 1://SDMap - envia los parametros y el modelo al m??todo que trabjara con el framework

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
                            phrase.add("                  Medida de inter??s: WRAccQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 2:
                            phrase.add("                  Medida de inter??s: ChiSquareQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 3:
                            phrase.add("                  Medida de inter??s: BinomialQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                    }
                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  \n");
                            phrase.add("                  Nota: La precisi??n relativa ponderada mide qu?? tan inusual es una regla, se define " + "\n");
                            phrase.add("                  como el balance entre su cobertura (porcentaje de m??dicos con estas opiniones) y su " + "\n");
                            phrase.add("                  ganancia de precisi??n. Se calcula con la siguiente ecuaci??n: " + "\n");
                            phrase.add("                  WRAccQF= n/N * (p-p0))" + "\n");
                            phrase.add("                  Donde p es la frecuencia relativa de la variable de inter??s (hospital seleccionado) " + "\n");
                            phrase.add("                  en el subgrupo (m??dicos con esas opiniones), p0 es la frecuencia relativa de la  " + "\n");
                            phrase.add("                  variable de inter??s en la poblaci??n total, N es el tama??o de la poblaci??n total y n es " + "\n");
                            phrase.add("                  el tama??o del subgrupo." + "\n\n");

                            break;

                        case 2:
                            phrase.add("                   \n");
                            phrase.add("                   Nota: La funci??n de calidad basada en Chi cuadrada se obtiene con la siguiente  " + "\n");
                            phrase.add("                   ecuaci??n: ChiSquareQF=n/N-n * cuadrado(p-p0)" + "\n");
                            phrase.add("                   Donde n es el tama??o del subgrupo, N es el tama??o de la poblaci??n total. p es el  \n");
                            phrase.add("                   valor promedio de un concepto de inter??s dado (hospital seleccionado) en el  \n");
                            phrase.add("                   subgrupo identificado por las caracter??sticas (opiniones) y p0 es el valor promedio   \n");
                            phrase.add("                   de un concepto de inter??s dado en la poblaci??n general, cuadrado se refiere a la   \n");
                            phrase.add("                   elevaci??n al cuadrado.  \n\n");
                            break;

                        case 3:
                            phrase.add("                 \n");
                            phrase.add("                  Nota: La Funci??n de calidad de prueba binomial compara las frecuencias " + "\n");
                            phrase.add("                  observadas (opiniones) de las dos categor??as de una variable dicot??mica (hospital   " + "\n");
                            phrase.add("                  seleccionado vs otros hospitales) con las frecuencias esperadas en una distribuci??n   " + "\n");
                            phrase.add("                  binomial con un par??metro de probabilidad especificado de 0.5 (opiniones iguales). " + "\n");
                            phrase.add("                  BinomialQF=((p-p0) * ra??z(n))/ra??z(p0*(1-p0))*ra??z(N/(N-n))  " + "\n");
                            phrase.add("                  Donde n es el tama??o del subgrupo, N es el tama??o de la poblaci??n total. p es el  " + "\n");
                            phrase.add("                  valor promedio de un concepto de inter??s dado (hospital seleccionado) en el " + "\n");
                            phrase.add("                  subgrupo identificado por las caracter??sticas (opiniones) y p0 es el valor promedio  " + "\n");
                            phrase.add("                  de un concepto de inter??s dado en la poblaci??n general, ra??z se refiere a la ra??z  " + "\n");
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
                            phrase.add("                  Medida de inter??s: WRAccQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");

                            break;

                        case 2:
                            phrase.add("                  Medida de inter??s: ChiSquareQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");
                            break;

                        case 3:
                            phrase.add("                  Medida de inter??s: BinomialQF" + "\n");
                            phrase.add("                  N??mero de subgrupos: " + this.reglasSeleccionadas + "\n");
                            phrase.add("                  N??mero de atributos: " + this.numeroSeleccionado + "\n");
                            break;

                    }

                    switch (medidaSeleccionada) {
                        case 1:
                            phrase.add("                  \n");
                            phrase.add("                  Nota: La precisi??n relativa ponderada mide qu?? tan inusual es una regla, se define " + "\n");
                            phrase.add("                  como el balance entre su cobertura (porcentaje de m??dicos con estas opiniones) y su " + "\n");
                            phrase.add("                  ganancia de precisi??n. Se calcula con la siguiente ecuaci??n: " + "\n");
                            phrase.add("                  WRAccQF= n/N * (p-p0))" + "\n");
                            phrase.add("                  Donde p es la frecuencia relativa de la variable de inter??s (hospital seleccionado) " + "\n");
                            phrase.add("                  en el subgrupo (m??dicos con esas opiniones), p0 es la frecuencia relativa de la  " + "\n");
                            phrase.add("                  variable de inter??s en la poblaci??n total, N es el tama??o de la poblaci??n total y n es " + "\n");
                            phrase.add("                  el tama??o del subgrupo." + "\n\n");

                            break;

                        case 2:
                            phrase.add("                   \n");
                            phrase.add("                   Nota: La funci??n de calidad basada en Chi cuadrada se obtiene con la siguiente  " + "\n");
                            phrase.add("                   ecuaci??n: ChiSquareQF=n/N-n * cuadrado(p-p0)" + "\n");
                            phrase.add("                   Donde n es el tama??o del subgrupo, N es el tama??o de la poblaci??n total. p es el  \n");
                            phrase.add("                   valor promedio de un concepto de inter??s dado (hospital seleccionado) en el  \n");
                            phrase.add("                   subgrupo identificado por las caracter??sticas (opiniones) y p0 es el valor promedio   \n");
                            phrase.add("                   de un concepto de inter??s dado en la poblaci??n general, cuadrado se refiere a la   \n");
                            phrase.add("                   elevaci??n al cuadrado.  \n\n");
                            break;

                        case 3:
                            phrase.add("                 \n");
                            phrase.add("                  Nota: La Funci??n de calidad de prueba binomial compara las frecuencias " + "\n");
                            phrase.add("                  observadas (opiniones) de las dos categor??as de una variable dicot??mica (hospital   " + "\n");
                            phrase.add("                  seleccionado vs otros hospitales) con las frecuencias esperadas en una distribuci??n   " + "\n");
                            phrase.add("                  binomial con un par??metro de probabilidad especificado de 0.5 (opiniones iguales). " + "\n");
                            phrase.add("                  BinomialQF=((p-p0) * ra??z(n))/ra??z(p0*(1-p0))*ra??z(N/(N-n))  " + "\n");
                            phrase.add("                  Donde n es el tama??o del subgrupo, N es el tama??o de la poblaci??n total. p es el  " + "\n");
                            phrase.add("                  valor promedio de un concepto de inter??s dado (hospital seleccionado) en el " + "\n");
                            phrase.add("                  subgrupo identificado por las caracter??sticas (opiniones) y p0 es el valor promedio  " + "\n");
                            phrase.add("                  de un concepto de inter??s dado en la poblaci??n general, ra??z se refiere a la ra??z  " + "\n");
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

    //traeemos la lista de todos los hospitales en la BD para moestrarlos al usuario de manera din??mica
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
        
        this.crearGraficaEncuestas();
        this.crearGraficaEncuestasPreg4();
        this.crearGraficaEncuestasPreg7();
        this.crearGraficaEncuestasPreg8();
        this.crearGraficaEncuestasPreg11();
        
    }
    
    public void crearGraficaEncuestas() {
        //setBarModel2(new BarChartModel());
        this.barModel2 = new BarChartModel();
        
        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de R??o Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 3)).forEachOrdered((e) -> {
            if(null != e.getCentro())
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
        XAxis.setLabel("A??os de pr??ctica");
    }
    
    //----pregunta 4
     public void crearGraficaEncuestasPreg4() {
        //setBarModel2(new BarChartModel());
        this.setBarModel3(new BarChartModel());
        
        ChartSeries h1 = new ChartSeries();
        h1.setLabel("Hospital Regional de R??o Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 4)).forEachOrdered((e) -> {
            if(null != e.getCentro())
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
        h1.setLabel("Hospital Regional de R??o Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 7)).forEachOrdered((e) -> {
            if(null != e.getCentro())
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
        h1.setLabel("Hospital Regional de R??o Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 8)).forEachOrdered((e) -> {
            if(null != e.getCentro())
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
        h1.setLabel("Hospital Regional de R??o Blanco ");
        ChartSeries h11 = new ChartSeries();
        h11.setLabel("Hospital General San Juan Bautista");
        ChartSeries h12 = new ChartSeries();
        h12.setLabel("Hospiatl General de Zona 53");

        this.datosEncuestas.stream().filter((e) -> (e.getPregunta() == 11)).forEachOrdered((e) -> {
            if(null != e.getCentro())
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

}
