/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.stock.modelo.TipoProducto;
import bs.ventas.modelo.ListaPrecioVenta;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "tl_circuito")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
@IdClass(CircuitoTallerPK.class)
public class CircuitoTaller implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Size(min = 1, max = 6)
    @Column(name = "CIRCOM", nullable = false, length = 6)
    private String circom;

    @Id
    @Size(min = 1, max = 6)
    @Column(name = "CIRAPL", nullable = false, length = 6)
    private String cirapl;

    /**
     * CircuitoTaller de inicio
     */
    @JoinColumn(name = "CIRCOM", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoTaller circuitoComienzo;

    /**
     * CircuitoTaller que se aplica
     */
    @JoinColumn(name = "CIRAPL", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoTaller circuitoAplicacion;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACTZTL", nullable = false, length = 1)
    private String actualizaTaller;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACTZFC", nullable = false, length = 1)
    private String actualizaFacturacion;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACTZST", nullable = false, length = 1)
    private String actualizaStock;
    
        /**
     * CircuitoTaller de inicio
     */
    @JoinColumn(name = "CIRCFC", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoFacturacion circuitoComienzoFacturacion;

    /**
     * CircuitoTaller que se aplica
     */
    @JoinColumn(name = "CIRAFC", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CodigoCircuitoFacturacion circuitoAplicacionFacturacion;
   
    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProductoProducto;

    @JoinColumn(name = "TIPSER", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProductoServicio;
    
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaPrecio;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "EDIIMP", nullable = false, length = 1)
    private String editaImporte;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PIDATR", nullable = false, length = 1)
    private String pideAtributos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR1", nullable = false, length = 1)
    private String administraAtributo1;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR2", nullable = false, length = 1)
    private String administraAtributo2;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR3", nullable = false, length = 1)
    private String administraAtributo3;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR4", nullable = false, length = 1)
    private String administraAtributo4;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR5", nullable = false, length = 1)
    private String administraAtributo5;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ADATR6", nullable = false, length = 1)
    private String administraAtributo6;
    @Size(max = 1)
    @Column(name = "ADATR7", length = 1)
    private String administraAtributo7;
    @Size(max = 1)
    @Column(name = "COMPST", length = 1)
    private String comprometeStock;

    @Size(max = 1)
    @Column(name = "ISANULL", length = 1)
    private String esAnulación;
    @Size(max = 1)
    @Column(name = "NCANPEN", length = 1)
    private String noCancelaPendiente;
    @Size(max = 1)
    @Column(name = "AGREGA", length = 1)
    private String permiteAgregarItems;

    @Size(max = 1)
    @Column(name = "PERCEB", length = 1)
    private String permiteClienteEnBlanco;

    @Size(max = 1)
    @Column(name = "PERTEB", length = 1)
    private String permiteTecnicoEnBlanco;

    @Size(max = 1)
    @Column(name = "PERDEB", length = 1)
    private String permiteDiagnosticoEnBlanco;

    @Size(max = 1)
    @Column(name = "PERSEB", length = 1)
    private String permiteSolucionEnBlanco;

    @Size(max = 30)
    @Column(name = "REPDET", length = 30)
    private String repdet;
    @Size(max = 30)
    @Column(name = "REPGRP", length = 30)
    private String repgrp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "ITMUNI", nullable = false, length = 255)
    private String itemUnico;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoTallerTaller> itemCircuitoTaller;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoTallerFacturacion> itemCircuitoFacturacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "circuito", fetch = FetchType.LAZY)
    private List<ItemCircuitoTallerStock> itemCircuitoStock;

    @Transient
    Comprobante comprobanteTaller;

    @Transient
    Comprobante comprobanteFacturacion;

    @Transient
    Comprobante comprobanteStock;

    @Transient
    private List<CircuitoTaller> circuitosRelacionados;

    @Embedded
    private Auditoria auditoria;

    public CircuitoTaller() {
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

    public CodigoCircuitoTaller getCircuitoComienzo() {
        return circuitoComienzo;
    }

    public void setCircuitoComienzo(CodigoCircuitoTaller circuitoComienzo) {
        this.circuitoComienzo = circuitoComienzo;
    }

    public CodigoCircuitoTaller getCircuitoAplicacion() {
        return circuitoAplicacion;
    }

    public void setCircuitoAplicacion(CodigoCircuitoTaller circuitoAplicacion) {
        this.circuitoAplicacion = circuitoAplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActualizaTaller() {
        return actualizaTaller;
    }

    public void setActualizaTaller(String actualizaTaller) {
        this.actualizaTaller = actualizaTaller;
    }

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
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

    public String getComprometeStock() {
        return comprometeStock;
    }

    public void setComprometeStock(String comprometeStock) {
        this.comprometeStock = comprometeStock;
    }

    public String getEsAnulación() {
        return esAnulación;
    }

    public void setEsAnulación(String esAnulación) {
        this.esAnulación = esAnulación;
    }

    public String getNoCancelaPendiente() {
        return noCancelaPendiente;
    }

    public void setNoCancelaPendiente(String noCancelaPendiente) {
        this.noCancelaPendiente = noCancelaPendiente;
    }

    public String getPermiteAgregarItems() {
        return permiteAgregarItems;
    }

    public void setPermiteAgregarItems(String permiteAgregarItems) {
        this.permiteAgregarItems = permiteAgregarItems;
    }

    public String getRepdet() {
        return repdet;
    }

    public void setRepdet(String repdet) {
        this.repdet = repdet;
    }

    public String getRepgrp() {
        return repgrp;
    }

    public void setRepgrp(String repgrp) {
        this.repgrp = repgrp;
    }

    public String getItemUnico() {
        return itemUnico;
    }

    public void setItemUnico(String itemUnico) {
        this.itemUnico = itemUnico;
    }

    public List<ItemCircuitoTallerStock> getItemCircuitoStock() {
        return itemCircuitoStock;
    }

    public void setItemCircuitoStock(List<ItemCircuitoTallerStock> itemCircuitoStock) {
        this.itemCircuitoStock = itemCircuitoStock;
    }

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public List<CircuitoTaller> getCircuitosRelacionados() {
        return circuitosRelacionados;
    }

    public void setCircuitosRelacionados(List<CircuitoTaller> circuitosRelacionados) {
        this.circuitosRelacionados = circuitosRelacionados;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getActualizaFacturacion() {
        return actualizaFacturacion;
    }

    public void setActualizaFacturacion(String actualizaFacturacion) {
        this.actualizaFacturacion = actualizaFacturacion;
    }

    public List<ItemCircuitoTallerTaller> getItemCircuitoTaller() {
        return itemCircuitoTaller;
    }

    public void setItemCircuitoTaller(List<ItemCircuitoTallerTaller> itemCircuitoTaller) {
        this.itemCircuitoTaller = itemCircuitoTaller;
    }

    public List<ItemCircuitoTallerFacturacion> getItemCircuitoFacturacion() {
        return itemCircuitoFacturacion;
    }

    public void setItemCircuitoFacturacion(List<ItemCircuitoTallerFacturacion> itemCircuitoFacturacion) {
        this.itemCircuitoFacturacion = itemCircuitoFacturacion;
    }

    public Comprobante getComprobanteTaller() {
        return comprobanteTaller;
    }

    public void setComprobanteTaller(Comprobante comprobanteTaller) {
        this.comprobanteTaller = comprobanteTaller;
    }

    public Comprobante getComprobanteFacturacion() {
        return comprobanteFacturacion;
    }

    public void setComprobanteFacturacion(Comprobante comprobanteFacturacion) {
        this.comprobanteFacturacion = comprobanteFacturacion;
    }

    public String getPermiteClienteEnBlanco() {
        return permiteClienteEnBlanco;
    }

    public void setPermiteClienteEnBlanco(String permiteClienteEnBlanco) {
        this.permiteClienteEnBlanco = permiteClienteEnBlanco;
    }

    public String getPermiteTecnicoEnBlanco() {
        return permiteTecnicoEnBlanco;
    }

    public void setPermiteTecnicoEnBlanco(String permiteTecnicoEnBlanco) {
        this.permiteTecnicoEnBlanco = permiteTecnicoEnBlanco;
    }

    public String getPermiteDiagnosticoEnBlanco() {
        return permiteDiagnosticoEnBlanco;
    }

    public void setPermiteDiagnosticoEnBlanco(String permiteDiagnosticoEnBlanco) {
        this.permiteDiagnosticoEnBlanco = permiteDiagnosticoEnBlanco;
    }

    public String getPermiteSolucionEnBlanco() {
        return permiteSolucionEnBlanco;
    }

    public void setPermiteSolucionEnBlanco(String permiteSolucionEnBlanco) {
        this.permiteSolucionEnBlanco = permiteSolucionEnBlanco;
    }

    public TipoProducto getTipoProductoProducto() {
        return tipoProductoProducto;
    }

    public void setTipoProductoProducto(TipoProducto tipoProductoProducto) {
        this.tipoProductoProducto = tipoProductoProducto;
    }

    public TipoProducto getTipoProductoServicio() {
        return tipoProductoServicio;
    }

    public void setTipoProductoServicio(TipoProducto tipoProductoServicio) {
        this.tipoProductoServicio = tipoProductoServicio;
    }

    public String getEditaImporte() {
        return editaImporte;
    }

    public void setEditaImporte(String editaImporte) {
        this.editaImporte = editaImporte;
    }

    public ListaPrecioVenta getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecioVenta listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public CodigoCircuitoFacturacion getCircuitoComienzoFacturacion() {
        return circuitoComienzoFacturacion;
    }

    public void setCircuitoComienzoFacturacion(CodigoCircuitoFacturacion circuitoComienzoFacturacion) {
        this.circuitoComienzoFacturacion = circuitoComienzoFacturacion;
    }

    public CodigoCircuitoFacturacion getCircuitoAplicacionFacturacion() {
        return circuitoAplicacionFacturacion;
    }

    public void setCircuitoAplicacionFacturacion(CodigoCircuitoFacturacion circuitoAplicacionFacturacion) {
        this.circuitoAplicacionFacturacion = circuitoAplicacionFacturacion;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 47 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
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
        final CircuitoTaller other = (CircuitoTaller) obj;
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
        return "CircuitoService{" + "circom=" + circom + ", cirapl=" + cirapl + '}';
    }

}
