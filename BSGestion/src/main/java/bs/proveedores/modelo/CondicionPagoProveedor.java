/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pv_condicion_pago")
@EntityListeners(AuditoriaListener.class)
public class CondicionPagoProveedor implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODIGO")
    private String codigo;
    
    @Size(max = 60)
    @Column(name = "DESCRP")
    private String descripcion;
    
    @Size(max = 60)
    @Column(name = "IMPCTA")
    private String imputaCuentaCorriente;
    
    @Size(max = 10)    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condicionPago", fetch=FetchType.LAZY)
    private List<ItemCondicionPagoProveedor> cuotas;
    
    @Embedded
    private Auditoria auditoria;
    

    public CondicionPagoProveedor() {
        
        this.auditoria = new Auditoria();
    }

    public CondicionPagoProveedor(String codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
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

    public String getImputaCuentaCorriente() {
        return imputaCuentaCorriente;
    }

    public void setImputaCuentaCorriente(String imputaCuentaCorriente) {
        this.imputaCuentaCorriente = imputaCuentaCorriente;
    }

    public List<ItemCondicionPagoProveedor> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<ItemCondicionPagoProveedor> cuotas) {
        this.cuotas = cuotas;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CondicionPagoProveedor)) {
            return false;
        }
        CondicionPagoProveedor other = (CondicionPagoProveedor) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.compras.modelo.CondicionPagoProveedor[ codigo=" + codigo + " ]";
    }
    
}
