/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.ProductoSustitutoDAO;
import bs.stock.modelo.ProductoSustituto;
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
public class ProductoSustitutoRN implements Serializable {

    @EJB
    ProductoSustitutoDAO productoSustitutoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ProductoSustituto e) throws Exception {

        control(e);

        if (e.getId() == null) {
            productoSustitutoDAO.crear(e);
        } else {
            productoSustitutoDAO.editar(e);
        }
    }

    public void control(ProductoSustituto sustituto) throws ExcepcionGeneralSistema {

        if (sustituto != null) {

            if (sustituto.getProducto() == null) {
                throw new ExcepcionGeneralSistema("El producto no puede estar vacío");
            }

            if (sustituto.getProductoSustituto() == null) {
                throw new ExcepcionGeneralSistema("El producto sustituto no puede estar vacío");
            }
        }
    }

    public ProductoSustituto getProductoSustituto(int id) {

        return productoSustitutoDAO.getObjeto(ProductoSustituto.class, id);
    }

    public void eliminar(ProductoSustituto aplicacion) throws Exception {

        productoSustitutoDAO.eliminar(aplicacion);
    }

    public List<ProductoSustituto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja, int cantidadRegistros) {

        return productoSustitutoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, cantidadRegistros);
    }

    public List<ProductoSustituto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja) {

        return productoSustitutoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, 50);
    }

    public List<ProductoSustituto> getListaByCodigoProducto(String artCod, boolean mostrarDeBaja) {

        return productoSustitutoDAO.getListaByBusqueda(null, "", artCod, mostrarDeBaja, 15);
    }

}
