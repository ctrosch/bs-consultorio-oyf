/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.dao;

import bs.global.modelo.ConceptoComprobante;
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
public class ConceptoComprobanteDAO extends BaseDAO{

    public ConceptoComprobante getConceptoComprobante(Integer id){
        
        return getObjeto(ConceptoComprobante.class, id);
    }
        
    public List<ConceptoComprobante> getListaByBusqueda(String modcom,String codcom, String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM ConceptoComprobante e "
                    + "WHERE 1=1 "
                    + (modcom==null || modcom.isEmpty() ? " " : " AND e.modcom = :modcom ")            
                    + (codcom==null || codcom.isEmpty() ? " " : " AND e.codcom = :codcom ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.codcom, e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            if(modcom!=null && !modcom.isEmpty()){
                q.setParameter("modcom", modcom);
            }
            
            if(codcom!=null && !codcom.isEmpty()){
                q.setParameter("codcom", codcom);
            }
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<ConceptoComprobante>();
        }        
    
    }

// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
