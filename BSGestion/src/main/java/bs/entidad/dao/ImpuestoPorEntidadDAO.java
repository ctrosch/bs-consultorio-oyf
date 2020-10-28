/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.dao;

import bs.entidad.modelo.ImpuestoPorEntidad;
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
public class ImpuestoPorEntidadDAO extends BaseDAO{

    public ImpuestoPorEntidad getImpuestoEntidad(Integer id){
        
        return getObjeto(ImpuestoPorEntidad.class, id);        
    }    
        
    /**
     * 
     * @param filtro
     * @param txtBusqueda
     * @param nroCuenta
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<ImpuestoPorEntidad> getListaByBusqueda(
            Map<String, String> filtro, 
            String txtBusqueda, 
            String nroCuenta, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM ImpuestoPorEntidad e "
                    + "WHERE  (e.tipoImpuesto.codigo LIKE :tipimp) "                    
                    + (nroCuenta==null || nroCuenta.isEmpty() ? " " : " AND e.entidadComercial.nrocta = :nrocta ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.tipoImpuesto.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            if(nroCuenta!=null && !nroCuenta.isEmpty()){
                q.setParameter("nrocta", nroCuenta);
            }
            
            q.setParameter("tipimp", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE,this.getClass().getSimpleName() + " getListaByBusqueda", e.getMessage());
            return new ArrayList<ImpuestoPorEntidad>();
        }        
    }    
    
}
