package bs.administracion.modelo;

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
import bs.global.modelo.Auditoria;
import bs.mantenimiento.modelo.MovimientoMantenimiento;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.produccion.modelo.MovimientoProduccion;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.stock.modelo.MovimientoStock;
import bs.taller.modelo.MovimientoTaller;
import bs.tarea.modelo.Tarea;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.MovimientoVenta;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.mail.internet.InternetAddress;
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
@Table(name = "ad_correo_electronico")
@EntityListeners(AuditoriaListener.class)
public class CorreoElectronico implements IAuditableEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    //Fecha del movimiento
    @Column(name = "FCHENV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaEnvio;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(optional = false)
    private EntidadComercial entidad;

    @Column(name = "DIRENV", length = 255)
    private String direccionEnvio;

    @Column(name = "DESTIN", length = 255)
    private String destinatarios;

    @Column(name = "DESCPY", length = 255)
    private String destinatariosCopia;

    @Column(name = "ASUNTO", length = 255)
    private String asunto;

    @Lob
    @Column(name = "CONTEN", length = 2147483647)
    private String contenido;

    @Column(name = "ENVIAD", length = 1)
    private String enviado;

    @Lob
    @Column(name = "ERROREN", length = 2147483647)
    private String error;

    @Lob
    @Column(name = "PATHAD", length = 2147483647)
    private String pathArchivo;

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

    @Embedded
    private Auditoria auditoria;

    @Transient
    private InternetAddress dirFrom;

    @Transient
    private File archivoAdjunto;

    public CorreoElectronico() {

        this.fechaEnvio = new Date();
        this.enviado = "N";
        this.auditoria = new Auditoria();

    }

    public CorreoElectronico(String asunto, String destinatarios, String contenido) {
        this.asunto = asunto;
        this.destinatarios = destinatarios;
        this.contenido = contenido;
        this.fechaEnvio = new Date();
        this.enviado = "N";
        this.auditoria = new Auditoria();
    }

    public CorreoElectronico(String asunto, String destinatarios, String mensaje, String archivo) {

        this.asunto = asunto;
        this.destinatarios = destinatarios;
        this.contenido = mensaje;
        this.pathArchivo = archivo;
        this.fechaEnvio = new Date();
        this.enviado = "N";
        this.auditoria = new Auditoria();

    }

    public CorreoElectronico(InternetAddress internetAddress, String asunto, String destinatarios, String mensaje) {

        this.asunto = asunto;
        this.direccionEnvio = internetAddress.getAddress();
        this.destinatarios = destinatarios;
        this.contenido = mensaje;
        this.dirFrom = internetAddress;
        this.fechaEnvio = new Date();
        this.enviado = "N";
        this.auditoria = new Auditoria();

    }

    public CorreoElectronico(InternetAddress internetAddress, String asunto, String destinatarios, String mensaje, String archivo) {

        this.asunto = asunto;
        this.direccionEnvio = internetAddress.getAddress();
        this.destinatarios = destinatarios;
        this.contenido = mensaje;
        this.dirFrom = internetAddress;
        this.pathArchivo = archivo;
        this.fechaEnvio = new Date();
        this.enviado = "N";
        this.auditoria = new Auditoria();

    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public InternetAddress getDirFrom() {
        return dirFrom;
    }

    public void setDirFrom(InternetAddress dirFrom) {
        this.dirFrom = dirFrom;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getPathArchivo() {
        return pathArchivo;
    }

    public void setPathArchivo(String pathArchivo) {
        this.pathArchivo = pathArchivo;
    }

    public String getDestinatariosCopia() {
        return destinatariosCopia;
    }

    public void setDestinatariosCopia(String destinatariosCopia) {
        this.destinatariosCopia = destinatariosCopia;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public File getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(File archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CorreoElectronico other = (CorreoElectronico) obj;
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
