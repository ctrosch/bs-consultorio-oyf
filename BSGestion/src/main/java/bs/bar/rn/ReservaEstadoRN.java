/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.ReservaEstadoDAO;
import bs.bar.modelo.ReservaEstado;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class ReservaEstadoRN implements Serializable {

   @EJB private ReservaEstadoDAO reservaEstadoDAO;

   @TransactionAttribute(TransactionAttributeType.REQUIRED)     
   public ReservaEstado guardar(ReservaEstado estado, boolean esNuevo) throws Exception{
       
       if (esNuevo){
           
           if(reservaEstadoDAO.getObjeto(ReservaEstado.class, estado.getCodigo())!=null){
               throw new ExcepcionGeneralSistema("Ya existe "+estado.getClass().getSimpleName()+" con el código"+ estado.getCodigo());
           }
           reservaEstadoDAO.crear(estado);
       }else{
           estado = (ReservaEstado) reservaEstadoDAO.editar(estado);
       } 
       return estado;
    }
   
   public void eliminar(ReservaEstado reservaEstado) throws Exception {
        
        reservaEstadoDAO.eliminar(reservaEstado);
        
    }
   
   
    public ReservaEstado getReservaEstado(String cod){
        return reservaEstadoDAO.getReservaEstado(cod);
    }
 
    public List<ReservaEstado> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {
        
        return reservaEstadoDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }    
}
