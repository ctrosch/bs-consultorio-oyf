/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.SubCuentaDAO;
import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
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
public class SubCuentaRN implements Serializable {

    @EJB
    private SubCuentaDAO subCuentaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SubCuenta guardar(SubCuenta e, boolean esNuevo) throws Exception {

        control(e);

        if (esNuevo) {

            if (subCuentaDAO.getObjeto(SubCuenta.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getCodigo());
            }
            subCuentaDAO.crear(e);

        } else {

            e = (SubCuenta) subCuentaDAO.editar(e);
        }

        return e;
    }

    private void control(SubCuenta subCuenta) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (subCuenta.getCodigo() == null || subCuenta.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (subCuenta.getDescripcion() == null || subCuenta.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (subCuenta.getCentroCosto() == null) {

            sErrores += "- El Centro de costo no puede estar vacío \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(SubCuenta deposito) throws Exception {

        subCuentaDAO.eliminar(deposito);

    }

    public SubCuenta getSubCuenta(String id) {
        return subCuentaDAO.getSubCuenta(id);
    }

    public List<SubCuenta> getListaByBusqueda(Map<String, String> filtro, CentroCosto centroCosto, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {
        return subCuentaDAO.getListaByBusqueda(filtro, centroCosto, txtBusqueda, mostrarDeBaja, cantRegistros);
    }

    public List<SubCuenta> getListaByBusqueda(CentroCosto centroCosto, String txtBusqueda, boolean mostrarDeBaja, int cantRegistros) {

        return subCuentaDAO.getListaByBusqueda(null, centroCosto, txtBusqueda, mostrarDeBaja, 10);
    }

    public List<SubCuenta> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return subCuentaDAO.getListaByBusqueda(null, null, txtBusqueda, mostrarDeBaja, 10);
    }

}
