/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.dao;

import bs.entidad.modelo.Contacto;
import bs.global.dao.BaseDAO;
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
public class ContactoDAO extends BaseDAO{

    public Contacto getContacto(Integer id){
        
        return getObjeto(Contacto.class, id);        
    }    
        
    public List<Contacto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM Contacto e "
                    + "WHERE  ((e.nombre LIKE :nombre ) "
                    + "or (e.direccion LIKE :direccion ) "                    
                    + "or (e.celular LIKE :celular ) "
                    + "or (e.email LIKE :email ) "
                    + ")"                                        
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.nombre ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("direccion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");                        
            q.setParameter("celular", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("email", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getContactoByBusqueda", e.getMessage());
            return new ArrayList<Contacto>();
        }        
    }    
    
}
