/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.rn;

import bs.contrato.dao.EstadoContratoDAO;
import bs.contrato.modelo.EstadoContrato;
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
public class EstadoContratoRN implements Serializable {

    @EJB
    private EstadoContratoDAO estadoContratoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EstadoContrato guardar(EstadoContrato estadoContrato, boolean esNuevo) throws Exception {

        control(estadoContrato, esNuevo);

        if (esNuevo) {

            if (estadoContratoDAO.getObjeto(EstadoContrato.class, estadoContrato.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Estado de Contrato con el código " + estadoContrato.getCodigo());
            }
            estadoContratoDAO.crear(estadoContrato);

        } else {
            estadoContrato = (EstadoContrato) estadoContratoDAO.editar(estadoContrato);
        }

        return estadoContrato;

    }

    public void preSave(EstadoContrato estadoContrato) {

    }

    public void control(EstadoContrato estadoContrato, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (estadoContrato.getCodigo() == null || estadoContrato.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (estadoContrato.getDescripcion() == null || estadoContrato.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (estadoContrato.getColor() == null || estadoContrato.getColor().isEmpty()) {
            sError += "El color es obligatorio | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(EstadoContrato estadoContrato) throws Exception {

        estadoContratoDAO.eliminar(estadoContrato);

    }

    public EstadoContrato duplicar(EstadoContrato e) throws Exception {

        if (e == null) {
            throw new ExcepcionGeneralSistema("Estado Contrato nulo, nada para duplicar");
        }

        EstadoContrato estadoContrato = e.clone();
        estadoContrato.setCodigo(e.getCodigo());
        estadoContrato.setDescripcion(e.getDescripcion() + "( Duplicado)");

        return estadoContrato;
    }

    public EstadoContrato getEstadoContrato(String codigo) {

        return estadoContratoDAO.getEstadoContrato(codigo);
    }

    public List<EstadoContrato> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return estadoContratoDAO.getEstadoContratoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<EstadoContrato> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return estadoContratoDAO.getEstadoContratoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
