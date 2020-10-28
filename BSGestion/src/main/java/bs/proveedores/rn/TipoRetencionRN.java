/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.TipoRetencionDAO;
import bs.proveedores.modelo.TipoRetencion;
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
public class TipoRetencionRN implements Serializable {

    @EJB
    private TipoRetencionDAO tipoRetencionDAO;

    public TipoRetencion getVendedor(String value) {
        return tipoRetencionDAO.getObjeto(TipoRetencion.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoRetencion s, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoRetencionDAO.getObjeto(TipoRetencion.class, s.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe entidad con el código" + s.getCodigo());
            }
            tipoRetencionDAO.crear(s);
        } else {
            tipoRetencionDAO.editar(s);
        }
    }

    public TipoRetencion getTipoRetencion(String codigo) {
        return tipoRetencionDAO.getTipoRetencion(codigo);
    }

    public List<TipoRetencion> getLista() {
        return tipoRetencionDAO.getLista();
    }

    public List<TipoRetencion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return tipoRetencionDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<TipoRetencion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoRetencionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoRetencion> getLista(boolean mostrarDebaja) {

        return tipoRetencionDAO.getLista(mostrarDebaja);

    }

    public void eliminar(TipoRetencion s) throws Exception {

        tipoRetencionDAO.eliminar(s);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
