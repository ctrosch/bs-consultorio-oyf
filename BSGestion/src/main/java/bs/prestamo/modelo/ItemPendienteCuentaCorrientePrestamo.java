/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.global.auditoria.AuditoriaListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "zz_pr_pendiente_cc")
//@Table(name = "pr_pendiente_cc")
@EntityListeners(AuditoriaListener.class)
@IdClass(ItemPendienteCuentaCorrientePrestamoPK.class)
@Cacheable(false)
public class ItemPendienteCuentaCorrientePrestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, length = 80)
    private Integer id;
    @Id
    @Column(name = "CUOTAS", nullable = false)
    private int cuotas;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "ID_PRES")
    private Integer idPrestamo;

    @JoinColumn(name = "ID_IPR", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemPrestamo itemPrestamo;

    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codigoFormulario;
    @Basic(optional = false)

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @Column(name = "SUCURS", length = 6)
    private String puntoVenta;

    @Column(name = "DESCOM", length = 60)
    private String comprobante;

    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "MONREG", length = 6)
    private String codigoMonedaRegistracion;

    @Column(name = "COTIZA", precision = 5, scale = 2)
    private double cotizacion;

    @Column(name = "IMPCAP", precision = 15, scale = 4, updatable = false, insertable = false)
    private double capital;

    @Column(name = "IMPINT", precision = 15, scale = 4, updatable = false, insertable = false)
    private double interes;

    @Column(name = "IMPGOT", precision = 15, scale = 4, updatable = false, insertable = false)
    private double gastosOtorgamiento;

    @Column(name = "IMPMCS", precision = 15, scale = 4)
    private double importeMicroseguros;

    @Column(name = "INTMOR", precision = 15, scale = 4, updatable = false, insertable = false)
    private double interesMora;

    @Column(name = "CALMOR", precision = 15, scale = 4, updatable = false, insertable = false)
    private boolean calculaInteresMora;

    @Column(name = "DESINT", precision = 15, scale = 4, updatable = false, insertable = false)
    private double descuentoInteres;

    @Column(name = "CALDES", precision = 15, scale = 4, updatable = false, insertable = false)
    private boolean calculaDescuentoInteres;

    @Column(name = "PENDIENTE", precision = 10, scale = 4, updatable = false, insertable = false)
    private double pendiente;

    @Column(name = "PENDIENTE_SEC", precision = 10, scale = 4, updatable = false, insertable = false)
    private double pendienteSecundario;

    @Transient
    private boolean seleccionado;

    @Transient
    private boolean liquidacionInteresMoraGenerada;

    @Transient
    private double importeAplicar;
    @Transient
    private double importeAplicarSecundario;

    public ItemPendienteCuentaCorrientePrestamo() {

        importeAplicar = 0;
        importeAplicarSecundario = 0;
        capital = 0;
        interes = 0;
        interesMora = 0;
        descuentoInteres = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getCodigoFormulario() {
        return codigoFormulario;
    }

    public void setCodigoFormulario(String codigoFormulario) {
        this.codigoFormulario = codigoFormulario;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public ItemPrestamo getItemPrestamo() {
        return itemPrestamo;
    }

    public void setItemPrestamo(ItemPrestamo itemPrestamo) {
        this.itemPrestamo = itemPrestamo;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public double getInteresMora() {
        return interesMora;
    }

    public void setInteresMora(double interesMora) {
        this.interesMora = interesMora;
    }

    public boolean isCalculaInteresMora() {
        return calculaInteresMora;
    }

    public void setCalculaInteresMora(boolean calculaInteresMora) {
        this.calculaInteresMora = calculaInteresMora;
    }

    public double getDescuentoInteres() {
        return descuentoInteres;
    }

    public void setDescuentoInteres(double descuentoInteres) {
        this.descuentoInteres = descuentoInteres;
    }

    public boolean isCalculaDescuentoInteres() {
        return calculaDescuentoInteres;
    }

    public void setCalculaDescuentoInteres(boolean calculaDescuentoInteres) {
        this.calculaDescuentoInteres = calculaDescuentoInteres;
    }

    public double getPendiente() {
        return pendiente;
    }

    public void setPendiente(double pendiente) {
        this.pendiente = pendiente;
    }

    public double getPendienteSecundario() {
        return pendienteSecundario;
    }

    public void setPendienteSecundario(double pendienteSecundario) {
        this.pendienteSecundario = pendienteSecundario;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public double getImporteAplicar() {
        return importeAplicar;
    }

    public void setImporteAplicar(double importeAplicar) {
        this.importeAplicar = importeAplicar;
    }

    public double getImporteAplicarSecundario() {
        return importeAplicarSecundario;
    }

    public void setImporteAplicarSecundario(double importeAplicarSecundario) {
        this.importeAplicarSecundario = importeAplicarSecundario;
    }

    public double getGastosOtorgamiento() {
        return gastosOtorgamiento;
    }

    public void setGastosOtorgamiento(double gastosOtorgamiento) {
        this.gastosOtorgamiento = gastosOtorgamiento;
    }

    public boolean isLiquidacionInteresMoraGenerada() {
        return liquidacionInteresMoraGenerada;
    }

    public void setLiquidacionInteresMoraGenerada(boolean liquidacionInteresMoraGenerada) {
        this.liquidacionInteresMoraGenerada = liquidacionInteresMoraGenerada;
    }

    public Integer getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public double getImporteMicroseguros() {
        return importeMicroseguros;
    }

    public void setImporteMicroseguros(double importeMicroseguros) {
        this.importeMicroseguros = importeMicroseguros;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + this.cuotas;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemPendienteCuentaCorrientePrestamo other = (ItemPendienteCuentaCorrientePrestamo) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.cuotas != other.cuotas) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemPendienteCuentaCorriente{" + "id=" + id + ", cuotas=" + cuotas + '}';
    }

}
