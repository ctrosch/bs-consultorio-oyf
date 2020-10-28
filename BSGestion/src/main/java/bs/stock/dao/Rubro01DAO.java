/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.Rubro01;
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
public class Rubro01DAO extends BaseDAO {

    public List<Rubro01> getLista() {
        return getLista(Rubro01.class, true, -1, -1);
    }

    public Rubro01 getTipoProducto(String id) {

        return getObjeto(Rubro01.class,id);
    }

    public List<Rubro01> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDeBaja,int cantMax) {
        try {
            
            String sQuery = "SELECT e FROM Rubro01 e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (codTipo==null ? " ": " AND e.tipoProducto.codigo = :tipoProducto ")
                    + (mostrarDeBaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.tippro, e.codigo";
            
            Query q = getEm().createQuery(sQuery);            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(codTipo!=null){
                q.setParameter("tipoProducto", codTipo);
            }
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
          
            return q.getResultList();            
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Rubro01>();
        }  
    }
 
}
