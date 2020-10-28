/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "pd_parametro")
public class ParametroProduccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", length = 2)
    private String id;

    @Column(name = "fcdrep", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaDesdeDefectoReporte;
    @Column(name = "dfdrep", nullable = false)
    private Integer diasRestarFechaDesdeReportes;
    @Column(name = "fchrep", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaHastaDefectoReporte;

    public ParametroProduccion() {
    }

    public ParametroProduccion(String codigo) {
        this.id = codigo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaDesdeDefectoReporte() {
        return fechaDesdeDefectoReporte;
    }

    public void setFechaDesdeDefectoReporte(Date fechaDesdeDefectoReporte) {
        this.fechaDesdeDefectoReporte = fechaDesdeDefectoReporte;
    }

    public Integer getDiasRestarFechaDesdeReportes() {
        return diasRestarFechaDesdeReportes;
    }

    public void setDiasRestarFechaDesdeReportes(Integer diasRestarFechaDesdeReportes) {
        this.diasRestarFechaDesdeReportes = diasRestarFechaDesdeReportes;
    }

    public Date getFechaHastaDefectoReporte() {
        return fechaHastaDefectoReporte;
    }

    public void setFechaHastaDefectoReporte(Date fechaHastaDefectoReporte) {
        this.fechaHastaDefectoReporte = fechaHastaDefectoReporte;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParametroProduccion other = (ParametroProduccion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "tv.global.modelo.Parametro[usrprmttvid=" + id + "]";
    }

}
