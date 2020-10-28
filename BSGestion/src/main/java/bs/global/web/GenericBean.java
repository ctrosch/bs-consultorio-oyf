/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.administracion.modelo.Reporte;
import bs.administracion.modelo.Vista;
import bs.administracion.modelo.VistaDetalle;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.administracion.rn.PlantillaRN;
import bs.administracion.rn.ReporteRN;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.MonedaRN;
import bs.global.util.JsfUtil;
import bs.seguridad.modelo.Menu;
import bs.seguridad.rn.MenuRN;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author ctrosch Bean genérico que contiene atributos y funcionalidades
 * comunes a todos los bean del sistema
 */
public class GenericBean implements Serializable {

    @EJB
    protected MenuRN menuRN;
    @EJB
    protected ModuloRN moduloRN;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    protected ReporteRN reporteRN;
    @EJB
    protected PlantillaRN plantillaRN;
    @EJB
    protected MonedaRN monedaRN;

    protected Logger log = Logger.getLogger(this.getClass().getName());

    protected Map<String, String> filtro;
    protected List<String> orderBy;
    protected Map filtroGrupo;
    protected Map filtroDetalle;

    protected String titulo;
    protected String codMenu;
    protected String codigoVista;
    protected Vista vista;
    protected Menu menuSeleccionado;
    protected int nivelPermiso;
    protected boolean beanIniciado = false;
    protected boolean mostrarDebaja;
    protected boolean esNuevo;
    protected boolean detalleVacio;
    protected String txtBusqueda;
    protected int cantidadRegistros;

    protected int indexTab1;
    protected int indexTab2;
    protected int indexTab3;
    protected int indexTab4;
    protected int indexTab5;

    protected int indiceWizard;

    protected boolean esAnulacion;
    protected boolean seleccionaMovimiento;
    protected boolean seleccionaTodo;
    protected boolean seleccionaPendiente;
    protected boolean mostrarSoloPendiente;

    protected String codigoReporte;
    protected Reporte reporte;
    protected String nombreArchivo;
    protected String pathArchivo;

    protected String emailEnvioComprobante;
    protected String informacionAdicional;
    protected boolean muestraReporte;
    protected boolean solicitaEmail;

    //VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected boolean buscaMovimiento;
    protected String accionEnLote;
    protected String modoVista; // B - Buscador | D - Datos
    protected Comprobante comprobante;
    protected PuntoVenta puntoVenta;
    protected Formulario formulario;
    protected Integer numeroFormularioDesde;
    protected Integer numeroFormularioHasta;
    protected Date fechaMovimientoDesde;
    protected Date fechaMovimientoHasta;
    //protected Date fechaMovimeintoMaxima;
    protected Date fechaMovimientoMaxima;
    protected BigDecimal cotizacion;

    @Inject
    private PrincipalBean principalBean;

    /**
     * Creates a new instance of GenericBean
     */
    public GenericBean() {

        filtro = new HashMap<String, String>();
        filtroGrupo = new HashMap();
        filtroDetalle = new HashMap();
        orderBy = new ArrayList<String>();

        fechaMovimientoMaxima = new Date();

        txtBusqueda = "";
        mostrarDebaja = false;
        cantidadRegistros = 50;

        indexTab1 = 0;
        indexTab2 = 0;
        indexTab3 = 0;
        indexTab4 = 0;
        indexTab5 = 0;

        indiceWizard = 0;
    }

    public void iniciar() {

        if (codMenu != null && !codMenu.isEmpty() && menuSeleccionado == null && principalBean.getUsuario() != null) {

            //ItemMenuUsuario imu = menuRN.getItemMenuUsuario(principalBean.getUsuario().getId(), codMenu);
            //menuSeleccionado = imu.getMenu();
            //nivelPermiso = imu.getNivelPermiso();
            menuSeleccionado = menuRN.getMenu(codMenu);
            vista = menuSeleccionado.getVista();
            addHistorial();
            codMenu = null;
        }

        cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        modoVista = "B";
        mostrarDebaja = false;
        muestraReporte = false;
        solicitaEmail = false;
        buscaMovimiento = false;
        esNuevo = false;
        esAnulacion = false;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        txtBusqueda = "";

    }

    public void addFavorito() {

        if (menuSeleccionado != null) {
            principalBean.agregarFavorito(menuSeleccionado);
            JsfUtil.addInfoMessage("El link " + menuSeleccionado.getNombre() + " se ha agregado a tus favoritos");
        }
    }

    public void addHistorial() {

        principalBean.agregarReciente(menuSeleccionado);
    }

    public boolean muestroCampo(String tipo, String campo) {

        if (campo == null) {
//            System.err.println("campo "+campo+" sale 1");
            return true;
        }
        if (vista == null) {
//            System.err.println("campo "+campo+" sale 2");
            return true;
        }
        if (vista.getDetalle() == null) {
//            System.err.println("campo "+campo+" sale 3");
            return true;
        }

        for (VistaDetalle i : vista.getDetalle()) {

            if (i.getTipo().equals(tipo) && i.getCampo().equals(campo)) {
//System.err.println("campo "+campo+" sale 4");
                return i.isVisible();
            }
        }
        return true;
    }

    public String obtenerUrlMenu(String idMenu) {

        Menu m = menuRN.getMenu(idMenu);
        if (m != null
                && m.getModulo().getActivo().equals("S")
                && m.getActivo().equals("S")) {
            return m.getUrlCompleta();
        }

        return "";
    }

    public String obtenerUrlMenu(String idMenu, String campo, String valor) {

//        System.err.println("Ejecuta este");
        Menu m = menuRN.getMenu(idMenu);
        if (m != null
                && m.getModulo().getActivo().equals("S")
                && m.getActivo().equals("S")
                && campo != null
                && !campo.isEmpty()
                && valor != null
                && !valor.isEmpty()) {

            return m.getUrlCompleta() + "&" + campo + "=" + valor;
        } else if (m != null
                && m.getModulo().getActivo().equals("S")
                && m.getActivo().equals("S")
                && campo != null
                && !campo.isEmpty()
                || valor == null
                || valor.isEmpty()) {
            return m.getUrlCompleta();
        } else {
            return "";
        }
    }

    public String obtenerUrlMenu(String idMenu, String campo, String valor, String campo2, String valor2) {

//        System.err.println("Ejecuta este");
        Menu m = menuRN.getMenu(idMenu);
        if (m != null
                && m.getModulo().getActivo().equals("S")
                && m.getActivo().equals("S")
                && campo != null
                && !campo.isEmpty()
                && valor != null
                && !valor.isEmpty()
                && campo2 != null
                && !campo2.isEmpty()
                && valor2 != null
                && !valor2.isEmpty()) {

            return m.getUrlCompleta() + "&" + campo + "=" + valor + "&" + campo2 + "=" + valor2;
        } else if (m != null
                && m.getModulo().getActivo().equals("S")
                && m.getActivo().equals("S")
                && campo != null
                && !campo.isEmpty()
                || valor == null
                || valor.isEmpty()) {
            return m.getUrlCompleta();
        } else {
            return "";
        }
    }

    public boolean isActiveModulo(String modulo) {

        return moduloRN.isActiveModulo(modulo);

    }

//    public abstract void cargarFiltroBusqueda();
//
//    public abstract void limpiarFiltroBusqueda();
    /**
     * Permite controlar los tabs seleccionados luego de actualizar el
     * formulario en la vista del usuario
     *
     * @param event
     */
    public void onTab1Change(TabChangeEvent event) {

        indexTab1 = getIndexTab(event.getTab().getId());
    }

    public void onTab2Change(TabChangeEvent event) {

        indexTab2 = getIndexTab(event.getTab().getId());
    }

    public void onTab3Change(TabChangeEvent event) {

        indexTab3 = getIndexTab(event.getTab().getId());
    }

    public void onTab4Change(TabChangeEvent event) {

        indexTab4 = getIndexTab(event.getTab().getId());
    }

    public void onTab5Change(TabChangeEvent event) {

        indexTab5 = getIndexTab(event.getTab().getId());
    }

    protected int getIndexTab(String id) {

        if (id.equals("t0")) {
            return 0;
        }
        if (id.equals("t1")) {
            return 1;
        }
        if (id.equals("t2")) {
            return 2;
        }
        if (id.equals("t3")) {
            return 3;
        }
        if (id.equals("t4")) {
            return 4;
        }
        if (id.equals("t5")) {
            return 5;
        }
        if (id.equals("t6")) {
            return 6;
        }
        if (id.equals("t7")) {
            return 7;
        }
        if (id.equals("t8")) {
            return 8;
        }
        if (id.equals("t9")) {
            return 9;
        }
        if (id.equals("t10")) {
            return 10;
        }
        if (id.equals("t11")) {
            return 11;
        }
        if (id.equals("t12")) {
            return 12;
        }
        if (id.equals("t13")) {
            return 13;
        }
        if (id.equals("t14")) {
            return 14;
        }
        if (id.equals("t15")) {
            return 15;
        }
        if (id.equals("t16")) {
            return 16;
        }
        if (id.equals("t17")) {
            return 17;
        }
        if (id.equals("t18")) {
            return 18;
        }
        if (id.equals("t19")) {
            return 19;
        }
        //Por defecto
        return 0;
    }

    //---------------------------------------------------------------------------
    public Map<String, String> getFiltro() {
        return filtro;
    }

    public void setFiltro(Map<String, String> filtro) {
        this.filtro = filtro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String verDatos() {

        return "Datos";
    }

    public String verLista() {

        return "Lista";
    }

    public boolean isMostrarDebaja() {
        return mostrarDebaja;
    }

    public void setMostrarDebaja(boolean mostrarDebaja) {
        this.mostrarDebaja = mostrarDebaja;
    }

    public boolean isEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public String getTxtBusqueda() {
        return txtBusqueda;
    }

    public void setTxtBusqueda(String txtBusqueda) {
        this.txtBusqueda = txtBusqueda;
    }

    public int getCantidadRegistros() {
        return cantidadRegistros;
    }

    public void setCantidadRegistros(int cantidadRegistros) {
        this.cantidadRegistros = cantidadRegistros;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Map<String, String> getFiltroGrupo() {
        return filtroGrupo;
    }

    public void setFiltroGrupo(Map<String, String> filtroGrupo) {
        this.filtroGrupo = filtroGrupo;
    }

    public Map<String, String> getFiltroDetalle() {
        return filtroDetalle;
    }

    public void setFiltroDetalle(Map<String, String> filtroDetalle) {
        this.filtroDetalle = filtroDetalle;
    }

    public boolean isBeanIniciado() {
        return beanIniciado;
    }

    public void setBeanIniciado(boolean beanIniciado) {
        this.beanIniciado = beanIniciado;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getEmailEnvioComprobante() {
        return emailEnvioComprobante;
    }

    public void setEmailEnvioComprobante(String emailEnvioComprobante) {
        this.emailEnvioComprobante = emailEnvioComprobante;
    }

    public String getInformacionAdicional() {
        return informacionAdicional;
    }

    public void setInformacionAdicional(String informacionAdicional) {
        this.informacionAdicional = informacionAdicional;
    }

    public boolean isMuestraReporte() {
        return muestraReporte;
    }

    public void setMuestraReporte(boolean muestraReporte) {
        this.muestraReporte = muestraReporte;
    }

    public boolean isSolicitaEmail() {
        return solicitaEmail;
    }

    public void setSolicitaEmail(boolean solicitaEmail) {
        this.solicitaEmail = solicitaEmail;
    }

    public boolean isEsAnulacion() {
        return esAnulacion;
    }

    public void setEsAnulacion(boolean esAnulacion) {
        this.esAnulacion = esAnulacion;
    }

    public boolean isBuscaMovimiento() {
        return buscaMovimiento;
    }

    public void setBuscaMovimiento(boolean buscaMovimiento) {
        this.buscaMovimiento = buscaMovimiento;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public Integer getNumeroFormularioDesde() {
        return numeroFormularioDesde;
    }

    public void setNumeroFormularioDesde(Integer numeroFormularioDesde) {
        this.numeroFormularioDesde = numeroFormularioDesde;
    }

    public Integer getNumeroFormularioHasta() {
        return numeroFormularioHasta;
    }

    public void setNumeroFormularioHasta(Integer numeroFormularioHasta) {
        this.numeroFormularioHasta = numeroFormularioHasta;
    }

    public Date getFechaMovimientoDesde() {
        return fechaMovimientoDesde;
    }

    public void setFechaMovimientoDesde(Date fechaMovimientoDesde) {
        this.fechaMovimientoDesde = fechaMovimientoDesde;
    }

    public Date getFechaMovimientoHasta() {
        return fechaMovimientoHasta;
    }

    public void setFechaMovimientoHasta(Date fechaMovimientoHasta) {
        this.fechaMovimientoHasta = fechaMovimientoHasta;
    }

    public boolean isSeleccionaMovimiento() {
        return seleccionaMovimiento;
    }

    public void setSeleccionaMovimiento(boolean seleccionaMovimiento) {
        this.seleccionaMovimiento = seleccionaMovimiento;
    }

    public boolean isSeleccionaTodo() {
        return seleccionaTodo;
    }

    public void setSeleccionaTodo(boolean seleccionaTodo) {
        this.seleccionaTodo = seleccionaTodo;
    }

    public boolean isSeleccionaPendiente() {
        return seleccionaPendiente;
    }

    public void setSeleccionaPendiente(boolean seleccionaPendiente) {
        this.seleccionaPendiente = seleccionaPendiente;
    }

    public int getIndexTab1() {
        return indexTab1;
    }

    public void setIndexTab1(int indexTab1) {
        this.indexTab1 = indexTab1;
    }

    public int getIndexTab2() {
        return indexTab2;
    }

    public void setIndexTab2(int indexTab2) {
        this.indexTab2 = indexTab2;
    }

    public int getIndexTab3() {
        return indexTab3;
    }

    public void setIndexTab3(int indexTab3) {
        this.indexTab3 = indexTab3;
    }

    public int getIndexTab4() {
        return indexTab4;
    }

    public void setIndexTab4(int indexTab4) {
        this.indexTab4 = indexTab4;
    }

    public int getIndexTab5() {
        return indexTab5;
    }

    public void setIndexTab5(int indexTab5) {
        this.indexTab5 = indexTab5;
    }

    public boolean isDetalleVacio() {
        return detalleVacio;
    }

    public void setDetalleVacio(boolean detalleVacio) {
        this.detalleVacio = detalleVacio;
    }

    public Date getFechaMovimientoMaxima() {
        return fechaMovimientoMaxima;
    }

    public void setFechaMovimientoMaxima(Date fechaMovimientoMaxima) {
        this.fechaMovimientoMaxima = fechaMovimientoMaxima;
    }

    public String getCodMenu() {
        return codMenu;
    }

    public void setCodMenu(String codMenu) {
        this.codMenu = codMenu;
    }

    public Vista getVista() {
        return vista;
    }

    public void setVista(Vista vista) {
        this.vista = vista;
    }

    public int getIndiceWizard() {
        return indiceWizard;
    }

    public void setIndiceWizard(int indiceWizard) {
        this.indiceWizard = indiceWizard;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public String getCodigoReporte() {
        return codigoReporte;
    }

    public void setCodigoReporte(String codigoReporte) {
        this.codigoReporte = codigoReporte;
    }

    public String getModoVista() {
        return modoVista;
    }

    public void setModoVista(String modoVista) {
        this.modoVista = modoVista;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Menu getMenuSeleccionado() {
        return menuSeleccionado;
    }

    public void setMenuSeleccionado(Menu menuSeleccionado) {
        this.menuSeleccionado = menuSeleccionado;
    }

    public boolean isMostrarSoloPendiente() {
        return mostrarSoloPendiente;
    }

    public void setMostrarSoloPendiente(boolean mostrarSoloPendiente) {
        this.mostrarSoloPendiente = mostrarSoloPendiente;
    }

    public String getAccionEnLote() {
        return accionEnLote;
    }

    public void setAccionEnLote(String accionEnLote) {
        this.accionEnLote = accionEnLote;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getCodigoVista() {
        return codigoVista;
    }

    public void setCodigoVista(String codigoVista) {
        this.codigoVista = codigoVista;
    }

    public int getNivelPermiso() {
        return nivelPermiso;
    }

    public void setNivelPermiso(int nivelPermiso) {
        this.nivelPermiso = nivelPermiso;
    }

    public void upload(FileUploadEvent event) {

        try {
            pathArchivo = copiarArchivo(event.getFile().getFileName(), event.getFile().getInputstream());
            JsfUtil.addInfoMessage("El archivo ha sido procesado con éxito");
        } catch (IOException e) {
            System.err.println("Error subiendo archivo " + e);
            JsfUtil.addErrorMessage("No es posible procesar el archivo");
        }
    }

    /**
     * Copiar un archivo a la carpeta temporales de la organizacion
     *
     * @param fileName
     * @param in
     * @return Path del archivo generado
     */
    public String copiarArchivo(String fileName, InputStream in) {
        try {

            String[] split = fileName.split("\\.");
            String extension = split[split.length - 1].toLowerCase();
            String archivo = split[0] + "." + extension;

            File file = new File(principalBean.getParametro().getPathCarpetaTemporales() + archivo);

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            System.out.println("Error copiando archivo: " + e);
            return "";
        }
    }

    public Iterator<Row> procesarArchivoExcel(FileUploadEvent event) throws IOException {

        upload(event);

        FileInputStream file = new FileInputStream(new File(pathArchivo));

        Iterator<Row> rowIterator = null;

        String[] split = pathArchivo.split("\\.");
        String extension = split[split.length - 1].toLowerCase();

        switch (extension) {
            case "xls": {
                HSSFWorkbook workbook = new HSSFWorkbook(file);
                HSSFSheet sheet = workbook.getSheetAt(0);
                rowIterator = sheet.iterator();
                break;
            }
            case "xlsx": {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);
                rowIterator = sheet.iterator();
                break;
            }
            default:
                JsfUtil.addErrorMessage("Formato de archivo incorrecto. El archivo debe tener extensión xls o xlsx");
                break;
        }

        return rowIterator;
    }

}
