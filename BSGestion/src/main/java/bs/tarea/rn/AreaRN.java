/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.tarea.dao.AreaDAO;
import bs.tarea.modelo.Area;
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
public class AreaRN implements Serializable {

    @EJB
    private AreaDAO areaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Area d, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (areaDAO.getObjeto(Area.class, d.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + d.getCodigo());
            }
            areaDAO.crear(d);

        } else {
            areaDAO.editar(d);
        }
    }

    public void eliminar(Area deposito) throws Exception {

        areaDAO.eliminar(deposito);
    }

    public List<Area> getLista(boolean mostrarDebaja) {

        return areaDAO.getListaByBusqueda("", mostrarDebaja, 0);
    }

    public Area getArea(String codigo) {

        return areaDAO.getArea(codigo);
    }

    public List<Area> getLista() {
        return areaDAO.getListaByBusqueda("", false, 0);
    }

    public List<Area> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return areaDAO.getListaByBusqueda(txtBusqueda, mostrarDeBaja, cantMax);
    }
}
