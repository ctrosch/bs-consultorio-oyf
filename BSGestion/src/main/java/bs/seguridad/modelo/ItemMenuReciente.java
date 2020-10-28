/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "sg_menu_reciente")
@IdClass(ItemMenuRecientePK.class)
public class ItemMenuReciente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_USUARIO", nullable = false)
    private int idUsuario;
    @Id
    @Column(name = "COD_MENU", nullable = false)
    private String codMenu;
    
    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @JoinColumn(name="ID_USUARIO",referencedColumnName="id", nullable=false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JoinColumn(name="COD_MENU",referencedColumnName="codigo", nullable=false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;
    
    public ItemMenuReciente() {

    }

    public ItemMenuReciente(int idUsuario, String codMenu) {
        this.idUsuario = idUsuario;
        this.codMenu = codMenu;
    }

    public String getCodMenu() {
        return codMenu;
    }

    public void setCodMenu(String codMenu) {
        this.codMenu = codMenu;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.idUsuario;
        hash = 29 * hash + (this.codMenu != null ? this.codMenu.hashCode() : 0);
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
        final ItemMenuReciente other = (ItemMenuReciente) obj;
        if (this.idUsuario != other.idUsuario) {
            return false;
        }
        if ((this.codMenu == null) ? (other.codMenu != null) : !this.codMenu.equals(other.codMenu)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMenuReciente{" + "idUsuario=" + idUsuario + ", codMenu=" + codMenu + '}';
    }
    
}
