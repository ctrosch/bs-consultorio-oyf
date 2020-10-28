/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "ad_perfil_cuenta")
@EntityListeners(AuditoriaListener.class)
public class PerfilCuenta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOMBRE", length = 100)
    private String nombre;
    @Column(name = "USUARIO", length = 100)
    private String usuario;
    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "DIRENV", length = 100)
    private String direccionEnvio;
    @Column(name = "NOMENV", length = 100)
    private String nombreEnvio;

    @Column(name = "HOST", length = 100)
    private String host;
    @Column(name = "STLS", length = 100)
    private String starttls;
    @Column(name = "PUERTO", length = 100)
    private String puerto;
    @Column(name = "AUTORI", length = 100)
    private String auth;
    @Column(name = "TISMTP", length = 100)
    private String smtpTipo;

    @Transient
    private boolean debug;

    @Transient
    private String debugMensaje;

    @Embedded
    private Auditoria auditoria;

    public PerfilCuenta() {

        this.debug = false;
        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getNombreEnvio() {
        return nombreEnvio;
    }

    public void setNombreEnvio(String nombreEnvio) {
        this.nombreEnvio = nombreEnvio;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStarttls() {
        return starttls;
    }

    public void setStarttls(String starttls) {
        this.starttls = starttls;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getSmtpTipo() {
        return smtpTipo;
    }

    public void setSmtpTipo(String smtpTipo) {
        this.smtpTipo = smtpTipo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getDebugMensaje() {
        return debugMensaje;
    }

    public void setDebugMensaje(String debugMensaje) {
        this.debugMensaje = debugMensaje;
    }

    @Override
    public PerfilCuenta clone() throws CloneNotSupportedException {
        return (PerfilCuenta) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final PerfilCuenta other = (PerfilCuenta) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PerfilCuenta{" + "id=" + id + '}';
    }

}
