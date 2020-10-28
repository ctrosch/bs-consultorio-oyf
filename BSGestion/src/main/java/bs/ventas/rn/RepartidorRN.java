/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.dao.RepartidorDAO;
import bs.ventas.modelo.Repartidor;
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
public class RepartidorRN implements Serializable {

    @EJB
    private RepartidorDAO repartidorDAO;

    public Repartidor getRepartidor(String value) {
        return repartidorDAO.getObjeto(Repartidor.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Repartidor v, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (repartidorDAO.getObjeto(Repartidor.class, v.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un repartidor con el código " + v.getCodigo());
            }
            repartidorDAO.crear(v);
        } else {
            v = (Repartidor) repartidorDAO.editar(v);
        }
    }

    public List<Repartidor> getLista(boolean mostrarDebaja) {

        return repartidorDAO.getLista(mostrarDebaja);

    }

    public void eliminar(Repartidor v) throws Exception {

        repartidorDAO.eliminar(v);

    }

    public List<Repartidor> getRepartidorByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return repartidorDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    public List<Repartidor> getRepartidorByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return repartidorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

}
