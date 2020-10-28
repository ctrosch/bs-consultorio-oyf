/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.dao.CanalVentaDAO;
import bs.ventas.modelo.CanalVenta;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class CanalVentaRN implements Serializable {

    @EJB
    private CanalVentaDAO canalVentaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CanalVenta guardar(CanalVenta canalVenta, boolean esNuevo) throws Exception {

        control(canalVenta);

        if (esNuevo) {

            if (canalVentaDAO.getObjeto(CanalVenta.class, canalVenta.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Canal de Venta con el código " + canalVenta.getCodigo());
            }
            canalVentaDAO.crear(canalVenta);

        } else {
            canalVenta = (CanalVenta) canalVentaDAO.editar(canalVenta);
        }

        return canalVenta;

    }

    private void control(CanalVenta canalVenta) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (canalVenta.getCodigo() == null || canalVenta.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (canalVenta.getDescripcion() == null || canalVenta.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(CanalVenta canalVenta) throws Exception {

        canalVentaDAO.eliminar(canalVenta);

    }

    public CanalVenta duplicar(CanalVenta c) throws Exception {

        if (c == null) {
            throw new ExcepcionGeneralSistema("Canal de Venta nulo, nada para duplicar");
        }

        CanalVenta canalVenta = c.clone();
        canalVenta.setCodigo(c.getCodigo());
        canalVenta.setDescripcion(c.getDescripcion() + "( Duplicado)");

        return canalVenta;
    }

    public CanalVenta getCanalVenta(String codigo) {

        return canalVentaDAO.getCanalVenta(codigo);
    }

    public List<CanalVenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return canalVentaDAO.getCanalVentaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<CanalVenta> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return canalVentaDAO.getCanalVentaByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
