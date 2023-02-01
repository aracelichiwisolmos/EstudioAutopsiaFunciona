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
import hospital.subgrupos.model.subgruposUtilidades;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
     public void mostrarResultadosSG() throws Throwable {
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
                                  reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());

                                break;
                            case 2://BSD
                                System.out.println("modelo" + modeloSeleccionado);                                
                                ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BSD\\centro_hospitalario\\vista_minable2.arff";
                                setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                                 reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());

                                break;
                            case 3://BS
                                System.out.println("modelo" + modeloSeleccionado);
                                ruta = "C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\D\\BeamSearch\\centro_hospitalario\\vista_minable2.arff";
                                setReglas2(oSGUtilidades.obtenerModelo(ruta, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                        reglasSeleccionadas, medidaSeleccionada, modeloSeleccionado));
                                 reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                
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
                                     reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                    break;

                                 case 2: 

                                    System.out.println("Entro con BSD");
                                    setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                            reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                    reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());
                                    break;
                                    
                                 case 3: 

                                    System.out.println("Entro con BeamSearch");
                                   setReglas2(oSGUtilidades.obtenerModelo(rutaD, claseSeleccionada, etiquetaSeleccionada, numeroSeleccionado,
                                           reglasSeleccionadas, medidaSeleccionada, getModeloSeleccionado()));
                                     reglasPDFA = oSGUtilidades.obtenerReglasD1(claseSeleccionada,medidaSeleccionada,getReglas2());
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
    
    
       public void crearPDF(int modeloseleccionado, List<ReglasSG> reglasSGList) throws DocumentException, FileNotFoundException {

        //FileOutputStream pdf = new FileOutputStream("D:\\Escritorio\\Reglas\\Reglas.pdf");
        FileOutputStream pdf = new FileOutputStream("C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\Reglas\\Reglas.pdf");
        int algoritmo = modeloseleccionado;
        System.out.println("corpues : " + corpusSeleccionado + "  Algoritmo: " + modeloSeleccionado);
//
        try {
            switch (claseSeleccionada) {
                case 1:
                    switch (corpusSeleccionado) {

                        case "c":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a SDMap" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando...");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                    texto.add("Conjunto de datos: C" + "\n");
                                    texto.add("Algoritmo: SDMap" + "\n");
                                    texto.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto.add("Hospital: " + this.etiquetaSeleccionada + "\n");
                                    texto.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");

                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                   
                                    
                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {

                                        
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;

                                case 2://--------------------------------------------

                                    System.out.println("Entro a BSD: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando...");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                    texto1.add("Conjunto de datos: C" + "\n");
                                    texto1.add("Algoritmo: SDMap" + "\n");
                                    texto1.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto1.add("Hospital: " + this.etiquetaSeleccionada + "\n");
                                    texto1.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");

                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);
                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;

                            
                               case 3://--------------------------------------------

                                    System.out.println("Entro a BeamSearch: " + algoritmo);
                                    Document documento3 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento3, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento3.open();

                                    System.out.println("Esta entrando...");

                                    Paragraph espacioD = new Paragraph();
                                    espacioD.add("\n\n");
                                    documento3.add(espacioD);

                                    Paragraph titulo3 = new Paragraph();
                                    titulo3.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo3.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo3.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento3.add(titulo3);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioD2 = new Paragraph();
                                    espacioD2.add("\n\n");
                                    documento3.add(espacioD2);
                                    //Columnas
                                    PdfPTable tabla3 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla3.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla3.setWidths(new float[]{15, 40});
                                    Paragraph columna3 = new Paragraph("No. Regla");
                                    columna3.getFont().setStyle(Font.BOLD);
                                    columna3.getFont().setSize(10);
                                    tabla3.addCell(columna3);

                                    Paragraph columna4 = new Paragraph("Regla");
                                    columna4.getFont().setStyle(Font.BOLD);
                                    columna4.getFont().setSize(10);
                                    tabla3.addCell(columna4);

                                    Paragraph texto2 = new Paragraph();
                                    texto2.add("Conjunto de datos: C" + "\n");
                                    texto2.add("Algoritmo: BeamSearch" + "\n");
                                    texto2.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto2.add("Hospital seleccionado: " + this.etiquetaSeleccionada + "\n");
                                    texto2.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");

                                    texto2.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto2.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto2.add("Reglas interpretadas en lenguaje natural");
                                    documento3.add(texto2);
                                    Paragraph espacio5 = new Paragraph();
                                    espacio5.add("\n\n");
                                    documento3.add(espacio5);
                                    int y3 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y3 = i + 1;

                                        tabla3.addCell("" + y3);
                                        tabla3.addCell(reglasPDFA.get(i));

                                    }
                                    documento3.add(tabla3);
                                    //Cierra el documento.
                                    documento3.close();

                                    break;

                    
                            }
                            break;
                        case "d":
                            System.out.println("Corpus: " + corpusSeleccionado);
                            switch (algoritmo) {
                                case 1:
                                    System.out.println("Entro a D SDMap" + algoritmo);
                                    Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento.open();

                                    System.out.println("Esta entrando...");

                                    Paragraph espacio = new Paragraph();
                                    espacio.add("\n\n");
                                    documento.add(espacio);

                                    Paragraph titulo = new Paragraph();
                                    titulo.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento.add(titulo);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacio2 = new Paragraph();
                                    espacio2.add("\n\n");
                                    documento.add(espacio2);
                                    //Columnas
                                    PdfPTable tabla = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla.setWidths(new float[]{15, 40});
                                    Paragraph column1 = new Paragraph("No. Regla");
                                    column1.getFont().setStyle(Font.BOLD);
                                    column1.getFont().setSize(10);
                                    tabla.addCell(column1);

                                    Paragraph column2 = new Paragraph("Regla");
                                    column2.getFont().setStyle(Font.BOLD);
                                    column2.getFont().setSize(10);
                                    tabla.addCell(column2);

                                    Paragraph texto = new Paragraph();
                                     texto.add("Conjunto de datos: D" + "\n");
                                    texto.add("Algoritmo: BeamSearch" + "\n");
                                    texto.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto.add("Hospital seleccionado: " + this.etiquetaSeleccionada + "\n");
                                    texto.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");


                                    texto.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto.add("Reglas interpretadas en lenguaje natural");
                                    documento.add(texto);
                                    Paragraph espacio3 = new Paragraph();
                                    espacio3.add("\n\n");
                                    documento.add(espacio3);

                                    int y = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y = i + 1;

                                        tabla.addCell("" + y);
                                        tabla.addCell(reglasPDFA.get(i));

                                    }
                                    documento.add(tabla);
                                    //se cierra el documento.
                                    documento.close();

                                    break;
                                case 2://---------------------------------------------
                                    System.out.println("Entro a D BSD: " + algoritmo);
                                    Document documento2 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento2, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento2.open();

                                    System.out.println("Esta entrando ...");

                                    Paragraph espacioC = new Paragraph();
                                    espacioC.add("\n\n");
                                    documento2.add(espacioC);

                                    Paragraph titulo1 = new Paragraph();
                                    titulo1.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo1.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo1.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento2.add(titulo1);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioC2 = new Paragraph();
                                    espacioC2.add("\n\n");
                                    documento2.add(espacioC2);
                                    //Columnas
                                    PdfPTable tabla2 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla2.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla2.setWidths(new float[]{15, 40});
                                    Paragraph columna1 = new Paragraph("No. Regla");
                                    columna1.getFont().setStyle(Font.BOLD);
                                    columna1.getFont().setSize(10);
                                    tabla2.addCell(columna1);

                                    Paragraph columna2 = new Paragraph("Regla");
                                    columna2.getFont().setStyle(Font.BOLD);
                                    columna2.getFont().setSize(10);
                                    tabla2.addCell(columna2);

                                    Paragraph texto1 = new Paragraph();
                                      texto1.add("Conjunto de datos: D" + "\n");
                                    texto1.add("Algoritmo: BeamSearch" + "\n");
                                    texto1.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto1.add("Hospital seleccionado: " + this.etiquetaSeleccionada + "\n");
                                    texto1.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");


                                    texto1.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto1.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto1.add("Reglas interpretadas en lenguaje natural");
                                    documento2.add(texto1);
                                    Paragraph espacio4 = new Paragraph();
                                    espacio4.add("\n\n");
                                    documento2.add(espacio4);

                                    int y2 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y2 = i + 1;

                                        tabla2.addCell("" + y2);
                                        tabla2.addCell(reglasPDFA.get(i));

                                    }
                                    documento2.add(tabla2);
                                    //Cierra el documento.
                                    documento2.close();

                                    break;
                                    
                                     case 3://---------------------------------------------
                                    System.out.println("Entro a D BeamSearch: " + algoritmo);
                                    Document documento3 = new Document(PageSize.LETTER, 80, 80, 75, 75);
//                            //writer es declarado como el método utilizado para escribir en el archivo.
//                            PdfWriter writer = null;
                                    PdfWriter.getInstance(documento3, pdf).setInitialLeading(20);

                                    //Abrir documento a editar.
                                    documento3.open();

                                    System.out.println("Esta entrando ...");

                                    Paragraph espacioC1 = new Paragraph();
                                    espacioC1.add("\n\n");
                                    documento3.add(espacioC1);

                                    Paragraph titulo2 = new Paragraph();
                                    titulo2.setAlignment(Paragraph.ALIGN_CENTER);
                                    titulo2.setFont(FontFactory.getFont("Times New Roman", 24, Font.BOLD));
                                    titulo2.add("REGLAS OBTENIDAS");

                                    try {
                                        //Se agrega el titulo al documento.
                                        documento3.add(titulo2);
                                    } catch (DocumentException ex) {
                                        ex.getMessage();
                                    }

                                    Paragraph espacioD = new Paragraph();
                                    espacioD.add("\n\n");
                                    documento3.add(espacioD);
                                    //Columnas
                                    PdfPTable tabla3 = new PdfPTable(2);
                                    //Porcentaje a la tabla (tamaño ancho).
                                    tabla3.setWidthPercentage(100);
                                    //Ancho de cada columna.
                                    tabla3.setWidths(new float[]{15, 40});
                                    Paragraph columna3 = new Paragraph("No. Regla");
                                    columna3.getFont().setStyle(Font.BOLD);
                                    columna3.getFont().setSize(10);
                                    tabla3.addCell(columna3);

                                    Paragraph columna4 = new Paragraph("Regla");
                                    columna4.getFont().setStyle(Font.BOLD);
                                    columna4.getFont().setSize(10);
                                    tabla3.addCell(columna4);

                                    Paragraph texto2 = new Paragraph();
                                      texto2.add("Conjunto de datos: D" + "\n");
                                    texto2.add("Algoritmo: BeamSearch" + "\n");
                                    texto2.add("Función de calidad: " + this.medidaSeleccionada+ "\n");
                                    texto2.add("Hospital seleccionado: " + this.etiquetaSeleccionada + "\n");
                                    texto2.add("Numero de subgrupos: " + this.numeroSeleccionado + "\n");


                                    texto2.setAlignment(Paragraph.ALIGN_CENTER);
                                    texto2.setFont(FontFactory.getFont("Times New Roman", 12, Font.BOLD));
                                    texto2.add("Reglas interpretadas en lenguaje natural");
                                    documento3.add(texto2);
                                    Paragraph espacio5 = new Paragraph();
                                    espacio5.add("\n\n");
                                    documento3.add(espacio5);

                                    int y3 = 0;

                                    //escribe la regla en la tabla
                                    for (int i = 0; i < reglasPDFA.size(); i++) {
                                        y3 = i + 1;

                                        tabla3.addCell("" + y3);
                                        tabla3.addCell(reglasPDFA.get(i));

                                    }
                                    documento3.add(tabla3);
                                    //Cierra el documento.
                                    documento3.close();

                                    break;
                            }
                            break;
                    }
                    break;

               
                   

                //--------------------------------
            }
            //pdf.add(phrase);
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
            switch (claseSeleccionada) {
                
                case 1:

                    switch (corpusSeleccionado) {
                        case "c":
                            switch (medidaSeleccionada) {
                                case 1:
                                    phrase.add("__________________Reglas interpretadas en lenguaje Natural____________________" + "\n" + "\n");
                                    phrase.add("                  Conjunto de datos: " + corpusSeleccionado + "\n");
                                    phrase.add("                  Algoritmo: SDMap" + "\n");

                                    phrase.add("                  Numero de subgrupos: " + this.reglasSeleccionadas + "\n");
                                    phrase.add("                  Medida de interés: WRAccQF" +  "\n");
                                    phrase.add("                  Numero de atributos: " + this.numeroSeleccionado + "\n");
                                    break;
                                case 2:
                                    phrase.add("___________________Reglas interpretadas en lenguaje Natural_____________" + "\n" + "\n");
                                    phrase.add("                   Conjunto de datos: C" + "\n");
                                    phrase.add("                   Algoritmo: BSD" + "\n");
                                     phrase.add("                  Numero de subgrupos: " + this.reglasSeleccionadas + "\n");
                                    phrase.add("                   Medida de interés: ChiSquareQF" + "\n");
                                    phrase.add("                   Numero de atributos: " + this.numeroSeleccionado + "\n");

                                    break;
                                    
                                 case 3:
                                    phrase.add("___________________Reglas interpretadas en lenguaje Natural_____________" + "\n" + "\n");
                                    phrase.add("                   Conjunto de datos: C" + "\n");
                                    phrase.add("                   Algoritmo: BeamSearch" + "\n");
                                     phrase.add("                  Medida de interés: " + this.reglasSeleccionadas + "\n");
                                    phrase.add("                  Función de calidad: BinomialQF" + "\n");
                                    phrase.add("                  Numero de atributos: " + this.numeroSeleccionado + "\n");

                                    break;

                            }

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
