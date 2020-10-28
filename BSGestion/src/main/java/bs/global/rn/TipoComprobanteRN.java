/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.TipoComprobanteDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.TipoComprobante;
import bs.global.modelo.TipoComprobantePK;
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
public class TipoComprobanteRN implements Serializable {

    @EJB
    private TipoComprobanteDAO tipoComprobanteDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoComprobante guardar(TipoComprobante t, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoComprobanteDAO.getObjeto(TipoComprobante.class, t.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un tipo de comprobante con el código " + t.getCodigo());
            }
            tipoComprobanteDAO.crear(t);
        } else {
            t = (TipoComprobante) tipoComprobanteDAO.editar(t);
        }
        return t;
    }

    public TipoComprobante getTipoComprobante(String modulo, String codigo) {

        return tipoComprobanteDAO.getTipoComprobante(modulo, codigo);
    }

    public TipoComprobante getTipoComprobante(TipoComprobantePK idPK) {

        return tipoComprobanteDAO.getTipoComprobante(idPK);
    }

    public List<TipoComprobante> getLista(String modulo, boolean mostrarDebaja) {

        return tipoComprobanteDAO.getListaByBusqueda(modulo, "", mostrarDebaja, 50);
    }

    public List<TipoComprobante> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja) {

        return tipoComprobanteDAO.getListaByBusqueda(modulo, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoComprobante> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoComprobanteDAO.getListaByBusqueda(modulo, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoComprobante> getLista(boolean mostrarDebaja) {

        return tipoComprobanteDAO.getListaByBusqueda(null, "", mostrarDebaja, 50);
    }

    public void eliminar(TipoComprobante t) throws Exception {

        tipoComprobanteDAO.eliminar(t);
    }

}
