
import hospital.asociacion.model.asociacionUtilidades;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean
public class ChartView2 implements Serializable {

    private LineChartModel animatedModel1;
    private BarChartModel animatedModel2;
    private BarChartModel animatedModelCasos;

    private BarChartModel animatedModelArea;
    private BarChartModel animatedModelAnosPractica;
    private BarChartModel animatedModelHallazgosP7;
    private BarChartModel animatedModelHallazgosP8;
    private BarChartModel animatedModelHallazgosP11;
    private BarChartModel animatedModelMCCSI;
    private BarChartModel animatedModelMCCNO;
    private BarChartModel animatedModelQSA;
    private BarChartModel animatedModelCausasMedico;
    private BarChartModel animatedFMRSA;
    private final asociacionUtilidades oAsociacionUtilidades;
    int instanciasC;
    int casos0;
    int casosMenos5;
    int casosEnte6y10;
    int casosEntre11y20;
    int casosMas20;
    int especialistas;
    int medicosG;
    int estudiantes;
    int maxCantCaso;
    int totalmenteDacuerdo;
    int deacuerdo;
    int incierto;
    int endesacuerdo;
    int totalmentedesacuerdo;
    int interno;
    int residente;
    int adscrito;
    int menosde5;
    int de5a10;
    int de11a15;
    int de16a20;
    int masde20;
    int totalmenteDacuerdo8;
    int deacuerdo8;
    int incierto8;
    int endesacuerdo8;
    int totalmentedesacuerdo8;
    int totalmenteDacuerdo11;
    int deacuerdo11;
    int incierto11;
    int endesacuerdo11;
    int totalmentedesacuerdo11;
    int p17_r1;
    int p17_r2;
    int p17_r3;
    int p17_r4;
    int p17_r5;
    int p17_r6;
    // int p17_r7;
    int p18_r1;
    int p18_r2;
    int p18_r3;
    // int p18_r4;
    int p18_r5;
    int p18_r6;
    int p18_r7;
    // int p18_r8;
    int p19_r1;
    int p19_r2;
    int p19_r3;
    int p19_r4;
    int p19_r5;
    int p19_r6;
    int p20_r1;
    int p20_r2;
    int p20_r3;
    int p20_r4;
    int p20_r5;
    int p20_r6;
    int p20_r7;
    int p20_r8;
    int p20_r9;
    int p20_r10;
    int p21_r1;
    int p21_r2;
    int p21_r3;
    int p21_r4;
    int p21_r5;
    int p21_r6;

    int p21_r10;

    public ChartView2() throws Exception {
        oAsociacionUtilidades = new asociacionUtilidades();
        this.estudiantes = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_8");
        this.medicosG = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_2");
        this.especialistas = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_1");
        this.casosMas20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_5");
        this.casosEntre11y20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_4");
        this.casosEnte6y10 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_3");
        this.casosMenos5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_2");
        this.casos0 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_1");
        this.totalmenteDacuerdo = oAsociacionUtilidades.cantInstancias_dadoColumna("p3_1");
        this.deacuerdo = oAsociacionUtilidades.cantInstancias_dadoColumna("p3_2");
        this.incierto = oAsociacionUtilidades.cantInstancias_dadoColumna("p3_3");
        this.endesacuerdo = oAsociacionUtilidades.cantInstancias_dadoColumna("p3_4");
        this.totalmentedesacuerdo = oAsociacionUtilidades.cantInstancias_dadoColumna("p3_5");
        this.interno = oAsociacionUtilidades.cantInstancias_dadoColumna("p15_1");
        this.residente = oAsociacionUtilidades.cantInstancias_dadoColumna("p15_2");
        this.adscrito = oAsociacionUtilidades.cantInstancias_dadoColumna("p15_3");
        this.menosde5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p1_1");
        this.de5a10 = oAsociacionUtilidades.cantInstancias_dadoColumna("p1_2");
        this.de11a15 = oAsociacionUtilidades.cantInstancias_dadoColumna("p1_3");
        this.de16a20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p1_4");
        this.masde20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p1_5");
        this.totalmenteDacuerdo8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p4_1");
        this.deacuerdo8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p4_2");
        this.incierto8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p4_3");
        this.endesacuerdo8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p4_4");
        this.totalmentedesacuerdo8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p4_5");
        this.totalmenteDacuerdo11 = oAsociacionUtilidades.cantInstancias_dadoColumna("p5_1");
        this.deacuerdo11 = oAsociacionUtilidades.cantInstancias_dadoColumna("p5_2");
        this.incierto11 = oAsociacionUtilidades.cantInstancias_dadoColumna("p5_3");
        this.endesacuerdo11 = oAsociacionUtilidades.cantInstancias_dadoColumna("p5_4");
        this.totalmentedesacuerdo11 = oAsociacionUtilidades.cantInstancias_dadoColumna("p5_5");
        this.p17_r1 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_1");
        this.p17_r2 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_2");
        this.p17_r3 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_3");
        this.p17_r4 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_4");
        this.p17_r5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_5");
        this.p17_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_6");
        //this.p17_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p8_7");
        this.p18_r1 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_1");
        this.p18_r2 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_2");
        this.p18_r3 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_3");
        //this.p18_r4 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_4");
        this.p18_r5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_5");
        this.p18_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_6");
        this.p18_r7 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_7");
        //this.p18_r8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p9_8");
        this.p19_r1 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_1");
        this.p19_r2 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_2");
        this.p19_r3 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_3");
        this.p19_r4 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_4");
        this.p19_r5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_5");
        this.p19_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p10_6");
        this.p20_r1 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_1");
        this.p20_r2 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_2");
        this.p20_r3 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_3");
        this.p20_r4 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_4");
        this.p20_r5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_5");
        this.p20_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_6");
        this.p20_r7 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_7");
        this.p20_r8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_8");
        this.p20_r9 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_9");
        this.p20_r10 = oAsociacionUtilidades.cantInstancias_dadoColumna("p11_10");
        this.p21_r1 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_1");
        this.p21_r2 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_2");

        this.p21_r3 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_3");
        this.p21_r4 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_4");
        this.p21_r5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_5");
        this.p21_r6 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_6");
        // this.p21_r7 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_7");
        //this.p21_r8 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_8");
        // this.p21_r9 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_9");
        this.p21_r10 = oAsociacionUtilidades.cantInstancias_dadoColumna("p12_10");

        this.instanciasC = this.instanciasC = oAsociacionUtilidades.cantInstanciasC();

        maxCantCaso = casos0;
        if (maxCantCaso < casosMenos5) {
            maxCantCaso = casosMenos5;
        }
        if (maxCantCaso < casosEnte6y10) {
            maxCantCaso = casosEnte6y10;
        }
        if (maxCantCaso < casosEntre11y20) {
            maxCantCaso = casosEntre11y20;
        }
        if (maxCantCaso < casosMas20) {
            maxCantCaso = casosMas20;
        }

    }

    @PostConstruct
    public void init() {
        createAnimatedModels();
    }

    public LineChartModel getAnimatedModel1() {
        return animatedModel1;
    }

    public BarChartModel getAnimatedModel2() {
        return animatedModel2;
    }

    public BarChartModel getAnimatedModelCasos() {
        return animatedModelCasos;
    }

    public asociacionUtilidades getoAsociacionUtilidades() {
        return oAsociacionUtilidades;
    }

    public BarChartModel getAnimatedModelHallazgosP7() {
        return animatedModelHallazgosP7;
    }

    public void setAnimatedModelHallazgosP7(BarChartModel animatedModelHallazgosP7) {
        this.animatedModelHallazgosP7 = animatedModelHallazgosP7;
    }

    public BarChartModel getAnimatedModelArea() {
        return animatedModelArea;
    }

    public void setAnimatedModelArea(BarChartModel animatedModelArea) {
        this.animatedModelArea = animatedModelArea;
    }

    public BarChartModel getAnimatedModelAnosPractica() {
        return animatedModelAnosPractica;
    }

    public void setAnimatedModelAnosPractica(BarChartModel animatedModelAnosPractica) {
        this.animatedModelAnosPractica = animatedModelAnosPractica;
    }

    public BarChartModel getAnimatedModelHallazgosP8() {
        return animatedModelHallazgosP8;
    }

    public void setAnimatedModelHallazgosP8(BarChartModel animatedModelHallazgosP8) {
        this.animatedModelHallazgosP8 = animatedModelHallazgosP8;
    }

    public BarChartModel getAnimatedModelHallazgosP11() {
        return animatedModelHallazgosP11;
    }

    public void setAnimatedModelHallazgosP11(BarChartModel animatedModelHallazgosP11) {
        this.animatedModelHallazgosP11 = animatedModelHallazgosP11;
    }

    public BarChartModel getAnimatedModelMCCSI() {
        return animatedModelMCCSI;
    }

    public void setAnimatedModelMCCSI(BarChartModel animatedModelMCCSI) {
        this.animatedModelMCCSI = animatedModelMCCSI;
    }

    public BarChartModel getAnimatedModelMCCNO() {
        return animatedModelMCCNO;
    }

    public void setAnimatedModelMCCNO(BarChartModel animatedModelMCCNO) {
        this.animatedModelMCCNO = animatedModelMCCNO;
    }

    public BarChartModel getAnimatedModelQSA() {
        return animatedModelQSA;
    }

    public void setAnimatedModelQSA(BarChartModel animatedModelQSA) {
        this.animatedModelQSA = animatedModelQSA;
    }

    public BarChartModel getAnimatedModelCausasMedico() {
        return animatedModelCausasMedico;
    }

    public void setAnimatedModelCausasMedico(BarChartModel animatedModelCausasMedico) {
        this.animatedModelCausasMedico = animatedModelCausasMedico;
    }

    public BarChartModel getAnimatedFMRSA() {
        return animatedFMRSA;
    }

    public void setAnimatedFMRSA(BarChartModel animatedFMRSA) {
        this.animatedFMRSA = animatedFMRSA;
    }

    private void createAnimatedModels() {

        animatedModel1 = initLinearModel();
        //animatedModel1.setTitle("Autopsias en H.R.R.B");
        animatedModel1.setAnimate(true);
        animatedModel1.setLegendPosition("ne");
        Axis yAxis = animatedModel1.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(16);
        Axis xAxis = animatedModel1.getAxis(AxisType.X);
        xAxis.setMin(2004);
        xAxis.setMax(2020);

        animatedModel2 = initBarModel();
        //animatedModel2.setTitle("Realización de autopsias a nivel mundial");
        animatedModel2.setAnimate(true);
        animatedModel2.setLegendPosition("ne");
        yAxis = animatedModel2.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelCasos = initBarModelCasos();
        animatedModelCasos.setAnimate(true);
        animatedModelCasos.setLegendPosition("ne");
        yAxis = animatedModelCasos.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);
        //-----------nueva
        animatedModelHallazgosP7 = initBarModelHallazgos();
        animatedModelHallazgosP7.setAnimate(true);
        animatedModelHallazgosP7.setLegendPosition("ne");
        yAxis = animatedModelHallazgosP7.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelHallazgosP8 = initBarModelHallazgos8();
        animatedModelHallazgosP8.setAnimate(true);
        animatedModelHallazgosP8.setLegendPosition("ne");
        yAxis = animatedModelHallazgosP8.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelHallazgosP11 = initBarModelHallazgos11();
        animatedModelHallazgosP11.setAnimate(true);
        animatedModelHallazgosP11.setLegendPosition("ne");
        yAxis = animatedModelHallazgosP11.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelAnosPractica = initBarModelAnosPractica();
        animatedModelAnosPractica.setAnimate(true);
        animatedModelAnosPractica.setLegendPosition("ne");
        yAxis = animatedModelAnosPractica.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelArea = initBarModelArea();
        animatedModelArea.setAnimate(true);
        animatedModelArea.setLegendPosition("ne");
        yAxis = animatedModelArea.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelMCCSI = initBarModelMCCSI();
        animatedModelMCCSI.setAnimate(true);
        animatedModelMCCSI.setLegendPosition("ne");
        yAxis = animatedModelMCCSI.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelMCCNO = initBarModelMCCNO();
        animatedModelMCCNO.setAnimate(true);
        animatedModelMCCNO.setLegendPosition("ne");
        yAxis = animatedModelMCCNO.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelQSA = initBarModelQSA();
        animatedModelQSA.setAnimate(true);
        animatedModelQSA.setLegendPosition("ne");
        yAxis = animatedModelQSA.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedModelCausasMedico = initBarModelCausasMedico();
        animatedModelCausasMedico.setAnimate(true);
        animatedModelCausasMedico.setLegendPosition("ne");
        yAxis = animatedModelCausasMedico.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

        animatedFMRSA = initBarModelFMRSA();
        animatedFMRSA.setAnimate(true);
        animatedFMRSA.setLegendPosition("ne");
        yAxis = animatedFMRSA.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(instanciasC);

    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();

        ChartSeries totalEncuestados = new ChartSeries();
        totalEncuestados.setLabel("Total");
        totalEncuestados.set("Encuestados", instanciasC);

        ChartSeries especialista = new ChartSeries();
        especialista.setLabel("Especialista");
        especialista.set("Especialista", especialistas);

        ChartSeries medicoGeneral = new ChartSeries();
        medicoGeneral.setLabel("Médico general");
        medicoGeneral.set("Médico", medicosG);

        ChartSeries estudiante = new ChartSeries();
        estudiante.setLabel("Estudiante medicina");
        estudiante.set("Estudiante", estudiantes);

        model.addSeries(totalEncuestados);
        model.addSeries(especialista);
        model.addSeries(medicoGeneral);
        model.addSeries(estudiante);
        return model;
    }

    private BarChartModel initBarModelCasos() {
        BarChartModel model = new BarChartModel();

        ChartSeries totalEncuestados = new ChartSeries();
        totalEncuestados.setLabel("Ningún Caso");
        totalEncuestados.set("Experiencia en casos de autopsia", casos0);

        ChartSeries especialista = new ChartSeries();
        especialista.setLabel("Menos de 5 casos");
        especialista.set("Menos de 5 casos", casosMenos5);

        ChartSeries medicoGeneral = new ChartSeries();
        medicoGeneral.setLabel("Entre 6 y 10 casos");
        medicoGeneral.set("Entre 6 y 10 casos", casosEnte6y10);

        ChartSeries estudiante = new ChartSeries();
        estudiante.setLabel("Entre 11 y 20 casos");
        estudiante.set("Entre 11 y 20 casos", casosEntre11y20);

        ChartSeries casos5 = new ChartSeries();
        casos5.setLabel("Más de 20 casos");
        casos5.set("Más de 20 casos", casosMas20);

        model.addSeries(totalEncuestados);
        model.addSeries(especialista);
        model.addSeries(medicoGeneral);
        model.addSeries(estudiante);
        model.addSeries(casos5);
        return model;
    }

    //------------nueva------
    private BarChartModel initBarModelHallazgos() {
        BarChartModel model = new BarChartModel();

        ChartSeries totaldeacuerdo = new ChartSeries();
        totaldeacuerdo.setLabel("Totalmente de acuerdo");
        totaldeacuerdo.set("Pueden ser discrepantes con los diagnósticos clínicos", totalmenteDacuerdo);

        ChartSeries deAcuerdo = new ChartSeries();
        deAcuerdo.setLabel("De acuerdo");
        deAcuerdo.set("De acuerdo", deacuerdo);

        ChartSeries incierto1 = new ChartSeries();
        incierto1.setLabel("Incierto");
        incierto1.set("Incierto", incierto);

        ChartSeries desacuerdo = new ChartSeries();
        desacuerdo.setLabel("En desacuerdo");
        desacuerdo.set("En desacuerdo", endesacuerdo);

        ChartSeries totalDesacuerdo = new ChartSeries();
        totalDesacuerdo.setLabel("Totalmente en desacuerdo");
        totalDesacuerdo.set("Totalmente en desacuerdo", totalmentedesacuerdo);

        model.addSeries(totaldeacuerdo);
        model.addSeries(deAcuerdo);
        model.addSeries(incierto1);
        model.addSeries(desacuerdo);
        model.addSeries(totalDesacuerdo);

        return model;
    }

    private BarChartModel initBarModelHallazgos8() {
        BarChartModel model = new BarChartModel();

        ChartSeries totaldeacuerdo = new ChartSeries();
        totaldeacuerdo.setLabel("Totalmente de acuerdo");
        totaldeacuerdo.set("Dan origen a casos de arbitraje", totalmenteDacuerdo8);

        ChartSeries deAcuerdo = new ChartSeries();
        deAcuerdo.setLabel("De acuerdo");
        deAcuerdo.set("De acuerdo", deacuerdo8);

        ChartSeries incierto1 = new ChartSeries();
        incierto1.setLabel("Incierto");
        incierto1.set("Incierto", incierto8);

        ChartSeries desacuerdo = new ChartSeries();
        desacuerdo.setLabel("En desacuerdo");
        desacuerdo.set("En desacuerdo", endesacuerdo8);

        ChartSeries totalDesacuerdo = new ChartSeries();
        totalDesacuerdo.setLabel("Totalmente en desacuerdo");
        totalDesacuerdo.set("Totalmente en desacuerdo", totalmentedesacuerdo8);

        model.addSeries(totaldeacuerdo);
        model.addSeries(deAcuerdo);
        model.addSeries(incierto1);
        model.addSeries(desacuerdo);
        model.addSeries(totalDesacuerdo);

        return model;
    }

    private BarChartModel initBarModelHallazgos11() {
        BarChartModel model = new BarChartModel();

        ChartSeries totaldeacuerdo = new ChartSeries();
        totaldeacuerdo.setLabel("Totalmente de acuerdo");
        totaldeacuerdo.set("Dan origen a casos de demandas", totalmenteDacuerdo11);

        ChartSeries deAcuerdo = new ChartSeries();
        deAcuerdo.setLabel("De acuerdo");
        deAcuerdo.set("De acuerdo", deacuerdo11);

        ChartSeries incierto1 = new ChartSeries();
        incierto1.setLabel("Incierto");
        incierto1.set("Incierto", incierto11);

        ChartSeries desacuerdo = new ChartSeries();
        desacuerdo.setLabel("En desacuerdo");
        desacuerdo.set("En desacuerdo", endesacuerdo11);

        ChartSeries totalDesacuerdo = new ChartSeries();
        totalDesacuerdo.setLabel("Totalmente en desacuerdo");
        totalDesacuerdo.set("Totalmente en desacuerdo", totalmentedesacuerdo11);

        model.addSeries(totaldeacuerdo);
        model.addSeries(deAcuerdo);
        model.addSeries(incierto1);
        model.addSeries(desacuerdo);
        model.addSeries(totalDesacuerdo);

        return model;
    }

    private BarChartModel initBarModelArea() {
        BarChartModel model = new BarChartModel();

        ChartSeries aInterno = new ChartSeries();
        aInterno.setLabel("Interno");
        aInterno.set("Área a la que pertenecen", interno);

        ChartSeries aresidente = new ChartSeries();
        aresidente.setLabel("Residente");
        aresidente.set("Residente", residente);

        ChartSeries aAdscrito = new ChartSeries();
        aAdscrito.setLabel("Adscrito");
        aAdscrito.set("Adscrito", adscrito);

        model.addSeries(aInterno);
        model.addSeries(aresidente);
        model.addSeries(aAdscrito);

        return model;
    }

    private BarChartModel initBarModelAnosPractica() {
        BarChartModel model = new BarChartModel();

        ChartSeries menosDe5 = new ChartSeries();
        menosDe5.setLabel("Menos de 5");
        menosDe5.set("Años de práctica", menosde5);

        ChartSeries de5A10 = new ChartSeries();
        de5A10.setLabel("5-10");
        de5A10.set("5-10", de5a10);

        ChartSeries de11A15 = new ChartSeries();
        de11A15.setLabel("11-15");
        de11A15.set("11-15", de11a15);

        ChartSeries de16A20 = new ChartSeries();
        de16A20.setLabel("16-20");
        de16A20.set("16-20", de16a20);

        ChartSeries masDe20 = new ChartSeries();
        masDe20.setLabel("Más de 20");
        masDe20.set("Más de 20", masde20);

        model.addSeries(menosDe5);
        model.addSeries(de5A10);
        model.addSeries(de11A15);
        model.addSeries(de16A20);
        model.addSeries(masDe20);

        return model;
    }

    private BarChartModel initBarModelMCCSI() {
        BarChartModel model = new BarChartModel();

        ChartSeries mReligioso = new ChartSeries();
        mReligioso.setLabel("Motivos religiosos");
        mReligioso.set("Rechazo por parte del familiar", p17_r1);

        ChartSeries mPolitico = new ChartSeries();
        mPolitico.setLabel("Motivos políticos");
        mPolitico.set("Motivos políticos", p17_r2);

        ChartSeries mMorales = new ChartSeries();
        mMorales.setLabel("Motivos morales");
        mMorales.set("Motivos morales", p17_r3);

        ChartSeries mNSC = new ChartSeries();
        mNSC.setLabel("No solicitar correctamente");
        mNSC.set("No solicitar correctamente", p17_r4);

        ChartSeries mInconformidad = new ChartSeries();
        mInconformidad.setLabel("Inconformidad de los familiares con la atención prestada al paciente");
        mInconformidad.set("Inconformidad de los familiares con la atención prestada al paciente", p17_r5);

        ChartSeries mDC = new ChartSeries();
        mDC.setLabel("Deficiente comunicación del médico con el paciente y sus familiares");
        mDC.set("Deficiente comunicación del médico con el paciente y sus familiares", p17_r6);

//        ChartSeries mCircunstancial = new ChartSeries();
//        mCircunstancial.setLabel("Circunstancial");
//        mCircunstancial.set("Circunstancial", p17_r7);
        model.addSeries(mReligioso);
        model.addSeries(mPolitico);
        model.addSeries(mMorales);
        model.addSeries(mNSC);
        model.addSeries(mInconformidad);
        model.addSeries(mDC);
//        model.addSeries(mCircunstancial);

        return model;
    }

    private BarChartModel initBarModelMCCNO() {
        BarChartModel model = new BarChartModel();

        ChartSeries mFDRH = new ChartSeries();
        mFDRH.setLabel("Falta de recursos humanos");
        mFDRH.set("Por qué no se realizan autopsias", p18_r1);

        ChartSeries mFM = new ChartSeries();
        mFM.setLabel("Falta de material");
        mFM.set("Falta de material", p18_r2);

        ChartSeries mFRF = new ChartSeries();
        mFRF.setLabel("Falta de recursos financieros");
        mFRF.set("Falta de recursos financieros", p18_r3);

//        ChartSeries mNSS = new ChartSeries();
//        mNSS.setLabel("No se solicitan");
//        mNSS.set("No se solicitan", p18_r4);
        ChartSeries mDSPD = new ChartSeries();
        mDSPD.setLabel("Desconocimiento de su utilidad didáctica");
        mDSPD.set("Desconocimiento de su utilidad didáctica", p18_r5);

        ChartSeries mNP = new ChartSeries();
        mNP.setLabel("Se considera que los nuevos procedimientos diagnósticos y otros estudios especializados eliminan la necesidad de su práctica");
        mNP.set("Se considera que los nuevos procedimientos diagnósticos y otros estudios especializados eliminan la necesidad de su práctica", p18_r6);

        ChartSeries mDAP = new ChartSeries();
        mDAP.setLabel("Desinterés del área de patología");
        mDAP.set("Desinterés del área de patología", p18_r7);

//        ChartSeries mNF = new ChartSeries();
//        mDAP.setLabel("Negación de los familiares");
//        mDAP.set("Negación de los familiares", p18_r8);
        model.addSeries(mFDRH);
        model.addSeries(mFM);
        model.addSeries(mFRF);
//        model.addSeries(mNSS);
        model.addSeries(mDSPD);
        model.addSeries(mNP);
        model.addSeries(mDAP);
//        model.addSeries(mNF);

        return model;
    }

    private BarChartModel initBarModelQSA() {
        BarChartModel model = new BarChartModel();

        ChartSeries medico = new ChartSeries();
        medico.setLabel("Médico");
        medico.set("Solicitud de autopsia", p19_r1);

        ChartSeries enfermera = new ChartSeries();
        enfermera.setLabel("Enfermera");
        enfermera.set("Enfermera", p19_r2);

        ChartSeries tSocial = new ChartSeries();
        tSocial.setLabel("Trabajadora Social");
        tSocial.set("Trabajadora Social", p19_r3);

        ChartSeries intitucion = new ChartSeries();
        intitucion.setLabel("La institución");
        intitucion.set("La institución", p19_r4);

        ChartSeries familiar = new ChartSeries();
        familiar.setLabel("Familiar");
        familiar.set("Familiar", p19_r5);

        ChartSeries CF = new ChartSeries();
        CF.setLabel("Criminología Forence");
        CF.set("Criminología Forence", p18_r6);

        model.addSeries(medico);
        model.addSeries(enfermera);
        model.addSeries(tSocial);
        model.addSeries(intitucion);
        model.addSeries(familiar);
        model.addSeries(CF);

        return model;
    }

    private BarChartModel initBarModelCausasMedico() {
        BarChartModel model = new BarChartModel();

        ChartSeries mFDRH = new ChartSeries();
        mFDRH.setLabel("Interes");
        mFDRH.set("Solicitud de autopsias por parte del médico", p20_r1);

        ChartSeries mFM = new ChartSeries();
        mFM.setLabel("Error médico");
        mFM.set("Error médico", p20_r2);

        ChartSeries mFRF = new ChartSeries();
        mFRF.setLabel("Medicamentos mal recetados");
        mFRF.set("Medicamentos mal recetados", p20_r3);

        ChartSeries mNSS = new ChartSeries();
        mNSS.setLabel("Negligencia médica");
        mNSS.set("Negligencia médica", p20_r4);

        ChartSeries mDSPD = new ChartSeries();
        mDSPD.setLabel("Diagnóstico erróneo");
        mDSPD.set("Diagnóstico erróneo", p20_r5);

        ChartSeries mNP = new ChartSeries();
        mNP.setLabel("Orientación diagnóstica");
        mNP.set("Orientación diagnóstica", p20_r6);

        ChartSeries mDAP = new ChartSeries();
        mDAP.setLabel("Educación Médica Continua");
        mDAP.set("Educación Médica Continua", p20_r7);

        ChartSeries mNF = new ChartSeries();
        mNF.setLabel("Problema Médico Legal");
        mNF.set("Problema Médico Legal", p20_r8);

        ChartSeries ADO = new ChartSeries();
        ADO.setLabel("Acoso por delincuencia organizada");
        ADO.set("Acoso por delincuencia organizada", p20_r9);

        ChartSeries ensenanza = new ChartSeries();
        ensenanza.setLabel("Enseñanza");
        ensenanza.set("Enseñanza", p20_r10);

        model.addSeries(mFDRH);
        model.addSeries(mFM);
        model.addSeries(mFRF);
        model.addSeries(mNSS);
        model.addSeries(mDSPD);
        model.addSeries(mNP);
        model.addSeries(mDAP);
        model.addSeries(mNF);
        model.addSeries(ADO);
        model.addSeries(ensenanza);

        return model;
    }

    private BarChartModel initBarModelFMRSA() {
        BarChartModel model = new BarChartModel();

        ChartSeries mFDRH = new ChartSeries();
        mFDRH.setLabel("Pedir el concentimiento desde que ingresa al hospital");
        mFDRH.set("solcitud de un estudio de autopsia", p21_r1);

        ChartSeries mFM = new ChartSeries();
        mFM.setLabel("Solicitar el consentimiento inmediatamente después de la defunción");
        mFM.set("Solicitar el consentimiento inmediatamente después de la defunción", p21_r2);

        ChartSeries mFRF = new ChartSeries();
        mFRF.setLabel("Que la institución a nivel regional o estatal reglamente la practica de autopsias de forma obligatoria ");
        mFRF.set("Que la institución a nivel regional o estatal reglamente la practica de autopsias de forma obligatoria ", p21_r3);

        ChartSeries mNSS = new ChartSeries();
        mNSS.setLabel("El médico tratante sea quién la solicite");
        mNSS.set("El médico tratante sea quién la solicite", p21_r4);

        ChartSeries mDSPD = new ChartSeries();
        mDSPD.setLabel("A través de otra área");
        mDSPD.set("A través de otra área", p21_r5);

        ChartSeries mDAP = new ChartSeries();
        mDAP.setLabel("A través del área de trabajo social");
        mDAP.set("A través del área de trabajo social", p21_r6);

        ChartSeries mNF = new ChartSeries();
        mNF.setLabel("Comité para determinar");
        mNF.set("Comité para determinar", p21_r10);

        model.addSeries(mFDRH);
        model.addSeries(mFM);
        model.addSeries(mFRF);
        model.addSeries(mNSS);
        model.addSeries(mDSPD);
        model.addSeries(mNF);
        model.addSeries(mDAP);

        return model;
    }

    //-------------------
    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Autopsies");

        series1.set(2004, 2);
        series1.set(2005, 6);
        series1.set(2006, 2);
        series1.set(2007, 7);
        series1.set(2008, 4);
        series1.set(2009, 16);
        series1.set(2010, 4);
        series1.set(2011, 6);
        series1.set(2012, 0);
        series1.set(2013, 2);
        series1.set(2014, 1);
        series1.set(2015, 2);
        series1.set(2016, 0);
        series1.set(2017, 1);
        series1.set(2018, 0);
        series1.set(2019, 3);
        series1.set(2020, 0);

        model.addSeries(series1);

        return model;
    }
}
