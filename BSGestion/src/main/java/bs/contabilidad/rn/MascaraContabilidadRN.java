/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.MascaraContabilidadDAO;
import bs.contabilidad.modelo.ItemMascaraContabilidad;
import bs.contabilidad.modelo.MascaraContabilidad;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class MascaraContabilidadRN implements Serializable {

    @EJB
    private MascaraContabilidadDAO mascaraDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MascaraContabilidad guardar(MascaraContabilidad e, boolean esNuevo) throws Exception {

        control(e);

        if (esNuevo) {

            if (mascaraDAO.getObjeto(MascaraContabilidad.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getCodigo());
            }
            mascaraDAO.crear(e);

        } else {

            e = (MascaraContabilidad) mascaraDAO.editar(e);
        }

        return e;
    }

    private void control(MascaraContabilidad mascara) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (mascara.getCodigo() == null || mascara.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (mascara.getDescripcion() == null || mascara.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (mascara.getItems() != null) {

            for (ItemMascaraContabilidad item : mascara.getItems()) {

                if (item.getCuentaContable() == null) {
                    item.setConError(true);
                    sErrores += "- Existe un item sin cuenta contable asignada \n";
                }

                if (item.getDebeHaber() == null || item.getDebeHaber().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Existe un item que no se ha indicado el campo debe/haber \n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(MascaraContabilidad deposito) throws Exception {

        mascaraDAO.eliminar(deposito);

    }

    public MascaraContabilidad getMascaraContabilidad(String id) {
        return mascaraDAO.getMascaraContabilidad(id);
    }

    public List<MascaraContabilidad> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return mascaraDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public List<MascaraContabilidad> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return mascaraDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public void nuevoItem(MascaraContabilidad mascara) throws ExcepcionGeneralSistema {

        if (mascara == null) {
            throw new ExcepcionGeneralSistema("No existe una máscara seleccionada para agregarle un item");
        }

        if (mascara.getItems() == null) {
            mascara.setItems(new ArrayList<ItemMascaraContabilidad>());
        }

        ItemMascaraContabilidad item = new ItemMascaraContabilidad();
        item.setNroitm(mascara.getItems().size() + 1);
        item.setMascara(mascara);

        mascara.getItems().add(item);
        reorganizarNroItem(mascara);
    }

    public void eliminarItem(MascaraContabilidad mascara, ItemMascaraContabilidad itemMascara) throws ExcepcionGeneralSistema {

        if (itemMascara == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemMascaraContabilidad item : mascara.getItems()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemMascara.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemMascaraContabilidad remove = mascara.getItems().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    mascaraDAO.eliminar(ItemMascaraContabilidad.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(mascara);
    }

    public void reorganizarNroItem(MascaraContabilidad mascara) {

        //Reorganizamos los números de items
        int i;

        if (mascara.getItems() != null) {

            i = 1;
            for (ItemMascaraContabilidad item : mascara.getItems()) {
                item.setNroitm(i);
                i++;
            }
        }

    }
}
