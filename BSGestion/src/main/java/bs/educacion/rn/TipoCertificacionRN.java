/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.TipoCertificacionDAO;
import bs.educacion.modelo.TipoCertificacion;
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
 * @author Claudio
 */
@Stateless
public class TipoCertificacionRN implements Serializable {

    @EJB
    private TipoCertificacionDAO tipoCertificacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoCertificacion guardar(TipoCertificacion tipo, boolean esNuevo) throws Exception {

        control(tipo);

        if (esNuevo) {

            if (tipoCertificacionDAO.getObjeto(TipoCertificacion.class, tipo.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Tipo de Certificación con el código " + tipo.getCodigo());
            }
            tipoCertificacionDAO.crear(tipo);

        } else {
            tipo = (TipoCertificacion) tipoCertificacionDAO.editar(tipo);
        }

        return tipo;

    }

    private void control(TipoCertificacion tipo) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (tipo.getCodigo() == null || tipo.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (tipo.getDescripcion() == null || tipo.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(TipoCertificacion tipo) throws Exception {

        tipoCertificacionDAO.eliminar(tipo);

    }

    public TipoCertificacion duplicar(TipoCertificacion a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Tipo de Certificación nula, nada para duplicar");
        }

        TipoCertificacion tipo = a.clone();
        tipo.setCodigo(a.getCodigo());
        tipo.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return tipo;
    }

    public TipoCertificacion getTipoCertificacion(String codigo) {

        return tipoCertificacionDAO.getTipoCertificacion(codigo);
    }

    public List<TipoCertificacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoCertificacionDAO.getTipoCertificacionByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoCertificacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoCertificacionDAO.getTipoCertificacionByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
