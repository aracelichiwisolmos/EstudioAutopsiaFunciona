package hospital.asociacion.model;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;

public class reglas {

    private elementoRegla[] precedente;
    private elementoRegla[] consecuente;
    private double confianza;
    private double soporte;
    private double lift;
    private AccesoDatos oAD;

    public reglas() {
        this.precedente = null;
        this.consecuente = null;
        this.confianza = 0;
        this.soporte = 0;
        this.lift = 0;
    }

    public elementoRegla[] getPrecedente() {
        return precedente;
    }

    public void setPrecedente(elementoRegla[] precedente) {
        this.precedente = precedente;
    }

    public elementoRegla[] getConsecuente() {
        return consecuente;
    }

    public void setConsecuente(elementoRegla[] consecuente) {
        this.consecuente = consecuente;
    }

    public double getConfianza() {
        return Math.round(confianza * 100);
    }

    public void setConfianza(double confianza) {
        this.confianza = confianza;
    }

    public double getSoporte() {
        return Math.round(soporte * 100);
    }

    public void setSoporte(double soporte) {
        this.soporte = soporte;
    }

    public double getLift() {
        return lift;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public String describeReglas() {
        String resultado = "";

        for (int i = 0; i < this.precedente.length; i++) {

            if (i < this.precedente.length - 1) {
                resultado += this.precedente[i].getNombreColumna() + "=" + this.precedente[i].getValor() + ", ";
            } else {
                resultado += this.precedente[i].getNombreColumna() + "=" + this.precedente[i].getValor() + " ==> ";
            }
        }
        for (int i = 0; i < this.consecuente.length; i++) {
            if (i < this.consecuente.length - 1) {
                resultado += this.consecuente[i].getNombreColumna() + "=" + this.consecuente[i].getValor() + ", ";
            } else {
                resultado += this.consecuente[i].getNombreColumna() + "=" + this.consecuente[i].getValor();
            }
        }
        return resultado;
    }

    public String interpretaRegla() throws Exception {

        String resultado = "El " + (int) this.getConfianza() + "% de los encuestados que ";

        for (int i = 0; i < this.getPrecedente().length; i++) {

            if (i < this.getPrecedente().length - 1) {
                if (i == this.getPrecedente().length - 2) {
                    resultado += interpretaAtributo(this.getPrecedente()[i].getNombreColumna(), this.getPrecedente()[i].getValor()) + " y ";
                } else {
                    resultado += interpretaAtributo(this.getPrecedente()[i].getNombreColumna(), this.getPrecedente()[i].getValor()) + ", ";
                }
            } else {
                resultado += interpretaAtributo(this.getPrecedente()[i].getNombreColumna(), this.getPrecedente()[i].getValor()) + ", también ";
            }
        }

        for (int i = 0; i < this.getConsecuente().length; i++) {
            if (i < this.getConsecuente().length - 1) {
                if (i == this.getConsecuente().length - 2) {
                    resultado += interpretaAtributo(this.getConsecuente()[i].getNombreColumna(), this.getConsecuente()[i].getValor()) + " y ";
                } else {
                    resultado += interpretaAtributo(this.getConsecuente()[i].getNombreColumna(), this.getConsecuente()[i].getValor()) + ", ";
                }
            } else {
                resultado += interpretaAtributo(this.getConsecuente()[i].getNombreColumna(), this.getConsecuente()[i].getValor()) + ". ";
            }
        }

        resultado += "Esta regla aparece con una frecuencia del " + (int) this.getSoporte() + "%.";
        return resultado;
    }

    public String interpretaAtributo(String preguntaCorta, String valorRespuesta) throws Exception {
        String interpretacionAtributo = null;
        ArrayList resultado = null;
        try {
            String sQuery = "SELECT interpreta_atributo('" + preguntaCorta + "','" + valorRespuesta + "');";
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
                ArrayList vRowTemp = (ArrayList) resultado.get(0);

                interpretacionAtributo = ((String) vRowTemp.get(0));

            }
        } catch (Exception e) {
            throw e;
        }
        return interpretacionAtributo;
    }

}
