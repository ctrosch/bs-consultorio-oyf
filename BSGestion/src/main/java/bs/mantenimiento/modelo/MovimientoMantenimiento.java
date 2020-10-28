/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.seguridad.modelo.Usuario;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Guillermo
 */
@Entity
@Table(name = "mt_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoMantenimiento implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "ESTADO", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Estado estado;

    //Comprobante de Inscripción
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    //Formulario
    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    //Punto de venta
    @JoinColumn(name = "PTOVTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVenta;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    //Fecha de movimiento
    @Basic(optional = false)
    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    //Observaciones
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto bienUso;

    @JoinColumn(name = "CODPLA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PlanActividad planActividad;

    @JoinColumn(name = "CODDEF", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Defecto defecto;

    @Lob
    @Column(name = "DETDEF", length = 2147483647)
    private String detalleDefecto;

    @JoinColumn(name = "USRSOL", referencedColumnName = "ID", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario solicitante;

    @JoinColumn(name = "USRRES", referencedColumnName = "ID", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario responsable;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoMantenimientoActividad> itemsMovimientoActividad;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoMantenimientoRecurso> itemsMovimientoRecurso;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoMantenimiento movimientoReversion;

    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoMantenimiento movimientoAplicado;

    // @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // private List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente;
    @Embedded
    private Auditoria auditoria;

    @Transient
    private double importeTotal;

    public MovimientoMantenimiento() {

        auditoria = new Auditoria();
        itemsMovimientoActividad = new ArrayList<ItemMovimientoMantenimientoActividad>();
        itemsMovimientoRecurso = new ArrayList<ItemMovimientoMantenimientoRecurso>();
        //itemsCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteEducacion>();
        this.cotizacion = 1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoMantenimiento getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoMantenimiento movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Producto getBienUso() {
        return bienUso;
    }

    public void setBienUso(Producto bienUso) {
        this.bienUso = bienUso;
    }

    public PlanActividad getPlanActividad() {
        return planActividad;
    }

    public void setPlanActividad(PlanActividad planActividad) {
        this.planActividad = planActividad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Defecto getDefecto() {
        return defecto;
    }

    public void setDefecto(Defecto defecto) {
        this.defecto = defecto;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemMovimientoMantenimientoActividad> getItemsMovimientoActividad() {
        return itemsMovimientoActividad;
    }

    public void setItemsMovimientoActividad(List<ItemMovimientoMantenimientoActividad> itemsMovimientoActividad) {
        this.itemsMovimientoActividad = itemsMovimientoActividad;
    }

    public List<ItemMovimientoMantenimientoRecurso> getItemsMovimientoRecurso() {
        return itemsMovimientoRecurso;
    }

    public void setItemsMovimientoRecurso(List<ItemMovimientoMantenimientoRecurso> itemsMovimientoRecurso) {
        this.itemsMovimientoRecurso = itemsMovimientoRecurso;
    }

    // public List<AplicacionCuentaCorrienteEducacion> getItemsCuentaCorriente() {
    //     return itemsCuentaCorriente;
    // }
    // public void setItemsCuentaCorriente(List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente) {
    //     this.itemsCuentaCorriente = itemsCuentaCorriente;
    // }
//    public double getImporteTotal() {
//
//        this.importeTotal = 0;
//
//        this.itemsMovimiento.stream().forEach(i -> {
//
//            if (i.getDebeHaber().equals("D")) {
//                this.importeTotal = this.importeTotal + i.getImporte();
//            }
//
//        });
//        return this.importeTotal;
//    }
    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getDetalleDefecto() {
        return detalleDefecto;
    }

    public void setDetalleDefecto(String detalleDefecto) {
        this.detalleDefecto = detalleDefecto;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public MovimientoMantenimiento getMovimientoAplicado() {
        return movimientoAplicado;
    }

    public void setMovimientoAplicado(MovimientoMantenimiento movimientoAplicado) {
        this.movimientoAplicado = movimientoAplicado;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    @Override
    public MovimientoMantenimiento clone() throws CloneNotSupportedException {
        return (MovimientoMantenimiento) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoMantenimiento other = (MovimientoMantenimiento) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoMantenimiento{" + "id=" + id + '}';
    }

}
