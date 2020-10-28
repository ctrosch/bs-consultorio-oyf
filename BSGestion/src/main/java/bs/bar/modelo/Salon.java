/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "br_salon")
@EntityListeners(AuditoriaListener.class)
public class Salon implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;

    @Column(name = "DESCRP", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "CAPACI", nullable = false, length = 200)
    private int capacidad;

    @Embedded
    private Auditoria auditoria;

    public Salon() {

        this.capacidad = 0;
        this.auditoria = new Auditoria();
    }

    public Salon(String codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
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

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final Salon other = (Salon) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UnidadMedida{" + "codigo=" + codigo + '}';
    }

}
