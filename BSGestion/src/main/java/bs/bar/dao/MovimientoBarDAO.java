/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.dao;

import bs.bar.modelo.MovimientoBar;
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
public class MovimientoBarDAO extends BaseDAO {

    public List<MovimientoBar> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoBar m ";
            if (soloPendientes) {
                sQuery += "WHERE EXISTS(SELECT i FROM ItemMovimientoBar i WHERE i.movimiento.id = m.id and i.cantidadPendiente > 0) ";
            }
            sQuery += generarStringFiltro(filtro, "m", !soloPendientes);
            sQuery += " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de bar");
            return new ArrayList<MovimientoBar>();
        }
    }

    public MovimientoBar getMovimientoBarById(Integer id) {
        try {
            return getEm().find(MovimientoBar.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }

    public MovimientoBar getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoBar m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoBar) q.getSingleResult();

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de compra");
            return null;
        }

    }

    public MovimientoBar getMovimientoByPuntoVentaNumeroOriginal(String nrocta, String puntoVentaOriginal, String numeroOriginal) {

        try {
            String sQuery = "SELECT m FROM MovimientoBar m "
                    + " WHERE m.puntoVentaOriginal = :puntoVentaOriginal "
                    + " AND m.numeroOriginal = :numeroOriginal "
                    + " AND m.proveedor.nrocta = :nrocta ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nrocta", nrocta);
            q.setParameter("puntoVentaOriginal", puntoVentaOriginal);
            q.setParameter("numeroOriginal", numeroOriginal);

            q.setMaxResults(1);
            return (MovimientoBar) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos de proveedor " + e);
            return null;
        }
    }

}
