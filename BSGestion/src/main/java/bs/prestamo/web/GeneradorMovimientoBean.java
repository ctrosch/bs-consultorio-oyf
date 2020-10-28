/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.DireccionEntregaBean;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.global.web.MailFactory;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.rn.MovimientoPrestamoRN;
import bs.prestamo.rn.PrestamoRN;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class GeneradorMovimientoBean extends GenericBean implements Serializable {

    protected @EJB
    MovimientoPrestamoRN movimientoRN;
    protected @EJB
    PrestamoRN prestamoRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;

    @EJB
    private MailFactory mailFactoryBean;

    protected String SUCPR = "";
    protected String CODPR = "";
    protected String SUCCJ = "";
    protected String CODCJ = "";

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial destinatario;
    protected List<MovimientoPrestamo> movimientos;
    protected List<Prestamo> prestamos;
    //--------------------------------------------------

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    protected MovimientoPrestamo m;
    protected MovimientoPrestamo mReversion;
    protected MovimientoPrestamo mBusqueda;

    /**
     * Creates a new instance of MovimientoPrestamoBean
     */
    public GeneradorMovimientoBean() {

        titulo = "Movimiento de Prestamo";
        muestraReporte = false;
    }

    public void generarLiquidacionesPrestamo() {

        try {

            filtro.clear();
            filtro.put("estado.codigo = ", "'C'");
            filtro.put("id = ", "60016");

            List<Prestamo> pendientes = prestamoRN.getListaByBusqueda("", filtro, 0);

            if (pendientes != null) {

                for (Prestamo p : pendientes) {

                    m = movimientoRN.nuevoMovimiento("SIP100", "0001", "", "");
                    movimientoRN.asignarPrestamo(m, p);
                    m = movimientoRN.guardar(m);

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GeneradorMovimientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Prestamo> completePrestamo(String query) {
        try {
            filtro.clear();
            filtro.put("estado.codigo = ", "'B'");

            prestamos = prestamoRN.getListaByBusqueda(query, filtro, 10);
            return prestamos;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Prestamo>();
        }
    }

    //------------------------------------------------------------------------------------------------------------
    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public String getSUCPR() {
        return SUCPR;
    }

    public void setSUCPR(String SUCPR) {
        this.SUCPR = SUCPR;
    }

    public String getCODPR() {
        return CODPR;
    }

    public void setCODPR(String CODPR) {
        this.CODPR = CODPR;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public DireccionEntregaBean getDireccionEntregaBean() {
        return direccionEntregaBean;
    }

    public void setDireccionEntregaBean(DireccionEntregaBean direccionEntregaBean) {
        this.direccionEntregaBean = direccionEntregaBean;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public List<MovimientoPrestamo> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoPrestamo> movimientos) {
        this.movimientos = movimientos;
    }

    public MovimientoPrestamo getM() {
        return m;
    }

    public void setM(MovimientoPrestamo m) {
        this.m = m;
    }

    public MovimientoPrestamo getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoPrestamo mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoPrestamo getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoPrestamo mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public String getSUCCJ() {
        return SUCCJ;
    }

    public void setSUCCJ(String SUCCJ) {
        this.SUCCJ = SUCCJ;
    }

    public String getCODCJ() {
        return CODCJ;
    }

    public void setCODCJ(String CODCJ) {
        this.CODCJ = CODCJ;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

}
