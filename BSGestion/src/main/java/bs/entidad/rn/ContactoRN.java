/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ContactoDAO;
import bs.entidad.modelo.Contacto;
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
public class ContactoRN implements Serializable {

    @EJB
    private ContactoDAO contactoDAO;

    public Contacto getContacto(Integer id) {

        return contactoDAO.getObjeto(Contacto.class, id);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Contacto guardar(Contacto e, boolean esNuevo) throws Exception {

        if (e.getId() == null) {
            contactoDAO.crear(e);
        } else {
            e = (Contacto) contactoDAO.editar(e);
        }

        return e;
    }

    public void eliminar(Contacto estadoEntidad) throws Exception {

        contactoDAO.eliminar(estadoEntidad);
    }

    public List<Contacto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return contactoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Contacto> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return contactoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }
}
