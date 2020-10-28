/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "ed_cuenta_corriente")
@EntityListeners(AuditoriaListener.class)
public class AplicacionCuentaCorrienteEducacion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOV", referencedColumnName = "ID")
    MovimientoEducacion movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APL", referencedColumnName = "ID")
    MovimientoEducacion movimientoAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IMOV", referencedColumnName = "ID")
    ItemMovimientoEducacion itemMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "ID")
    ItemMovimientoEducacion itemMovimientoAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITMARA", referencedColumnName = "ID", nullable = false)
    private ItemArancel itemArancel;

    //Alumno
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial alumno;

    //Texto asociado
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "CUOTAS", nullable = false)
    private int cuota;
    @Basic(optional = false)

    //Fecha del movimiento
    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaAplicacion;

    //Fecha de vencimiento de la cuota
    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //Código de imputación
    @Column(name = "IMPTCN", length = 6)
    private String codigoImputacion;

    @Column(name = "IMPORT", precision = 15, scale = 2)
    private double importe;

    @Column(name = "IMPPEN", precision = 15, scale = 2)
    private double importePendiente;

    @Column(name = "IMPORI", precision = 15, scale = 2)
    private double importeOriginal;

    //Importe en moneda secundaria
    @Column(name = "IMPSEC", precision = 15, scale = 2)
    private double importeSecundario;

    //Moneda de Registración
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Cambio de la operación
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    //Fecha de interés
    @Column(name = "FCHINT")
    @Temporal(TemporalType.DATE)
    private Date fechaInteres;

    //Importe de la cuota en moneda nacional
    @Column(name = "IMPINT", precision = 15, scale = 2)
    private double importeInteres;

    @Column(name = "CALINT", length = 1)
    private String interesCalculado;

    @Embedded
    private Auditoria auditoria;

    public AplicacionCuentaCorrienteEducacion() {

        auditoria = new Auditoria();
        importe = 0;
        importeSecundario = 0;
        importeInteres = 0;
        interesCalculado = "N";
        cotizacion = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoEducacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoEducacion movimiento) {
        this.movimiento = movimiento;
    }

    public ItemMovimientoEducacion getItemMovimiento() {
        return itemMovimiento;
    }

    public void setItemMovimiento(ItemMovimientoEducacion itemMovimiento) {
        this.itemMovimiento = itemMovimiento;
    }

    public MovimientoEducacion getMovimientoAplicado() {
        return movimientoAplicado;
    }

    public void setMovimientoAplicado(MovimientoEducacion movimientoAplicado) {
        this.movimientoAplicado = movimientoAplicado;
    }

    public ItemMovimientoEducacion getItemMovimientoAplicado() {
        return itemMovimientoAplicado;
    }

    public void setItemMovimientoAplicado(ItemMovimientoEducacion itemMovimientoAplicado) {
        this.itemMovimientoAplicado = itemMovimientoAplicado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCodigoImputacion() {
        return codigoImputacion;
    }

    public void setCodigoImputacion(String codigoImputacion) {
        this.codigoImputacion = codigoImputacion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(double importeSecundario) {
        this.importeSecundario = importeSecundario;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Date getFechaInteres() {
        return fechaInteres;
    }

    public void setFechaInteres(Date fechaInteres) {
        this.fechaInteres = fechaInteres;
    }

    public EntidadComercial getAlumno() {
        return alumno;
    }

    public void setAlumno(EntidadComercial alumno) {
        this.alumno = alumno;
    }

    public double getImporteInteres() {
        return importeInteres;
    }

    public void setImporteInteres(double importeInteres) {
        this.importeInteres = importeInteres;
    }

    public String getInteresCalculado() {
        return interesCalculado;
    }

    public void setInteresCalculado(String interesCalculado) {
        this.interesCalculado = interesCalculado;
    }

    public double getImportePendiente() {
        return importePendiente;
    }

    public void setImportePendiente(double importePendiente) {
        this.importePendiente = importePendiente;
    }

    public ItemArancel getItemArancel() {
        return itemArancel;
    }

    public void setItemArancel(ItemArancel itemArancel) {
        this.itemArancel = itemArancel;
    }

    public double getImporteOriginal() {
        return importeOriginal;
    }

    public void setImporteOriginal(double importeOriginal) {
        this.importeOriginal = importeOriginal;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public AplicacionCuentaCorrienteEducacion clone() throws CloneNotSupportedException {
        return (AplicacionCuentaCorrienteEducacion) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final AplicacionCuentaCorrienteEducacion other = (AplicacionCuentaCorrienteEducacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AplicacionCuentaCorrienteEducacion{" + "id=" + id + '}';
    }

}
