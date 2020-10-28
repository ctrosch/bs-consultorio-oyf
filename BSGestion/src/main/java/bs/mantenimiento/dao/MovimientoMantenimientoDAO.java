/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.dao;

import bs.global.dao.BaseDAO;
import bs.mantenimiento.modelo.ItemMovimientoMantenimientoActividad;
import bs.mantenimiento.modelo.MovimientoMantenimiento;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Guillermo
 */
@Stateless
public class MovimientoMantenimientoDAO extends BaseDAO {

    public MovimientoMantenimiento editar(MovimientoMantenimiento e) {
        return (MovimientoMantenimiento) super.editar(e);
    }

    public List<MovimientoMantenimiento> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT e FROM MovimientoMantenimiento e ";
            sQuery += generarStringFiltro(filtro, "e", true);
            sQuery += " ORDER BY e.fechaMovimiento DESC, e.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Mantenimiento ");
            return new ArrayList<MovimientoMantenimiento>();
        }
    }

    public MovimientoMantenimiento getMovimientoMantenimientoById(Integer id) {
        try {
            return getEm().find(MovimientoMantenimiento.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró el movimiento de Mantenimiento: " + id);
            return null;
        }
    }

    public MovimientoMantenimiento getMovimientoMantenimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT e FROM MovimientoMantenimiento e "
                    + "WHERE e.formulario.codigo = :codFormulario "
                    + "AND e.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoMantenimiento) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Mantenimiento ");
            return null;
        }

    }

    public MovimientoMantenimiento getMovimientoMantenimiento(Integer id) {

        return getObjeto(MovimientoMantenimiento.class, id);
    }

    public ItemMovimientoMantenimientoActividad getItemMovimientoMantenimiento(Integer id) {

        return getObjeto(ItemMovimientoMantenimientoActividad.class, id);
    }

}
