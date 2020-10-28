/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.CodigoCircuitoEducacionDAO;
import bs.educacion.modelo.CodigoCircuitoEducacion;
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
public class CodigoCircuitoEducacionRN implements Serializable {

    @EJB
    private CodigoCircuitoEducacionDAO codigoCircuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CodigoCircuitoEducacion c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (codigoCircuitoDAO.getObjeto(CodigoCircuitoEducacion.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un código de circuito con el código " + c.getCodigo());
            }
            codigoCircuitoDAO.crear(c);

        } else {
            codigoCircuitoDAO.editar(c);
        }

    }

    public void eliminar(CodigoCircuitoEducacion codigoCircuitoFacturacion) throws Exception {

        codigoCircuitoDAO.eliminar(codigoCircuitoFacturacion);
    }

    public CodigoCircuitoEducacion getCodigoCircuitoEducacion(String codigo) {

        return codigoCircuitoDAO.getObjeto(CodigoCircuitoEducacion.class, codigo);

    }

    public List<CodigoCircuitoEducacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return codigoCircuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

}
