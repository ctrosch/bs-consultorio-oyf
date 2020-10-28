/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cg_cuenta_contable")
@EntityListeners(AuditoriaListener.class)
public class CuentaContable implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "NROCTA")
    private String nrocta;
    @Basic(optional = false)
    @Size(min = 1, max = 150)
    @Column(name = "DESCRIP")
    private String descripcion;

    @Column(name = "NIVEL")
    private Integer nivel;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Size(max = 1)
    @Column(name = "imputa")
    private String imputable;
    @Size(max = 1)
    @Column(name = "impuvt")
    private String imputacionVenta;
    @Size(max = 1)
    @Column(name = "impucj")
    private String imputacionTesoreria;
    @Size(max = 1)
    @Column(name = "impupv")
    private String imputacionCompras;
    @Size(max = 1)
    @Column(name = "impucg")
    private String imputacionContabilidad;
    @Size(max = 20)
    @Column(name = "RUBR01")
    private String rubr01;
    @Size(max = 20)
    @Column(name = "RUBR02")
    private String rubr02;
    @Size(max = 20)
    @Column(name = "RUBR03")
    private String rubr03;
    @Size(max = 20)
    @Column(name = "RUBR04")
    private String rubr04;
    @Size(max = 20)
    @Column(name = "RUBR05")
    private String rubr05;
    @Size(max = 20)
    @Column(name = "RUBR06")
    private String rubr06;
    @Size(max = 20)
    @Column(name = "RUBR07")
    private String rubr07;
    @Size(max = 20)
    @Column(name = "RUBR08")
    private String rubr08;
    @Size(max = 20)
    @Column(name = "RUBR09")
    private String rubr09;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuentaContable")
    private List<CuentaContableCentroCosto> centrosCostos;

    @Embedded
    private Auditoria auditoria;

    public CuentaContable() {
        this.auditoria = new Auditoria();
        this.centrosCostos = new ArrayList<CuentaContableCentroCosto>();
        this.imputable = "S";
        this.imputacionCompras = "S";
        this.imputacionContabilidad = "S";
        this.imputacionTesoreria = "S";
        this.imputacionVenta = "S";
         
    }

    public CuentaContable(String nrocta) {

        this.nrocta = nrocta;
        this.auditoria = new Auditoria();
        this.centrosCostos = new ArrayList<CuentaContableCentroCosto>();
        this.imputable = "S";
        this.imputacionCompras = "S";
        this.imputacionContabilidad = "S";
        this.imputacionTesoreria = "S";
        this.imputacionVenta = "S";
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImputable() {
        return imputable;
    }

    public void setImputable(String imputable) {
        this.imputable = imputable;
    }

    public String getImputacionVenta() {
        return imputacionVenta;
    }

    public void setImputacionVenta(String imputacionVenta) {
        this.imputacionVenta = imputacionVenta;
    }

    public String getImputacionTesoreria() {
        return imputacionTesoreria;
    }

    public void setImputacionTesoreria(String imputacionTesoreria) {
        this.imputacionTesoreria = imputacionTesoreria;
    }

    public String getImputacionCompras() {
        return imputacionCompras;
    }

    public void setImputacionCompras(String imputacionCompras) {
        this.imputacionCompras = imputacionCompras;
    }

    public String getImputacionContabilidad() {
        return imputacionContabilidad;
    }

    public void setImputacionContabilidad(String imputacionContabilidad) {
        this.imputacionContabilidad = imputacionContabilidad;
    }

    public String getRubr01() {
        return rubr01;
    }

    public void setRubr01(String rubr01) {
        this.rubr01 = rubr01;
    }

    public String getRubr02() {
        return rubr02;
    }

    public void setRubr02(String rubr02) {
        this.rubr02 = rubr02;
    }

    public String getRubr03() {
        return rubr03;
    }

    public void setRubr03(String rubr03) {
        this.rubr03 = rubr03;
    }

    public String getRubr04() {
        return rubr04;
    }

    public void setRubr04(String rubr04) {
        this.rubr04 = rubr04;
    }

    public String getRubr05() {
        return rubr05;
    }

    public void setRubr05(String rubr05) {
        this.rubr05 = rubr05;
    }

    public String getRubr06() {
        return rubr06;
    }

    public void setRubr06(String rubr06) {
        this.rubr06 = rubr06;
    }

    public String getRubr07() {
        return rubr07;
    }

    public void setRubr07(String rubr07) {
        this.rubr07 = rubr07;
    }

    public String getRubr08() {
        return rubr08;
    }

    public void setRubr08(String rubr08) {
        this.rubr08 = rubr08;
    }

    public String getRubr09() {
        return rubr09;
    }

    public void setRubr09(String rubr09) {
        this.rubr09 = rubr09;
    }

    public List<CuentaContableCentroCosto> getCentrosCostos() {
        return centrosCostos;
    }

    public void setCentrosCostos(List<CuentaContableCentroCosto> centrosCostos) {
        this.centrosCostos = centrosCostos;
    }
    
    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getDescripcionComplete() {
        return (nrocta != null ? nrocta + " - " : "") + (descripcion != null ? descripcion : "");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nrocta != null ? nrocta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CuentaContable)) {
            return false;
        }
        CuentaContable other = (CuentaContable) object;
        if ((this.nrocta == null && other.nrocta != null) || (this.nrocta != null && !this.nrocta.equals(other.nrocta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.contabilidad.dao.PlanCuenta[ nrocta=" + nrocta + " ]";
    }

}
