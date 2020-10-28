/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.modelo;

import bs.global.auditoria.AuditoriaListener;
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
 * @author Claudio
 */
@Entity
@Table(name = "co_circuito_codigo")
@EntityListeners(AuditoriaListener.class)
public class CodigoCircuitoCompra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public CodigoCircuitoCompra() {

        this.auditoria = new Auditoria();
    }

    public CodigoCircuitoCompra(String Circom) {
        this.codigo = Circom;
        this.auditoria = new Auditoria();
    }

    public CodigoCircuitoCompra(String Circom, String Descrp) {
        this.codigo = Circom;
        this.descripcion = Descrp;
        this.auditoria = new Auditoria();
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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

    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (descripcion != null ? descripcion : "");
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
        final CodigoCircuitoCompra other = (CodigoCircuitoCompra) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isd.facturacion.modelo.CodigoCircuitoCompra[Circom=" + codigo + "]";
    }

}
