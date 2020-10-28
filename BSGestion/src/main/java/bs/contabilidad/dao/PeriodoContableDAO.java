/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.dao;

import bs.contabilidad.modelo.PeriodoContable;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Stateless
public class PeriodoContableDAO extends BaseDAO {

    public PeriodoContable getPeriodoContable(String id) {
        return getObjeto(PeriodoContable.class, id);
    }

    public List<PeriodoContable> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM PeriodoContable e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<PeriodoContable>();
        }
    }

    public PeriodoContable getPeriodoActivo(String periodoInterno) {

        try {
            String sQuery = "SELECT o FROM PeriodoContable o "
                    + " WHERE o.estado = 'A' "
                    + " AND o.periodoInterno = :periodoInterno"
                    + " ORDER BY o.fechaInicio DESC ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("periodoInterno", periodoInterno);
            q.setMaxResults(1);

            return (PeriodoContable) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;

        } catch (Exception e) {
            System.err.println("No se puede obtener objeto PeriodoContable - " + e);
            return null;
        }
    }

    public PeriodoContable getPeriodoByFecha(String periodoInterno, Date fechaDesde) {

        try {
            String sQuery = "SELECT o FROM PeriodoContable o "
                    + " WHERE o.estado = 'A' "
                    + " AND o.periodoInterno = :periodoInterno"
                    + " AND :parametro1 BETWEEN o.fechaInicio AND o.fechaFin "
                    + " ORDER BY o.fechaInicio DESC ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("periodoInterno", periodoInterno);
            q.setParameter("parametro1", fechaDesde, TemporalType.DATE);

            return (PeriodoContable) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;

        } catch (Exception e) {
            System.err.println("No se puede obtener objeto PeriodoContable - " + e);
            return null;
        }
    }

}
