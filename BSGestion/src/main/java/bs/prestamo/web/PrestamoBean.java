/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.modelo.UnidadNegocio;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.global.web.MailFactory;
import bs.prestamo.modelo.EstadoPrestamo;
import bs.prestamo.modelo.Financiador;
import bs.prestamo.modelo.ItemPrestamo;
import bs.prestamo.modelo.LineaCredito;
import bs.prestamo.modelo.ParametroPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.modelo.Promotor;
import bs.prestamo.rn.NotificacionesPrestamoRN;
import bs.prestamo.rn.ParametroPrestamoRN;
import bs.prestamo.rn.PrestamoRN;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class PrestamoBean extends GenericBean implements Serializable {

    protected @EJB
    ParametroPrestamoRN parametroRN;
    protected @EJB
    PrestamoRN prestamoRN;
    protected @EJB
    NotificacionesPrestamoRN notificacionesPrestamoRN;
    protected @EJB
    EntidadRN entidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    @EJB
    private MailFactory mailFactoryBean;

    //Datos inicializacion registracion
    protected String NROCTA = "";

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial destinatario;
    protected EstadoPrestamo estado;
    protected LineaCredito lineaCredito;
    protected Financiador financiador;
    protected Promotor promotor;
    protected ParametroPrestamo parametro;
    protected UnidadNegocio unidadNegocio;

    protected Prestamo prestamo;
    protected List<Prestamo> lista;
    private Integer ID;
    protected boolean mantieneCotizacionOriginal;

    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    public PrestamoBean() {

        titulo = "Préstamos";
        muestraReporte = false;
        numeroFormularioDesde = 1;
        mantieneCotizacionOriginal = false;

    }

    @PostConstruct
    public void init() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                parametro = parametroRN.getParametro();

                if (ID != null && ID > 0) {
                    seleccionar(prestamoRN.getPrestamo(ID));
                } else {
                    buscar();
                    buscaMovimiento = true;
                }

                destinatario = null;
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        try {
            prestamo = prestamoRN.nuevo();

            if (NROCTA != null && !NROCTA.isEmpty()) {

                EntidadComercial destinatario = entidadRN.getEntidad(NROCTA);

                if (destinatario != null) {
                    prestamo.setDestinatario(destinatario);
                    procesarDestinatario();
                }
            }

            if (usuarioSessionBean.getUsuario().getPromotor() != null) {
                prestamo.setPromotor(usuarioSessionBean.getUsuario().getPromotor());
                prestamo.getAuditoria().setUsuario(usuarioSessionBean.getUsuario().getUsuario());
            }

            buscaMovimiento = false;
            esNuevo = true;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible generar un nuevo movimiento " + ex);
            Logger.getLogger(PrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void guardar(boolean nuevo) {

        try {
            prestamo = prestamoRN.guardar(prestamo);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

            JsfUtil.addInfoMessage("El préstamo " + prestamo.getCodigo() + " para " + prestamo.getNombreDestinatario() + " se guardó correctamente", "");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void cambiarEstado(String estado) {

        try {
            prestamoRN.cambiarEstado(prestamo, estado);
            JsfUtil.addInfoMessage("El estado del préstamo ha cambiado satisfactoriamente");
        } catch (Exception ex) {
            Logger.getLogger(PrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Problemas para cambiar de estado " + ex);
        }

    }

    public void asignarCodigo() {

        try {
            if (esNuevo || prestamo.getEstado().getCodigo().equals("A")) {
                prestamoRN.asignarCodigo(prestamo);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código de préstamo " + ex);
        }
    }

    public void limpiarCodigo() {

        if (esNuevo) {
            prestamo.setCodigo("");
        }
    }

    public void eliminarItem(ItemPrestamo nItem) {

        try {
            prestamoRN.eliminarItemPrestamo(prestamo, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item ");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public void seleccionar(Prestamo m) {

        if (m == null) {
            return;
        }

        this.prestamo = m;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void onSelect(SelectEvent event) {
        prestamo = (Prestamo) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void procesarDestinatario() {

        if (prestamo != null && prestamo.getDestinatario() != null) {

            try {
                prestamoRN.asignarDestinatario(prestamo, prestamo.getDestinatario());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar destinatario " + ex);
            }
        }
    }

//    public void calcularFechaPrimerVencimiento() {
//
//        try {
//            Calendar fechaAuxiliar = Calendar.getInstance();
//            fechaAuxiliar.setTime(prestamo.getFechaVencimientoPrimeraCuota());
//            prestamo.setFechaVencimientoPrimeraCuota(prestamoRN.calcularProximoVencimiento(prestamo, fechaAuxiliar));
//            prestamoRN.generarCuota(prestamo);
//            prestamoRN.asignarCodigo(prestamo);
//        } catch (Exception ex) {
//            JsfUtil.addErrorMessage("Problemas para generar cuotas " + ex);
//        }
//    }
    public void generarCuotas() {

        try {
            prestamoRN.generarCuota(prestamo);
            prestamoRN.asignarCodigo(prestamo);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar cuotas " + ex);
        }
    }

    public void calcularImportes() {

//        prestamoRN.calcularImportes(prestamo, "U", false);
    }

    public void calcularImportesConImpuesto() {

//        prestamoRN.calcularImportes(prestamo, "U", true);
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void generarReporte() {

        try {

            reporte = reporteRN.getReporte(codigoReporte);

            Map parameters = new HashMap();

            if (prestamo.getSucursal().getLogo() != null && !prestamo.getSucursal().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + prestamo.getSucursal().getLogo()));
            }

            parameters.put("ID", prestamo.getId());

            nombreArchivo = "PRESTAMO_" + prestamo.getCodigo();
            muestraReporte = true;

            reportFactory.exportReportToPdfFile(reporte, nombreArchivo, parameters);

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + "PRESTAMO_" + prestamo.getCodigo());
            muestraReporte = false;

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + ex);
            muestraReporte = false;
        }
    }

    public void nuevaBusqueda() {

        buscar();
        buscaMovimiento = true;
    }

    public void resetParametros() {

        destinatario = null;
        estado = null;
        financiador = null;
        lineaCredito = null;
        promotor = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        lista = null;
        indiceWizard = 0;
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        lista = prestamoRN.getListaByBusqueda("", filtro, cantidadRegistros);

        if (lista == null || lista.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos solo préstamos por unidad de negocio a la que pertenece el usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

        if (destinatario != null) {

            filtro.put("destinatario.nrocta =", "'" + destinatario.getNrocta() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaEntrega >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaEntrega <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }

        if (estado != null) {
            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }

        if (financiador != null) {
            filtro.put("financiador.id = ", "'" + financiador.getId() + "'");
        }

        if (lineaCredito != null) {
            filtro.put("lineaCredito.id = ", "'" + lineaCredito.getId() + "'");
        }

        if (promotor != null) {
            filtro.put("promotor.id = ", "'" + promotor.getId() + "'");
        }
    }

    public boolean validarParametros() {

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public List<Prestamo> complete(String query) {
        try {
            cargarFiltroBusqueda();
            return prestamoRN.getListaByBusqueda(query, filtro, 15);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Prestamo>();
        }
    }

    public List<EntidadComercial> completeDestinatario(String query) {
        try {

            filtro.clear();
            String sFiltro = usuarioSessionBean.getStringInSucursal();

            if (sFiltro != null && !sFiltro.isEmpty()) {
                filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
            }

            TipoEntidad tipo = tipoEntidadRN.getTipoEntidad("3");

            return entidadRN.getListaByBusqueda(filtro, query, tipo, mostrarDebaja, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EntidadComercial>();
        }
    }

    //------------------------------------------------------------------------------------------------------------
    public PrestamoRN getPrestamoRN() {
        return prestamoRN;
    }

    public void setPrestamoRN(PrestamoRN prestamoRN) {
        this.prestamoRN = prestamoRN;
    }

    public NotificacionesPrestamoRN getNotificacionesPrestamoRN() {
        return notificacionesPrestamoRN;
    }

    public void setNotificacionesPrestamoRN(NotificacionesPrestamoRN notificacionesPrestamoRN) {
        this.notificacionesPrestamoRN = notificacionesPrestamoRN;
    }

    public EntidadRN getEntidadRN() {
        return entidadRN;
    }

    public void setEntidadRN(EntidadRN entidadRN) {
        this.entidadRN = entidadRN;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public String getNROCTA() {
        return NROCTA;
    }

    public void setNROCTA(String NROCTA) {
        this.NROCTA = NROCTA;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public List<Prestamo> getLista() {
        return lista;
    }

    public void setLista(List<Prestamo> lista) {
        this.lista = lista;
    }

    public boolean isMantieneCotizacionOriginal() {
        return mantieneCotizacionOriginal;
    }

    public void setMantieneCotizacionOriginal(boolean mantieneCotizacionOriginal) {
        this.mantieneCotizacionOriginal = mantieneCotizacionOriginal;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public LineaCredito getLineaCredito() {
        return lineaCredito;
    }

    public void setLineaCredito(LineaCredito lineaCredito) {
        this.lineaCredito = lineaCredito;
    }

    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador financiador) {
        this.financiador = financiador;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public void setPromotor(Promotor promotor) {
        this.promotor = promotor;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public ParametroPrestamo getParametro() {
        return parametro;
    }

    public void setParametro(ParametroPrestamo parametro) {
        this.parametro = parametro;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

}
