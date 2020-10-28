/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.GrupoDAO;
import bs.seguridad.modelo.Grupo;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class GrupoRN implements Serializable {

    @EJB
    GrupoDAO grupoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Grupo grupo, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (grupoDAO.getObjeto(Grupo.class, grupo.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad " + grupo.getClass().getName() + " con el mismo código " + grupo.getCodigo());
            }
            grupoDAO.crear(grupo);
        } else {
            grupoDAO.editar(grupo);
        }
    }

    public Grupo getGrupo(String codigo) {

        return grupoDAO.getGrupo(codigo);
    }

    public void eliminar(Grupo grupo) throws Exception {

        grupoDAO.eliminar(grupo);
    }

    public List<Grupo> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantidadRegistros) {

        return grupoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, cantidadRegistros);
    }

    public List<Grupo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantidadRegistros) {

        return grupoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantidadRegistros);
    }

}
