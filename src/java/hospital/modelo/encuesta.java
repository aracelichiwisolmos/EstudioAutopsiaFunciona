package hospital.modelo;

import hospital.datos.AccesoDatos;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class encuesta {

    private AccesoDatos oAD;
    private String sQuery;
    private ArrayList resultado = null;
    private ArrayList vRowTemp;

    public void insertarEncuesta(int id_grado, int id_area,
            int id_centro_espc, int id_centro_lic, int id_resp1, int id_resp2, int id_resp3,
            int id_resp4, int id_resp5, int[] array_rep8, int[] array_rep9, int[] array_rep10,
            int[] array_rep11, int[] array_rep12, typeClasificado[] array_repa6,
            typeClasificado[] array_repa7, typeClasificado[] array_repa8,
            typeClasificado[] array_repa9, typeClasificado[] array_repa10, typeClasificado[] array_repa11,
            typeClasificado[] array_repa12, typeClasificado[] array_repa13, IdentificacionUsuario iu) throws Exception {
        try {
            int id_categoria = 1;
            int id_especialidad = 2;

            sQuery = "Select insertar_encuesta("
                    + id_grado + ", "
                    + id_area + ", "
                    + id_categoria + ", "
                    + id_especialidad + ", "
                    + id_centro_espc + ", "
                    + id_centro_lic + ", "
                    + id_resp1 + ", "
                    + id_resp2 + ", "
                    + id_resp3 + ", "
                    + id_resp4 + ", "
                    + id_resp5 + ", "
                    + devuelveStringArregloDeEntero(array_rep8) + ", "
                    + devuelveStringArregloDeEntero(array_rep9) + ", "
                    + devuelveStringArregloDeEntero(array_rep10) + ", "
                    + devuelveStringArregloDeEntero(array_rep11) + ", "
                    + devuelveStringArregloDeEntero(array_rep12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa6) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa7) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa8) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa9) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa10) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa11) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa13) + ");";

            ArrayList res = null;
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    res = oAD.ejecutarConsulta(sQuery);
                    if (!res.isEmpty()) {
                        String sQueryNuestro = "SELECT * FROM insertaridusuariobuena("
                                + "'" + iu.getClave3() + "'::character varying, "
                                + (((ArrayList) res.get(0)).get(0)) + "::integer,"
                                + +iu.getClCh() + "::integer, "
                                + +iu.getClIns() + "::integer,"
                                + +iu.getClMun() + "::integer,"
                                + +iu.getClLoc() + "::integer);";
                        oAD.ejecutarConsulta(sQueryNuestro);
                        //  String q2 = "insert into medico_centro values(" + (((ArrayList) res.get(0)).get(0)) + ",'" + iu.getClave3() + "')";
                        // oAD.ejecutarComando(q2);
                    }

                    oAD.desconectar();
                }
                oAD = null;
            } else {
                res = oAD.ejecutarConsulta(sQuery);
                if (!res.isEmpty()) {
                    String sQueryNuestro = "SELECT * FROM insertaridusuariobuena("
                            + "'" + iu.getClave3() + "'::character varying, "
                            + (((ArrayList) res.get(0)).get(0)) + "::integer,"
                            + +iu.getClCh() + "::integer, "
                            + +iu.getClIns() + "::integer,"
                            + +iu.getClMun() + "::integer,"
                            + +iu.getClLoc() + "::integer);";
                    oAD.ejecutarConsulta(sQueryNuestro);
                    String q2 = "insert into medico_centro values(" + (((ArrayList) res.get(0)).get(0)) + ",'" + iu.getClave3() + "')";
                    oAD.ejecutarComando(q2);
                }
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }

    }

    public void insertarEncuesta(int id_grado, int id_area,
            int id_centro_espc, int id_centro_lic, int id_resp1, int id_resp2, int id_resp3,
            int id_resp4, int id_resp5, int[] array_rep8, int[] array_rep9, int[] array_rep10,
            int[] array_rep11, int[] array_rep12, typeClasificado[] array_repa6,
            typeClasificado[] array_repa7, typeClasificado[] array_repa8,
            typeClasificado[] array_repa9, typeClasificado[] array_repa10, typeClasificado[] array_repa11,
            typeClasificado[] array_repa12, typeClasificado[] array_repa13) throws Exception {
        try {
            int id_categoria = 1;
            int id_especialidad = 2;

            sQuery = "Select insertar_encuesta("
                    + id_grado + ", "
                    + id_area + ", "
                    + id_categoria + ", "
                    + id_especialidad + ", "
                    + id_centro_espc + ", "
                    + id_centro_lic + ", "
                    + id_resp1 + ", "
                    + id_resp2 + ", "
                    + id_resp3 + ", "
                    + id_resp4 + ", "
                    + id_resp5 + ", "
                    + devuelveStringArregloDeEntero(array_rep8) + ", "
                    + devuelveStringArregloDeEntero(array_rep9) + ", "
                    + devuelveStringArregloDeEntero(array_rep10) + ", "
                    + devuelveStringArregloDeEntero(array_rep11) + ", "
                    + devuelveStringArregloDeEntero(array_rep12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa6) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa7) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa8) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa9) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa10) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa11) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa13) + ");";

            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }

    }

    //-----------------------
    public void actualizarEncuesta(int idMed, int id_grado, int id_area,
            int id_centro_espc, int id_centro_lic, int id_resp1, int id_resp2, int id_resp3,
            int id_resp4, int id_resp5, int[] array_rep8, int[] array_rep9, int[] array_rep10,
            int[] array_rep11, int[] array_rep12, typeClasificado[] array_repa6,
            typeClasificado[] array_repa7, typeClasificado[] array_repa8,
            typeClasificado[] array_repa9, typeClasificado[] array_repa10, typeClasificado[] array_repa11,
            typeClasificado[] array_repa12, typeClasificado[] array_repa13, IdentificacionUsuario iu) throws Exception {
        try {
            int id_categoria = 1;
            int id_especialidad = 2;

            sQuery = "Select actualizar_encuesta("
                    + idMed + ", "
                    + id_grado + ", "
                    + id_area + ", "
                    + id_categoria + ", "
                    + id_especialidad + ", "
                    + id_centro_espc + ", "
                    + id_centro_lic + ", "
                    + id_resp1 + ", "
                    + id_resp2 + ", "
                    + id_resp3 + ", "
                    + id_resp4 + ", "
                    + id_resp5 + ", "
                    + devuelveStringArregloDeEntero(array_rep8) + ", "
                    + devuelveStringArregloDeEntero(array_rep9) + ", "
                    + devuelveStringArregloDeEntero(array_rep10) + ", "
                    + devuelveStringArregloDeEntero(array_rep11) + ", "
                    + devuelveStringArregloDeEntero(array_rep12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa6) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa7) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa8) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa9) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa10) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa11) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa12) + ", "
                    + devuelveStringArregloDeTypeClasificado(array_repa13) + ");";

            ArrayList res = null;
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    res = oAD.ejecutarConsulta(sQuery);
                    if (!res.isEmpty()) {
                        String sQueryNuestro = "SELECT * FROM insertaridusuariobuena("
                                + "'" + iu.getClave3() + "'::character varying, "
                                + (((ArrayList) res.get(0)).get(0)) + "::integer,"
                                + +iu.getClCh() + "::integer, "
                                + +iu.getClIns() + "::integer,"
                                + +iu.getClMun() + "::integer,"
                                + +iu.getClLoc() + "::integer);";
                        oAD.ejecutarConsulta(sQueryNuestro);
                        String q2 = "insert into medico_centro values(" + (((ArrayList) res.get(0)).get(0)) + ",'" + iu.getClave3() + "')";
                        oAD.ejecutarComando(q2);
                    }

                    oAD.desconectar();
                }
                oAD = null;
            } else {
                res = oAD.ejecutarConsulta(sQuery);
                if (!res.isEmpty()) {
                    String sQueryNuestro = "SELECT * FROM insertaridusuariobuena("
                            + "'" + iu.getClave3() + "'::character varying, "
                            + (((ArrayList) res.get(0)).get(0)) + "::integer,"
                            + +iu.getClCh() + "::integer, "
                            + +iu.getClIns() + "::integer,"
                            + +iu.getClMun() + "::integer,"
                            + +iu.getClLoc() + "::integer);";
                    oAD.ejecutarConsulta(sQueryNuestro);
                    String q2 = "insert into medico_centro values(" + (((ArrayList) res.get(0)).get(0)) + ",'" + iu.getClave3() + "')";
                    oAD.ejecutarComando(q2);
                }
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }

    }
    //---------------------

    public String devuelveStringArregloDeEntero(int[] arreglo) {
        String result = "Array[";
        for (int i = 0; i < arreglo.length; i++) {
            result += arreglo[i];
            if (i != arreglo.length - 1) {
                result += ",";
            }
        }
        result += "]";

        return result;
    }

    public String devuelveStringArregloDeTypeClasificado(typeClasificado[] arreglo) {

        String result = "(Array[";
        typeClasificado oTypeClasificado;

        for (int i = 0; i < arreglo.length; i++) {
            oTypeClasificado = arreglo[i];
            result += "'(" + oTypeClasificado.getIdclasificado() + "," + oTypeClasificado.getId_medico() + ","
                    + oTypeClasificado.getTexto() + "," + oTypeClasificado.getClasificado() + "," + oTypeClasificado.getId_preg() + ")'";
            if (i != arreglo.length - 1) {
                result += ",";
            } else {
                result += "])::clasificado[]";
            }
        }
        return result;
    }

    public void eliminarEncuesta(int control) throws Exception {
        try {
            //sQuery = "Select eliminar_encuesta(" + control + ");";
            sQuery = "Select eliminar_encuesta_idU(" + control + ");";
            if (oAD == null) {
                oAD = new AccesoDatos();
                if (oAD.conectar()) {
                    oAD.ejecutarConsulta(sQuery);
                    oAD.desconectar();
                }
                oAD = null;
            } else {
                oAD.ejecutarConsulta(sQuery);
                oAD.desconectar();
            }
        } catch (Exception e) {
            throw e;
        }

    }

    public List<String> consultarRespuesta(int idMedico, int idPreg) throws Exception {
        try {
            List<String> lista = new ArrayList<>();
            String item;
            sQuery = "SELECT * from consultar_respuesta(" + idMedico + ", " + idPreg + ");";

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
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    item = String.valueOf(((Double) vRowTemp.get(0)).intValue());
                    lista.add(item);
                }

            }
            return lista;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> consultarRespuestaAbierta(int idMedico, int idPreg) throws Exception {
        try {
            List<String> lista = new ArrayList<>();
            String item;
            sQuery = "SELECT * from consultar_respuesta_abierta(" + idMedico + ", " + idPreg + ");";

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
                for (int i = 0; i < resultado.size(); i++) {
                    vRowTemp = (ArrayList) resultado.get(i);
                    item = ((String) vRowTemp.get(0));
                    lista.add(item.replace('&', ',').replace('<', '(').replace('>', ')'));
                }

            }
            return lista;
        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarFormacionEducativa(int idMedico, int idNivel) throws Exception {
        try {
            int item = 0;
            sQuery = "SELECT * from consultar_formacion_educativa(" + idMedico + ", " + idNivel + ");";

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
                vRowTemp = (ArrayList) resultado.get(0);
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarArea(int idMedico) throws Exception {
        try {
            int item = 0;
            sQuery = "SELECT * from consultar_area(" + idMedico + ");";

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
                vRowTemp = (ArrayList) resultado.get(0);
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

    public int consultarGrado(int idMedico) throws Exception {
        try {
            int item = 0;
            sQuery = "SELECT * from consultar_grado(" + idMedico + ");";

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
                vRowTemp = (ArrayList) resultado.get(0);
                item = ((Double) vRowTemp.get(0)).intValue();
            }

            return item;

        } catch (Exception e) {
            throw e;
        }
    }

}
