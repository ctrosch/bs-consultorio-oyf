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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "mt_plan_actividad")
@EntityListeners(AuditoriaListener.class)
public class PlanActividad implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;

    @Size(max = 120)
    @Column(name = "DESCRP")
    private String descripcion;

    @OneToMany(mappedBy = "planActividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroOrden")
    private List<ItemPlanActividad> itemsPlanActividad;

    @Embedded
    private Auditoria auditoria;

    public PlanActividad() {

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

    public List<ItemPlanActividad> getItemsPlanActividad() {
        return itemsPlanActividad;
    }

    public void setItemsPlanActividad(List<ItemPlanActividad> itemsPlanActividad) {
        this.itemsPlanActividad = itemsPlanActividad;
    }

    @Override
    public PlanActividad clone() throws CloneNotSupportedException {
        return (PlanActividad) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlanActividad)) {
            return false;
        }
        PlanActividad other = (PlanActividad) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.mantenimiento.modelo.PlanActividad[ codigo=" + codigo + " ]";
    }

}
