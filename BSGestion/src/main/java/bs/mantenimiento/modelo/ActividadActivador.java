/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Periodo;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "mt_actividad_activador")
@EntityListeners(AuditoriaListener.class)
public class ActividadActivador implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "CODACT", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Actividad actividad;

    @Size(max = 1)
    @Column(name = "TIPACT")
    private String tipoActivador;

    @JoinColumn(name = "PERIOD", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Periodo periodo;

    @JoinColumn(name = "CODEVE", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private EventoMantenimiento evento;

    @Size(max = 200)
    @Column(name = "DESCRP")
    private String descripcion;

    @Column(name = "CANTID")
    private int cantidad;

    @Column(name = "REPITE")
    private String repite;

    @Column(name = "CNTREP")
    private int cantidadReperticiones;

    @Embedded
    private Auditoria auditoria;

    public ActividadActivador() {
        this.auditoria = new Auditoria();
        this.cantidad = 0;
        this.cantidadReperticiones = 0;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public String getTipoActivador() {
        return tipoActivador;
    }

    public void setTipoActivador(String tipoActivador) {
        this.tipoActivador = tipoActivador;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public EventoMantenimiento getEvento() {
        return evento;
    }

    public void setEvento(EventoMantenimiento evento) {
        this.evento = evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getRepite() {
        return repite;
    }

    public void setRepite(String repite) {
        this.repite = repite;
    }

    public int getCantidadReperticiones() {
        return cantidadReperticiones;
    }

    public void setCantidadReperticiones(int cantidadReperticiones) {
        this.cantidadReperticiones = cantidadReperticiones;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActividadActivador)) {
            return false;
        }
        ActividadActivador other = (ActividadActivador) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.mantenimiento.modelo.ActividadAdjunto[ id=" + id + " ]";
    }

}
