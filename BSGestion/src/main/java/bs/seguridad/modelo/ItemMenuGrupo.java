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
@Table(name = "sg_menu_grupo")
@IdClass(ItemMenuGrupoPK.class)
public class ItemMenuGrupo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "COD_GRUPO", nullable = false)
    private String codGrupo;
    @Id
    @Column(name = "COD_MENU", nullable = false)
    private String codMenu;

    @JoinColumn(name="COD_GRUPO",referencedColumnName="codigo", nullable=false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Grupo grupo;

    @JoinColumn(name="COD_MENU",referencedColumnName="codigo", nullable=false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;
    
    public ItemMenuGrupo() {

    }

    public ItemMenuGrupo(String codGrupo, String codMenu) {
        this.codGrupo = codGrupo;
        this.codMenu = codMenu;
    }
    
    public String getCodMenu() {
        return codMenu;
    }

    public void setCodMenu(String codMenu) {
        this.codMenu = codMenu;
    }
   
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.codGrupo != null ? this.codGrupo.hashCode() : 0);
        hash = 59 * hash + (this.codMenu != null ? this.codMenu.hashCode() : 0);
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
        final ItemMenuGrupo other = (ItemMenuGrupo) obj;
        if ((this.codGrupo == null) ? (other.codGrupo != null) : !this.codGrupo.equals(other.codGrupo)) {
            return false;
        }
        if ((this.codMenu == null) ? (other.codMenu != null) : !this.codMenu.equals(other.codMenu)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMenuGrupo{" + "codGrupo=" + codGrupo + ", codMenu=" + codMenu + '}';
    }
    
}
