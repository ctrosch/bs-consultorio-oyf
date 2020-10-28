/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.SalonDAO;
import bs.bar.modelo.Salon;
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
public class SalonRN implements Serializable {

    @EJB
    private SalonDAO salonDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Salon guardar(Salon e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (salonDAO.getObjeto(Salon.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + e.getClass().getSimpleName() + " con el código" + e.getCodigo());
            }
            salonDAO.crear(e);
        } else {
            e = (Salon) salonDAO.editar(e);
        }
        return e;
    }

    public Salon getSalon(String cod) {
        return salonDAO.getSalon(cod);
    }

    public List<Salon> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return salonDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
