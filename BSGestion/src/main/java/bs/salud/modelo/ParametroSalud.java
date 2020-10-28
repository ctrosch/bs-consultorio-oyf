/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Guillermo
 */
@Entity
@Table(name = "sa_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroSalud implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", length = 2)
    private String id;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoSalud estado;

    @Column(name = "HORIN1", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date horaInicioMañana;

    @Column(name = "HORFI1", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date horaFinMañana;

    @Column(name = "HORIN2", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date horaInicioTarde;

    @Column(name = "HORFI2", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date horaFinTarde;

    @Column(name = "DURACI", nullable = false)
    private Integer duracionTurno;

    @Embedded
    private Auditoria auditoria;

    public ParametroSalud() {
        this.auditoria = new Auditoria();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EstadoSalud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSalud estado) {
        this.estado = estado;
    }

    public Date getHoraInicioMañana() {
        return horaInicioMañana;
    }

    public void setHoraInicioMañana(Date horaInicioMañana) {
        this.horaInicioMañana = horaInicioMañana;
    }

    public Date getHoraFinMañana() {
        return horaFinMañana;
    }

    public void setHoraFinMañana(Date horaFinMañana) {
        this.horaFinMañana = horaFinMañana;
    }

    public Date getHoraFinTarde() {
        return horaFinTarde;
    }

    public void setHoraFinTarde(Date horaFinTarde) {
        this.horaFinTarde = horaFinTarde;
    }

    public Integer getDuracionTurno() {
        return duracionTurno;
    }

    public void setDuracionTurno(Integer duracionTurno) {
        this.duracionTurno = duracionTurno;
    }

    public Date getHoraInicioTarde() {
        return horaInicioTarde;
    }

    public void setHoraInicioTarde(Date horaInicioTarde) {
        this.horaInicioTarde = horaInicioTarde;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
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
        final ParametroSalud other = (ParametroSalud) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ParametroSalud{" + "id=" + id + '}';
    }

}
