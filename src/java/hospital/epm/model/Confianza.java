/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.epm.model;

/**
 *
 * @author mscay
 */
public class Confianza {

    private String RULE_NUMBER;
    private String N_VARS;
    private String TP;
    private String TN;
    private String FP;
    private String FN;
    private String ACC;
    private String SUPP;
    private String FPR;
    private String GR;
    private String SUPDIFF;
    private String NRULES;
    private String AUC;
    private String WRACC;
    private String TPR;
    private String CONF;
    private String FISHER;
    private String GAIN;
    private int idConfianza;

    //constructor vacio
    public Confianza() {

    }

    public String getRULE_NUMBER() {
        return RULE_NUMBER;
    }

    public void setRULE_NUMBER(String RULE_NUMBER) {
        this.RULE_NUMBER = RULE_NUMBER;
    }

    public String getN_VARS() {
        return N_VARS;
    }

    public void setN_VARS(String N_VARS) {
        this.N_VARS = N_VARS;
    }

    public String getTP() {
        return TP;
    }

    public void setTP(String TP) {
        this.TP = TP;
    }

    public String getTN() {
        return TN;
    }

    public void setTN(String TN) {
        this.TN = TN;
    }

    public String getFP() {
        return FP;
    }

    public void setFP(String FP) {
        this.FP = FP;
    }

    public String getFN() {
        return FN;
    }

    public void setFN(String FN) {
        this.FN = FN;
    }

    public String getACC() {
        return ACC;
    }

    public void setACC(String ACC) {
        this.ACC = ACC;
    }

    public String getSUPP() {
        return SUPP;
    }

    public void setSUPP(String SUPP) {
        this.SUPP = SUPP;
    }

    public String getFPR() {
        return FPR;
    }

    public void setFPR(String FPR) {
        this.FPR = FPR;
    }

    public String getGR() {
        return GR;
    }

    public void setGR(String GR) {
        this.GR = GR;
    }

    public String getSUPDIFF() {
        return SUPDIFF;
    }

    public void setSUPDIFF(String SUPDIFF) {
        this.SUPDIFF = SUPDIFF;
    }

    public String getNRULES() {
        return NRULES;
    }

    public void setNRULES(String NRULES) {
        this.NRULES = NRULES;
    }

    public String getAUC() {
        return AUC;
    }

    public void setAUC(String AUC) {
        this.AUC = AUC;
    }

    public String getWRACC() {
        return WRACC;
    }

    public void setWRACC(String WRACC) {
        this.WRACC = WRACC;
    }

    public String getTPR() {
        return TPR;
    }

    public void setTPR(String TPR) {
        this.TPR = TPR;
    }

    public String getCONF() {
        double conf = Double.parseDouble(CONF);
        double p = Math.round(conf * 100);
        String confianza = String.valueOf(p);

        return confianza;
        //  return CONF;
    }

    public void setCONF(String CONF) {
        this.CONF = CONF;
    }

    public String getFISHER() {
        return FISHER;
    }

    public void setFISHER(String FISHER) {
        this.FISHER = FISHER;
    }

    public String getGAIN() {
        return GAIN;
    }

    public void setGAIN(String GAIN) {
        this.GAIN = GAIN;
    }

    public int getIdConfianza() {
        return idConfianza;
    }

    public void setIdConfianza(int idConfianza) {
        this.idConfianza = idConfianza;
    }

}
