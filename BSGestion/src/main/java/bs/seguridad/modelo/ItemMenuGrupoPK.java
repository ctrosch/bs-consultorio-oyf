/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.modelo;

import java.io.Serializable;

/**
 *
 * @author ctrosch
 */

public class ItemMenuGrupoPK implements Serializable {
    
    private String codGrupo;    
    private String codMenu;

    public ItemMenuGrupoPK() {
        
    }

    public ItemMenuGrupoPK(String codGrupo, String codMenu) {
        this.codGrupo = codGrupo;
        this.codMenu = codMenu;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

    public String getCodMenu() {
        return codMenu;
    }

    public void setCodMenu(String codMenu) {
        this.codMenu = codMenu;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.codGrupo != null ? this.codGrupo.hashCode() : 0);
        hash = 97 * hash + (this.codMenu != null ? this.codMenu.hashCode() : 0);
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
        final ItemMenuGrupoPK other = (ItemMenuGrupoPK) obj;
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
        return "ItemMenuGrupoPK{" + "codGrupo=" + codGrupo + ", codMenu=" + codMenu + '}';
    }
    
}
