/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.dao;

import bs.entidad.modelo.Categoria;
import bs.entidad.modelo.CategoriaPK;
import bs.entidad.modelo.TipoEntidad;
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
public class CategoriaDAO extends BaseDAO{

    public Categoria getCategoria(String codigo, String tipo){
        
        CategoriaPK id = new CategoriaPK(codigo, tipo);        
        return getObjeto(Categoria.class, id);        
    }    
        
    public List<Categoria> getCategoriaByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM Categoria e "
                    + "WHERE "
                    + "      ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion ) "                    
                    + "     ) "
                    + (tipo==null ? " " : " AND e.codtip = :tipo ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            if(tipo!=null){
                q.setParameter("tipo", tipo.getCodigo());
            }
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getCategoriaByBusqueda", e.getMessage());
            return new ArrayList<Categoria>();
        }        
    }    
    
}
