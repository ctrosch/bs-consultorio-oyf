/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPorSituacionIVA;
import bs.global.modelo.FormularioPorSituacionIVAPK;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;


/**
 *
 * @author ctrosch
 */

@Stateless
public class FormularioPorSituacionIVADAO extends BaseDAO {

    public FormularioPorSituacionIVA getFormulario(FormularioPorSituacionIVAPK idPK){

        return getEm().find(FormularioPorSituacionIVA.class, idPK);
    }

    public List<Formulario> getFormularioByComprobante(Comprobante comprobante) {
                        
        try {
            
            String sQuery = "SELECT DISTINCT e.formulario FROM FormularioPorSituacionIVA e "
                    + " WHERE e.comprobante.modulo =:modulo "
                    + " AND e.comprobante.codigo =:codigo"                    
                    + " ORDER BY e.formulario.codigo ";
            
            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("modulo", comprobante.getModulo());
            q.setParameter("codigo", comprobante.getCodigo());
            
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<Formulario>();
        }         
    }
    
    public List<Formulario> getFormularioByComprobante(Comprobante comprobante, String codSuc) {
                        
        try {
            
            String sQuery = "SELECT DISTINCT e.formulario FROM FormularioPorSituacionIVA e "
                    + " WHERE e.comprobante.modulo =:modulo "
                    + " AND e.comprobante.codigo =:codigo"                    
                    + " AND e.sucurs =:codSuc"                                        
                    + " ORDER BY e.formulario.codigo ";
            
            Query q = getEm().createQuery(sQuery);
                        
            q.setParameter("modulo", comprobante.getModulo());
            q.setParameter("codigo", comprobante.getCodigo());
            q.setParameter("codSuc", codSuc);
                        
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<Formulario>();
        }         
    }

    /**
     * 
     * @param filtro
     * @param txtBusqueda
     * @param modcom
     * @param codcom
     * @param mostrarDebaja
     * @param cantMax
     * @return 
     */
    public List<FormularioPorSituacionIVA> getListaByBusqueda(
            String modcom, String codcom,
            Map<String, String> filtro, 
            String txtBusqueda, 
            boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM FormularioPorSituacionIVA e "
                    + "WHERE 1=1 "
                    + (modcom==null || modcom.isEmpty() ? " " : " AND e.modcom = :modcom ")            
                    + (codcom==null || codcom.isEmpty() ? " " : " AND e.codcom = :codcom ")            
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
                    + "ORDER BY e.codcom ";

            Query q = getEm().createQuery(sQuery);
            
            if(modcom!=null && !modcom.isEmpty()){
                q.setParameter("modcom", modcom);
            }
            
            if(codcom!=null && !codcom.isEmpty()){
                q.setParameter("codcom", codcom);
            }
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<FormularioPorSituacionIVA>();
        }        
    } 
    
}
