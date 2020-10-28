/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.tesoreria.dao.CuentaTesoreriaDAO;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
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
public class CuentaTesoreriaRN implements Serializable {

    @EJB
    private CuentaTesoreriaDAO cuentaTesoreriaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CuentaTesoreria e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (cuentaTesoreriaDAO.getObjeto(CuentaTesoreria.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + e.getCodigo());
            }
            cuentaTesoreriaDAO.crear(e);
        } else {
            cuentaTesoreriaDAO.editar(e);
        }
    }

    public void eliminar(CuentaTesoreria v) throws Exception {

        cuentaTesoreriaDAO.eliminar(v);
    }

    public CuentaTesoreria duplicar(CuentaTesoreria c) throws Exception {

        if (c == null) {
            throw new ExcepcionGeneralSistema("Cuenta de Tesorería nula, nada para duplicar");
        }

        CuentaTesoreria cuentaTesoreria = c.clone();

        cuentaTesoreria.setDescripcion(c.getDescripcion() + "( Duplicado)");

        return cuentaTesoreria;
    }

    public CuentaTesoreria getCuentaTesoreria(String codigo) {
        return cuentaTesoreriaDAO.getObjeto(CuentaTesoreria.class, codigo);
    }

    public List<CuentaTesoreria> getListaByBusqueda(String codTipo, Map<String, String> filtro) {
        return cuentaTesoreriaDAO.getListaByBusqueda(codTipo, filtro, "", false, 20);
    }

    public List<CuentaTesoreria> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDebaja) {

        return cuentaTesoreriaDAO.getListaByBusqueda(codTipo, null, txtBusqueda, mostrarDebaja, 20);
    }

    public List<CuentaTesoreria> getListaByBusqueda(String codTipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cant) {

        return cuentaTesoreriaDAO.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cant);
    }

    public List<CuentaTesoreria> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDebaja, int cant) {

        return cuentaTesoreriaDAO.getListaByBusqueda(codTipo, null, txtBusqueda, mostrarDebaja, cant);
    }

    public List<CuentaTesoreria> getListaByBusqueda(String codTipo, String txtBusqueda) {

        return cuentaTesoreriaDAO.getListaByBusqueda(codTipo, null, txtBusqueda, false, 50);

    }

    public List<ItemSaldoPendienteCuentaTesoreria> getSaldosPendienteByCuenta(String nroCuenta) {

        return cuentaTesoreriaDAO.getSaldosPendienteByCuentaTesoreria(nroCuenta);

    }

}
