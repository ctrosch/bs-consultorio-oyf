/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.rn;

import bs.facturacion.dao.CodigoCircuitoFacturacionDAO;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
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
public class CodigoCircuitoFacturacionRN implements Serializable {

    @EJB
    private CodigoCircuitoFacturacionDAO codigoCircuitoFacturacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CodigoCircuitoFacturacion guardar(CodigoCircuitoFacturacion c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (codigoCircuitoFacturacionDAO.getObjeto(CodigoCircuitoFacturacion.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un código de circuito con el código " + c.getCodigo());
            }
            codigoCircuitoFacturacionDAO.crear(c);

        } else {
            c = (CodigoCircuitoFacturacion) codigoCircuitoFacturacionDAO.editar(c);
        }

        return c;
    }

    public void eliminar(CodigoCircuitoFacturacion codigoCircuitoFacturacion) throws Exception {

        codigoCircuitoFacturacionDAO.eliminar(codigoCircuitoFacturacion);
    }

    public CodigoCircuitoFacturacion getCodigoCircuitoFacturacion(String codigo) {

        return codigoCircuitoFacturacionDAO.getObjeto(CodigoCircuitoFacturacion.class, codigo);

    }

    public List<CodigoCircuitoFacturacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoFacturacionDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoFacturacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoFacturacionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoFacturacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return codigoCircuitoFacturacionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

}
