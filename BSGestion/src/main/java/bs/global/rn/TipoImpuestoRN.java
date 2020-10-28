/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.TipoImpuestoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.TipoImpuesto;
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
public class TipoImpuestoRN implements Serializable {

    @EJB
    private TipoImpuestoDAO tipoDeImpuestoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoImpuesto t, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoDeImpuestoDAO.getObjeto(TipoImpuesto.class, t.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un tipo de impuesto con el código " + t.getCodigo());
            }
            tipoDeImpuestoDAO.crear(t);
        } else {
            tipoDeImpuestoDAO.editar(t);
        }

    }

    public List<TipoImpuesto> getLista(boolean mostrarDebaja) {

        return tipoDeImpuestoDAO.getLista(mostrarDebaja);

    }

    public void eliminar(TipoImpuesto tipoDeImpuesto) throws Exception {

        tipoDeImpuestoDAO.eliminar(tipoDeImpuesto);

    }

    public List<TipoImpuesto> getTipoByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoDeImpuestoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoImpuesto> getTipoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoDeImpuestoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public TipoImpuesto getTipoImpuesto(String value) {
        return tipoDeImpuestoDAO.getObjeto(TipoImpuesto.class, value);
    }
}
