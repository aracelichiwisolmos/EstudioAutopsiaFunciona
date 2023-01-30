package hospital.datos;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

/**
 * Clase que realiza el acceso a la base de datos. Driver, sUrl, sUsr, sPwd se
 * leen del archivo de configuración Autor: BAOZ
 */
public final class AccesoDatos implements Serializable {

    private static String sUrl = null;  // sUrl de conexión
    private static String sUsr = null; // Usuario
    private static String sPwd = null;  // Contraseña
    private static String sDriver = null; //driver del gestor
    private java.sql.Connection oConexion;  // La conexión

    public AccesoDatos() throws Exception {
        //La asociación al archivo de conexión cambia dependiendo de JSF,Srv,etc.
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getInitParameterMap();
        sUrl = params.get("urldb");
        sUsr = params.get("usrdb");
        sPwd = params.get("pwddb");
        sDriver = params.get("driverdb");
    }

    /**
     * Realiza la conexión a la base de datos.
     */
    public boolean conectar() throws Exception {
        boolean bRet = false;
        try {
            Class.forName(sDriver).newInstance();
            oConexion = DriverManager.getConnection(sUrl, sUsr, sPwd);
            bRet = true;
        } catch (SQLException e) {
            throw e;
        }
        return bRet;
    }

    /**
     * Realiza la desconexión a la base de datos.
     */
    public void desconectar() throws Exception {
        oConexion.close();
    }

    /**
     * Código que se ejecuta cuando este objeto es recolectado.
     */
    @Override
    public void finalize() throws Exception, Throwable {
        try {
            oConexion.close();
            oConexion = null;
        } finally {
            super.finalize();
        }
    }

    /**
     * Realiza una consulta a la base de datos y retorna un arreglo de arreglos
     * de objetos con los resultados.
     */
    public synchronized ArrayList ejecutarConsulta(String psQuery) throws Exception {
        Statement stmt = null;
        ResultSet rset = null;
        ArrayList vrset = null;
        ResultSetMetaData rsmd = null;
        int nNumCols = 0;
        try {
            stmt = oConexion.createStatement();
            rset = stmt.executeQuery(psQuery);
            rsmd = rset.getMetaData();
            nNumCols = rsmd.getColumnCount();
            vrset = convierteALista(rset, rsmd, nNumCols);
        } finally {
            if (rset != null) {
                rset.close();
                if (stmt != null) {
                    stmt.close();
                }
            }
            rset = null;
            stmt = null;
        }
        return vrset;
    }

    /**
     * Realiza una petición de modificación de datos, retornando un entero con
     * el número de registros afectados.
     */
    public synchronized int ejecutarComando(String psStatement)
            throws Exception {

        int ret = 0;
        ArrayList vTransaction = new ArrayList();

        vTransaction.add(psStatement);
        ret = ejecutarComando(vTransaction);

        return ret;
    }

    /**
     * Realiza una serie de peticiones de modificación de datos, retornando un
     * entero con el número de registros afectados. Estas peticiones se ejecutan
     * en una transacción.
     */
    public synchronized int ejecutarComando(ArrayList pvStatement)
            throws Exception {

        int ret = 0, i = 0;
        Statement stmt = null;
        String temp = "";

        try {
            oConexion.setAutoCommit(false);
            stmt = oConexion.createStatement();
            for (i = 0; i < pvStatement.size(); i++) {
                temp = (String) pvStatement.get(i);
                ret += stmt.executeUpdate(temp);
            }
            oConexion.commit();
        } catch (SQLException e) {
            oConexion.rollback();
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            stmt = null;
        }

        return ret;
    }

    /**
     * Recorre un result set y entrega el arreglo resultante.
     */
    private synchronized ArrayList convierteALista(ResultSet rset,
            ResultSetMetaData rsmd,
            int nNumCols)
            throws Exception {
        ArrayList vrset = new ArrayList();
        ArrayList vrsettmp = null;
        int i = 0;

        while (rset.next()) {
            vrsettmp = new ArrayList();
            for (i = 1; i <= nNumCols; i++) {
                switch (rsmd.getColumnType(i)) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        String varchar = "" + doubleQuote(rset.getString(i));
                        vrsettmp.add(varchar);
                        break;
                    case Types.INTEGER:
                        vrsettmp.add(new Double(rset.getLong(i)));
                        break;
                    case Types.SMALLINT:
                        vrsettmp.add(new Double(rset.getInt(i)));
                        break;
                    case Types.BIGINT:
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                    case Types.FLOAT:
                    case Types.DOUBLE:
                        vrsettmp.add(new Double(rset.getDouble(i)));
                        break;
                    case Types.DATE:
                    case Types.TIME:
                    case Types.TIMESTAMP:
                        vrsettmp.add((rset.getTimestamp(i) == null ? null : new Date(rset.getTimestamp(i).getTime())));
                        break;
                    default:
                        String str = "" + rset.getString(i);
                        vrsettmp.add(str);
                } //switch
            }  //for
            vrset.add(vrsettmp);
        } //while
        return vrset;
    }

    /**
     * Si la cadena contiene comillas en la base de datos, convierte a código.
     *
     * @return String cadena sin las comillas internas.
     */
    private String doubleQuote(String psCadena) {
        if (psCadena == null) {
            psCadena = "";
        }
        String CadenaEntrada = "";
        if (psCadena.equals("")) {
            return psCadena;
        } else if (psCadena.equals("\"")) {
            return "&quot;";
        } else {
            int indice = -2;
            CadenaEntrada = psCadena;
            while ((indice = CadenaEntrada.indexOf("\"", indice + 2)) != -1) {
                CadenaEntrada = CadenaEntrada.substring(0, CadenaEntrada.indexOf("\"", indice)) + "&quot;" + CadenaEntrada.substring(CadenaEntrada.indexOf("\"", indice) + 1);
            }
        }
        return CadenaEntrada;
    }

    public synchronized ArrayList<String> atributos(String nombreBase, String nombre) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            DatabaseMetaData metadata = oConexion.getMetaData();
            ResultSet atributos = metadata.getColumns(null, null, nombre, null);
            while (atributos.next()) {
                ret.add(atributos.getString("COLUMN_NAME") + ":" + atributos.getString("TYPE_NAME") + ":" + atributos.getString("COLUMN_SIZE"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccesoDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
}
