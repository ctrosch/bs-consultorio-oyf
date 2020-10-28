/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.proveedores.modelo.ConceptoRetencionValor;
import bs.proveedores.modelo.TipoRetencion;
import bs.proveedores.rn.ConceptoRetencionRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ConceptoRetencionBean extends GenericBean implements Serializable {

    @EJB
    private ConceptoRetencionRN conceptoRN;

    private ConceptoRetencion concepto;
    private List<ConceptoRetencion> lista;
    private TipoRetencion tipoRetencion;

    private String CODIGO;
    private String TCONCEPTO;

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                if (CODIGO != null && !CODIGO.isEmpty() && TCONCEPTO != null && !TCONCEPTO.isEmpty()) {
                    seleccionar(conceptoRN.getConcepto(TCONCEPTO, CODIGO));
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
        concepto = new ConceptoRetencion();
        modoVista = "D";

        if (tipoRetencion != null) {
            concepto.setTipo(tipoRetencion.getCodigo());
            concepto.setTipoRetencion(tipoRetencion);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (concepto != null) {

                concepto = conceptoRN.guardar(concepto, esNuevo);
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
            concepto.getAuditoria().setDebaja(habDes);
            concepto = conceptoRN.guardar(concepto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos" + ex);
        }
    }

    public void eliminar() {
        try {
            conceptoRN.eliminar(concepto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos" + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        String codTipo = "";
        if (tipoRetencion != null) {
            codTipo = tipoRetencion.getCodigo();
        }
        lista = conceptoRN.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipoRetencion = null;

        buscar();

    }

    public void onSelect(SelectEvent event) {
        esNuevo = false;
        concepto = (ConceptoRetencion) event.getObject();
        modoVista = "D";
    }

    public void seleccionar(ConceptoRetencion c) {

        if (c == null) {
            return;
        }

        esNuevo = false;
        concepto = c;
        modoVista = "D";
    }

    public List<ConceptoRetencion> complete(String query) {
        try {

            String codTipo = "";
            if (tipoRetencion != null) {
                codTipo = tipoRetencion.getCodigo();
            }

            lista = conceptoRN.getListaByBusqueda(codTipo, query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ConceptoRetencion>();
        }
    }

    public void onTipoChange() {

        if (tipoRetencion != null) {

            concepto.setTipo(tipoRetencion.getCodigo());
            concepto.setTipoRetencion(tipoRetencion);
        }
    }

    public void nuevoItemValor() {

        conceptoRN.nuevoItemValor(concepto);
    }

    public void eliminarItemValor(ConceptoRetencionValor itemValor) {

        try {
            conceptoRN.eliminarItemValor(concepto, itemValor);
            JsfUtil.addWarningMessage("Se ha borrado el item");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public ConceptoRetencion getConcepto() {
        return concepto;
    }

    public void setConcepto(ConceptoRetencion concepto) {
        this.concepto = concepto;
    }

    public List<ConceptoRetencion> getLista() {
        return lista;
    }

    public void setLista(List<ConceptoRetencion> lista) {
        this.lista = lista;
    }

    public TipoRetencion getTipoRetencion() {
        return tipoRetencion;
    }

    public void setTipoRetencion(TipoRetencion tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public String getTCONCEPTO() {
        return TCONCEPTO;
    }

    public void setTCONCEPTO(String TCONCEPTO) {
        this.TCONCEPTO = TCONCEPTO;
    }

}
