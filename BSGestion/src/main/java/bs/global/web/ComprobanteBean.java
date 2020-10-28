/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.contabilidad.web.DistribucionBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Concepto;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.FormularioPorSituacionIVA;
import bs.global.modelo.ItemDistribucionConcepto;
import bs.global.modelo.Moneda;
import bs.global.rn.ComprobanteRN;
import bs.global.util.JsfUtil;
import bs.stock.modelo.Deposito;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
public class ComprobanteBean extends GenericBean implements Serializable {

    @EJB
    private ComprobanteRN comprobanteRN;

    private Comprobante comprobante;
    private ConceptoComprobante conceptoComprobante;
    private ItemDistribucionConcepto itemDistribucion;
    private List<Comprobante> lista;
    protected String MODULO = "-";

    //Atributos para filtro
    private String tipoMovimiento;
    private Deposito deposito;
    private Deposito depositoTransferencia;
    private String tipoImputacion;
    private Moneda monedaRegistracion;
    private String debeHaber;
    private String signoAplicacionCuentaCorriente;
    private String comprobanteInterno;
    private String seIncluyeEnSubDiario;
    private String esComprobanteReversion;
    private String tipoComprobante;
    private String seIncluyeEnEstadisticas;

    @ManagedProperty(value = "#{distribucionBean}")
    private DistribucionBean distribucionBean;

    /**
     * Creates a new instance of ComprobanteTesoreriaBean
     */
    public ComprobanteBean() {

    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        comprobante = new Comprobante();
        comprobante.setModulo(MODULO);
    }

    public void guardar(boolean nuevo) {

        try {
            if (comprobante != null) {

                comprobante = comprobanteRN.guardar(comprobante, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
//            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            comprobante.getAuditoria().setDebaja(habDes);
            comprobante = comprobanteRN.guardar(comprobante, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            comprobanteRN.eliminar(comprobante);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = comprobanteRN.getListaByBusqueda(MODULO, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (tipoMovimiento != null && !tipoMovimiento.isEmpty()) {

            filtro.put("tipoMovimiento = ", "'" + tipoMovimiento + "'");
        }

        if (deposito != null) {
            filtro.put("deposito.codigo = ", "'" + deposito.getCodigo() + "'");
        }

        if (depositoTransferencia != null) {
            filtro.put("depositoTransferencia.codigo = ", "'" + depositoTransferencia.getCodigo() + "'");
        }

        if (tipoImputacion != null && !tipoImputacion.isEmpty()) {

            filtro.put("tipoImputacion = ", "'" + tipoImputacion + "'");
        }

        if (monedaRegistracion != null) {
            filtro.put("monedaRegistracion.codigo = ", "'" + monedaRegistracion.getCodigo() + "'");
        }

        if (debeHaber != null && !debeHaber.isEmpty()) {

            filtro.put("debeHaber = ", "'" + debeHaber + "'");
        }

        if (signoAplicacionCuentaCorriente != null && !signoAplicacionCuentaCorriente.isEmpty()) {

            filtro.put("signoAplicacionCuentaCorriente = ", "'" + signoAplicacionCuentaCorriente + "'");
        }

        if (comprobanteInterno != null && !comprobanteInterno.isEmpty()) {

            filtro.put("comprobanteInterno = ", "'" + comprobanteInterno + "'");
        }

        if (seIncluyeEnSubDiario != null && !seIncluyeEnSubDiario.isEmpty()) {

            filtro.put("seIncluyeEnSubDiario = ", "'" + seIncluyeEnSubDiario + "'");
        }
        if (esComprobanteReversion != null && !esComprobanteReversion.isEmpty()) {

            filtro.put("esComprobanteReversion = ", "'" + esComprobanteReversion + "'");
        }

        if (tipoComprobante != null && !tipoComprobante.isEmpty()) {

            filtro.put("tipoComprobante = ", "'" + tipoComprobante + "'");
        }

        if (seIncluyeEnEstadisticas != null && !seIncluyeEnEstadisticas.isEmpty()) {

            filtro.put("seIncluyeEnEstadisticas = ", "'" + seIncluyeEnEstadisticas + "'");
        }

    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        tipoMovimiento = null;
        deposito = null;
        depositoTransferencia = null;
        tipoImputacion = "";
        monedaRegistracion = null;
        debeHaber = "";
        signoAplicacionCuentaCorriente = "";
        comprobanteInterno = "";
        seIncluyeEnSubDiario = "";
        esComprobanteReversion = "";
        tipoComprobante = "";
        seIncluyeEnEstadisticas = "";

        buscar();
    }

    public List<Comprobante> complete(String query) {
        try {
            lista = comprobanteRN.getListaByBusqueda(MODULO, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void onSelect(SelectEvent event) {

        comprobante = (Comprobante) event.getObject();
        comprobanteRN.cargarCentrosCostos(comprobante);
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Comprobante c) {

        if (c == null) {
            return;
        }

        comprobante = c;
        comprobanteRN.cargarCentrosCostos(comprobante);
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() {

        try {
            if (comprobante == null) {
                JsfUtil.addErrorMessage("No hay comprobante activo");
                return;
            }

            comprobante = comprobanteRN.duplicar(comprobante);
            esNuevo = true;
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (comprobante == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    //---------------------------------------------------------------------------
    //CONCEPTOS
    //---------------------------------------------------------------------------
    public void nuevoItemConcepto() {

        try {
            comprobanteRN.nuevoItemConcepto(comprobante);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item concepto: " + ex);
        }
    }

    protected void onConceptoSelect(SelectEvent event) {
        try {
            Concepto concepto = (Concepto) event.getObject();
            comprobanteRN.asignarConceptoItemConcepto(comprobante, concepto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar concepto: " + ex);
        }
    }

    public void onConceptoAsociadoSelect(SelectEvent event) {

        try {
            Concepto concepto = (Concepto) event.getObject();
            comprobanteRN.asignarConceptoItemConcepto(comprobante, concepto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar concepto: " + ex);
        }
    }

    public void eliminarItemConcepto(ConceptoComprobante conceptoComprobante) {

        try {
            comprobanteRN.eliminarItemConcepto(comprobante, conceptoComprobante);
            JsfUtil.addWarningMessage("Se ha borrado el item concepto " + (conceptoComprobante.getConcepto() != null ? conceptoComprobante.getConcepto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (conceptoComprobante.getConcepto() != null ? conceptoComprobante.getConcepto().getDescripcion() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //FORMULARIO POR SITUACIÓN DE IVA
    //---------------------------------------------------------------------------
    public void nuevoItemFormulario() {

        try {
            comprobanteRN.nuevoItemFormulario(comprobante);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item formulario: " + ex);
        }
    }

    protected void onFormularioSelect(SelectEvent event) {

        //        try {
        //            Formulario formulario1 = (Formulario) event.getObject();
        //            comprobanteRN.asignarFormularioItemFormulario(comprobante, formulario1);
        //        } catch (ExcepcionGeneralSistema ex) {
        //            JsfUtil.addErrorMessage("No es posible asignar formulario: " + ex);
        //        }
    }

    public void onSituacionIvaSelect(SelectEvent event) {

        try {
            CondicionDeIva condicionIva = (CondicionDeIva) event.getObject();
            comprobanteRN.asignarSituacionIvaItemFormulario(comprobante, condicionIva);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar condición de iva: " + ex);
        }
    }

    public void eliminarItemFormulario(FormularioPorSituacionIVA itemFormulario) {

        try {
            comprobanteRN.eliminarItemFormulario(comprobante, itemFormulario);
            JsfUtil.addWarningMessage("Se ha borrado el item formulario " + (itemFormulario.getFormulario() != null ? itemFormulario.getFormulario().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemFormulario != null && itemFormulario.getFormulario() != null ? itemFormulario.getFormulario().getDescripcion() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    // DISTRIBUCION POR CONCEPTO
    //---------------------------------------------------------------------------
    public void seleccionarItemDistribucion(ItemDistribucionConcepto itemSeleccionado) {

        itemDistribucion = itemSeleccionado;

        if (itemDistribucion.getCentroCosto() != null) {
            distribucionBean.setCentroCosto(itemDistribucion.getCentroCosto());
            distribucionBean.buscar();
        }
    }

    public void procesarItemDistribucion() {

        //System.err.println("itemDistribucion " + itemDistribucion);
        //System.err.println("distribucionBean.getDistribucion() " + distribucionBean.getDistribucion());
        if (itemDistribucion != null && distribucionBean.getDistribucion() != null) {
            itemDistribucion.setDistribucion(distribucionBean.getDistribucion());
        }
    }

    //---------------------------------------------------------------------------
    @Override
    public Comprobante getComprobante() {
        return comprobante;
    }

    @Override
    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public List<Comprobante> getLista() {
        return lista;
    }

    public void setLista(List<Comprobante> lista) {
        this.lista = lista;
    }

    public DistribucionBean getDistribucionBean() {
        return distribucionBean;
    }

    public void setDistribucionBean(DistribucionBean distribucionBean) {
        this.distribucionBean = distribucionBean;
    }

    public ConceptoComprobante getConceptoComprobante() {
        return conceptoComprobante;
    }

    public void setConceptoComprobante(ConceptoComprobante conceptoComprobante) {
        this.conceptoComprobante = conceptoComprobante;
    }

    public ItemDistribucionConcepto getItemDistribucion() {
        return itemDistribucion;
    }

    public void setItemDistribucion(ItemDistribucionConcepto itemDistribucion) {
        this.itemDistribucion = itemDistribucion;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Deposito getDepositoTransferencia() {
        return depositoTransferencia;
    }

    public void setDepositoTransferencia(Deposito depositoTransferencia) {
        this.depositoTransferencia = depositoTransferencia;
    }

    public String getTipoImputacion() {
        return tipoImputacion;
    }

    public void setTipoImputacion(String tipoImputacion) {
        this.tipoImputacion = tipoImputacion;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public String getSignoAplicacionCuentaCorriente() {
        return signoAplicacionCuentaCorriente;
    }

    public void setSignoAplicacionCuentaCorriente(String signoAplicacionCuentaCorriente) {
        this.signoAplicacionCuentaCorriente = signoAplicacionCuentaCorriente;
    }

    public String getComprobanteInterno() {
        return comprobanteInterno;
    }

    public void setComprobanteInterno(String comprobanteInterno) {
        this.comprobanteInterno = comprobanteInterno;
    }

    public String getSeIncluyeEnSubDiario() {
        return seIncluyeEnSubDiario;
    }

    public void setSeIncluyeEnSubDiario(String seIncluyeEnSubDiario) {
        this.seIncluyeEnSubDiario = seIncluyeEnSubDiario;
    }

    public String getEsComprobanteReversion() {
        return esComprobanteReversion;
    }

    public void setEsComprobanteReversion(String esComprobanteReversion) {
        this.esComprobanteReversion = esComprobanteReversion;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getSeIncluyeEnEstadisticas() {
        return seIncluyeEnEstadisticas;
    }

    public void setSeIncluyeEnEstadisticas(String seIncluyeEnEstadisticas) {
        this.seIncluyeEnEstadisticas = seIncluyeEnEstadisticas;
    }

    public ComprobanteRN getComprobanteRN() {
        return comprobanteRN;
    }

}
