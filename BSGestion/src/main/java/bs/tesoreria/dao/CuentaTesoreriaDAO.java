/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.dao;

import bs.global.dao.BaseDAO;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class CuentaTesoreriaDAO extends BaseDAO{

    public List<CuentaTesoreria> getLista(boolean mostrarDebaja, int cantMax) {
        
        try {
            String sQuery = "SELECT e FROM CuentaTesoreria e "
                    + "WHERE 1=1"
                    + (mostrarDebaja ? " ": " e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<CuentaTesoreria>();
        } 
    }
    
    /**
     * 
     * @param codTipo
     * @param filtro
     * @param txtBusqueda
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<CuentaTesoreria> getListaByBusqueda(String codTipo,Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM CuentaTesoreria e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion ) "                    
                    + "     ) "
                    + ((codTipo==null || codTipo.isEmpty()) ? " ": " AND e.tipoCuenta.codigo = :tipoCuenta ")
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            
            if(codTipo!=null && !codTipo.isEmpty()){
                q.setParameter("tipoCuenta", codTipo);            
            }
            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getCuentaTesoreriaByBusqueda", e.getMessage());
            return new ArrayList<CuentaTesoreria>();
        }        
    }
    
    public List<ItemSaldoPendienteCuentaTesoreria> getSaldosPendienteByCuentaTesoreria(String nroCuenta) {
        
        try {
            
            String sQuery = " SELECT cj_movimiento_item.NROINT, cj_movimiento_item.CHEQUE, cj_movimiento_item.CODBCO, cj_movimiento_item.NROCTA, cj_movimiento_item.FCHVNC, "
                    + "	(sum(cj_movimiento_item.IMPDEB)-sum(cj_movimiento_item.IMPHAB)) AS IMPORTE, "
                    + " IFNULL(cj_movimiento_item.NOMENT,'') AS NOMBRE,"
                    + " cj_movimiento_item.DOCFIR AS FIRMANTE, "
                    + " cj_movimiento_item.FCHCHE, " 
                    + " cj_movimiento_item.NRODOC, "
                    + " cj_movimiento_item.CATEGO "
                    + " FROM cj_movimiento_item, cj_cuenta "
                    + " WHERE cj_movimiento_item.NROCTA = cj_cuenta.CODIGO And (not (cj_movimiento_item.NROCTA is null)) "
                    + " AND cj_movimiento_item.NROCTA = ?1 "
                    + " GROUP BY cj_movimiento_item.NROCTA, cj_movimiento_item.NROINT, cj_movimiento_item.CHEQUE, cj_movimiento_item.CODBCO, cj_movimiento_item.FCHVNC "
                    + " HAVING (sum(cj_movimiento_item.IMPDEB)-sum(cj_movimiento_item.IMPHAB))<>0 ORDER BY cj_movimiento_item.FCHVNC " ;

            Query q = getEm().createNativeQuery(sQuery, ItemSaldoPendienteCuentaTesoreria.class);
            q.setParameter(1, nroCuenta);                        
            return q.getResultList();
            
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldosPendienteByCuentaTesoreria", e);           
            return new ArrayList<ItemSaldoPendienteCuentaTesoreria>();
        }        
    }
}
