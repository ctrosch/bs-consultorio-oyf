/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoStock implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    /**
     * Comprobante de stock
     */
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    //Punto de venta
    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVenta;

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

    /**
     * Tipo de movimiento A = Ajuste I = Ingreso E = Egreso T = Transferencia
     */
    @Column(name = "TIPMOV", length = 1, nullable = false)
    private String tipoMovimiento;

    //Entidad comercial
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial entidadComercial;

    /**
     * Deposito ingreso
     */
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    /**
     * Deposito egreso
     */
    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    /**
     * Mascara de stock
     */
    @JoinColumn(name = "MASCAR", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private MascaraStock mascara;
    /**
     * Observaciones textos
     */
    @Lob
    @Column(name = "OBSERV", length = 65535)
    private String observaciones;

    /**
     * Control de impresión
     */
    @Column(name = "IMPRES")
    private Short controlDeImpresion;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @JoinColumn(name = "COFSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "ISANUL")
    private String esAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    private MovimientoStock movimientoReversion;

    //Transportista
    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimiento", fetch = FetchType.LAZY)
    @OrderBy("nroitm")
    private List<ItemProductoStock> itemsProducto;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimiento", fetch = FetchType.LAZY)
    @OrderBy("nroitm")
    private List<ItemTransferenciaStock> itemTransferencia;

    @Transient
    private boolean persistido;

    @Transient
    private String atributo1;
    @Transient
    private String atributo2;
    @Transient
    private String atributo3;
    @Transient
    private String atributo4;
    @Transient
    private String atributo5;
    @Transient
    private String atributo6;
    @Transient
    private String atributo7;

    public MovimientoStock() {

        this.auditoria = new Auditoria();
        this.itemsProducto = new ArrayList<ItemProductoStock>();
        this.itemTransferencia = new ArrayList<ItemTransferenciaStock>();
        this.cotizacion = BigDecimal.ONE;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
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

    public MascaraStock getMascara() {
        return mascara;
    }

    public void setMascara(MascaraStock mascara) {
        this.mascara = mascara;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Short getControlDeImpresion() {
        return controlDeImpresion;
    }

    public void setControlDeImpresion(Short controlDeImpresion) {
        this.controlDeImpresion = controlDeImpresion;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public String getEsAnulacion() {
        return esAnulacion;
    }

    public void setEsAnulacion(String esAnulacion) {
        this.esAnulacion = esAnulacion;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemProductoStock> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<ItemProductoStock> itemProducto) {
        this.itemsProducto = itemProducto;
    }

    public List<ItemTransferenciaStock> getItemTransferencia() {
        return itemTransferencia;
    }

    public void setItemTransferencia(List<ItemTransferenciaStock> itemTransferencia) {
        this.itemTransferencia = itemTransferencia;
    }

    public boolean isPersistido() {
        return persistido;
    }

    public void setPersistido(boolean persistido) {
        this.persistido = persistido;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    public MovimientoStock getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoStock movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public EntidadComercial getTransporte() {
        return transporte;
    }

    public void setTransporte(EntidadComercial transporte) {
        this.transporte = transporte;
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
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoStock other = (MovimientoStock) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoStock{" + "id=" + id + '}';
    }

}
