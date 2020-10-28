/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.dao;

import bs.global.dao.BaseDAO;
import bs.taller.modelo.EquipoModelo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class EquipoModeloDAO extends BaseDAO {

    public EquipoModelo getEquipoModelo(Integer id) {

        return getObjeto(EquipoModelo.class, id);
    }

    public List<EquipoModelo> getListaByBusqueda(Integer codMarca, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM EquipoModelo e "
                    //+ "WHERE  ((e.codigo LIKE :codigo ) "
                    //+ "or (e.descripcion LIKE :descripcion ) "
                    //+ ")"
                    + "WHERE  (e.descripcion LIKE :descripcion ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (codMarca == null ? " " : " AND e.marca.codigo = :codMarca ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            // q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (codMarca != null) {
                q.setParameter("codMarca", codMarca);
            }

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<EquipoModelo>();
        }
    }

}
