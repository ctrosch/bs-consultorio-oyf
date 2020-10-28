/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.prestamo.modelo.AmortizacionPrestamo;
import bs.prestamo.rn.AmortizacionPrestamoRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class AmortizacionPrestamoBean extends GenericBean implements Serializable {

    private AmortizacionPrestamo amortizacion;
    private List<AmortizacionPrestamo> lista;

    @EJB
    private AmortizacionPrestamoRN amortizacionRN;

    public AmortizacionPrestamoBean() {

    }

    @PostConstruct
    public void init() {

        super.iniciar();

        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        amortizacion = new AmortizacionPrestamo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (amortizacion != null) {

                amortizacionRN.guardar(amortizacion, esNuevo);
                esNuevo = false;
                buscaMovimiento = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            amortizacion.getAuditoria().setDebaja(habDes);
            amortizacionRN.guardar(amortizacion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            amortizacionRN.eliminar(amortizacion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = amortizacionRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<AmortizacionPrestamo> complete(String query) {
        try {
            lista = amortizacionRN.getListaByBusqueda(query, false, 5);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<AmortizacionPrestamo>();
        }
    }

    public void onSelect(SelectEvent event) {
        amortizacion = (AmortizacionPrestamo) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(AmortizacionPrestamo u) {

        if (u == null) {
            return;
        }

        amortizacion = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (amortizacion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public AmortizacionPrestamo getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(AmortizacionPrestamo unidadMedida) {
        this.amortizacion = unidadMedida;
    }

    public List<AmortizacionPrestamo> getLista() {
        return lista;
    }

    public void setLista(List<AmortizacionPrestamo> lista) {
        this.lista = lista;
    }
}
