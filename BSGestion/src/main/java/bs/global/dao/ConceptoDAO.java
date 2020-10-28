/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Concepto;
import bs.global.modelo.ConceptoPK;
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
public class ConceptoDAO extends BaseDAO {

    public Concepto getConcepto(String modulo, String tipo, String codigo) {

        ConceptoPK idPK = new ConceptoPK(modulo, tipo, codigo);
        return getObjeto(Concepto.class, idPK);
    }

// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<Concepto> getConceptoByTipo(String modulo, String tipo, boolean mostrarDebaja) {
        try {
            String sQuery = "SELECT e FROM Concepto e "
                    + "WHERE e.modulo = :modulo "
                    + "AND e.tipo = :tipo "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("tipo", tipo);
            q.setParameter("modulo", modulo);
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getConceptoByTipo", e.getCause());
            return new ArrayList<Concepto>();
        }
    }

    public List<Concepto> getListaByBusqueda(String modulo, String tipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Concepto e "
                    + "WHERE ((e.codigo LIKE :codigo)  OR  (e.descripcion LIKE :descripcion)) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + ((tipo == null || tipo.isEmpty()) ? " " : " AND e.tipo = :tipoConcepto ")
                    + ((modulo == null || modulo.isEmpty()) ? " " : " AND e.modulo = :modulo ")
                    + "ORDER BY e.tipo,e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (modulo != null && !modulo.isEmpty()) {
                q.setParameter("modulo", (modulo.isEmpty() ? "" : modulo));
            }

            if (tipo != null && !tipo.isEmpty()) {
                q.setParameter("tipoConcepto", (tipo.isEmpty() ? "" : tipo));
            }

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<Concepto>();
        }

    }

}
