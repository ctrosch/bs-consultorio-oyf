/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.MovimientoStock;
import bs.tarea.modelo.Tarea;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "pd_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoProduccion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @JoinColumns({
        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false),
        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private CircuitoProduccion circuito;

    //Comprobante de compra
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    //Punto de venta
    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
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

    //Fecha requerida
    @Column(name = "FCHREQ", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRequerida;

    //Fecha inicio
    @Column(name = "FCHINI", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    //Fecha inicio
    @Column(name = "FCHFIN", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO")
    @ManyToOne(optional = false)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Deposito depositoTransferencia;

    //Departamento
    @JoinColumn(name = "DEPPRO", referencedColumnName = "codigo", nullable = false)
    @ManyToOne
    private DepartamentoProduccion departamento;

    @JoinColumn(name = "CODPLA", referencedColumnName = "codigo", nullable = false)
    @ManyToOne
    private Planta planta;

    //Tipo de comprobante
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPMOV", length = 2)
    private TipoMovimientoProduccion tipoMovimiento;

    //Tipo de comprobante
//    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORI", length = 20)
    private String prioridad;

    //Solicitante
    @Column(name = "SOLICI", length = 40)
    private String solicitante;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MST", referencedColumnName = "id")
    private MovimientoStock movimientoStock;

    @JoinColumn(name = "ID_TAR", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimiento", fetch = FetchType.LAZY)
    private List<ItemMovimientoProduccion> itemsMovimiento;

    @Transient
    private List<ItemMovimientoProduccion> itemsProducto;

    @Transient
    private List<ItemMovimientoProduccion> itemsComponente;

    @Transient
    private List<ItemMovimientoProduccion> itemsProceso;

    @Transient
    private List<ItemMovimientoProduccion> itemsHorario;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private PuntoVenta puntoVentaStock;

    @Transient
    private boolean persistido;
    @Transient
    private String atributo1;
    @Transient
    private String atributo2;
    @Transient
    private String atributo3;
    @Transient
    private String atributo4;
    @Transient
    private String atributo5;
    @Transient
    private String atributo6;
    @Transient
    private String atributo7;

    @Transient
    private Comprobante comprobanteStock;

    @Transient
    private MovimientoProduccion parteProduccion;
    @Transient
    private MovimientoProduccion valeConsumo;
    @Transient
    private MovimientoProduccion parteProceso;

    @Transient
    private boolean noSincronizaNumeroFormulario;

    public MovimientoProduccion() {

        estado = "1";

        fechaMovimiento = new Date();
        fechaRequerida = new Date();

        itemsMovimiento = new ArrayList<>();

        this.auditoria = new Auditoria();
    }

    public MovimientoProduccion(Formulario formulario) {

        estado = "1";

        fechaMovimiento = new Date();
        fechaRequerida = new Date();

        this.formulario = formulario;

        itemsProducto = new ArrayList<ItemMovimientoProduccion>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CircuitoProduccion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoProduccion circuito) {
        this.circuito = circuito;
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

    public Date getFechaRequerida() {
        return fechaRequerida;
    }

    public void setFechaRequerida(Date fechaRequerida) {
        this.fechaRequerida = fechaRequerida;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public DepartamentoProduccion getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoProduccion departamento) {
        this.departamento = departamento;
    }

    public TipoMovimientoProduccion getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoProduccion tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isPersistido() {
        return persistido;
    }

    public void setPersistido(boolean persistido) {
        this.persistido = persistido;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    public Deposito getDepositoTransferencia() {
        return depositoTransferencia;
    }

    public void setDepositoTransferencia(Deposito depositoTransferencia) {
        this.depositoTransferencia = depositoTransferencia;
    }

    public MovimientoProduccion getParteProduccion() {
        return parteProduccion;
    }

    public void setParteProduccion(MovimientoProduccion parteProduccion) {
        this.parteProduccion = parteProduccion;
    }

    public MovimientoProduccion getValeConsumo() {
        return valeConsumo;
    }

    public void setValeConsumo(MovimientoProduccion valeConsumo) {
        this.valeConsumo = valeConsumo;
    }

    public MovimientoProduccion getParteProceso() {
        return parteProceso;
    }

    public void setParteProceso(MovimientoProduccion parteProceso) {
        this.parteProceso = parteProceso;
    }

    public MovimientoStock getMovimientoStock() {
        return movimientoStock;
    }

    public void setMovimientoStock(MovimientoStock movimientoStock) {
        this.movimientoStock = movimientoStock;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public PuntoVenta getPuntoVentaStock() {
        return puntoVentaStock;
    }

    public void setPuntoVentaStock(PuntoVenta puntoVentaStock) {
        this.puntoVentaStock = puntoVentaStock;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public boolean isNoSincronizaNumeroFormulario() {
        return noSincronizaNumeroFormulario;
    }

    public void setNoSincronizaNumeroFormulario(boolean noSincronizaNumeroFormulario) {
        this.noSincronizaNumeroFormulario = noSincronizaNumeroFormulario;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public List<ItemMovimientoProduccion> getItemsMovimiento() {
        return itemsMovimiento;
    }

    public void setItemsMovimiento(List<ItemMovimientoProduccion> itemsMovimiento) {
        this.itemsMovimiento = itemsMovimiento;
    }

    public List<ItemMovimientoProduccion> getItemsProducto() {
        return itemsMovimiento.stream().filter(i -> i.getTipitm().equals("P")).collect(Collectors.toList());
    }

    public void setItemsProducto(List<ItemMovimientoProduccion> itemsProducto) {
        this.itemsProducto = itemsProducto;
    }

    public List<ItemMovimientoProduccion> getItemsComponente() {
        return itemsMovimiento.stream().filter(i -> i.getTipitm().equals("C")).collect(Collectors.toList());
    }

    public void setItemsComponente(List<ItemMovimientoProduccion> itemsComponente) {
        this.itemsComponente = itemsComponente;
    }

    public List<ItemMovimientoProduccion> getItemsProceso() {
        return itemsMovimiento.stream().filter(i -> i.getTipitm().equals("R")).collect(Collectors.toList());
    }

    public void setItemsProceso(List<ItemMovimientoProduccion> itemsProceso) {
        this.itemsProceso = itemsProceso;
    }

    public List<ItemMovimientoProduccion> getItemsHorario() {
        return itemsMovimiento.stream().filter(i -> i.getTipitm().equals("H")).collect(Collectors.toList());
    }

    public void setItemsHorario(List<ItemMovimientoProduccion> itemsHorario) {
        this.itemsHorario = itemsHorario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoProduccion other = (MovimientoProduccion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoProduccion{" + "id=" + id + '}';
    }

}
