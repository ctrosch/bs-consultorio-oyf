/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.PeriodoCursadoDAO;
import bs.educacion.modelo.PeriodoCursado;
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

public class PeriodoCursadoRN implements Serializable {

    @EJB
    private PeriodoCursadoDAO periodoCursadoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(PeriodoCursado p, boolean esNuevo) throws Exception {

        control(p);

        if (esNuevo) {

            if (periodoCursadoDAO.getObjeto(PeriodoCursado.class, p.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + p.getClass().getSimpleName() + " con el código" + p.getCodigo());
            }
            periodoCursadoDAO.crear(p);
        } else {
            periodoCursadoDAO.editar(p);
        }
    }

    private void control(PeriodoCursado p) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (p.getCodigo() == null || p.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (p.getDescripcion() == null || p.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(PeriodoCursado estadoEducacion) throws Exception {

        periodoCursadoDAO.eliminar(estadoEducacion);

    }

    public PeriodoCursado getPeriodoCursado(String cod) {
        return periodoCursadoDAO.getPeriodoCursado(cod);
    }

    public List<PeriodoCursado> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return periodoCursadoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<PeriodoCursado> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return periodoCursadoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
