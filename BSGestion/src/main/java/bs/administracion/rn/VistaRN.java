/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.VistaDAO;
import bs.administracion.modelo.Vista;
import bs.administracion.modelo.VistaDetalle;
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
public class VistaRN implements Serializable {

    @EJB
    private VistaDAO vistaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Vista guardar(Vista vista, boolean esNuevo) throws Exception {

        reorganizarItems(vista);
        controlVista(vista);

        if (getVista(vista.getCodigo()) == null) {
            vistaDAO.crear(vista);
        } else {
            vista = (Vista) vistaDAO.editar(vista);
        }

        return vista;
    }

    public void eliminar(Vista visita) throws Exception {

        vistaDAO.eliminar(visita);
    }

    public Vista getVista(String codigo) {

        return vistaDAO.getObjeto(Vista.class, codigo);
    }

    public List<Vista> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return vistaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public String obtenerSiguienteCogido(String origen) {

        String codigo = vistaDAO.obtenerSiguienteCodigo(origen);

        if (codigo == null || codigo.isEmpty()) {
            codigo = origen.toUpperCase() + "_00001";
        }

        return codigo;
    }

    public void nuevoItemDetalle(Vista vista, String tipo) throws ExcepcionGeneralSistema {

        VistaDetalle vistaDetalle = new VistaDetalle();
        vistaDetalle.setVista(vista);
        vistaDetalle.setTipo(tipo);
        vistaDetalle.setOrigen(vista.getOrigen());
        vista.getDetalle().add(vistaDetalle);

        reorganizarItems(vista);
    }

    public void eliminarItemDetalle(Vista vista, VistaDetalle detalle) throws ExcepcionGeneralSistema {

        if (vista.getDetalle().remove(detalle)) {

            vistaDAO.eliminar(VistaDetalle.class, detalle.getCodigo());
        }
    }

    public void obtenerCodigo(Vista vista) {

        if (vista == null || vista.getOrigen() == null) {
            return;
        }

        if (vista.getCodigo() == null) {
            String codigo = obtenerSiguienteCogido(vista.getOrigen());
            vista.setCodigo(codigo);
        }
    }

    public void reorganizarItems(Vista vista) {

        if (vista.getDetalle() != null) {

            int idDetalle = 0;

            for (VistaDetalle d : vista.getDetalle()) {

                if (d.getCodigo() != null && !d.getCodigo().isEmpty()) {
                    String sId = d.getCodigo().split("_")[2];
                    int idActual = Integer.parseInt(sId);
                    idDetalle = idActual;
                } else {
                    idDetalle++;
                    String sID = "000" + String.valueOf(idDetalle);
                    d.setCodigo(vista.getCodigo() + "_" + sID.substring(sID.length() - 3, sID.length()));
                }
            }
        }
    }

    private void controlVista(Vista vista) throws ExcepcionGeneralSistema {

        String sErrores = "";

        for (VistaDetalle d : vista.getDetalle()) {

            d.setConError(false);

            if (d.getTipo() == null || d.getTipo().isEmpty()) {
                sErrores += " - Seleccione el tipo para el item " + d.getCodigo() + "\n";
                d.setConError(true);
            }

            if (d.getNombre() == null || d.getNombre().isEmpty()) {
                sErrores += "- Ingrese por un nombre para el item" + d.getCodigo() + "\n";
                d.setConError(true);
            }

            if (d.getCampo() == null || d.getCampo().isEmpty()) {
                sErrores += "Ingrese por el campo para el item" + d.getCodigo() + "\n";
                d.setConError(true);
            }

        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void sincronizarFromReferencia(Vista vista) throws ExcepcionGeneralSistema, Exception {

        VistaDetalle itemDetalleFaltante = null;

        if (vista.getVistaReferencia() == null) {

            for (VistaDetalle itemDetalle : vista.getDetalle()) {

                for (VistaDetalle itemDetalleReferencia : vista.getVistaReferencia().getDetalle()) {

                    if (itemDetalle.getCampo().equals(itemDetalleReferencia.getCampo())
                            && itemDetalle.getTipo().equals(itemDetalleReferencia.getTipo())) {
                        itemDetalleFaltante = itemDetalleReferencia;
                    }

                }

                if (itemDetalleFaltante != null) {

                    nuevoItemDetalle(vista, itemDetalleFaltante.getTipo());
                    VistaDetalle itemDetalleNuevo = vista.getDetalle().get(vista.getDetalle().size() - 1);

                    itemDetalleNuevo.setNombre(itemDetalleFaltante.getNombre());
                    itemDetalleNuevo.setCampo(itemDetalleFaltante.getCampo());
                    itemDetalleNuevo.setTipo(itemDetalleFaltante.getTipo());
                    itemDetalleNuevo.setRequerido(itemDetalleFaltante.isRequerido());
                    itemDetalleNuevo.setSoloLectura(itemDetalleFaltante.isSoloLectura());
                    itemDetalleNuevo.setVisible(itemDetalleFaltante.isVisible());

                }
            }

            reorganizarItems(vista);
            guardar(vista, false);
        }
    }
}
