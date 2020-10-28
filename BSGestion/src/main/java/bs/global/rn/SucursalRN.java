/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.SucursalDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Sucursal;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class SucursalRN implements Serializable {

    @EJB
    private SucursalDAO sucursalDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Sucursal s, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (sucursalDAO.getObjeto(Sucursal.class, s.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe sucursal con el código " + s.getCodigo());
            }
            sucursalDAO.crear(s);
        } else {
            sucursalDAO.editar(s);
        }
    }

    public Sucursal getSucursal(String codigo) {

        Sucursal puntoVenta = sucursalDAO.getSucursal(codigo);

        if (puntoVenta != null && puntoVenta.getAuditoria().getDebaja().equals("S")) {
            puntoVenta = null;
        }
        return puntoVenta;
    }

    public List<Sucursal> getLista() {
        return sucursalDAO.getLista();
    }

    public List<Sucursal> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return sucursalDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 25);
    }

    public List<Sucursal> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return sucursalDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Sucursal> getLista(boolean mostrarDebaja) {

        return sucursalDAO.getLista(mostrarDebaja);

    }

    public void eliminar(Sucursal s) throws Exception {

        sucursalDAO.eliminar(s);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
