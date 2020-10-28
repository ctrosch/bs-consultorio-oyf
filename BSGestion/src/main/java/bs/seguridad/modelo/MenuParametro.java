/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "sg_menu_parametro")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class MenuParametro implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    @Id        
    @Column(name = "ID")
    private String id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 50)
    @Column(name = "VALOR")
    private String valor;
    
    @JoinColumn(name = "COD_MENU", referencedColumnName = "CODIGO")
    @ManyToOne
    private Menu menu;
    
    @Embedded
    private Auditoria auditoria;

    public MenuParametro() {
        this.auditoria = new Auditoria();
    }
    

    public MenuParametro(String id) {
        this.id = id;
        this.auditoria = new Auditoria();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
    
    @Override
    public MenuParametro clone() throws CloneNotSupportedException{
        return (MenuParametro) super.clone();
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MenuParametro)) {
            return false;
        }
        MenuParametro other = (MenuParametro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.seguridad.modelo.MenuParametro[ id=" + id + " ]";
    }
    
}
