/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.dao.VendedorDAO;
import bs.ventas.modelo.Vendedor;
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
public class VendedorRN implements Serializable {

    @EJB
    private VendedorDAO vendedorDAO;

    public Vendedor getVendedor(String value) {
        return vendedorDAO.getObjeto(Vendedor.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Vendedor v, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (vendedorDAO.getObjeto(Vendedor.class, v.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un vendedor con el código " + v.getCodigo());
            }
            vendedorDAO.crear(v);
        } else {
            v = (Vendedor) vendedorDAO.editar(v);
        }
    }

    public List<Vendedor> getLista(boolean mostrarDebaja) {

        return vendedorDAO.getLista(mostrarDebaja);

    }

    public void eliminar(Vendedor v) throws Exception {

        vendedorDAO.eliminar(v);

    }

    public List<Vendedor> getVendedorByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return vendedorDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, 15);
    }

    public List<Vendedor> getVendedorByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return vendedorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

}
