/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Periodo;
import bs.global.rn.PeriodoRN;
import bs.global.util.JsfUtil;
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
public class PeriodoBean extends GenericBean implements Serializable {

    private Periodo periodoPago;
    private List<Periodo> lista;

    @EJB
    private PeriodoRN periodoPagoRN;

    public PeriodoBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        periodoPago = new Periodo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (periodoPago != null) {

                periodoPagoRN.guardar(periodoPago, esNuevo);
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
            periodoPago.getAuditoria().setDebaja(habDes);
            periodoPagoRN.guardar(periodoPago, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            periodoPagoRN.eliminar(periodoPago);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = periodoPagoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Periodo> complete(String query) {
        try {
            lista = periodoPagoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Periodo>();
        }
    }

    public void onSelect(SelectEvent event) {
        periodoPago = (Periodo) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Periodo u) {

        if (u == null) {
            return;
        }

        periodoPago = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (periodoPago == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Periodo getPeriodoPago() {
        return periodoPago;
    }

    public void setPeriodoPago(Periodo unidadMedida) {
        this.periodoPago = unidadMedida;
    }

    public List<Periodo> getLista() {
        return lista;
    }

    public void setLista(List<Periodo> lista) {
        this.lista = lista;
    }
}
