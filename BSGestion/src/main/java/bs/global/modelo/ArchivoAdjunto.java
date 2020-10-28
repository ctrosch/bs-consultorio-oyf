package bs.global.modelo;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.compra.modelo.MovimientoCompra;
import bs.educacion.modelo.MovimientoEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.mantenimiento.modelo.MovimientoMantenimiento;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.produccion.modelo.MovimientoProduccion;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.salud.modelo.MovimientoSalud;
import bs.stock.modelo.MovimientoStock;
import bs.taller.modelo.MovimientoTaller;
import bs.tarea.modelo.Tarea;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.MovimientoVenta;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "gr_archivo_adjunto")
@EntityListeners(AuditoriaListener.class)
public class ArchivoAdjunto implements IAuditableEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    //Fecha del movimiento
    @Column(name = "FCHADJ", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaAdjunto;

    @Column(name = "NOMBRE", length = 255)
    private String nombre;

    @Lob
    @Column(name = "PATHAD", length = 2147483647)
    private String pathArchivo;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(optional = false)
    private EntidadComercial entidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MFC", referencedColumnName = "ID")
    private MovimientoFacturacion movimientoFacturacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    private MovimientoVenta movimientoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MPV", referencedColumnName = "ID")
    private MovimientoProveedor movimientoProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCO", referencedColumnName = "ID")
    private MovimientoCompra movimientoCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    private MovimientoTesoreria movimientoTesoreria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MTL", referencedColumnName = "ID")
    private MovimientoTaller movimientoTaller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MPD", referencedColumnName = "ID")
    private MovimientoProduccion movimientoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MMT", referencedColumnName = "ID")
    private MovimientoMantenimiento movimientoMantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MTA", referencedColumnName = "ID")
    private Tarea movimientoTarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MED", referencedColumnName = "ID")
    private MovimientoEducacion movimientoEducacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MST", referencedColumnName = "ID")
    private MovimientoStock movimientoStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MPR", referencedColumnName = "ID")
    private MovimientoPrestamo movimientoPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MSA", referencedColumnName = "ID")
    private MovimientoSalud movimientoSalud;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private File archivoAdjunto;

    public ArchivoAdjunto() {

        this.fechaAdjunto = new Date();
        this.auditoria = new Auditoria();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaAdjunto() {
        return fechaAdjunto;
    }

    public void setFechaAdjunto(Date fechaAdjunto) {
        this.fechaAdjunto = fechaAdjunto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPathArchivo() {
        return pathArchivo;
    }

    public void setPathArchivo(String pathArchivo) {
        this.pathArchivo = pathArchivo;
    }

    public MovimientoFacturacion getMovimientoFacturacion() {
        return movimientoFacturacion;
    }

    public void setMovimientoFacturacion(MovimientoFacturacion movimientoFacturacion) {
        this.movimientoFacturacion = movimientoFacturacion;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
    }

    public MovimientoProveedor getMovimientoProveedor() {
        return movimientoProveedor;
    }

    public void setMovimientoProveedor(MovimientoProveedor movimientoProveedor) {
        this.movimientoProveedor = movimientoProveedor;
    }

    public MovimientoCompra getMovimientoCompra() {
        return movimientoCompra;
    }

    public void setMovimientoCompra(MovimientoCompra movimientoCompra) {
        this.movimientoCompra = movimientoCompra;
    }

    public MovimientoTesoreria getMovimientoTesoreria() {
        return movimientoTesoreria;
    }

    public void setMovimientoTesoreria(MovimientoTesoreria movimientoTesoreria) {
        this.movimientoTesoreria = movimientoTesoreria;
    }

    public MovimientoTaller getMovimientoTaller() {
        return movimientoTaller;
    }

    public void setMovimientoTaller(MovimientoTaller movimientoTaller) {
        this.movimientoTaller = movimientoTaller;
    }

    public MovimientoProduccion getMovimientoProduccion() {
        return movimientoProduccion;
    }

    public void setMovimientoProduccion(MovimientoProduccion movimientoProduccion) {
        this.movimientoProduccion = movimientoProduccion;
    }

    public MovimientoMantenimiento getMovimientoMantenimiento() {
        return movimientoMantenimiento;
    }

    public void setMovimientoMantenimiento(MovimientoMantenimiento movimientoMantenimiento) {
        this.movimientoMantenimiento = movimientoMantenimiento;
    }

    public Tarea getMovimientoTarea() {
        return movimientoTarea;
    }

    public void setMovimientoTarea(Tarea movimientoTarea) {
        this.movimientoTarea = movimientoTarea;
    }

    public MovimientoEducacion getMovimientoEducacion() {
        return movimientoEducacion;
    }

    public void setMovimientoEducacion(MovimientoEducacion movimientoEducacion) {
        this.movimientoEducacion = movimientoEducacion;
    }

    public MovimientoStock getMovimientoStock() {
        return movimientoStock;
    }

    public void setMovimientoStock(MovimientoStock movimientoStock) {
        this.movimientoStock = movimientoStock;
    }

    public MovimientoPrestamo getMovimientoPrestamo() {
        return movimientoPrestamo;
    }

    public void setMovimientoPrestamo(MovimientoPrestamo movimientoPrestamo) {
        this.movimientoPrestamo = movimientoPrestamo;
    }

    public MovimientoSalud getMovimientoSalud() {
        return movimientoSalud;
    }

    public void setMovimientoSalud(MovimientoSalud movimientoSalud) {
        this.movimientoSalud = movimientoSalud;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public File getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(File archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArchivoAdjunto other = (ArchivoAdjunto) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CorreoElectronico{" + "id=" + id + '}';
    }

}
