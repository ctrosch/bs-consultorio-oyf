/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.CuentaContableDAO;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
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
 * @author Claudio
 */
@Stateless
public class CuentaContableRN implements Serializable {

    @EJB
    private CuentaContableDAO cuentaContableDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CuentaContable guardar(CuentaContable cuenta, boolean esNuevo) throws Exception {

        control(cuenta);

        if (esNuevo) {
            if (cuentaContableDAO.getObjeto(CuentaContable.class, cuenta.getNrocta()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una cuenta contable con el código" + cuenta.getNrocta());
            }
            cuentaContableDAO.crear(cuenta);
        } else {
            cuenta = (CuentaContable) cuentaContableDAO.editar(cuenta);
        }

        return cuenta;
    }

    private void control(CuentaContable cuenta) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (cuenta.getNrocta() == null || cuenta.getNrocta().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (cuenta.getDescripcion() == null || cuenta.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (cuenta.getImputable() == null || cuenta.getImputable().isEmpty()) {

            sErrores += "- Debe indicar si la cuenta es imputable \n";
        }

        if (cuenta.getCuentaContable() != null && cuenta.getCuentaContable().getImputable().equals("S")) {
            sErrores += "- La cuenta principal de la nueva cuenta contable, no puede ser una cuenta imputable \n";
        }

        if (cuenta.getCentrosCostos() != null) {

            for (CuentaContableCentroCosto i : cuenta.getCentrosCostos()) {

                if (i.getCuentaContable() == null) {
                    sErrores += "- La cuenta contable en el item " + i.getNroItem() + ", no puede estar vacía\n";
                }

                if (i.getCentroCosto() == null) {
                    sErrores += "- El centro de costo en el item " + i.getNroItem() + ", no puede estar vacío\n";
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public CuentaContable nuevo(CuentaContable cuentaMadre) throws Exception {

        if (cuentaMadre != null) {
            if (cuentaMadre.getImputable().equals("S")) {
                throw new ExcepcionGeneralSistema("La cuenta principal de la nueva cuenta contable, no puede ser una cuenta imputable");
            }
        }

        String codigo = obtenerSiguienteCogido(cuentaMadre);

        CuentaContable cuentaContable = new CuentaContable();
        cuentaContable.setNrocta(codigo);

        if (cuentaMadre != null) {
            cuentaContable.setNivel(cuentaMadre.getNivel() + 1);
            cuentaContable.setCuentaContable(cuentaMadre);
        } else {
            cuentaContable.setNivel(1);
        }

        return cuentaContable;
    }

    public void eliminar(CuentaContable cuenta) throws Exception {

        cuentaContableDAO.eliminar(cuenta);
    }

    public CuentaContable getCuentaContable(String nrocta) {
        return cuentaContableDAO.getCuentaContable(nrocta);
    }

    public List<CuentaContable> getListaByBusqueda(String txtBusqueda, String imputable, boolean mostrarDeBaja, int cantMax) {
        return cuentaContableDAO.getListaByBusqueda(txtBusqueda, imputable, null, mostrarDeBaja, cantMax);
    }

    public List<CuentaContable> getListaByBusqueda(String txtBusqueda, String imputable, Map<String, String> filtro, boolean mostrarDeBaja, int cantMax) {
        return cuentaContableDAO.getListaByBusqueda(txtBusqueda, imputable, filtro, mostrarDeBaja, cantMax);
    }

    public List<CuentaContable> getListaByNivel(Integer nivel, boolean mostrarDeBaja) {

        return cuentaContableDAO.getListaByNivel(nivel, mostrarDeBaja);
    }

    public List<CuentaContable> getCuentasByCuentaMadre(CuentaContable cuenta) {

        return cuentaContableDAO.getCuentasByCuentaMadre(cuenta);
    }

    public String obtenerSiguienteCogido(CuentaContable cuentaMadre) {

        String nrocta = cuentaMadre == null ? null : cuentaMadre.getNrocta();

        return cuentaContableDAO.obtenerSiguienteCodigo(nrocta);

    }

    public void nuevoItemCentroCosto(CuentaContable cuentaContable) throws ExcepcionGeneralSistema {

        if (cuentaContable == null) {
            throw new ExcepcionGeneralSistema("No existe una cuenta contable seleccionada para agregarle un centro de costo");
        }

        if (cuentaContable.getCentrosCostos() == null) {
            cuentaContable.setCentrosCostos(new ArrayList<>());
        }

        CuentaContableCentroCosto itemCentroCosto = new CuentaContableCentroCosto();
        itemCentroCosto.setNroItem(cuentaContable.getCentrosCostos().size() + 1);
        itemCentroCosto.setCuentaContable(cuentaContable);

        cuentaContable.getCentrosCostos().add(itemCentroCosto);
        reorganizarNroItem(cuentaContable);
    }

    public void eliminarItemCentroCosto(CuentaContable cuentaContable, CuentaContableCentroCosto itemCentroCosto) throws ExcepcionGeneralSistema {

        if (itemCentroCosto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (CuentaContableCentroCosto item : cuentaContable.getCentrosCostos()) {

            if (item.getNroItem() >= 0) {

                if (item.getNroItem() == itemCentroCosto.getNroItem()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            CuentaContableCentroCosto remove = cuentaContable.getCentrosCostos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    cuentaContableDAO.eliminar(CuentaContableCentroCosto.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(cuentaContable);

    }

    public void reorganizarNroItem(CuentaContable cuentaContable) {

        //Reorganizamos los números de items
        int i;

        if (cuentaContable.getCentrosCostos() != null) {

            i = 1;
            for (CuentaContableCentroCosto item : cuentaContable.getCentrosCostos()) {
                item.setNroItem(i);
                i++;
            }
        }
    }

    public List<CuentaContableCentroCosto> getCentroCostoByCuenta(CuentaContable cuenta) {
        return cuentaContableDAO.getCentroCostoByCuenta(cuenta);
    }

    public int getCantidadRegistros() {

        return cuentaContableDAO.getCantidadRegistros(CuentaContable.class);
    }
}
