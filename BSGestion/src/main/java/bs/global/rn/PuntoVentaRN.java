/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.PuntoVentaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.PuntoVenta;
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
public class PuntoVentaRN implements Serializable {

    @EJB
    private PuntoVentaDAO puntoVentaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(PuntoVenta s, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (puntoVentaDAO.getObjeto(PuntoVenta.class, s.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe punto de venta con el código " + s.getCodigo());
            }
            puntoVentaDAO.crear(s);
        } else {
            puntoVentaDAO.editar(s);
        }
    }

    public PuntoVenta getPuntoVenta(String codigo) {

        PuntoVenta puntoVenta = puntoVentaDAO.getPuntoVenta(codigo);

        if (puntoVenta != null && puntoVenta.getAuditoria().getDebaja().equals("S")) {
            puntoVenta = null;
        }
        return puntoVenta;
    }

    public List<PuntoVenta> getLista() {
        return puntoVentaDAO.getLista();
    }

    public List<PuntoVenta> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return puntoVentaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 25);
    }

    public List<PuntoVenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return puntoVentaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<PuntoVenta> getLista(boolean mostrarDebaja) {

        return puntoVentaDAO.getLista(mostrarDebaja);

    }

    public void eliminar(PuntoVenta s) throws Exception {

        puntoVentaDAO.eliminar(s);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
