/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.TipoProducto;
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
public class Rubro02DAO extends BaseDAO {

    public List<Rubro02> getLista() {
        return getLista(Rubro02.class, true, -1, -1);
    }

    public Rubro02 getTipoProducto(String id) {

        return getObjeto(Rubro02.class,id);
    }

    public List<Rubro02> getListaByBusqueda(String codTipo, String txtBusqueda, boolean mostrarDeBaja,int cantMax) {
        try {
            
            String sQuery = "SELECT e FROM Rubro02 e "
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
            return new ArrayList<Rubro02>();
        }  
    }
 
}
