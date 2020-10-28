/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.MascaraStockDAO;
import bs.stock.modelo.ItemMascaraStock;
import bs.stock.modelo.MascaraStock;
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
public class MascaraStockRN implements Serializable {

    @EJB
    private MascaraStockDAO mascaraDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MascaraStock guardar(MascaraStock mascara, boolean esNuevo) throws Exception {

        control(mascara);

        if (esNuevo) {

            if (mascaraDAO.getObjeto(MascaraStock.class, mascara.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + mascara.getCodigo());
            }
            mascaraDAO.crear(mascara);

        } else {

            mascara = (MascaraStock) mascaraDAO.editar(mascara);
        }

        return mascara;
    }

    private void control(MascaraStock mascara) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (mascara.getCodigo() == null || mascara.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (mascara.getDescripcion() == null || mascara.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (mascara.getItems() != null) {

            for (ItemMascaraStock item : mascara.getItems()) {

                if (item.getProducto() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sin producto asignado \n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(MascaraStock deposito) throws Exception {

        mascaraDAO.eliminar(deposito);

    }

    public MascaraStock getMascaraStock(String id) {
        return mascaraDAO.getMascaraStock(id);
    }

    public List<MascaraStock> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return mascaraDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public List<MascaraStock> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return mascaraDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public void nuevoItem(MascaraStock mascara) throws ExcepcionGeneralSistema {

        if (mascara == null) {
            throw new ExcepcionGeneralSistema("No existe una máscara seleccionada para agregarle un item");
        }

        if (mascara.getItems() == null) {
            mascara.setItems(new ArrayList<ItemMascaraStock>());
        }

        ItemMascaraStock item = new ItemMascaraStock();
        item.setNroitm(mascara.getItems().size() + 1);
        item.setMascara(mascara);

        mascara.getItems().add(item);
        reorganizarNroItem(mascara);
    }

    public void eliminarItem(MascaraStock mascara, ItemMascaraStock itemMascara) throws ExcepcionGeneralSistema {

        if (itemMascara == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemMascaraStock item : mascara.getItems()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemMascara.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemMascaraStock remove = mascara.getItems().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    mascaraDAO.eliminar(ItemMascaraStock.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(mascara);
    }

    public void reorganizarNroItem(MascaraStock mascara) {

        //Reorganizamos los números de items
        int i;

        if (mascara.getItems() != null) {

            i = 1;
            for (ItemMascaraStock item : mascara.getItems()) {
                item.setNroitm(i);
                i++;
            }
        }

    }
}
