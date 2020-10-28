/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.util;

import bs.administracion.modelo.CorreoElectronico;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Moneda;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.naming.NamingException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.PrimeFaces;

/**
 * @author Claudio Clase para generar informes
 */
public abstract class InformeBase extends GenericBean implements Serializable {

    @EJB
    protected MailFactory mailFactoryBean;

    protected Map parameters;

    protected boolean todoOk;
    protected boolean verLog;
    protected Integer copias;
    protected Date fecha;
    protected Date fechaDesde;
    protected Date fechaHasta;
    protected Date fechaVencimientoDesde;
    protected Date fechaVencimientoHasta;
    protected Date fechaPagoDesde;
    protected Date fechaPagoHasta;
    protected Date fechaMediaDesde;
    protected Date fechaMediaHasta;

    protected Date fechaEntregaHasta;

    protected Sucursal sucursal;
    protected UnidadNegocio unidadNegocio;
    protected Moneda monedaRegistracion;
    protected Moneda monedaVisualizacion;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @PostConstruct
    public void init() {

        super.iniciar();

        Calendar cFechaDesde = Calendar.getInstance();
        Calendar cFechaVencimientoDesde = Calendar.getInstance();
        Calendar cFechaMediaDesde = Calendar.getInstance();
        Calendar cFechaEntregaHasta = Calendar.getInstance();

        cFechaDesde.add(Calendar.MONTH, -1);
        cFechaVencimientoDesde.add(Calendar.YEAR, -5);

        cFechaMediaDesde.add(Calendar.MONTH, -5);
        cFechaMediaDesde.set(Calendar.DAY_OF_MONTH, 1);

        cFechaEntregaHasta.add(Calendar.YEAR, 10);

        copias = 1;
        fecha = new Date();
        fechaDesde = cFechaDesde.getTime();
        fechaHasta = new Date();
        fechaMovimientoDesde = cFechaDesde.getTime();
        fechaMovimientoHasta = new Date();
        fechaVencimientoDesde = cFechaVencimientoDesde.getTime();
        fechaVencimientoHasta = new Date();
        fechaPagoDesde = cFechaDesde.getTime();
        fechaPagoHasta = new Date();
        fechaMediaDesde = cFechaMediaDesde.getTime();
        fechaMediaHasta = new Date();

        fechaEntregaHasta = cFechaEntregaHasta.getTime();

        cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        monedaRegistracion = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaPrimaria());
        monedaVisualizacion = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaPrimaria());

    }

    public InformeBase() {

        muestraReporte = false;
        solicitaEmail = false;
        emailEnvioComprobante = "";
        parameters = new HashMap();

    }

    public abstract void validarDatos() throws ExcepcionGeneralSistema;

    public abstract void cargarParametros() throws ExcepcionGeneralSistema;

    public abstract void resetParametros() throws ExcepcionGeneralSistema;

    public void verReporte() {

        reporteToPdf();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    protected void reporteToPdf() {

        try {

            validarDatos();
            cargarParametros();

            reportFactory.exportReportToPdfFile(reporte, nombreArchivo, parameters);
            muestraReporte = true;
            todoOk = true;

        } catch (ExcepcionGeneralSistema e) {

            JsfUtil.addErrorMessage("No se puede ejecutar reporte pdf " + e);
            todoOk = false;
            muestraReporte = false;

        } catch (JRException e) {

            JsfUtil.addErrorMessage("No se puede ejecutar reporte pdf " + e);
            todoOk = false;
            muestraReporte = false;

        } catch (Exception e) {

            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede ejecutar reporte pdf " + e);
            todoOk = false;
            muestraReporte = false;
        }

    }

    public void reporteToXls() {
        try {
            validarDatos();
            cargarParametros();
            reportFactory.exportReportToXlsFile(reporte, nombreArchivo, parameters);

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (NamingException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (JRException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reporteToTxt() {
        try {
            validarDatos();
            cargarParametros();
            reportFactory.exportReportToTxtFile(reporte, nombreArchivo, parameters);

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (NamingException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (JRException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No se puede ejecutar reporte xls " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void reporteToEmail(CorreoElectronico ce) throws Exception {

        if (aplicacionBean.getParametro().getEnviaNotificaciones() == 'S') {

            String pathPDF = reportFactory.getPathTemporales() + nombreArchivo + ".pdf";

            if (pathPDF == null || pathPDF.isEmpty()) {
                throw new ExcepcionGeneralSistema("No existe reporte o archivo a enviar");
            }

            ce.setPathArchivo(pathPDF);

            List<CorreoElectronico> correos = new ArrayList<CorreoElectronico>();
            correos.add(ce);

            mailFactoryBean.enviarCorreosElectronicos(correos);

            JsfUtil.addInfoMessage("El envío fue exitoso. Destino: " + emailEnvioComprobante);

        } else {
            JsfUtil.addWarningMessage("No tiene permiso para enviar notificaciones");
        }

        solicitaEmail = false;
    }

    //----------------------------------------------------------------------------------
    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public boolean isTodoOk() {
        return todoOk;
    }

    public void setTodoOk(boolean todoOk) {
        this.todoOk = todoOk;
    }

    public Integer getCopias() {
        return copias;
    }

    public void setCopias(Integer copias) {
        this.copias = copias;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public Moneda getMonedaVisualizacion() {
        return monedaVisualizacion;
    }

    public void setMonedaVisualizacion(Moneda monedaVisualizacion) {
        this.monedaVisualizacion = monedaVisualizacion;
    }

    public boolean isVerLog() {
        return verLog;
    }

    public void setVerLog(boolean verLog) {
        this.verLog = verLog;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public Date getFechaVencimientoDesde() {
        return fechaVencimientoDesde;
    }

    public void setFechaVencimientoDesde(Date fechaVencimientoDesde) {
        this.fechaVencimientoDesde = fechaVencimientoDesde;
    }

    public Date getFechaVencimientoHasta() {
        return fechaVencimientoHasta;
    }

    public void setFechaVencimientoHasta(Date fechaVencimientoHasta) {
        this.fechaVencimientoHasta = fechaVencimientoHasta;
    }

    public Date getFechaPagoDesde() {
        return fechaPagoDesde;
    }

    public void setFechaPagoDesde(Date fechaPagoDesde) {
        this.fechaPagoDesde = fechaPagoDesde;
    }

    public Date getFechaPagoHasta() {
        return fechaPagoHasta;
    }

    public void setFechaPagoHasta(Date fechaPagoHasta) {
        this.fechaPagoHasta = fechaPagoHasta;
    }

    public Date getFechaMediaDesde() {
        return fechaMediaDesde;
    }

    public void setFechaMediaDesde(Date fechaMediaDesde) {
        this.fechaMediaDesde = fechaMediaDesde;
    }

    public Date getFechaMediaHasta() {
        return fechaMediaHasta;
    }

    public void setFechaMediaHasta(Date fechaMediaHasta) {
        this.fechaMediaHasta = fechaMediaHasta;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFechaEntregaHasta() {
        return fechaEntregaHasta;
    }

    public void setFechaEntregaHasta(Date fechaEntregaHasta) {
        this.fechaEntregaHasta = fechaEntregaHasta;
    }

}
