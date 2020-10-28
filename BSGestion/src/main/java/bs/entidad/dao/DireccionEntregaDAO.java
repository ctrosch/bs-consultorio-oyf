/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.dao;

import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.DireccionEntregaEntidadPK;
import bs.global.dao.BaseDAO;
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
public class DireccionEntregaDAO extends BaseDAO {

    public DireccionEntregaEntidad getDireccionEntregaEntidad(String codigo, String nroCta) {

        DireccionEntregaEntidadPK id = new DireccionEntregaEntidadPK(codigo, nroCta);
        return getObjeto(DireccionEntregaEntidad.class, id);
    }

    /**
     *
     * @param filtro
     * @param txtBusqueda
     * @param nroCuenta
     * @param mostrarDebaja
     * @param cantMax
     * @return
     */
    public List<DireccionEntregaEntidad> getListaByBusqueda(
            Map<String, String> filtro,
            String txtBusqueda,
            String nroCuenta, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM DireccionEntregaEntidad e "
                    + "WHERE "
                    + "      ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion ) "
                    + "     ) "
                    + (nroCuenta == null || nroCuenta.isEmpty() ? " " : " AND e.nrocta = :nrocta ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (nroCuenta != null && !nroCuenta.isEmpty()) {
                q.setParameter("nrocta", nroCuenta);
            }

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<DireccionEntregaEntidad>();
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getDireccionEntregaEntidadByBusqueda", e.getMessage());
            return new ArrayList<DireccionEntregaEntidad>();
        }
    }

}
