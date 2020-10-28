/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.Numeros;
import bs.ventas.dao.CondicionPagoVentaDAO;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ItemCondicionPagoVenta;
import bs.ventas.modelo.ItemCondicionPagoVentaPK;
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
public class CondicionPagoVentaRN implements Serializable {

    @EJB
    private CondicionPagoVentaDAO condicionDePagoVentaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CondicionDePagoVenta guardar(CondicionDePagoVenta condicion, boolean esNuevo) throws Exception {

        asignarDatos(condicion);
        control(condicion);

        if (esNuevo) {

            if (condicionDePagoVentaDAO.getObjeto(CondicionDePagoVenta.class, condicion.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un condicion con ese id " + condicion.getCodigo());
            }

            condicionDePagoVentaDAO.crear(condicion);

        } else {

            condicion = (CondicionDePagoVenta) condicionDePagoVentaDAO.editar(condicion);

        }

        return condicion;

    }

    private void control(CondicionDePagoVenta condicion) throws ExcepcionGeneralSistema, Exception {

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

        for (ItemCondicionPagoVenta i : condicion.getCuotas()) {

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
            sErrores += "- La sumatoria de las cuotas debe ser igual a 100% \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    private void asignarDatos(CondicionDePagoVenta condicion) throws ExcepcionGeneralSistema, Exception {

        if (condicion.getCuotas() != null) {

            for (ItemCondicionPagoVenta item : condicion.getCuotas()) {

                if (item.getCondicionPago() == null) {
                    item.setCondicionPago(condicion);
                }

                if (item.getCndpag() == null || item.getCndpag().isEmpty()) {
                    item.setCndpag(condicion.getCodigo());
                }

            }
        }
    }

    public CondicionDePagoVenta getCondicionDePagoVenta(String value) {

        return condicionDePagoVentaDAO.getObjeto(CondicionDePagoVenta.class, value);
    }

    public List<CondicionDePagoVenta> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return condicionDePagoVentaDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<CondicionDePagoVenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return condicionDePagoVentaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public CondicionDePagoVenta getPrimeraCondicionByImputacion(String imputaCuentaCorriente) {

        List<CondicionDePagoVenta> lista = condicionDePagoVentaDAO.getListaByBusquedaByImputacion(imputaCuentaCorriente, false, 1);

        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        } else {
            return null;
        }
    }

    public void nuevoItemCondicionPagoVenta(CondicionDePagoVenta condicion) throws ExcepcionGeneralSistema {

        if (condicion == null) {
            throw new ExcepcionGeneralSistema("No existe una condicion de pago seleccionada para agregarle una Cuota");
        }

        if (condicion.getCuotas() == null) {
            condicion.setCuotas(new ArrayList<ItemCondicionPagoVenta>());
        }

        ItemCondicionPagoVenta itemCondicionDePago = new ItemCondicionPagoVenta();

        itemCondicionDePago.setCondicionPago(condicion);

        condicion.getCuotas().add(itemCondicionDePago);

    }

    public void eliminarItemCondicionPagoVenta(CondicionDePagoVenta condicion, ItemCondicionPagoVenta item) throws ExcepcionGeneralSistema, Exception {

        if (condicion.getCuotas().remove(item)) {

            ItemCondicionPagoVentaPK itemCondicionPagoVentaPK = new ItemCondicionPagoVentaPK();
            itemCondicionPagoVentaPK.setCndpag(condicion.getCodigo());
            itemCondicionPagoVentaPK.setCuotas(item.getCuotas());

            condicionDePagoVentaDAO.eliminar(ItemCondicionPagoVenta.class, itemCondicionPagoVentaPK);
        }

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
