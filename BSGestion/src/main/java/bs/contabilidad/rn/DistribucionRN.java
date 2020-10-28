/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.DistribucionDAO;
import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.Distribucion;
import bs.contabilidad.modelo.ItemDistribucion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class DistribucionRN implements Serializable {

    @EJB
    private DistribucionDAO distribucionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Distribucion guardar(Distribucion e, boolean esNuevo) throws Exception {

        control(e);

        reorganizarNroItem(e);

        if (esNuevo) {

            if (distribucionDAO.getObjeto(Distribucion.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getCodigo());
            }
            distribucionDAO.crear(e);

        } else {

            e = (Distribucion) distribucionDAO.editar(e);
        }

        return e;
    }

    private void control(Distribucion distribucion) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (distribucion.getCodigo() == null || distribucion.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (distribucion.getDescripcion() == null || distribucion.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (distribucion.getCentroCosto() == null) {

            sErrores += "- El centro de costo no puede estar vacío \n";
        }

        BigDecimal porcentaje = BigDecimal.ZERO;

        if (distribucion.getItemsDistribucion() != null) {

            for (ItemDistribucion item : distribucion.getItemsDistribucion()) {

                if (item.getSubCuenta() == null) {
                    item.setConError(true);
                    sErrores += "- Ingrese la subcuenta para el item " + item.getNroItem();
                }

                if (item.getPorcentaje() == null || item.getPorcentaje().compareTo(BigDecimal.ZERO) <= 0) {
                    item.setConError(true);
                    sErrores += "- El porcentaje debe ser mayor a cero para el item " + item.getNroItem();
                }

                porcentaje = porcentaje.add(item.getPorcentaje()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (!porcentaje.equals(new BigDecimal("100.00"))) {

            sErrores += "- La suma de los porcentajes de los items, debe ser igual a 100%. Porcentaje actual: " + porcentaje + " \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Distribucion deposito) throws Exception {

        distribucionDAO.eliminar(deposito);

    }

    public Distribucion getDistribucion(String id) {
        return distribucionDAO.getDistribucion(id);
    }

    public List<Distribucion> getListaByBusqueda(CentroCosto centroCosto, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return distribucionDAO.getListaByBusqueda(null, centroCosto, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public List<Distribucion> getListaByBusqueda(Map<String, String> filtro, CentroCosto centroCosto, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return distribucionDAO.getListaByBusqueda(filtro, centroCosto, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public void nuevoItemDistribucion(Distribucion distribucion) throws ExcepcionGeneralSistema {

        if (distribucion == null) {
            throw new ExcepcionGeneralSistema("No existe una distribución seleccionada para agregarle un item");
        }

        if (distribucion.getCentroCosto() == null) {
            throw new ExcepcionGeneralSistema("Primero debe seleccionar un centro de costo");
        }

        if (distribucion.getItemsDistribucion() == null) {
            distribucion.setItemsDistribucion(new ArrayList<ItemDistribucion>());
        }

        ItemDistribucion item = new ItemDistribucion();
        item.setNroItem(distribucion.getItemsDistribucion().size() + 1);
        item.setDistribucion(distribucion);
        item.setPorcentaje(BigDecimal.ZERO);

        distribucion.getItemsDistribucion().add(item);

    }

    public void eliminarItemDistribucion(Distribucion distribucion, ItemDistribucion itemBorrar) throws ExcepcionGeneralSistema {

        if (distribucion == null) {
            throw new ExcepcionGeneralSistema("No existe una distribucion seleccionada para quitar una subCuenta");
        }

        if (itemBorrar == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemDistribucion item : distribucion.getItemsDistribucion()) {

            if (item.getNroItem() >= 0) {

                if (item.getNroItem() == itemBorrar.getNroItem()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemDistribucion remove = distribucion.getItemsDistribucion().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    distribucionDAO.eliminar(ItemDistribucion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(distribucion);

    }

    public void reorganizarNroItem(Distribucion distribucion) {

        //Reorganizamos los números de items
        int i;

        if (distribucion.getItemsDistribucion() != null) {

            i = 1;
            for (ItemDistribucion item : distribucion.getItemsDistribucion()) {
                item.setNroItem(i);
                i++;
            }
        }

    }

}
