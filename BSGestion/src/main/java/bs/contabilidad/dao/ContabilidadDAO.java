/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.dao;

import bs.contabilidad.modelo.ItemMovimientoContabilidad;
import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class ContabilidadDAO extends BaseDAO {

    public MovimientoContabilidad editar(MovimientoContabilidad m) {
        return (MovimientoContabilidad) super.editar(m);
    }

    public List<MovimientoContabilidad> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoContabilidad m ";            
            sQuery += generarStringFiltro(filtro, "m", true);
            sQuery += " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de contabilidad");
            return new ArrayList<MovimientoContabilidad>();
        }
    }

    public MovimientoContabilidad getMovimientoContabilidadById(Integer id) {
        try {
            return getEm().find(MovimientoContabilidad.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }

    public MovimientoContabilidad getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoContabilidad m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoContabilidad) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de contabilidad");
            return null;
        }

    }

    public MovimientoContabilidad getMovimiento(Integer id) {

        return getObjeto(MovimientoContabilidad.class, id);
    }
    
    
    public ItemMovimientoContabilidad getItem(Integer id) {

        return getObjeto(ItemMovimientoContabilidad.class, id);
    }
    
}
