/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.dao;

import bs.bar.modelo.Reserva;
import bs.bar.modelo.Salon;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class ReservaDAO extends BaseDAO {

    public Reserva getReserva(String cod) {
        return getObjeto(Reserva.class, cod);
    }

    public List<Reserva> getListaByBusqueda(Date fechaMovimiento, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Reserva e "
                    + " WHERE (e.nombreContacto LIKE :nombreContacto OR e.telefono LIKE :telefon) "
                    + " AND (e.fechaMovimiento = :fechaMovimiento) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.fechaMovimiento DESC";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("fechaMovimiento", fechaMovimiento);
            q.setParameter("nombreContacto", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("telefon", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {

            return new ArrayList<Reserva>();
        }
    }

    public int getTotalReservasByFecha(Date fechaMovimiento, Salon salon) {

        try {

            String sQuery = "SELECT count(e) FROM Reserva e "
                    + " WHERE (e.fechaMovimiento = :fechaMovimiento) "
                    + (salon != null ? " AND e.salon.codigo = :salon" : "")
                    + " AND e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("fechaMovimiento", fechaMovimiento);
            if (salon != null) {
                q.setParameter("salon", salon.getCodigo());
            }

            return ((Long) q.getSingleResult()).intValue();

        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalPersonasByFecha(Date fechaMovimiento, Salon salon) {

        try {

            String sQuery = "SELECT sum(e.cantidadPersonas) FROM Reserva e "
                    + " WHERE (e.fechaMovimiento = :fechaMovimiento) "
                    + (salon != null ? " AND e.salon.codigo = :salon" : "")
                    + " AND e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("fechaMovimiento", fechaMovimiento);

            if (salon != null) {
                q.setParameter("salon", salon.getCodigo());
            }

            return (q.getSingleResult() != null ? ((Long) q.getSingleResult()).intValue() : 0);

        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getTotalPersonasByFecha", e.getCause());
            return 0;
        }
    }

    public List<String> getContactos(String query) {

        try {

            String sQuery = "SELECT e.nombreContacto FROM Reserva e "
                    + " WHERE (e.nombreContacto Like :nombreContacto) "
                    + " AND e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("nombreContacto", "%" + query.replaceAll(" ", "%") + "%");

            q.setMaxResults(10);

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<String>();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getContactos", e.getCause());
            return new ArrayList<String>();
        }

    }
}
