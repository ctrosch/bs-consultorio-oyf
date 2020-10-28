/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_formula")
@EntityListeners(AuditoriaListener.class)
public class Formula implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    /**
     * Código de formular
     */
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 8)
    private String codigo;
    
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    
    @Column(name = "GENHIS")
    private Character generaHistoricoCambio;
    
    @Column(name = "GENPLA")
    private Character generaFomulaAplanada;
    
    @Column(name = "TIPPLA", length = 8)
    private String tippla;
    @Column(name = "GENAUT")
    private Character genaut;
    @Column(name = "SQLSQL", length = 255)
    private String sqlsql;
    
    @Embedded
    private Auditoria auditoria;

    public Formula() {        
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

    public Character getGeneraHistoricoCambio() {
        return generaHistoricoCambio;
    }

    public void setGeneraHistoricoCambio(Character generaHistoricoCambio) {
        this.generaHistoricoCambio = generaHistoricoCambio;
    }

    public Character getGeneraFomulaAplanada() {
        return generaFomulaAplanada;
    }

    public void setGeneraFomulaAplanada(Character generaFomulaAplanada) {
        this.generaFomulaAplanada = generaFomulaAplanada;
    }

    public String getTippla() {
        return tippla;
    }

    public void setTippla(String tippla) {
        this.tippla = tippla;
    }

    public Character getGenaut() {
        return genaut;
    }

    public void setGenaut(Character genaut) {
        this.genaut = genaut;
    }

    public String getSqlsql() {
        return sqlsql;
    }

    public void setSqlsql(String sqlsql) {
        this.sqlsql = sqlsql;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final Formula other = (Formula) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Fomula{" + "codigo=" + codigo + '}';
    }
    
}
