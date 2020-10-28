/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.TipoUsuarioDAO;
import bs.seguridad.modelo.TipoUsuario;
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
public class TipoUsuarioRN implements Serializable {

    @EJB
    TipoUsuarioDAO tipoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoUsuario t, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoDAO.getObjeto(TipoUsuario.class, t.getId()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe tipo usuario con el código" + t.getId());
            }
            tipoDAO.crear(t);
        } else {
            tipoDAO.editar(t);
        }

        //     if(t.getId()==null){
        //         tipoDAO.crear(t);
        //     }else{
        //         tipoDAO.editar(t);
        //     }
    }

    public void eliminar(TipoUsuario t) throws Exception {

        tipoDAO.eliminar(t);

    }

    public List<TipoUsuario> getLista(boolean mostrarDebaja) {

        return tipoDAO.getLista(mostrarDebaja);

    }

    public TipoUsuario getTipo(int id) {

        return tipoDAO.getTipo(id);
    }

    public List<TipoUsuario> getLista() {

        return tipoDAO.getLista();

    }

    public List<TipoUsuario> getTipoByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoDAO.getTipoByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
