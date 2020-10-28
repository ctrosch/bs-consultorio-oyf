/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.PlanEstudio;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class PlanEstudioDAO extends BaseDAO {

    public PlanEstudio getPlanEstudio(String id) {
        return getObjeto(PlanEstudio.class, id);
    }

    public List<PlanEstudio> getLista() {
        return getLista(PlanEstudio.class, true, -1, -1);
    }

    public List<PlanEstudio> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM PlanEstudio e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.titulo LIKE :titulo) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("titulo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<PlanEstudio>();
        }
    }

    public int getMaxCodigo(String codCarrera) {

        try {
            String sQuery = "select CAST(MAX(ed_plan_estudio.codigo) AS SIGNED)+1  from ed_plan_estudio where codigo < 900000 and CODCAR = '" + codCarrera + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }

    public int getCantidadRegistros() {

        try {
            String sQuery = "SELECT COUNT(e) FROM PlanEstudio e WHERE e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCantidadRegistros", e);
            return 0;
        }

    }
}
