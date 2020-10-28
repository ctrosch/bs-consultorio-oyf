/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.ItemMovimientoEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.global.dao.BaseDAO;
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
public class MovimientoEducacionDAO extends BaseDAO {

    public MovimientoEducacion editar(MovimientoEducacion e) {
        return (MovimientoEducacion) super.editar(e);
    }

    public List<MovimientoEducacion> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT e FROM MovimientoEducacion e ";
            sQuery += generarStringFiltro(filtro, "e", true);
            sQuery += " ORDER BY e.fechaMovimiento DESC, e.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Educación ");
            return new ArrayList<MovimientoEducacion>();
        }
    }

    public boolean controlarAlumnoSiEstaInscripto(String legAlumno, String codCarrera, String codCurso) {
        try {

            String sQuery = "SELECT count(e) FROM MovimientoEducacion e "
                    + " WHERE e.alumno.nrocta = '" + legAlumno + "' "
                    + " AND e.carrera.codigo = '" + codCarrera + "' "
                    + " AND e.curso.codigo = '" + codCurso + "' "
                    + " AND e.movimientoReversion IS NULL "
                    + " AND e.comprobante.tipoComprobante = 'IN' ";

            Query q = getEm().createQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue() > 0;

        } catch (Exception e) {
//            log.log(Level.SEVERE, "controlarAlumnoSiEstaInscripto", e.getCause());
            return true;
        }
    }

    public MovimientoEducacion getMovimientoEducacionById(Integer id) {
        try {
            return getEm().find(MovimientoEducacion.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró el movimiento de Educación: " + id);
            return null;
        }
    }

    public MovimientoEducacion getMovimientoEducacion(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT e FROM MovimientoEducacion e "
                    + "WHERE e.formulario.codigo = :codFormulario "
                    + "AND e.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoEducacion) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Educacón ");
            return null;
        }

    }

    public MovimientoEducacion getMovimientoEducacion(Integer id) {

        return getObjeto(MovimientoEducacion.class, id);
    }

    public ItemMovimientoEducacion getItemMovimientoEducacion(Integer id) {

        return getObjeto(ItemMovimientoEducacion.class, id);
    }

}
