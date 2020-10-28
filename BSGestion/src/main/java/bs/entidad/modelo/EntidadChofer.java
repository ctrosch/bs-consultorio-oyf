/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "en_entidad_chofer")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class EntidadChofer implements Serializable {

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
    private EntidadComercial entidadComercial;

    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombreChofer;

    @Column(name = "APELLI", nullable = false, length = 200)
    private String apellidoChofer;

    @Column(name = "DNI", nullable = true, length = 50)
    private String dniChofer;

    @Embedded
    private Auditoria auditoria;

    public EntidadChofer() {

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

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }

    public String getNombreChofer() {
        return nombreChofer;
    }

    public void setNombreChofer(String nombreChofer) {
        this.nombreChofer = nombreChofer;
    }

    public String getApellidoChofer() {
        return apellidoChofer;
    }

    public void setApellidoChofer(String apellidoChofer) {
        this.apellidoChofer = apellidoChofer;
    }

    public String getDniChofer() {
        return dniChofer;
    }

    public void setDniChofer(String dniChofer) {
        this.dniChofer = dniChofer;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final EntidadChofer other = (EntidadChofer) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntidadChofer{" + "id=" + id + '}';
    }

}
