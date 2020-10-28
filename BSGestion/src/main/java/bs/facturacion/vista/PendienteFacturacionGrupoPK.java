/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.vista;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author ctrosch
 */
//@Embeddable
public class PendienteFacturacionGrupoPK implements Serializable {

    private String circom;
    private String codsuc;
    private String canvta;
    private String nrocta;
    private String razonSocial;
    private String nrodoc;
    private String cndpag;
    private String vnddor;
    private String deping;
    private String depegr;
    private String codlis;
    private String direccion;
    private String codpos;
    private String codtra;
    private String monreg;

    private BigDecimal cotizacion;

    public PendienteFacturacionGrupoPK() {
    }

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

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }

    public String getVnddor() {
        return vnddor;
    }

    public void setVnddor(String vnddor) {
        this.vnddor = vnddor;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public void setCodtra(String codtra) {
        this.codtra = codtra;
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

    public String getCanvta() {
        return canvta;
    }

    public void setCanvta(String canvta) {
        this.canvta = canvta;
    }

    public String getCodsuc() {
        return codsuc;
    }

    public void setCodsuc(String codsuc) {
        this.codsuc = codsuc;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.circom);
        hash = 97 * hash + Objects.hashCode(this.codsuc);
        hash = 97 * hash + Objects.hashCode(this.canvta);
        hash = 97 * hash + Objects.hashCode(this.nrocta);
        hash = 97 * hash + Objects.hashCode(this.razonSocial);
        hash = 97 * hash + Objects.hashCode(this.nrodoc);
        hash = 97 * hash + Objects.hashCode(this.cndpag);
        hash = 97 * hash + Objects.hashCode(this.vnddor);
        hash = 97 * hash + Objects.hashCode(this.deping);
        hash = 97 * hash + Objects.hashCode(this.depegr);
        hash = 97 * hash + Objects.hashCode(this.codlis);
        hash = 97 * hash + Objects.hashCode(this.direccion);
        hash = 97 * hash + Objects.hashCode(this.codpos);
        hash = 97 * hash + Objects.hashCode(this.codtra);
        hash = 97 * hash + Objects.hashCode(this.monreg);
        hash = 97 * hash + Objects.hashCode(this.cotizacion);
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
        final PendienteFacturacionGrupoPK other = (PendienteFacturacionGrupoPK) obj;
        if (!Objects.equals(this.circom, other.circom)) {
            return false;
        }
        if (!Objects.equals(this.codsuc, other.codsuc)) {
            return false;
        }
        if (!Objects.equals(this.nrocta, other.nrocta)) {
            return false;
        }
        if (!Objects.equals(this.razonSocial, other.razonSocial)) {
            return false;
        }
        if (!Objects.equals(this.nrodoc, other.nrodoc)) {
            return false;
        }
        if (!Objects.equals(this.cndpag, other.cndpag)) {
            return false;
        }
        if (!Objects.equals(this.vnddor, other.vnddor)) {
            return false;
        }
        if (!Objects.equals(this.deping, other.deping)) {
            return false;
        }
        if (!Objects.equals(this.depegr, other.depegr)) {
            return false;
        }
        if (!Objects.equals(this.codlis, other.codlis)) {
            return false;
        }
        if (!Objects.equals(this.direccion, other.direccion)) {
            return false;
        }
        if (!Objects.equals(this.codpos, other.codpos)) {
            return false;
        }
        if (!Objects.equals(this.codtra, other.codtra)) {
            return false;
        }
        if (!Objects.equals(this.monreg, other.monreg)) {
            return false;
        }
        if (!Objects.equals(this.canvta, other.canvta)) {
            return false;
        }
        if (!Objects.equals(this.cotizacion, other.cotizacion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteFacturacionGrupoPK{" + "circom=" + circom + "codsuc=" + codsuc + ", nrocta=" + nrocta + ", razonSocial=" + razonSocial + ", nrodoc=" + nrodoc + ", cndpag=" + cndpag + ", vnddor=" + vnddor + ", deping=" + deping + ", depegr=" + depegr + ", codlis=" + codlis + ", direccion=" + direccion + ", codpos=" + codpos + ", codtra=" + codtra + ", monreg=" + monreg + ", canvta=" + canvta + ", cotizacion=" + cotizacion + '}';
    }

}
