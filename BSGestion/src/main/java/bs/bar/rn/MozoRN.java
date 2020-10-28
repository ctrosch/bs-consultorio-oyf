/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.MozoDAO;
import bs.bar.modelo.Mozo;
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
public class MozoRN implements Serializable {

    @EJB
    private MozoDAO salonDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Mozo guardar(Mozo e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (salonDAO.getObjeto(Mozo.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + e.getClass().getSimpleName() + " con el código" + e.getCodigo());
            }
            salonDAO.crear(e);
        } else {
            e = (Mozo) salonDAO.editar(e);
        }
        return e;
    }

    public Mozo getMozo(String codigo) {
        return salonDAO.getMozo(codigo);
    }

    public List<Mozo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return salonDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
