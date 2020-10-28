/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.rn;

import bs.tesoreria.dao.ItemMovimientoTesoreriaDAO;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaCentroCosto;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaSubcuenta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ItemMovimientoTesoreriaRN implements Serializable {

    @EJB
    private ItemMovimientoTesoreriaDAO itemCentroCostoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ItemMovimientoTesoreria guardar(ItemMovimientoTesoreria e) throws Exception {

        if (e.getId() == null) {
            itemCentroCostoDAO.crear(e);
        } else {
            e = (ItemMovimientoTesoreria) itemCentroCostoDAO.editar(e);
        }

        validarImputacionCentroCosto(e);
        return e;
    }

    public void eliminar(ItemMovimientoTesoreria v) throws Exception {

        itemCentroCostoDAO.eliminar(v);
    }

    public ItemMovimientoTesoreria getItemMovimientoTesoreria(Integer id) {
        return itemCentroCostoDAO.getObjeto(ItemMovimientoTesoreria.class, id);
    }

    public List<ItemMovimientoTesoreria> getListaByBusqueda(Map<String, String> filtro) {
        return itemCentroCostoDAO.getListaByBusqueda(filtro, 0);
    }

    public void calcularImportes(ItemMovimientoTesoreria item) {

        //Calculamos la imputación en dimensiones
        if (item.getItemsCentroCosto() != null) {

            for (ItemMovimientoTesoreriaCentroCosto cc : item.getItemsCentroCosto()) {

                if (cc.getSubCuentas() != null) {

                    for (ItemMovimientoTesoreriaSubcuenta is : cc.getSubCuentas()) {

                        is.setDebhab(item.getDebeHaber());

                        if (is.getPorcentaje() != null && is.getPorcentaje().compareTo(BigDecimal.ZERO) > 0) {
                            is.setImporte(item.getImporte().multiply(is.getPorcentaje()).setScale(4, RoundingMode.HALF_UP).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        }
                    }
                }
            }
        }

        validarImputacionCentroCosto(item);
    }

    public void validarImputacionCentroCosto(ItemMovimientoTesoreria i) {

//        System.err.println("valida imputación");
        BigDecimal acumulado = null;

        for (ItemMovimientoTesoreriaCentroCosto cc : i.getItemsCentroCosto()) {

            cc.setConError(false);

            acumulado = BigDecimal.ZERO;

            if (cc.getSubCuentas() != null) {

                for (ItemMovimientoTesoreriaSubcuenta is : cc.getSubCuentas()) {

                    acumulado = acumulado.add(is.getImporte());
                }
            }

            if (i.getImporte().compareTo(acumulado) != 0) {
                cc.setConError(true);
            }
        }

    }

    public void validarImputacionCentroCostoLista(List<ItemMovimientoTesoreria> lista) {

        if (lista == null) {
            return;
        }

        for (ItemMovimientoTesoreria i : lista) {
            validarImputacionCentroCosto(i);
        }

    }

}
