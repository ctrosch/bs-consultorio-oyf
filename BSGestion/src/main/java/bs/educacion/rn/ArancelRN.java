/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.ArancelDAO;
import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.ItemArancel;
import bs.educacion.modelo.ParametroEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ArancelRN implements Serializable {

    @EJB
    private ArancelDAO arancelDAO;
    @EJB
    private ParametroEducacionRN parametroEducacionRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Arancel guardar(Arancel arancel, boolean esNuevo) throws Exception {

        preSave(arancel, esNuevo);
        control(arancel, esNuevo);
        reorganizarNroItem(arancel);

        if (esNuevo) {

            if (arancelDAO.getObjeto(Arancel.class, arancel.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una arancel con ese codigo " + arancel.getCodigo());
            }

            arancelDAO.crear(arancel);

        } else {

            if (arancelUtilizado(arancel)) {
                arancel.setSincronizacionPendiente("S");
                arancel.setFechaSincronizacion(null);
                sincronizarImportesInscripciones(arancel);
                arancel.setSincronizacionPendiente("N");
                arancel.setFechaSincronizacion(new Date());
            }

            arancel = (Arancel) arancelDAO.editar(arancel);
        }

        return arancel;

    }

    public void preSave(Arancel arancel, boolean esNuevo) throws Exception {

        if (esNuevo) {

        }

        if (arancel.getCodigo() == null || arancel.getCodigo().isEmpty()) {
            arancel.setCodigo(arancel.getCarrera().getCodigo() + "-" + arancel.getAnioLectivo() + "-" + arancel.getReferencia());
        }

        if (arancel.getItems() != null) {
            arancel.getItems().forEach((item) -> {
                item.setArancel(arancel);
            });
        }
    }

    public void control(Arancel arancel, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (arancel.getCodigo() == null || arancel.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (arancel.getCarrera() == null) {
            sError += "La carrera es obligatoria | ";
        }

        if (arancel.getAnioLectivo() <= 0) {
            sError += "El año lectivo de referencia del arancél es obligatorio | ";
        }

        if (arancel.getReferencia() == null || arancel.getReferencia().isEmpty()) {
            sError += "La referencia del arancél es obligatorio | ";
        }

        if (arancel.getDescripcion() == null || arancel.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (arancel.getFechaDesde() == null) {
            sError += "La fecha disponible desde es obligatoria | ";
        }

        if (arancel.getFechaHasta() == null) {
            sError += "La fecha disponible hasta es obligatoria | ";
        }

        if (arancel.getItems() == null || arancel.getItems().isEmpty()) {
            sError += "El arancél " + arancel.getAnioLectivo() + " debe tener al menos 1 item | ";
        } else {

            for (ItemArancel ia : arancel.getItems()) {

                if (ia.getConcepto() == null) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un concepto seleccionado | ";
                }

                if (ia.getPeriodo() == null) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un período seleccionado | ";
                }

                if (ia.getCantidad() <= 0) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un valor para el campo cantidad cuotas mayor o igual a 1 | ";
                }

                if (ia.getCuotaDesde() <= 0) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un valor para el campo N° Cuota Desde mayor a 0 | ";
                }

                if (ia.getCuotaDesde() > ia.getCuotaHasta()) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un valor para el campo N° Cuota Desde menor o igual a N° Cuota Hasta | ";
                }

                if (ia.getCuotaHasta() <= 0) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un valor para el campo N° Cuota Hasta mayor a 0 | ";
                }

                if (ia.getCuotaHasta() < ia.getCuotaDesde()) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un valor para el campo N° Cuota Hasta mayor o igual a N° Cuota Desde | ";
                }

                if (ia.getEditaImporte().equals("N") && ia.getImporte() <= 0) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener un importe unitario asignado | ";
                }

                if (ia.getOrigenFechaCalculoVencimiento() == null || ia.getOrigenFechaCalculoVencimiento().isEmpty()) {
                    sError += "El item " + ia.getNroitm() + " del arancél " + arancel.getAnioLectivo() + " debe tener Fecha Cal. Vto. seleccionado | ";
                }

            }
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(Arancel arancel) throws Exception {

        arancelDAO.eliminar(arancel);

    }

    public Arancel duplicar(Arancel a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Arancel nulo, nada para duplicar");
        }

        Arancel arancel = a.clone();
        arancel.setCodigo("");
        arancel.setDescripcion(arancel.getDescripcion() + "( Duplicado)");

        return arancel;
    }

    public Arancel getArancel(String codigo) {

        return arancelDAO.getArancel(codigo);
    }

    public List<Arancel> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return arancelDAO.getArancelByBusqueda(null, txtBusqueda, mostrarDebaja, 10);
    }

    public List<Arancel> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return arancelDAO.getArancelByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String getProximoCodigo(String codCarrera) {

        int codigo = arancelDAO.getMaxCodigo(codCarrera);

        String nroCodigo = "00000000" + String.valueOf(codigo);
        return codCarrera + nroCodigo.substring(nroCodigo.length() - 3, nroCodigo.length());

    }

    public void asignarCodigo(Arancel arancel) throws Exception {
        String sErrores = "";

        if (arancel.getCarrera() == null) {
            arancel.setCodigo("");
            return;
        }

        if (arancel.getAnioLectivo() <= 0) {
            arancel.setCodigo("");
            return;
        }

        if (arancel.getReferencia() == null || arancel.getReferencia().isEmpty()) {
            arancel.setCodigo("");
            return;
        }

        arancel.setCodigo(arancel.getCarrera().getCodigo() + "-" + arancel.getAnioLectivo() + "-" + arancel.getReferencia());
        arancel.setDescripcion("Arancel " + arancel.getCarrera().getTitulo() + " " + arancel.getAnioLectivo() + " " + arancel.getReferencia());

        Calendar cal = Calendar.getInstance();

        cal.set(arancel.getAnioLectivo(), Calendar.JANUARY, 1);
        arancel.setFechaDesde(cal.getTime());

        cal.set(arancel.getAnioLectivo(), Calendar.DECEMBER, 31);
        arancel.setFechaHasta(cal.getTime());

    }

    public void reorganizarNroItem(Arancel arancel) {

        //Reorganizamos los números de items
        int i;

        if (arancel.getItems() != null) {

            i = 1;
            for (ItemArancel item : arancel.getItems()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    public void nuevoItemArancel(Arancel arancel) throws ExcepcionGeneralSistema {

        if (arancel == null) {
            throw new ExcepcionGeneralSistema("No existe un arancel seleccionado para agregar items");
        }

        if (arancel.getItems() == null) {
            arancel.setItems(new ArrayList<>());
        }

        ParametroEducacion parametro = parametroEducacionRN.getParametro();

        ItemArancel item = new ItemArancel();
        item.setArancel(arancel);
        item.setNroitm(arancel.getItems().size() + 1);

        item.setPeriodo(parametro.getPeriodo());
        item.setDiaVencimientoSegunPeriodo(parametro.getDiaVencimientoSegunPeriodo());
        item.setOrigenFechaCalculoVencimiento(parametro.getOrigenFechaCalculoVencimiento());

        arancel.getItems().add(item);

    }

    public void eliminarItemArancel(Arancel arancel, ItemArancel item) throws ExcepcionGeneralSistema {

        if (arancel == null) {
            throw new ExcepcionGeneralSistema("No existe un arancel seleccionado para quitar un item");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemArancel e : arancel.getItems()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == e.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemArancel remove = arancel.getItems().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    arancelDAO.eliminar(ItemArancel.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(arancel);

    }

    public int getCantidadRegistros() {

        return arancelDAO.getCantidadRegistros();
    }

    public boolean arancelUtilizado(Arancel arancel) {

        if (arancel == null) {
            return false;
        }

        String utlizado = arancelDAO.arancelUtilizado(arancel);

        return utlizado.equals("S");

    }

    public void sincronizarImportesInscripciones(Arancel arancel) throws ExcepcionGeneralSistema {

        if (arancel != null && arancel.getItems() != null) {

            arancelDAO.sincronizarImportesInscripciones(arancel);

//            for (ItemArancel i : arancel.getItems()) {
//                arancelDAO.sincronizarImportesInscripciones(i);
//            }
            arancel.setSincronizacionPendiente("N");
            arancel.setFechaSincronizacion(new Date());
        }

    }

}
