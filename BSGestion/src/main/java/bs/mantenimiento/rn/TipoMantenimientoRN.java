/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.TipoMantenimientoDAO;
import bs.mantenimiento.modelo.TipoMantenimiento;
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
public class TipoMantenimientoRN implements Serializable {

    @EJB
    private TipoMantenimientoDAO tipoMantenimientoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoMantenimiento guardar(TipoMantenimiento tipoMantenimiento, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoMantenimientoDAO.getObjeto(TipoMantenimiento.class, tipoMantenimiento.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un tipo de mantenimiento con el código " + tipoMantenimiento.getCodigo());
            }
            tipoMantenimientoDAO.crear(tipoMantenimiento);

        } else {
            tipoMantenimiento = (TipoMantenimiento) tipoMantenimientoDAO.editar(tipoMantenimiento);
        }

        return tipoMantenimiento;

    }

    public void eliminar(TipoMantenimiento tipoMantenimiento) throws Exception {

        tipoMantenimientoDAO.eliminar(tipoMantenimiento);

    }

    public TipoMantenimiento duplicar(TipoMantenimiento a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Tipo de Mantenimiento nulo, nada para duplicar");
        }

        TipoMantenimiento tipoMantenimiento = a.clone();
        tipoMantenimiento.setCodigo(a.getCodigo());
        tipoMantenimiento.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return tipoMantenimiento;
    }

    public TipoMantenimiento getTipoMantenimiento(String codigo) {

        return tipoMantenimientoDAO.getTipoMantenimiento(codigo);
    }

    public List<TipoMantenimiento> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoMantenimientoDAO.getTipoManteniminetoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoMantenimiento> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoMantenimientoDAO.getTipoManteniminetoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
