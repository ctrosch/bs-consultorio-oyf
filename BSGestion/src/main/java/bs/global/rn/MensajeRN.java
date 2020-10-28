/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.MensajeDAO;
import bs.global.modelo.Mensaje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Agustin
 */
@Stateless
public class MensajeRN implements Serializable {

    @EJB
    private MensajeDAO mensajeDAO;

    public Mensaje getMensaje(String value) {
        return mensajeDAO.getObjeto(Mensaje.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Mensaje mensaje, boolean esNuevo) throws Exception {

        if (mensaje.getId() == null && esNuevo) {
            mensajeDAO.crear(mensaje);
        } else {
            mensajeDAO.editar(mensaje);
        }
    }

    public List<Mensaje> getLista(boolean mostrarDebaja) {

        return mensajeDAO.getLista(mostrarDebaja);

    }

    public void eliminar(Mensaje zona) throws Exception {

        mensajeDAO.eliminar(zona);

    }

    public List<Mensaje> getListaByBusqueda(String txtBusqueda, Map<String, String> filtro, boolean mostrarDebaja, int cantMax) {

        List<String> orden = new ArrayList<String>();
        orden.add("leido ASC");
        orden.add("fecha DESC");

        return mensajeDAO.getListaByBusqueda(txtBusqueda, filtro, orden, mostrarDebaja, cantMax);
    }

}
