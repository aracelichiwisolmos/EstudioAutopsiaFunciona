package hospital.epm.model;

import hospital.asociacion.model.*;
import hospital.datos.AccesoDatos;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
//obtencion de reglas

public class reglasEPM {

    private elementoRegla[] precedente;
    private elementoRegla[] consecuente;
    private double confianza;
    private double soporte;
    private double lift;
    private AccesoDatos oAD;
    private String regla;
    private String regla2;
    ArrayList reglas = new ArrayList();

    public reglasEPM() {
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

    public String getRegla() {
        return regla;
    }

    public void setRegla(String regla) {
        this.regla = regla;
    }

    public String getRegla2() {
        return regla2;
    }

    public void setRegla2(String regla2) {
        this.regla = regla2;
    }

    public ArrayList getReglas() {
        return reglas;
    }

    public void setReglas(ArrayList reglas) {
        this.reglas = reglas;
    }

//    public void leerArchivo() {
//        // crea el flujo para leer desde el archivo
//        File file = new File("D:\\Documentos\\Tesis\\3er semestre\\Tesis\\Aylin1\\Código\\Respaldo\\EstudioAutopsiaFunciona\\corpus\\RULES.txt");
//        ArrayList listaEstudiantes = new ArrayList<>();
//        Scanner scanner;
//        try {
//            //se pasa el flujo al objeto scanner
//            scanner = new Scanner(file);
//            while (scanner.hasNextLine()) {
//                // el objeto scanner lee linea a linea desde el archivo
//                String linea = scanner.nextLine();
//                Scanner delimitar = new Scanner(linea);
//                //se usa una expresión regular
//                //que valida que antes o despues de una coma (,) exista cualquier cosa
//                //parte la cadena recibida cada vez que encuentre una coma
//                delimitar.useDelimiter("\\s* \\s*");
//                reglasEPM e = new reglasEPM();
//// Estudiante e = new Estudiante();
//                //  e.setRegla(linea);
//                e.setRegla(delimitar.next());
//                //  e.setCedula(delimitar.next());
//                // e.setNombres(delimitar.next());
//                //e.setApellidos(delimitar.next());
//                //e.setTelefono(delimitar.next());
//                //e.setDireccion(delimitar.next());
//                reglas.add(e);
//                //  System.out.println("Cedula " + e.getCedula() + " Nombre" + e.getNombres() + "Apellido" + e.getApellidos());
//            }
//            //se cierra el ojeto scanner
//            scanner.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        //return reglas;
//    }
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
