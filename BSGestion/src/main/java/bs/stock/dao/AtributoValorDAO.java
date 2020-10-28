/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;


import bs.global.dao.BaseDAO;
import bs.stock.modelo.AtributoValor;
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
public class AtributoValorDAO extends BaseDAO{

    public AtributoValor getAtributoValor(int id){
        
        return getObjeto(AtributoValor.class, id);        
    }    
        
    /**
     * 
     * @param nombre
     * @param txtBusqueda
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<String> getListaByBusqueda(String nombre, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e.valor FROM AtributoValor e "
                    + "WHERE "
                    + "      (e.valor LIKE :valor) "
                    + (nombre==null || nombre.isEmpty() ? " " : " AND e.atributo.nombre = :nombre ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.valor ";

            Query q = getEm().createQuery(sQuery);
            
            if(nombre!=null && !nombre.isEmpty()){
                q.setParameter("nombre", nombre);
            }
            
            q.setParameter("valor", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                        
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<String>();
        }        
    }    
    
}
