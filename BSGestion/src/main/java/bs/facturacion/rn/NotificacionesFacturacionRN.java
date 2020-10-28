/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.rn;

import bs.administracion.modelo.CorreoElectronico;
import bs.facturacion.modelo.MovimientoFacturacion;
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
public class NotificacionesFacturacionRN implements Serializable {

    @EJB
    private MailFactory mailFactoryBean;

    public CorreoElectronico generarCorreoElectronicoCliente(MovimientoFacturacion m, String emailEnvioComprobante, String informacionAdicional) throws Exception {

        if (emailEnvioComprobante == null || emailEnvioComprobante.isEmpty()) {
            throw new ExcepcionGeneralSistema("Dirección de correo no válida");
        }

        NumberFormat numF = new DecimalFormat("00000000");

        String asunto = m.getFormulario().getDescripcion() + " "
                + m.getFormulario().getLetra() + " "
                + m.getPuntoVenta().getCodigo() + " "
                + numF.format(m.getNumeroFormulario());

        String destinatario = mailFactoryBean.validarDestinatario(emailEnvioComprobante);

        String mensaje = "";
        mensaje = mailFactoryBean.generarMensaje(m.getComprobante().getDescripcion(), generarMensajeComprobanteFacturacion(m, informacionAdicional));

        return new CorreoElectronico(asunto, destinatario, mensaje);
    }

    public List<String> generarMensajeComprobanteFacturacion(MovimientoFacturacion m, String informacionAdicional) throws Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("El comprobante de facturación es nulo");
        }

        SimpleDateFormat formateadorFecha = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat formateadorNumero = new DecimalFormat("00000000");

        List<String> contenido = new ArrayList<String>();

        contenido.add("<b>Estimado Cliente:</b>");
        contenido.add("Enviamos adjunto " + m.getComprobante().getDescripcion() + "");
        contenido.add("<hr/>");
        contenido.add("Fecha comprobante: " + formateadorFecha.format(m.getFechaMovimiento()));
        contenido.add("Número comprobante: " + formateadorNumero.format(m.getNumeroFormulario()));
        contenido.add("<hr/>");

        if (informacionAdicional != null && !informacionAdicional.isEmpty()) {
            contenido.add("Observaciones: " + informacionAdicional);
            contenido.add("<hr/>");
        }

        contenido.add("Por favor, confirmar recepción del comprobante. Gracias");

        return contenido;
    }
}
