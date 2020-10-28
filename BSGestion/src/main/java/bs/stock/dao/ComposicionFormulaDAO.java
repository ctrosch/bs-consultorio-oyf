/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.ComposicionFormulaPK;
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
public class ComposicionFormulaDAO extends BaseDAO {

    public ComposicionFormula getComposicionFormula(String artcod, String codfor) {

        try {
            return getEm().find(ComposicionFormula.class, new ComposicionFormulaPK(artcod, codfor));
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public ComposicionFormula getComposicionFormula(ComposicionFormulaPK idPK) {

        try {
            return getEm().find(ComposicionFormula.class, idPK);
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public List<ComposicionFormula> getListaByBusqueda(Map<String, String> filtro, String artcod, String codfor, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {
        try {

            String sQuery = "SELECT e FROM ComposicionFormula e "
                    + " WHERE (e.producto.descripcion LIKE :descripcion) "
                    + (artcod == null || artcod.isEmpty() ? " " : " AND e.artcod = :artcod ")
                    + (codfor == null || codfor.isEmpty() ? " " : " AND e.codfor = :codfor ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.producto.codigo";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (artcod != null && !artcod.isEmpty()) {
                q.setParameter("artcod", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            }

            if (codfor != null && !codfor.isEmpty()) {
                q.setParameter("codfor", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            }

            if (cantidadRegistros > 0) {
                q.setMaxResults(cantidadRegistros);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<ComposicionFormula>();
        }
    }

    public void borrarItemsComponentes() {

        try {

            String sQuery = "DELETE FROM ItemComposicionFormulaComponente ";

            Query q = getEm().createQuery(sQuery);

            int upd = q.executeUpdate();
            getEm().flush();

        } catch (Exception e) {
            log.log(Level.SEVERE, "borrarItemsComponentes", e);
        }
    }

    public void borrarItemsProceso() {

        try {

            String sQuery = "DELETE FROM ItemComposicionFormulaProceso ";

            Query q = getEm().createQuery(sQuery);

            int upd = q.executeUpdate();
            getEm().flush();

        } catch (Exception e) {
            log.log(Level.SEVERE, "borrarItemsProceso", e);
        }
    }

}
