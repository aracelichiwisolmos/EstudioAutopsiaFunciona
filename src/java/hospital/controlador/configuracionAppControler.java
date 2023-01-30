/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.controlador;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class configuracionAppControler{

    static final ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    static final String realPath = servletContext.getInitParameter("misFicheros");

    static final String pahtModel_mcc_aut = realPath + "mcc_aut.model";
    static final String pahtModel_mcc_no_aut = realPath + "mcc_no_aut.model";
    static final String pahtModel_com_op_sug = realPath + "com_sug_op.model";

    static final String pahtCorpus_mcc_aut = realPath + "mcc_aut.arff";
    static final String pahtCorpus_mcc_no_aut = realPath + "mcc_no_aut.arff";
    static final String pahtCorpus_com_op_sug = realPath + "com_sug_op.arff";

    static final String options_mcc_aut = "weka.classifiers.meta.FilteredClassifier -F \"weka.filters.MultiFilter -F \\\"weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 1000 -prune-rate -1.0 -N 0 -L -stemmer weka.core.stemmers.NullStemmer -M 1 -O -tokenizer \\\\\\\"weka.core.tokenizers.WordTokenizer -delimiters \\\\\\\\\\\\\\\" \\\\\\\\\\\\\\\\r\\\\\\\\\\\\\\\\n\\\\\\\\\\\\\\\\t.,;:\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\'\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\"()?!\\\\\\\\\\\\\\\"\\\\\\\"\\\" -F \\\"weka.filters.supervised.attribute.AttributeSelection -E \\\\\\\"weka.attributeSelection.InfoGainAttributeEval \\\\\\\" -S \\\\\\\"weka.attributeSelection.Ranker -T 0.0 -N -1\\\\\\\"\\\"\" -W weka.classifiers.functions.MultilayerPerceptron -- -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
    static final String options_mcc_no_aut = "weka.classifiers.meta.FilteredClassifier -F \"weka.filters.MultiFilter -F \\\"weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 1000 -prune-rate -1.0 -N 0 -L -stemmer weka.core.stemmers.NullStemmer -M 1 -O -tokenizer \\\\\\\"weka.core.tokenizers.WordTokenizer -delimiters \\\\\\\\\\\\\\\" \\\\\\\\\\\\\\\\r\\\\\\\\\\\\\\\\n\\\\\\\\\\\\\\\\t.,;:\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\'\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\"()?!\\\\\\\\\\\\\\\"\\\\\\\"\\\" -F \\\"weka.filters.supervised.attribute.AttributeSelection -E \\\\\\\"weka.attributeSelection.InfoGainAttributeEval \\\\\\\" -S \\\\\\\"weka.attributeSelection.Ranker -T 0.0 -N -1\\\\\\\"\\\"\" -W weka.classifiers.functions.MultilayerPerceptron -- -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
    static final String options_com_op_sug = "weka.classifiers.meta.FilteredClassifier -F \"weka.filters.MultiFilter -F \\\"weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 1000 -prune-rate -1.0 -N 0 -L -stemmer weka.core.stemmers.NullStemmer -M 1 -O -tokenizer \\\\\\\"weka.core.tokenizers.WordTokenizer -delimiters \\\\\\\\\\\\\\\" \\\\\\\\\\\\\\\\r\\\\\\\\\\\\\\\\n\\\\\\\\\\\\\\\\t.,;:\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\'\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\"()?!\\\\\\\\\\\\\\\"\\\\\\\"\\\" -F \\\"weka.filters.supervised.attribute.AttributeSelection -E \\\\\\\"weka.attributeSelection.InfoGainAttributeEval \\\\\\\" -S \\\\\\\"weka.attributeSelection.Ranker -T 0.0 -N -1\\\\\\\"\\\"\" -W weka.classifiers.functions.MultilayerPerceptron -- -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
    static final String options_bayesNet = "-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";

    static final String pahtModel_c_Apriori = realPath + "c_Apriori.model";
    static final String pahtModel_c_FPGrowth = realPath + "c_FPGrowth.model";
    static final String pahtModel_c_PredictiveApriori = realPath + "c_PredictiveApriori.model";
    static final String pahtModel_c_Tertius = realPath + "c_Tertius.model";
    
// para el conjunto c 2 algoritmos

//iEPMiner
    static final String pahtModel_c_iEPMiner1 = realPath + "EPM\\C\\iEPMiner\\Frm_sol_aut\\RULES.txt";
    static final String pahtModel_c_iEPMiner2 = realPath + "EPM\\C\\iEPMiner\\Med_aut\\RULES.txt";
    static final String pahtModel_c_iEPMiner3 = realPath + "EPM\\C\\iEPMiner\\Per_sol_aut\\RULES.txt";
    static final String pahtModel_c_iEPMiner4 = realPath + "EPM\\C\\iEPMiner\\No_hosp\\RULES.txt";
    static final String pahtModel_c_iEPMiner5 = realPath + "EPM\\C\\iEPMiner\\Rechazo_fam\\RULES.txt";
    static final String pahtModel_c_iEPMiner6 = realPath + "EPM\\C\\iEPMiner\\Mcc_no_aut\\RULES.txt";
    static final String pahtModel_c_iEPMiner7 = realPath + "EPM\\C\\iEPMiner\\Mcc_aut\\RULES.txt";
    //DGCP-Tree
    static final String pahtModel_c_DGCP1 = realPath + "EPM\\C\\DGCP\\Frm_sol_aut\\RULES.txt";
    static final String pahtModel_c_DGCP2 = realPath + "EPM\\C\\DGCP\\Med_aut\\RULES.txt";
    static final String pahtModel_c_DGCP3 = realPath + "EPM\\C\\DGCP\\Per_sol_aut\\RULES.txt";
    static final String pahtModel_c_DGCP4 = realPath + "EPM\\C\\DGCP\\No_hosp\\RULES.txt";
    static final String pahtModel_c_DGCP5 = realPath + "EPM\\C\\DGCP\\Rechazo_fam\\RULES.txt";
    static final String pahtModel_c_DGCP6 = realPath + "EPM\\C\\DGCP\\Mcc_no_aut\\RULES.txt";
    static final String pahtModel_c_DGCP7 = realPath + "EPM\\C\\DGCP\\Mcc_aut\\RULES.txt";
    //SDMap
      static final String pahtModel_c_SDMap = realPath + "";//reglas?

    // para el conjunto d 3 algoritmos
    //iEPMiner
    static final String pahtModel_d_iEPMiner1 = realPath + "EPM\\D\\iEPMiner\\Frm_sol_aut\\RULES.txt";
    static final String pahtModel_d_iEPMiner2 = realPath + "EPM\\D\\iEPMiner\\Med_aut\\RULES.txt";
    static final String pahtModel_d_iEPMiner3 = realPath + "EPM\\D\\iEPMiner\\Per_sol_aut\\RULES.txt";
    static final String pahtModel_d_iEPMiner4 = realPath + "EPM\\D\\iEPMiner\\No_hosp\\RULES.txt";
    static final String pahtModel_d_iEPMiner5 = realPath + "EPM\\D\\iEPMiner\\Rechazo_fam\\RULES.txt";
    static final String pahtModel_d_iEPMiner6 = realPath + "EPM\\D\\iEPMiner\\Mcc_no_aut\\RULES.txt";
    static final String pahtModel_d_iEPMiner7 = realPath + "EPM\\D\\iEPMiner\\Mcc_aut\\RULES.txt";
    //DGCP-Tree
    static final String pahtModel_d_DGCP1 = realPath + "EPM\\D\\DGCP\\Frm_sol_aut\\RULES.txt";
    static final String pahtModel_d_DGCP2 = realPath + "EPM\\D\\DGCP\\Med_aut\\RULES.txt";
    static final String pahtModel_d_DGCP3 = realPath + "EPM\\D\\DGCP\\Per_sol_aut\\RULES.txt";
    static final String pahtModel_d_DGCP4 = realPath + "EPM\\D\\DGCP\\No_hosp\\RULES.txt";
    static final String pahtModel_d_DGCP5 = realPath + "EPM\\D\\DGCP\\Rechazo_fam\\RULES.txt";
    static final String pahtModel_d_DGCP6 = realPath + "EPM\\D\\DGCP\\Mcc_no_aut\\RULES.txt";
    static final String pahtModel_d_DGCP7 = realPath + "EPM\\D\\DGCP\\Mcc_aut\\RULES.txt";
    

    static final String pahtModel_d_Apriori = realPath + "d_Apriori.model";
    static final String pahtModel_d_Tertius = realPath + "d_Tertius.model";

    static final String pahtModel_d_BayesNet = realPath + "d_BayesNet.model";

    public String getPahtModel_mcc_aut() {
        return pahtModel_mcc_aut;
    }

    public String getPahtModel_mcc_no_aut() {
        return pahtModel_mcc_no_aut;
    }

    public String getPahtModel_com_op_sug() {
        return pahtModel_com_op_sug;
    }

    public String getPahtCorpus_mcc_aut() {
        return pahtCorpus_mcc_aut;
    }

    public String getPahtCorpus_mcc_no_aut() {
        return pahtCorpus_mcc_no_aut;
    }

    public String getPahtCorpus_com_op_sug() {
        return pahtCorpus_com_op_sug;
    }

    public String getOptions_mcc_aut() {
        return options_mcc_aut;
    }

    public String getOptions_mcc_no_aut() {
        return options_mcc_no_aut;
    }

    public String getOptions_com_op_sug() {
        return options_com_op_sug;
    }

    public String getPahtModel_c_Apriori() {
        return pahtModel_c_Apriori;
    }

    public String getPahtModel_c_FPGrowth() {
        return pahtModel_c_FPGrowth;
    }

    public String getPahtModel_c_PredictiveApriori() {
        return pahtModel_c_PredictiveApriori;
    }

    public String getPahtModel_c_Tertius() {
        return pahtModel_c_Tertius;
    }

    public String getPahtModel_d_Apriori() {
        return pahtModel_d_Apriori;
    }

    public String getPahtModel_d_Tertius() {
        return pahtModel_d_Tertius;
    }

    public static String getOptions_bayesNet() {
        return options_bayesNet;
    }

    public static String getPahtModel_d_BayesNet() {
        return pahtModel_d_BayesNet;
    }

    //----------------------------------------
    public static String getPahtModel_c_iEPMiner1() {
        return pahtModel_c_iEPMiner1;
    }

    public static String getPahtModel_c_iEPMiner2() {
        return pahtModel_c_iEPMiner2;
    }

    public static String getPahtModel_c_iEPMiner3() {
        return pahtModel_c_iEPMiner3;
    }

    public static String getPahtModel_c_iEPMiner4() {
        return pahtModel_c_iEPMiner4;
    }

    public static String getPahtModel_c_iEPMiner5() {
        return pahtModel_c_iEPMiner5;
    }

    public static String getPahtModel_c_iEPMiner6() {
        return pahtModel_c_iEPMiner6;
    }

    public static String getPahtModel_c_iEPMiner7() {
        return pahtModel_c_iEPMiner7;
    }

    public static String getPahtModel_c_DGCP1() {
        return pahtModel_c_DGCP1;
    }

    public static String getPahtModel_c_DGCP2() {
        return pahtModel_c_DGCP2;
    }

    public static String getPahtModel_c_DGCP3() {
        return pahtModel_c_DGCP3;
    }

    public static String getPahtModel_c_DGCP4() {
        return pahtModel_c_DGCP4;
    }

    public static String getPahtModel_c_DGCP5() {
        return pahtModel_c_DGCP5;
    }

    public static String getPahtModel_c_DGCP6() {
        return pahtModel_c_DGCP6;
    }

    public static String getPahtModel_c_DGCP7() {
        return pahtModel_c_DGCP7;
    }

    public static String getPahtModel_d_iEPMiner1() {
        return pahtModel_d_iEPMiner1;
    }

    public static String getPahtModel_d_iEPMiner2() {
        return pahtModel_d_iEPMiner2;
    }

    public static String getPahtModel_d_iEPMiner3() {
        return pahtModel_d_iEPMiner3;
    }

    public static String getPahtModel_d_iEPMiner4() {
        return pahtModel_d_iEPMiner4;
    }

    public static String getPahtModel_d_iEPMiner5() {
        return pahtModel_d_iEPMiner5;
    }

    public static String getPahtModel_d_iEPMiner6() {
        return pahtModel_d_iEPMiner6;
    }

    public static String getPahtModel_d_iEPMiner7() {
        return pahtModel_d_iEPMiner7;
    }

    public static String getPahtModel_d_DGCP1() {
        return pahtModel_d_DGCP1;
    }

    public static String getPahtModel_d_DGCP2() {
        return pahtModel_d_DGCP2;
    }

    public static String getPahtModel_d_DGCP3() {
        return pahtModel_d_DGCP3;
    }

    public static String getPahtModel_d_DGCP4() {
        return pahtModel_d_DGCP4;
    }

    public static String getPahtModel_d_DGCP5() {
        return pahtModel_d_DGCP5;
    }

    public static String getPahtModel_d_DGCP6() {
        return pahtModel_d_DGCP6;
    }

    public static String getPahtModel_d_DGCP7() {
        return pahtModel_d_DGCP7;
    }

}
