/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.LocalidadDAO;
import bs.global.modelo.Localidad;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class LocalidadRN implements Serializable {

    @EJB
    private LocalidadDAO localidadDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Localidad guardar(Localidad c) throws Exception {

        if (c.getId() == null) {
            localidadDAO.crear(c);
        } else {
            c = (Localidad) localidadDAO.editar(c);
        }

        return c;
    }

    public Localidad getLocalidad(int id) {

        return localidadDAO.getLocalidad(id);
    }

    public List<Localidad> getListaByBusqueda(String codPais, String codProv, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return localidadDAO.getListaByBusqueda(codPais, codProv, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Localidad> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return localidadDAO.getListaByBusqueda(null, null, txtBusqueda, mostrarDebaja, cantMax);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void eliminar(Localidad c) throws Exception {

        localidadDAO.eliminar(c);
    }
}
