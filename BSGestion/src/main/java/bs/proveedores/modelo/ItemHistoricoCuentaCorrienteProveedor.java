/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
@Table(name = "zz_pv_historico_cc")
public class ItemHistoricoCuentaCorrienteProveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_MOV", nullable = false)
    private Integer id;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codfor;

    @Column(name = "SUCURS", length = 6)
    private String puntoVenta;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @Column(name = "SUCORI", length = 6)
    private String puntoVentaOriginal;

    @Column(name = "NROORI", nullable = false)
    private String numeroOriginal;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fchmov;

    @Column(name = "MONREG", length = 6)
    private String codigoMonedaRegistracion;

    @Column(name = "COTIZA", precision = 5, scale = 2)
    private BigDecimal cotizacion;

    @Column(name = "DEBE", precision = 38, scale = 2)
    private BigDecimal debe;
    @Column(name = "HABER", precision = 38, scale = 2)
    private BigDecimal haber;

    @Column(name = "DEBE_SEC", precision = 10, scale = 2)
    private BigDecimal debeSecundario;
    @Column(name = "HABER_SEC", precision = 10, scale = 2)
    private BigDecimal haberSecundario;

    @Column(name = "DESCOM", length = 60)
    private String comprobante;

    @Transient
    private BigDecimal saldo;

    @Transient
    private BigDecimal saldoSecundario;

    public ItemHistoricoCuentaCorrienteProveedor() {
    }

    public ItemHistoricoCuentaCorrienteProveedor(Integer id) {
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

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getPuntoVentaOriginal() {
        return puntoVentaOriginal;
    }

    public void setPuntoVentaOriginal(String puntoVentaOriginal) {
        this.puntoVentaOriginal = puntoVentaOriginal;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public String getpuntoVentaOriginal() {
        return puntoVentaOriginal;
    }

    public void setpuntoVentaOriginal(String puntoVentaOriginal) {
        this.puntoVentaOriginal = puntoVentaOriginal;
    }

    public String getNumeroOriginal() {
        return numeroOriginal;
    }

    public void setNumeroOriginal(String numeroOriginal) {
        this.numeroOriginal = numeroOriginal;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public BigDecimal getDebeSecundario() {
        return debeSecundario;
    }

    public void setDebeSecundario(BigDecimal debeSecundario) {
        this.debeSecundario = debeSecundario;
    }

    public BigDecimal getHaberSecundario() {
        return haberSecundario;
    }

    public void setHaberSecundario(BigDecimal haberSecundario) {
        this.haberSecundario = haberSecundario;
    }

    public BigDecimal getSaldoSecundario() {
        return saldoSecundario;
    }

    public void setSaldoSecundario(BigDecimal saldoSecundario) {
        this.saldoSecundario = saldoSecundario;
    }

}
