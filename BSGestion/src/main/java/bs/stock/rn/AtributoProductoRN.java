/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.AtributoProductoDAO;
import bs.stock.modelo.AtributoProducto;
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
public class AtributoProductoRN implements Serializable {

    @EJB
    AtributoProductoDAO atributoProductoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(AtributoProducto a, boolean esNuevo) throws Exception {

        control(a);

        if (a.getId() == null) {
            atributoProductoDAO.crear(a);
        } else {
            atributoProductoDAO.editar(a);
        }
    }

    public void control(AtributoProducto atributo) throws ExcepcionGeneralSistema {

        if (atributo != null) {

            if (atributo.getProducto() == null) {
                throw new ExcepcionGeneralSistema("El producto no puede estar vacío");
            }

            if (atributo.getNombre() == null || atributo.getNombre().isEmpty()) {
                throw new ExcepcionGeneralSistema("El nombre del atributo no puede estar vacío");
            }

            if (atributo.getValor() == null || atributo.getValor().isEmpty()) {
                throw new ExcepcionGeneralSistema("El valor del atributo no puede estar vacío para el atributo " + atributo.getNombre());
            }
        }
    }

    public AtributoProducto getAtributoProducto(int id) {

        return atributoProductoDAO.getObjeto(AtributoProducto.class, id);
    }

    public void eliminar(AtributoProducto aplicacion) throws Exception {

        atributoProductoDAO.eliminar(aplicacion);
    }

    public List<AtributoProducto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja, int cantidadRegistros) {

        return atributoProductoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, cantidadRegistros);
    }

    public List<AtributoProducto> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja) {

        return atributoProductoDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, 50);
    }

    public List<AtributoProducto> getListaByCodigoProducto(String artCod, boolean mostrarDeBaja) {

        return atributoProductoDAO.getListaByBusqueda(null, "", artCod, mostrarDeBaja, 50);
    }

}
