/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.rn;

import bs.global.dao.ComprobanteDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.PuntoVentaRN;
import bs.produccion.modelo.ItemMovimientoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.Operario;
import bs.produccion.modelo.TipoMovimientoProduccion;
import bs.produccion.rn.ProduccionRN;
import bs.tarea.dao.TareaDAO;
import bs.tarea.modelo.EstadoTarea;
import bs.tarea.modelo.Tarea;
import bs.tarea.modelo.TareaOperario;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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

public class TareaRN implements Serializable {

    @EJB
    private TareaDAO tareaDAO;
    @EJB
    private ComprobanteDAO comprobanteDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    private ProduccionRN produccionRN;

    public Tarea nuevoMovimiento(String CODTA, String PVTTA, String CODST, String PVTST) throws ExcepcionGeneralSistema {

        Comprobante comprobante = comprobanteDAO.getComprobante("TA", CODTA);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(PVTTA);

        Comprobante comprobanteStock = comprobanteDAO.getComprobante("ST", CODST);
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(PVTST);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de tarea " + CODTA);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + PVTTA);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tarea para el comprobante (" + CODTA + ") "
                    + "para el punto de venta (" + PVTTA + ") "
                    + "con la condición de iva (X) ");
        }

        Tarea m = crearMovimiento(comprobante, formulario, puntoVenta);

        return m;
    }

    private Tarea crearMovimiento(Comprobante comprobante, Formulario formulario, PuntoVenta puntoVenta) throws ExcepcionGeneralSistema {

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("El punto de venta no puede ser nulo");
        }

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante de stock no puede ser nulo");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("El formulario de stock no puede ser nulo");
        }

        Tarea m = new Tarea();
        m.setFormulario(formulario);
        m.setComprobante(comprobante);
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFechaMovimiento(new Date());
        m.setPuntoVenta(puntoVenta);
        m.setEstado(EstadoTarea.A.name());
        return m;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Tarea guardar(Tarea tarea) throws Exception {

        controlTarea(tarea);
        generarMovimientosAdicionales(tarea);

        if (tarea.getId() == null) {

            tomarNumeroFormulario(tarea);
            tareaDAO.crear(tarea);

        } else {

            tarea = (Tarea) tareaDAO.editar(tarea);

            if (tarea.getMovimientosProduccion() != null) {

                for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {
                    produccionRN.actualizarCantidadesPendientes(m);
                }
            }

        }

        return tarea;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardarModificada(Tarea tarea) throws Exception {

        if (tarea.getId() == null) {

            controlTarea(tarea);
            generarMovimientosAdicionales(tarea);
            tomarNumeroFormulario(tarea);
            tareaDAO.crear(tarea);

            if (tarea.getTareaAnular() != null) {
                anularMovimiento(tarea.getTareaAnular());
            }

            if (tarea.getMovimientosProduccion() != null) {

                for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {
                    produccionRN.actualizarCantidadesPendientes(m);
                }
            }

        } else {
            throw new ExcepcionGeneralSistema("La tarea fue generada o está activa, no es posible modificarla");
        }
    }

    public void generarMovimientosAdicionales(Tarea tarea) throws Exception {

        if (tarea.getMovimientosProduccion() != null) {

            for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {
                if (m.getId() == null) {
                    produccionRN.prepararMovimiento(m);
                    produccionRN.tomarNumeroFormulario(m);
                }
            }
        }

    }

    public boolean controlTarea(Tarea tarea) throws ExcepcionGeneralSistema {

        String sErrores = "";

        if (tarea.getEstado() == null) {
            sErrores += "- La tarea no tiene un estado asignado\n";
        } else if (tarea.getEstado().equals("X")) {
            sErrores += "- La tarea está anulada, no es posible modificar\n";
        }

        if (tarea.getEstado().equals("B")) {

            if (!tarea.isIniciaTareaIndividual() && tarea.getOperarios().size() <= 1) {
                sErrores += " - Indique si realizará la tarea de forma individual o agrege los operarios que colaboran\n";

            }
        } else if (tarea.getOperarios() == null || tarea.getOperarios().isEmpty()) {
            sErrores += " - La lista de operarios no puede estar vacía\n";
        }

        if (tarea.getEstado().equals("Z")) {

            if (tarea.getHoraFin() == null) {
                sErrores += " - La hora fin no puede estar \n";
            }

            if (tarea.getHoraFin() != null && tarea.getHoraInicio() != null) {
                if (tarea.getHoraFin().before(tarea.getHoraInicio())) {
                    sErrores += " - La hora fin no puede ser anterior a la hora de inicio\n";
                }
            }

            if (tarea.getTiempo() > 700) {
                sErrores += " - Verifique que la fecha y hora de fin de la tarea sea la correcta, tiempo ingresado (" + tarea.getTiempo() + " min.) \n";
            }

            if (tarea.getArea().getCodigo().equals("PRD")) {

                if (tarea.getCantidad().compareTo(BigDecimal.ZERO) < 0) {
                    sErrores += " - Indique la cantidad realizada\n";
                }
            }

        }

        if (tarea.getArea() == null) {
            sErrores += "- Seleccione el área donde iniciará la tarea\n";

        }
        if (tarea.getOperarios().size() < 1) {
            sErrores += " - No es posible iniciar la tarea sin usuarios\n";

        }

        if (tarea.getProceso() == null) {
            sErrores += " - Seleccione el proceso donde iniciará la tarea\n";
        }

        if (tarea.getArea().getCodigo().equals("PRD")) {

            if (tarea.getMovimientoProduccion() == null) {
                sErrores += " - Seleccione la orden de producción donde iniciará la tarea\n";

            }
        }

        if (!tarea.getArea().getCodigo().equals("PRD")) {

            if (tarea.getBienUso() == null) {
                sErrores += " - Seleccione el bien de uso donde realizará la tarea\n";

            }
        }

        if (tarea.getArea().getCodigo().equals("PRY")) {

            if (tarea.getProyecto() == null) {
                sErrores += " - Seleccione el proyecto\n";

            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        return true;
    }

    private void tomarNumeroFormulario(Tarea m) throws ExcepcionGeneralSistema {

        m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
    }

    public void eliminar(Tarea tarea) throws Exception {

        tareaDAO.eliminar(Tarea.class, tarea.getId());

    }

    public Tarea getTarea(Integer id) {
        return tareaDAO.getTarea(id);
    }

    public List<Tarea> getListaByBusqueda(Operario operario, String estado, boolean mostrarDebaja, int cantidadRegistros) {

        return tareaDAO.getListaByBusqueda(operario, estado, mostrarDebaja, cantidadRegistros);

    }

    public List<Tarea> getListaByBusqueda(Operario operario, Map<String, String> filtro, boolean mostrarDebaja, int cantidadRegistros) {

        return tareaDAO.getListaByBusqueda(operario, filtro, mostrarDebaja, cantidadRegistros);

    }

    public void completarCantidadRegistrosProduccion(Tarea tarea) {

        if (tarea == null) {
            return;
        }

        if (tarea.getMovimientoProduccion() == null) {
            return;
        }

        if (tarea.getMovimientosProduccion() == null) {
            return;
        }

        MovimientoProduccion ordenProduccion = tarea.getMovimientoProduccion();

        for (MovimientoProduccion mp : tarea.getMovimientosProduccion()) {

            if (mp.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)
                    || mp.getTipoMovimiento().equals(TipoMovimientoProduccion.VC)) {

                for (ItemMovimientoProduccion item : mp.getItemsMovimiento()) {

                    for (ItemMovimientoProduccion itemProductoOrdenProduccion : ordenProduccion.getItemsMovimiento()) {

                        if (item.getProducto().equals(itemProductoOrdenProduccion.getProducto())) {
                            itemProductoOrdenProduccion.setProduccion(item.getCantidad());
                        }
                    }
                }
            }

        }
    }

    public void eliminarMovimientosProduccion(Tarea tarea) throws Exception {

        List<MovimientoProduccion> listaBorrar = new ArrayList<MovimientoProduccion>();

        for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {

            if (m.getId() != null) {
                listaBorrar.add(m);
                produccionRN.eliminarMovimiento(m);
            }
        }
        if (tarea.getMovimientosProduccion().removeAll(listaBorrar)) {

        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void anularMovimiento(Tarea tarea) throws Exception {

        tarea.setEstado("X");
        tarea.getAuditoria().setDebaja("S");
        //tarea.setHoraInicio(new Date(0));
        //tarea.setHoraFin(new Date(0));
        tarea.setTiempo(0);

        eliminarMovimientosProduccion(tarea);

        if (tarea.getMovimientosProduccion() != null) {
            tarea.getMovimientosProduccion().clear();
        }

        tarea = (Tarea) tareaDAO.editar(tarea);
    }

    public Tarea copiarMovimiento(Tarea tareaOld) throws ExcepcionGeneralSistema {

        if (tareaOld == null) {
            throw new ExcepcionGeneralSistema("Tarea nula, nada para copiar");
        }

        String CODST = "";
        String PVTST = "";

        if (tareaOld.getMovimientoStock() != null) {
            CODST = tareaOld.getMovimientoStock().getComprobante().getCodigo();
            PVTST = tareaOld.getMovimientoStock().getPuntoVenta().getCodigo();
        }

        Tarea tareaNew = nuevoMovimiento(tareaOld.getComprobante().getCodigo(), tareaOld.getPuntoVenta().getCodigo(), CODST, PVTST);

        tareaNew.setEstado("C");
        tareaNew.setFechaMovimiento(tareaOld.getFechaMovimiento());
        tareaNew.setArea(tareaOld.getArea());
        tareaNew.setProyecto(tareaOld.getProyecto());
        tareaNew.setTipoMantenimiento(tareaOld.getTipoMantenimiento());
        tareaNew.setMovimientoProduccion(tareaOld.getMovimientoProduccion());
        tareaNew.setGrupoProduccion(tareaOld.getGrupoProduccion());
        tareaNew.setDepartamento(tareaOld.getDepartamento());
        tareaNew.setProceso(tareaOld.getProceso());
        tareaNew.setBienUso(tareaOld.getBienUso());
        tareaNew.setCantidad(tareaOld.getCantidad());
        tareaNew.setHoraInicio(tareaOld.getHoraInicio());
        tareaNew.setHoraFin(tareaOld.getHoraFin());
        tareaNew.setTiempo(tareaOld.getTiempo());
        tareaNew.setObservaciones(tareaOld.getObservaciones());

        if (tareaOld.getMovimientoProduccion() != null) {
            tareaNew.setMovimientoProduccion(produccionRN.getMovimiento(tareaOld.getMovimientoProduccion().getId()));
        }

        tareaNew.setTareaAnular(tareaOld);

        if (tareaOld.getOperarios() != null && !tareaOld.getOperarios().isEmpty()) {

            for (TareaOperario u : tareaOld.getOperarios()) {

                TareaOperario tu = new TareaOperario();
                tu.setTarea(tareaNew);
                tu.setOperario(u.getOperario());
                tareaNew.getOperarios().add(tu);
            }
        }

        return tareaNew;
    }

}
