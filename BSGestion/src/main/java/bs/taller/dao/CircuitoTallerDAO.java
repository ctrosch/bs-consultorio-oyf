/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.taller.dao;


import bs.global.dao.BaseDAO;
import bs.global.modelo.Comprobante;
import bs.taller.modelo.CircuitoTaller;
import bs.taller.modelo.CircuitoTallerPK;
import bs.taller.modelo.CodigoCircuitoTaller;
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
public class CircuitoTallerDAO extends BaseDAO {
    
    public CircuitoTaller getCircuito(CircuitoTallerPK idPK){
        return getEm().find(CircuitoTaller.class, idPK);
    }
    
    public List<CircuitoTaller> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            
            String sQuery = "SELECT e FROM CircuitoTaller e "
                    + " WHERE (e.circom LIKE :circom "
                    + "  OR e.cirapl LIKE :cirapl"
                    + "  OR e.descripcion LIKE :descripcion"
                    + "       ) "
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + " ORDER BY e.circom, e.cirapl ";
            
            Query q = getEm().createQuery(sQuery);            
            q.setParameter("circom"     ,"%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("cirapl"     ,"%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion","%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }    
                        
            return q.getResultList();            
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<CircuitoTaller>();
        }        
    }

    public Comprobante getComprobanteTaller(String circom, String cirapl, String codcom) {

        try{
            
            String sQuery = "SELECT i.comprobante FROM ItemCircuitoTallerTaller i "
                    + "WHERE i.cirapl = '"+cirapl+"' "
                    + "AND i.circom = '"+circom+"' "
                    + "AND i.modulo = 'TL' "
                    + "AND i.codcom = '"+codcom+"' ";
            return queryObject(Comprobante.class, sQuery);

        }catch(Exception e){
            return null;
        }
       
    }
    
    public Comprobante getComprobanteFacturacion(String circom, String cirapl, String codcom) {

        try{
            
            String sQuery = "SELECT i.comprobante FROM ItemCircuitoTallerFacturacion i "
                    + "WHERE i.cirapl = '"+cirapl+"' "
                    + "AND i.circom = '"+circom+"' "
                    + "AND i.modulo = 'FC' "
                    + "AND i.codcom = '"+codcom+"' ";
            return queryObject(Comprobante.class, sQuery);

        }catch(Exception e){
            return null;
        }
       
    }
    
    public Comprobante getComprobanteStock(String circom, String cirapl, String codcom) {
        try{

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoTallerStock i "
                    + "WHERE i.cirapl = '"+cirapl+"' "
                    + "AND i.circom = '"+circom+"' "
                    + "AND i.modulo = 'ST' "
                    + "AND i.codcom = '"+codcom+"' ";
            return queryObject(Comprobante.class, sQuery);

        }catch(Exception e){
            return null;
        }
    }

    public CodigoCircuitoTaller getCodigoCircuito(String codigo) {

        return getEm().find(CodigoCircuitoTaller.class, codigo);

    }

    public List<CodigoCircuitoTaller> getListaCodigoCircuito() {

        return getLista(CodigoCircuitoTaller.class, true, -1, -1);
    }

}
