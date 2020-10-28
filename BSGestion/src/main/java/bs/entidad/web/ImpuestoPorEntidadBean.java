/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.entidad.rn.ImpuestoPorEntidadRN;
import bs.global.rn.TipoConceptoRN;
import bs.global.util.JsfUtil;
import bs.ventas.web.ConceptoVentaBean;
import java.io.Serializable;
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
public class ImpuestoPorEntidadBean implements Serializable {

    private ImpuestoPorEntidad impuestoPorEntidad;
    private List<ImpuestoPorEntidad> lista;
    private EntidadComercial entidad;

    private boolean mostrarDebaja;
    private boolean esNuevo;
    private String txtBusqueda;

    @EJB
    private ImpuestoPorEntidadRN impuestoPorEntidadRN;
    @EJB
    private TipoConceptoRN tipoConceptoRN;

    @ManagedProperty(value = "#{conceptoVentaBean}")
    protected ConceptoVentaBean conceptoVentaBean;

    public ImpuestoPorEntidadBean() {

    }

    @PostConstruct
    public void init() {
        mostrarDebaja = false;
        txtBusqueda = "";
        nuevo();
        buscar();

        conceptoVentaBean.setTipoConcepto(tipoConceptoRN.getTipoConcepto("VT", "P"));
    }

    public void nuevo() {

        esNuevo = true;
        impuestoPorEntidad = new ImpuestoPorEntidad();

        if (entidad != null) {

            impuestoPorEntidad.setEntidadComercial(entidad);

            entidad.getImpuestos().add(impuestoPorEntidad);
        }
    }

    public void nuevo(EntidadComercial e) {

        entidad = e;
        nuevo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (impuestoPorEntidad != null) {
                impuestoPorEntidad = impuestoPorEntidadRN.guardar(impuestoPorEntidad, esNuevo);

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
            impuestoPorEntidad.getAuditoria().setDebaja(habDes);
            impuestoPorEntidad = impuestoPorEntidadRN.guardar(impuestoPorEntidad, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            impuestoPorEntidadRN.eliminar(impuestoPorEntidad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        String nroCuenta = "";
        if (entidad != null) {
            nroCuenta = entidad.getNrocta();
        }

        lista = impuestoPorEntidadRN.getListaByBusqueda(txtBusqueda, nroCuenta, mostrarDebaja);
    }

    public List<ImpuestoPorEntidad> complete(String query) {
        try {

            String nroCuenta = "";
            if (entidad != null) {
                nroCuenta = entidad.getNrocta();
            }

            lista = impuestoPorEntidadRN.getListaByBusqueda(query, nroCuenta, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ImpuestoPorEntidad>();
        }
    }

    public void onSelect(SelectEvent event) {
        impuestoPorEntidad = (ImpuestoPorEntidad) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(ImpuestoPorEntidad i) {

        if (i == null) {
            return;
        }

        impuestoPorEntidad = i;
        esNuevo = false;
    }

    public void imprimir() {

        if (impuestoPorEntidad == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarConcepto() {

//        if(localidadBean.getLocalidad()!=null && impuestoPorEntidad!=null){
//
//            impuestoPorEntidad.setLocalidad(localidadBean.getLocalidad());
//            impuestoPorEntidad.setProvincia(localidadBean.getLocalidad().getProvincia());
//
//        }
    }

    public void procesarCliente() {

//        if(localidadBean.getLocalidad()!=null && impuestoPorEntidad!=null){
//
//            impuestoPorEntidad.setLocalidad(localidadBean.getLocalidad());
//            impuestoPorEntidad.setProvincia(localidadBean.getLocalidad().getProvincia());
//        }
    }

    //--------------------------------------------------------------------------------------
    public ImpuestoPorEntidad getImpuestoPorEntidad() {
        return impuestoPorEntidad;
    }

    public void setImpuestoPorEntidad(ImpuestoPorEntidad impuestoPorEntidad) {
        this.impuestoPorEntidad = impuestoPorEntidad;
    }

    public List<ImpuestoPorEntidad> getLista() {
        return lista;
    }

    public void setLista(List<ImpuestoPorEntidad> lista) {
        this.lista = lista;
    }

    public boolean isMostrarDebaja() {
        return mostrarDebaja;
    }

    public void setMostrarDebaja(boolean mostrarDebaja) {
        this.mostrarDebaja = mostrarDebaja;
    }

    public boolean isEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public String getTxtBusqueda() {
        return txtBusqueda;
    }

    public void setTxtBusqueda(String txtBusqueda) {
        this.txtBusqueda = txtBusqueda;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    public ConceptoVentaBean getConceptoVentaBean() {
        return conceptoVentaBean;
    }

    public void setConceptoVentaBean(ConceptoVentaBean conceptoVentaBean) {
        this.conceptoVentaBean = conceptoVentaBean;
    }

}
