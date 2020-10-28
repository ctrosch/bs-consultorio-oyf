/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.taller.dao.CodigoCircuitoTallerDAO;
import bs.taller.modelo.CodigoCircuitoTaller;
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
public class CodigoCircuitoTallerRN implements Serializable {

    @EJB
    private CodigoCircuitoTallerDAO codigoCircuitoServiceDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CodigoCircuitoTaller c, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (codigoCircuitoServiceDAO.getObjeto(CodigoCircuitoTaller.class, c.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un código de circuito con el código " + c.getCodigo());
            }
            codigoCircuitoServiceDAO.crear(c);

        } else {
            c = (CodigoCircuitoTaller) codigoCircuitoServiceDAO.editar(c);
        }

    }

    public void eliminar(CodigoCircuitoTaller codigoCircuitoService) throws Exception {

        codigoCircuitoServiceDAO.eliminar(codigoCircuitoService);
    }

    public CodigoCircuitoTaller getCodigoCircuitoService(String codigo) {

        return codigoCircuitoServiceDAO.getObjeto(CodigoCircuitoTaller.class, codigo);

    }

    public List<CodigoCircuitoTaller> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoServiceDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoTaller> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return codigoCircuitoServiceDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<CodigoCircuitoTaller> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return codigoCircuitoServiceDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

}
