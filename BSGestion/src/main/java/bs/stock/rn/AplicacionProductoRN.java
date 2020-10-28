/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.AplicacionProductoDAO;
import bs.stock.modelo.AplicacionProducto;
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
public class AplicacionProductoRN implements Serializable {

    @EJB
    AplicacionProductoDAO aplicacionProductoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(AplicacionProducto a) throws Exception {

        control(a);

        if (a.getId() == null) {
            aplicacionProductoDAO.crear(a);
        } else {
            aplicacionProductoDAO.editar(a);
        }
    }

    public void control(AplicacionProducto aplicacion) throws ExcepcionGeneralSistema {

        if (aplicacion != null) {

            if (aplicacion.getProducto() == null) {
                throw new ExcepcionGeneralSistema("El producto no puede estar vacío");
            }
        }
    }

    public AplicacionProducto getAplicacionProducto(int id) {

        return aplicacionProductoDAO.getObjeto(AplicacionProducto.class, id);
    }

    public void eliminar(AplicacionProducto aplicacion) throws Exception {

        aplicacionProductoDAO.eliminar(aplicacion);
    }

    public List<AplicacionProducto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja, int cantidadRegistros) {

        return aplicacionProductoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, cantidadRegistros);
    }

    public List<AplicacionProducto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja) {

        return aplicacionProductoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, 50);
    }

    public List<AplicacionProducto> getListaByCodigoProducto(String artCod, boolean mostrarDeBaja) {

        return aplicacionProductoDAO.getListaByBusqueda(null, "", artCod, mostrarDeBaja, 50);
    }

}
