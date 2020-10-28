/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "zz_vt_pendiente_cc")
@EntityListeners(AuditoriaListener.class)
@IdClass(ItemPendienteCuentaCorrientePK.class)
@Cacheable(false)
public class ItemPendienteCuentaCorriente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false, length = 80)
    private Integer id;
    @Id
    @Column(name = "CUOTAS", nullable = false)
    private int cuotas;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

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
    private BigDecimal cotizacion;

    @Column(name = "PENDIENTE", precision = 10, scale = 2)
    private BigDecimal pendiente;

    @Column(name = "PENDIENTE_SEC", precision = 10, scale = 2)
    private BigDecimal pendienteSecundario;

    @Transient
    private boolean seleccionado;
    @Transient
    private BigDecimal importeAplicar;
    @Transient
    private BigDecimal importeAplicarSecundario;

    public ItemPendienteCuentaCorriente() {

        importeAplicar = BigDecimal.ZERO;
        importeAplicarSecundario = BigDecimal.ZERO;
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

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public BigDecimal getImporteAplicar() {
        return importeAplicar;
    }

    public void setImporteAplicar(BigDecimal importeAplicar) {
        this.importeAplicar = importeAplicar;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public BigDecimal getPendienteSecundario() {
        return pendienteSecundario;
    }

    public void setPendienteSecundario(BigDecimal pendienteSecundario) {
        this.pendienteSecundario = pendienteSecundario;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public BigDecimal getImporteAplicarSecundario() {
        return importeAplicarSecundario;
    }

    public void setImporteAplicarSecundario(BigDecimal importeAplicarSecundario) {
        this.importeAplicarSecundario = importeAplicarSecundario;
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
        final ItemPendienteCuentaCorriente other = (ItemPendienteCuentaCorriente) obj;
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
