/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.contabilidad.dao.PeriodoContableDAO;
import bs.contabilidad.modelo.PeriodoContable;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class PeriodoContableRN implements Serializable {

    @EJB
    private PeriodoContableDAO periodoContableDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeriodoContable guardar(PeriodoContable periodo, boolean esNuevo) throws Exception {

        control(periodo);

        if (esNuevo) {
            if (periodoContableDAO.getObjeto(PeriodoContable.class, periodo.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un periodo contable con el código" + periodo.getCodigo());
            }
            periodoContableDAO.crear(periodo);
        } else {
            periodo = (PeriodoContable) periodoContableDAO.editar(periodo);
        }

        return periodo;
    }

    public PeriodoContable duplicar(PeriodoContable p) throws Exception {

        if (p == null) {
            throw new ExcepcionGeneralSistema("Período Contable nulo, nada para duplicar");
        }

        PeriodoContable periodo = p.clone();
        periodo.setCodigo(p.getCodigo());
        periodo.setDescripcion(p.getDescripcion() + "( Duplicado)");

        return periodo;
    }

    private void control(PeriodoContable periodo) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (periodo.getCodigo() == null || periodo.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (periodo.getDescripcion() == null || periodo.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

        if (periodo.getEstado() == null || periodo.getEstado().isEmpty()) {

            sErrores += "- El estado no puede se nulo o estar vacío \n";
        }

        if (periodo.getPeriodoInterno() == null || periodo.getPeriodoInterno().isEmpty()) {

            sErrores += "- Si es período interno no puede estar vacío \n";
        }

        if (periodo.getFechaInicio() == null) {

            sErrores += "- La Fecha de Inicio no puede ser nula \n";
        }

        if (periodo.getFechaFin() == null) {

            sErrores += "- La Fecha de Finalización no puede ser nula \n";
        }

        if (periodo.getFechaFin().before(periodo.getFechaInicio())) {

            sErrores += "- La fecha fin del periodo no puede ser anterior a la fecha inicio \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public PeriodoContable getPeriodoActivo(String periodoInterno) {

        return periodoContableDAO.getPeriodoActivo(periodoInterno);
    }

    public PeriodoContable getPeriodoByFecha(String periodoInterno, Date fechaMovimiento) {

        return periodoContableDAO.getPeriodoByFecha(periodoInterno, fechaMovimiento);
    }

    public void eliminar(PeriodoContable periodo) throws Exception {

        periodoContableDAO.eliminar(periodo);
    }

    public PeriodoContable getPeriodoContable(String codigo) {
        return periodoContableDAO.getPeriodoContable(codigo);
    }

    public List<PeriodoContable> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        return periodoContableDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, cantMax);
    }

    public List<PeriodoContable> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        return periodoContableDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

    public PeriodoContable onFechaDesdeSelect(PeriodoContable p) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(p.getFechaInicio());
        gc.add(Calendar.YEAR, 1);
        gc.add(Calendar.DAY_OF_YEAR, -1);
        p.setFechaFin(gc.getTime());

        return p;

    }
}
