/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "mt_actividad")
@EntityListeners(AuditoriaListener.class)
public class Actividad implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;
    @Size(max = 120)
    @Column(name = "DESCRP", length = 120)
    private String descripcion;

    @Column(name = "TMPEST")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tiempoEstimado;

    @Column(name = "TMPESP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tiempoParada;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Subactividad> subactividades;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ActividadAdjunto> adjuntos;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ActividadRecurso> recursos;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ActividadActivador> activador;

    @Embedded
    private Auditoria auditoria;

    public Actividad() {

        this.auditoria = new Auditoria();
        this.activador = new ArrayList<>();
        this.adjuntos = new ArrayList<>();
        this.recursos = new ArrayList<>();
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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

    public List<Subactividad> getSubactividades() {
        return subactividades;
    }

    public void setSubactividades(List<Subactividad> subactividades) {
        this.subactividades = subactividades;
    }

    public List<ActividadAdjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<ActividadAdjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public List<ActividadRecurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<ActividadRecurso> recursos) {
        this.recursos = recursos;
    }

    public List<ActividadActivador> getActivador() {
        return activador;
    }

    public void setActivador(List<ActividadActivador> activador) {
        this.activador = activador;
    }

    public Date getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(Date tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public Date getTiempoParada() {
        return tiempoParada;
    }

    public void setTiempoParada(Date tiempoParada) {
        this.tiempoParada = tiempoParada;
    }

    @Override
    public Actividad clone() throws CloneNotSupportedException {
        return (Actividad) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.codigo);
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
        final Actividad other = (Actividad) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.mantenimiento.modelo.Actividad[ codigo=" + codigo + " ]";
    }

}
