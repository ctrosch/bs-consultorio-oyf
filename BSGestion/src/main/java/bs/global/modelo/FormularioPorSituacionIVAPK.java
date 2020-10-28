/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */
@Embeddable
public class FormularioPorSituacionIVAPK implements Serializable {

    private String modcom;
    private String codcom;
    private String cndiva;
    private String sucurs;

    public FormularioPorSituacionIVAPK() {

    }

    public FormularioPorSituacionIVAPK(String modcom, String codcom, String puntoVenta, String cndiva) {
        this.modcom = modcom;
        this.codcom = codcom;
        this.cndiva = cndiva;
        this.sucurs = puntoVenta;
    }

    public String getCndiva() {
        return cndiva;
    }

    public void setCndiva(String cndiva) {
        this.cndiva = cndiva;
    }

    public String getCodcom() {
        return codcom;
    }

    public void setCodcom(String codcom) {
        this.codcom = codcom;
    }

    public String getModcom() {
        return modcom;
    }

    public void setModcom(String modcom) {
        this.modcom = modcom;
    }

    public String getSucurs() {
        return sucurs;
    }

    public void setSucurs(String sucurs) {
        this.sucurs = sucurs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.modcom != null ? this.modcom.hashCode() : 0);
        hash = 53 * hash + (this.codcom != null ? this.codcom.hashCode() : 0);
        hash = 53 * hash + (this.cndiva != null ? this.cndiva.hashCode() : 0);
        hash = 53 * hash + (this.sucurs != null ? this.sucurs.hashCode() : 0);
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
        final FormularioPorSituacionIVAPK other = (FormularioPorSituacionIVAPK) obj;
        if ((this.modcom == null) ? (other.modcom != null) : !this.modcom.equals(other.modcom)) {
            return false;
        }
        if ((this.codcom == null) ? (other.codcom != null) : !this.codcom.equals(other.codcom)) {
            return false;
        }
        if ((this.cndiva == null) ? (other.cndiva != null) : !this.cndiva.equals(other.cndiva)) {
            return false;
        }
        if ((this.sucurs == null) ? (other.sucurs != null) : !this.sucurs.equals(other.sucurs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FormularioPorSituacionIVAPK{" + "modcom=" + modcom + ", codcom=" + codcom + ", cndiva=" + cndiva + ", puntoVenta=" + sucurs + '}';
    }

}
