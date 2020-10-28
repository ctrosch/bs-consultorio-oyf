/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.CompradorDAO;
import bs.proveedores.modelo.Comprador;
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
public class CompradorRN implements Serializable {

    @EJB
    private CompradorDAO compradorDAO;

    public Comprador getComprador(String value) {
        return compradorDAO.getObjeto(Comprador.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Comprador v, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (compradorDAO.getObjeto(Comprador.class, v.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un comprador con el código " + v.getCodigo());
            }
            compradorDAO.crear(v);
        } else {
            compradorDAO.editar(v);
        }
    }

    public List<Comprador> getLista(boolean mostrarDebaja) {

        return compradorDAO.getListaByBusqueda(null, "", mostrarDebaja, 15);

    }

    public void eliminar(Comprador v) throws Exception {

        compradorDAO.eliminar(v);

    }

    public Comprador duplicar(Comprador c) throws Exception {

        if (c == null) {
            throw new ExcepcionGeneralSistema("Comprador nulo, nada para duplicar");
        }

        Comprador comprador = c.clone();
        comprador.setCodigo(c.getCodigo());
        comprador.setNombre(c.getNombre() + "( Duplicado)");

        return comprador;
    }

    public List<Comprador> getCompradorByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return compradorDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Comprador> getCompradorByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return compradorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

}
