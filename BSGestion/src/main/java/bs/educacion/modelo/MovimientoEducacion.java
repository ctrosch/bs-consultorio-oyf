/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.entidad.modelo.EntidadComercial;
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
import bs.global.modelo.TipoDocumento;
import bs.ventas.modelo.MovimientoVenta;
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
@Table(name = "ed_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoEducacion implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoEducacion estado;

//    @JoinColumns({
//        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false),
//        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false)
//    })
//    @ManyToOne(fetch = FetchType.LAZY)
//    private CircuitoEducacion circuito;
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

    //EntidadComercial
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial alumno;

    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombreAlumno;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    //Codigo postal
    @JoinColumn(name = "CODLOC", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @Column(name = "DIRECC", length = 60)
    private String direccion;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    //Observaciones
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "CODCAR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Carrera carrera;

    @JoinColumn(name = "CODPLA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private PlanEstudio planEstudio;

    @JoinColumn(name = "CODCUR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Curso curso;

    @JoinColumn(name = "CODARA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Arancel arancel;

    @Column(name = "PCTBON", precision = 15, scale = 4)
    private double porcentajeBonificacion;

    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoEducacion movimientoAplicado;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoEducacion movimientoReversion;

    @JoinColumn(name = "ID_MED", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoEducacion movimientoPrincipal;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    private MovimientoVenta movimientoVenta;

    @OneToMany(mappedBy = "movimientoPrincipal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaMovimiento")
    private List<MovimientoEducacion> movimientosRelacionados;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoEducacion> itemsMovimiento;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("cuota ASC, fechaVencimiento ASC")
    private List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private double importeTotal;

    @Transient
    private boolean seleccionado;

    @Transient
    private boolean conError;

    public MovimientoEducacion() {

        auditoria = new Auditoria();
        itemsMovimiento = new ArrayList<>();
        itemsCuentaCorriente = new ArrayList<>();
        movimientosRelacionados = new ArrayList<>();
        this.cotizacion = 1;
        this.porcentajeBonificacion = 0;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoEducacion getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoEducacion movimientoReversion) {
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

    public EntidadComercial getAlumno() {
        return alumno;
    }

    public void setAlumno(EntidadComercial alumno) {
        this.alumno = alumno;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
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

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemMovimientoEducacion> getItemsMovimiento() {
        return itemsMovimiento;
    }

    public void setItemsMovimiento(List<ItemMovimientoEducacion> itemsMovimiento) {
        this.itemsMovimiento = itemsMovimiento;
    }

    public List<AplicacionCuentaCorrienteEducacion> getItemsCuentaCorriente() {
        return itemsCuentaCorriente;
    }

    public void setItemsCuentaCorriente(List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente) {
        this.itemsCuentaCorriente = itemsCuentaCorriente;
    }

    public double getImporteTotal() {

        this.importeTotal = 0;

        this.itemsMovimiento.stream().forEach(i -> {
            if (i.getDebeHaber().equals("D")) {
                this.importeTotal = this.importeTotal + i.getImporte();
            }
        });

        if (this.importeTotal == 0) {

            this.itemsMovimiento.stream().forEach(i -> {
                if (i.getDebeHaber().equals("H")) {
                    this.importeTotal = this.importeTotal + i.getImporte();
                }
            });
        }

        return this.importeTotal;
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public MovimientoEducacion getMovimientoPrincipal() {
        return movimientoPrincipal;
    }

    public void setMovimientoPrincipal(MovimientoEducacion movimientoPrincipal) {
        this.movimientoPrincipal = movimientoPrincipal;
    }

    public List<MovimientoEducacion> getMovimientosRelacionados() {
        return movimientosRelacionados;
    }

    public void setMovimientosRelacionados(List<MovimientoEducacion> movimientosRelacionados) {
        this.movimientosRelacionados = movimientosRelacionados;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public EstadoEducacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoEducacion estado) {
        this.estado = estado;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public Arancel getArancel() {
        return arancel;
    }

    public void setArancel(Arancel arancel) {
        this.arancel = arancel;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
    }

    public MovimientoEducacion getMovimientoAplicado() {
        return movimientoAplicado;
    }

    public void setMovimientoAplicado(MovimientoEducacion movimientoAplicado) {
        this.movimientoAplicado = movimientoAplicado;
    }

    public PlanEstudio getPlanEstudio() {
        return planEstudio;
    }

    public void setPlanEstudio(PlanEstudio planEstudio) {
        this.planEstudio = planEstudio;
    }

    public double getPorcentajeBonificacion() {
        return porcentajeBonificacion;
    }

    public void setPorcentajeBonificacion(double porcentajeBonificacion) {
        this.porcentajeBonificacion = porcentajeBonificacion;
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
        final MovimientoEducacion other = (MovimientoEducacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoEducacion{" + "id=" + id + '}';
    }

}
