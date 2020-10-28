/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.administracion.modelo.Reporte;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "ed_circuito")
@IdClass(CircuitoEducacionPK.class)
@EntityListeners(AuditoriaListener.class)
public class CircuitoEducacion implements Serializable {

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
    private CodigoCircuitoEducacion circuitoComienzo;

    /**
     * Circuito a aplicar
     */
    @JoinColumn(name = "CIRAPL", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoEducacion circuitoAplicacion;

    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "ACTZED", nullable = false, length = 1)
    private String actualizaEducacion;
    @Basic(optional = false)
    @Column(name = "ACTZST", nullable = false, length = 1)
    private String actualizaStock;
    @Basic(optional = false)
    @Column(name = "ACTZVT", nullable = false, length = 1)
    private String actualizaVenta;
    @Basic(optional = false)
    @Column(name = "ACTZCJ", nullable = false, length = 1)
    private String actualizaTesoreria;

    @JoinColumn(name = "REPGRP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteGrupo;

    @JoinColumn(name = "REPDET", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporteDetalle;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoEducacionEducacion> itemCircuitoEducacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoEducacionStock> itemCircuitoStock;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoEducacionVenta> itemCircuitoVenta;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoEducacionTesoreria> itemCircuitoTesoreria;

    @Transient
    Comprobante comprobanteEducacion;

    @Transient
    Comprobante comprobanteVenta;

    @Transient
    Comprobante comprobanteStock;

    @Transient
    Comprobante comprobanteTesoreria;

    @Transient
    Comprobante Comprobante;

    @Transient
    private List<CircuitoEducacion> circuitosRelacionados;

    public CircuitoEducacion() {

        //Actualización de módulos
        this.actualizaEducacion = "N";
        this.actualizaStock = "N";
        this.actualizaVenta = "N";
        this.actualizaTesoreria = "N";
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

    public CodigoCircuitoEducacion getCircuitoComienzo() {
        return circuitoComienzo;
    }

    public void setCircuitoComienzo(CodigoCircuitoEducacion circuitoComienzo) {
        this.circuitoComienzo = circuitoComienzo;
    }

    public CodigoCircuitoEducacion getCircuitoAplicacion() {
        return circuitoAplicacion;
    }

    public void setCircuitoAplicacion(CodigoCircuitoEducacion circuitoAplicacion) {
        this.circuitoAplicacion = circuitoAplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActualizaEducacion() {
        return actualizaEducacion;
    }

    public void setActualizaEducacion(String actualizaEducacion) {
        this.actualizaEducacion = actualizaEducacion;
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

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemCircuitoEducacionEducacion> getItemCircuitoEducacion() {
        return itemCircuitoEducacion;
    }

    public void setItemCircuitoEducacion(List<ItemCircuitoEducacionEducacion> itemCircuitoEducacion) {
        this.itemCircuitoEducacion = itemCircuitoEducacion;
    }

    public List<ItemCircuitoEducacionStock> getItemCircuitoStock() {
        return itemCircuitoStock;
    }

    public void setItemCircuitoStock(List<ItemCircuitoEducacionStock> itemCircuitoStock) {
        this.itemCircuitoStock = itemCircuitoStock;
    }

    public List<ItemCircuitoEducacionVenta> getItemCircuitoVenta() {
        return itemCircuitoVenta;
    }

    public void setItemCircuitoVenta(List<ItemCircuitoEducacionVenta> itemCircuitoVenta) {
        this.itemCircuitoVenta = itemCircuitoVenta;
    }

    public List<ItemCircuitoEducacionTesoreria> getItemCircuitoTesoreria() {
        return itemCircuitoTesoreria;
    }

    public void setItemCircuitoTesoreria(List<ItemCircuitoEducacionTesoreria> itemCircuitoTesoreria) {
        this.itemCircuitoTesoreria = itemCircuitoTesoreria;
    }

    public Comprobante getComprobanteEducacion() {
        return comprobanteEducacion;
    }

    public void setComprobanteEducacion(Comprobante comprobanteEducacion) {
        this.comprobanteEducacion = comprobanteEducacion;
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

    public Comprobante getComprobante() {
        return Comprobante;
    }

    public void setComprobante(Comprobante Comprobante) {
        this.Comprobante = Comprobante;
    }

    public List<CircuitoEducacion> getCircuitosRelacionados() {
        return circuitosRelacionados;
    }

    public void setCircuitosRelacionados(List<CircuitoEducacion> circuitosRelacionados) {
        this.circuitosRelacionados = circuitosRelacionados;
    }

    public Comprobante getComprobanteTesoreria() {
        return comprobanteTesoreria;
    }

    public void setComprobanteTesoreria(Comprobante comprobanteTesoreria) {
        this.comprobanteTesoreria = comprobanteTesoreria;
    }

    @Override
    public CircuitoEducacion clone() throws CloneNotSupportedException {
        return (CircuitoEducacion) super.clone();
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
        final CircuitoEducacion other = (CircuitoEducacion) obj;
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
