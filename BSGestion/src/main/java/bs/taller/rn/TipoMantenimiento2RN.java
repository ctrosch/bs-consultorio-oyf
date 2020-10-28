/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.taller.dao.TipoMantenimiento2DAO;
import bs.taller.modelo.TipoMantenimiento2;
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

public class TipoMantenimiento2RN implements Serializable {

    @EJB
    private TipoMantenimiento2DAO tipoMantenimientoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoMantenimiento2 tipoMantenimietno, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoMantenimientoDAO.getObjeto(TipoMantenimiento2.class, tipoMantenimietno.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe unidad de medida con el código" + tipoMantenimietno.getCodigo());
            }
            tipoMantenimientoDAO.crear(tipoMantenimietno);
        } else {
            tipoMantenimientoDAO.editar(tipoMantenimietno);
        }
    }

    public void eliminar(TipoMantenimiento2 operario) throws Exception {

        tipoMantenimientoDAO.eliminar(operario);

    }

    public TipoMantenimiento2 getTipoMantenimiento(String cod) {
        return tipoMantenimientoDAO.getTipoMantenimiento(cod);
    }

    public List<TipoMantenimiento2> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return tipoMantenimientoDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
