/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.Numeros;
import bs.proveedores.dao.CondicionPagoProveedorDAO;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ItemCondicionPagoProveedor;
import bs.proveedores.modelo.ItemCondicionPagoProveedorPK;
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
public class CondicionPagoProveedorRN implements Serializable {

    @EJB
    private CondicionPagoProveedorDAO condicionPagoProveedorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CondicionPagoProveedor guardar(CondicionPagoProveedor condicion, boolean esNuevo) throws Exception {

        preSave(condicion);
        control(condicion);

        if (esNuevo) {

            if (condicionPagoProveedorDAO.getObjeto(CondicionPagoProveedor.class, condicion.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un condicion con ese id " + condicion.getCodigo());
            }

            condicionPagoProveedorDAO.crear(condicion);

        } else {

            condicion = (CondicionPagoProveedor) condicionPagoProveedorDAO.editar(condicion);

        }

        return condicion;

    }

    private void preSave(CondicionPagoProveedor condicion) throws ExcepcionGeneralSistema, Exception {

        if (condicion.getCuotas() != null) {

            for (ItemCondicionPagoProveedor item : condicion.getCuotas()) {

                if (item.getCondicionPago() == null) {
                    item.setCondicionPago(condicion);
                }

                if (item.getCndpag() == null || item.getCndpag().isEmpty()) {
                    item.setCndpag(condicion.getCodigo());
                }
            }
        }
    }

    private void control(CondicionPagoProveedor condicion) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (condicion.getCodigo() == null || condicion.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (condicion.getDescripcion() == null || condicion.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (condicion.getImputaCuentaCorriente() == null || condicion.getImputaCuentaCorriente().isEmpty()) {

            sErrores += "- Seleccione si imputa en Cuenta Corriente \n";
        }

        double porcentajeTotal = 0;

        for (ItemCondicionPagoProveedor i : condicion.getCuotas()) {

            if (i.getCuotas() <= 0) {
                sErrores += "- El Nro. de cuota debe ser mayor a 0 \n";
            }

            if (i.getDiasDePago() < 0) {
                sErrores += "- La Cant. Dias en el El Nro. cuota" + i.getCuotas() + " debe ser mayor o igual a 0 \n";
            }

            if (i.getPorcentaje() < 0 || i.getPorcentaje() > 100) {
                sErrores += "- En el Nro. cuota " + i.getCuotas() + " el porcentaje debe ser mayor a 0 o menor igual a 100 \n";
            }

            porcentajeTotal = porcentajeTotal + i.getPorcentaje();

        }

        if (Numeros.redondear(porcentajeTotal) != 100) {
            sErrores += "- La sumatoria de las cuotas debe ser igual a 100 \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void nuevoItemCondicionPagoProveedor(CondicionPagoProveedor condicion) throws ExcepcionGeneralSistema {

        if (condicion == null) {
            throw new ExcepcionGeneralSistema("No existe una condicion de pago seleccionada para agregarle una Cuota");
        }

        if (condicion.getCuotas() == null) {
            condicion.setCuotas(new ArrayList<ItemCondicionPagoProveedor>());
        }

        ItemCondicionPagoProveedor itemCondicionDePago = new ItemCondicionPagoProveedor();

        itemCondicionDePago.setCondicionPago(condicion);

        condicion.getCuotas().add(itemCondicionDePago);

    }

    public void eliminarItemCondicionPago(CondicionPagoProveedor condicion, ItemCondicionPagoProveedor item) throws ExcepcionGeneralSistema, Exception {

        if (condicion.getCuotas().remove(item)) {

            ItemCondicionPagoProveedorPK itemCondicionPagoProveedorPK = new ItemCondicionPagoProveedorPK();
            itemCondicionPagoProveedorPK.setCndpag(item.getCndpag());
            itemCondicionPagoProveedorPK.setCuotas(item.getCuotas());

            condicionPagoProveedorDAO.eliminar(ItemCondicionPagoProveedor.class, itemCondicionPagoProveedorPK);
        }

    }

    public CondicionPagoProveedor getCondicionPagoProveedor(String value) {

        return condicionPagoProveedorDAO.getObjeto(CondicionPagoProveedor.class, value);
    }

    public List<CondicionPagoProveedor> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return condicionPagoProveedorDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CondicionPagoProveedor> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return condicionPagoProveedorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public CondicionPagoProveedor getPrimeraCondicionByImputacion(String imputaCuentaCorriente) {

        List<CondicionPagoProveedor> lista = condicionPagoProveedorDAO.getListaByBusquedaByImputacion(imputaCuentaCorriente, false, 1);

        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        } else {
            return null;
        }
    }

}
