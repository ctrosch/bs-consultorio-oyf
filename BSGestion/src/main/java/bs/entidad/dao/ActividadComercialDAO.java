/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.dao;

import bs.entidad.modelo.ActividadComercial;
import bs.entidad.modelo.ActividadComercialPK;
import bs.entidad.modelo.TipoEntidad;
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
public class ActividadComercialDAO extends BaseDAO {

    public ActividadComercial getActividadComercial(String codigo, String tipo) {

        ActividadComercialPK id = new ActividadComercialPK(codigo, tipo);
        return getObjeto(ActividadComercial.class, id);
    }

    public List<ActividadComercial> getActividadComercialByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM ActividadComercial e "
                    + "WHERE "
                    + "      ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion ) "
                    + "     ) "
                    + (tipo == null ? " " : " AND e.codtip = :tipo ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (tipo != null) {
                q.setParameter("tipo", tipo.getCodigo());
            }

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getActividadComercialByBusqueda", e.getMessage());
            return new ArrayList<ActividadComercial>();
        }
    }

}
