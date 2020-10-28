/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.rn;

import bs.compra.dao.CircuitoCompraDAO;
import bs.compra.modelo.CircuitoCompra;
import bs.compra.modelo.CircuitoCompraPK;
import bs.compra.modelo.ItemCircuitoCompraCompra;
import bs.compra.modelo.ItemCircuitoCompraPK;
import bs.compra.modelo.ItemCircuitoCompraProveedor;
import bs.compra.modelo.ItemCircuitoCompraStock;
import bs.compra.modelo.ItemCircuitoCompraTesoreria;
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
public class CircuitoCompraRN implements Serializable {

    @EJB
    private CircuitoCompraDAO circuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CircuitoCompra guardar(CircuitoCompra c, boolean esNuevo) throws Exception {

        asignarValores(c);
        control(c);

        if (esNuevo) {

            CircuitoCompraPK idPK = new CircuitoCompraPK(c.getCircom(), c.getCirapl());

            if (circuitoDAO.getObjeto(CircuitoCompra.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un circuito con el código " + idPK);
            }
            circuitoDAO.crear(c);

        } else {
            c = (CircuitoCompra) circuitoDAO.editar(c);
        }

        return c;
    }

    public void asignarValores(CircuitoCompra c) {

        if (c.getCircom() == null || c.getCircom().isEmpty() && c.getCircuitoComienzo() != null) {
            c.setCircom(c.getCircuitoComienzo().getCodigo());
        }

        if (c.getCirapl() == null || c.getCirapl().isEmpty() && c.getCircuitoAplicacion() != null) {
            c.setCirapl(c.getCircuitoAplicacion().getCodigo());
        }

        if (c.getItemCircuitoCompra() != null) {
            for (ItemCircuitoCompraCompra i : c.getItemCircuitoCompra()) {
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

        if (c.getItemCircuitoProveedor() != null) {
            for (ItemCircuitoCompraProveedor i : c.getItemCircuitoProveedor()) {
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
            for (ItemCircuitoCompraStock i : c.getItemCircuitoStock()) {
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
            for (ItemCircuitoCompraTesoreria i : c.getItemCircuitoTesoreria()) {
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

    public void control(CircuitoCompra c) throws ExcepcionGeneralSistema {

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

    public CircuitoCompra getCircuito(CircuitoCompraPK idPK) {

        return circuitoDAO.getObjeto(CircuitoCompra.class, idPK);

    }

    public CircuitoCompra getCircuito(String circom, String cirapl) {

        CircuitoCompraPK idPK = new CircuitoCompraPK(circom, cirapl);
        return circuitoDAO.getObjeto(CircuitoCompra.class, idPK);

    }

    public List<CircuitoCompra> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoCompra> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoCompra> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public void eliminar(CircuitoCompra codigoCircuitoCompra) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Comprobante getComprobanteCompra(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cf = circuitoDAO.getComprobanteCompra(circom, cirapl, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de compras (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
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

    public Comprobante getComprobanteProveedor(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cv = circuitoDAO.getComprobanteProveedor(circom, cirapl, codcom);
        if (cv == null) {
            throw new ExcepcionGeneralSistema("El comprobante de proveedores (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
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

    public void cargarCircuitosRelacionados(CircuitoCompra circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", false));
    }

    public CircuitoCompra iniciarCircuito(String circom, String cirapl, String codCO, String codPV, String codST, String codCJ) throws ExcepcionGeneralSistema {

        CircuitoCompra circuito = null;

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

        if (circuito.getActualizaCompra().equals("S") && codCO != null) {

            circuito.setComprobanteCompra(getComprobanteCompra(circom, cirapl, codCO));

        } else if (circuito.getActualizaProveedor().equals("S") && codPV != null) {

            circuito.setComprobanteProveedor(getComprobanteProveedor(circom, cirapl, codPV));

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
        if (circuito.getItemCircuitoProveedor() == null || circuito.getItemCircuitoProveedor().isEmpty()) {
            throw new ExcepcionGeneralSistema("El circuito debe tener configurado al menos un comprobante de proveedor");
        } else if (circuito.getComprobanteProveedor() == null) {
            circuito.setComprobanteProveedor(circuito.getItemCircuitoProveedor().get(0).getComprobante());
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

    public CircuitoCompra iniciarCircuito(String circom, String cirapl, String codCO, String codPV) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codCO, codPV, null, null);
    }

    public CircuitoCompra iniciarCircuito(String circom, String cirapl, String codCO, String codPV, String codST) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codCO, codPV, codST, null);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoCompra(CircuitoCompra circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoCompra() == null) {
            circuito.setItemCircuitoCompra(new ArrayList<ItemCircuitoCompraCompra>());
        }

        ItemCircuitoCompraCompra itemCircuito = new ItemCircuitoCompraCompra();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoCompra().add(itemCircuito);

    }

    public void eliminarItemCircuitoCompra(CircuitoCompra circuito, ItemCircuitoCompraCompra itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoCompra() != null) {

            if (circuito.getItemCircuitoCompra().remove(itemCircuito)) {

                ItemCircuitoCompraPK itemCircuitoCompraCompraPK = new ItemCircuitoCompraPK();
                itemCircuitoCompraCompraPK.setModulo(itemCircuito.getModulo());
                itemCircuitoCompraCompraPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoCompraCompraPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoCompraCompraPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoCompraCompra.class, itemCircuitoCompraCompraPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoProveedor(CircuitoCompra circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoProveedor() == null) {
            circuito.setItemCircuitoProveedor(new ArrayList<ItemCircuitoCompraProveedor>());
        }

        ItemCircuitoCompraProveedor itemCircuito = new ItemCircuitoCompraProveedor();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoProveedor().add(itemCircuito);

    }

    public void eliminarItemCircuitoProveedor(CircuitoCompra circuito, ItemCircuitoCompraProveedor itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoProveedor() != null) {

            if (circuito.getItemCircuitoProveedor().remove(itemCircuito)) {

                ItemCircuitoCompraPK itemCircuitoCompraProveedorPK = new ItemCircuitoCompraPK();
                itemCircuitoCompraProveedorPK.setModulo(itemCircuito.getModulo());
                itemCircuitoCompraProveedorPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoCompraProveedorPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoCompraProveedorPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoCompraProveedor.class, itemCircuitoCompraProveedorPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoStock(CircuitoCompra circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoStock() == null) {
            circuito.setItemCircuitoStock(new ArrayList<ItemCircuitoCompraStock>());
        }

        ItemCircuitoCompraStock itemCircuito = new ItemCircuitoCompraStock();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoStock().add(itemCircuito);

    }

    public void eliminarItemCircuitoStock(CircuitoCompra circuito, ItemCircuitoCompraStock itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoStock() != null) {

            if (circuito.getItemCircuitoStock().remove(itemCircuito)) {

                ItemCircuitoCompraPK itemCircuitoCompraStockPK = new ItemCircuitoCompraPK();
                itemCircuitoCompraStockPK.setModulo(itemCircuito.getModulo());
                itemCircuitoCompraStockPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoCompraStockPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoCompraStockPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoCompraStock.class, itemCircuitoCompraStockPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoTesoreria(CircuitoCompra circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoTesoreria() == null) {
            circuito.setItemCircuitoTesoreria(new ArrayList<ItemCircuitoCompraTesoreria>());
        }

        ItemCircuitoCompraTesoreria itemCircuito = new ItemCircuitoCompraTesoreria();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoTesoreria().add(itemCircuito);

    }

    public void eliminarItemCircuitoTesoreria(CircuitoCompra circuito, ItemCircuitoCompraTesoreria itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoTesoreria() != null) {

            if (circuito.getItemCircuitoTesoreria().remove(itemCircuito)) {

                ItemCircuitoCompraPK itemCircuitoCompraTesoreriaPK = new ItemCircuitoCompraPK();
                itemCircuitoCompraTesoreriaPK.setModulo(itemCircuito.getModulo());
                itemCircuitoCompraTesoreriaPK.setCodcom(itemCircuito.getCodcom());
                itemCircuitoCompraTesoreriaPK.setCirapl(itemCircuito.getCirapl());
                itemCircuitoCompraTesoreriaPK.setCircom(itemCircuito.getCircom());

                fItemBorrado = true;
                circuitoDAO.eliminar(ItemCircuitoCompraTesoreria.class, itemCircuitoCompraTesoreriaPK);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public CircuitoCompra duplicar(CircuitoCompra circuito) throws CloneNotSupportedException {

        circuito = circuito.clone();
        circuito.setDescripcion(circuito.getDescripcion() + " (Duplicado)");

        if (circuito.getItemCircuitoCompra() != null) {

            circuito.getItemCircuitoCompra().clear();

//            for (ItemCircuitoCompraCompra i : circuito.getItemCircuitoCompra()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoProveedor() != null) {

            circuito.getItemCircuitoProveedor().clear();

//            for (ItemCircuitoCompraProveedor i : circuito.getItemCircuitoProveedor()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoStock() != null) {

            circuito.getItemCircuitoStock().clear();

//            for (ItemCircuitoCompraStock i : circuito.getItemCircuitoStock()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoTesoreria() != null) {

            circuito.getItemCircuitoTesoreria().clear();

//            for (ItemCircuitoCompraTesoreria i : circuito.getItemCircuitoTesoreria()) {
//                i.setCircuito(circuito);
//            }
        }

        return circuito;
    }

}
