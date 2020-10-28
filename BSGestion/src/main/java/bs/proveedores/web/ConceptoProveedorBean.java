/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Concepto;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.TipoConcepto;
import bs.global.rn.ConceptoRN;
import bs.global.rn.TipoConceptoRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
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
public class ConceptoProveedorBean extends GenericBean implements Serializable {

    @EJB
    private ConceptoRN conceptoRN;
    @EJB
    private TipoConceptoRN tipoConceptoRN;

    private Concepto concepto;
    private List<Concepto> lista;
    private final String MODULO = "PV";
    private List<TipoConcepto> tipos;
    private TipoConcepto tipoConcepto;
    private String CODIGO = "";
    private String TCONCEPTO = "";

    /**
     * Creates a new instance of BuscadorConceptoBean
     */
    public ConceptoProveedorBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();

                tipos = tipoConceptoRN.getLista(MODULO, false);
                if (CODIGO != null && !CODIGO.isEmpty() && TCONCEPTO != null && !TCONCEPTO.isEmpty()) {
                    seleccionar(conceptoRN.getConcepto(MODULO, TCONCEPTO, CODIGO));
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
        concepto = new Concepto();
        concepto.setModulo(MODULO);
    }

    public void guardar(boolean nuevo) {

        try {
            if (concepto != null) {

                conceptoRN.guardar(concepto, esNuevo);
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
            conceptoRN.guardar(concepto, false);
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
        if (tipoConcepto != null) {
            codTipo = tipoConcepto.getCodigo();
        }

        lista = conceptoRN.getListaByBusqueda(MODULO, codTipo, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipoConcepto = null;
        buscar();
    }

    public void onSelect(SelectEvent event) {
        esNuevo = false;
        modoVista = "D";
        concepto = (Concepto) event.getObject();
    }

    public List<Concepto> complete(String query) {
        try {
            String codTipo = "";
            if (tipoConcepto != null) {
                codTipo = tipoConcepto.getCodigo();
            }

            lista = conceptoRN.getListaByBusqueda(MODULO, codTipo, query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Concepto>();
        }
    }

    public void onTipoChange() {

        // if (tipoConcepto != null) {
        //     concepto.setTipo(tipoConcepto.getCodigo());
        //     concepto.setTipoConcepto(tipoConcepto);
        // }
        if (concepto.getTipoConcepto() != null) {
            concepto.setTipo(concepto.getTipoConcepto().getCodigo());
            tipoConcepto = concepto.getTipoConcepto();
        }

    }

    public void seleccionar(Concepto c) {

        if (c == null) {
            return;
        }

        concepto = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void nuevoItemImpuesto() {

        try {
            conceptoRN.nuevoItemImpuesto(concepto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item impuesto: " + ex);
        }
    }

    public void eliminarItemImpuesto(ImpuestoPorConcepto impuestoPorConcepto) {

        try {
            conceptoRN.eliminarItemImpuesto(concepto, impuestoPorConcepto);
            JsfUtil.addWarningMessage("Se ha borrado el item impuesto ");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error, no es posible borrar el item impuesto " + " " + ex);
        }
    }

    public void asignarCodigoImpuesto(ImpuestoPorConcepto impuestoPorConcepto) {
        conceptoRN.asignarCodigoImpuesto(impuestoPorConcepto);
    }

    public List<Concepto> getLista() {
        return lista;
    }

    public void setLista(List<Concepto> lista) {
        this.lista = lista;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public List<TipoConcepto> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoConcepto> tipos) {
        this.tipos = tipos;
    }

    public TipoConcepto getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(TipoConcepto tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
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
