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

public class ItemMenuUsuarioPK implements Serializable {
    
    private int idUsuario;    
    private String codMenu;

    public ItemMenuUsuarioPK() {
    }

    public ItemMenuUsuarioPK(int idUsuario, String codMenu) {
        this.idUsuario = idUsuario;
        this.codMenu = codMenu;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodMenu() {
        return codMenu;
    }

    public void setCodMenu(String codMenu) {
        this.codMenu = codMenu;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.idUsuario;
        hash = 61 * hash + (this.codMenu != null ? this.codMenu.hashCode() : 0);
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
        final ItemMenuUsuarioPK other = (ItemMenuUsuarioPK) obj;
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
