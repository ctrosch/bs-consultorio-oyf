/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.produccion.dao.CodigoCircuitoProduccionDAO;
import bs.produccion.modelo.CodigoCircuitoProduccion;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Agustin
 */
@Stateless
public class CodigoCircuitoProduccionRN implements Serializable {

    @EJB
    private CodigoCircuitoProduccionDAO codigoCircuitoProduccionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CodigoCircuitoProduccion c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (codigoCircuitoProduccionDAO.getObjeto(CodigoCircuitoProduccion.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un código de circuito con el código " + c.getCodigo());
            }
            codigoCircuitoProduccionDAO.crear(c);

        } else {
            codigoCircuitoProduccionDAO.editar(c);
        }

    }

    public void eliminar(CodigoCircuitoProduccion codigoCircuitoFacturacion) throws Exception {

        codigoCircuitoProduccionDAO.eliminar(codigoCircuitoFacturacion);
    }

    public CodigoCircuitoProduccion getCodigoCircuitoProduccion(String codigo) {

        return codigoCircuitoProduccionDAO.getObjeto(CodigoCircuitoProduccion.class, codigo);

    }

    public List<CodigoCircuitoProduccion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoProduccionDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoProduccion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoProduccionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoProduccion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return codigoCircuitoProduccionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

}
