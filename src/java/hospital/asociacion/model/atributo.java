
package hospital.asociacion.model;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;


public class atributo {

    private String atributo;
    private String describAntecedente;
    private String describeConsecuente;
    private AccesoDatos oAD;
    private String sQuery;

    public atributo() {
        this.atributo = null;
        this.describAntecedente = null;
        this.describeConsecuente = null;
    }

   public void descripciones(String atributo) throws Exception {        
        ArrayList vRowTemp;
        ArrayList resultado = null;

        try {
            sQuery = "SELECT antecedente, consecuente\n"
                    + "  FROM interpretacion\n"
                    + "  WHERE atributo='" + atributo + "';";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    resultado = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                resultado = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (resultado != null) {
                this.atributo=atributo;
                vRowTemp = (ArrayList) resultado.get(0);
                this.describAntecedente=(String) vRowTemp.get(0);
                this.describeConsecuente=(String) vRowTemp.get(1);
            }
        } catch (Exception e) {
           throw e;
        }

    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getDescribAntecedente() {
        return describAntecedente;
    }

    public void setDescribAntecedente(String describAntecedente) {
        this.describAntecedente = describAntecedente;
    }

    public String getDescribeConsecuente() {
        return describeConsecuente;
    }

    public void setDescribeConsecuente(String describeConsecuente) {
        this.describeConsecuente = describeConsecuente;
    }

}
