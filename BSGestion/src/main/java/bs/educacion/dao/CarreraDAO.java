/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.Carrera;
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
public class CarreraDAO extends BaseDAO {

    public Carrera getCarrera(String id) {
        return getObjeto(Carrera.class, id);
    }

    public List<Carrera> getLista() {
        return getLista(Carrera.class, true, -1, -1);
    }

    public List<Carrera> getCarreraByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Carrera e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.titulo LIKE :titulo "
                    + " OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("titulo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCarreraByBusqueda", e);
            return new ArrayList<Carrera>();
        }
    }

    public int getMaxCodigo(String codUnidadNegocio) {

        try {
            String sQuery = "select CAST(MAX(ed_carrera.codigo) AS SIGNED)+1  from ed_carrera where codigo < 900000 and UNINEG = '" + codUnidadNegocio + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }

    public int getCantidadRegistros() {

        try {
            String sQuery = "SELECT COUNT(e) FROM Carrera e WHERE e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCantidadRegistros", e);
            return 0;
        }

    }
}
