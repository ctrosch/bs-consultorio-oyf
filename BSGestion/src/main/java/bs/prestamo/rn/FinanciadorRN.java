/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.FinanciadorDAO;
import bs.prestamo.modelo.Financiador;
import bs.prestamo.modelo.ItemCuentaContableFinanciador;
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

public class FinanciadorRN implements Serializable {

    @EJB
    private FinanciadorDAO financiadorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Financiador financiador, boolean esNuevo) throws Exception {

        reorganizarNroItem(financiador);
        control(financiador);

        if (financiador.getId() == null) {
            financiadorDAO.crear(financiador);
        } else {
            financiadorDAO.editar(financiador);
        }
    }

    private void control(Financiador financiador) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (financiador.getDescripcion() == null || financiador.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Financiador financiador) throws Exception {

        financiadorDAO.eliminar(financiador);

    }

    public Financiador getFinanciador(Integer id) {
        return financiadorDAO.getFinanciador(id);
    }

    public List<Financiador> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return financiadorDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Financiador> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return financiadorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public void nuevoItemCuentaContable(Financiador financiador) throws ExcepcionGeneralSistema {

        if (financiador == null) {
            throw new ExcepcionGeneralSistema("No existe un financiador seleccionado para agregarle una cuenta contable");
        }

        if (financiador.getItemsCuentaContable() == null) {
            financiador.setItemsCuentaContable(new ArrayList<ItemCuentaContableFinanciador>());
        }

        ItemCuentaContableFinanciador itemCuentaContable = new ItemCuentaContableFinanciador();
        itemCuentaContable.setNroitm(financiador.getItemsCuentaContable().size() + 1);
        itemCuentaContable.setFinanciador(financiador);

        financiador.getItemsCuentaContable().add(itemCuentaContable);

    }

    public void eliminarItemCuentaContable(Financiador financiador, ItemCuentaContableFinanciador item) throws ExcepcionGeneralSistema {

        if (financiador == null) {
            throw new ExcepcionGeneralSistema("No existe un Financiador seleccionado para quitar una Materia");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemCuentaContableFinanciador e : financiador.getItemsCuentaContable()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == e.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemCuentaContableFinanciador remove = financiador.getItemsCuentaContable().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    financiadorDAO.eliminar(ItemCuentaContableFinanciador.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(financiador);

    }

    public void reorganizarNroItem(Financiador curso) {

        //Reorganizamos los números de items
        int i;

        if (curso.getItemsCuentaContable() != null) {

            i = 1;
            for (ItemCuentaContableFinanciador item : curso.getItemsCuentaContable()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

}
