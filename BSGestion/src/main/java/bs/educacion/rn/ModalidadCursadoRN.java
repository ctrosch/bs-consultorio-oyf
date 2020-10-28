/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.ModalidadCursadoDAO;
import bs.educacion.modelo.ModalidadCursado;
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
public class ModalidadCursadoRN implements Serializable {

    @EJB
    private ModalidadCursadoDAO modalidadCursadoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ModalidadCursado guardar(ModalidadCursado modalidad, boolean esNuevo) throws Exception {

        control(modalidad);

        if (esNuevo) {

            if (modalidadCursadoDAO.getObjeto(ModalidadCursado.class, modalidad.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una Modalidad de Cursado con el código " + modalidad.getCodigo());
            }
            modalidadCursadoDAO.crear(modalidad);

        } else {
            modalidad = (ModalidadCursado) modalidadCursadoDAO.editar(modalidad);
        }

        return modalidad;

    }

    private void control(ModalidadCursado modalidad) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (modalidad.getCodigo() == null || modalidad.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (modalidad.getDescripcion() == null || modalidad.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(ModalidadCursado modalidad) throws Exception {

        modalidadCursadoDAO.eliminar(modalidad);

    }

    public ModalidadCursado duplicar(ModalidadCursado a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Modalidad de Cursado nula, nada para duplicar");
        }

        ModalidadCursado modalidad = a.clone();
        modalidad.setCodigo(a.getCodigo());
        modalidad.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return modalidad;
    }

    public ModalidadCursado getModalidadCursado(String codigo) {

        return modalidadCursadoDAO.getModalidadCursado(codigo);
    }

    public List<ModalidadCursado> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return modalidadCursadoDAO.getModalidadCursadoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<ModalidadCursado> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return modalidadCursadoDAO.getModalidadCursadoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
