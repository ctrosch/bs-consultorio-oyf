/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Vendedor;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cv_contrato")
@EntityListeners(AuditoriaListener.class)
public class Contrato implements Serializable, IAuditableEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "ESTCON", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoContrato estado;

    @NotNull
    @Column(name = "NROCON", length = 10)
    private String nroContrato;

    @JoinColumn(name = "TIPCON", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoContrato tipoContrato;

    @Column(name = "DESCRP", length = 13)
    private String descripcion;

    @Lob
    @Column(name = "DETALLE", length = 2147483647)
    private String detalle;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaFirma;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePago;

    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;

    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;

    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @Column(name = "FCHDES", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaDesde;

    @Column(name = "FCHHAS", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaHasta;

    @Column(name = "FCHPRI", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaPrimerFactura;

    @Column(name = "FCHULT", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaUltimaFactura;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @OneToMany(mappedBy = "contrato", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemContrato> itemsComtrato;

    @Embedded
    private Auditoria auditoria;

    public Contrato() {
        this.auditoria = new Auditoria();
        this.fechaFirma = new Date();
        this.fechaDesde = new Date();
        this.fechaHasta = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(Date fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public CondicionDePagoVenta getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionDePagoVenta condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public String getNroContrato() {
        return nroContrato;
    }

    public void setNroContrato(String nroContrato) {
        this.nroContrato = nroContrato;
    }

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Date getFechaPrimerFactura() {
        return fechaPrimerFactura;
    }

    public void setFechaPrimerFactura(Date fechaPrimerFactura) {
        this.fechaPrimerFactura = fechaPrimerFactura;
    }

    public Date getFechaUltimaFactura() {
        return fechaUltimaFactura;
    }

    public void setFechaUltimaFactura(Date fechaUltimaFactura) {
        this.fechaUltimaFactura = fechaUltimaFactura;
    }

    public EstadoContrato getEstado() {
        return estado;
    }

    public void setEstado(EstadoContrato estado) {
        this.estado = estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ItemContrato> getItemsComtrato() {
        return itemsComtrato;
    }

    public void setItemsComtrato(List<ItemContrato> itemsComtrato) {
        this.itemsComtrato = itemsComtrato;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public Contrato clone() throws CloneNotSupportedException {
        return (Contrato) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Contrato other = (Contrato) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Contrato{" + "id=" + id + '}';
    }

}
