/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.modelo.Auditoria;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Empresa;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.Periodo;
import bs.global.modelo.Provincia;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoDocumento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_prestamo")
@XmlRootElement
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "ESTADO", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoPrestamo estado;

    @Size(max = 30)
    @Column(name = "CODIGO", length = 30)
    private String codigo;

    //Fecha del movimiento
    @Column(name = "FCHENT")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;

    @Column(name = "FCHCRE")
    @Temporal(TemporalType.DATE)
    private Date fechaCredito;
//
//    @Column(name = "FCHVPC")
//    @Temporal(TemporalType.DATE)
//    private Date fechaVencimientoPrimeraCuota;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial destinatario;

    @Column(name = "DESTIN", nullable = false, length = 200)
    private String nombreDestinatario;

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    @Column(name = "DIRECC", length = 60)
    private String direccion;
//    @Column(name = "NUMERO", length = 20)
//    private String numero;
    @Column(name = "BARRIO", length = 6)
    private String barrio;

    //Codigo postal entrega
    @JoinColumn(name = "CODLOC", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumns({
        @JoinColumn(name = "CODPRV", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @Column(name = "IMPCAP", precision = 15, scale = 4)
    private double importeCapital;

    @Column(name = "IMPINT", precision = 15, scale = 4)
    private double importeInteres;

    @Column(name = "IMPMOR", precision = 15, scale = 4)
    private double importeMora;

    @Column(name = "IMPDES", precision = 15, scale = 4)
    private double importeDescuento;

    @Column(name = "IMPGOT", precision = 15, scale = 4)
    private double gastosOtorgamiento;

    @Column(name = "IMPMCS", precision = 15, scale = 4)
    private double importeMicroseguros;

    @Column(name = "IMPTOT", precision = 15, scale = 4)
    private double importeTotal;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @Lob
    @Size(max = 2147483647)
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;
    @Size(max = 100)
    @Column(name = "REFERN", length = 100)
    private String referencia;

    @Size(max = 1)
    @Column(name = "REPROG", length = 1)
    private String reprogramado;

    @OneToMany(mappedBy = "prestamo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("cuota ASC")
    private List<ItemPrestamo> itemsPrestamo;

    @JoinColumn(name = "CODPRO", referencedColumnName = "ID")
    @ManyToOne
    private Promotor promotor;

    @JoinColumn(name = "CODFIN", referencedColumnName = "ID")
    @ManyToOne
    private Financiador financiador;

    @JoinColumn(name = "CODLIN", referencedColumnName = "ID")
    @ManyToOne
    private LineaCredito lineaCredito;

    @JoinColumn(name = "CODAMO", referencedColumnName = "CODIGO")
    @ManyToOne
    private AmortizacionPrestamo amortizacion;

    @JoinColumn(name = "CLAS01", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion01 clasificacion01;

    @JoinColumn(name = "CLAS02", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion02 clasificacion02;

    @JoinColumn(name = "CLAS03", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion03 clasificacion03;

    @JoinColumn(name = "CLAS04", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion04 clasificacion04;

    @JoinColumn(name = "CLAS05", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion05 clasificacion05;

    @Column(name = "CNTCUO")
    private Integer cantidadCuotas;

    @Column(name = "PERGRA")
    private Integer periodosGracia;

    @JoinColumn(name = "PERIOD", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Periodo periodo;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "ID_PRR", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Prestamo prestamoReprogramado;

    @Embedded
    private Auditoria auditoria;

    @Transient
    boolean vacioCuotas;

    public Prestamo() {

        itemsPrestamo = new ArrayList<ItemPrestamo>();

        importeCapital = 0;
        importeInteres = 0;
        importeDescuento = 0;
        gastosOtorgamiento = 0;
        importeMora = 0;
        importeTotal = 0;
        fechaCredito = new Date();
        fechaEntrega = new Date();
//        fechaVencimientoPrimeraCuota = new Date();
        periodosGracia = 0;
        reprogramado = "N";

        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public Date getFechaCredito() {
        return fechaCredito;
    }

    public void setFechaCredito(Date fechaCredito) {
        this.fechaCredito = fechaCredito;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public double getImporteCapital() {
        return importeCapital;
    }

    public void setImporteCapital(double importeCapital) {
        this.importeCapital = importeCapital;
    }

    public double getImporteInteres() {
        return importeInteres;
    }

    public void setImporteInteres(double importeInteres) {
        this.importeInteres = importeInteres;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<ItemPrestamo> getItemsPrestamo() {
        return itemsPrestamo;
    }

    public void setItemsPrestamo(List<ItemPrestamo> itemsPrestamo) {
        this.itemsPrestamo = itemsPrestamo;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public void setPromotor(Promotor promotor) {
        this.promotor = promotor;
    }

    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador financiador) {
        this.financiador = financiador;
    }

    public LineaCredito getLineaCredito() {
        return lineaCredito;
    }

    public void setLineaCredito(LineaCredito lineaCredito) {
        this.lineaCredito = lineaCredito;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public AmortizacionPrestamo getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(AmortizacionPrestamo amortizacion) {
        this.amortizacion = amortizacion;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public Clasificacion01 getClasificacion01() {
        return clasificacion01;
    }

    public void setClasificacion01(Clasificacion01 clasificacion01) {
        this.clasificacion01 = clasificacion01;
    }

    public Clasificacion02 getClasificacion02() {
        return clasificacion02;
    }

    public void setClasificacion02(Clasificacion02 clasificacion02) {
        this.clasificacion02 = clasificacion02;
    }

    public Clasificacion03 getClasificacion03() {
        return clasificacion03;
    }

    public void setClasificacion03(Clasificacion03 clasificacion03) {
        this.clasificacion03 = clasificacion03;
    }

    public Clasificacion04 getClasificacion04() {
        return clasificacion04;
    }

    public void setClasificacion04(Clasificacion04 clasificacion04) {
        this.clasificacion04 = clasificacion04;
    }

    public Clasificacion05 getClasificacion05() {
        return clasificacion05;
    }

    public void setClasificacion05(Clasificacion05 clasificacion05) {
        this.clasificacion05 = clasificacion05;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
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

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
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

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public double getImporteMora() {
        return importeMora;
    }

    public void setImporteMora(double importeMora) {
        this.importeMora = importeMora;
    }

    public double getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(double importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public boolean isVacioCuotas() {
        return vacioCuotas;
    }

    public void setVacioCuotas(boolean vacioCuotas) {
        this.vacioCuotas = vacioCuotas;
    }

    public double getGastosOtorgamiento() {
        return gastosOtorgamiento;
    }

    public void setGastosOtorgamiento(double gastosOtorgamiento) {
        this.gastosOtorgamiento = gastosOtorgamiento;
    }
//
//    public Date getFechaVencimientoPrimeraCuota() {
//        return fechaVencimientoPrimeraCuota;
//    }
//
//    public void setFechaVencimientoPrimeraCuota(Date fechaVencimientoPrimeraCuota) {
//        this.fechaVencimientoPrimeraCuota = fechaVencimientoPrimeraCuota;
//    }

    public Integer getPeriodosGracia() {
        return periodosGracia;
    }

    public void setPeriodosGracia(Integer periodosGracia) {
        this.periodosGracia = periodosGracia;
    }

    public Prestamo getPrestamoReprogramado() {
        return prestamoReprogramado;
    }

    public void setPrestamoReprogramado(Prestamo prestamoReprogramado) {
        this.prestamoReprogramado = prestamoReprogramado;
    }

    public String getReprogramado() {
        return reprogramado;
    }

    public void setReprogramado(String reprogramado) {
        this.reprogramado = reprogramado;
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

    public double getImporteMicroseguros() {
        return importeMicroseguros;
    }

    public void setImporteMicroseguros(double importeMicroseguros) {
        this.importeMicroseguros = importeMicroseguros;
    }

    @Override
    public Prestamo clone() throws CloneNotSupportedException {
        return (Prestamo) super.clone();
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
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.presupuesto.modelo.MovimientoPrestamo[ id=" + id + " ]";
    }

}
