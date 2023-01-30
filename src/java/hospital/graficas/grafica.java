/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.graficas;

import hospital.datos.AccesoDatos;
import hospital.modelo.area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class grafica {

    private AccesoDatos oAD;

    public String itemsBarras() throws Exception {
        try {
            area oArea = new area();
            String dataset = "";
            List<area> listaAreas;
            HashMap<Integer, String> listaCentros;
            int cant;
            listaAreas = oArea.buscaTodos();
            listaCentros = obtenerCentros();
            System.out.println("----------"+listaCentros.size()+"----------");
            for (Map.Entry<Integer, String> centro : listaCentros.entrySet()) {
                for (area x : listaAreas) {                    
                    cant = cant_por_area_y_esc(x.getIdArea(), centro.getKey());
                    if (dataset.equals("")) {
                        System.out.println("-----------------Valor-"+centro.getValue()+"--"+cant+"-------------"+x.getIdArea()+"-----");
                        dataset += "[{x: '" + centro.getValue()+ "', y: " + cant + ", group:" + x.getIdArea() + "}";
                    } else {
                        dataset += ",{x: '" + centro.getValue() + "', y: " + cant + ", group:" + x.getIdArea() + "}";
                    }

                }
            }
            if (!dataset.equals("")) {
                dataset += "]";
            }
            System.out.println("----------------------"+dataset+"----------------------");
            return dataset;
        } catch (Exception e) {
            throw e;
        }
    }

    private HashMap<Integer, String> obtenerCentros() throws Exception {
        try {
            HashMap<Integer, String> lResp = null;
            ArrayList rst = null;
            String sQuery = "";

            sQuery = "Select * from centrostodos();";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    rst = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                rst = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (rst != null) {
                lResp = new HashMap<>();

                for (int i = 0; i < rst.size(); i++) {
                    ArrayList vRowTemp = (ArrayList) rst.get(i);
                    lResp.put(((Double) vRowTemp.get(0)).intValue(), (String) vRowTemp.get(1));
                }
            }
            return lResp;

        } catch (Exception e) {
            throw e;
        }
    }

    private int cant_por_area_y_esc(Integer idArea, Integer idcentro) throws Exception {
        try {
            int cant = 0;
            ArrayList rst = null;
            String sQuery = "";

            sQuery = "Select * from cant_x_area_y_escuela(" + idArea + "," + idcentro + ");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    rst = oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                rst = oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
            if (rst != null) {
                ArrayList vRowTemp = (ArrayList) rst.get(0);
                cant = (((Double) vRowTemp.get(0)).intValue());

            }
            return cant;
        } catch (Exception e) {
            throw e;
        }
    }

    public String groupsBarras() throws Exception {
        try {
            String resultado = "";
            area oArea = new area();
            List<area> listaAreas;
            listaAreas = oArea.buscaTodos();
            for (area x : listaAreas) {
                if (resultado.equals("")) {
                    resultado += "[{id: " + x.getIdArea()+ ", content:\"" + x.getArea() + "\"}";
                } else {
                    resultado += ",{id: " + x.getIdArea()+ ", content:\"" + x.getArea() + "\"}";
                }

            }
             if (!resultado.equals("")) {
                resultado += "]";
            }
             System.out.println("----------------------"+resultado+"----------------------");
            return resultado;

        } catch (Exception e) {
            throw e;
        }
    }
}
