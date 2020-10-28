/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.TipoComprobante;
import bs.global.rn.TipoComprobanteRN;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
public class TipoComprobanteBean extends GenericBean implements Serializable {

    @EJB
    private TipoComprobanteRN tipoComprobanteRN;

    private TipoComprobante tipoComprobante;
    private List<TipoComprobante> lista;
    protected String MODULO = "-";

    /**
     * Creates a new instance of ComprobanteTesoreriaBean
     */
    public TipoComprobanteBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        tipoComprobante = new TipoComprobante(MODULO);

    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoComprobante != null) {

                tipoComprobante = tipoComprobanteRN.guardar(tipoComprobante, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
//            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            tipoComprobante.getAuditoria().setDebaja(habDes);
            tipoComprobante = tipoComprobanteRN.guardar(tipoComprobante, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoComprobanteRN.eliminar(tipoComprobante);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = tipoComprobanteRN.getListaByBusqueda(MODULO, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public List<TipoComprobante> complete(String query) {
        try {
            lista = tipoComprobanteRN.getListaByBusqueda(MODULO, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoComprobante>();
        }
    }

    public void onSelect(SelectEvent event) {

        tipoComprobante = (TipoComprobante) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoComprobante c) {

        if (c == null) {
            return;
        }

        tipoComprobante = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tipoComprobante == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoComprobanteRN getTipoComprobanteRN() {
        return tipoComprobanteRN;
    }

    public void setTipoComprobanteRN(TipoComprobanteRN tipoComprobanteRN) {
        this.tipoComprobanteRN = tipoComprobanteRN;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public List<TipoComprobante> getLista() {
        return lista;
    }

    public void setLista(List<TipoComprobante> lista) {
        this.lista = lista;
    }

}
