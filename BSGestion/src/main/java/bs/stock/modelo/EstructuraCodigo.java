/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_estructura_codigo")
@EntityListeners(AuditoriaListener.class)
@IdClass(EstructuraCodiogoPK.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PREFIJ", discriminatorType = DiscriminatorType.STRING, length = 10)
public class EstructuraCodigo implements Serializable, IAuditableEntity{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "TIPPRO", nullable = false, length = 6)
    private String tippro;    
    @Id
    @Column(name = "PREFIJ", nullable = false, length = 20)
    private String prefijo;
    @Id
    @Column(name = "NRORUB", nullable = false, length = 2)
    private String nrorub;
    
    @Column(name="RUBROS", length=30)
    private String rubros;

    @Column(name="DESCRP", length=60)
    private String descripcion;

    @Column(name="SEPARA", length=1)
    private String separador;
    
    @Column(name="VALIDA", length=1)
    private String valida;
    
    @Column(name="MASCAR", length=30)
    private String mascara;    
    
    @Embedded
    private Auditoria auditoria;

    public EstructuraCodigo() {
    }

    public EstructuraCodigo(String tippro, String prefijo, String nrorub) {
        this.tippro = tippro;
        this.prefijo = prefijo;
        this.nrorub = nrorub;
    }

    public String getTippro() {
        return tippro;
    }

    public void setTippro(String tippro) {
        this.tippro = tippro;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getNrorub() {
        return nrorub;
    }

    public void setNrorub(String nrorub) {
        this.nrorub = nrorub;
    }

    public String getRubros() {
        return rubros;
    }

    public void setRubros(String rubros) {
        this.rubros = rubros;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSeparador() {
        return separador;
    }

    public void setSeparador(String separador) {
        this.separador = separador;
    }

    public String getValida() {
        return valida;
    }

    public void setValida(String valida) {
        this.valida = valida;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.tippro != null ? this.tippro.hashCode() : 0);
        hash = 29 * hash + (this.prefijo != null ? this.prefijo.hashCode() : 0);
        hash = 29 * hash + (this.nrorub != null ? this.nrorub.hashCode() : 0);
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
        final EstructuraCodigo other = (EstructuraCodigo) obj;
        if ((this.tippro == null) ? (other.tippro != null) : !this.tippro.equals(other.tippro)) {
            return false;
        }
        if ((this.prefijo == null) ? (other.prefijo != null) : !this.prefijo.equals(other.prefijo)) {
            return false;
        }
        if ((this.nrorub == null) ? (other.nrorub != null) : !this.nrorub.equals(other.nrorub)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EstructuraCodigo{" + "tippro=" + tippro + ", prefijo=" + prefijo + ", nrorub=" + nrorub + '}';
    }
    
}
