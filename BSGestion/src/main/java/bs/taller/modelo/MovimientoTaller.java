/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.Provincia;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.MovimientoStock;
import bs.ventas.modelo.ListaPrecioVenta;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "tl_movimiento")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class MovimientoTaller implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false),
        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    CircuitoTaller circuito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID")
    private MovimientoTaller movimientoAplicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAPO", referencedColumnName = "ID")
    private MovimientoTaller movimientoAplicado;

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

    @NotNull
    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @NotNull
    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @Column(name = "FCHREQ")
    @Temporal(TemporalType.DATE)
    private Date fechaRequerida;
    @Column(name = "FCHINI")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FCHEGR")
    @Temporal(TemporalType.DATE)
    private Date fechaEgreso;

    @JoinColumn(name = "CODEQU", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Equipo equipo;

    @JoinColumn(name = "TECNIC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tecnico tecnico;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    @JoinColumn(name = "CONTAC", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contacto contacto;

    @Column(name = "NOMCON", nullable = false, length = 200)
    private String nombreContacto;

    @Column(name = "DIRECC", length = 60)
    private String direccion;
    @Column(name = "barrio", length = 6)
    private String barrio;

    //Codigo postal entrega
    @JoinColumn(name = "codloc", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumns({
        @JoinColumn(name = "codpro", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "codpai", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    //Moneda de registracion
    @JoinColumn(name = "monreg", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "monsec", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "cotiza", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Size(max = 20)
    @Column(name = "priori", length = 20)
    private String prioridad;

    @Lob
    @Size(max = 65535)
    @Column(name = "motivo", length = 65535)
    private String motivo;
    @Lob
    @Size(max = 65535)
    @Column(name = "diagno", length = 65535)
    private String diagnostico;
    @Lob
    @Size(max = 65535)
    @Column(name = "soluci", length = 65535)
    private String solucion;
    @Lob
    @Size(max = 65535)
    @Column(name = "acceso", length = 65535)
    private String datosAcceso;
    @Lob
    @Size(max = 65535)
    @Column(name = "observ", length = 65535)
    private String observaciones;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaPrecio;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MST", referencedColumnName = "ID")
    MovimientoStock movimientoStock;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MFC", referencedColumnName = "ID")
    MovimientoFacturacion movimientoFacturacion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemProductoTaller> itemsProducto;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemServicioTaller> itemsServicio;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemAplicacionTaller> itemsAplicacion;

    @Transient
    private PuntoVenta puntoVentaStock;
    @Transient
    private Comprobante comprobanteStock;

    @Embedded
    private Auditoria auditoria;

    public MovimientoTaller() {

        this.itemsProducto = new ArrayList<ItemProductoTaller>();
        this.itemsServicio = new ArrayList<ItemServicioTaller>();
        this.auditoria = new Auditoria();
    }

    public MovimientoTaller(Integer id) {
        this.id = id;
        this.itemsProducto = new ArrayList<ItemProductoTaller>();
        this.itemsServicio = new ArrayList<ItemServicioTaller>();
        auditoria = new Auditoria();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CircuitoTaller getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoTaller circuito) {
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

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
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

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaEgreso() {
        return fechaEgreso;
    }

    public void setFechaEgreso(Date fechaEgreso) {
        this.fechaEgreso = fechaEgreso;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
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

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getDatosAcceso() {
        return datosAcceso;
    }

    public void setDatosAcceso(String datosAcceso) {
        this.datosAcceso = datosAcceso;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Deposito getDepositoTransferencia() {
        return depositoTransferencia;
    }

    public void setDepositoTransferencia(Deposito depositoTransferencia) {
        this.depositoTransferencia = depositoTransferencia;
    }

    public MovimientoStock getMovimientoStock() {
        return movimientoStock;
    }

    public void setMovimientoStock(MovimientoStock movimientoStock) {
        this.movimientoStock = movimientoStock;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public PuntoVenta getPuntoVentaStock() {
        return puntoVentaStock;
    }

    public void setPuntoVentaStock(PuntoVenta puntoVentaStock) {
        this.puntoVentaStock = puntoVentaStock;
    }

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public MovimientoFacturacion getMovimientoFacturacion() {
        return movimientoFacturacion;
    }

    public void setMovimientoFacturacion(MovimientoFacturacion movimientoFacturacion) {
        this.movimientoFacturacion = movimientoFacturacion;
    }

    public List<ItemProductoTaller> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<ItemProductoTaller> itemsProducto) {
        this.itemsProducto = itemsProducto;
    }

    public List<ItemAplicacionTaller> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemAplicacionTaller> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public MovimientoTaller getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoTaller movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public List<ItemServicioTaller> getItemsServicio() {
        return itemsServicio;
    }

    public void setItemsServicio(List<ItemServicioTaller> itemsServicio) {
        this.itemsServicio = itemsServicio;
    }

    public MovimientoTaller getMovimientoAplicado() {
        return movimientoAplicado;
    }

    public void setMovimientoAplicado(MovimientoTaller movimientoAplicado) {
        this.movimientoAplicado = movimientoAplicado;
    }

    public ListaPrecioVenta getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecioVenta listaPrecio) {
        this.listaPrecio = listaPrecio;
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
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoTaller other = (MovimientoTaller) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Movimiento{" + "id=" + id + '}';
    }

}
