package hospital.modelo;

import hospital.clasificacion.model.clasificacionWeka;
import hospital.controlador.configuracionAppControler;
import java.io.Serializable;

public class clasificadora {

    private clasificacionWeka oClacificacionWeka;
    private final configuracionAppControler oConf;

    public clasificadora() {

        oConf = new configuracionAppControler();
        oClacificacionWeka = null;
    }

    public String clasifica(String texto, int preg) throws Exception {
        String resultado;
        try {
            String options = null;
            String pathModel = null;
            String pathCorpus = null;

            if (preg == 6) {
                options = oConf.getOptions_mcc_aut();
                pathModel = oConf.getPahtModel_mcc_aut();
                pathCorpus = oConf.getPahtCorpus_mcc_aut();
            }
            if (preg == 7) {
                options = oConf.getOptions_mcc_no_aut();
                pathModel = oConf.getPahtModel_mcc_no_aut();
                pathCorpus = oConf.getPahtCorpus_mcc_no_aut();
            }
            if (preg == 13) {
                options = oConf.getOptions_com_op_sug();
                pathModel = oConf.getPahtModel_com_op_sug();
                pathCorpus = oConf.getPahtCorpus_com_op_sug();
            }
            oClacificacionWeka = new clasificacionWeka(pathCorpus, pathModel);
            resultado = oClacificacionWeka.clasificar(texto, options);
            return resultado;
        } catch (Exception e) {
            throw e;
        }
    }

}
