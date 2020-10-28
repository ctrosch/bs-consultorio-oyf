/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.proveedores.modelo.ConceptoRetencionPK;
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
public class ConceptoRetencionDAO extends BaseDAO {

    public ConceptoRetencion getConcepto(String tipo, String codigo) {

        ConceptoRetencionPK idPK = new ConceptoRetencionPK(tipo, codigo);
        return getObjeto(ConceptoRetencion.class, idPK);
    }

// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<ConceptoRetencion> getConceptoByTipo(String modulo, String tipo, boolean mostrarDebaja) {
        try {
            String sQuery = "SELECT e FROM ConceptoRetencion e "
                    + "WHERE e.tipo = :tipo "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("tipo", tipo);

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getConceptoByTipo", e.getCause());
            return new ArrayList<ConceptoRetencion>();
        }
    }

    public List<ConceptoRetencion> getListaByBusqueda(String tipo, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM ConceptoRetencion e "
                    + "WHERE ((e.codigo LIKE :codigo)  OR  (e.descripcion LIKE :descripcion)) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + ((tipo == null || tipo.isEmpty()) ? " " : " AND e.tipo = :tipoConceptoRetencion ")
                    + "ORDER BY e.tipo,e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (tipo != null && !tipo.isEmpty()) {
                q.setParameter("tipoConceptoRetencion", (tipo.isEmpty() ? "" : tipo));
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
            return new ArrayList<ConceptoRetencion>();
        }

    }

    public List<ConceptoRetencion> getListaByBusqueda(String tipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM ConceptoRetencion e "
                    + "WHERE ((e.codigo LIKE :codigo)  OR  (e.descripcion LIKE :descripcion)) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + ((tipo == null || tipo.isEmpty()) ? " " : " AND e.tipo = :tipoConceptoRetencion ")
                    + "ORDER BY e.tipo,e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (tipo != null && !tipo.isEmpty()) {
                q.setParameter("tipoConceptoRetencion", (tipo.isEmpty() ? "" : tipo));
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
            return new ArrayList<ConceptoRetencion>();
        }

    }

//    public List<ConceptoRetencion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, String tipo, boolean mostrarDebaja, int cantMax) {
//
//        try {
//            String sQuery = "SELECT e FROM ConceptoRetencion e "
//                    + "WHERE e.tipo = :tipo "
//                    + " AND ((e.codigo LIKE :codigo) "
//                    + "  OR  (e.descripcion LIKE :descripcion ) "
//                    + "     ) "
//                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
//                    + generarStringFiltro(filtro,"e", false)
//                    + "ORDER BY e.codigo ";
//
//            Query q = getEm().createQuery(sQuery);
//
//            q.setParameter("tipo", tipo);
//            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
//            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
//
//
//            if(cantMax>0){
//                q.setMaxResults(cantMax);
//            }
//
//            return q.getResultList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.log(Level.SEVERE, "getConceptoRetencionByBusqueda", e.getMessage());
//            return new ArrayList<ConceptoRetencion>();
//        }
//    }
}
