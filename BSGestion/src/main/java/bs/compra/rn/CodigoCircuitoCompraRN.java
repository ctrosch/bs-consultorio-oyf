/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.rn;

import bs.compra.dao.CodigoCircuitoCompraDAO;
import bs.compra.modelo.CodigoCircuitoCompra;
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
 * @author Claudio
 */
@Stateless
public class CodigoCircuitoCompraRN implements Serializable {

    @EJB
    private CodigoCircuitoCompraDAO codigoCircuitoCompraDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CodigoCircuitoCompra c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (codigoCircuitoCompraDAO.getObjeto(CodigoCircuitoCompra.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un código de circuito con el código " + c.getCodigo());
            }
            codigoCircuitoCompraDAO.crear(c);

        } else {
            codigoCircuitoCompraDAO.editar(c);
        }

    }

    public void eliminar(CodigoCircuitoCompra codigoCircuitoFacturacion) throws Exception {

        codigoCircuitoCompraDAO.eliminar(codigoCircuitoFacturacion);
    }

    public CodigoCircuitoCompra getCodigoCircuitoCompra(String codigo) {

        return codigoCircuitoCompraDAO.getObjeto(CodigoCircuitoCompra.class, codigo);

    }

    public List<CodigoCircuitoCompra> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoCompraDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoCompra> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoCompraDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoCompra> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return codigoCircuitoCompraDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

}
