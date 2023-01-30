package hospital.controlador;

import hospital.modelo.IdentificacionUsuario;
import hospital.modelo.clasificadora;
import hospital.modelo.encuesta;
import hospital.modelo.typeClasificado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oConsultaEncuesta")
@SessionScoped
public class consultaEncuestaControler implements Serializable {

    private int resp1_Seleccionado;
    private int resp2_Seleccionado;
    private int resp3_Seleccionado;
    private int resp4_Seleccionado;
    private int resp5_Seleccionado;
    private String resp6_Seleccionado;
    private String resp7_Seleccionado;
    private List<String> resp8_Seleccionado;
    private String respAbierta8_Seleccionado;
    private List<String> resp9_Seleccionado;
    private String respAbierta9_Seleccionado;
    private List<String> resp10_Seleccionado;
    private String respAbierta10_Seleccionado;
    private List<String> resp11_Seleccionado;
    private String respAbierta11_Seleccionado;
    private List<String> resp12_Seleccionado;
    private String respAbierta12_Seleccionado;
    private String resp13_Seleccionado;
    private int resp14_AreaSeleccionado;
    private int resp15_GradoSeleccionado;
    private int resp16_CentroLicSeleccionado;
    private int resp17_CentroEspSeleccionado;
    private int idMedico;
    private int control;

    //-------------------
    private int centro_hospitalario;
    private int institucion;
    private int localidad;
    private int municipio;
    //-------

    private identificacionUsuarioJB oIdUS;

    private encuesta oEncuesta;
    private clasificadora oClasificadora;

    public consultaEncuestaControler() throws Exception {
        this.centro_hospitalario = 0;
        this.institucion = 0;
        this.localidad = 0;
        this.municipio = 0;
        this.resp1_Seleccionado = 0;
        this.resp2_Seleccionado = 0;
        this.resp3_Seleccionado = 0;
        this.resp4_Seleccionado = 0;
        this.resp5_Seleccionado = 0;
        this.resp6_Seleccionado = "";
        this.resp7_Seleccionado = "";
        this.resp8_Seleccionado = null;
        this.respAbierta8_Seleccionado = "";
        this.resp9_Seleccionado = null;
        this.respAbierta9_Seleccionado = "";
        this.resp10_Seleccionado = null;
        this.respAbierta10_Seleccionado = "";
        this.resp11_Seleccionado = null;
        this.respAbierta11_Seleccionado = "";
        this.resp12_Seleccionado = null;
        this.respAbierta12_Seleccionado = "";
        this.resp13_Seleccionado = null;
        this.resp14_AreaSeleccionado = 0;
        this.resp15_GradoSeleccionado = 0;
        this.resp16_CentroLicSeleccionado = 0;
        this.resp17_CentroEspSeleccionado = 0;

        this.oEncuesta = new encuesta();
        this.oClasificadora = new clasificadora();

    }

    public String obtenStringPregAbiertas(List<String> lista) {
        String resultado = "";
        /*for (String valor : lista) {resultado += valor + "\n";}*/
        resultado = lista.stream().map((valor) -> valor + "\n").reduce(resultado, String::concat);
        return resultado;

    }

    public String consultarEncuesta(int idMed, int idCont) throws Exception {
        encuesta oSurvey = new encuesta();
        idMedico = idMed;
        control = idCont;
        System.out.println("id_medico_" + idMed);
        IdentificacionUsuario idus = new IdentificacionUsuario();

        //.......
        this.centro_hospitalario = idus.consultarCentroHospitalario(this.idMedico);
        this.institucion = idus.consultarInstitucion(this.idMedico);
        this.municipio = idus.consultarMunicipio(this.idMedico);
        this.localidad = idus.consultarLocalidad(this.idMedico);
        //.....
        this.resp1_Seleccionado = Integer.parseInt(oSurvey.consultarRespuesta(this.idMedico, 3).get(0));
        this.resp2_Seleccionado = Integer.parseInt(oSurvey.consultarRespuesta(this.idMedico, 4).get(0));
        this.resp3_Seleccionado = Integer.parseInt(oSurvey.consultarRespuesta(this.idMedico, 7).get(0));
        this.resp4_Seleccionado = Integer.parseInt(oSurvey.consultarRespuesta(this.idMedico, 8).get(0));
        this.resp5_Seleccionado = Integer.parseInt(oSurvey.consultarRespuesta(this.idMedico, 11).get(0));
        this.resp6_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 14));
        this.resp7_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 16));
        this.resp8_Seleccionado = oSurvey.consultarRespuesta(this.idMedico, 17);
        this.respAbierta8_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 17));
        this.resp9_Seleccionado = oSurvey.consultarRespuesta(this.idMedico, 18);
        this.respAbierta9_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 18));
        this.resp10_Seleccionado = oSurvey.consultarRespuesta(this.idMedico, 19);
        this.respAbierta10_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 19));
        this.resp11_Seleccionado = oSurvey.consultarRespuesta(this.idMedico, 20);
        this.respAbierta11_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 20));
        this.resp12_Seleccionado = oSurvey.consultarRespuesta(this.idMedico, 21);
        this.respAbierta12_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 21));
        this.resp13_Seleccionado = obtenStringPregAbiertas(oSurvey.consultarRespuestaAbierta(this.idMedico, 22));
        this.resp14_AreaSeleccionado = oSurvey.consultarArea(this.idMedico);
        this.resp15_GradoSeleccionado = oSurvey.consultarGrado(this.idMedico);
        this.resp16_CentroLicSeleccionado = oSurvey.consultarFormacionEducativa(this.idMedico, 2);
        this.resp17_CentroEspSeleccionado = oSurvey.consultarFormacionEducativa(this.idMedico, 1);

        return "consultar_encuesta";
    }

    //--------------
    public void actualizarEncuesta2(int idMed, IdentificacionUsuario iu) throws Exception {
        try {
            System.out.println("entra en actualizar" + iu + "   " + idMed);
            List<String> lista = new ArrayList<>();
            int[] array_rep8, array_rep9, array_rep10, array_rep11, array_rep12;
            typeClasificado[] array_repA6, array_repA7, array_repA8, array_repA9, array_repA10, array_repA11, array_repA12, array_repA13;

            array_rep8 = devuelveArregloEnteros(resp8_Seleccionado);
            array_rep9 = devuelveArregloEnteros(resp9_Seleccionado);
            array_rep10 = devuelveArregloEnteros(resp10_Seleccionado);
            array_rep11 = devuelveArregloEnteros(resp11_Seleccionado);
            array_rep12 = devuelveArregloEnteros(resp12_Seleccionado);

            resp6_Seleccionado = resp6_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : resp6_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA6 = devuelveArregloTypeClasificado(lista, 6);
            lista.clear();

            resp7_Seleccionado = resp7_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : resp7_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA7 = devuelveArregloTypeClasificado(lista, 7);
            lista.clear();

            respAbierta8_Seleccionado = respAbierta8_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : respAbierta8_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA8 = devuelveArregloTypeClasificado(lista, 8);
            lista.clear();

            respAbierta9_Seleccionado = respAbierta9_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : respAbierta9_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA9 = devuelveArregloTypeClasificado(lista, 9);
            lista.clear();

            respAbierta10_Seleccionado = respAbierta10_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : respAbierta10_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA10 = devuelveArregloTypeClasificado(lista, 10);
            lista.clear();

            respAbierta11_Seleccionado = respAbierta11_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : respAbierta11_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA11 = devuelveArregloTypeClasificado(lista, 11);
            lista.clear();

            respAbierta12_Seleccionado = respAbierta12_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : respAbierta12_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA12 = devuelveArregloTypeClasificado(lista, 12);
            lista.clear();

            resp13_Seleccionado = resp13_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
            for (String item : resp13_Seleccionado.split("\\.")) {
                lista.add(item.trim());
            }
            array_repA13 = devuelveArregloTypeClasificado(lista, 13);
            lista.clear();

            oEncuesta.actualizarEncuesta(idMed, resp15_GradoSeleccionado, resp14_AreaSeleccionado,
                    resp17_CentroEspSeleccionado, resp16_CentroLicSeleccionado, resp1_Seleccionado, resp2_Seleccionado,
                    resp3_Seleccionado, resp4_Seleccionado, resp5_Seleccionado,
                    array_rep8, array_rep9, array_rep10, array_rep11, array_rep12, array_repA6,
                    array_repA7, array_repA8, array_repA9, array_repA10, array_repA11, array_repA12,
                    array_repA13, iu);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));

        }
    }
    //------

//    public void insertarEncuesta() throws Exception {
//        try {
//            System.out.println("entra en insert=");
//            List<String> lista = new ArrayList<>();
//            int[] array_rep8, array_rep9, array_rep10, array_rep11, array_rep12;
//            typeClasificado[] array_repA6, array_repA7, array_repA8, array_repA9, array_repA10, array_repA11, array_repA12, array_repA13;
//
//            array_rep8 = devuelveArregloEnteros(resp8_Seleccionado);
//            array_rep9 = devuelveArregloEnteros(resp9_Seleccionado);
//            array_rep10 = devuelveArregloEnteros(resp10_Seleccionado);
//            array_rep11 = devuelveArregloEnteros(resp11_Seleccionado);
//            array_rep12 = devuelveArregloEnteros(resp12_Seleccionado);
//
//            resp6_Seleccionado = resp6_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : resp6_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA6 = devuelveArregloTypeClasificado(lista, 6);
//            lista.clear();
//
//            resp7_Seleccionado = resp7_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : resp7_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA7 = devuelveArregloTypeClasificado(lista, 7);
//            lista.clear();
//
//            respAbierta8_Seleccionado = respAbierta8_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : respAbierta8_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA8 = devuelveArregloTypeClasificado(lista, 8);
//            lista.clear();
//
//            respAbierta9_Seleccionado = respAbierta9_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : respAbierta9_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA9 = devuelveArregloTypeClasificado(lista, 9);
//            lista.clear();
//
//            respAbierta10_Seleccionado = respAbierta10_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : respAbierta10_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA10 = devuelveArregloTypeClasificado(lista, 10);
//            lista.clear();
//
//            respAbierta11_Seleccionado = respAbierta11_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : respAbierta11_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA11 = devuelveArregloTypeClasificado(lista, 11);
//            lista.clear();
//
//            respAbierta12_Seleccionado = respAbierta12_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : respAbierta12_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA12 = devuelveArregloTypeClasificado(lista, 12);
//            lista.clear();
//
//            resp13_Seleccionado = resp13_Seleccionado.replace(',', '&').replace('(', '<').replace(')', '>');
//            for (String item : resp13_Seleccionado.split("\\.")) {
//                lista.add(item.trim());
//            }
//            array_repA13 = devuelveArregloTypeClasificado(lista, 13);
//            lista.clear();
//            System.out.println("entra en insert2");
//            oEncuesta.insertarEncuesta(resp15_GradoSeleccionado, resp14_AreaSeleccionado,
//                    resp17_CentroEspSeleccionado, resp16_CentroLicSeleccionado, resp1_Seleccionado, resp2_Seleccionado,
//                    resp3_Seleccionado, resp4_Seleccionado, resp5_Seleccionado,
//                    array_rep8, array_rep9, array_rep10, array_rep11, array_rep12, array_repA6,
//                    array_repA7, array_repA8, array_repA9, array_repA10, array_repA11, array_repA12,
//                    array_repA13);
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Operación exitosa."));
//            System.out.println("entra en insert3");
//        } catch (Exception e) {
//            e.printStackTrace();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Error:" + e.getMessage()));
//
//        }
//    }
    public int[] devuelveArregloEnteros(List<String> lista) {
        int[] resultado = new int[lista.size()];
        String item;
        for (int i = 0; i < lista.size(); i++) {
            item = lista.get(i);
            resultado[i] = Integer.parseInt(item);
        }
        return resultado;
    }

    public typeClasificado[] devuelveArregloTypeClasificado(List<String> lista, int pregunta) throws Exception {
        typeClasificado[] resultado = new typeClasificado[lista.size()];
        typeClasificado oTipo;
        String texto;
        String clasificado;

        if (pregunta == 6 || pregunta == 7 || pregunta == 13) {
            for (int i = 0; i < lista.size(); i++) {
                texto = lista.get(i);
                if (!texto.equals("")) {
                    clasificado = oClasificadora.clasifica(texto, pregunta);
                    oTipo = new typeClasificado(texto, clasificado);
                    resultado[i] = oTipo;
                } else {
                    oTipo = new typeClasificado();
                    resultado[i] = oTipo;
                }

            }
        } else if (pregunta == 8 || pregunta == 9 || pregunta == 10 || pregunta == 11 || pregunta == 12) {
            for (int i = 0; i < lista.size(); i++) {
                texto = lista.get(i);
                if (!texto.equals("")) {
                    oTipo = new typeClasificado(texto, "z");
                    resultado[i] = oTipo;
                } else {
                    oTipo = new typeClasificado();
                    resultado[i] = oTipo;
                }
            }
        }
        return resultado;
    }

    public String actualizarEncuesta(IdentificacionUsuario iu) throws Exception {

        try {
            actualizarEncuesta2(104, iu);
            //   insertarEncuesta();
            System.out.println("Entro en actualizar");
            return "encuesta";
        } catch (Exception e) {
            throw e;
        }

    }

    public int getResp1_Seleccionado() {
        return resp1_Seleccionado;
    }

    public void setResp1_Seleccionado(int resp1_Seleccionado) {
        this.resp1_Seleccionado = resp1_Seleccionado;
    }

    public int getResp2_Seleccionado() {
        return resp2_Seleccionado;
    }

    public void setResp2_Seleccionado(int resp2_Seleccionado) {
        this.resp2_Seleccionado = resp2_Seleccionado;
    }

    public int getResp3_Seleccionado() {
        return resp3_Seleccionado;
    }

    public void setResp3_Seleccionado(int resp3_Seleccionado) {
        this.resp3_Seleccionado = resp3_Seleccionado;
    }

    public int getResp4_Seleccionado() {
        return resp4_Seleccionado;
    }

    public void setResp4_Seleccionado(int resp4_Seleccionado) {
        this.resp4_Seleccionado = resp4_Seleccionado;
    }

    public int getResp5_Seleccionado() {
        return resp5_Seleccionado;
    }

    public void setResp5_Seleccionado(int resp5_Seleccionado) {
        this.resp5_Seleccionado = resp5_Seleccionado;
    }

    public String getResp6_Seleccionado() {
        return resp6_Seleccionado;
    }

    public void setResp6_Seleccionado(String resp6_Seleccionado) {
        this.resp6_Seleccionado = resp6_Seleccionado;
    }

    public String getResp7_Seleccionado() {
        return resp7_Seleccionado;
    }

    public void setResp7_Seleccionado(String resp7_Seleccionado) {
        this.resp7_Seleccionado = resp7_Seleccionado;
    }

    public List<String> getResp8_Seleccionado() {
        return resp8_Seleccionado;
    }

    public void setResp8_Seleccionado(List<String> resp8_Seleccionado) {
        this.resp8_Seleccionado = resp8_Seleccionado;
    }

    public String getRespAbierta8_Seleccionado() {
        return respAbierta8_Seleccionado;
    }

    public void setRespAbierta8_Seleccionado(String respAbierta8_Seleccionado) {
        this.respAbierta8_Seleccionado = respAbierta8_Seleccionado;
    }

    public List<String> getResp9_Seleccionado() {
        return resp9_Seleccionado;
    }

    public void setResp9_Seleccionado(List<String> resp9_Seleccionado) {
        this.resp9_Seleccionado = resp9_Seleccionado;
    }

    public String getRespAbierta9_Seleccionado() {
        return respAbierta9_Seleccionado;
    }

    public void setRespAbierta9_Seleccionado(String respAbierta9_Seleccionado) {
        this.respAbierta9_Seleccionado = respAbierta9_Seleccionado;
    }

    public List<String> getResp10_Seleccionado() {
        return resp10_Seleccionado;
    }

    public void setResp10_Seleccionado(List<String> resp10_Seleccionado) {
        this.resp10_Seleccionado = resp10_Seleccionado;
    }

    public String getRespAbierta10_Seleccionado() {
        return respAbierta10_Seleccionado;
    }

    public void setRespAbierta10_Seleccionado(String respAbierta10_Seleccionado) {
        this.respAbierta10_Seleccionado = respAbierta10_Seleccionado;
    }

    public List<String> getResp11_Seleccionado() {
        return resp11_Seleccionado;
    }

    public void setResp11_Seleccionado(List<String> resp11_Seleccionado) {
        this.resp11_Seleccionado = resp11_Seleccionado;
    }

    public String getRespAbierta11_Seleccionado() {
        return respAbierta11_Seleccionado;
    }

    public void setRespAbierta11_Seleccionado(String respAbierta11_Seleccionado) {
        this.respAbierta11_Seleccionado = respAbierta11_Seleccionado;
    }

    public List<String> getResp12_Seleccionado() {
        return resp12_Seleccionado;
    }

    public void setResp12_Seleccionado(List<String> resp12_Seleccionado) {
        this.resp12_Seleccionado = resp12_Seleccionado;
    }

    public String getRespAbierta12_Seleccionado() {
        return respAbierta12_Seleccionado;
    }

    public void setRespAbierta12_Seleccionado(String respAbierta12_Seleccionado) {
        this.respAbierta12_Seleccionado = respAbierta12_Seleccionado;
    }

    public String getResp13_Seleccionado() {
        return resp13_Seleccionado;
    }

    public void setResp13_Seleccionado(String resp13_Seleccionado) {
        this.resp13_Seleccionado = resp13_Seleccionado;
    }

    public int getResp14_AreaSeleccionado() {
        return resp14_AreaSeleccionado;
    }

    public void setResp14_AreaSeleccionado(int resp14_AreaSeleccionado) {
        this.resp14_AreaSeleccionado = resp14_AreaSeleccionado;
    }

    public int getResp15_GradoSeleccionado() {
        return resp15_GradoSeleccionado;
    }

    public void setResp15_GradoSeleccionado(int resp15_GradoSeleccionado) {
        this.resp15_GradoSeleccionado = resp15_GradoSeleccionado;
    }

    public int getResp16_CentroLicSeleccionado() {
        return resp16_CentroLicSeleccionado;
    }

    public void setResp16_CentroLicSeleccionado(int resp16_CentroLicSeleccionado) {
        this.resp16_CentroLicSeleccionado = resp16_CentroLicSeleccionado;
    }

    public int getResp17_CentroEspSeleccionado() {
        return resp17_CentroEspSeleccionado;
    }

    public void setResp17_CentroEspSeleccionado(int resp17_CentroEspSeleccionado) {
        this.resp17_CentroEspSeleccionado = resp17_CentroEspSeleccionado;
    }

    public int getMedicoId() {
        return idMedico;
    }

    public void setMedicoId(int medicoId) {
        this.idMedico = medicoId;
    }

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    //-------------------
    public int getCentro_hospitalario() {
        return centro_hospitalario;
    }

    public void setCentro_hospitalario(int centro_hospitalario) {
        this.centro_hospitalario = centro_hospitalario;
    }

    public int getInstitucion() {
        return institucion;
    }

    public void setInstitucion(int institucion) {
        this.institucion = institucion;
    }

    public int getLocalidad() {
        return localidad;
    }

    public void setLocalidad(int localidad) {
        this.localidad = localidad;
    }

    public int getMunicipio() {
        return municipio;
    }

    public void setMunicipio(int municipio) {
        this.municipio = municipio;
    }

}
