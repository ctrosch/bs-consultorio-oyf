/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ContactoSectorDAO;
import bs.entidad.modelo.ContactoSector;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ContactoSectorRN implements Serializable {

    @EJB
    private ContactoSectorDAO contactoSectorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ContactoSector guardar(ContactoSector contactoSector, boolean esNuevo) throws Exception {

        control(contactoSector);

        if (esNuevo) {

            if (contactoSectorDAO.getObjeto(ContactoSector.class, contactoSector.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Sector con el código " + contactoSector.getCodigo());
            }
            contactoSectorDAO.crear(contactoSector);

        } else {
            contactoSector = (ContactoSector) contactoSectorDAO.editar(contactoSector);
        }

        return contactoSector;

    }

    private void control(ContactoSector contactoSector) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (contactoSector.getCodigo() == null || contactoSector.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (contactoSector.getDescripcion() == null || contactoSector.getDescripcion().isEmpty()) {
            sErrores += "- La descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(ContactoSector contactoSector) throws Exception {

        contactoSectorDAO.eliminar(contactoSector);

    }

    public ContactoSector duplicar(ContactoSector s) throws Exception {

        if (s == null) {
            throw new ExcepcionGeneralSistema("Sector nulo, nada para duplicar");
        }

        ContactoSector contactoSector = s.clone();
        contactoSector.setCodigo(s.getCodigo());
        contactoSector.setDescripcion(s.getDescripcion() + "( Duplicado)");

        return contactoSector;
    }

    public ContactoSector getContactoSector(String codigo) {

        return contactoSectorDAO.getContactoSector(codigo);
    }

    public List<ContactoSector> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return contactoSectorDAO.getContactoSectorByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<ContactoSector> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return contactoSectorDAO.getContactoSectorByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
