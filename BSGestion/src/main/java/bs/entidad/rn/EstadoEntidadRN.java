/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.EstadoEntidadDAO;
import bs.entidad.modelo.EstadoEntidad;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class EstadoEntidadRN implements Serializable {

    @EJB
    private EstadoEntidadDAO estadoEntidadDAO;

    public EstadoEntidad getEstadoEntidad(String codigo) {

        return estadoEntidadDAO.getObjeto(EstadoEntidad.class, codigo);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EstadoEntidad guardar(EstadoEntidad e, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (estadoEntidadDAO.getObjeto(EstadoEntidad.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un estado con el código " + e.getCodigo());
            }
            estadoEntidadDAO.crear(e);
        } else {
            e = (EstadoEntidad) estadoEntidadDAO.editar(e);
        }
        return e;
    }

    public void eliminar(EstadoEntidad estadoEntidad) throws Exception {

        estadoEntidadDAO.eliminar(estadoEntidad);
    }

    public List<EstadoEntidad> getEstadoEntidadByBusqueda(boolean mostrarDebaja) {

        return estadoEntidadDAO.getListaByBusqueda(null, "", false, 15);
    }

    public List<EstadoEntidad> getEstadoEntidadByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return estadoEntidadDAO.getListaByBusqueda(null, txtBusqueda, false, 15);
    }

}
