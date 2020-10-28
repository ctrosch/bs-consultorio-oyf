/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.rn;

import bs.contrato.dao.ContratoDAO;
import bs.contrato.modelo.Contrato;
import bs.contrato.modelo.ItemContrato;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.modelo.Producto;
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
 * @author ctrosch
 */
@Stateless
public class ContratoRN implements Serializable {

    @EJB
    private ContratoDAO contratoDAO;
    @EJB
    private EstadoContratoRN estadoRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Contrato guardar(Contrato contrato, boolean esNuevo) throws Exception {

        preSave(contrato, esNuevo);
        control(contrato, esNuevo);

        if (contrato.getId() == null) {

            contratoDAO.crear(contrato);
        } else {

            contrato = (Contrato) contratoDAO.editar(contrato);
        }

        return contrato;

    }

    public Contrato nuevo() throws ExcepcionGeneralSistema, Exception {

        Contrato contrato = new Contrato();
        contrato.setNroContrato(getProximoNro());
        contrato.setEstado(estadoRN.getEstadoContrato("A"));

        return contrato;
    }

    public Contrato duplicar(Contrato c) throws Exception {

        if (c == null) {
            throw new ExcepcionGeneralSistema(" nulo, nada para duplicar");
        }

        Contrato contrato = c.clone();
        contrato.setNroContrato(getProximoNro());
        contrato.setEstado(estadoRN.getEstadoContrato("A"));
        contrato.setDescripcion(c.getDescripcion() + "( Duplicado)");

        return contrato;
    }

    public void preSave(Contrato contrato, boolean esNuevo) throws Exception {

        if (contrato.getItemsComtrato() != null && !contrato.getItemsComtrato().isEmpty()) {

            contrato.getItemsComtrato().forEach(i -> {
                i.setContrato(contrato);

            });
        }

    }

    public void control(Contrato contrato, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (contrato.getNroContrato() == null || contrato.getNroContrato().isEmpty()) {
            sError += "No hay número de contrato Asignado | ";
        }

        if (contrato.getTipoContrato() == null) {
            sError += "No hay tipo de contrato Asignado | ";
        }

        if (contrato.getEstado() == null) {
            sError += "No hay estado Asignado | ";
        }

        if (contrato.getCliente() == null) {
            sError += "No hay cliente Asignado | ";
        }

        if (contrato.getDescripcion() == null || contrato.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (contrato.getFechaDesde() == null) {
            sError += "La Fecha desde no puede estar vacía | ";
        }

        if (contrato.getFechaHasta() == null) {
            sError += "La Fecha hasta no puede estar vacía | ";
        }

        if (contrato.getFechaHasta().before(contrato.getFechaDesde())) {
            sError += "La Fecha Hasta debe ser mayor a la Fecha Desde | ";

        }

        if (contrato.getItemsComtrato() != null && !contrato.getItemsComtrato().isEmpty()) {

            for (ItemContrato i : contrato.getItemsComtrato()) {
                if (i.getFechaHasta().before(i.getFechaDesde())) {
                    sError += "La Fecha Hasta debe ser mayor o Igual a la Fecha Desde en el item " + i.getDescripcion() + " | ";
                }

                if (i.getFechaDesde().before(contrato.getFechaDesde())) {
                    sError += "La Fecha Desde del item " + i.getDescripcion() + " debe ser mayor o igual a la Fecha Desde del Contrato | ";
                }

                if (contrato.getFechaHasta().before(i.getFechaHasta())) {
                    sError += "La Fecha Hasta del item " + i.getDescripcion() + " debe ser menor o igual a la Fecha Hasta del Contrato | ";
                }

                if (i.getPrecio() <= 0 && i.getPrecioSecundario() <= 0) {
                    sError += "El precio del producto " + i.getDescripcion() + " debe ser mayor a cero | ";
                }
            }
        } else {
            sError += "El contrato debe tener al menos 1 items a facturar | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public String getProximoNro() {

        String nroCodigo = "00000000" + String.valueOf(contratoDAO.getMaxNroContrato());
        return nroCodigo.substring(nroCodigo.length() - 6, nroCodigo.length());

    }

//    public void asignarNroContrato(Contrato contrato) throws Exception {
//
//        String nro = getProximoNro();
//
//        contrato.setNroContrato(nro);
//    }
    public void eliminar(Contrato contrato) throws Exception {

        contratoDAO.eliminar(contrato);

    }

    public void nuevoItem(Contrato contrato) throws ExcepcionGeneralSistema {

        if (contrato == null) {
            throw new ExcepcionGeneralSistema("No existe un Contrato seleccionado para agregarle un Item");
        }

        if (contrato.getItemsComtrato() == null) {
            contrato.setItemsComtrato(new ArrayList<ItemContrato>());
        }

        ItemContrato itemContrato = new ItemContrato();
        itemContrato.setNroitm(contrato.getItemsComtrato().size() + 1);
        itemContrato.setContrato(contrato);

        contrato.getItemsComtrato().add(itemContrato);

        reorganizarNroItem(contrato);

    }

    public void eliminarItem(Contrato contrato, ItemContrato itemContrato) throws ExcepcionGeneralSistema, Exception {
        if (contrato == null) {
            throw new ExcepcionGeneralSistema("Contrato vacío, nada para eliminar");
        }
        if (itemContrato == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemContrato item : contrato.getItemsComtrato()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemContrato.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemContrato remove = contrato.getItemsComtrato().remove(indiceItemBorrar);
            if (remove != null) {
                if (contrato.getId() != null && remove.getId() != null) {

                    contratoDAO.eliminar(ItemContrato.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(contrato);
    }

    public void reorganizarNroItem(Contrato contrato) {

        //Reorganizamos los números de items
        int i;

        if (contrato.getItemsComtrato() != null) {

            i = 1;
            for (ItemContrato item : contrato.getItemsComtrato()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    public void asignarProducto(ItemContrato a, Producto producto) throws ExcepcionGeneralSistema {

        if (a == null || producto == null) {
            return;
        }

        a.setDescripcion(producto.getDescripcion());

    }

    public void procesarFechaDesde(ItemContrato a, Contrato contrato) throws ExcepcionGeneralSistema {

        if (a == null || a.getFechaDesde() == null || contrato == null) {
            return;
        }
        String sError = "";

        if (a.getFechaDesde().before(contrato.getFechaDesde())) {
            sError += "Fecha Desde del Item debe ser posterior o igual a Fecha Desde del contrato | ";
        }

        if (a.getFechaHasta().before(a.getFechaDesde())) {
            sError += "Fecha Desde del Item debe ser anterior o igual a Fecha Hasta del Item | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void procesarFechaHasta(ItemContrato a, Contrato contrato) throws ExcepcionGeneralSistema {

        if (a == null || a.getFechaHasta() == null || contrato == null) {
            return;
        }
        String sError = "";

        if (contrato.getFechaHasta().before(a.getFechaHasta())) {
            sError += "Fecha Hasta del Item debe ser anterior o igual a Fecha Hasta del contrato | ";
        }

        if (a.getFechaHasta().before(a.getFechaDesde())) {
            sError += "Fecha Hasta del Item debe ser posterior o igual a Fecha Hasta del Item | ";
        }
        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public Contrato getContrato(Integer id) {
        return contratoDAO.getContrato(id);
    }

    public List<Contrato> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return contratoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public List<Contrato> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return contratoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

}
