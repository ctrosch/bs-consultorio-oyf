/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.ZonaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Zona;
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
public class ZonaRN implements Serializable {

    @EJB
    private ZonaDAO zonaDAO;

    public Zona getZona(String value) {
        return zonaDAO.getObjeto(Zona.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Zona zona, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (zonaDAO.getObjeto(Zona.class, zona.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya extiste una zona con el código" + zona.getCodigo());
            }
            zonaDAO.crear(zona);
        } else {
            zonaDAO.editar(zona);
        }
    }

    public List<Zona> getLista(boolean mostrarDebaja) {

        return zonaDAO.getLista(mostrarDebaja);

    }

    public void eliminar(Zona zona) throws Exception {

        zonaDAO.eliminar(zona);

    }

    public List<Zona> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return zonaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Zona> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return zonaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

}
