/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.MonedaValores;
import bs.global.rn.MonedaRN;
import bs.global.rn.MonedaValoresRN;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class MonedaValoresBean extends GenericBean implements Serializable {

    @EJB
    private MonedaValoresRN monedaValoresRN;
    @EJB
    private MonedaRN monedaRN;

    private MonedaValores monedaValores;
    private List<MonedaValores> lista;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    // Variables para filtros
    ///
    ///
    public MonedaValoresBean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        modoVista = "D";
        monedaValores = new MonedaValores();
//        monedaValores.setFecha(new Date());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        monedaValores.setFechaAlta(format.format(monedaValores.getFecha()));

    }

    public void guardar(boolean nuevo) {

        try {
            if (monedaValores != null) {
                monedaValores = monedaValoresRN.guardar(monedaValores, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }

                aplicacionBean.actualizarCotizacion();

            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            monedaValores.getAuditoria().setDebaja(habDes);
            monedaValores = monedaValoresRN.guardar(monedaValores, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {

        try {
            monedaValoresRN.eliminar(monedaValores);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = monedaValoresRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;

        buscar();

    }

    public List<MonedaValores> complete(String query) {
        try {
            lista = monedaValoresRN.getLista();
            return lista;
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<MonedaValores>();
        }
    }

    public void onSelect(SelectEvent event) {
        monedaValores = (MonedaValores) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(MonedaValores z) {

        if (z == null) {
            return;
        }

        monedaValores = z;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (monedaValores == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void onDateSelect(SelectEvent event) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        monedaValores.setFechaAlta(format.format(event.getObject()));
    }

    public void cargaUltimoValor() {

        BigDecimal cotizacion = monedaRN.getCotizacionDia(monedaValores.getCodcof());
        monedaValores.setCotizacion(cotizacion);
    }

    //-----------------------------------------------------------------------
    public MonedaValores getMonedaValores() {
        return monedaValores;
    }

    public void setMonedaValores(MonedaValores monedaValores) {
        this.monedaValores = monedaValores;
    }

    public List<MonedaValores> getLista() {
        return lista;
    }

    public void setLista(List<MonedaValores> lista) {
        this.lista = lista;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
