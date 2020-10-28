/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.vista;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
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
//@Entity
//@Table(name = "FC_COMPROBANTES_PENDIENTES",  schema = "dbo")
public class ComprobantesPendientes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private String id;
    @Basic(optional = false)
    @Column(name = "MODFOR", nullable = false, length = 2)
    private String modfor;
    @Basic(optional = false)
    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codfor;
    @Basic(optional = false)
    @Column(name = "NROFOR", nullable = false)
    private int nrofor;
    @Basic(optional = false)
    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;
    @Basic(optional = false)
    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;
    @Basic(optional = false)
    @Column(name = "SUCURS", nullable = false, length = 13)
    private String sucurs;
    @Column(name = "CIRCOM", nullable = false, length = 13)
    private String circom;
    @Column(name = "ITEMS_PENDIENTES", precision = 15, scale = 4)
    private BigDecimal itemsPendientes;
    @Column(name = "ESTAUT", nullable = false, length = 13)
    private String estaut;

    public ComprobantesPendientes() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public Date getFchmov() {
        return fechaMovimiento;
    }

    public void setFchmov(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public BigDecimal getItemsPendientes() {
        return itemsPendientes;
    }

    public void setItemsPendientes(BigDecimal itemsPendientes) {
        this.itemsPendientes = itemsPendientes;
    }

    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public int getNrofor() {
        return nrofor;
    }

    public void setNrofor(int nrofor) {
        this.nrofor = nrofor;
    }

    public String getSucurs() {
        return sucurs;
    }

    public void setSucurs(String sucurs) {
        this.sucurs = sucurs;
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getEstaut() {
        return estaut;
    }

    public void setEstaut(String estaut) {
        this.estaut = estaut;
    }

    
}
