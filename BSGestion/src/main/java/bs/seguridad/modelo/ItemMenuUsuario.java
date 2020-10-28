/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "sg_menu_usuario")
@IdClass(ItemMenuUsuarioPK.class)
public class ItemMenuUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_USUARIO", nullable = false)
    private int idUsuario;
    @Id
    @Column(name = "COD_MENU", nullable = false)
    private String codMenu;

    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Usuario usuario;

    @Column(name = "NIVPER")
    private Integer nivelPermiso;

    @JoinColumn(name = "COD_MENU", referencedColumnName = "codigo", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Menu menu;

    public ItemMenuUsuario() {

    }

    public ItemMenuUsuario(int idUsuario, String codMenu) {
        this.idUsuario = idUsuario;
        this.codMenu = codMenu;
        this.nivelPermiso = 3;
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

    public Integer getNivelPermiso() {
        return nivelPermiso;
    }

    public void setNivelPermiso(Integer nivelPermiso) {
        this.nivelPermiso = nivelPermiso;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.idUsuario;
        hash = 67 * hash + (this.codMenu != null ? this.codMenu.hashCode() : 0);
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
        final ItemMenuUsuario other = (ItemMenuUsuario) obj;
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
        return "isd.seguridad.modelo.SG_UsuarioMenuPK[idUsuario=" + idUsuario + ", codMenu=" + codMenu + "]";
    }

}
