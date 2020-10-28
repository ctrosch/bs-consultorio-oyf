/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.rn;

import bs.contrato.dao.TipoContratoDAO;
import bs.contrato.modelo.TipoContrato;
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
public class TipoContratoRN implements Serializable {

    @EJB
    private TipoContratoDAO tipoContratoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoContrato guardar(TipoContrato tipoContrato, boolean esNuevo) throws Exception {

        control(tipoContrato, esNuevo);

        if (esNuevo) {

            if (tipoContratoDAO.getObjeto(TipoContrato.class, tipoContrato.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Tipo de Contrato con el código " + tipoContrato.getCodigo());
            }
            tipoContratoDAO.crear(tipoContrato);

        } else {
            tipoContrato = (TipoContrato) tipoContratoDAO.editar(tipoContrato);
        }

        return tipoContrato;

    }

    public void preSave(TipoContrato tipoContrato) {

    }

    public void control(TipoContrato tipoContrato, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (tipoContrato.getCodigo() == null || tipoContrato.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (tipoContrato.getDescripcion() == null || tipoContrato.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(TipoContrato tipoContrato) throws Exception {

        tipoContratoDAO.eliminar(tipoContrato);

    }

    public TipoContrato duplicar(TipoContrato t) throws Exception {

        if (t == null) {
            throw new ExcepcionGeneralSistema("Tipo Contrato nulo, nada para duplicar");
        }

        TipoContrato tipoContrato = t.clone();
        tipoContrato.setCodigo(t.getCodigo());
        tipoContrato.setDescripcion(t.getDescripcion() + "( Duplicado)");

        return tipoContrato;
    }

    public TipoContrato getTipoContrato(String codigo) {

        return tipoContratoDAO.getTipoContrato(codigo);
    }

    public List<TipoContrato> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoContratoDAO.getTipoContratoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoContrato> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoContratoDAO.getTipoContratoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
