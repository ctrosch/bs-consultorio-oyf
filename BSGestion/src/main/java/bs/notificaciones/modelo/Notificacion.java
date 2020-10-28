/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.notificaciones.modelo;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Reporte;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "nt_notificacion")
@EntityListeners(AuditoriaListener.class)
public class Notificacion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Size(max = 200)
    @Column(name = "DESCRP")
    private String descripcion;

    @Size(max = 1)
    @Column(name = "MENPSH")
    private String mensajePush;

    @Size(max = 1)
    @Column(name = "EMAIL")
    private String email;

    @Size(max = 1)
    @Column(name = "WHSAAP")
    private String whatsapp;

    @JoinColumn(name = "MODULO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modulo modulo;

    @JoinColumn(name = "CODREP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporte;

    @OneToMany(mappedBy = "notificacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemNotificacionGrupo> grupos;

    @OneToMany(mappedBy = "notificacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemNotificacionUsuario> usuarios;

    @Embedded
    private Auditoria auditoria;

    public Notificacion() {
        this.auditoria = new Auditoria();
        this.email = "N";
        this.mensajePush = "N";
        this.whatsapp = "N";
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

    public String getMensajePush() {
        return mensajePush;
    }

    public void setMensajePush(String mensajePush) {
        this.mensajePush = mensajePush;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public List<ItemNotificacionGrupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<ItemNotificacionGrupo> grupos) {
        this.grupos = grupos;
    }

    public List<ItemNotificacionUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<ItemNotificacionUsuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public Notificacion clone() throws CloneNotSupportedException {
        return (Notificacion) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.codigo);
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
        final Notificacion other = (Notificacion) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Notificacion{" + "codigo=" + codigo + '}';
    }

}
