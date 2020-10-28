/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.dao;

import bs.entidad.modelo.EntidadComercial;
import bs.global.dao.BaseDAO;
import bs.salud.modelo.ReservaTurno;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Guillermo
 */
@Stateless
public class ReservaTurnoDAO extends BaseDAO {

    public ReservaTurno getReservaTurno(Integer cod) {
        return getObjeto(ReservaTurno.class, cod);
    }

    public List<ReservaTurno> getListaByBusqueda(Date fechaMovimiento, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM ReservaTurno e "
                    + " WHERE ((e.paciente.nombre LIKE :nombrePaciente )"
                    + "OR (e.paciente.apellido LIKE :apellidoPaciente) "
                    + "OR (e.paciente.nrodoc LIKE :nrodocPaciente) "
                    + " AND (e.fechaMovimiento = :fechaMovimiento)) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.fechaMovimiento DESC";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("fechaMovimiento", fechaMovimiento);
            q.setParameter("nombrePaciente", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("apellidoPaciente", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nrodocPaciente", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {

            return new ArrayList<ReservaTurno>();
        }
    }

    public List<ReservaTurno> getListaByBusqueda(EntidadComercial profesional, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM ReservaTurno e "
                    + " WHERE (e.profesional.nrocta LIKE :nrocta )"
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.fechaMovimiento DESC";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("nrocta", profesional.getNrocta());

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }
            return q.getResultList();

        } catch (Exception e) {

            return new ArrayList<ReservaTurno>();

        }
    }

}
