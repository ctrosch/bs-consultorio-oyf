/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.rn.ModuloRN;
import bs.global.modelo.TipoConcepto;
import bs.global.rn.TipoConceptoRN;
import bs.global.util.JsfUtil;
import bs.global.web.*;
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
public class TipoConceptoTesoreriaBean extends GenericBean implements Serializable {

    @EJB
    private TipoConceptoRN tipoDeConceptoRN;
    @EJB
    private ModuloRN moduloRN;

    private TipoConcepto tipoDeConcepto;
    private List<TipoConcepto> lista;
    private Modulo modulo;

    public TipoConceptoTesoreriaBean() {

    }

    @PostConstruct
    public void init() {

        mostrarDebaja = false;
        txtBusqueda = "";
        modulo = moduloRN.getModulo("CJ");
        nuevo();
        buscar();
    }

    public void nuevo() {
        esNuevo = true;
        buscaMovimiento = false;
        tipoDeConcepto = new TipoConcepto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoDeConcepto != null) {

                tipoDeConceptoRN.guardar(tipoDeConcepto, esNuevo);
                esNuevo = false;
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
            tipoDeConcepto.getAuditoria().setDebaja(habDes);
            tipoDeConceptoRN.guardar(tipoDeConcepto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoDeConceptoRN.eliminar(tipoDeConcepto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        String codMod = "";
        if (modulo != null) {
            codMod = modulo.getCodigo();
        }

        lista = tipoDeConceptoRN.getListaByBusqueda(codMod, txtBusqueda, mostrarDebaja);
    }

    public List<TipoConcepto> complete(String query) {
        try {

            lista = tipoDeConceptoRN.getListaByBusqueda("CJ", query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoConcepto>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoDeConcepto = (TipoConcepto) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(TipoConcepto t) {

        if (t == null) {
            return;
        }

        tipoDeConcepto = t;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (tipoDeConcepto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoConcepto getTipoDeConcepto() {
        return tipoDeConcepto;
    }

    public void setTipoDeConcepto(TipoConcepto tipoDeConcepto) {
        this.tipoDeConcepto = tipoDeConcepto;
    }

    public List<TipoConcepto> getLista() {
        return lista;
    }

    public void setLista(List<TipoConcepto> lista) {
        this.lista = lista;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

}
