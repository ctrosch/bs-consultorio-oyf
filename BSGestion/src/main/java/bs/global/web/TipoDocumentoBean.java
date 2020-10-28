/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.TipoDocumento;
import bs.global.rn.TipoDocumentoRN;
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
public class TipoDocumentoBean extends GenericBean implements Serializable {

    private TipoDocumento tipoDocumento;
    private List<TipoDocumento> lista;

    @EJB
    private TipoDocumentoRN tipoDocumentoRN;

    public TipoDocumentoBean() {

    }

    @PostConstruct
    public void init() {
        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {
        tipoDocumento = new TipoDocumento();
        esNuevo = true;
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoDocumento != null) {

                tipoDocumentoRN.guardar(tipoDocumento, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoDocumentoRN.eliminar(tipoDocumento);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = tipoDocumentoRN.getListaByBusqueda(txtBusqueda, false);
    }

    public void onSelect(SelectEvent event) {
        tipoDocumento = (TipoDocumento) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(TipoDocumento t) {
        if (t == null) {
            return;
        }
        tipoDocumento = t;
        esNuevo = false;
    }

    public List<TipoDocumento> complete(String query) {
        try {
            lista = tipoDocumentoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoDocumento>();
        }
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDeImpuesto(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public List<TipoDocumento> getLista() {
        return lista;
    }

    public void setLista(List<TipoDocumento> lista) {
        this.lista = lista;
    }

}
