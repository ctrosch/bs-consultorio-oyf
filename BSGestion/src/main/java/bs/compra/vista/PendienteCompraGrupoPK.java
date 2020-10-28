/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.compra.vista;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author ctrosch
 */
//@Embeddable
public class PendienteCompraGrupoPK implements Serializable {
   
    private String circom;       
    private String nrocta;            
    private String nrodoc;        
    private String cndpag;            
    private String comdor;
    private String deping;
    private String depegr;     
    private String codlis;          
    private String direccion;            
    private String codpos;
    private String codtra;        
    private String monreg;        
    private BigDecimal cotizacion;

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }


    public String getDeping() {
        return deping;
    }

    public void setDeping(String deping) {
        this.deping = deping;
    }

    public String getDepegr() {
        return depegr;
    }

    public void setDepegr(String depegr) {
        this.depegr = depegr;
    }

    public String getCodlis() {
        return codlis;
    }

    public void setCodlis(String codlis) {
        this.codlis = codlis;
    }

    public String getCodpos() {
        return codpos;
    }

    public void setCodpos(String codpos) {
        this.codpos = codpos;
    }

    public String getCodtra() {
        return codtra;
    }

    public String getComdor() {
        return comdor;
    }

    public void setComdor(String comdor) {
        this.comdor = comdor;
    }
    
    public void setCodtra(String codtra) {
        this.codtra = codtra;
    }
    
    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMonreg() {
        return monreg;
    }

    public void setMonreg(String monreg) {
        this.monreg = monreg;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 29 * hash + (this.nrocta != null ? this.nrocta.hashCode() : 0);
        hash = 29 * hash + (this.nrodoc != null ? this.nrodoc.hashCode() : 0);
        hash = 29 * hash + (this.cndpag != null ? this.cndpag.hashCode() : 0);
        hash = 29 * hash + (this.comdor != null ? this.comdor.hashCode() : 0);
        hash = 29 * hash + (this.deping != null ? this.deping.hashCode() : 0);
        hash = 29 * hash + (this.depegr != null ? this.depegr.hashCode() : 0);
        hash = 29 * hash + (this.codlis != null ? this.codlis.hashCode() : 0);
        hash = 29 * hash + (this.direccion != null ? this.direccion.hashCode() : 0);
        hash = 29 * hash + (this.codpos != null ? this.codpos.hashCode() : 0);
        hash = 29 * hash + (this.codtra != null ? this.codtra.hashCode() : 0);
        hash = 29 * hash + (this.monreg != null ? this.monreg.hashCode() : 0);
        hash = 29 * hash + (this.cotizacion != null ? this.cotizacion.hashCode() : 0);
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
        final PendienteCompraGrupoPK other = (PendienteCompraGrupoPK) obj;
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.nrocta == null) ? (other.nrocta != null) : !this.nrocta.equals(other.nrocta)) {
            return false;
        }        
        if ((this.nrodoc == null) ? (other.nrodoc != null) : !this.nrodoc.equals(other.nrodoc)) {
            return false;
        }
        if ((this.cndpag == null) ? (other.cndpag != null) : !this.cndpag.equals(other.cndpag)) {
            return false;
        }
        if ((this.comdor == null) ? (other.comdor != null) : !this.comdor.equals(other.comdor)) {
            return false;
        }
        if ((this.deping == null) ? (other.deping != null) : !this.deping.equals(other.deping)) {
            return false;
        }
        if ((this.depegr == null) ? (other.depegr != null) : !this.depegr.equals(other.depegr)) {
            return false;
        }
        if ((this.codlis == null) ? (other.codlis != null) : !this.codlis.equals(other.codlis)) {
            return false;
        }
        if ((this.direccion == null) ? (other.direccion != null) : !this.direccion.equals(other.direccion)) {
            return false;
        }
        if ((this.codpos == null) ? (other.codpos != null) : !this.codpos.equals(other.codpos)) {
            return false;
        }
        if ((this.codtra == null) ? (other.codtra != null) : !this.codtra.equals(other.codtra)) {
            return false;
        }
        if ((this.monreg == null) ? (other.monreg != null) : !this.monreg.equals(other.monreg)) {
            return false;
        }
        if (this.cotizacion != other.cotizacion && (this.cotizacion == null || !this.cotizacion.equals(other.cotizacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteCompraGrupoPK{" + "circom=" + circom + ", nrocta=" + nrocta + ", nrodoc=" + nrodoc + ", cndpag=" + cndpag + ", comdor=" + comdor + ", deping=" + deping + ", depegr=" + depegr + ", codlis=" + codlis + ", direccion=" + direccion + ", codpos=" + codpos + ", codtra=" + codtra + ", monreg=" + monreg + ", cotizacion=" + cotizacion + '}';
    }
    
      
}
