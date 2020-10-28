/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.EstadoBarDAO;
import bs.bar.modelo.EstadoBar;
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
public class EstadoBarRN implements Serializable {

    @EJB
    private EstadoBarDAO salonDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EstadoBar guardar(EstadoBar e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (salonDAO.getObjeto(EstadoBar.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + e.getClass().getSimpleName() + " con el código" + e.getCodigo());
            }
            salonDAO.crear(e);
        } else {
            e = (EstadoBar) salonDAO.editar(e);
        }
        return e;
    }

    public EstadoBar getEstadoBar(String cod) {
        return salonDAO.getEstadoBar(cod);
    }

    public List<EstadoBar> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return salonDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
