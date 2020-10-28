/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import bs.ventas.modelo.MovimientoVenta;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */
//@Entity
//@Table(name = "cv_liquidacion")
//@EntityListeners(AuditoriaListener.class)
public class MovimientoLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @Column(name = "FCHDES", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaDesde;

    @Column(name = "FCHHAS", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaHasta;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_CON", referencedColumnName = "ID")
    private Contrato contrato;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    private MovimientoVenta movimientoVenta;

    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteGenerar;

    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVentaComprobanteGenerar;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "LIQUID", length = 1)
    private String liquidado;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoFacturacion> itemsProducto;

    @Embedded
    private Auditoria auditoria;

    public MovimientoLiquidacion() {

        auditoria = new Auditoria();
        itemsProducto = new ArrayList<ItemMovimientoFacturacion>();
    }

    public MovimientoLiquidacion(Integer id) {

        this.id = id;
        auditoria = new Auditoria();

        itemsProducto = new ArrayList<ItemMovimientoFacturacion>();

    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ItemMovimientoFacturacion> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<ItemMovimientoFacturacion> itemsProducto) {
        this.itemsProducto = itemsProducto;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
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

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Comprobante getComprobanteGenerar() {
        return comprobanteGenerar;
    }

    public void setComprobanteGenerar(Comprobante comprobanteGenerar) {
        this.comprobanteGenerar = comprobanteGenerar;
    }

    public PuntoVenta getPuntoVentaComprobanteGenerar() {
        return puntoVentaComprobanteGenerar;
    }

    public void setPuntoVentaComprobanteGenerar(PuntoVenta puntoVentaComprobanteGenerar) {
        this.puntoVentaComprobanteGenerar = puntoVentaComprobanteGenerar;
    }

    public String getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(String liquidado) {
        this.liquidado = liquidado;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoLiquidacion other = (MovimientoLiquidacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoFacturacion{" + "id=" + id + '}';
    }
}
