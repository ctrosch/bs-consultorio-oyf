/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.MesaDAO;
import bs.bar.modelo.Mesa;
import bs.bar.modelo.MovimientoBar;
import bs.bar.modelo.Salon;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class MesaRN implements Serializable {

    @EJB
    private MesaDAO salonDAO;

    @EJB
    private MovimientoBarRN movimientoBarRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Mesa guardar(Mesa e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (salonDAO.getObjeto(Mesa.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + e.getClass().getSimpleName() + " con el código" + e.getCodigo());
            }
            salonDAO.crear(e);
        } else {
            e = (Mesa) salonDAO.editar(e);
        }
        return e;
    }

    public Mesa getMesa(String cod) {
        return salonDAO.getMesa(cod);
    }

    public List<Mesa> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return salonDAO.getListaByBusqueda(txtBusqueda, null, mostrarDebaja, cantidadRegistros);
    }

    public List<Mesa> getListaBySalon(Salon salon, boolean mostrarDebaja, int cantidadRegistros) {

        Map<String, String> filtro = salonDAO.getFiltro();

        if (salon != null) {
            filtro.put("salon.codigo = ", "'" + salon.getCodigo() + "'");
        }

        return salonDAO.getListaByBusqueda("", filtro, mostrarDebaja, cantidadRegistros);
    }

    public List<Mesa> sincronizarEstadoMesas(List<Mesa> mesas) {

        if (mesas == null) {
            return null;
        }

        List<MovimientoBar> movimientos = movimientoBarRN.getListaPendientes();

        if (movimientos == null) {
            return null;
        }

        mesas.forEach(i -> {

            movimientos.forEach(m -> {

                if (i.equals(m.getMesa())) {
                    i.setMovimiento(m);
                }
            });
        });

        return mesas;
    }

}
