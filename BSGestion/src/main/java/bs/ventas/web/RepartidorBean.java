/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.ventas.modelo.Repartidor;
import bs.ventas.rn.RepartidorRN;
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
public class RepartidorBean extends GenericBean implements Serializable {

    private Repartidor repartidor;
    private List<Repartidor> lista;
    private String codigo;

    @EJB
    private RepartidorRN repartidorRN;

    /**
     * Creates a new instance of RepartidorBean
     */
    public RepartidorBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(repartidorRN.getRepartidor(codigo));
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
        repartidor = new Repartidor();
    }

    public void guardar(boolean nuevo) {

        try {
            if (repartidor != null) {

                repartidorRN.guardar(repartidor, esNuevo);
                esNuevo = false;
                modoVista = "D";
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
            repartidor.getAuditoria().setDebaja(habDes);
            repartidorRN.guardar(repartidor, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            repartidorRN.eliminar(repartidor);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = repartidorRN.getRepartidorByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Repartidor> complete(String query) {
        try {

            lista = repartidorRN.getRepartidorByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Repartidor>();
        }
    }

    public void onSelect(SelectEvent event) {
        repartidor = (Repartidor) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Repartidor v) {

        if (v == null) {
            return;
        }

        repartidor = v;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (repartidor == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public List<Repartidor> getLista() {
        return lista;
    }

    public void setLista(List<Repartidor> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
