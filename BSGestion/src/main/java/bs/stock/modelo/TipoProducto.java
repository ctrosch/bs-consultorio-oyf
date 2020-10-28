/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_tipo_producto")
@EntityListeners(AuditoriaListener.class)
public class TipoProducto implements Serializable, IAuditableEntity{
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="TIPPRO", length=6)
    private String codigo;

    @Column(name="DESCRP", length=60)
    private String descripcion;

    @Column(name="STOCKS",length=1)
    private String gestionaStock;
    
    @OneToMany(mappedBy = "tipoProducto", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Atributo> atributos;

    @OneToMany(mappedBy = "tipoProducto", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Rubro01> rubro01;
    
    @OneToMany(mappedBy = "tipoProducto", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Rubro02> rubro02;
    
    @OneToMany(mappedBy = "tipoProducto", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Rubro03> rubro03;
    
    @Embedded
    private Auditoria auditoria;
    
    public TipoProducto() {
        
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

    public String getGestionaStock() {
        return gestionaStock;
    }

    public void setGestionaStock(String gestionaStock) {
        this.gestionaStock = gestionaStock;
    }
    
    public List<Rubro01> getRubro01() {
        return rubro01;
    }

    public void setRubro01(List<Rubro01> rubro01) {
        this.rubro01 = rubro01;
    }

    public List<Rubro02> getRubro02() {
        return rubro02;
    }

    public void setRubro02(List<Rubro02> rubro02) {
        this.rubro02 = rubro02;
    }

    public List<Rubro03> getRubro03() {
        return rubro03;
    }

    public void setRubro03(List<Rubro03> rubro03) {
        this.rubro03 = rubro03;
    }

    public List<Atributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }
    
    
    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoProducto other = (TipoProducto) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    
    @Override
    public String toString() {
        return "tv.producto.modelo.TipoProducto[id=" + codigo + "]";
    }

}
