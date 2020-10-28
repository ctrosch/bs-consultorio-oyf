/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.Salon;
import bs.bar.rn.SalonRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
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
public class SalonBean extends GenericBean implements Serializable {

    private Salon salon;
    private List<Salon> lista;
    private String CODIGO;

    @EJB
    private SalonRN salonRN;

    public SalonBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(salonRN.getSalon(CODIGO));
                }

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
        salon = new Salon();
    }

    public void guardar(boolean nuevo) {

        try {
            if (salon != null) {

                salonRN.guardar(salon, esNuevo);
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            salon.getAuditoria().setDebaja(habDes);
            salonRN.guardar(salon, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = salonRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Salon> complete(String query) {
        try {
            lista = salonRN.getListaByBusqueda(query, false, 0);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Salon>();
        }
    }

    public void onSelect(SelectEvent event) {
        salon = (Salon) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Salon d) {

        if (d == null) {
            return;
        }

        salon = d;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (salon == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public List<Salon> getLista() {
        return lista;
    }

    public void setLista(List<Salon> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
