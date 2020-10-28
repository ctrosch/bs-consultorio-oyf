/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.TipoUsuario;
import bs.seguridad.rn.TipoUsuarioRN;
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
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class TipoUsuarioBean extends GenericBean implements Serializable {

    @EJB
    private TipoUsuarioRN tipoRN;

    private TipoUsuario tipo;
    private List<TipoUsuario> lista;

    /**
     * Creates a new instance of SG_EstadoBean
     */
    public TipoUsuarioBean() {
    }

    @PostConstruct
    public void init() {

        mostrarDebaja = false;
        txtBusqueda = "";
        nuevo();
        buscar();

    }

    public void nuevo() {
        esNuevo = true;
        tipo = new TipoUsuario();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipo != null) {
                tipoRN.guardar(tipo, esNuevo);
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
            tipo.getAuditoria().setDebaja(habDes);
            tipoRN.guardar(tipo, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoRN.eliminar(tipo);
            nuevo();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = tipoRN.getTipoByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<TipoUsuario> complete(String query) {
        try {
            lista = tipoRN.getTipoByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoUsuario>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipo = (TipoUsuario) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(TipoUsuario t) {

        if (t == null) {
            return;
        }

        tipo = t;
        esNuevo = false;
    }

    public void imprimir() {

        if (tipo == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public List<TipoUsuario> getLista() {
        return lista;
    }

    public void setLista(List<TipoUsuario> lista) {
        this.lista = lista;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

}
