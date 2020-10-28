/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Frecuencia;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "vt_condicion_pago")
@EntityListeners(AuditoriaListener.class)
public class CondicionDePagoVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "codigo", length = 6)
    private String codigo;
    @Column(name = "DESCRP", length = 60)
    private String descripcion;

    @Column(name = "IMPCTA", length = 60)
    private String imputaCuentaCorriente;

    @Enumerated(EnumType.STRING)
    @Column(name = "FCHORI", length = 2)
    private FechaOrigenCalculo fechaOrigenCalculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPCAL", length = 2)
    private TipoCalculoVencimiento tipoCalculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "FRECUE", length = 1)
    private Frecuencia frecuencia;

    @Column(name = "DIAFIJ")
    private Integer diaSegunFrecuencia;

    @Column(name = "CNTCUO")
    private Integer cantidadCuotas;

    @Column(name = "TOMFOR", length = 1)
    private String tomaFechaOrigenComoPrimerVencimiento;

    @Size(max = 10)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condicionPago", fetch = FetchType.LAZY)
    private List<ItemCondicionPagoVenta> cuotas;

    @Embedded
    private Auditoria auditoria;

    public CondicionDePagoVenta() {
    }

    public CondicionDePagoVenta(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ItemCondicionPagoVenta> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<ItemCondicionPagoVenta> cuotas) {
        this.cuotas = cuotas;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getImputaCuentaCorriente() {
        return imputaCuentaCorriente;
    }

    public void setImputaCuentaCorriente(String imputaCuentaCorriente) {
        this.imputaCuentaCorriente = imputaCuentaCorriente;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public FechaOrigenCalculo getFechaOrigenCalculo() {
        return fechaOrigenCalculo;
    }

    public void setFechaOrigenCalculo(FechaOrigenCalculo fechaOrigenCalculo) {
        this.fechaOrigenCalculo = fechaOrigenCalculo;
    }

    public TipoCalculoVencimiento getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculoVencimiento tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Integer getDiaSegunFrecuencia() {
        return diaSegunFrecuencia;
    }

    public void setDiaSegunFrecuencia(Integer diaSegunFrecuencia) {
        this.diaSegunFrecuencia = diaSegunFrecuencia;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public String getTomaFechaOrigenComoPrimerVencimiento() {
        return tomaFechaOrigenComoPrimerVencimiento;
    }

    public void setTomaFechaOrigenComoPrimerVencimiento(String tomaFechaOrigenComoPrimerVencimiento) {
        this.tomaFechaOrigenComoPrimerVencimiento = tomaFechaOrigenComoPrimerVencimiento;
    }

    public void setFrecuencia(Frecuencia frecuencia) {
        this.frecuencia = frecuencia;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final CondicionDePagoVenta other = (CondicionDePagoVenta) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CondicionDePagoVenta{" + "codigo=" + codigo + '}';
    }

}
