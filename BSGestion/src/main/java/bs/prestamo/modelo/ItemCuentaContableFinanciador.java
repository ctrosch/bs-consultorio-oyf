/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_financiador_cuenta_contable")
@XmlRootElement
public class ItemCuentaContableFinanciador implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "CODFIN", referencedColumnName = "ID")
    @ManyToOne
    private Financiador financiador;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean seleccionado;

    @Transient
    private boolean conError;

    public ItemCuentaContableFinanciador() {

        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador financiador) {
        this.financiador = financiador;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final ItemCuentaContableFinanciador other = (ItemCuentaContableFinanciador) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCuentaContableFinanciador{" + "id=" + id + '}';
    }

}
