/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.taller.dao;

import bs.global.dao.BaseDAO;
import bs.taller.modelo.Equipo;
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
public class EquipoDAO extends BaseDAO{

    public Equipo getEquipo(Integer id){
        
        return getObjeto(Equipo.class, id);        
    }    
        
    public List<Equipo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM Equipo e "
                    + "WHERE  ((e.codigo LIKE :codigo ) "
                    + "or (e.descripcion LIKE :descripcion ) "                    
                    + ")"                                        
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                       
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<Equipo>();
        }        
    }    

    public int getMaxNroEquipo(){
        
        try {
            String sQuery = "select CAST(MAX(tl_equipo.codigo) AS SIGNED)+1  from tl_equipo ";

            Query q = getEm().createNativeQuery(sQuery);
            
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {            
            log.log(Level.SEVERE, "getMaxNroEquipo", e.getMessage());
            return 0;
        }  
    }
    
}
