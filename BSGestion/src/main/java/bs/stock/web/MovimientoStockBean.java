/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.GenericBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.ItemProductoStock;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.MovimientoStockRN;
import bs.stock.rn.ProductoRN;
import bs.stock.web.informe.ConsultaStock;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class MovimientoStockBean extends GenericBean implements Serializable {

    @EJB
    protected MovimientoStockRN movimientoStockRN;
    @EJB
    protected MovimientoStockRN comprobanteRN;
    @EJB
    protected ProductoRN productoRN;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{consultaStock}")
    protected ConsultaStock consultaStock;

    @ManagedProperty(value = "#{depositoBean}")
    protected DepositoBean depositoBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    protected String SUCST = "";
    protected String CODST = "";

    protected MovimientoStock m;
    protected MovimientoStock mBusqueda;
    protected MovimientoStock mReversion;
    protected ItemProductoStock item;
    protected List<MovimientoStock> movimientos;

    private String codigoBarra;

    protected Date fechaMinima;

    /**
     * Creates a new instance of MovimientoInventarioBean
     */
    public MovimientoStockBean() {

        fechaMinima = new Date();
        muestraReporte = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (SUCST == null) {
                    SUCST = "";
                }
                if (CODST == null) {
                    CODST = "";
                }

                iniciarMovimiento();
                modoVista = "D";
                beanIniciado = true;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    public void iniciarMovimiento() {

        try {

            nombreArchivo = "";
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = movimientoStockRN.nuevoMovimiento(CODST, SUCST);

            esNuevo = true;
            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();

        } catch (Exception ex) {
//            ex.printStackTrace();
            JsfUtil.addErrorMessage("ERROR iniciarMovimiento" + ex);
        }
    }

    public void nuevo() {

        m = null;
        iniciarMovimiento();
        esNuevo = true;
        modoVista = "D";

    }

    public void guardar(boolean nuevo) {

        try {
            m = movimientoStockRN.guardar(m);
            JsfUtil.addInfoMessage("El documento " + m.getComprobante().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");

            if (nuevo) {
                m = movimientoStockRN.nuevoMovimiento(CODST, SUCST);
                depositoBean.setDeposito(null);
                productoBean.setProducto(null);
            }
        } catch (Exception ex) {

            JsfUtil.addErrorMessage(ex.getMessage());
        }

    }

    public void agregarItem(ItemMovimientoStock nItem) {
        try {

            movimientoStockRN.puedoAgregarItem(m, nItem);
            nItem.setTodoOk(true);

            //Cargarmos un nuevo item en blanco
            m.getItemsProducto().add(movimientoStockRN.nuevoItemProducto(m));

            productoBean.setProducto(null);

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("agregarItem" + ex);
        }
    }

    public void eliminarItem(ItemProductoStock nItem) {

        try {
            movimientoStockRN.eliminarItemProducto(m, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (nItem.getProducto() == null ? "" : nItem.getProducto().getDescripcion()) + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible borrar el item " + (nItem.getProducto() == null ? "" : nItem.getProducto().getDescripcion()) + " - " + ex);
        }
    }

    public void procesarDeposito() {

        if (depositoBean != null && m != null) {
            m.setDeposito(depositoBean.getDeposito());
        }
    }

    public void procesarDepositoTransferencia() {

        if (depositoBean != null && m != null) {
            m.setDepositoTransferencia(depositoBean.getDeposito());
        }
    }

    public void nuevoItem() {

        try {
            productoBean.setProducto(null);
            //Cargarmos un nuevo item en blanco
            m.getItemsProducto().add(movimientoStockRN.nuevoItemProducto(m));

            item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
            PrimeFaces.current().executeScript("PF('dlgProducto').show()");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void procesarProducto() {

        if (productoBean.getProducto() != null && m != null && item != null) {

            try {

                movimientoStockRN.asignarProducto(item, productoBean.getProducto());

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }
        }
    }

    /**
     *
     * @throws Exception El código de barra puede recibir diferentes datos
     * separados por espacio.
     *
     * 1 - Codigo de producto 2 - Cantidad 3 - Precio
     *
     * Ej: 0001 2 155.30
     *
     */
    public void procesarCodigoBarra() throws Exception {

        ItemProductoStock ip = null;

        try {

            if (codigoBarra == null || codigoBarra.isEmpty()) {
                return;
            }

            String[] datos = codigoBarra.split(" ");

            Producto producto = productoRN.getProductoByCodigoBarra(datos[0]);

            if (producto == null) {
                JsfUtil.addErrorMessage("No se ha encontrado el producto");
                return;
            }
            ip = movimientoStockRN.nuevoItemProducto(m);

            m.getItemsProducto().add(ip);
            movimientoStockRN.asignarProducto(ip, producto);

            if (datos.length >= 2 && datos[1] != null) {
                ip.setCantidad(new BigDecimal(datos[1]));
            } else {
                ip.setCantidad(BigDecimal.ONE);
            }

            codigoBarra = "";

            JsfUtil.addInfoMessage("Producto agregado");
        } catch (ExcepcionGeneralSistema ex) {
            movimientoStockRN.eliminarItemProducto(m, ip);
            JsfUtil.addErrorMessage("No es posible agregar el producto " + ex);
        }

    }

    public void procesarStock() {

        if (consultaStock.getItemStock() != null && m != null && item != null) {

            Stock s = consultaStock.getItemStock();

            item.setAtributo1(s.getAtributo1());
            item.setAtributo2(s.getAtributo2());
            item.setAtributo3(s.getAtributo3());
            item.setAtributo4(s.getAtributo4());
            item.setAtributo5(s.getAtributo5());
            item.setAtributo6(s.getAtributo6());
            item.setAtributo7(s.getAtributo7());
        }
    }

    public void resetParametros() {

        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }

        cargarFiltroBusqueda();

        movimientos = movimientoStockRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }

        modoVista = "B";
    }

    public boolean validarParametros() {

        if (comprobante == null) {
            JsfUtil.addWarningMessage("Comprobante vacío no es posible buscar");
            return false;
        }

        if (puntoVenta == null) {
            JsfUtil.addWarningMessage("Punto de venta vacío no es posible buscar");
            return false;
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
        String sFiltroSuc = usuarioSessionBean.getStringInSucursal();

        if (sFiltroSuc != null && !sFiltroSuc.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltroSuc + ")");
        }

        if (comprobante != null) {

            filtro.put("comprobante.modulo = ", "'" + comprobante.getModulo() + "'");
            filtro.put("comprobante.codigo = ", "'" + comprobante.getCodigo() + "'");
        }

        if (puntoVenta != null) {

            filtro.put("puntoVenta.codigo = ", "'" + puntoVenta.getCodigo() + "'");
        }

        filtro.put("movimientoReversion", " IS NULL");

        if (numeroFormularioDesde != null) {
            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {
            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {
            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {
            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    public void revertirMovimiento(MovimientoStock mSel) {
        try {

            mReversion = mSel;
            m = movimientoStockRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            buscar();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiarFiltroBusqueda() {

        mBusqueda = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        buscar();
    }

    public void seleccionarMovimiento(MovimientoStock mSel) {

        m = mSel;
        modoVista = "D";
    }

    public void procesarMascaraStock() throws ExcepcionGeneralSistema {

        if (m != null && m.getMascara() != null) {
            movimientoStockRN.asignarMascaraStock(m, m.getMascara());
        }
    }

    public void preparoBusquedaProductos() {

        if (m != null && m.getItemsProducto() != null && !m.getItemsProducto().isEmpty()) {

            productoBean.getProductosSeleccionados().clear();

            m.getItemsProducto().forEach(i -> {

                productoBean.getProductosSeleccionados().add(i.getProducto());

            });
        }
    }

    public void procesarProductos() {

        if (productoBean.getProductosSeleccionados() != null && m != null) {

            try {

                for (Producto p : productoBean.getProductosSeleccionados()) {

                    if (m.getItemsProducto().isEmpty()) {

                        item = movimientoStockRN.nuevoItemProducto(m);
                        movimientoStockRN.asignarProducto(item, p);

                        m.getItemsProducto().add(item);
                    }

                    if (m.getItemsProducto()
                            .stream()
                            .filter(i -> i.getProducto() != null && i.getProducto().equals(p)).count() == 0) {

                        item = movimientoStockRN.nuevoItemProducto(m);
                        movimientoStockRN.asignarProducto(item, p);
                        m.getItemsProducto().add(item);
                    }
                }

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible procesar los productos " + ex);
            }
        }
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoStock movimiento) {

        m = movimiento;
        imprimir();
    }

    public void generarReporte() {

        try {
            Map parameters = new HashMap();

            if (m == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - No existe movimiento seleccionado");
            }

            if (m.getFormulario().getReporte() == null) {
                throw new ExcepcionGeneralSistema("El comprobante no tienen reporte asociado");
            }

            if (m.getPuntoVenta().getUnidadNegocio().getLogo() != null && !m.getPuntoVenta().getUnidadNegocio().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + m.getPuntoVenta().getUnidadNegocio().getLogo()));
            }

            parameters.put("ID", m.getId());
            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (JRException e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        }
    }

    //-------------------------------------------------------------------------
    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public MovimientoStock getM() {
        return m;
    }

    public void setM(MovimientoStock m) {
        this.m = m;
    }

    public Date getFechaMinima() {
        return fechaMinima;
    }

    public void setFechaMinima(Date fechaMinima) {
        this.fechaMinima = fechaMinima;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public DepositoBean getDepositoBean() {
        return depositoBean;
    }

    public void setDepositoBean(DepositoBean depositoBean) {
        this.depositoBean = depositoBean;
    }

    public String getSUCST() {
        return SUCST;
    }

    public void setSUCST(String SUCST) {
        this.SUCST = SUCST;
    }

    public String getCODST() {
        return CODST;
    }

    public void setCODST(String CODST) {
        this.CODST = CODST;
    }

    public List<MovimientoStock> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoStock> movimientos) {
        this.movimientos = movimientos;
    }

    public boolean isDetalleVacio() {

        detalleVacio = true;

        if (m == null) {
            return detalleVacio;
        }

        if (m.getItemsProducto() != null) {

            if (m.getItemsProducto().size() == 1 && m.getId() != null) {
                detalleVacio = false;
            }

            if (m.getItemsProducto().size() > 1) {
                detalleVacio = false;
            }
        } else {
            detalleVacio = true;
        }

        return detalleVacio;
    }

    @Override
    public void setDetalleVacio(boolean detalleVacio) {
        this.detalleVacio = detalleVacio;
    }

    public ConsultaStock getConsultaStock() {
        return consultaStock;
    }

    public void setConsultaStock(ConsultaStock consultaStock) {
        this.consultaStock = consultaStock;
    }

    public ItemProductoStock getItem() {
        return item;
    }

    public void setItem(ItemProductoStock item) {
        this.item = item;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public MovimientoStock getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoStock mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public MovimientoStock getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoStock mReversion) {
        this.mReversion = mReversion;
    }

}
