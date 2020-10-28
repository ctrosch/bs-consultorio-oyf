/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.bar.dao.MovimientoBarDAO;
import bs.bar.modelo.ItemMovimientoBar;
import bs.bar.modelo.MovimientoBar;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.rn.SucursalRN;
import bs.stock.modelo.Producto;
import bs.ventas.rn.ListaPrecioVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoBarRN implements Serializable {

    @EJB
    private SucursalRN SucursalRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private MovimientoBarDAO movimientoDAO;
    @EJB
    private ComprobanteRN comprobanteDAO;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;
    @EJB
    private EstadoBarRN estadoRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    protected ModuloRN moduloRN;
    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    PuntoVentaRN puntoVentaRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoBar guardar(MovimientoBar m) throws Exception {

        preSave(m);
        calcularImportes(m);
        controlComprobante(m);

        if (m.getId() == null) {
//            generarMovimientosAdicionales(m);
            tomarNumeroFormulario(m);
            movimientoDAO.crear(m);
        } else {
            m = (MovimientoBar) movimientoDAO.editar(m);
        }

        return m;
    }

    public void preSave(MovimientoBar m) {

    }

    public void controlComprobante(MovimientoBar m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("BR", m.getFechaMovimiento());

        if (m.getEstado() == null) {
            sErrores += "- El estado no puede estar vacío \n";
        }

        if (m.getSalon() == null) {
            sErrores += "- El salón no puede estar vacío \n";
        }
        if (m.getMesa() == null) {
            sErrores += "- La mesa no puede estar vacía \n";
        }
        if (m.getMozo() == null) {
            sErrores += "- El mozo no puede estar vacío \n";
        }

        if (m.getEstado() != null
                && ("C,D,Z").contains(m.getEstado().getCodigo())
                && (m.getItems() == null || m.getItems().isEmpty())) {
            sErrores += "- El detalle está vacío, no es posible guardar el comprobante \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

    }

    public MovimientoBar nuevoMovimiento(String codCG, String sucCG, Date fechaMovimiento) throws ExcepcionGeneralSistema {

        Comprobante comprobante = comprobanteRN.getComprobante("BR", codCG);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucCG);
        Parametro p = parametrosRN.getParametro();
        BigDecimal cotizacion = monedaRN.getCotizacionDia(p.getCodigoMonedaSecundaria());
        Moneda monedaSec = monedaRN.getMoneda(p.getCodigoMonedaSecundaria());

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de contabilidad " + codCG);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + sucCG);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de contabilidad para el comprobante (" + codCG + ") "
                    + "para El punto de venta (" + sucCG + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoBar m = new MovimientoBar();

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);

        m.setComprobante(comprobante);
        m.setFechaMovimiento(fechaMovimiento);
        m.setMonedaSecundaria(monedaSec);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);
        //Hay que cambiar y que tome de los parámetros 11/09/2020
        m.setListaDePrecio(listaPrecioRN.getListaPrecio("A"));

        asignarFormulario(m);

        return m;
    }

    public void reorganizarNroItem(MovimientoBar m) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoBar ip : m.getItems()) {
            ip.setNroitm(i);
            i++;
        }
    }

    public void asignarProducto(ItemMovimientoBar i, Producto producto) throws ExcepcionGeneralSistema {

        String sErrores = "";

//        if (i.getMovimiento().getListaDePrecio() == null) {
//            sErrores += "- El comprobante no tiene una lista de precio asignada \n";
//        }
        if (i.getMovimiento().getMonedaSecundaria() == null) {
            sErrores += "- El comprobante no tiene una moneda secundaria asignada \n";
        }

        if (producto.getCuentaContableVenta() == null) {
            sErrores += "- El producto no tiene una cuenta contable de venta asignada \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        i.setProducto(producto);
        i.setDescripcion(producto.getDescripcion());
        i.setUnidadMedida(producto.getUnidadDeMedida());
        i.setConcepto(producto.getConceptoVenta());

        asignarPrecioItem(i);
        //calcularImportes(i.getMovimiento(), i.getMovimiento().getListaDePrecio().getIncluyeImpuestos().equals("S"));

    }

    private void asignarPrecioItem(ItemMovimientoBar itemProducto) {

        if (itemProducto.getProducto() == null) {
            return;
        }

        String monedaReposicion = itemProducto.getProducto().getMonedaReposicion().getCodigo();
        BigDecimal cotizacion = itemProducto.getMovimiento().getCotizacion();
        BigDecimal utilidadAdicional = BigDecimal.ZERO;

//        if (itemProducto.getMovimiento().getComprobanteVenta().getCalculaUtilidadAdicional().equals("S")) {
//            utilidadAdicional = obtenerImpuestoByConcepto(itemProducto.getConcepto());
//        }
        if (itemProducto.getMovimiento().getListaDePrecio().getIncluyeImpuestos().equals("S")) {
            itemProducto.setPrecioConImpuesto(listaPrecioRN.getPrecioByProducto(itemProducto.getMovimiento().getListaDePrecio(), itemProducto.getProducto(), cotizacion, utilidadAdicional));
        } else {
            itemProducto.setPrecio(listaPrecioRN.getPrecioByProducto(itemProducto.getMovimiento().getListaDePrecio(), itemProducto.getProducto(), cotizacion, utilidadAdicional).doubleValue());
//            itemProducto.setPrecioSecundario(itemProducto.getPrecio().divide(cotizacion, 2, RoundingMode.HALF_UP));
        }

    }

    public void asignarFormulario(MovimientoBar m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    public void calcularImportes(MovimientoBar m) {

    }

    public void cerrarMesa(MovimientoBar m) throws Exception {

        int nroFor = formularioRN.tomarProximoNumero(m.getFormulario());
        m.setNumeroFormulario(nroFor);
        m.setEstado(estadoRN.getEstadoBar("Z"));
        guardar(m);
    }

    public ItemMovimientoBar nuevoItemMovimiento(MovimientoBar movimiento) throws ExcepcionGeneralSistema {

        //puedoAgregarItem(movimiento);
        ItemMovimientoBar nItem = new ItemMovimientoBar();

        nItem.setNroitm(movimiento.getItems().size() + 1);

        nItem.setMovimiento(movimiento);
        reorganizarNroItem(movimiento);

        return nItem;
    }

    public boolean eliminarItem(MovimientoBar m, ItemMovimientoBar nItem) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item vacío, nada para eliminar");
        }

        if (m.getId() != null && !m.getFormulario().getModfor().equals("BR")) {
            throw new ExcepcionGeneralSistema("Solo se puede eliminar items de comprobantes de bar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        int indiceItemAplicacion = -1;
        int indiceItemAnulacion = -1;

        for (ItemMovimientoBar ip : m.getItems()) {

            if (ip.getNroitm() >= 0) {

                if (ip.getNroitm() == nItem.getNroitm()) {
                    indiceItemProducto = i;
                }
            }
            i++;
        }

        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemMovimientoBar remove = m.getItems().remove(indiceItemProducto);
            if (remove != null) {
                if (m.getId() != null && remove.getId() != null) {
                    movimientoDAO.eliminar(ItemMovimientoBar.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(m);
        calcularImportes(m);

        return fItemBorrado;
    }

    public List<MovimientoBar> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return movimientoDAO.getListaByBusqueda(filtro, true, 0);
    }

    public List<MovimientoBar> getListaPendientes() {

        Map<String, String> filtro = movimientoDAO.getFiltro();

        filtro.put("estado.codigo IN", "('A','B','C','D')");

        return movimientoDAO.getListaByBusqueda(filtro, false, 0);
    }

    private void tomarNumeroFormulario(MovimientoBar m) throws ExcepcionGeneralSistema, Exception {

        if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        formularioRN.guardar(m.getFormulario());

    }

    private void generarMovimientosAdicionales(MovimientoBar m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
