/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.rn;

import bs.bar.dao.ReservaDAO;
import bs.bar.modelo.Reserva;
import bs.bar.modelo.Salon;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.PuntoVentaRN;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class ReservaRN implements Serializable {

    @EJB
    private ReservaDAO reservaDAO;

    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected ComprobanteRN comprobanteRN;

    @EJB
    private FormularioRN formularioRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Reserva guardar(Reserva reserva) throws Exception {

        if (reserva.getId() == null) {
            reservaDAO.crear(reserva);
        } else {
            reserva = (Reserva) reservaDAO.editar(reserva);
        }

        return reserva;
    }

    public Reserva nuevoMovimiento(String modBR, String codBR, String sucBR) throws Exception {

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucBR);
        Comprobante comprobante = comprobanteRN.getComprobante(modBR, codBR);
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        Reserva reserva = new Reserva();
        reserva.setComprobante(comprobante);
        reserva.setPuntoVenta(puntoVenta);
        reserva.setFormulario(formulario);
        reserva.setFechaMovimiento(new Date());
        if (reserva.getFormulario().getTipoNumeracion().equals("A")) {
            reserva.setNumeroFormulario(formularioRN.tomarProximoNumero(reserva.getFormulario()));
        }

        return reserva;
    }

    public void eliminar(Reserva reserva) throws Exception {

        reservaDAO.eliminar(reserva);

    }

    public Reserva getReserva(String cod) {
        return reservaDAO.getReserva(cod);
    }

    public List<Reserva> getListaByBusqueda(Date fechaMovimiento, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return reservaDAO.getListaByBusqueda(fechaMovimiento, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public int getTotalReservasByFecha(Date fechaMovimiento, Salon salon) {

        return reservaDAO.getTotalReservasByFecha(fechaMovimiento, salon);

    }

    public int getTotalPersonasByFecha(Date fechaMovimiento, Salon salon) {

        return reservaDAO.getTotalPersonasByFecha(fechaMovimiento, salon);

    }

    public List<String> getContactos(String query) {

        return reservaDAO.getContactos(query);

    }
}
