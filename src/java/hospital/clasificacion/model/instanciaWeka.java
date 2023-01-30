
package hospital.clasificacion.model;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class instanciaWeka {

    public Instance crearInstancia(String texto, Instances train) {
        Instance instance = new Instance(2);
        Attribute atributo = train.attribute("text");
        instance.setValue(atributo, texto);
        instance.setDataset(train);
        return instance;
    }
    
}
