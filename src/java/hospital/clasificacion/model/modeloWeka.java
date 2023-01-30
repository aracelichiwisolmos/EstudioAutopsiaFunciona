
package hospital.clasificacion.model;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class modeloWeka {

    private final Instances train;
    private final FilteredClassifier algoritmo;
        
    
    public modeloWeka(String corpus) throws Exception {
        algoritmo =new FilteredClassifier();
        this.train = ConverterUtils.DataSource.read(corpus);
        train.setClassIndex(0);
    }

    public void generarModelo(String rutaDestino) throws Exception{
        if (train.numInstances()==0)
            throw new Exception("No hay datos de entrenamiento.");
        weka.core.SerializationHelper.write(rutaDestino,algoritmo);
    }
}
