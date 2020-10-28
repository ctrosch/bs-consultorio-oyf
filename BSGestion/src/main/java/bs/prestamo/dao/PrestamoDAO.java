/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.dao;

import bs.global.dao.BaseDAO;
import bs.prestamo.modelo.ItemPrestamo;
import bs.prestamo.modelo.Prestamo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class PrestamoDAO extends BaseDAO {

    public Prestamo editar(Prestamo m) {
        return (Prestamo) super.editar(m);
    }

    public List<Prestamo> getListaByBusqueda(String txtBusqueda, Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT m FROM Prestamo m "
                    + "WHERE "
                    + "      ((m.destinatario.razonSocial LIKE :razonSocial) "
                    + "  OR  (m.destinatario.nombreFantasia LIKE :nombreFantasia ) "
                    + "  OR  (m.destinatario.nrocta LIKE :nrocta ) "
                    + "  OR  (m.destinatario.email LIKE :email )"
                    + "  OR  (m.destinatario.nrodoc LIKE :nrodoc )"
                    + "  OR  (m.destinatario.direccion LIKE :direccion )"
                    + "  OR  (m.codigo LIKE :codigo )"
                    + "     ) ";
            sQuery += generarStringFiltro(filtro, "m", false);
            sQuery += " ORDER BY m.fechaEntrega DESC, m.codigo DESC";

            Query q = getEm().createQuery(sQuery);

//            System.err.println("sQuery " + sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("razonSocial", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nombreFantasia", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nrocta", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("email", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nrodoc", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("direccion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener préstamos");
            return new ArrayList<Prestamo>();
        }
    }

    public Prestamo getPrestamo(Integer id) {
        try {
            return getEm().find(Prestamo.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }

    public Prestamo getPrestamo(String codigo) {

        try {
            String sQuery = "SELECT m FROM Prestamo m "
                    + "WHERE m.codigo = :codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", codigo);

            q.setMaxResults(1);
            return (Prestamo) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener préstamo");
            return null;
        }
    }

    public List<ItemPrestamo> getCuotasPendientes(Prestamo prestamo) {

        if (prestamo == null) {
            return null;
        }

        try {
            String sQuery = "SELECT i FROM ItemPrestamo i"
                    + "WHERE i.prestamo.id = :idPrestamo "
                    + "AND (i.totalCuota - i.interesCancelado - i.capitalCancelado - i.gastosOtorgamientoCancelado) > 0 ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("idPrestamo", prestamo.getId());

            return q.getResultList();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener cuotas pendientes");
            return null;
        }
    }

    public int getProximoCodigo() {

        try {
//            String sQuery = "select IFNULL(CAST(LEFT(MAX(pr_prestamo.CODIGO),4) AS SIGNED),0) +1  "
//                    + " from pr_prestamo"
//                    + " WHERE 1=1";

            String sQuery = "SELECT MAX(pr_prestamo.ID)+1  from pr_prestamo ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getProximoCodigoProducto", e.getMessage());
            return 0;
        }
    }

    public Prestamo vaciarDetalleCuotas(Prestamo prestamo) {

        if (prestamo == null) {
            return null;
        }

        try {
            String sQuery = "DELETE FROM ItemPrestamo i "
                    + " WHERE i.prestamo.id = :idPrestamo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("idPrestamo", prestamo.getId());

            q.executeUpdate();

            return getPrestamo(prestamo.getId());

        } catch (NoResultException e) {

            return prestamo;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error vaciarDetalleCuotas");
            return prestamo;
        }

    }
}
