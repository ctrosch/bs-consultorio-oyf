/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "en_entidad_horario")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class EntidadHorario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private EntidadComercial profesionalMedico;

    @Column(name = "DIA", nullable = false, length = 30)
    private String diaSemana;

    @Column(name = "ATIMAN", nullable = false, length = 1)
    private String atiendeTurnoMañana;

    @Column(name = "INITMA")
    @Temporal(TemporalType.TIME)
    private Date horaInicioMañana;

    @Column(name = "FINTMA")
    @Temporal(TemporalType.TIME)
    private Date horaFinMañana;

    @Column(name = "ATITAR", nullable = false, length = 1)
    private String atiendeTurnoTarde;

    @Column(name = "INITTA")
    @Temporal(TemporalType.TIME)
    private Date horaInicioTarde;

    @Column(name = "FINTTA")
    @Temporal(TemporalType.TIME)
    private Date horaFinTarde;

    @Embedded
    private Auditoria auditoria;

    public EntidadHorario() {

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

    public EntidadComercial getProfesionalMedico() {
        return profesionalMedico;
    }

    public void setProfesionalMedico(EntidadComercial profesionalMedico) {
        this.profesionalMedico = profesionalMedico;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getAtiendeTurnoMañana() {
        return atiendeTurnoMañana;
    }

    public void setAtiendeTurnoMañana(String atiendeTurnoMañana) {
        this.atiendeTurnoMañana = atiendeTurnoMañana;
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

    public String getAtiendeTurnoTarde() {
        return atiendeTurnoTarde;
    }

    public void setAtiendeTurnoTarde(String atiendeTurnoTarde) {
        this.atiendeTurnoTarde = atiendeTurnoTarde;
    }

    public Date getHoraInicioTarde() {
        return horaInicioTarde;
    }

    public void setHoraInicioTarde(Date horaInicioTarde) {
        this.horaInicioTarde = horaInicioTarde;
    }

    public Date getHoraFinTarde() {
        return horaFinTarde;
    }

    public void setHoraFinTarde(Date horaFinTarde) {
        this.horaFinTarde = horaFinTarde;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final EntidadHorario other = (EntidadHorario) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntidadHorario{" + "id=" + id + '}';
    }

}
