/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.util;

import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Reporte;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Empresa;
import bs.global.web.AplicacionBean;
import bs.global.web.PrincipalBean;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

/**
 *
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class ReportFactory implements Serializable {

    @Inject
    protected AplicacionBean aplicacionBean;

    @Inject
    protected PrincipalBean principalBean;

    private Context ctx;
    private javax.sql.DataSource ds;
    private Connection conexion;
    private String pathTemporales;
    private String pathReportes;
    private boolean iniciado;
    private boolean debug = false;

    public ReportFactory() throws NamingException, SQLException {

    }

    @PostConstruct
    public void init() {

        try {

            Parametro p = aplicacionBean.getParametro();

            ctx = new InitialContext();

            ds = (javax.sql.DataSource) ctx.lookup((p.getDataSource() == null ? "bs-demo" : p.getDataSource()));
            conexion = ds.getConnection();
            conexion.setAutoCommit(true);
            pathTemporales = p.getPathCarpetaTemporales();
            pathReportes = p.getPathCarpetaReportes();

//            System.err.println("Iniciar Report Factory");
//            System.err.println("Datasource " + (p.getDataSource() == null ? "bs-demo" : p.getDataSource()));
//            System.err.println("pathTemporales " + pathTemporales);
//            System.err.println("pathReportes " + pathReportes);
            iniciado = true;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "No es posible iniciar ReportFactory", ex);
        }

    }

    public void exportReportToPdfFile(Reporte reporte, String nombreSalida, Map parameters) throws Exception {

        if (!iniciado) {
            init();
        }

        if (reporte == null) {
            throw new ExcepcionGeneralSistema("El reporte es nulo");
        }

        if (reporte.getPath() == null) {
            throw new ExcepcionGeneralSistema("El reporte no tienen un path asignado");
        }

        if (nombreSalida == null || nombreSalida.isEmpty()) {
            nombreSalida = JsfUtil.getCadenaAlfanumAleatoria(12);
        }

        if (parameters.containsKey("DEBUG")) {
            System.err.println("pathReport " + getPathReport(reporte));
            System.err.println("nombreSalida " + nombreSalida);
            System.err.println("parameters " + parameters);
        }
//
//        System.err.println("pathReport " + getPathReport(reporte));
//        System.err.println("parameters " + parameters);
//        System.err.println("nombreSalida " + nombreSalida);

        cargarDatosOrganizacion(parameters);
        String pathReport = getPathReport(reporte);
        JasperPrint print = JasperFillManager.fillReport(pathReport, parameters, conexion);
        JasperExportManager.exportReportToPdfFile(print, pathTemporales + nombreSalida + ".pdf");

    }

    public void exportReportToXlsFile(Reporte reporte, String nombreSalida, Map parameters) throws NamingException, SQLException, JRException, IOException, Exception {

        if (!iniciado) {
            init();
        }

        if (reporte == null) {
            throw new ExcepcionGeneralSistema("El reporte es nulo");
        }

        if (reporte.getPath() == null) {
            throw new ExcepcionGeneralSistema("El reporte no tienen un path asignado");
        }

        generarExcel(reporte, nombreSalida, parameters);
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        FileInputStream entrada = new FileInputStream(pathTemporales + nombreSalida + ".xls");
        byte[] lectura = new byte[entrada.available()];
        entrada.read(lectura);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreSalida + ".xls");
        response.setContentLength(lectura.length);
        response.getOutputStream().write(lectura);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        entrada.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void exportReportToTxtFile(Reporte reporte, String nombreSalida, Map parameters) throws NamingException, SQLException, JRException, IOException, Exception {

        if (!iniciado) {
            init();
        }

        if (reporte == null) {
            throw new ExcepcionGeneralSistema("El reporte es nulo");
        }

        if (reporte.getPath() == null) {
            throw new ExcepcionGeneralSistema("El reporte no tienen un path asignado");
        }

        generarTxt(reporte, nombreSalida, parameters);

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        FileInputStream entrada = new FileInputStream(pathTemporales + nombreSalida + ".txt");
        byte[] lectura = new byte[entrada.available()];
        entrada.read(lectura);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreSalida + ".txt");
        response.setContentLength(lectura.length);
        response.getOutputStream().write(lectura);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        entrada.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void exportReportToTxtFile_(Reporte reporte, String nombreSalida, Map parameters) throws NamingException, SQLException, JRException, IOException, Exception {

        if (!iniciado) {
            init();
        }

        if (reporte == null) {
            throw new ExcepcionGeneralSistema("El reporte es nulo");
        }

        if (reporte.getPath() == null) {
            throw new ExcepcionGeneralSistema("El reporte no tienen un path asignado");
        }

        String pathReport = getPathReport(reporte);
        JasperPrint print = JasperFillManager.fillReport(pathReport, parameters, conexion);

        final JRTextExporter textExporter = new JRTextExporter();
        SimpleExporterInput exporterInput = new SimpleExporterInput(print);
        SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(new File(pathTemporales + nombreSalida + ".txt"));

        // Default item text configuration that can be overridden in the .jrxml template itself
        SimpleTextReportConfiguration textReportConfiguration = new SimpleTextReportConfiguration();
        textReportConfiguration.setCharHeight(new Float(10));
        textReportConfiguration.setCharWidth(new Float(10));
        textReportConfiguration.setOverrideHints(false);
        textExporter.setConfiguration(textReportConfiguration);

        textExporter.setExporterInput(exporterInput);
        textExporter.setExporterOutput(exporterOutput);
        textExporter.exportReport();

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        FileInputStream entrada = new FileInputStream(pathTemporales + nombreSalida + ".txt");
        byte[] lectura = new byte[entrada.available()];
        entrada.read(lectura);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreSalida + ".txt");
        response.setContentLength(lectura.length);
        response.getOutputStream().write(lectura);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        entrada.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public String exportReportToPdfFileGetPath(Reporte reporte, String nombreSalida, Map parameters) throws Exception {

        if (!iniciado) {
            init();
        }

        exportReportToPdfFile(reporte, nombreSalida, parameters);
        return pathTemporales + nombreSalida + ".pdf";
    }

    private void generarExcel(Reporte reporte, String nombreSalida, Map parameters) throws NamingException, SQLException, JRException, IOException, Exception {

        if (!iniciado) {
            init();
        }

        String query = generarQuerySQL(reporte, parameters);

        if (parameters.containsKey("DEBUG")) {
            System.err.println("pathReport " + getPathReport(reporte));
            System.err.println("nombreSalida " + nombreSalida);
            System.err.println("parameters " + parameters);
            System.err.println("query " + query);
        }

        PreparedStatement stmt = conexion.prepareStatement(query);

        ResultSet resultSet = stmt.executeQuery();

        ExcelFactory resultSetToExcel = new ExcelFactory(resultSet, nombreSalida);

        resultSetToExcel.generate(new File(pathTemporales + nombreSalida + ".xls"));

    }

    private void generarTxt(Reporte reporte, String nombreSalida, Map parameters) throws NamingException, SQLException, JRException, IOException, Exception {

        if (!iniciado) {
            init();
        }

        String query = generarQuerySQL(reporte, parameters);

//        System.err.println("query " + query);
        PreparedStatement stmt = conexion.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();

        File file = new File(pathTemporales + nombreSalida + ".txt");
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);

        int numCols = rs.getMetaData().getColumnCount();

        while (rs.next()) {

            for (int i = 1; i <= numCols; i++) {
                out.write(rs.getString(i) != null ? rs.getString(i) : "");
            }
            out.newLine();
        }

        out.close();
    }

    /**
     * Generamos un consulta sql desde un reporte
     *
     * @param reporte
     * @param parameters
     * @return
     * @throws net.sf.jasperreports.engine.JRException
     */
    public String generarQuerySQL(Reporte reporte, Map parameters) throws JRException {

        if (!iniciado) {
            init();
        }

        String pathReport = getPathReport(reporte);

        File sourceFile = new File(pathReport);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(sourceFile);

        String query = jasperReport.getQuery().getText();

//        System.err.println("parametros " + parameters);
        Iterator<Map.Entry<String, Object>> entries;
        entries = parameters.entrySet().iterator();
        while (entries.hasNext()) {

            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            String parametro = "";

            if (value == null) {
                continue;
            }

            if (value.getClass() == Integer.class || value.getClass() == Long.class
                    || value.getClass() == Float.class || value.getClass() == Double.class) {

                parametro = String.valueOf(value);

            } else if (value.getClass() == java.util.Date.class || value.getClass() == java.sql.Date.class) {

                parametro = JsfUtil.getFechaSQL((java.util.Date) value);

            } else {
                parametro = "'" + value + "'";
            }

            query = query.replaceAll("\\$P\\{" + key + "\\}", parametro);

        }

        query = query.replaceAll("EMPRESA.\\*", "' '");
        query = query.replaceAll("gr_organizacion.\\*", "' '");

        return query;
    }

    public DataSource getArchivoAdjuntoPDF(String reportName, String nombreSalida, Map parameters) throws JRException {

        JasperPrint jasperPrint = JasperFillManager.fillReport(this.getClass().getClassLoader().getResourceAsStream(reportName), parameters, conexion);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        DataSource adjunto = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
        return adjunto;

    }

    public void cargarDatosOrganizacion(Map p) {

        Empresa e = principalBean.getEmpresa();

        if (e != null) {

            p.put("EMP_NOMBRE", e.getNombreFantasia());
            p.put("EMP_RAZON_SOCIAL", e.getRazonSocial());
            p.put("EMP_DIRECCION", e.getDireccion());
            p.put("EMP_PROVINCIA", e.getProvincia());
            p.put("EMP_PAIS", e.getPais());
            p.put("EMP_TELEFONO", e.getTelefono());
            p.put("EMP_FAX", e.getFax());
            p.put("EMP_EMAIL", e.getEmail());
            p.put("EMP_CUIT", e.getCuit());
            p.put("EMP_IB", e.getNroIB());
            p.put("EMP_AG", e.getNroAG());
            p.put("EMP_INIACT", e.getInicioActividades());
            p.put("EMP_CONDICION_IVA", e.getCondicionIVA());
            p.put("EMP_CIUDAD", e.getCiudad());

        }

        if (p.get("LOGO") == null) {
            p.put("LOGO", FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/image/logo.png"));
        }
    }

    /**
     * public void verReportePDF(String pathReport, String nombreSalida, Map
     * parameters) throws NamingException, SQLException, JRException,
     * IOException {
     *
     * cargarDatosOrganizacion(parameters);
     *
     * JasperPrint print = JasperFillManager.fillReport(pathReport, parameters,
     * conexion); byte[] bytes = JasperExportManager.exportReportToPdf(print);
     *
     * HttpServletResponse response = (HttpServletResponse)
     * FacesContext.getCurrentInstance().getExternalContext().getResponse();
     * response.addHeader("Content-disposition", "inline;filename=" +
     * nombreSalida + ".pdf"); response.setContentLength(bytes.length);
     * response.getOutputStream().write(bytes);
     * response.setContentType("application/pdf");
     * FacesContext.getCurrentInstance().responseComplete();
     *
     * }
     *
     */
    /**
     * public void descargarReportePDF(JasperReport jasperReport, String
     * nombreSalida, Map parameters) throws NamingException, SQLException,
     * JRException, IOException {
     *
     * cargarDatosOrganizacion(parameters);
     *
     * JasperPrint print = JasperFillManager.fillReport(jasperReport,
     * parameters, conexion); byte[] bytes =
     * JasperExportManager.exportReportToPdf(print);
     *
     * HttpServletResponse response = (HttpServletResponse)
     * FacesContext.getCurrentInstance().getExternalContext().getResponse();
     * response.addHeader("Content-disposition", "attachment;filename=" +
     * nombreSalida + ".pdf"); response.setContentLength(bytes.length);
     * response.getOutputStream().write(bytes);
     * response.setContentType("application/pdf");
     * FacesContext.getCurrentInstance().responseComplete(); }
     *
     */
    /**
     * public String exportReportToHtmlFile(String reportName, String
     * nombreSalida, Map parameters) throws NamingException, SQLException,
     * JRException, IOException {
     *
     * String pathReport =
     * aplicacionBean.getParametro().getPathCarpetaReportes();
     *
     * JasperReport jasperReport = (JasperReport)
     * JRLoader.loadObject(getClass().getClassLoader().getResourceAsStream(reportName));
     *
     * JasperPrint print = JasperFillManager.fillReport(jasperReport,
     * parameters, conexion);
     *
     * JasperExportManager.exportReportToHtmlFile(print, pathReport +
     * "pedidoweb.html");
     *
     * File archivo = null; FileReader fr = null; BufferedReader br = null;
     * String linea = ""; String contenido = ""; try { // Apertura del fichero y
     * creacion de BufferedReader para poder // hacer una lectura comoda
     * (disponer del metodo readLine()). archivo = new File(pathReport +
     * "pedidoweb.html"); fr = new FileReader(archivo); br = new
     * BufferedReader(fr); // Lectura del fichero
     *
     * while ((linea = br.readLine()) != null) { contenido += linea + "\n"; }
     *
     * //System.err.println(contenido); return contenido;
     *
     * } catch (Exception e) { e.printStackTrace();
     *
     * } finally { // En el finally cerramos el fichero, para asegurarnos // que
     * se cierra tanto si todo va bien como si salta // una excepcion. try { if
     * (null != fr) { fr.close(); }
     *
     * return ""; } catch (Exception e2) {
     *
     * e2.printStackTrace(); return ""; } } }
     *
     */
//    public void exportReportToXlsFile(String pathReport,String nombreSalida,Map parameters) throws JRException{
//
//        cargarDatosOrganizacion(parameters);
//        JasperPrint print = JasperFillManager.fillReport(pathReport, parameters, conexion);
//
//        JRXlsExporter exportador = new JRXlsExporter();
//        exportador.setParameter(JRExporterParameter.JASPER_PRINT, print);
//        exportador.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,pathTemporales+ nombreSalida +".xls");
//        exportador.setParameter(JRExporterParameter.IGNORE_PAGE_MARGINS, true);
//        exportador.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
//        exportador.setParameter(JRXlsAbstractExporterParameter.IS_IGNORE_CELL_BORDER, false);
//        exportador.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,true);
//        exportador.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,true);
//        exportador.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,true);
//        exportador.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,true);
//
//        try {
//            exportador.exportReport();
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//    }
    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public javax.sql.DataSource getDs() {
        return ds;
    }

    public void setDs(javax.sql.DataSource ds) {
        this.ds = ds;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public String getPathTemporales() {
        return pathTemporales;
    }

    public void setPathTemporales(String pathTemporales) {
        this.pathTemporales = pathTemporales;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Obtenemos el path del reporte. Si es un reporte de usuario lo obtenermos
     * de un directorio diferente si es un reporte de sistema, está dentro de la
     * aplicación.
     *
     * @param origen
     * @param nombreReporte
     * @return
     */
    private String getPathReport(Reporte reporte) {

        if (reporte.getOrigen().equals("SIS")) {
            return FacesContext.getCurrentInstance().getExternalContext().getRealPath(reporte.getPath());
        } else {
            return pathReportes + reporte.getPath();
        }
    }

}
