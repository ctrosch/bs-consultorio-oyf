/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;


import bs.global.dao.BaseDAO;
import bs.stock.modelo.AtributoProducto;
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
public class AtributoProductoDAO extends BaseDAO{

    public AtributoProducto getAtributoProducto(int id){
        
        return getObjeto(AtributoProducto.class, id);        
    }    
        
    /**
     * 
     * @param filtro
     * @param txtBusqueda
     * @param codigoProducto
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<AtributoProducto> getListaByBusqueda(
            Map<String, String> filtro, 
            String txtBusqueda, 
            String codigoProducto, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM AtributoProducto e "
                    + "WHERE "
                    + "      ((e.nombre LIKE :nombre) "
                    + "  OR  (e.valor LIKE :valor ) "
                    + "     ) "
                    + (codigoProducto==null || codigoProducto.isEmpty() ? " " : " AND e.artcod = :artcod ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.orden ";

            Query q = getEm().createQuery(sQuery);
            
            if(codigoProducto!=null && !codigoProducto.isEmpty()){
                q.setParameter("artcod", codigoProducto);
            }
            
            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("valor", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                        
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getAtributoProductoByBusqueda", e.getMessage());
            return new ArrayList<AtributoProducto>();
        }        
    }    
    
}
