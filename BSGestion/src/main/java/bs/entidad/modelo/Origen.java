/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 * Tabla: VTTVND
 *
 */
@Entity
@Table(name="en_origen")
@IdClass(OrigenPK.class)
@EntityListeners(AuditoriaListener.class)
public class Origen implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id    
    @Column(name="CODIGO", length = 6)
    private String codigo;
    
    @Id    
    @Column(name="CODTIP", length = 6)
    private String codtip;    
    
    @Column(name="DESCRP", length=60)
    private String descripcion;
    
    @Embedded
    private Auditoria auditoria;

    public Origen() {
        this.auditoria = new Auditoria();
    }

    public Origen(String codigo, String codtip) {
        this.codigo = codigo;
        this.codtip = codtip;
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

    public String getCodtip() {
        return codtip;
    }

    public void setCodtip(String codtip) {
        this.codtip = codtip;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    @Override
    public String toString() {
        return "codigo=" + codigo + ", codtip=" + codtip ;
    }

}
