/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.modelo;

import bs.administracion.modelo.Reporte;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.TipoProducto;
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
@Table(name = "co_circuito")
@IdClass(CircuitoCompraPK.class)
@EntityListeners(AuditoriaListener.class)
public class CircuitoCompra implements Serializable {

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
    private CodigoCircuitoCompra circuitoComienzo;

    /**
     * Circuito a aplicar
     */
    @JoinColumn(name = "CIRAPL", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoCompra circuitoAplicacion;

    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "ACTZCO", nullable = false, length = 1)
    private String actualizaCompra;
    @Basic(optional = false)
    @Column(name = "ACTZST", nullable = false, length = 1)
    private String actualizaStock;
    @Basic(optional = false)
    @Column(name = "ACTZPV", nullable = false, length = 1)
    private String actualizaProveedor;
    @Basic(optional = false)
    @Column(name = "ACTZCJ", nullable = false, length = 1)
    private String actualizaTesoreria;

    @Column(name = "ANULAC", length = 1)
    private String esAnulacion;

    @Column(name = "PRCTOT", nullable = false, length = 1)
    private String procesoTotal;

    @Column(name = "PRCOPC", length = 1)
    private String procesoTotalOpcional;

    //Verifica Recuperación de ítems de un único comprobante
    @Column(name = "ITMUNI", nullable = false)
    private String verificaItemsUnicoComprobante;

    @Column(name = "NCAPEN", length = 1)
    private String noCancelaPendiente;

    @Column(name = "REVPEN", length = 1)
    private String reviertePendiente;

    @Basic(optional = false)
    @Column(name = "COMPST", nullable = false, length = 1)
    private String comprometeStock;

    @Basic(optional = false)
    @Column(name = "PENING", nullable = false, length = 1)
    private String pendienteIngreso;

    @JoinColumn(name = "REPGRP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteGrupo;

    @JoinColumn(name = "REPDET", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteDetalle;

    @Column(name = "ADMDEP", length = 1)
    private String admiteMultipleDeposito;

    @JoinColumn(name = "DEPDEF", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoFijo;

    @Basic(optional = false)
    @Column(name = "DEPOBL", nullable = false, length = 1)
    private String depositoObligatorio;

    @Basic(optional = false)
    @Column(name = "RDEPPA", nullable = false, length = 1)
    private String recuperaDepositoPasoAnterior;

    @Column(name = "LIMCRD", length = 1)
    private String controlaLimiteCredito;
    //Avisa o para
    @Column(name = "LIMACC", length = 1)
    private String accionExcesoLimiteCrdito;

    @JoinColumn(name = "TIPUNI", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProductoUnico;

    @JoinColumn(name = "PRDUNI", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto productoUnico;

    @Column(name = "EDIDES", length = 1)
    private String editaDescripcion;
    @Column(name = "EDICPT", length = 1)
    private String editaConcepto;
    @Column(name = "EDIIMP", length = 1)
    private String editaImporte;
    @Column(name = "EDIBON", length = 1)
    private String editaBonificacion;
    @Column(name = "EDICAN", length = 1)
    private String editaCantidad;
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
    @Column(name = "EDIOBS", length = 1)
    private String editaObservacionesDetalle;

    @Column(name = "AGREGA", length = 1)
    private String permiteAgregarItems;

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
    @Column(name = "PCCCER", length = 1)
    private String permiteComprobanteConPrecioCero;
    @Column(name = "CNTCER", length = 1)
    private String permiteCantidadCero;
    @Column(name = "NPRDUP", length = 1)
    private String permiteProductosDuplicados;
    @Column(name = "DLGCNT", length = 1)
    private String dialogoCantidadSeleccionPendiente;

    @Column(name = "CTLPEN", length = 1)
    private String verificaPendiente;

    @Column(name = "ADEPOS", nullable = false, length = 1)
    private String administraDeposito;

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
    private Long precioMinimo;
    @Column(name = "PREMAX", precision = 4)
    private Long precioMaximo;

    @Lob
    @Column(name = "USRPRE", length = 65535)
    private String recuperacionPrecio;
    @Lob
    @Column(name = "USRBON", length = 65535)
    private String recuperaBonificacion;

    @Column(name = "ANUTOT", length = 1)
    private String esAnulacionTotal;

    @Column(name = "pickdet", length = 1)
    private String pickingDetalle;

    @Column(name = "VALTCJ", length = 1)
    private String validaTotalContraTesoreria;

    @Column(name = "ASECTO", length = 1)
    private String administraSector;

    @Column(name = "ADMSEC", length = 1)
    private String admiteMultipleSector;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoCompraCompra> itemCircuitoCompra;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoCompraStock> itemCircuitoStock;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoCompraProveedor> itemCircuitoProveedor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoCompraTesoreria> itemCircuitoTesoreria;

    @Transient
    Comprobante comprobanteCompra;

    @Transient
    Comprobante comprobanteProveedor;

    @Transient
    Comprobante comprobanteStock;

    @Transient
    Comprobante Comprobante;

    @Transient
    private List<CircuitoCompra> circuitosRelacionados;

    public CircuitoCompra() {

        this.actualizaCompra = "N";
        this.actualizaStock = "N";
        this.actualizaProveedor = "N";
        this.actualizaTesoreria = "N";
        this.comprometeStock = "N";
        this.administraDeposito = "N";
        this.administraAtributo1 = "N";
        this.administraAtributo2 = "N";
        this.administraAtributo3 = "N";
        this.administraAtributo4 = "N";
        this.administraAtributo5 = "N";
        this.administraAtributo6 = "N";
        this.administraAtributo7 = "N";
        this.editaDescripcion = "N";
        this.editaBonificacion = "N";
        this.editaCoeficientes = "N";
        this.editaConcepto = "N";
        this.editaCondicionPago = "N";
        this.editaFormulaDeKit = "N";
        this.editaGrupoBonificacion = "N";
        this.editaImporte = "N";
        this.editaListaPrecio = "N";
        this.editaCantidad = "S";
        this.pendienteIngreso = "N";
        this.procesoTotal = "N";
        this.pickingDetalle = "N";
        this.validaTotalContraTesoreria = "S";
        this.permiteProductosConPrecioCero = "N";
        this.permiteComprobanteConPrecioCero = "N";
        this.reviertePendiente = "N";
        this.permiteAgregarItems = "S";
        this.permiteProductosDuplicados = "N";
        this.permiteCantidadCero = "N";
        this.editaObservacionesDetalle = "N";
        this.administraSector = "N";
        this.congelaCotizacion = "S";
        this.congelaPrecio = "S";
        this.congelaBonificacion = "S";
        this.esAnulacion = "N";
        this.esAnulacionTotal = "N";
        this.verificaItemsUnicoComprobante = "N";
        this.depositoObligatorio = "N";
        this.recuperaDepositoPasoAnterior = "S";
        this.noCancelaPendiente = "N";
        this.dialogoCantidadSeleccionPendiente = "N";

        this.auditoria = new Auditoria();

    }

    public String getAdministraSector() {
        return administraSector;
    }

    public void setAdministraSector(String administraSector) {
        this.administraSector = administraSector;
    }

    public String getAdmiteMultipleSector() {
        return admiteMultipleSector;
    }

    public void setAdmiteMultipleSector(String admiteMultipleSector) {
        this.admiteMultipleSector = admiteMultipleSector;
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

    public CodigoCircuitoCompra getCircuitoComienzo() {
        return circuitoComienzo;
    }

    public void setCircuitoComienzo(CodigoCircuitoCompra circuitoComienzo) {

        if (circuitoComienzo != null) {
            circom = circuitoComienzo.getCodigo();
        }

        this.circuitoComienzo = circuitoComienzo;
    }

    public CodigoCircuitoCompra getCircuitoAplicacion() {
        return circuitoAplicacion;
    }

    public void setCircuitoAplicacion(CodigoCircuitoCompra circuitoAplicacion) {

        if (circuitoAplicacion != null) {
            cirapl = circuitoAplicacion.getCodigo();
        }

        this.circuitoAplicacion = circuitoAplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActualizaCompra() {
        return actualizaCompra;
    }

    public void setActualizaCompra(String actualizaCompra) {
        this.actualizaCompra = actualizaCompra;
    }

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
    }

    public String getActualizaProveedor() {
        return actualizaProveedor;
    }

    public void setActualizaProveedor(String actualizaProveedor) {
        this.actualizaProveedor = actualizaProveedor;
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

    public String getNoCancelaPendiente() {
        return noCancelaPendiente;
    }

    public void setNoCancelaPendiente(String noCancelaPendiente) {
        this.noCancelaPendiente = noCancelaPendiente;
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

    public String getAccionExcesoLimiteCrdito() {
        return accionExcesoLimiteCrdito;
    }

    public void setAccionExcesoLimiteCrdito(String accionExcesoLimiteCrdito) {
        this.accionExcesoLimiteCrdito = accionExcesoLimiteCrdito;
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

    public String getAdministraDeposito() {
        return administraDeposito;
    }

    public void setAdministraDeposito(String administraDeposito) {
        this.administraDeposito = administraDeposito;
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

    public Long getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(Long precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public Long getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(Long precioMaximo) {
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

    public List<ItemCircuitoCompraCompra> getItemCircuitoCompra() {
        return itemCircuitoCompra;
    }

    public void setItemCircuitoCompra(List<ItemCircuitoCompraCompra> itemCircuitoCompra) {
        this.itemCircuitoCompra = itemCircuitoCompra;
    }

    public List<ItemCircuitoCompraStock> getItemCircuitoStock() {
        return itemCircuitoStock;
    }

    public void setItemCircuitoStock(List<ItemCircuitoCompraStock> itemCircuitoStock) {
        this.itemCircuitoStock = itemCircuitoStock;
    }

    public List<ItemCircuitoCompraProveedor> getItemCircuitoProveedor() {
        return itemCircuitoProveedor;
    }

    public void setItemCircuitoProveedor(List<ItemCircuitoCompraProveedor> itemCircuitoProveedor) {
        this.itemCircuitoProveedor = itemCircuitoProveedor;
    }

    public List<ItemCircuitoCompraTesoreria> getItemCircuitoTesoreria() {
        return itemCircuitoTesoreria;
    }

    public void setItemCircuitoTesoreria(List<ItemCircuitoCompraTesoreria> itemCircuitoTesoreria) {
        this.itemCircuitoTesoreria = itemCircuitoTesoreria;
    }

    public Comprobante getComprobanteCompra() {
        return comprobanteCompra;
    }

    public void setComprobanteCompra(Comprobante comprobanteCompra) {
        this.comprobanteCompra = comprobanteCompra;
    }

    public Comprobante getComprobanteProveedor() {
        return comprobanteProveedor;
    }

    public void setComprobanteProveedor(Comprobante comprobanteProveedor) {
        this.comprobanteProveedor = comprobanteProveedor;
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

    public String getEditaObservacionesDetalle() {
        return editaObservacionesDetalle;
    }

    public void setEditaObservacionesDetalle(String editaObservacionesDetalle) {
        this.editaObservacionesDetalle = editaObservacionesDetalle;
    }

    public String getEditaDescripcion() {
        return editaDescripcion;
    }

    public void setEditaDescripcion(String editaDescripcion) {
        this.editaDescripcion = editaDescripcion;
    }

    public List<CircuitoCompra> getCircuitosRelacionados() {
        return circuitosRelacionados;
    }

    public void setCircuitosRelacionados(List<CircuitoCompra> circuitosRelacionados) {
        this.circuitosRelacionados = circuitosRelacionados;
    }

    public String getPickingDetalle() {
        return pickingDetalle;
    }

    public void setPickingDetalle(String pickingDetalle) {
        this.pickingDetalle = pickingDetalle;
    }

    public String getCongelaCotizacion() {
        return congelaCotizacion;
    }

    public void setCongelaCotizacion(String congelaCotizacion) {
        this.congelaCotizacion = congelaCotizacion;
    }

    public String getPendienteIngreso() {
        return pendienteIngreso;
    }

    public void setPendienteIngreso(String pendienteIngreso) {
        this.pendienteIngreso = pendienteIngreso;
    }

    public String getReviertePendiente() {
        return reviertePendiente;
    }

    public void setReviertePendiente(String reviertePendiente) {
        this.reviertePendiente = reviertePendiente;
    }

    public String getValidaTotalContraTesoreria() {
        return validaTotalContraTesoreria;
    }

    public void setValidaTotalContraTesoreria(String validaTotalContraTesoreria) {
        this.validaTotalContraTesoreria = validaTotalContraTesoreria;
    }

    public Comprobante getComprobante() {
        return Comprobante;
    }

    public void setComprobante(Comprobante Comprobante) {
        this.Comprobante = Comprobante;
    }

    public String getDepositoObligatorio() {
        return depositoObligatorio;
    }

    public void setDepositoObligatorio(String depositoObligatorio) {
        this.depositoObligatorio = depositoObligatorio;
    }

    public String getDialogoCantidadSeleccionPendiente() {
        return dialogoCantidadSeleccionPendiente;
    }

    public void setDialogoCantidadSeleccionPendiente(String dialogoCantidadSeleccionPendiente) {
        this.dialogoCantidadSeleccionPendiente = dialogoCantidadSeleccionPendiente;
    }

    public String getPermiteComprobanteConPrecioCero() {
        return permiteComprobanteConPrecioCero;
    }

    public void setPermiteComprobanteConPrecioCero(String permiteComprobanteConPrecioCero) {
        this.permiteComprobanteConPrecioCero = permiteComprobanteConPrecioCero;
    }

    public String getRecuperaDepositoPasoAnterior() {
        return recuperaDepositoPasoAnterior;
    }

    public void setRecuperaDepositoPasoAnterior(String recuperaDepositoPasoAnterior) {
        this.recuperaDepositoPasoAnterior = recuperaDepositoPasoAnterior;
    }

    @Override
    public CircuitoCompra clone() throws CloneNotSupportedException {
        return (CircuitoCompra) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 67 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
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
        final CircuitoCompra other = (CircuitoCompra) obj;
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
        return "CircuitoCompra{" + "circom=" + circom + ", cirapl=" + cirapl + '}';
    }

}
