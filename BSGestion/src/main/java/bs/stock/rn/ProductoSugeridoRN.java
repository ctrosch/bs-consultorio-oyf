/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.ProductoSugeridoDAO;
import bs.stock.modelo.ProductoSugerido;
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
public class ProductoSugeridoRN implements Serializable {

    @EJB
    ProductoSugeridoDAO productoSugeridoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ProductoSugerido e) throws Exception {

        control(e);

        if (e.getId() == null) {
            productoSugeridoDAO.crear(e);
        } else {
            productoSugeridoDAO.editar(e);
        }
    }

    public void control(ProductoSugerido sugerido) throws ExcepcionGeneralSistema {

        if (sugerido != null) {

            if (sugerido.getProducto() == null) {
                throw new ExcepcionGeneralSistema("El producto no puede estar vacío");
            }

            if (sugerido.getProductoSugerido() == null) {
                throw new ExcepcionGeneralSistema("El producto sugerido no puede estar vacío");
            }
        }
    }

    public ProductoSugerido getProductoSugerido(int id) {

        return productoSugeridoDAO.getObjeto(ProductoSugerido.class, id);
    }

    public void eliminar(ProductoSugerido aplicacion) throws Exception {

        productoSugeridoDAO.eliminar(aplicacion);
    }

    public List<ProductoSugerido> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja, int cantidadRegistros) {

        return productoSugeridoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, cantidadRegistros);
    }

    public List<ProductoSugerido> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja) {

        return productoSugeridoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, 50);
    }

    public List<ProductoSugerido> getListaByCodigoProducto(String artCod, boolean mostrarDeBaja) {

        return productoSugeridoDAO.getListaByBusqueda(null, "", artCod, mostrarDeBaja, 50);
    }

}
