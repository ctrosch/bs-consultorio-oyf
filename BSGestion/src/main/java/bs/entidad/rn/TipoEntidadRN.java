/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.TipoEntidadDAO;
import bs.entidad.modelo.TipoEntidad;
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
public class TipoEntidadRN implements Serializable {

    @EJB
    private TipoEntidadDAO estadoEntidadDAO;

    public TipoEntidad getTipoEntidad(String codigo) {

        return estadoEntidadDAO.getObjeto(TipoEntidad.class, codigo);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoEntidad e, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (estadoEntidadDAO.getObjeto(TipoEntidad.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un tipo con el código " + e.getCodigo());
            }
            estadoEntidadDAO.crear(e);
        } else {
            estadoEntidadDAO.editar(e);
        }
    }

    public void eliminar(TipoEntidad estadoEntidad) throws Exception {

        estadoEntidadDAO.eliminar(estadoEntidad);
    }

    public List<TipoEntidad> getListaByBusqueda(boolean mostrarDebaja) {

        return estadoEntidadDAO.getListaByBusqueda(null, "", false, 15);
    }

    public List<TipoEntidad> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return estadoEntidadDAO.getListaByBusqueda(null, txtBusqueda, false, 15);
    }

}
