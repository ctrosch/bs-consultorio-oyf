/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */

@Embeddable
public class Auditoria implements Serializable{

    @Column(name = "FECALT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;
    @Column(name = "FECMOD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
    @Column(name = "USERID", length = 15)
    private String usuario;
    @Column(name = "ULTOPR", length = 1)
    private String ultimaOperacion;    
    @Column(name = "DEBAJA", length = 1)
    private String debaja;
    

    public Auditoria() {

        this.debaja = "N";
        this.ultimaOperacion = "A";    
        this.fechaAlta = new Date();
        this.fechaModificacion = new Date();

    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUltimaOperacion() {
        return ultimaOperacion;
    }

    public void setUltimaOperacion(String ultimaOperacion) {
        this.ultimaOperacion = ultimaOperacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDebaja() {
        return debaja;
    }

    public void setDebaja(String debaja) {
        this.debaja = debaja;
    }
    
}
