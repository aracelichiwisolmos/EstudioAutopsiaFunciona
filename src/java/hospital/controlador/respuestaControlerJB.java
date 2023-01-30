package hospital.controlador;

import hospital.clasificacion.model.clasificacionUtilidades;
import hospital.modelo.IdentificacionUsuario;
import hospital.modelo.centroEstudio;
import hospital.modelo.ciudad;
import hospital.modelo.clasificadora;
import hospital.modelo.encuesta;
import hospital.modelo.estado;
import hospital.modelo.respuesta;
import hospital.modelo.typeClasificado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "oRespuestaJB")
@ViewScoped
public class respuestaControlerJB implements Serializable {

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

    private typeClasificado oTypeClasificado;
    private encuesta oEncuesta;
    private clasificadora oClasificadora;
    private centroEstudio oEstudio;
    private estado oEstado;
    private ciudad oCiudad;
    respuesta orespuesta;
    private List<respuesta> lrespuesta;

    public respuestaControlerJB() throws Exception {
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
        this.resp13_Seleccionado = "";
        this.resp14_AreaSeleccionado = 0;
        this.resp15_GradoSeleccionado = 0;
        this.resp16_CentroLicSeleccionado = 0;
        this.resp17_CentroEspSeleccionado = 0;
        this.oEncuesta = new encuesta();
        this.oTypeClasificado = new typeClasificado();
        this.oClasificadora = new clasificadora();
        this.orespuesta = new respuesta();
        this.lrespuesta = orespuesta.infoRespuesta();
        this.oEstudio = new centroEstudio();
    }

    public void insertarEncuesta(IdentificacionUsuario iu) throws Exception {
        try {
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

            oEncuesta.insertarEncuesta(resp15_GradoSeleccionado, resp14_AreaSeleccionado,
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
        clasificacionUtilidades oClasificaUtilidades = new clasificacionUtilidades();
        typeClasificado[] resultado = new typeClasificado[lista.size()];
        typeClasificado oTipo;
        String texto;
        String clasificado;
        configuracionAppControler oConf = new configuracionAppControler();

        if (pregunta == 6 || pregunta == 7 || pregunta == 13) {
            String pathModel = null;
            String sQuery = null;
            switch (pregunta) {
                case 6:
                    pathModel = oConf.getPahtModel_mcc_aut();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=14;";
                    break;
                case 7:
                    pathModel = oConf.getPahtModel_mcc_no_aut();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=16;";
                    break;
                case 13:
                    pathModel = oConf.getPahtModel_com_op_sug();
                    sQuery = "SELECT texto, clasificado FROM entrenamiento_texto WHERE id_pregunta=22;";
                    break;
            }
            for (int i = 0; i < lista.size(); i++) {
                texto = lista.get(i);
                if (!texto.equals("")) {
                    clasificado = oClasificaUtilidades.clasificarNuevaInstancia(texto, pathModel, sQuery);
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

    public typeClasificado getoTypeClasificado() {
        return oTypeClasificado;
    }

    public void setoTypeClasificado(typeClasificado oTypeClasificado) {
        this.oTypeClasificado = oTypeClasificado;
    }

    public encuesta getoEncuesta() {
        return oEncuesta;
    }

    public void setoEncuesta(encuesta oEncuesta) {
        this.oEncuesta = oEncuesta;
    }

    public clasificadora getoClasificadora() {
        return oClasificadora;
    }

    public void setoClasificadora(clasificadora oClasificadora) {
        this.oClasificadora = oClasificadora;
    }

    public List<respuesta> getLrespuesta() {
        return lrespuesta;
    }

    public void setLrespuesta(List<respuesta> lrespuesta) {
        this.lrespuesta = lrespuesta;
    }
//----

    public centroEstudio getEstudio() {
        return oEstudio;
    }

    public void setEstudio(centroEstudio oEstudio) {
        this.oEstudio = oEstudio;
    }

    public estado getEstado() {
        return oEstado;
    }

    public void setEstado(estado oEstado) {
        this.oEstado = oEstado;
    }

    public ciudad getCiudad() {
        return oCiudad;
    }

    public void setEstado(ciudad oCiudad) {
        this.oCiudad = oCiudad;
    }
}
