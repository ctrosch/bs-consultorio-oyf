/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Empresa;
import bs.global.modelo.Localidad;
import bs.global.modelo.Provincia;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoDocumento;
import bs.global.modelo.UnidadNegocio;
import bs.global.modelo.Zona;
import bs.proveedores.modelo.Comprador;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ListaCosto;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.modelo.Cobrador;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.LimiteCreditoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
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

/**
 * @author Claudio
 */
@Entity
@Table(name = "en_entidad")
@EntityListeners(AuditoriaListener.class)
public class EntidadComercial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;
    @Basic(optional = false)

    @Column(name = "NROSUB", length = 13)
    private String nrosub;

    @Column(name = "RAZONS", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "FNTSIA", length = 120)
    private String nombreFantasia;

    @Column(name = "NOMBRE", length = 200)
    private String nombre;

    @Column(name = "SEXO", length = 1)
    private String sexo;

    @Column(name = "MEDPUB", length = 200)
    private String medioPublicitario;

    @Column(name = "TRABAJ", length = 1)
    private String trabajo;

    @Column(name = "ESTREA", length = 100)
    private String estudioRealizado;

    @Column(name = "AMNOMB", length = 200)
    private String nombreAsistenciaMedica;

    @Column(name = "AMTELE", length = 250)
    private String telefonoAsistenciaMedica;

    @Column(name = "AMNRO", length = 50)
    private String nroAfiliadoAsistenciaMedica;

    @Column(name = "AMGRUS", length = 2)
    private String grupoSanguineo;

    @Column(name = "AMRH", length = 1)
    private String factorRh;

    @Column(name = "AMALER", length = 1)
    private String alergia;

    @Column(name = "AMDETA", length = 250)
    private String detalleAlergia;

    @Column(name = "AMTRAT", length = 1)
    private String tratamientoMedico;

    @Column(name = "AMDTRA", length = 250)
    private String detalleTratamientoMedico;

    @Column(name = "AMCEME", length = 200)
    private String contactoEmergencia;

    @Column(name = "AMTCEM", length = 250)
    private String telefonoContactoEmergencia;

    @Column(name = "AMDEXT", length = 250)
    private String datosExtras;

    @Column(name = "FCDIRE", length = 250)
    private String direccionFactura;

    // Dirección fiscal
    @Column(name = "DIRECC", length = 250)
    private String direccion;
    @Column(name = "NUMERO", length = 10)
    private String numero;
    @Column(name = "DPPISO", length = 6)
    private String departamentoPiso;
    @Column(name = "DEPTOS", length = 6)
    private String departamentoNumero;
    @Column(name = "BARRIO", length = 100)
    private String barrio;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumns({
        @JoinColumn(name = "FCPROV", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "FCPAIS", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provinciaFactura;

    @JoinColumn(name = "FCCDLO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidadFactura;

    @Column(name = "TELEFN", length = 30)
    private String telefono;
    @Column(name = "NROFAX", length = 20)
    private String nrofax;
    @Column(name = "NTELEX", length = 15)
    private String ntelex;

    @JoinColumn(name = "CODZON", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Zona zona;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoEntidad estado;

    @JoinColumn(name = "CODTIP", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoEntidad tipo;

    @JoinColumns({
        @JoinColumn(name = "CODCAT", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODTIP", referencedColumnName = "CODTIP", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria;

    @JoinColumns({
        @JoinColumn(name = "CODORI", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODTIP", referencedColumnName = "CODTIP", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Origen origen;

    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @JoinColumn(name = "TIPDO1", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento1;

    @JoinColumn(name = "TIPDO2", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento2;

    @JoinColumn(name = "TIPDO3", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento3;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    @Column(name = "NRODO1", length = 50)
    private String nrodo1;

    @Column(name = "NRODO2", length = 50)
    private String nrodo2;

    @Column(name = "NRODO3", length = 50)
    private String nrodo3;

    @Column(name = "FCHNAC", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;

    @JoinColumn(name = "REPDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Repartidor repartidor;

    @JoinColumn(name = "COBRAD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cobrador cobrador;

    @JoinColumn(name = "COMDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprador comprador;

    @JoinColumn(name = "CNDPVT", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePagoVentas;

    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecioVenta;

    @JoinColumn(name = "CODLCV", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private LimiteCreditoVenta limiteDeCreditoVenta;

    @JoinColumn(name = "CNDPPV", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionPagoProveedor condicionPagoProveedor;

    @JoinColumn(name = "CODLIC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaCosto listaCosto;

    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @JoinColumn(name = "CTAVIN", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cuentaVinculada;

    @Lob
    @Column(name = "NOTAS", length = 2147483647)
    private String notas;

    @Column(name = "DIREML", length = 200)
    private String email;

    @Column(name = "CONTAD", length = 1)
    private String soloContado;

    @Column(name = "ENTCOM", length = 1)
    private String entidadComodin;

    @Column(name = "INHIBE", length = 1)
    private String inhibidoParaFacturacion;
    @Column(name = "LATTUD", precision = 2, scale = 6)
    private BigDecimal coordenadaLatitud;
    @Column(name = "LONTUD", precision = 2, scale = 6)
    private BigDecimal coordenadaLongitud;
    @Column(name = "ULTCON")
    @Temporal(TemporalType.DATE)
    private Date ultimaConsultaAFIP;

    @Column(name = "TIPING", length = 2)
    private String tipoIngresosBrutos;

    @Column(name = "PIDECA", length = 1)
    private String pideCodigoAutorizacion;

    @Column(name = "PCTRET", precision = 10, scale = 2)
    private BigDecimal porcentajeRetencion;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Column(name = "APELLI", length = 200)
    private String apellido;

    @JoinColumn(name = "CANVTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVenta;

    @Column(name = "CNTTUR")
    private int cantidadTurnosDiarios;

    @Column(name = "DURTUR")
    private Integer duracionTurno;

    @Column(name = "NROMAT", length = 30)
    private String nroMatricula;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(mappedBy = "entidad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DireccionEntregaEntidad> direccionesDeEntrega;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<RetencionPorEntidad> retenciones;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<ImpuestoPorEntidad> impuestos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.EAGER)
    private List<Contacto> contactos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<EntidadActividadComercial> actividades;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<EntidadTransporte> transportes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<EntidadObraSocial> obraSociales;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<EntidadCamion> camiones;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadComercial", fetch = FetchType.LAZY)
    private List<EntidadChofer> choferes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profesionalMedico", fetch = FetchType.LAZY)
    private List<EntidadHorario> horarios;

    @Transient
    private String conDeuda;

    @Transient
    private List<ItemPendienteCuentaCorriente> saldos;

    public EntidadComercial() {

        this.nombreFantasia = " ";
        this.auditoria = new Auditoria();
        this.retenciones = new ArrayList<RetencionPorEntidad>();
        this.porcentajeRetencion = BigDecimal.ZERO;
        this.soloContado = "N";
        this.entidadComodin = "N";
        this.pideCodigoAutorizacion = "S";
        this.cantidadTurnosDiarios = 1;
    }

    public EntidadComercial(TipoEntidad tipo) {

        this.tipo = tipo;
        this.auditoria = new Auditoria();
        this.nombreFantasia = " ";
        this.retenciones = new ArrayList<RetencionPorEntidad>();
        this.soloContado = "N";
        this.entidadComodin = "N";
        this.porcentajeRetencion = BigDecimal.ZERO;
        this.pideCodigoAutorizacion = "S";
    }

    public EntidadComercial(String nrocta, TipoEntidad tipo) {

        this.nrocta = nrocta;
        this.nombreFantasia = " ";
        this.tipo = tipo;
        this.auditoria = new Auditoria();
        this.retenciones = new ArrayList<RetencionPorEntidad>();
        this.soloContado = "N";
        this.entidadComodin = "N";
        this.porcentajeRetencion = BigDecimal.ZERO;
        this.pideCodigoAutorizacion = "S";
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getNrosub() {
        return nrosub;
    }

    public void setNrosub(String nrosub) {
        this.nrosub = nrosub;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDepartamentoPiso() {
        return departamentoPiso;
    }

    public void setDepartamentoPiso(String departamentoPiso) {
        this.departamentoPiso = departamentoPiso;
    }

    public String getDepartamentoNumero() {
        return departamentoNumero;
    }

    public void setDepartamentoNumero(String departamentoNumero) {
        this.departamentoNumero = departamentoNumero;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNrofax() {
        return nrofax;
    }

    public void setNrofax(String nrofax) {
        this.nrofax = nrofax;
    }

    public String getNtelex() {
        return ntelex;
    }

    public void setNtelex(String ntelex) {
        this.ntelex = ntelex;
    }

    public TipoEntidad getTipo() {
        return tipo;
    }

    public void setTipo(TipoEntidad tipo) {
        this.tipo = tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public CondicionDeIva getCondicionDeIva() {
        return condicionDeIva;
    }

    public void setCondicionDeIva(CondicionDeIva condicionDeIva) {
        this.condicionDeIva = condicionDeIva;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public TipoDocumento getTipoDocumento1() {
        return tipoDocumento1;
    }

    public void setTipoDocumento1(TipoDocumento tipoDocumento1) {
        this.tipoDocumento1 = tipoDocumento1;
    }

    public TipoDocumento getTipoDocumento2() {
        return tipoDocumento2;
    }

    public void setTipoDocumento2(TipoDocumento tipoDocumento2) {
        this.tipoDocumento2 = tipoDocumento2;
    }

    public TipoDocumento getTipoDocumento3() {
        return tipoDocumento3;
    }

    public void setTipoDocumento3(TipoDocumento tipoDocumento3) {
        this.tipoDocumento3 = tipoDocumento3;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public String getNrodo1() {
        return nrodo1;
    }

    public void setNrodo1(String nrodo1) {
        this.nrodo1 = nrodo1;
    }

    public String getNrodo2() {
        return nrodo2;
    }

    public void setNrodo2(String nrodo2) {
        this.nrodo2 = nrodo2;
    }

    public String getNrodo3() {
        return nrodo3;
    }

    public void setNrodo3(String nrodo3) {
        this.nrodo3 = nrodo3;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
    }

    public CondicionDePagoVenta getCondicionDePagoVentas() {
        return condicionDePagoVentas;
    }

    public void setCondicionDePagoVentas(CondicionDePagoVenta condicionDePagoVentas) {
        this.condicionDePagoVentas = condicionDePagoVentas;
    }

    public ListaPrecioVenta getListaDePrecioVenta() {
        return listaDePrecioVenta;
    }

    public void setListaDePrecioVenta(ListaPrecioVenta listaDePrecioVenta) {
        this.listaDePrecioVenta = listaDePrecioVenta;
    }

    public LimiteCreditoVenta getLimiteDeCreditoVenta() {
        return limiteDeCreditoVenta;
    }

    public void setLimiteDeCreditoVenta(LimiteCreditoVenta limiteDeCreditoVenta) {
        this.limiteDeCreditoVenta = limiteDeCreditoVenta;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String direccionemail) {
        this.email = direccionemail;
    }

    public String getSoloContado() {
        return soloContado;
    }

    public void setSoloContado(String soloContado) {
        this.soloContado = soloContado;
    }

    public String getInhibidoParaFacturacion() {
        return inhibidoParaFacturacion;
    }

    public void setInhibidoParaFacturacion(String inhibidoParaFacturacion) {
        this.inhibidoParaFacturacion = inhibidoParaFacturacion;
    }

    public EstadoEntidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntidad estado) {
        this.estado = estado;
    }

    public BigDecimal getCoordenadaLatitud() {
        return coordenadaLatitud;
    }

    public void setCoordenadaLatitud(BigDecimal coordenadaLatitud) {
        this.coordenadaLatitud = coordenadaLatitud;
    }

    public BigDecimal getCoordenadaLongitud() {
        return coordenadaLongitud;
    }

    public void setCoordenadaLongitud(BigDecimal coordenadaLongitud) {
        this.coordenadaLongitud = coordenadaLongitud;
    }

    public Date getUltimaConsultaAFIP() {
        return ultimaConsultaAFIP;
    }

    public void setUltimaConsultaAFIP(Date ultimaConsultaAFIP) {
        this.ultimaConsultaAFIP = ultimaConsultaAFIP;
    }

    public String getTipoIngresosBrutos() {
        return tipoIngresosBrutos;
    }

    public void setTipoIngresosBrutos(String tipoIngresosBrutos) {
        this.tipoIngresosBrutos = tipoIngresosBrutos;
    }

    public List<EntidadTransporte> getTransportes() {
        return transportes;
    }

    public void setTransportes(List<EntidadTransporte> transportes) {
        this.transportes = transportes;
    }

    public EntidadComercial getCuentaVinculada() {
        return cuentaVinculada;
    }

    public void setCuentaVinculada(EntidadComercial cuentaVinculada) {
        this.cuentaVinculada = cuentaVinculada;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<DireccionEntregaEntidad> getDireccionesDeEntrega() {
        return direccionesDeEntrega;
    }

    public void setDireccionesDeEntrega(List<DireccionEntregaEntidad> direccionesDeEntrega) {
        this.direccionesDeEntrega = direccionesDeEntrega;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public String getConDeuda() {
        return conDeuda;
    }

    public void setConDeuda(String conDeuda) {
        this.conDeuda = conDeuda;
    }

    public CondicionPagoProveedor getCondicionPagoProveedor() {
        return condicionPagoProveedor;
    }

    public void setCondicionPagoProveedor(CondicionPagoProveedor condicionPagoProveedor) {
        this.condicionPagoProveedor = condicionPagoProveedor;
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaCosto) {
        this.listaCosto = listaCosto;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public List<RetencionPorEntidad> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<RetencionPorEntidad> retenciones) {
        this.retenciones = retenciones;
    }

    public BigDecimal getPorcentajeRetencion() {
        return porcentajeRetencion;
    }

    public void setPorcentajeRetencion(BigDecimal porcentajeRetencion) {
        this.porcentajeRetencion = porcentajeRetencion;
    }

    public EntidadComercial getTransporte() {
        return transporte;
    }

    public void setTransporte(EntidadComercial transporte) {
        this.transporte = transporte;
    }

    public List<ImpuestoPorEntidad> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<ImpuestoPorEntidad> impuestos) {
        this.impuestos = impuestos;
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    public String getEntidadComodin() {
        return entidadComodin;
    }

    public void setEntidadComodin(String entidadComodin) {
        this.entidadComodin = entidadComodin;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public Origen getOrigen() {
        return origen;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public String getPideCodigoAutorizacion() {
        return pideCodigoAutorizacion;
    }

    public void setPideCodigoAutorizacion(String pideCodigoAutorizacion) {
        this.pideCodigoAutorizacion = pideCodigoAutorizacion;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccionFactura() {
        return direccionFactura;
    }

    public void setDireccionFactura(String direccionFactura) {
        this.direccionFactura = direccionFactura;
    }

    public Provincia getProvinciaFactura() {
        return provinciaFactura;
    }

    public void setProvinciaFactura(Provincia provinciaFactura) {
        this.provinciaFactura = provinciaFactura;
    }

    public Localidad getLocalidadFactura() {
        return localidadFactura;
    }

    public void setLocalidadFactura(Localidad localidadFactura) {
        this.localidadFactura = localidadFactura;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    public String getAlergia() {
        return alergia;
    }

    public void setAlergia(String alergia) {
        this.alergia = alergia;
    }

    public String getTratamientoMedico() {
        return tratamientoMedico;
    }

    public void setTratamientoMedico(String tratamientoMedico) {
        this.tratamientoMedico = tratamientoMedico;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public void setContactoEmergencia(String contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }

    public String getDatosExtras() {
        return datosExtras;
    }

    public void setDatosExtras(String datosExtras) {
        this.datosExtras = datosExtras;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMedioPublicitario() {
        return medioPublicitario;
    }

    public void setMedioPublicitario(String medioPublicitario) {
        this.medioPublicitario = medioPublicitario;
    }

    public String getEstudioRealizado() {
        return estudioRealizado;
    }

    public void setEstudioRealizado(String estudioRealizado) {
        this.estudioRealizado = estudioRealizado;
    }

    public String getNombreAsistenciaMedica() {
        return nombreAsistenciaMedica;
    }

    public void setNombreAsistenciaMedica(String nombreAsistenciaMedica) {
        this.nombreAsistenciaMedica = nombreAsistenciaMedica;
    }

    public String getTelefonoAsistenciaMedica() {
        return telefonoAsistenciaMedica;
    }

    public void setTelefonoAsistenciaMedica(String telefonoAsistenciaMedica) {
        this.telefonoAsistenciaMedica = telefonoAsistenciaMedica;
    }

    public String getNroAfiliadoAsistenciaMedica() {
        return nroAfiliadoAsistenciaMedica;
    }

    public void setNroAfiliadoAsistenciaMedica(String nroAfiliadoAsistenciaMedica) {
        this.nroAfiliadoAsistenciaMedica = nroAfiliadoAsistenciaMedica;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getFactorRh() {
        return factorRh;
    }

    public void setFactorRh(String factorRh) {
        this.factorRh = factorRh;
    }

    public String getDetalleAlergia() {
        return detalleAlergia;
    }

    public void setDetalleAlergia(String detalleAlergia) {
        this.detalleAlergia = detalleAlergia;
    }

    public String getDetalleTratamientoMedico() {
        return detalleTratamientoMedico;
    }

    public void setDetalleTratamientoMedico(String detalleTratamientoMedico) {
        this.detalleTratamientoMedico = detalleTratamientoMedico;
    }

    public String getTelefonoContactoEmergencia() {
        return telefonoContactoEmergencia;
    }

    public void setTelefonoContactoEmergencia(String telefonoContactoEmergencia) {
        this.telefonoContactoEmergencia = telefonoContactoEmergencia;
    }

    public List<ItemPendienteCuentaCorriente> getSaldos() {
        return saldos;
    }

    public void setSaldos(List<ItemPendienteCuentaCorriente> saldos) {
        this.saldos = saldos;
    }

    public double getImporteSaldo() {

        if (saldos != null) {
            return saldos.stream().mapToDouble(i -> i.getPendiente().doubleValue()).sum();
        } else {
            return 0;
        }
    }

    public double getImporteAplicar() {
        if (saldos != null) {
            return saldos.stream()
                    .filter(f -> f.getImporteAplicar() != null && f.isSeleccionado())
                    .mapToDouble(i -> i.getImporteAplicar().doubleValue()).sum();
        } else {
            return 0;
        }
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public String getNombreRazonComplete() {
        return (nrocta != null ? nrocta + " - " : "") + (razonSocial != null ? razonSocial : "");
    }

    public String getNombreComplete() {
        return (nrocta != null ? nrocta + " - " : "") + (apellido != null ? apellido + " " : "") + (nombre != null ? nombre : "");
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

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public List<EntidadActividadComercial> getActividades() {
        return actividades;
    }

    public void setActividades(List<EntidadActividadComercial> actividades) {
        this.actividades = actividades;
    }

    public List<EntidadCamion> getCamiones() {
        return camiones;
    }

    public void setCamiones(List<EntidadCamion> camiones) {
        this.camiones = camiones;
    }

    public List<EntidadChofer> getChoferes() {
        return choferes;
    }

    public void setChoferes(List<EntidadChofer> choferes) {
        this.choferes = choferes;
    }

    public int getCantidadTurnosDiarios() {
        return cantidadTurnosDiarios;
    }

    public void setCantidadTurnosDiarios(int cantidadTurnosDiarios) {
        this.cantidadTurnosDiarios = cantidadTurnosDiarios;
    }

    public List<EntidadHorario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<EntidadHorario> horarios) {
        this.horarios = horarios;
    }

    public Integer getDuracionTurno() {
        return duracionTurno;
    }

    public void setDuracionTurno(Integer duracionTurno) {
        this.duracionTurno = duracionTurno;
    }

    public String getNroMatricula() {
        return nroMatricula;
    }

    public void setNroMatricula(String nroMatricula) {
        this.nroMatricula = nroMatricula;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.nrocta != null ? this.nrocta.hashCode() : 0);
        return hash;
    }

    public List<EntidadObraSocial> getObraSociales() {
        return obraSociales;
    }

    public void setObraSociales(List<EntidadObraSocial> obraSociales) {
        this.obraSociales = obraSociales;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntidadComercial other = (EntidadComercial) obj;
        if ((this.nrocta == null) ? (other.nrocta != null) : !this.nrocta.equals(other.nrocta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntidadComercial{" + "nrocta=" + nrocta + '}';
    }

}
