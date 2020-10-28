/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;


import bs.global.dao.BaseDAO;
import bs.stock.modelo.EquivalenciaProveedor;
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
public class EquivalenciaProveedorDAO extends BaseDAO{

    public EquivalenciaProveedor getEquivalenciaProveedor(int id){
        
        return getObjeto(EquivalenciaProveedor.class, id);        
    }    
        
    /**
     * 
     * @param filtro
     * @param txtBusqueda
     * @param artCod
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<EquivalenciaProveedor> getListaByBusqueda(
            Map<String, String> filtro, 
            String txtBusqueda, 
            String artcod, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM EquivalenciaProveedor e "
                    + "WHERE "
                    + "      ((e.codigo LIKE :codigo) "
                    + "  OR  (e.nrocta LIKE :nrocta ) "                    
                    + "     ) "
                    + (artcod==null || artcod.isEmpty() ? " " : " AND e.artcod = :artcod ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.artcod ";

            Query q = getEm().createQuery(sQuery);
            
            if(artcod!=null && !artcod.isEmpty()){
                q.setParameter("artcod", artcod);
            }
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("nrocta", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getEquivalenciaProveedorByBusqueda", e.getMessage());
            return new ArrayList<EquivalenciaProveedor>();
        }        
    }    
    
}
