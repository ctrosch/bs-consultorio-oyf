/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.rn;

import bs.administracion.modelo.CorreoElectronico;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.web.MailFactory;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.TipoMovimientoProduccion;
import bs.produccion.rn.ProduccionRN;
import bs.tarea.modelo.Tarea;
import bs.tarea.modelo.TareaOperario;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class NotificacionesTareaRN implements Serializable {

    @EJB
    private MailFactory mailFactoryBean;
    @EJB
    private ProduccionRN produccionRN;

    public CorreoElectronico generarCorreoElectronico(Tarea tarea, String emailEnvioComprobante) throws Exception {

        if (emailEnvioComprobante == null || emailEnvioComprobante.isEmpty()) {
            throw new ExcepcionGeneralSistema("Dirección de correo no válida");
        }

        NumberFormat numF = new DecimalFormat("00000000");

        String asunto = tarea.getArea().getDescripcion() + " | ";

        if (tarea.getArea().getCodigo().equals("MAN")) {
            asunto += tarea.getBienUso().getDescripcion() + " | " + tarea.getTipoMantenimiento().getDescripcion();
        }

        if (tarea.getArea().getCodigo().equals("PRY")) {
            asunto += tarea.getProyecto().getDescripcion();
        }

        if (tarea.getArea().getCodigo().equals("PRD")) {
            asunto += tarea.getMovimientoProduccion().getItemsMovimiento().get(0).getProducto().getDescripcion();
        }

        asunto += (tarea.getObservaciones() == null ? "" : tarea.getObservaciones());

        String destinatario = mailFactoryBean.validarDestinatario(emailEnvioComprobante);

        String mensaje = "";
        mensaje = mailFactoryBean.generarMensaje(tarea.getComprobante().getDescripcion(), generarMensajeTareaFinalizada(tarea));

        return new CorreoElectronico(asunto, destinatario, mensaje);
    }

    public List<String> generarMensajeTareaFinalizada(Tarea tarea) throws ExcepcionGeneralSistema {

        if (tarea == null) {
            throw new ExcepcionGeneralSistema("El comprobante de tarea es nulo");
        }

        SimpleDateFormat formateadorFecha = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat formateadorNumero = new DecimalFormat("00000000");

        List<String> contenido = new ArrayList<String>();

        contenido.add("Fecha : " + formateadorFecha.format(tarea.getFechaMovimiento()));
        contenido.add("Número: " + formateadorNumero.format(tarea.getNumeroFormulario()));
        contenido.add("Tiempo empleado: " + tarea.getTiempoTotal());

        String sOperarios = "";

        for (TareaOperario tu : tarea.getOperarios()) {

            if (sOperarios.isEmpty()) {
                sOperarios += tu.getOperario().getNombre();
            } else {
                sOperarios += " - " + tu.getOperario().getNombre();
            }
        }
        contenido.add("Operarios: " + sOperarios);
        contenido.add("<hr/>");
        contenido.add("Area: " + tarea.getArea().getDescripcion());

        if (tarea.getArea().getCodigo().equals("MAN")) {

            contenido.add("Bien de uso: " + tarea.getBienUso().getDescripcion());
            contenido.add("Tipo mantenimiento: " + tarea.getTipoMantenimiento().getDescripcion());
        }

        if (tarea.getArea().getCodigo().equals("PRY")) {
            contenido.add("Proyecto: " + tarea.getProyecto().getDescripcion());
        }

        if (tarea.getArea().getCodigo().equals("PRD")) {

            MovimientoProduccion ordenProduccion = produccionRN.getMovimiento(tarea.getMovimientoProduccion().getId());

            contenido.add("Comprobante producción.: " + ordenProduccion.getComprobante().getCodigo() + " - " + formateadorNumero.format(ordenProduccion.getNumeroFormulario()));
            contenido.add("Proceso realizado: " + tarea.getProceso().getDescripcion());
            contenido.add("Producto: " + ordenProduccion.getItemsMovimiento().get(0).getProducto().getDescripcion());

            MovimientoProduccion parteProduccion = null;

            if (tarea.getMovimientosProduccion() != null) {
                for (MovimientoProduccion mp : tarea.getMovimientosProduccion()) {

                    if (mp.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {

                        parteProduccion = mp;
                    }
                }
            }

            if (parteProduccion != null) {

                contenido.add("Producción: " + parteProduccion.getItemsMovimiento().get(0).getCantidad());
            }

            contenido.add("Estado general OP " + formateadorNumero.format(ordenProduccion.getNumeroFormulario()));
            contenido.add("<hr/>");

            try {
                BigDecimal pedido = ordenProduccion.getItemsMovimiento().get(0).getCantidad();
                BigDecimal pendiente = ordenProduccion.getItemsMovimiento().get(0).getCantidadPendiente();
                BigDecimal producido = pedido.add(pendiente.negate());

                contenido.add("Pedido: " + pedido);
                contenido.add("Producido: " + producido);
                contenido.add("Pendiente: " + pendiente);
                contenido.add("Avance: " + producido.multiply(new BigDecimal(100)).divide(pedido, 2, RoundingMode.HALF_UP) + " %");
            } catch (Exception e) {
            }

        }

        contenido.add("Observaciones: " + tarea.getObservaciones());
        contenido.add("<hr/>");

        return contenido;
    }
}
