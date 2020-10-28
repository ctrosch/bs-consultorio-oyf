/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.ventas.modelo.Cobrador;
import bs.ventas.rn.CobradorRN;
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
public class CobradorBean extends GenericBean implements Serializable {

    private Cobrador cobrador;
    private List<Cobrador> lista;
    private String codigo;

    @EJB
    private CobradorRN cobradorRN;

    // Variables para filtros
    /**
     * Creates a new instance of CobradorBean
     */
    public CobradorBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(cobradorRN.getCobrador(codigo));
                } else {
                    buscar();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        cobrador = new Cobrador();
    }

    public void guardar(boolean nuevo) {

        try {
            if (cobrador != null) {

                cobradorRN.guardar(cobrador, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            cobrador.getAuditoria().setDebaja(habDes);
            cobradorRN.guardar(cobrador, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos" + ex);
        }
    }

    public void eliminar() {
        try {
            cobradorRN.eliminar(cobrador);
            nuevo();

            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos" + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = cobradorRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Cobrador> complete(String query) {
        try {

            lista = cobradorRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Cobrador>();
        }
    }

    public void onSelect(SelectEvent event) {
        cobrador = (Cobrador) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Cobrador c) {

        if (c == null) {
            return;
        }

        cobrador = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (cobrador == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
    }

    public List<Cobrador> getLista() {
        return lista;
    }

    public void setLista(List<Cobrador> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
