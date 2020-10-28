/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "ed_plan_estudio")
@EntityListeners(AuditoriaListener.class)
public class PlanEstudio implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Size(min = 1, max = 20)
    @Column(name = "CODIGO", length = 20, nullable = false)
    private String codigo;

    @JoinColumn(name = "CODCAR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Carrera carrera;

    @Column(name = "ANIOPL", nullable = false)
    private Integer anio;

    @Size(max = 200)
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Size(max = 50)
    @Column(name = "DURACC")
    private String duracion;

    @JoinColumn(name = "CODMOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ModalidadCursado modalidadCursado;

    @JoinColumn(name = "TIPCER", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoCertificacion tipoCertificacion;

    @JoinColumn(name = "REGLAM", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Reglamento reglamento;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @OneToMany(mappedBy = "planEstudio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemPlanEstudioMateria> materias;

    @Embedded
    private Auditoria auditoria;

    public PlanEstudio() {
        this.auditoria = new Auditoria();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public ModalidadCursado getModalidadCursado() {
        return modalidadCursado;
    }

    public void setModalidadCursado(ModalidadCursado modalidadCursado) {
        this.modalidadCursado = modalidadCursado;
    }

    public TipoCertificacion getTipoCertificacion() {
        return tipoCertificacion;
    }

    public void setTipoCertificacion(TipoCertificacion tipoCertificacion) {
        this.tipoCertificacion = tipoCertificacion;
    }

    public Reglamento getReglamento() {
        return reglamento;
    }

    public void setReglamento(Reglamento reglamento) {
        this.reglamento = reglamento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ItemPlanEstudioMateria> getMaterias() {
        return materias;
    }

    public void setMaterias(List<ItemPlanEstudioMateria> materias) {
        this.materias = materias;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public PlanEstudio clone() throws CloneNotSupportedException {
        return (PlanEstudio) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.codigo);
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
        final PlanEstudio other = (PlanEstudio) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.educacion.modelo.PlanEstudio[ codigo=" + codigo + " ]";
    }

}
