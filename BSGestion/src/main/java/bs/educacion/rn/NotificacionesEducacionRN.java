/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.administracion.modelo.CorreoElectronico;
import bs.educacion.modelo.MovimientoEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.web.MailFactory;
import java.io.Serializable;
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
public class NotificacionesEducacionRN implements Serializable {

    @EJB
    private MailFactory mailFactoryBean;

    public CorreoElectronico generarCorreoElectronicoAlumno(MovimientoEducacion m, String emailEnvioComprobante, String informacionAdicional) throws Exception {

        if (emailEnvioComprobante == null || emailEnvioComprobante.isEmpty()) {
            throw new ExcepcionGeneralSistema("Dirección de correo no válida");
        }

        if (m == null) {
            throw new ExcepcionGeneralSistema("Comprobante no encontrado");
        }

        NumberFormat numF = new DecimalFormat("00000000");

        String asunto = m.getFormulario().getDescripcion() + " "
                + m.getFormulario().getLetra() + " "
                + m.getPuntoVenta().getCodigo() + " "
                + numF.format(m.getNumeroFormulario());

        String destinatario = mailFactoryBean.validarDestinatario(emailEnvioComprobante);

        String mensaje = "";
        mensaje = mailFactoryBean.generarMensaje("Su comprobante", generarMensajeComprobante(m, informacionAdicional));

        return new CorreoElectronico(asunto, destinatario, mensaje);
    }

    public List<String> generarMensajeComprobante(MovimientoEducacion m, String informacionAdicional) throws Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("El comprobante de educación es nulo");
        }

        SimpleDateFormat formateadorFecha = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat formateadorNumero = new DecimalFormat("00000000");

        List<String> contenido = new ArrayList<String>();

        contenido.add("<b>Estimado Alumno:</b>");
        contenido.add("Enviamos adjunto " + m.getFormulario().getDescripcion());
        contenido.add("<hr/>");
        contenido.add("Fecha comprobante: " + formateadorFecha.format(m.getFechaMovimiento()));
        contenido.add("Número comprobante: " + formateadorNumero.format(m.getNumeroFormulario()));
        contenido.add("<hr/>");

        if (informacionAdicional != null && !informacionAdicional.isEmpty()) {
            contenido.add("Observaciones: " + informacionAdicional);
            contenido.add("<hr/>");
        }

        return contenido;
    }

    public CorreoElectronico generarCorreoElectronico(MovimientoEducacion m, String emailEnvioComprobante, String informacionAdicional) throws Exception {

        if (emailEnvioComprobante == null || emailEnvioComprobante.isEmpty()) {
            throw new ExcepcionGeneralSistema("Dirección de correo no válida");
        }

        if (m == null) {
            throw new ExcepcionGeneralSistema("Comprobante no encontrado");
        }

        NumberFormat numF = new DecimalFormat("00000000");

        String asunto = m.getFormulario().getDescripcion() + " "
                + m.getFormulario().getLetra() + " "
                + m.getPuntoVenta().getCodigo() + " "
                + numF.format(m.getNumeroFormulario());

        String destinatario = mailFactoryBean.validarDestinatario(emailEnvioComprobante);

        String mensaje = "";
        mensaje = mailFactoryBean.generarMensaje(m.getComprobante().getDescripcion(), generarMensajeComprobante(m, informacionAdicional));

        return new CorreoElectronico(asunto, destinatario, mensaje);
    }

}
