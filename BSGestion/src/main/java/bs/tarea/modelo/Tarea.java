/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.modelo;

import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.util.JsfUtil;
import bs.inversiones.modelo.Proyecto;
import bs.produccion.modelo.DepartamentoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.Producto;
import bs.taller.modelo.TipoMantenimiento2;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "ta_tarea")
@XmlRootElement
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    //Comprobante de compra
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    //Sucursal o punto de venta
    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVenta;

    //Fecha de movimiento
    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @ManyToOne
    @JoinColumn(name = "COD_AREA", referencedColumnName = "CODIGO", nullable = false)
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_PRY", referencedColumnName = "CODIGO", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_TMN", referencedColumnName = "CODIGO", nullable = false)
    private TipoMantenimiento2 tipoMantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MPD", referencedColumnName = "ID", nullable = false)
    private MovimientoProduccion movimientoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MST", referencedColumnName = "ID", nullable = false)
    private MovimientoStock movimientoStock;

    @JoinColumn(name = "DEPPRO", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private DepartamentoProduccion departamento;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto proceso;

    @JoinColumn(name = "CODBUS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto bienUso;

    @Column(name = "CANTID", nullable = false)
    private BigDecimal cantidad;

    @Column(name = "ESTADO", nullable = false, length = 1)
    private String estado;

    @Column(name = "HORINI")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;

    @Column(name = "HORFIN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaFin;

    @Column(name = "TIEMPO")
    private Integer tiempo;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "GRUPO", length = 20)
    private String grupoProduccion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarea")
    private List<TareaOperario> operarios;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarea", fetch = FetchType.LAZY)
    private List<MovimientoProduccion> movimientosProduccion;

    @Transient
    String tiempoTotal;

    @Transient
    private Tarea tareaAnular;

    @Transient
    boolean iniciaTareaIndividual;
    @Transient
    boolean ingresaHoraFinManualmente;

    @Embedded
    private Auditoria auditoria;

    public Tarea() {
        this.auditoria = new Auditoria();
        this.operarios = new ArrayList<TareaOperario>();
        this.movimientosProduccion = new ArrayList<MovimientoProduccion>();
        this.cantidad = BigDecimal.ZERO;
    }

    public Tarea(Integer id) {
        this.id = id;
        this.auditoria = new Auditoria();
        this.operarios = new ArrayList<TareaOperario>();
        this.movimientosProduccion = new ArrayList<MovimientoProduccion>();
        this.cantidad = BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public TipoMantenimiento2 getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimiento2 tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public MovimientoProduccion getMovimientoProduccion() {
        return movimientoProduccion;
    }

    public void setMovimientoProduccion(MovimientoProduccion movimientoProduccion) {
        this.movimientoProduccion = movimientoProduccion;
    }

    public DepartamentoProduccion getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoProduccion departamento) {
        this.departamento = departamento;
    }

    public Producto getProceso() {
        return proceso;
    }

    public void setProceso(Producto proceso) {
        this.proceso = proceso;
    }

    public Producto getBienUso() {
        return bienUso;
    }

    public void setBienUso(Producto bienUso) {
        this.bienUso = bienUso;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<TareaOperario> getOperarios() {
        return operarios;
    }

    public void setOperarios(List<TareaOperario> operarios) {
        this.operarios = operarios;
    }

    public String getTiempoTotal() {

        if (horaInicio == null || horaFin == null) {
            return "00:00:00";
        }
        return JsfUtil.diferenciaEntreFechasFormateada(horaInicio, horaFin);
    }

    public void setTiempoTotal(String tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<MovimientoProduccion> getMovimientosProduccion() {
        return movimientosProduccion;
    }

    public void setMovimientosProduccion(List<MovimientoProduccion> movimientosProduccion) {
        this.movimientosProduccion = movimientosProduccion;
    }

    public Integer getTiempo() {
        return tiempo;
    }

    public void setTiempo(Integer tiempo) {
        this.tiempo = tiempo;
    }

    public boolean isIniciaTareaIndividual() {
        return iniciaTareaIndividual;
    }

    public void setIniciaTareaIndividual(boolean iniciaTareaIndividual) {
        this.iniciaTareaIndividual = iniciaTareaIndividual;
    }

    public Tarea getTareaAnular() {
        return tareaAnular;
    }

    public void setTareaAnular(Tarea tareaAnular) {
        this.tareaAnular = tareaAnular;
    }

    public boolean isIngresaHoraFinManualmente() {
        return ingresaHoraFinManualmente;
    }

    public void setIngresaHoraFinManualmente(boolean ingresaHoraFinManualmente) {
        this.ingresaHoraFinManualmente = ingresaHoraFinManualmente;
    }

    public String getGrupoProduccion() {
        return grupoProduccion;
    }

    public void setGrupoProduccion(String grupoProduccion) {
        this.grupoProduccion = grupoProduccion;
    }

    public MovimientoStock getMovimientoStock() {
        return movimientoStock;
    }

    public void setMovimientoStock(MovimientoStock movimientoStock) {
        this.movimientoStock = movimientoStock;
    }

    @Override
    public Tarea clone() throws CloneNotSupportedException {
        return (Tarea) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tarea)) {
            return false;
        }
        Tarea other = (Tarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.produccion.modelo.Tarea[ id=" + id + " ]";
    }

}
