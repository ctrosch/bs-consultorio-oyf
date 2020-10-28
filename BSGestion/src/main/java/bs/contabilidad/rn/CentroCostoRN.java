/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.CentroCostoDAO;
import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
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
public class CentroCostoRN implements Serializable {

    @EJB
    private CentroCostoDAO centroCostoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CentroCosto guardar(CentroCosto e, boolean esNuevo) throws Exception {

        control(e);

        if (esNuevo) {

            if (centroCostoDAO.getObjeto(CentroCosto.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getCodigo());
            }
            centroCostoDAO.crear(e);

        } else {

            e = (CentroCosto) centroCostoDAO.editar(e);
        }

        return e;
    }

    private void control(CentroCosto centroCosto) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (centroCosto.getCodigo() == null || centroCosto.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (centroCosto.getDescripcion() == null || centroCosto.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (centroCosto.getSubCuentas() != null) {

            for (SubCuenta item : centroCosto.getSubCuentas()) {

                if (item.getCodigo() == null) {
                    item.setConError(true);
                    sErrores += "- Ingrese un código para la subcuenta \n";
                }

                if (item.getDescripcion() == null || item.getDescripcion().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Ingrese una descripción para la subcuenta \n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(CentroCosto deposito) throws Exception {

        centroCostoDAO.eliminar(deposito);

    }

    public CentroCosto getCentroCosto(String id) {
        return centroCostoDAO.getCentroCosto(id);
    }

    public List<CentroCosto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return centroCostoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public List<CentroCosto> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return centroCostoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public void nuevoItemSubCuenta(CentroCosto centroCosto) throws ExcepcionGeneralSistema {

        if (centroCosto == null) {
            throw new ExcepcionGeneralSistema("No existe un centro de costo seleccionado para agregarle un item");
        }

        if (centroCosto.getSubCuentas() == null) {
            centroCosto.setSubCuentas(new ArrayList<SubCuenta>());
        }

        SubCuenta subCuenta = new SubCuenta();
        subCuenta.setCentroCosto(centroCosto);

        centroCosto.getSubCuentas().add(subCuenta);

    }

    public void eliminarItemSubCuenta(CentroCosto centroCosto, SubCuenta subCuenta) throws ExcepcionGeneralSistema {

        if (subCuenta == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (SubCuenta item : centroCosto.getSubCuentas()) {

            if (item.getCodigo() != null) {

                if (item.getCodigo().equals(subCuenta.getCodigo())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            SubCuenta remove = centroCosto.getSubCuentas().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getCodigo() != null) {
                    centroCostoDAO.eliminar(SubCuenta.class, remove.getCodigo());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

    }

}
