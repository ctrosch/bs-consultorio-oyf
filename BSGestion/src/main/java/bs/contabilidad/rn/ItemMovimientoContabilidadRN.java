/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.ItemMovimientoContabilidadDAO;
import bs.contabilidad.modelo.ItemMovimientoContabilidad;
import bs.contabilidad.modelo.ItemMovimientoContabilidadCentroCosto;
import bs.contabilidad.modelo.ItemMovimientoContabilidadSubcuenta;
import bs.global.util.Numeros;
import java.io.Serializable;
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
public class ItemMovimientoContabilidadRN implements Serializable {

    @EJB
    private ItemMovimientoContabilidadDAO itemMovimientoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ItemMovimientoContabilidad guardar(ItemMovimientoContabilidad e) throws Exception {

        if (e.getId() == null) {
            itemMovimientoDAO.crear(e);
        } else {
            e = (ItemMovimientoContabilidad) itemMovimientoDAO.editar(e);
        }

        validarImputacionCentroCosto(e);
        return e;
    }

    public void eliminar(ItemMovimientoContabilidad v) throws Exception {

        itemMovimientoDAO.eliminar(v);
    }

    public ItemMovimientoContabilidad getItemMovimientoContabilidad(Integer id) {
        return itemMovimientoDAO.getObjeto(ItemMovimientoContabilidad.class, id);
    }

    public List<ItemMovimientoContabilidad> getListaByBusqueda(Map<String, String> filtro) {
        return itemMovimientoDAO.getListaByBusqueda(filtro, 0);
    }

    public void calcularImportes(ItemMovimientoContabilidad item) {

        //Calculamos la imputación en dimensiones
        if (item.getItemsCentroCosto() != null) {

            for (ItemMovimientoContabilidadCentroCosto cc : item.getItemsCentroCosto()) {

                if (cc.getSubCuentas() != null) {

                    for (ItemMovimientoContabilidadSubcuenta is : cc.getSubCuentas()) {

                        is.setDebhab(item.getDebeHaber());

                        if (is.getPorcentaje() > 0) {
                            is.setImporte(Numeros.redondear(Math.abs(item.getImporteDebe() - item.getImporteHaber()) * is.getPorcentaje() / 100, 4));
                        }
                    }
                }
            }
        }

        validarImputacionCentroCosto(item);
    }

    public void validarImputacionCentroCosto(ItemMovimientoContabilidad i) {

//        System.err.println("valida imputación");
        double acumulado;

        for (ItemMovimientoContabilidadCentroCosto cc : i.getItemsCentroCosto()) {

            cc.setConError(false);

            acumulado = 0;

            if (cc.getSubCuentas() != null) {

                for (ItemMovimientoContabilidadSubcuenta is : cc.getSubCuentas()) {

                    acumulado = acumulado + is.getImporte();
                }
            }

            if ((i.getImporteDebe() - i.getImporteHaber()) != 0) {
                cc.setConError(true);
            }
        }

    }

    public void validarImputacionCentroCostoLista(List<ItemMovimientoContabilidad> lista) {

        if (lista == null) {
            return;
        }

        for (ItemMovimientoContabilidad i : lista) {
            validarImputacionCentroCosto(i);
        }

    }

}
