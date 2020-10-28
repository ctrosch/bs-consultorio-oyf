/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import java.io.Serializable;
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
@Table(name = "zz_ed_historico_cc")
public class ItemHistoricoCuentaCorrienteEducacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_MOV", nullable = false)
    private Integer id;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "CODCUR", nullable = false, length = 20)
    private String codcur;

    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codfor;

    @Column(name = "PTOVTA", length = 6)
    private String puntoVenta;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fchmov;

    @Column(name = "DEBE", precision = 38, scale = 2)
    private double debe;
    @Column(name = "HABER", precision = 38, scale = 2)
    private double haber;

    @Column(name = "DESCOM", length = 60)
    private String comprobante;

    @Column(name = "OBSERV", length = 255)
    private String observaciones;

    @Transient
    private double saldo;

    @Transient
    private double saldoSecundario;

    public ItemHistoricoCuentaCorrienteEducacion() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getCodcur() {
        return codcur;
    }

    public void setCodcur(String codcur) {
        this.codcur = codcur;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public Date getFchmov() {
        return fchmov;
    }

    public void setFchmov(Date fchmov) {
        this.fchmov = fchmov;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldoSecundario() {
        return saldoSecundario;
    }

    public void setSaldoSecundario(double saldoSecundario) {
        this.saldoSecundario = saldoSecundario;
    }

}
