/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "vt_cuenta_corriente")
@EntityListeners(AuditoriaListener.class)
public class AplicacionCuentaCorrienteVenta implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOV", referencedColumnName = "ID")        
    MovimientoVenta movimiento;
       
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APL", referencedColumnName = "ID")        
    MovimientoVenta movimientoAplicacion;
    
    @Column(name = "CUOTAS", nullable = false)
    private int cuota;
    @Basic(optional = false)
        
    //Fecha del movimiento
    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaAplicacion;

    //Cliente
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    //Código de subcuenta
    @Column(name = "NROSUB", length = 13)
    private String nroSubcuenta;
    
    //Código de imputación
    @Column(name = "IMPTCN", length = 6)
    private String codigoImputacion;

    //Importe de la cuota en moneda nacional
    @Column(name = "IMPNAC", precision = 15, scale = 2)
    private BigDecimal importe;
    
    //Importe en moneda secundaria
    @Column(name = "IMPSEC", precision = 15, scale = 2)
    private BigDecimal importeSecundario;

    //Fecha de vencimiento de la cuota
    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    //Texto asociado
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;
    
    //Coeficiente de deuda
    @JoinColumn(name = "COFSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;        
    
    //Moneda de Registración    
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;
    
    //Cambio de la operación
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;
 
    //Fecha de interés	
    @Column(name = "FCHINT")
    @Temporal(TemporalType.DATE)
    private Date fechaInteres;

    //Codigo de barra
    @Column(name = "CODBAR", length = 120)
    private String codbar;
    
    @Embedded
    private Auditoria auditoria;
    

    public AplicacionCuentaCorrienteVenta() {
        
        auditoria = new Auditoria();
        importe = BigDecimal.ZERO;
        importeSecundario = BigDecimal.ZERO;
        cotizacion = BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoVenta getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoVenta movimiento) {
        this.movimiento = movimiento;
    }

    public MovimientoVenta getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoVenta movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public String getNroSubcuenta() {
        return nroSubcuenta;
    }

    public void setNroSubcuenta(String nroSubcuenta) {
        this.nroSubcuenta = nroSubcuenta;
    }

    public String getCodigoImputacion() {
        return codigoImputacion;
    }

    public void setCodigoImputacion(String codigoImputacion) {
        this.codigoImputacion = codigoImputacion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(BigDecimal importeSecundario) {
        this.importeSecundario = importeSecundario;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
    
    
    public Date getFechaInteres() {
        return fechaInteres;
    }

    public void setFechaInteres(Date fechaInteres) {
        this.fechaInteres = fechaInteres;
    }

    public String getCodbar() {
        return codbar;
    }

    public void setCodbar(String codbar) {
        this.codbar = codbar;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final AplicacionCuentaCorrienteVenta other = (AplicacionCuentaCorrienteVenta) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AplicacionCuentaCorrienteVenta{" + "id=" + id + '}';
    }
    
    
    

}
