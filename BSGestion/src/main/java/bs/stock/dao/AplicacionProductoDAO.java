/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;


import bs.global.dao.BaseDAO;
import bs.stock.modelo.AplicacionProducto;
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
public class AplicacionProductoDAO extends BaseDAO{

    public AplicacionProducto getAplicacionProducto(int id){
        
        return getObjeto(AplicacionProducto.class, id);        
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
    public List<AplicacionProducto> getListaByBusqueda(
            Map<String, String> filtro, 
            String txtBusqueda, 
            String artcod, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM AplicacionProducto e "
                    + "WHERE "
                    + "      ((e.campo1 LIKE :campo1) "
                    + "  OR  (e.campo2 LIKE :campo2 ) "                    
                    + "  OR  (e.campo3 LIKE :campo3 ) "                    
                    + "  OR  (e.campo4 LIKE :campo4 ) "                    
                    + "  OR  (e.campo5 LIKE :campo5 ) "                    
                    + "  OR  (e.campo6 LIKE :campo6 ) "                    
                    + "  OR  (e.campo7 LIKE :campo7 ) "                    
                    + "  OR  (e.campo8 LIKE :campo8 ) "                    
                    + "     ) "
                    + (artcod==null || artcod.isEmpty() ? " " : " AND e.producto.codigo = :artcod ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.nroitm ";

            Query q = getEm().createQuery(sQuery);
            
            if(artcod!=null && !artcod.isEmpty()){
                q.setParameter("artcod", artcod);
            }
            
            q.setParameter("campo1", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("campo2", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("campo3", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("campo4", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("campo5", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("campo6", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("campo7", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("campo8", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getAplicacionProductoByBusqueda", e.getMessage());
            return new ArrayList<AplicacionProducto>();
        }        
    }    
    
}
