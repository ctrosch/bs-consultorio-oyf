/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Localidad;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class LocalidadDAO extends BaseDAO {

    public Localidad getLocalidad(int id) {

        return getObjeto(Localidad.class, id);

    }

    public List<Localidad> getListaByBusqueda(String codPais, String codProv, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Localidad e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "
                    + "     ) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (codPais == null || codPais.isEmpty() ? " " : " AND e.pais.codigo = :codPais ")
                    + (codProv == null || codProv.isEmpty() ? " " : " AND e.provincia.codigo = :codProv ")
                    + "ORDER BY e.descripcion ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (codPais != null && !codPais.isEmpty()) {
                q.setParameter("codPais", codPais);
            }

            if (codProv != null && !codProv.isEmpty()) {
                q.setParameter("codProv", codProv);
            }

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Localidad>();
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
