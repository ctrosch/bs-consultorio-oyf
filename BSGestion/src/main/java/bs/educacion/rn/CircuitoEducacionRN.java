/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.CircuitoEducacionDAO;
import bs.educacion.modelo.CircuitoEducacion;
import bs.educacion.modelo.CircuitoEducacionPK;
import bs.educacion.modelo.ItemCircuitoEducacionEducacion;
import bs.educacion.modelo.ItemCircuitoEducacionStock;
import bs.educacion.modelo.ItemCircuitoEducacionTesoreria;
import bs.educacion.modelo.ItemCircuitoEducacionVenta;
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
public class CircuitoEducacionRN implements Serializable {

    @EJB
    private CircuitoEducacionDAO circuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CircuitoEducacion guardar(CircuitoEducacion c, boolean esNuevo) throws Exception {

        asignarValores(c);
        control(c);

        if (esNuevo) {

            CircuitoEducacionPK idPK = new CircuitoEducacionPK(c.getCircom(), c.getCirapl());

            if (circuitoDAO.getObjeto(CircuitoEducacion.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un circuito con el código " + idPK);
            }
            circuitoDAO.crear(c);

        } else {
            c = (CircuitoEducacion) circuitoDAO.editar(c);
        }

        return c;
    }

    public void asignarValores(CircuitoEducacion c) {

        if (c.getCircom() == null || c.getCircom().isEmpty() && c.getCircuitoComienzo() != null) {
            c.setCircom(c.getCircuitoComienzo().getCodigo());
        }

        if (c.getCirapl() == null || c.getCirapl().isEmpty() && c.getCircuitoAplicacion() != null) {
            c.setCirapl(c.getCircuitoAplicacion().getCodigo());
        }

        if (c.getItemCircuitoEducacion() != null) {
            for (ItemCircuitoEducacionEducacion i : c.getItemCircuitoEducacion()) {
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
            for (ItemCircuitoEducacionVenta i : c.getItemCircuitoVenta()) {
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
            for (ItemCircuitoEducacionStock i : c.getItemCircuitoStock()) {
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
            for (ItemCircuitoEducacionTesoreria i : c.getItemCircuitoTesoreria()) {
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

    public void control(CircuitoEducacion c) throws ExcepcionGeneralSistema {

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

    public CircuitoEducacion getCircuito(CircuitoEducacionPK idPK) {

        return circuitoDAO.getObjeto(CircuitoEducacion.class, idPK);

    }

    public CircuitoEducacion getCircuito(String circom, String cirapl) {

        CircuitoEducacionPK idPK = new CircuitoEducacionPK(circom, cirapl);
        return circuitoDAO.getObjeto(CircuitoEducacion.class, idPK);

    }

    public List<CircuitoEducacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public void eliminar(CircuitoEducacion codigoCircuitoEducacion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Comprobante getComprobanteEducacion(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cf = circuitoDAO.getComprobanteEducacion(circom, cirapl, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de educación (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
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

    public void cargarCircuitosRelacionados(CircuitoEducacion circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", false));
    }

    public CircuitoEducacion iniciarCircuito(String circom, String cirapl, String codED, String codPV, String codST, String codCJ) throws ExcepcionGeneralSistema {

        CircuitoEducacion circuito = null;

        if (circom != null && cirapl != null) {
            circuito = getCircuito(circom, cirapl);
        }

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No se encontró circuito " + circom + "-" + cirapl);
        }

        cargarCircuitosRelacionados(circuito);

        if (circuito.getActualizaEducacion().equals("S") && codED != null) {

            circuito.setComprobanteEducacion(getComprobanteEducacion(circom, cirapl, codED));

        } else if (circuito.getActualizaVenta().equals("S") && codPV != null) {

            circuito.setComprobanteVenta(getComprobanteVenta(circom, cirapl, codPV));

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
            throw new ExcepcionGeneralSistema("El circuito debe tener configurado al menos un comprobante de proveedor");
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

    public CircuitoEducacion iniciarCircuito(String circom, String cirapl, String codCO, String codPV) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codCO, codPV, null, null);
    }

    public CircuitoEducacion iniciarCircuito(String circom, String cirapl, String codCO, String codPV, String codST) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codCO, codPV, codST, null);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoEducacion(CircuitoEducacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoEducacion() == null) {
            circuito.setItemCircuitoEducacion(new ArrayList<ItemCircuitoEducacionEducacion>());
        }

        ItemCircuitoEducacionEducacion itemCircuito = new ItemCircuitoEducacionEducacion();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoEducacion().add(itemCircuito);

    }

    public void eliminarItemCircuitoEducacion(CircuitoEducacion circuito, ItemCircuitoEducacionEducacion itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoEducacion() != null) {

            if (circuito.getItemCircuitoEducacion().remove(itemCircuito)) {
                fItemBorrado = true;
                circuitoDAO.eliminar(itemCircuito);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoVenta(CircuitoEducacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoVenta() == null) {
            circuito.setItemCircuitoVenta(new ArrayList<ItemCircuitoEducacionVenta>());
        }

        ItemCircuitoEducacionVenta itemCircuito = new ItemCircuitoEducacionVenta();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoVenta().add(itemCircuito);

    }

    public void eliminarItemCircuitoVenta(CircuitoEducacion circuito, ItemCircuitoEducacionVenta itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoVenta() != null) {

            if (circuito.getItemCircuitoVenta().remove(itemCircuito)) {
                fItemBorrado = true;
                circuitoDAO.eliminar(itemCircuito);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoStock(CircuitoEducacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoStock() == null) {
            circuito.setItemCircuitoStock(new ArrayList<ItemCircuitoEducacionStock>());
        }

        ItemCircuitoEducacionStock itemCircuito = new ItemCircuitoEducacionStock();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoStock().add(itemCircuito);

    }

    public void eliminarItemCircuitoStock(CircuitoEducacion circuito, ItemCircuitoEducacionStock itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoStock() != null) {

            if (circuito.getItemCircuitoStock().remove(itemCircuito)) {
                fItemBorrado = true;
                circuitoDAO.eliminar(itemCircuito);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    //----------------------------------------------------------------------------
    public void nuevoItemCircuitoTesoreria(CircuitoEducacion circuito) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No existe un circuito seleccionado para agregarle un comprobante");
        }

        if (circuito.getItemCircuitoTesoreria() == null) {
            circuito.setItemCircuitoTesoreria(new ArrayList<ItemCircuitoEducacionTesoreria>());
        }

        ItemCircuitoEducacionTesoreria itemCircuito = new ItemCircuitoEducacionTesoreria();
        itemCircuito.setCircom(circuito.getCircom());
        itemCircuito.setCirapl(circuito.getCirapl());

        circuito.getItemCircuitoTesoreria().add(itemCircuito);

    }

    public void eliminarItemCircuitoTesoreria(CircuitoEducacion circuito, ItemCircuitoEducacionTesoreria itemCircuito) throws ExcepcionGeneralSistema, Exception {

        boolean fItemBorrado = false;

        if (circuito != null && itemCircuito != null && circuito.getItemCircuitoTesoreria() != null) {

            if (circuito.getItemCircuitoTesoreria().remove(itemCircuito)) {
                fItemBorrado = true;
                circuitoDAO.eliminar(itemCircuito);
            }

        } else {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public CircuitoEducacion duplicar(CircuitoEducacion circuito) throws CloneNotSupportedException {

        circuito = circuito.clone();
        circuito.setDescripcion(circuito.getDescripcion() + " (Duplicado)");

        if (circuito.getItemCircuitoEducacion() != null) {

            circuito.getItemCircuitoEducacion().clear();

//            for (ItemCircuitoEducacionEducacion i : circuito.getItemCircuitoEducacion()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoVenta() != null) {

            circuito.getItemCircuitoVenta().clear();

//            for (ItemCircuitoEducacionVenta i : circuito.getItemCircuitoVenta()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoStock() != null) {

            circuito.getItemCircuitoStock().clear();

//            for (ItemCircuitoEducacionStock i : circuito.getItemCircuitoStock()) {
//                i.setCircuito(circuito);
//            }
        }

        if (circuito.getItemCircuitoTesoreria() != null) {

            circuito.getItemCircuitoTesoreria().clear();

//            for (ItemCircuitoEducacionTesoreria i : circuito.getItemCircuitoTesoreria()) {
//                i.setCircuito(circuito);
//            }
        }

        return circuito;
    }

}
