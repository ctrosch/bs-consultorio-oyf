/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.ventas.modelo.*;
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
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "zz_pr_historico_cc")
public class ItemHistoricoCuentaCorrientePrestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_MOV", nullable = false)
    private Integer id;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codfor;
    @Basic(optional = false)
    @Column(name = "NROFOR", nullable = false)
    private int nrofor;

    @Column(name = "SUCURS", length = 6)
    private String sucurs;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fchmov;

    @Column(name = "MONREG", length = 6)
    private String codigoMonedaRegistracion;

    @Column(name = "COTIZA", precision = 5, scale = 2)
    private double cotizacion;

    @Column(name = "DEBE", precision = 10, scale = 4)
    private double debe;
    @Column(name = "HABER", precision = 10, scale = 4)
    private double haber;

    @Column(name = "DEBE_SEC", precision = 10, scale = 4)
    private double debeSecundario;
    @Column(name = "HABER_SEC", precision = 10, scale = 4)
    private double haberSecundario;

    @Column(name = "DESCOM", length = 60)
    private String comprobante;

    @Transient
    private double saldo;

    @Transient
    private double saldoSecundario;

    public ItemHistoricoCuentaCorrientePrestamo() {
    }

    public ItemHistoricoCuentaCorrientePrestamo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public Date getFchmov() {
        return fchmov;
    }

    public void setFchmov(Date fechaMovimiento) {
        this.fchmov = fechaMovimiento;
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

    public double getDebe() {
        return debe;
    }

    public void setDebe(double debe) {
        this.debe = debe;
    }

    public double getHaber() {
        return haber;
    }

    public void setHaber(double haber) {
        this.haber = haber;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public double getDebeSecundario() {
        return debeSecundario;
    }

    public void setDebeSecundario(double debeSecundario) {
        this.debeSecundario = debeSecundario;
    }

    public double getHaberSecundario() {
        return haberSecundario;
    }

    public void setHaberSecundario(double haberSecundario) {
        this.haberSecundario = haberSecundario;
    }

    public double getSaldoSecundario() {
        return saldoSecundario;
    }

    public void setSaldoSecundario(double saldoSecundario) {
        this.saldoSecundario = saldoSecundario;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

}
