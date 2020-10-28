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
@Table(name = "en_entidad_camion")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class EntidadCamion implements Serializable {

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

    @Column(name = "MODELO", nullable = false, length = 200)
    private String modelo;

    @Column(name = "DOMCHA", nullable = false, length = 50)
    private String dominioChasis;

    @Column(name = "DOMACO", nullable = true, length = 50)
    private String dominioAcoplado;

    @Embedded
    private Auditoria auditoria;

    public EntidadCamion() {

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

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDominioChasis() {
        return dominioChasis;
    }

    public void setDominioChasis(String dominioChasis) {
        this.dominioChasis = dominioChasis;
    }

    public String getDominioAcoplado() {
        return dominioAcoplado;
    }

    public void setDominioAcoplado(String dominioAcoplado) {
        this.dominioAcoplado = dominioAcoplado;
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
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final EntidadCamion other = (EntidadCamion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntidadCamion{" + "id=" + id + '}';
    }

}
