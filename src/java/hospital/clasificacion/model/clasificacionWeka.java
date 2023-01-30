package hospital.clasificacion.model;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class clasificacionWeka {

    private static Instances train;
    Instances data;
    FilteredClassifier clasificador;

    public clasificacionWeka(String corpus, String modelo) throws Exception {
        this.clasificador = (FilteredClassifier) weka.core.SerializationHelper.read(modelo);
        clasificacionWeka.train = ConverterUtils.DataSource.read(corpus);
        clasificacionWeka.train.setClassIndex(0);
        this.data = new Instances(train);
       }

    public String clasificar(String texto,String options) throws Exception {
        double predicted;
        Instance instance;
        instanciaWeka instancia = new instanciaWeka();

        if (train.numInstances() == 0) {
            throw new Exception("No hay datos de entrenamiento.");
        }
        instance = instancia.crearInstancia(texto, data);
        clasificador.setOptions(weka.core.Utils.splitOptions(options));
        clasificador.buildClassifier(data);
        predicted = clasificador.classifyInstance(instance);
        return train.classAttribute().value((int) predicted);
    }
}
