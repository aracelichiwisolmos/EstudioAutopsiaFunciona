
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
public class ChartView implements Serializable {

    private LineChartModel animatedModel1;
    private BarChartModel animatedModel2;
    private BarChartModel animatedModelCasos;

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

    public ChartView() throws Exception {
        oAsociacionUtilidades = new asociacionUtilidades();
        this.estudiantes = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_8");
        this.medicosG = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_2");
        this.especialistas = oAsociacionUtilidades.cantInstancias_dadoColumna("p14_1");
        this.casosMas20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_5");
        this.casosEntre11y20 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_4");
        this.casosEnte6y10 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_3");
        this.casosMenos5 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_2");
        this.casos0 = oAsociacionUtilidades.cantInstancias_dadoColumna("p2_1");
        this.instanciasC = oAsociacionUtilidades.cantInstanciasC();

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
        yAxis.setMax(maxCantCaso);

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
