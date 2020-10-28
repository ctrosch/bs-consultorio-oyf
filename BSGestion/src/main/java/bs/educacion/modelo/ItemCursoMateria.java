/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "ed_curso_materia")
@EntityListeners(AuditoriaListener.class)
public class ItemCursoMateria implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "CODCUR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Curso curso;

    @JoinColumn(name = "PERCUR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeriodoCursado periodoCursado;

    @JoinColumn(name = "CODMAT", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Materia materia;

    @JoinColumn(name = "CODPRF", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial profesor;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemCursoMateria() {
        this.auditoria = new Auditoria();
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public EntidadComercial getProfesor() {
        return profesor;
    }

    public void setProfesor(EntidadComercial profesor) {
        this.profesor = profesor;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public PeriodoCursado getPeriodoCursado() {
        return periodoCursado;
    }

    public void setPeriodoCursado(PeriodoCursado periodoCursado) {
        this.periodoCursado = periodoCursado;
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
        if (!(object instanceof ItemCursoMateria)) {
            return false;
        }
        ItemCursoMateria other = (ItemCursoMateria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.educacion.modelo.CursoMateria[ id=" + id + " ]";
    }

}
