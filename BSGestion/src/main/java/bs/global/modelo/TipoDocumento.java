/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ctrosch
 */
@Entity
@Table(name = "gr_tipo_documento")
@EntityListeners(AuditoriaListener.class)
public class TipoDocumento implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CODIGO", length = 6)
    private String codigo;

    @Column(name = "DESCRP", length = 60)
    private String descripcion;

    @Column(name = "MASKDC", length = 30)
    private String mascara;

    @Column(name = "VALDUP", length = 1)
    private String validaDuplicidad;

    @Lob
    @Column(name = "SCRIPT", length = 2147483647)
    private String script;

    @Embedded
    private Auditoria auditoria;

    public TipoDocumento() {
        this.auditoria = new Auditoria();
    }

    public TipoDocumento(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMascara() {
        return (mascara == null || mascara.isEmpty() ? "99999999999" : mascara);
    }

    public String getFormato() {
        return (mascara == null || mascara.isEmpty() ? "00000000000" : mascara.replace("9", "#"));
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

    public String getValidaDuplicidad() {
        return validaDuplicidad;
    }

    public void setValidaDuplicidad(String validaDuplicidad) {
        this.validaDuplicidad = validaDuplicidad;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final TipoDocumento other = (TipoDocumento) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TipoDocumento{" + "codigo=" + codigo + '}';
    }

}
