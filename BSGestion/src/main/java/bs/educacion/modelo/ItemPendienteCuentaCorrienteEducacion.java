/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "zz_ed_pendiente_cc")
@IdClass(ItemPendienteCuentaCorrienteEducacionPK.class)
public class ItemPendienteCuentaCorrienteEducacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false, length = 80)
    private Integer id;

    @Column(name = "OBSERV", length = 255)
    private String observaciones;

    @Id
    @Column(name = "CUOTAS", nullable = false)
    private int cuotas;

    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codigoFormulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @Column(name = "PTOVTA", length = 6)
    private String puntoVenta;

    @Column(name = "DESCOM", length = 60)
    private String comprobante;

    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "PENDIENTE", precision = 10, scale = 4)
    private double pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID", referencedColumnName = "ID", nullable = false, updatable = false, insertable = false)
    ItemMovimientoEducacion itemMovimientoEducacion;

    @Transient
    private boolean seleccionado;
    @Transient
    private double importeInteres;
    @Transient
    private double importeAplicar;
    @Transient
    private double importeAplicarSecundario;

    @Transient
    private boolean calculaInteres;

    @Transient
    private int diasVencidos;

    public ItemPendienteCuentaCorrienteEducacion() {

        importeAplicar = 0;
        importeAplicarSecundario = 0;
        importeInteres = 0;

        calculaInteres = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public double getPendiente() {
        return pendiente;
    }

    public void setPendiente(double pendiente) {
        this.pendiente = pendiente;
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

    public ItemMovimientoEducacion getItemMovimientoEducacion() {
        return itemMovimientoEducacion;
    }

    public void setItemMovimientoEducacion(ItemMovimientoEducacion itemMovimientoEducacion) {
        this.itemMovimientoEducacion = itemMovimientoEducacion;
    }

    public int getDiasVencidos() {
        return diasVencidos;
    }

    public void setDiasVencidos(int diasVencidos) {
        this.diasVencidos = diasVencidos;
    }

    public boolean isCalculaInteres() {
        return calculaInteres;
    }

    public void setCalculaInteres(boolean calculaInteres) {
        this.calculaInteres = calculaInteres;
    }

    public double getImporteInteres() {
        return importeInteres;
    }

    public void setImporteInteres(double importeInteres) {
        this.importeInteres = importeInteres;
    }

    @Override
    public ItemPendienteCuentaCorrienteEducacion clone() throws CloneNotSupportedException {
        return (ItemPendienteCuentaCorrienteEducacion) super.clone();
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
        final ItemPendienteCuentaCorrienteEducacion other = (ItemPendienteCuentaCorrienteEducacion) obj;
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
