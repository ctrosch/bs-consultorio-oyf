/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.ArchivoAdjunto;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
import bs.global.modelo.Provincia;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoDocumento;
import bs.ventas.modelo.MovimientoVenta;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
@Table(name = "sa_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoSalud implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoSalud estado;

    //Comprobante
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

    //Profesional
    @JoinColumn(name = "CODPRF", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial profesional;

    //Paciente
    @JoinColumn(name = "CODPAC", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial paciente;

    //Obra Social
    @JoinColumn(name = "CODOSC", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial obraSocial;

    @Column(name = "NOMPAC", nullable = false, length = 200)
    private String nombrePaciente;

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

    @Column(name = "HORMOV", nullable = true)
    @Temporal(TemporalType.TIME)
    private Date horaMovimiento;

    @Column(name = "DESCRP", nullable = true, length = 200)
    private String descripcion;

    @Lob
    @Column(name = "DETCSA", length = 2147483647)
    private String detalleConsulta;

    //Observaciones
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "NOMPRF", nullable = false, length = 200)
    private String nombreProfesional;

    @Column(name = "NOMOSC", nullable = true, length = 200)
    private String nombreObraSocial;

    @Column(name = "NROAFL", nullable = true, length = 60)
    private String numeroAfiliado;

    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoSalud movimientoAplicado;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoSalud movimientoReversion;

    @JoinColumn(name = "ID_MED", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoSalud movimientoPrincipal;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    private MovimientoVenta movimientoVenta;

    @OneToMany(mappedBy = "movimientoPrincipal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaMovimiento")
    private List<MovimientoSalud> movimientosRelacionados;

    @OneToMany(mappedBy = "movimientoSalud", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaAdjunto")
    private List<ArchivoAdjunto> adjuntos;

    @Transient
    private List<MovimientoSalud> historiaClinica;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean seleccionado;

    @Transient
    private boolean conError;

    public MovimientoSalud() {

        auditoria = new Auditoria();
        movimientosRelacionados = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoSalud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSalud estado) {
        this.estado = estado;
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

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public EntidadComercial getProfesional() {
        return profesional;
    }

    public void setProfesional(EntidadComercial profesional) {
        this.profesional = profesional;
    }

    public EntidadComercial getPaciente() {
        return paciente;
    }

    public void setPaciente(EntidadComercial paciente) {
        this.paciente = paciente;
    }

    public EntidadComercial getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(EntidadComercial obraSocial) {
        this.obraSocial = obraSocial;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
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

    public Date getHoraMovimiento() {
        return horaMovimiento;
    }

    public void setHoraMovimiento(Date horaMovimiento) {
        this.horaMovimiento = horaMovimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDetalleConsulta() {
        return detalleConsulta;
    }

    public void setDetalleConsulta(String detalleConsulta) {
        this.detalleConsulta = detalleConsulta;
    }

    public String getNombreProfesional() {
        return nombreProfesional;
    }

    public void setNombreProfesional(String nombreProfesional) {
        this.nombreProfesional = nombreProfesional;
    }

    public String getNombreObraSocial() {
        return nombreObraSocial;
    }

    public void setNombreObraSocial(String nombreObraSocial) {
        this.nombreObraSocial = nombreObraSocial;
    }

    public String getNumeroAfiliado() {
        return numeroAfiliado;
    }

    public void setNumeroAfiliado(String numeroAfiliado) {
        this.numeroAfiliado = numeroAfiliado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public MovimientoSalud getMovimientoAplicado() {
        return movimientoAplicado;
    }

    public void setMovimientoAplicado(MovimientoSalud movimientoAplicado) {
        this.movimientoAplicado = movimientoAplicado;
    }

    public MovimientoSalud getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoSalud movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public MovimientoSalud getMovimientoPrincipal() {
        return movimientoPrincipal;
    }

    public void setMovimientoPrincipal(MovimientoSalud movimientoPrincipal) {
        this.movimientoPrincipal = movimientoPrincipal;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
    }

    public List<MovimientoSalud> getMovimientosRelacionados() {
        return movimientosRelacionados;
    }

    public void setMovimientosRelacionados(List<MovimientoSalud> movimientosRelacionados) {
        this.movimientosRelacionados = movimientosRelacionados;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public List<ArchivoAdjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<ArchivoAdjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public List<MovimientoSalud> getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(List<MovimientoSalud> historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.id);
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
        final MovimientoSalud other = (MovimientoSalud) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoSalud{" + "id=" + id + '}';
    }

}
