/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.modelo;

import java.io.Serializable;

/**
 *
 * @author Claudio
 */

public class DireccionEntregaEntidadPK implements Serializable {

    private String codigo;
    private String nrocta;
    
    public DireccionEntregaEntidadPK() {
        
    }

    public DireccionEntregaEntidadPK(String codigo, String nrocta) {
        this.codigo = codigo;
        this.nrocta = nrocta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 67 * hash + (this.nrocta != null ? this.nrocta.hashCode() : 0);
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
        final DireccionEntregaEntidadPK other = (DireccionEntregaEntidadPK) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.nrocta == null) ? (other.nrocta != null) : !this.nrocta.equals(other.nrocta)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "DireccionesDeEntregaVentaPK{" + "codigo=" + codigo + ", nrocta=" + nrocta + '}';
    }
    
    
    
}
