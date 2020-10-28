/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.EstadoUsuarioDAO;
import bs.seguridad.modelo.EstadoUsuario;
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
public class EstadoUsuarioRN implements Serializable {

    @EJB
    EstadoUsuarioDAO estadoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EstadoUsuario e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (estadoDAO.getObjeto(EstadoUsuario.class, e.getId()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un estado de usuario con el código" + e.getId());
            }
            estadoDAO.crear(e);
        } else {
            estadoDAO.editar(e);
        }

        //  if(e.getId()==null){
        //      estadoDAO.crear(e);
        //  }else{
        //     estadoDAO.editar(e);
        //   }
    }

    public void eliminar(EstadoUsuario e) throws Exception {

        estadoDAO.eliminar(e);

    }

    public List<EstadoUsuario> getLista(boolean mostrarDebaja) {

        return estadoDAO.getLista(mostrarDebaja);

    }

    public EstadoUsuario getEstado(int id) {

        return estadoDAO.getEstado(id);
    }

    public List<EstadoUsuario> getLista() {

        return estadoDAO.getLista();

    }

    public List<EstadoUsuario> getUsuarioByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return estadoDAO.getEstadoByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
