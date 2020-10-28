/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.dao;

import bs.global.modelo.Provincia;
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
public class ProvinciaDAO extends BaseDAO {

    public Provincia getProvincia(String codigo){
        return getEm().find(Provincia.class,codigo);
    }
    
    public List<Provincia> getListaByBusqueda(String codPais,String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM Provincia e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "                    
                    + "     ) "
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + (codPais==null || codPais.isEmpty() ? " ": " AND e.pais.codigo = :codPais ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(codPais!=null && !codPais.isEmpty()){
                q.setParameter("codPais", codPais);
            }
                                    
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {            
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<Provincia>();
        }            
    }
 
}
