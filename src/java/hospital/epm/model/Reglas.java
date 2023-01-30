/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.epm.model;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author mscay
 */
public class Reglas {

    private String regla;
//    private String regla1;
//    private String regla2;
//    private String regla3;
//    private String regla4;
    //-----------------------
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
    private int idRegla;

    List<Reglas> reglas = new ArrayList<>();
    List<Reglas> confianza = new ArrayList<>();

    //constructor vacio
//    public Reglas() {
//
//    }
//    Reglas(ArrayList<Reglas> reglasList, ArrayList<Reglas> confList) {
//        this.reglas = reglasList;
//        this.confianza = confList;
//    }
    public Reglas() {

        this.regla = null;
        this.CONF = null;

    }

    //getters y setters
    public String getRegla() {
        return regla;
    }

    public void setRegla(String regla) {
        this.regla = regla;
    }

//    public String getRegla1() {
//        return regla1;
//    }
//
//    public void setRegla1(String regla1) {
//        this.regla1 = regla1;
//    }
//
//    public String getRegla2() {
//        return regla2;
//    }
//
//    public void setRegla2(String regla2) {
//        this.regla2 = regla2;
//    }
//
//    public String getRegla3() {
//        return regla3;
//    }
//
//    public void setRegla3(String regla3) {
//        this.regla3 = regla3;
//    }
//
//    public String getRegla4() {
//        return regla4;
//    }
//
//    public void setRegla4(String regla4) {
//        this.regla4 = regla4;
//    }
    //---------------------------------------------------
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

        return CONF;

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

    public int getIdRegla() {
        return idRegla;
    }

    public void setIdRegla(int idRegla) {
        this.idRegla = idRegla;
    }

    public List<Reglas> getReglas() {
        return reglas;
    }

    public void setReglas(ArrayList<Reglas> reglas) {
        this.reglas = reglas;
    }

    public List<Reglas> getConfianza() {
        return confianza;
    }

    public void setConfianza(ArrayList<Reglas> confianza) {
        this.confianza = confianza;
    }

}
