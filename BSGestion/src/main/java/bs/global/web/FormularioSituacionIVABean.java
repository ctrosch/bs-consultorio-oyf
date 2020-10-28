/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Comprobante;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPorSituacionIVA;
import bs.global.rn.CondicionDeIvaRN;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.FormularioRN;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class FormularioSituacionIVABean extends GenericBean implements Serializable {

    @EJB
    private FormularioPorSituacionIVARN formularioSituacionIvaRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private CondicionDeIvaRN condicionIvaRN;

    private FormularioPorSituacionIVA formularioPorSituacionIva;
    private List<FormularioPorSituacionIVA> lista;

    private Comprobante comprobante;
    private List<Formulario> formularios;
    private List<CondicionDeIva> condicionesDeIva;

    public FormularioSituacionIVABean() {

    }

    @PostConstruct
    public void init() {
        mostrarDebaja = false;
        txtBusqueda = "";
        condicionesDeIva = condicionIvaRN.getListaByBusqueda("", mostrarDebaja);
//        nuevo();
//        buscar();
    }

    public void iniciarFiltro(String modulo) {

        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = '" + modulo + "'");
        formularios = formularioRN.getFormularioByBusqueda(filtro, "", mostrarDebaja, 100);
    }

    public void nuevo() {

        if (comprobante == null) {
            JsfUtil.addErrorMessage("El comprobante es nulo, no se puede crear un nuevo objeto de formularioPorSituacionIva");
            return;
        }

        esNuevo = true;
        formularioPorSituacionIva = new FormularioPorSituacionIVA();

        formularioPorSituacionIva.setModcom(comprobante.getModulo());
        formularioPorSituacionIva.setCodcom(comprobante.getCodigo());
        formularioPorSituacionIva.setComprobante(comprobante);
    }

    public void nuevo(FormularioPorSituacionIVA fsi) {
        esNuevo = true;
        formularioPorSituacionIva = fsi;
    }

    public void guardar(boolean nuevo) {

        try {
            if (formularioPorSituacionIva != null) {

                formularioPorSituacionIva = formularioSituacionIvaRN.guardar(formularioPorSituacionIva, esNuevo);

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
            formularioPorSituacionIva.getAuditoria().setDebaja(habDes);
            formularioPorSituacionIva = formularioSituacionIvaRN.guardar(formularioPorSituacionIva, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            formularioSituacionIvaRN.eliminar(formularioPorSituacionIva);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        if (comprobante == null) {
            return;
        }

        lista = formularioSituacionIvaRN.getListaByBusqueda(comprobante, txtBusqueda, mostrarDebaja);
    }

    public List<FormularioPorSituacionIVA> complete(String query) {
        try {

            if (comprobante == null) {
                return null;
            }

            lista = formularioSituacionIvaRN.getListaByBusqueda(comprobante, query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<FormularioPorSituacionIVA>();
        }
    }

    public void onSelect(SelectEvent event) {
        formularioPorSituacionIva = (FormularioPorSituacionIVA) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(FormularioPorSituacionIVA fsi) {

        if (fsi == null) {
            return;
        }

        formularioPorSituacionIva = fsi;
        esNuevo = false;
    }

    public void imprimir() {

        if (formularioPorSituacionIva == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarFormulario() {
//
//        if(localidadBean.getLocalidad()!=null && direccionEntrega!=null){
//
//            direccionEntrega.setLocalidad(localidadBean.getLocalidad());
//            direccionEntrega.setProvincia(localidadBean.getLocalidad().getProvincia());
//
//        }
    }

    public void procesarPuntoVenta() {

//        if(localidadBean.getLocalidad()!=null && direccionEntrega!=null){
//
//            direccionEntrega.setLocalidad(localidadBean.getLocalidad());
//            direccionEntrega.setProvincia(localidadBean.getLocalidad().getProvincia());
//        }
    }

    public void procesarCondicionIva() {

//        if(localidadBean.getLocalidad()!=null && direccionEntrega!=null){
//
//            direccionEntrega.setLocalidad(localidadBean.getLocalidad());
//            direccionEntrega.setProvincia(localidadBean.getLocalidad().getProvincia());
//        }
    }

    //--------------------------------------------------------------------------------------
    public FormularioPorSituacionIVA getFormularioPorSituacionIva() {
        return formularioPorSituacionIva;
    }

    public void setFormularioPorSituacionIva(FormularioPorSituacionIVA formularioPorSituacionIva) {
        this.formularioPorSituacionIva = formularioPorSituacionIva;
    }

    public List<FormularioPorSituacionIVA> getLista() {
        return lista;
    }

    public void setLista(List<FormularioPorSituacionIVA> lista) {
        this.lista = lista;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public List<Formulario> getFormularios() {
        return formularios;
    }

    public void setFormularios(List<Formulario> formularios) {
        this.formularios = formularios;
    }

    public List<CondicionDeIva> getCondicionesDeIva() {
        return condicionesDeIva;
    }

    public void setCondicionesDeIva(List<CondicionDeIva> condicionesDeIva) {
        this.condicionesDeIva = condicionesDeIva;
    }

}
