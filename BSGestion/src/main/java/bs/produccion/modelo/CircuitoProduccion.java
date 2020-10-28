/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

import bs.global.modelo.Comprobante;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "pd_circuito")
@IdClass(CircuitoProduccionPK.class)
public class CircuitoProduccion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "circom", nullable = false, length = 6)
    private String circom;
    @Id
    @Column(name = "cirapl", nullable = false, length = 6)
    private String cirapl;

    /**
     * Circuito de inicio
     */
    @JoinColumn(name = "circom", referencedColumnName = "codigo", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private CodigoCircuitoProduccion circuitoComienzo;

    /**
     * Circuito a aplicar
     */
    @JoinColumn(name = "cirapl", referencedColumnName = "codigo", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private CodigoCircuitoProduccion circuitoAplicacion;
    /**
     * Descripción del circuito
     */
    @Basic(optional = false)
    @Column(name = "descrp", nullable = false, length = 60)
    private String descripcion;

    /**
     * Tipo circuito
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipmov", nullable = false, length = 1)
    private TipoMovimientoProduccion tipoMovimiento;

    /**
     * Actualiza stock
     */
    @Basic(optional = false)
    @Column(name = "actzst", nullable = false)
    private String actualizaStock;
    /**
     * Actualiza producción
     */
    @Basic(optional = false)
    @Column(name = "actzpd", nullable = false)
    private String actualizaProduccion;

    /**
     * Administra atributos
     */
    @Column(name = "pidatr")
    private String pideAtributos;
    @Column(name = "adatr1", nullable = false, length = 1)
    private String administraAtributo1;
    @Column(name = "adatr2", nullable = false, length = 1)
    private String administraAtributo2;
    @Column(name = "adatr3", nullable = false, length = 1)
    private String administraAtributo3;
    @Column(name = "adatr4", nullable = false, length = 1)
    private String administraAtributo4;
    @Column(name = "adatr5", nullable = false, length = 1)
    private String administraAtributo5;
    @Column(name = "adatr6", nullable = false, length = 1)
    private String administraAtributo6;
    @Column(name = "adatr7", nullable = false, length = 1)
    private String administraAtributo7;

    /**
     * Es comprobante de anulación
     */
    @Column(name = "isanull", length = 1)
    private String esAnulacion;
    /**
     * Necesita autorización
     */
    @Basic(optional = false)
    @Column(name = "autori", nullable = false)
    private String requiereAutorizacion;
    /**
     * Proceso Total
     */
    @Basic(optional = false)
    @Column(name = "prctot", nullable = false)
    private String procesoTotal;

    /**
     * Proceso Total opcional
     */
    @Column(name = "prcopc")
    private String procesoOpcional;

    /**
     * No aplica items o no cancela pendiente.
     */
    @Column(name = "ncanpen")
    private String noCancelaPendiente;

    @Column(name = "nconpen")
    private String noControlaPendiente;

    /**
     * Un item por comprobante
     */
    @Column(name = "itmuni")
    private String itemUnico;
    /**
     * Compromete stock
     */
    @Column(name = "compst")
    private String comprometeStock;

    /**
     * Reporte de pendiente (Grupo)
     */
    @Column(name = "repgrp", length = 60)
    private String reportePendienteGrupo;
    /**
     * Reporte de pendiente (Detalle)
     */
    @Column(name = "repdet", length = 60)
    private String reportePendienteDetalle;
    /**
     * Deposito por defecto
     */
    @Basic(optional = false)
    @Column(name = "depdef", nullable = false, length = 15)
    private String depositoDefecto;

    /**
     * Toma númer de serie desde parte de producción
     */
    @Basic(optional = false)
    @Column(name = "tserpp", nullable = false, length = 1)
    private String tomaNumeroSerieDesdeParteProduccion;

    /**
     * Posibilidad de agregar nuevo items
     */
    @Column(name = "agrega")
    private String permiteAgregarItems;
    /**
     * Genera orden de produccón automática
     */
    @Column(name = "genopa")
    private String generaOrdenProduccionAutomatica;
    /**
     * Trabaja con Nro interno por item
     */
    @Column(name = "ctlint")
    private String trabajaConNroInternoPorItem;
    /**
     * Ultimo numero
     */
    @Column(name = "ultint", precision = 53)
    private Double ultimoNumero;
    /**
     * Automatiza parte producción
     */
    @Column(name = "autppr", length = 1)
    private String automatizaParteProduccion;

    /**
     * Graba composición de formula
     */
    @Column(name = "grafor", length = 1)
    private String grabaComposicionFormula;
    /**
     * Permite ingreso de prod sin formula en vales de consumo
     */
    @Column(name = "sinfor", length = 1)
    private String permiteIngresoProduccionSinFormula;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoProduccionProduccion> itemCircuitoProduccion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoProduccionStock> itemCircuitoStock;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoProduccionValeConsumo> itemCircuitoValeConsumo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoProduccionParteProceso> itemCircuitoParteProceso;

    @Transient
    private Comprobante comprobanteStock;

    @Transient
    Comprobante comprobanteProduccion;

    @Transient
    private Comprobante comprobanteValeConsumo;

    @Transient
    Comprobante comprobanteParteProceso;

    @Transient
    private List<CircuitoProduccion> circuitosRelacionados;

    public CircuitoProduccion() {

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

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
    }

    public String getActualizaProduccion() {
        return actualizaProduccion;
    }

    public void setActualizaProduccion(String actualizaProduccion) {
        this.actualizaProduccion = actualizaProduccion;
    }

    public CodigoCircuitoProduccion getCircuitoComienzo() {
        return circuitoComienzo;
    }

    public void setCircuitoComienzo(CodigoCircuitoProduccion circuitoComienzo) {
        this.circuitoComienzo = circuitoComienzo;
    }

    public CodigoCircuitoProduccion getCircuitoAplicacion() {
        return circuitoAplicacion;
    }

    public void setCircuitoAplicacion(CodigoCircuitoProduccion circuitoAplicacion) {
        this.circuitoAplicacion = circuitoAplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoMovimientoProduccion getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoProduccion tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getEsAnulacion() {
        return esAnulacion;
    }

    public void setEsAnulacion(String esAnulacion) {
        this.esAnulacion = esAnulacion;
    }

    public String getRequiereAutorizacion() {
        return requiereAutorizacion;
    }

    public void setRequiereAutorizacion(String requiereAutorizacion) {
        this.requiereAutorizacion = requiereAutorizacion;
    }

    public String getProcesoTotal() {
        return procesoTotal;
    }

    public void setProcesoTotal(String procesoTotal) {
        this.procesoTotal = procesoTotal;
    }

    public String getProcesoOpcional() {
        return procesoOpcional;
    }

    public void setProcesoOpcional(String procesoOpcional) {
        this.procesoOpcional = procesoOpcional;
    }

    public String getNoCancelaPendiente() {
        return noCancelaPendiente;
    }

    public void setNoCancelaPendiente(String noCancelaPendiente) {
        this.noCancelaPendiente = noCancelaPendiente;
    }

    public String getItemUnico() {
        return itemUnico;
    }

    public void setItemUnico(String itemUnico) {
        this.itemUnico = itemUnico;
    }

    public String getComprometeStock() {
        return comprometeStock;
    }

    public void setComprometeStock(String comprometeStock) {
        this.comprometeStock = comprometeStock;
    }

    public String getReportePendienteGrupo() {
        return reportePendienteGrupo;
    }

    public void setReportePendienteGrupo(String reportePendienteGrupo) {
        this.reportePendienteGrupo = reportePendienteGrupo;
    }

    public String getReportePendienteDetalle() {
        return reportePendienteDetalle;
    }

    public void setReportePendienteDetalle(String reportePendienteDetalle) {
        this.reportePendienteDetalle = reportePendienteDetalle;
    }

    public String getDepositoDefecto() {
        return depositoDefecto;
    }

    public void setDepositoDefecto(String depositoDefecto) {
        this.depositoDefecto = depositoDefecto;
    }

    public String getTomaNumeroSerieDesdeParteProduccion() {
        return tomaNumeroSerieDesdeParteProduccion;
    }

    public void setTomaNumeroSerieDesdeParteProduccion(String tomaNumeroSerieDesdeParteProduccion) {
        this.tomaNumeroSerieDesdeParteProduccion = tomaNumeroSerieDesdeParteProduccion;
    }

    public String getPermiteAgregarItems() {
        return permiteAgregarItems;
    }

    public void setPermiteAgregarItems(String permiteAgregarItems) {
        this.permiteAgregarItems = permiteAgregarItems;
    }

    public String getGeneraOrdenProduccionAutomatica() {
        return generaOrdenProduccionAutomatica;
    }

    public void setGeneraOrdenProduccionAutomatica(String generaOrdenProduccionAutomatica) {
        this.generaOrdenProduccionAutomatica = generaOrdenProduccionAutomatica;
    }

    public String getTrabajaConNroInternoPorItem() {
        return trabajaConNroInternoPorItem;
    }

    public void setTrabajaConNroInternoPorItem(String trabajaConNroInternoPorItem) {
        this.trabajaConNroInternoPorItem = trabajaConNroInternoPorItem;
    }

    public Double getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(Double ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }

    public String getAutomatizaParteProduccion() {
        return automatizaParteProduccion;
    }

    public void setAutomatizaParteProduccion(String automatizaParteProduccion) {
        this.automatizaParteProduccion = automatizaParteProduccion;
    }

    public String getGrabaComposicionFormula() {
        return grabaComposicionFormula;
    }

    public void setGrabaComposicionFormula(String grabaComposicionFormula) {
        this.grabaComposicionFormula = grabaComposicionFormula;
    }

    public String getPermiteIngresoProduccionSinFormula() {
        return permiteIngresoProduccionSinFormula;
    }

    public void setPermiteIngresoProduccionSinFormula(String permiteIngresoProduccionSinFormula) {
        this.permiteIngresoProduccionSinFormula = permiteIngresoProduccionSinFormula;
    }

    public String getPideAtributos() {
        return pideAtributos;
    }

    public void setPideAtributos(String pideAtributos) {
        this.pideAtributos = pideAtributos;
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

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public Comprobante getComprobanteProduccion() {
        return comprobanteProduccion;
    }

    public void setComprobanteProduccion(Comprobante comprobanteProduccion) {
        this.comprobanteProduccion = comprobanteProduccion;
    }

    public Comprobante getComprobanteValeConsumo() {
        return comprobanteValeConsumo;
    }

    public void setComprobanteValeConsumo(Comprobante comprobanteValeConsumo) {
        this.comprobanteValeConsumo = comprobanteValeConsumo;
    }

    public Comprobante getComprobanteParteProceso() {
        return comprobanteParteProceso;
    }

    public void setComprobanteParteProceso(Comprobante comprobanteParteProceso) {
        this.comprobanteParteProceso = comprobanteParteProceso;
    }

    public List<CircuitoProduccion> getCircuitosRelacionados() {
        return circuitosRelacionados;
    }

    public void setCircuitosRelacionados(List<CircuitoProduccion> circuitosRelacionados) {
        this.circuitosRelacionados = circuitosRelacionados;
    }

    public List<ItemCircuitoProduccionProduccion> getItemCircuitoProduccion() {
        return itemCircuitoProduccion;
    }

    public void setItemCircuitoProduccion(List<ItemCircuitoProduccionProduccion> itemCircuitoProduccion) {
        this.itemCircuitoProduccion = itemCircuitoProduccion;
    }

    public List<ItemCircuitoProduccionStock> getItemCircuitoStock() {
        return itemCircuitoStock;
    }

    public void setItemCircuitoStock(List<ItemCircuitoProduccionStock> itemCircuitoStock) {
        this.itemCircuitoStock = itemCircuitoStock;
    }

    public List<ItemCircuitoProduccionValeConsumo> getItemCircuitoValeConsumo() {
        return itemCircuitoValeConsumo;
    }

    public void setItemCircuitoValeConsumo(List<ItemCircuitoProduccionValeConsumo> itemCircuitoValeConsumo) {
        this.itemCircuitoValeConsumo = itemCircuitoValeConsumo;
    }

    public List<ItemCircuitoProduccionParteProceso> getItemCircuitoParteProceso() {
        return itemCircuitoParteProceso;
    }

    public void setItemCircuitoParteProceso(List<ItemCircuitoProduccionParteProceso> itemCircuitoParteProceso) {
        this.itemCircuitoParteProceso = itemCircuitoParteProceso;
    }

    public String getNoControlaPendiente() {
        return noControlaPendiente;
    }

    public void setNoControlaPendiente(String noControlaPendiente) {
        this.noControlaPendiente = noControlaPendiente;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 23 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CircuitoProduccion other = (CircuitoProduccion) obj;
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
        return "CircuitoProduccion{" + "circom=" + circom + ", cirapl=" + cirapl + '}';
    }

}
