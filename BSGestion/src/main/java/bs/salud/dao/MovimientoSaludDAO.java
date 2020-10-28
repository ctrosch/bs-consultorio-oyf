/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.dao;

import bs.entidad.modelo.EntidadComercial;
import bs.global.dao.BaseDAO;
import bs.salud.modelo.MovimientoSalud;
import java.util.ArrayList;
import java.util.Date;
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
public class MovimientoSaludDAO extends BaseDAO {

    public MovimientoSalud editar(MovimientoSalud s) {
        return (MovimientoSalud) super.editar(s);
    }

    public List<MovimientoSalud> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT e FROM MovimientoSalud e ";
            sQuery += generarStringFiltro(filtro, "e", true);
            sQuery += " ORDER BY e.fechaMovimiento DESC, e.horaMovimiento ASC, e.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Salud ");
            return new ArrayList<MovimientoSalud>();
        }
    }

    public MovimientoSalud getMovimientoSaludById(Integer id) {
        try {
            return getEm().find(MovimientoSalud.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró el movimiento de Salud: " + id);
            return null;
        }
    }

    public MovimientoSalud getMovimientoSalud(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT e FROM MovimientoSalud e "
                    + "WHERE e.formulario.codigo = :codFormulario "
                    + "AND e.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoSalud) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Salud ");
            return null;
        }

    }

    public List<MovimientoSalud> getMovimientoSaludEspera(Date fechaMovimiento, EntidadComercial profesional) {

        try {
            String sQuery = "SELECT e FROM MovimientoSalud e "
                    + "WHERE "
                    + "e.fechaMovimiento = :fechaMovimiento "
                    + "AND e.profesional.nrocta LIKE :nrocta "
                    + "AND e.estado.codigo IN ('E','R','H') "
                    + "AND e.horaMovimiento IS NOT NULL ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("fechaMovimiento", fechaMovimiento);
            q.setParameter("nrocta", profesional.getNrocta());

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Salud en espera ");
            return new ArrayList<MovimientoSalud>();
        }

    }

    public List<MovimientoSalud> getMovimientoSaludEspera(EntidadComercial profesional) {

        try {
            String sQuery = "SELECT e FROM MovimientoSalud e "
                    + "WHERE "
                    + "e.profesional.nrocta LIKE :nrocta "
                    + "AND ( e.estado.codigo = 'E' "
                    + "OR e.estado.codigo = 'R') ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nrocta", profesional.getNrocta());

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los movimientos de Salud en espera ");
            return new ArrayList<MovimientoSalud>();
        }

    }

    public MovimientoSalud getMovimientoSalud(Integer id) {

        return getObjeto(MovimientoSalud.class, id);
    }

    public boolean controlarSiEstaTurnoDado(Date fechaMovimiento, Date horaMovimiento, EntidadComercial profesional) {
        try {

            String sQuery = "SELECT count(e) FROM MovimientoSalud e "
                    + "WHERE "
                    + "e.fechaMovimiento = :fechaMovimiento "
                    + "AND e.horaMovimiento = :horaMovimiento "
                    + "AND e.profesional.nrocta LIKE :nrocta "
                    + "AND e.estado.codigo IN ('E','R','H')";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("fechaMovimiento", fechaMovimiento);
            q.setParameter("horaMovimiento", horaMovimiento);
            q.setParameter("nrocta", profesional.getNrocta());

            return ((Long) q.getSingleResult()).intValue() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener el turno dado");
            return true;
        }
    }

}
