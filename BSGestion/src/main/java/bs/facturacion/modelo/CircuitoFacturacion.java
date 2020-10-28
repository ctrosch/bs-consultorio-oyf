/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.modelo;

import bs.administracion.modelo.Reporte;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.TipoProducto;
import bs.ventas.modelo.CanalVenta;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "fc_circuito")
@IdClass(CircuitoFacturacionPK.class)
@EntityListeners(AuditoriaListener.class)
public class CircuitoFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CIRCOM", nullable = false, length = 6)
    private String circom;
    @Id
    @Column(name = "CIRAPL", nullable = false, length = 6)
    private String cirapl;

    /**
     * Circuito de inicio
     */
    @JoinColumn(name = "CIRCOM", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoFacturacion circuitoComienzo;

    /**
     * Circuito a aplicar
     */
    @JoinColumn(name = "CIRAPL", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoFacturacion circuitoAplicacion;

    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "ACTZFC", nullable = false, length = 1)
    private String actualizaFacturacion;
    @Basic(optional = false)
    @Column(name = "ACTZST", nullable = false, length = 1)
    private String actualizaStock;
    @Basic(optional = false)
    @Column(name = "ACTZVT", nullable = false, length = 1)
    private String actualizaVenta;
    @Basic(optional = false)
    @Column(name = "ACTZCJ", nullable = false, length = 1)
    private String actualizaTesoreria;

    @Column(name = "ANULAC", length = 1)
    private String esAnulacion;

    @Column(name = "REVPEN", length = 1)
    private String reviertePendiente;

    @Column(name = "PRCTOT", nullable = false, length = 1)
    private String procesoTotal;

    @Column(name = "PRCOPC", length = 1)
    private String procesoTotalOpcional;

    //Verifica Recuperación de ítems de un único comprobante
    @Column(name = "ITMUNI", nullable = false)
    private String verificaItemsUnicoComprobante;

    @Column(name = "NCAPEN", length = 1)
    private String noCancelaPendiente;

    @Basic(optional = false)
    @Column(name = "COMPST", nullable = false, length = 1)
    private String comprometeStock;

    @Column(name = "ADMDEP", length = 1)
    private String admiteMultipleDeposito;

    @Column(name = "admant", length = 1)
    private String administraAnticipo;

    @JoinColumn(name = "repgrp", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteGrupo;

    @JoinColumn(name = "repdet", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteDetalle;

    @JoinColumn(name = "DEPDEF", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoFijo;

    @JoinColumn(name = "CVTDEF", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVentaDefecto;

    @Column(name = "LIMCRD", length = 1)
    private String controlaLimiteCredito;

    //Avisa o para
    @Column(name = "LIMACC", length = 1)
    private String accionExcesoLimiteCredito;

    @JoinColumn(name = "TIPUNI", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProductoUnico;

    @JoinColumn(name = "PRDUNI", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto productoUnico;

    @Column(name = "EDIENT", length = 1)
    private String editaEntidad;
    @Column(name = "EDICPT", length = 1)
    private String editaConcepto;
    @Column(name = "EDIIMP", length = 1)
    private String editaImporte;
    @Column(name = "EDIBON", length = 1)
    private String editaBonificacion;
    @Column(name = "EDICAN", length = 1)
    private String editaCantidad;
    @Column(name = "EDIUNI", length = 1)
    private String editaUnidadMedida;
    @Column(name = "EDIDES", length = 1)
    private String editaDescripcion;
    @Column(name = "EDIOBS", length = 1)
    private String editaObservacionesDetalle;
    @Column(name = "EDIDEP", length = 1)
    private String editaDeposito;

    @Column(name = "EDIFAC", length = 1)
    private String edifac;
    @Column(name = "EDICOF", length = 1)
    private String editaCoeficientes;
    @Column(name = "EDIFRK", length = 1)
    private String editaFormulaDeKit;
    @Column(name = "EDILPR", length = 1)
    private String editaListaPrecio;
    @Column(name = "EDIGRU", length = 1)
    private String editaGrupoBonificacion;
    @Column(name = "EDIPAG")
    private String editaCondicionPago;

    @Column(name = "AGREGA", length = 1)
    private String permiteAgregarItems;

    @Column(name = "COSCER", length = 1)
    private String pidePrecioCostoSiEsCero;

    @Column(name = "TRANSP", length = 1)
    private String transporteObligatorio;

    @Column(name = "PRIMER", length = 1)
    private String esPrimerPaso;
    @Column(name = "ULTIMO", length = 1)
    private String esUltimoPaso;

    @Column(name = "NCXDEV", length = 1)
    private String aplicaComprobantesCanceladosCtaCte;

    @Column(name = "CONGEL", length = 1)
    private String congelaPrecio;

    @Column(name = "CONCOT", length = 1)
    private String congelaCotizacion;

    @Column(name = "NPRCER", length = 1)
    private String permiteProductosConPrecioCero;
    @Column(name = "CNTCER", length = 1)
    private String permiteCantidadCero;
    @Column(name = "CNTDEF", length = 1)
    private int cantidadPorDefectoEnItems;
    @Column(name = "NPRDUP", length = 1)
    private String permiteProductosDuplicados;
    @Column(name = "DLGCNT", length = 1)
    private String dialogoCantidadSeleccionPendiente;

    @Column(name = "CTLPEN", length = 1)
    private String verificaPendiente;
    @Column(name = "ADATR1", nullable = false, length = 1)
    private String administraAtributo1;
    @Column(name = "ADATR2", nullable = false, length = 1)
    private String administraAtributo2;
    @Column(name = "ADATR3", nullable = false, length = 1)
    private String administraAtributo3;
    @Column(name = "ADATR4", nullable = false, length = 1)
    private String administraAtributo4;
    @Column(name = "ADATR5", nullable = false, length = 1)
    private String administraAtributo5;
    @Column(name = "ADATR6", nullable = false, length = 1)
    private String administraAtributo6;
    @Column(name = "ADATR7", nullable = false, length = 1)
    private String administraAtributo7;

    @Column(name = "CONBON", length = 1)
    private String congelaBonificacion;

    @Column(name = "MAXITM", length = 1)
    private Short cantidadMaximaDeItems;

    @Column(name = "CTRLPR", length = 1)
    private String controlaPrecio;

    @Column(name = "PREMIN", precision = 4)
    private double precioMinimo;
    @Column(name = "PREMAX", precision = 4)
    private double precioMaximo;

    @Lob
    @Column(name = "USRPRE", length = 65535)
    private String recuperacionPrecio;
    @Lob
    @Column(name = "USRBON", length = 65535)
    private String recuperaBonificacion;

    @Column(name = "ANUTOT", length = 1)
    private String esAnulacionTotal;

    @Column(name = "PICKDET", length = 1)
    private String pickingDetalle;

    @Column(name = "VALTCJ", length = 1)
    private String validaTotalContraTesoreria;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoFacturacion> itemCircuitoFacturacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoStock> itemCircuitoStock;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoVenta> itemCircuitoVenta;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoTesoreria> itemCircuitoTesoreria;

    @Transient
    Comprobante comprobanteFacturacion;

    @Transient
    Comprobante comprobanteVenta;

    @Transient
    Comprobante comprobanteStock;

    @Transient
    Comprobante Comprobante;

    @Transient
    private List<CircuitoFacturacion> circuitosRelacionados;

    public CircuitoFacturacion() {

        //Actualización de módulos
        this.actualizaFacturacion = "N";
        this.actualizaStock = "N";
        this.actualizaVenta = "N";
        this.actualizaTesoreria = "N";

        //Configuraciones de item
        this.editaCantidad = "S";
        this.permiteCantidadCero = "N";
        this.cantidadPorDefectoEnItems = 1;
        this.cantidadMaximaDeItems = 0;
        this.editaDescripcion = "N";
        this.editaObservacionesDetalle = "N";
        this.editaUnidadMedida = "N";
        this.permiteAgregarItems = "N";
        this.permiteProductosDuplicados = "N";
        this.editaFormulaDeKit = "N";
        this.tipoProductoUnico = null;
        this.productoUnico = null;

        //Configuraciones de precios
        this.editaImporte = "S";
        this.congelaPrecio = "N";
        this.editaListaPrecio = "S";
        this.editaBonificacion = "N";
        this.permiteProductosConPrecioCero = "N";
        this.pidePrecioCostoSiEsCero = "N";
        this.dialogoCantidadSeleccionPendiente = "N";

        this.congelaCotizacion = "N";
        this.congelaBonificacion = "N";
        this.controlaPrecio = "N";
        this.precioMinimo = 0;
        this.precioMaximo = 0;
        this.recuperacionPrecio = "N";
        this.recuperaBonificacion = "N";

        //Datos asociado a stock
        this.comprometeStock = "N";
        this.pickingDetalle = "N";
        this.editaDeposito = "S";
        this.depositoFijo = null;

        this.administraAtributo1 = "N";
        this.administraAtributo2 = "N";
        this.administraAtributo3 = "N";
        this.administraAtributo4 = "N";
        this.administraAtributo5 = "N";
        this.administraAtributo6 = "N";
        this.administraAtributo7 = "N";

        //Configuraciones de comprobante
        this.editaEntidad = "N";
        this.transporteObligatorio = "N";
        this.editaCondicionPago = "N";
        this.controlaLimiteCredito = "N";
        this.accionExcesoLimiteCredito = "N";
        this.aplicaComprobantesCanceladosCtaCte = "N";
        this.administraAnticipo = "N";

        //Configuraciones de pendientes
        this.esPrimerPaso = "N";
        this.esUltimoPaso = "N";
        this.esAnulacion = "N";
        this.esAnulacionTotal = "N";
        this.reviertePendiente = "N";
        this.procesoTotal = "N";
        this.procesoTotalOpcional = "N";
        this.verificaItemsUnicoComprobante = "N";
        this.noCancelaPendiente = "N";
        this.verificaPendiente = "N";

        //No se muestra
        this.admiteMultipleDeposito = "N";
        this.editaGrupoBonificacion = "N";
        this.editaCoeficientes = "N";
        this.editaConcepto = "N";
        this.edifac = "N";

        this.validaTotalContraTesoreria = "S";

        this.auditoria = new Auditoria();
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getCirapl() {
        return cirapl;
    }

    public void setCirapl(String cirapl) {
        this.cirapl = cirapl;
    }

    public CodigoCircuitoFacturacion getCircuitoComienzo() {
        return circuitoComienzo;
    }

    public void setCircuitoComienzo(CodigoCircuitoFacturacion circuitoComienzo) {
        this.circuitoComienzo = circuitoComienzo;
    }

    public CodigoCircuitoFacturacion getCircuitoAplicacion() {
        return circuitoAplicacion;
    }

    public void setCircuitoAplicacion(CodigoCircuitoFacturacion circuitoAplicacion) {
        this.circuitoAplicacion = circuitoAplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActualizaFacturacion() {
        return actualizaFacturacion;
    }

    public void setActualizaFacturacion(String actualizaFacturacion) {
        this.actualizaFacturacion = actualizaFacturacion;
    }

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
    }

    public String getActualizaVenta() {
        return actualizaVenta;
    }

    public void setActualizaVenta(String actualizaVenta) {
        this.actualizaVenta = actualizaVenta;
    }

    public String getActualizaTesoreria() {
        return actualizaTesoreria;
    }

    public void setActualizaTesoreria(String actualizaTesoreria) {
        this.actualizaTesoreria = actualizaTesoreria;
    }

    public String getEsAnulacion() {
        return esAnulacion;
    }

    public void setEsAnulacion(String esAnulacion) {
        this.esAnulacion = esAnulacion;
    }

    public String getAdministraAtributo1() {
        return administraAtributo1;
    }

    public void setAdministraAtributo1(String administraAtributo1) {
        this.administraAtributo1 = administraAtributo1;
    }

    public String getAdministraAtributo2() {
        return administraAtributo2;
    }

    public void setAdministraAtributo2(String administraAtributo2) {
        this.administraAtributo2 = administraAtributo2;
    }

    public String getAdministraAtributo3() {
        return administraAtributo3;
    }

    public void setAdministraAtributo3(String administraAtributo3) {
        this.administraAtributo3 = administraAtributo3;
    }

    public String getAdministraAtributo4() {
        return administraAtributo4;
    }

    public void setAdministraAtributo4(String administraAtributo4) {
        this.administraAtributo4 = administraAtributo4;
    }

    public String getAdministraAtributo5() {
        return administraAtributo5;
    }

    public void setAdministraAtributo5(String administraAtributo5) {
        this.administraAtributo5 = administraAtributo5;
    }

    public String getAdministraAtributo6() {
        return administraAtributo6;
    }

    public void setAdministraAtributo6(String administraAtributo6) {
        this.administraAtributo6 = administraAtributo6;
    }

    public String getAdministraAtributo7() {
        return administraAtributo7;
    }

    public void setAdministraAtributo7(String administraAtributo7) {
        this.administraAtributo7 = administraAtributo7;
    }

    public String getProcesoTotal() {
        return procesoTotal;
    }

    public void setProcesoTotal(String procesoTotal) {
        this.procesoTotal = procesoTotal;
    }

    public String getProcesoTotalOpcional() {
        return procesoTotalOpcional;
    }

    public void setProcesoTotalOpcional(String procesoTotalOpcional) {
        this.procesoTotalOpcional = procesoTotalOpcional;
    }

    public String getVerificaItemsUnicoComprobante() {
        return verificaItemsUnicoComprobante;
    }

    public void setVerificaItemsUnicoComprobante(String verificaItemsUnicoComprobante) {
        this.verificaItemsUnicoComprobante = verificaItemsUnicoComprobante;
    }

    public String getComprometeStock() {
        return comprometeStock;
    }

    public void setComprometeStock(String comprometeStock) {
        this.comprometeStock = comprometeStock;
    }

    public String getAdmiteMultipleDeposito() {
        return admiteMultipleDeposito;
    }

    public void setAdmiteMultipleDeposito(String admiteMultipleDeposito) {
        this.admiteMultipleDeposito = admiteMultipleDeposito;
    }

    public String getAdministraAnticipo() {
        return administraAnticipo;
    }

    public void setAdministraAnticipo(String administraAnticipo) {
        this.administraAnticipo = administraAnticipo;
    }

    public Reporte getReporteGrupo() {
        return reporteGrupo;
    }

    public void setReporteGrupo(Reporte reporteGrupo) {
        this.reporteGrupo = reporteGrupo;
    }

    public Reporte getReporteDetalle() {
        return reporteDetalle;
    }

    public void setReporteDetalle(Reporte reporteDetalle) {
        this.reporteDetalle = reporteDetalle;
    }

    public Deposito getDepositoFijo() {
        return depositoFijo;
    }

    public void setDepositoFijo(Deposito depositoFijo) {
        this.depositoFijo = depositoFijo;
    }

    public String getControlaLimiteCredito() {
        return controlaLimiteCredito;
    }

    public void setControlaLimiteCredito(String controlaLimiteCredito) {
        this.controlaLimiteCredito = controlaLimiteCredito;
    }

    public String getAccionExcesoLimiteCredito() {
        return accionExcesoLimiteCredito;
    }

    public void setAccionExcesoLimiteCredito(String accionExcesoLimiteCredito) {
        this.accionExcesoLimiteCredito = accionExcesoLimiteCredito;
    }

    public TipoProducto getTipoProductoUnico() {
        return tipoProductoUnico;
    }

    public void setTipoProductoUnico(TipoProducto tipoProductoUnico) {
        this.tipoProductoUnico = tipoProductoUnico;
    }

    public Producto getProductoUnico() {
        return productoUnico;
    }

    public void setProductoUnico(Producto productoUnico) {
        this.productoUnico = productoUnico;
    }

    public String getEditaConcepto() {
        return editaConcepto;
    }

    public void setEditaConcepto(String editaConcepto) {
        this.editaConcepto = editaConcepto;
    }

    public String getEditaImporte() {
        return editaImporte;
    }

    public void setEditaImporte(String editaImporte) {
        this.editaImporte = editaImporte;
    }

    public String getEditaBonificacion() {
        return editaBonificacion;
    }

    public void setEditaBonificacion(String editaBonificacion) {
        this.editaBonificacion = editaBonificacion;
    }

    public String getEditaCantidad() {
        return editaCantidad;
    }

    public void setEditaCantidad(String editaCantidad) {
        this.editaCantidad = editaCantidad;
    }

    public String getEdifac() {
        return edifac;
    }

    public void setEdifac(String edifac) {
        this.edifac = edifac;
    }

    public String getEditaCoeficientes() {
        return editaCoeficientes;
    }

    public void setEditaCoeficientes(String editaCoeficientes) {
        this.editaCoeficientes = editaCoeficientes;
    }

    public String getEditaFormulaDeKit() {
        return editaFormulaDeKit;
    }

    public void setEditaFormulaDeKit(String editaFormulaDeKit) {
        this.editaFormulaDeKit = editaFormulaDeKit;
    }

    public String getEditaListaPrecio() {
        return editaListaPrecio;
    }

    public void setEditaListaPrecio(String editaListaPrecio) {
        this.editaListaPrecio = editaListaPrecio;
    }

    public String getEditaGrupoBonificacion() {
        return editaGrupoBonificacion;
    }

    public void setEditaGrupoBonificacion(String editaGrupoBonificacion) {
        this.editaGrupoBonificacion = editaGrupoBonificacion;
    }

    public String getEditaCondicionPago() {
        return editaCondicionPago;
    }

    public void setEditaCondicionPago(String editaCondicionPago) {
        this.editaCondicionPago = editaCondicionPago;
    }

    public String getPermiteAgregarItems() {
        return permiteAgregarItems;
    }

    public void setPermiteAgregarItems(String permiteAgregarItems) {
        this.permiteAgregarItems = permiteAgregarItems;
    }

    public String getEsPrimerPaso() {
        return esPrimerPaso;
    }

    public void setEsPrimerPaso(String esPrimerPaso) {
        this.esPrimerPaso = esPrimerPaso;
    }

    public String getEsUltimoPaso() {
        return esUltimoPaso;
    }

    public void setEsUltimoPaso(String esUltimoPaso) {
        this.esUltimoPaso = esUltimoPaso;
    }

    public String getAplicaComprobantesCanceladosCtaCte() {
        return aplicaComprobantesCanceladosCtaCte;
    }

    public void setAplicaComprobantesCanceladosCtaCte(String aplicaComprobantesCanceladosCtaCte) {
        this.aplicaComprobantesCanceladosCtaCte = aplicaComprobantesCanceladosCtaCte;
    }

    public String getCongelaPrecio() {
        return congelaPrecio;
    }

    public void setCongelaPrecio(String congelaPrecio) {
        this.congelaPrecio = congelaPrecio;
    }

    public String getPermiteProductosConPrecioCero() {
        return permiteProductosConPrecioCero;
    }

    public void setPermiteProductosConPrecioCero(String permiteProductosConPrecioCero) {
        this.permiteProductosConPrecioCero = permiteProductosConPrecioCero;
    }

    public String getPermiteCantidadCero() {
        return permiteCantidadCero;
    }

    public void setPermiteCantidadCero(String permiteCantidadCero) {
        this.permiteCantidadCero = permiteCantidadCero;
    }

    public String getPermiteProductosDuplicados() {
        return permiteProductosDuplicados;
    }

    public void setPermiteProductosDuplicados(String permiteProductosDuplicados) {
        this.permiteProductosDuplicados = permiteProductosDuplicados;
    }

    public String getVerificaPendiente() {
        return verificaPendiente;
    }

    public void setVerificaPendiente(String verificaPendiente) {
        this.verificaPendiente = verificaPendiente;
    }

    public String getNoCancelaPendiente() {
        return noCancelaPendiente;
    }

    public void setNoCancelaPendiente(String noCancelaPendiente) {
        this.noCancelaPendiente = noCancelaPendiente;
    }

    public String getCongelaBonificacion() {
        return congelaBonificacion;
    }

    public void setCongelaBonificacion(String congelaBonificacion) {
        this.congelaBonificacion = congelaBonificacion;
    }

    public Short getCantidadMaximaDeItems() {
        return cantidadMaximaDeItems;
    }

    public void setCantidadMaximaDeItems(Short cantidadMaximaDeItems) {
        this.cantidadMaximaDeItems = cantidadMaximaDeItems;
    }

    public String getControlaPrecio() {
        return controlaPrecio;
    }

    public void setControlaPrecio(String controlaPrecio) {
        this.controlaPrecio = controlaPrecio;
    }

    public double getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(double precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public double getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(double precioMaximo) {
        this.precioMaximo = precioMaximo;
    }

    public String getRecuperacionPrecio() {
        return recuperacionPrecio;
    }

    public void setRecuperacionPrecio(String recuperacionPrecio) {
        this.recuperacionPrecio = recuperacionPrecio;
    }

    public String getRecuperaBonificacion() {
        return recuperaBonificacion;
    }

    public void setRecuperaBonificacion(String recuperaBonificacion) {
        this.recuperaBonificacion = recuperaBonificacion;
    }

    public String getEsAnulacionTotal() {
        return esAnulacionTotal;
    }

    public void setEsAnulacionTotal(String esAnulacionTotal) {
        this.esAnulacionTotal = esAnulacionTotal;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemCircuitoFacturacion> getItemCircuitoFacturacion() {
        return itemCircuitoFacturacion;
    }

    public void setItemCircuitoFacturacion(List<ItemCircuitoFacturacion> itemCircuitoFacturacion) {
        this.itemCircuitoFacturacion = itemCircuitoFacturacion;
    }

    public List<ItemCircuitoStock> getItemCircuitoStock() {
        return itemCircuitoStock;
    }

    public void setItemCircuitoStock(List<ItemCircuitoStock> itemCircuitoStock) {
        this.itemCircuitoStock = itemCircuitoStock;
    }

    public List<ItemCircuitoVenta> getItemCircuitoVenta() {
        return itemCircuitoVenta;
    }

    public void setItemCircuitoVenta(List<ItemCircuitoVenta> itemCircuitoVenta) {
        this.itemCircuitoVenta = itemCircuitoVenta;
    }

    public List<ItemCircuitoTesoreria> getItemCircuitoTesoreria() {
        return itemCircuitoTesoreria;
    }

    public void setItemCircuitoTesoreria(List<ItemCircuitoTesoreria> itemCircuitoTesoreria) {
        this.itemCircuitoTesoreria = itemCircuitoTesoreria;
    }

    public Comprobante getComprobanteFacturacion() {
        return comprobanteFacturacion;
    }

    public void setComprobanteFacturacion(Comprobante comprobanteFacturacion) {
        this.comprobanteFacturacion = comprobanteFacturacion;
    }

    public Comprobante getComprobanteVenta() {
        return comprobanteVenta;
    }

    public void setComprobanteVenta(Comprobante comprobanteVenta) {
        this.comprobanteVenta = comprobanteVenta;
    }

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public Comprobante getComprobanteTesoreria() {
        return Comprobante;
    }

    public void setComprobanteTesoreria(Comprobante Comprobante) {
        this.Comprobante = Comprobante;
    }

    public String getEditaUnidadMedida() {
        return editaUnidadMedida;
    }

    public void setEditaUnidadMedida(String editarUnidadMedida) {
        this.editaUnidadMedida = editarUnidadMedida;
    }

    public String getEditaDescripcion() {
        return editaDescripcion;
    }

    public void setEditaDescripcion(String editaDescripcion) {
        this.editaDescripcion = editaDescripcion;
    }

    public String getEditaObservacionesDetalle() {
        return editaObservacionesDetalle;
    }

    public void setEditaObservacionesDetalle(String editaObservacionesDetalle) {
        this.editaObservacionesDetalle = editaObservacionesDetalle;
    }

    public String getEditaEntidad() {
        return editaEntidad;
    }

    public void setEditaEntidad(String editaEntidad) {
        this.editaEntidad = editaEntidad;
    }

    public List<CircuitoFacturacion> getCircuitosRelacionados() {
        return circuitosRelacionados;
    }

    public void setCircuitosRelacionados(List<CircuitoFacturacion> circuitosRelacionados) {
        this.circuitosRelacionados = circuitosRelacionados;
    }

    public String getPickingDetalle() {
        return pickingDetalle;
    }

    public void setPickingDetalle(String pickingDetalle) {
        this.pickingDetalle = pickingDetalle;
    }

    public String getEditaDeposito() {
        return editaDeposito;
    }

    public void setEditaDeposito(String editaDeposito) {
        this.editaDeposito = editaDeposito;
    }

    public String getPidePrecioCostoSiEsCero() {
        return pidePrecioCostoSiEsCero;
    }

    public void setPidePrecioCostoSiEsCero(String pidePrecioCostoSiEsCero) {
        this.pidePrecioCostoSiEsCero = pidePrecioCostoSiEsCero;
    }

    public Comprobante getComprobante() {
        return Comprobante;
    }

    public void setComprobante(Comprobante Comprobante) {
        this.Comprobante = Comprobante;
    }

    public String getReviertePendiente() {
        return reviertePendiente;
    }

    public void setReviertePendiente(String reviertePendiente) {
        this.reviertePendiente = reviertePendiente;
    }

    public String getTransporteObligatorio() {
        return transporteObligatorio;
    }

    public void setTransporteObligatorio(String transporteObligatorio) {
        this.transporteObligatorio = transporteObligatorio;
    }

    public int getCantidadPorDefectoEnItems() {
        return cantidadPorDefectoEnItems;
    }

    public void setCantidadPorDefectoEnItems(int cantidadPorDefectoEnItems) {
        this.cantidadPorDefectoEnItems = cantidadPorDefectoEnItems;
    }

    public String getCongelaCotizacion() {
        return congelaCotizacion;
    }

    public void setCongelaCotizacion(String congelaCotizacion) {
        this.congelaCotizacion = congelaCotizacion;
    }

    public String getValidaTotalContraTesoreria() {
        return validaTotalContraTesoreria;
    }

    public void setValidaTotalContraTesoreria(String validaTotalContraTesoreria) {
        this.validaTotalContraTesoreria = validaTotalContraTesoreria;
    }

    public CanalVenta getCanalVentaDefecto() {
        return canalVentaDefecto;
    }

    public void setCanalVentaDefecto(CanalVenta canalVentaDefecto) {
        this.canalVentaDefecto = canalVentaDefecto;
    }

    public String getDialogoCantidadSeleccionPendiente() {
        return dialogoCantidadSeleccionPendiente;
    }

    public void setDialogoCantidadSeleccionPendiente(String dialogoCantidadSeleccionPendiente) {
        this.dialogoCantidadSeleccionPendiente = dialogoCantidadSeleccionPendiente;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 37 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CircuitoFacturacion other = (CircuitoFacturacion) obj;
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.cirapl == null) ? (other.cirapl != null) : !this.cirapl.equals(other.cirapl)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isd.facturacion.modelo.FC_Circuito[circom=" + circom + " cirapl=" + cirapl + " ]";
    }

}
