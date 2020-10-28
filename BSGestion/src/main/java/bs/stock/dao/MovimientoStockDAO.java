/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.MovimientoStock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoStockDAO extends BaseDAO {
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public void crearMovimiento(MovimientoStock m) {
        getEm().persist(m);
    }

    public void editarMovimiento(MovimientoStock m) {
        getEm().merge(m);
    }

    public void crearItemProducto(ItemMovimientoStock im){
        getEm().persist(im);

    }

    public void editarItemProducto(ItemMovimientoStock im){
        getEm().merge(im);
    }

    public MovimientoStock getMovimiento(Integer id) {

       return getObjeto(MovimientoStock.class, id);
    }

     public ItemMovimientoStock getItemProducto(Integer id) {
       return getObjeto(ItemMovimientoStock.class, id);
    }

    public List<MovimientoStock> getListaByBusqueda(Map<String, String> filtro, int cantMax) {
        try {
            String sQuery = "SELECT m FROM MovimientoStock m ";
                   sQuery += generarStringFiltro(filtro,"m", true);
                   sQuery +=" ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery); 
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            } 
            
            return q.getResultList();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de inventario");
            return new ArrayList<MovimientoStock>();
        }
    }
    
    public MovimientoStock getMovimiento(String codFormulario, Integer numeroFormulario) {
        try {
            String sQuery = "SELECT m FROM MovimientoStock m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";
          
            Query q = getEm().createQuery(sQuery);            
            
            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);
            
            q.setMaxResults(1);
            return (MovimientoStock) q.getSingleResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de stock");
            return null;
        }
    }

    public void borraItemsTransferencia(Integer id) {

        Query q1 = getEm().createNativeQuery("DELETE FROM st_movimiento_item WHERE idcab = " + id + " and TIPITM = 'T'");
        q1.executeUpdate();

    }
   
 
}
