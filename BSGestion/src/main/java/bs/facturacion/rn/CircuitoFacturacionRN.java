/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.rn;

import bs.facturacion.dao.CircuitoFacturacionDAO;
import bs.facturacion.modelo.CircuitoFacturacion;
import bs.facturacion.modelo.CircuitoFacturacionPK;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.facturacion.modelo.ItemCircuito;
import bs.facturacion.modelo.ItemCircuitoFacturacion;
import bs.facturacion.modelo.ItemCircuitoPK;
import bs.facturacion.modelo.ItemCircuitoStock;
import bs.facturacion.modelo.ItemCircuitoTesoreria;
import bs.facturacion.modelo.ItemCircuitoVenta;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import java.io.Serializable;
import java.util.ArrayList;
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
public class CircuitoFacturacionRN implements Serializable {

    @EJB
    private CircuitoFacturacionDAO circuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CircuitoFacturacion guardar(CircuitoFacturacion c, boolean esNuevo) throws Exception {

        asignarValores(c);
        control(c);

        if (esNuevo) {

            CircuitoFacturacionPK idPK = new CircuitoFacturacionPK(c.getCircom(), c.getCirapl());

            if (circuitoDAO.getObjeto(CircuitoFacturacion.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un circuito con el código " + idPK);
            }
            circuitoDAO.crear(c);

        } else {
            c = (CircuitoFacturacion) circuitoDAO.editar(c);
        }

        return c;
    }

    public void asignarValores(CircuitoFacturacion c) {

        if (c.getCircom() == null || c.getCircom().isEmpty() && c.getCircuitoComienzo() != null) {
            c.setCircom(c.getCircuitoComienzo().getCodigo());
        }

        if (c.getCirapl() == null || c.getCirapl().isEmpty() && c.getCircuitoAplicacion() != null) {
            c.setCirapl(c.getCircuitoAplicacion().getCodigo());
        }

        if (c.getItemCircuitoFacturacion() != null) {
            for (ItemCircuitoFacturacion i : c.getItemCircuitoFacturacion()) {
                if (i.getComprobante() != null) {
                    if (i.getModulo() == null || i.getModulo().isEmpty()) {
                        i.setModulo(i.getComprobante().getModulo());
                    }

                    if (i.getCodcom() == null || i.getCodcom().isEmpty()) {
                        i.setCodcom(i.getComprobante().getCodigo());
                    }
                }
            }
        }

        if (c.getItemCircuitoVenta() != null) {
            for (ItemCircuitoVenta i : c.getItemCircuitoVenta()) {
                if (i.getComprobante() != null) {
                    if (i.getModulo() == null || i.getModulo().isEmpty()) {
                        i.setModulo(i.getComprobante().getModulo());
                    }

                    if (i.getCodcom() == null || i.getCodcom().isEmpty()) {
                        i.setCodcom(i.getComprobante().getCodigo());
                    }
                }
            }
        }

        if (c.getItemCircuitoStock() != null) {
            for (ItemCircuitoStock i : c.getItemCircuitoStock()) {
                if (i.getComprobante() != null) {
                    if (i.getModulo() == null || i.getModulo().isEmpty()) {
                        i.setModulo(i.getComprobante().getModulo());
                    }

                    if (i.getCodcom() == null || i.getCodcom().isEmpty()) {
                        i.setCodcom(i.getComprobante().getCodigo());
                    }
                }
            }
        }

        if (c.getItemCircuitoTesoreria() != null) {
            for (ItemCircuitoTesoreria i : c.getItemCircuitoTesoreria()) {
                if (i.getComprobante() != null) {
                    if (i.getModulo() == null || i.getModulo().isEmpty()) {
                        i.setModulo(i.getComprobante().getModulo());
                    }

                    if (i.getCodcom() == null || i.getCodcom().isEmpty()) {
                        i.setCodcom(i.getComprobante().getCodigo());
                    }
                }
            }
        }

    }

    public void control(CircuitoFacturacion c) throws ExcepcionGeneralSistema {
        String sError = "";

        if (c.getCircom() == null || c.getCircom().isEmpty()) {
            sError += "El Circuito de Inicio es obligatorio | ";
        }

        if (c.getCirapl() == null || c.getCirapl().isEmpty()) {
            sError += "El Circuito Aplicado es obligatorio | ";
        }

        if (c.getDescripcion() == null || c.getDescripcion().isEmpty()) {
            sError += "La Descripción es obligatoria | ";
        }

        if (!c.getCircom().equals(c.getCirapl())) {
            if (c.getReporteGrupo() == null) {
                sError += "El Reporte grupo es obligatorio | ";
            }

            if (c.getReporteDetalle() == null) {
                sError += "El Reporte detalle es obligatorio | ";
            }

        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public CircuitoFacturacion getCircuito(CircuitoFacturacionPK idPK) {
        return circuitoDAO.getCircuito(idPK);
    }

    public CircuitoFacturacion getCircuito(String circom, String cirapl) {

        CircuitoFacturacionPK idPK = new CircuitoFacturacionPK(circom, cirapl);
        return circuitoDAO.getCircuito(idPK);
    }

    public List<CircuitoFacturacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoFacturacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoFacturacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public Comprobante getComprobanteFacturacion(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

//        System.out.println(circuitoDAO.getComprobanteProduccion(circom, cirapl ,modcom,codcom));
        Comprobante cf = circuitoDAO.getComprobanteFacturacion(circom, cirapl, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de facturacion (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cf;
    }

    public Comprobante getComprobanteStock(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cs = circuitoDAO.getComprobanteStock(circom, cirapl, codcom);
        if (cs == null) {
            throw new ExcepcionGeneralSistema("El comprobante de stock (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cs;
    }

    public Comprobante getComprobanteVenta(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cv = circuitoDAO.getComprobanteVenta(circom, cirapl, codcom);
        if (cv == null) {
            throw new ExcepcionGeneralSistema("El comprobante de venta (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cv;
    }

    public Comprobante getComprobanteTesoreria(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cj = circuitoDAO.getComprobanteTesoreria(circom, cirapl, codcom);
        if (cj == null) {
            throw new ExcepcionGeneralSistema("El comprobante de tesoreria (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cj;
    }

    public CodigoCircuitoFacturacion getCodigoCircuito(String codigo) {

        return circuitoDAO.getCodigoCircuito(codigo);
    }

    public List<CodigoCircuitoFacturacion> getListaCodigoCircuito() {

        return circuitoDAO.getListaCodigoCircuito();
    }

    public void eliminar(CircuitoFacturacion circuito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cargarCircuitosRelacionados(CircuitoFacturacion circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", false));
    }

    public CircuitoFacturacion cargarCircuitosRelacionadoss(CircuitoFacturacion circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", false));

        return circuito;
    }

    public CircuitoFacturacion iniciarCircuito(String circom, String cirapl,
            String codFC, String codVT, String codST, String codCJ) throws ExcepcionGeneralSistema {

        CircuitoFacturacion circuito = null;

        if (circom != null && cirapl != null) {
            circuito = getCircuito(circom, cirapl);
        }

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No se encontró circuito " + circom + "-" + cirapl);
        }

        if (circuito.getAuditoria().getDebaja().equals("S")) {
            throw new ExcepcionGeneralSistema("El circuito " + circom + "-" + cirapl + " se encuentra deshabilitado");
        }

        cargarCircuitosRelacionados(circuito);

        if (circuito.getActualizaFacturacion().equals("S") && codFC != null) {

            circuito.setComprobanteFacturacion(getComprobanteFacturacion(circom, cirapl, codFC));

        } else if (circuito.getActualizaVenta().equals("S") && codVT != null) {

            circuito.setComprobanteVenta(getComprobanteVenta(circom, cirapl, codVT));

            if (circuito.getActualizaTesoreria().equals("S") && codCJ != null) {
                circuito.setComprobanteTesoreria(getComprobanteTesoreria(circom, cirapl, codCJ));
            }

            if (circuito.getActualizaStock().equals("S") && codST != null) {
                circuito.setComprobanteStock(getComprobanteStock(circom, cirapl, codST));
            }

        } else if (circuito.getActualizaStock().equals("S") && codST != null) {

            circuito.setComprobanteStock(getComprobanteStock(circom, cirapl, codST));
        }

        //Siempre al menos 1 comprobante de venta tiene que haber configurado.
        if (circuito.getItemCircuitoVenta() == null || circuito.getItemCircuitoVenta().isEmpty()) {
            throw new ExcepcionGeneralSistema("El circuito debe tener configurado al menos un comprobante de venta");
        } else if (circuito.getComprobanteVenta() == null) {
            circuito.setComprobanteVenta(circuito.getItemCircuitoVenta().get(0).getComprobante());
        }

        if (!circuito.getCircom().equals(circuito.getCirapl())) {

            if (circuito.getReporteGrupo() == null) {
                throw new ExcepcionGeneralSistema("El circuito debe tener configurado el reporte de grupo");
            }

            if (circuito.getReporteDetalle() == null) {
                throw new ExcepcionGeneralSistema("El circuito debe tener configurado el reporte de detalle");
            }
        }

        return circuito;
    }

    public CircuitoFacturacion iniciarCircuito(String circom, String cirapl, String codFC) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codFC, null, null);
    }

    public CircuitoFacturacion iniciarCircuito(String circom, String cirapl, String codFC, String codVT) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codFC, codVT, null, null);
    }

    public CircuitoFacturacion iniciarCircuito(String circom, String cirapl, String codFC, String codVT, String codST) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codFC, codVT, codST, null);
    }

    public void asignarComprobanteItemCircuitoFacturacion(ItemCircuito itemCircuito, Comprobante comprobante) throws ExcepcionGeneralSistema {

        itemCircuito.setModulo(comprobante.getModulo());
        itemCircuito.setCodcom(comprobante.getCodigo());

    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoFacturacion(CircuitoFacturacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoFacturacion() == null) {
            circuito.setItemCircuitoFacturacion(new ArrayList<ItemCircuitoFacturacion>());
        }

        ItemCircuitoFacturacion itemCircuito = new ItemCircuitoFacturacion();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoFacturacion().add(itemCircuito);

    }

    public void eliminarItemCircuitoFacturacion(CircuitoFacturacion circuito, ItemCircuitoFacturacion itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoFacturacion() != null) {

            if (circuito.getItemCircuitoFacturacion().remove(itemCircuito)) {

                ItemCircuitoPK itemCircuitoFacturacionPK = new ItemCircuitoPK();
                itemCircuitoFacturacionPK.setModulo(itemCircuito.getModulo());
                itemCircuitoFacturacionPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoFacturacionPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoFacturacionPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoFacturacion.class, itemCircuitoFacturacionPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoVenta(CircuitoFacturacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoVenta() == null) {
            circuito.setItemCircuitoVenta(new ArrayList<ItemCircuitoVenta>());
        }

        ItemCircuitoVenta itemCircuito = new ItemCircuitoVenta();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoVenta().add(itemCircuito);

    }

    public void eliminarItemCircuitoVenta(CircuitoFacturacion circuito, ItemCircuitoVenta itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoVenta() != null) {

            if (circuito.getItemCircuitoVenta().remove(itemCircuito)) {

                ItemCircuitoPK itemCircuitoVentaPK = new ItemCircuitoPK();
                itemCircuitoVentaPK.setModulo(itemCircuito.getModulo());
                itemCircuitoVentaPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoVentaPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoVentaPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoVenta.class, itemCircuitoVentaPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoStock(CircuitoFacturacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoStock() == null) {
            circuito.setItemCircuitoStock(new ArrayList<ItemCircuitoStock>());
        }

        ItemCircuitoStock itemCircuito = new ItemCircuitoStock();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoStock().add(itemCircuito);

    }

    public void eliminarItemCircuitoStock(CircuitoFacturacion circuito, ItemCircuitoStock itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoStock() != null) {

            if (circuito.getItemCircuitoStock().remove(itemCircuito)) {

                ItemCircuitoPK itemCircuitoStockPK = new ItemCircuitoPK();
                itemCircuitoStockPK.setModulo(itemCircuito.getModulo());
                itemCircuitoStockPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoStockPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoStockPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoStock.class, itemCircuitoStockPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoTesoreria(CircuitoFacturacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoTesoreria() == null) {
            circuito.setItemCircuitoTesoreria(new ArrayList<ItemCircuitoTesoreria>());
        }

        ItemCircuitoTesoreria itemCircuito = new ItemCircuitoTesoreria();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoTesoreria().add(itemCircuito);

    }

    public void eliminarItemCircuitoTesoreria(CircuitoFacturacion circuito, ItemCircuitoTesoreria itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoTesoreria() != null) {
            if (circuito.getItemCircuitoTesoreria().remove(itemCircuito)) {

                ItemCircuitoPK itemCircuitoTesoreriaPK = new ItemCircuitoPK();
                itemCircuitoTesoreriaPK.setModulo(itemCircuito.getModulo());
                itemCircuitoTesoreriaPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoTesoreriaPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoTesoreriaPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;

                circuitoDAO.eliminar(ItemCircuitoTesoreria.class, itemCircuitoTesoreriaPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

}
