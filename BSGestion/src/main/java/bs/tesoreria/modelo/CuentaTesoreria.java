/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Empresa;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cj_cuenta")
@EntityListeners(AuditoriaListener.class)
public class CuentaTesoreria implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @JoinColumn(name = "TIPCTA", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoCuentaTesoreria tipoCuenta;

    @Column(name = "NROAUT")
    private String numeroInternoAutomatico;

    @Column(name = "EMICHE", nullable = false, length = 1)
    private String emiteCheque;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ULTCHE", precision = 15, scale = 4)
    private BigDecimal numeroUltimoCheque;

    @Column(name = "CODMON", nullable = false, length = 1)
    private String codigoMoneda;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "CODBCO", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Banco banco;

    @JoinColumn(name = "CODTAR", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private TarjetaCredito tarjetaCredito;

    @Column(name = "VALDUP")
    private Character validaDuplicidad;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Embedded
    private Auditoria auditoria;

    public CuentaTesoreria() {
    }

    public CuentaTesoreria(String codigo) {
        this.codigo = codigo;
    }

    public CuentaTesoreria(String codigo, String descrp, String Indica, String Emiche) {
        this.codigo = codigo;
        this.descripcion = descrp;
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

    public TipoCuentaTesoreria getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuentaTesoreria tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getNumeroInternoAutomatico() {
        return numeroInternoAutomatico;
    }

    public void setNumeroInternoAutomatico(String numeroInternoAutomatico) {
        this.numeroInternoAutomatico = numeroInternoAutomatico;
    }

    public String getEmiteCheque() {
        return emiteCheque;
    }

    public void setEmiteCheque(String emiteCheque) {
        this.emiteCheque = emiteCheque;
    }

    public BigDecimal getNumeroUltimoCheque() {
        return numeroUltimoCheque;
    }

    public void setNumeroUltimoCheque(BigDecimal numeroUltimoCheque) {
        this.numeroUltimoCheque = numeroUltimoCheque;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public TarjetaCredito getTarjetaCredito() {
        return tarjetaCredito;
    }

    public void setTarjetaCredito(TarjetaCredito tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }

    public Character getValidaDuplicidad() {
        return validaDuplicidad;
    }

    public void setValidaDuplicidad(Character validaDuplicidad) {
        this.validaDuplicidad = validaDuplicidad;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
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
    public CuentaTesoreria clone() throws CloneNotSupportedException {
        return (CuentaTesoreria) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
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
        final CuentaTesoreria other = (CuentaTesoreria) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.tesoreria.modelo.CuentaTesoreria[ codigo=" + codigo + " ]";
    }

}
