/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_atributo")
@EntityListeners(AuditoriaListener.class)
public class Atributo implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "NROITM", nullable = false)
    private int nroitm;
        
    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO")    
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoProducto tipoProducto;

    @Column(name = "NOMBRE", length = 60)
    private String nombre;
    
    @OneToMany(mappedBy = "atributo", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<AtributoValor> valores;
        
    @Transient
    private boolean conError;
    
    
    @Embedded
    private Auditoria auditoria;

    public Atributo() {        
        this.auditoria = new Auditoria();     
        this.valores = new ArrayList<AtributoValor>();
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<AtributoValor> getValores() {
        return valores;
    }

    public void setValores(List<AtributoValor> valores) {
        this.valores = valores;
    }
    
    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
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

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Atributo other = (Atributo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AtributoTipoProducto{" + "id=" + id + '}';
    }
   
    
}
