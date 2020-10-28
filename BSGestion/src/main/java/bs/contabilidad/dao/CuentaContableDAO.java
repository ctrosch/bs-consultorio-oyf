/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.dao;

import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
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
public class CuentaContableDAO extends BaseDAO {

    public CuentaContable getCuentaContable(String id) {
        return getObjeto(CuentaContable.class, id);
    }
   
    public List<CuentaContable> getListaByBusqueda(String txtBusqueda,String imputable, Map<String, String> filtro, boolean mostrarDeBaja,int cantMax) {
        try {
            
            String sQuery = "SELECT e FROM CuentaContable e "
                    + " WHERE (e.nrocta LIKE :nrocta OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + (imputable ==null || imputable.isEmpty() ? " ": " AND e.imputable = '"+imputable+"' ")
                    + generarStringFiltro(filtro,"e", false)
                    + " ORDER BY e.nrocta";
            
            Query q = getEm().createQuery(sQuery);            
            q.setParameter("nrocta", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
          
            return q.getResultList();            
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<CuentaContable>();
        }  
    }

    public List<CuentaContable> getListaByNivel(Integer nivel, boolean mostrarDeBaja) {
        
        String sQuery = "SELECT m FROM CuentaContable m "
                + " WHERE m.nivel = " + nivel
                + (mostrarDeBaja ? " ": " AND m.auditoria.debaja = 'N' ")
                + " ORDER BY m.nrocta";

        return queryList(CuentaContable.class, sQuery);    
    }
    
    public List<CuentaContable> getCuentasByCuentaMadre(CuentaContable cuentaMadre) {

        try {
            String sQuery = "SELECT m FROM CuentaContable m "
                    + " WHERE m.cuentaContable.nrocta = :ctacon"
                    + " ORDER BY m.nrocta ";
            Query q = (Query) getEm().createQuery(sQuery);
            q.setParameter("ctacon", cuentaMadre.getNrocta());
            return q.getResultList();

        } catch (Exception e) {
            System.out.println("No se puede obtener lista de cuentas por cuenta contable madre " + e.getMessage());
            return null;
        }
    }
    
    
    public List<CuentaContableCentroCosto> getCentroCostoByCuenta(CuentaContable cuenta) {

        try {
            String sQuery = "SELECT m FROM CuentaContableCentroCosto m "
                    + " WHERE m.cuentaContable.nrocta = :ctacon"
                    + " ORDER BY m.nroItem ";
            Query q = (Query) getEm().createQuery(sQuery);
            q.setParameter("ctacon", cuenta.getNrocta());
            return q.getResultList();

        } catch (Exception e) {
            System.out.println("No se puede obtener lista de centros de costo por cuenta contable " + e.getMessage());
            return null;
        }
    }
    
    public String obtenerSiguienteCodigo(String nroCuentaMadre) {

        try {
            String sQuery = " select CAST(concat(max(nrocta)+1,'') AS CHAR) from cg_cuenta_contable "
                    + "where "
                    + (nroCuentaMadre==null? " ctacon is null ":" ctacon = '"+nroCuentaMadre+"' ");
            
            Query q = getEm().createNativeQuery(sQuery);
            
            return (String) q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "obtenerSiguienteCodigo", e.getMessage());
            return "1";
        }
    }
}
