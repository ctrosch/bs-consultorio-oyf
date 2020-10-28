/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.EquivalenciaProveedorDAO;
import bs.stock.modelo.EquivalenciaProveedor;
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
public class EquivalenciaProveedorRN implements Serializable {

    @EJB
    EquivalenciaProveedorDAO equivalenciaProveedorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EquivalenciaProveedor e) throws Exception {

        control(e);

        if (e.getId() == null) {
            equivalenciaProveedorDAO.crear(e);
        } else {
            equivalenciaProveedorDAO.editar(e);
        }
    }

    public void control(EquivalenciaProveedor equivalencia) throws ExcepcionGeneralSistema {

        if (equivalencia != null) {

            if (equivalencia.getProducto() == null) {
                throw new ExcepcionGeneralSistema("El producto no puede estar vacío");
            }

            if (equivalencia.getProveedor() == null) {
                throw new ExcepcionGeneralSistema("El proveedor no puede estar vacío");
            }
        }
    }

    public EquivalenciaProveedor getEquivalenciaProveedor(int id) {

        return equivalenciaProveedorDAO.getObjeto(EquivalenciaProveedor.class, id);
    }

    public void eliminar(EquivalenciaProveedor equivalencia) throws Exception {

        equivalenciaProveedorDAO.eliminar(equivalencia);
    }

    public List<EquivalenciaProveedor> getListaByBusqueda(String txtBusqueda, String artCod, boolean mostrarDeBaja) {

        return equivalenciaProveedorDAO.getListaByBusqueda(null, txtBusqueda, artCod, mostrarDeBaja, 15);
    }

    public List<EquivalenciaProveedor> getListaByCodigoProducto(String artCod, boolean mostrarDeBaja) {

        return equivalenciaProveedorDAO.getListaByBusqueda(null, "", artCod, mostrarDeBaja, 15);
    }

}
