/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import bs.global.modelo.TipoDocumento;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cj_movimiento_item")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoTesoreria implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MovimientoTesoreria movimiento;

    @Column(name = "NROITM", nullable = false)
    private int nroItem;

    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;

    @JoinColumn(name = "NROENT", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial entidadComercial;

    @Column(name = "NOMENT", length = 120)
    private String nombreEntidad;

    @Column(name = "NROINT")
    private Integer numeroInterno;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO"),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT"),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @Column(name = "CHEQUE", length = 30)
    private String cheque;

    @Column(name = "CATEGO", length = 1)
    private String categoriaCheque;

    @Column(name = "CHESUC")
    private Short chequeSucursal;

    @Column(name = "ESTCHE")
    private Character estadoCheque;

    @Column(name = "FCHCHE")
    @Temporal(TemporalType.DATE)
    private Date fechaCheque;

    @Column(name = "CODCLA", length = 6)
    private String clasificacionCheque;

    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "FCHAUX")
    @Temporal(TemporalType.DATE)
    private Date fechaAuxiliar;

    @JoinColumn(name = "CODBCO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Banco banco;

    @Column(name = "DOCFIR", length = 120)
    private String firmanteDocumento;

    @Column(name = "CLRING")
    private Short clearing;

    @JoinColumn(name = "NROCTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaTesoreria cuentaTesoreria;

    @Column(name = "INDICA", length = 1)
    private String Indica;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "IMPDEB", precision = 18, scale = 2)
    private BigDecimal importeDebe;
    @Column(name = "IMPHAB", precision = 18, scale = 2)
    private BigDecimal importeHaber;

    @Column(name = "IMPORT", precision = 15, scale = 4)
    private BigDecimal importe;

    @Column(name = "PORCOM", precision = 15, scale = 4)
    private double porcentajeComision;

    @Column(name = "IMPCOM", precision = 15, scale = 4)
    private double importeComision;

    @JoinColumn(name = "COFSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private BigDecimal importeMonedaSecundaria;
    @Column(name = "SECDEB", precision = 15, scale = 4)
    private BigDecimal importeDebeMonedaSecundaria;
    @Column(name = "SECHAB", precision = 15, scale = 4)
    private BigDecimal importeHaberMonedaSecundaria;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String numeroDocumento;

    @Column(name = "CTABAN", length = 20)
    private String numeroCuentaBancaria;

    @Column(name = "NROLEG", length = 6)
    private String numeroLegajo;

    @Column(name = "AUTPOS", length = 30)
    private String codigoAutorizacionTarjeta;
    @Column(name = "CUOTAS")
    private Short cuotasTarjeta;
    @Column(name = "PAGADO")
    private String pagado;
    @Column(name = "NROLOT")
    private Integer numeroLoteTarjeta;
    @Column(name = "PRITAR")
    private Integer primeroSeisDigitoTarjeta;
    @Column(name = "ULTTAR")
    private Integer ultimosCuatroDigitoTarjeta;
    @Column(name = "TERMID", length = 25)
    private String terminalTarjeta;

    @JoinColumn(name = "CODTAR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private TarjetaCredito tarjetaCredito;

    @OneToMany(mappedBy = "itemTesoreria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoTesoreriaCentroCosto> itemsCentroCosto;

    @Transient
    private BigDecimal saldo;

    @Transient
    private BigDecimal saldoMonedaSecundaria;

    @Transient
    private boolean imputaCentroCosto;

    @Transient
    private BigDecimal importeImputadoCentroCosto;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    @Transient
    private boolean seleccionado;

    public ItemMovimientoTesoreria() {

        this.auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;
        this.importe = BigDecimal.ZERO;
        this.importeDebe = BigDecimal.ZERO;
        this.importeHaber = BigDecimal.ZERO;
        this.importeMonedaSecundaria = BigDecimal.ZERO;
        this.importeDebeMonedaSecundaria = BigDecimal.ZERO;
        this.importeHaberMonedaSecundaria = BigDecimal.ZERO;
        this.cantidad = BigDecimal.ONE;

        this.fechaAuxiliar = new Date();
        this.itemsCentroCosto = new ArrayList<ItemMovimientoTesoreriaCentroCosto>();
//        this.fechaCheque = new Date();
//        this.fechaVencimiento = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoTesoreria getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoTesoreria movimiento) {
        this.movimiento = movimiento;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public int getNroItem() {
        return nroItem;
    }

    public void setNroItem(int nroItem) {
        this.nroItem = nroItem;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }

    public Integer getNumeroInterno() {
        return numeroInterno;
    }

    public void setNumeroInterno(Integer numeroInterno) {
        this.numeroInterno = numeroInterno;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public String getCategoriaCheque() {
        return categoriaCheque;
    }

    public void setCategoriaCheque(String categoriaCheque) {
        this.categoriaCheque = categoriaCheque;
    }

    public Short getChequeSucursal() {
        return chequeSucursal;
    }

    public void setChequeSucursal(Short chequeSucursal) {
        this.chequeSucursal = chequeSucursal;
    }

    public Character getEstadoCheque() {
        return estadoCheque;
    }

    public void setEstadoCheque(Character estadoCheque) {
        this.estadoCheque = estadoCheque;
    }

    public Date getFechaCheque() {
        return fechaCheque;
    }

    public void setFechaCheque(Date fechaCheque) {
        this.fechaCheque = fechaCheque;
    }

    public String getClasificacionCheque() {
        return clasificacionCheque;
    }

    public void setClasificacionCheque(String clasificacionCheque) {
        this.clasificacionCheque = clasificacionCheque;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaAuxiliar() {
        return fechaAuxiliar;
    }

    public void setFechaAuxiliar(Date fechaAuxiliar) {
        this.fechaAuxiliar = fechaAuxiliar;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getFirmanteDocumento() {
        return firmanteDocumento;
    }

    public void setFirmanteDocumento(String firmanteDocumento) {
        this.firmanteDocumento = firmanteDocumento;
    }

    public Short getClearing() {
        return clearing;
    }

    public void setClearing(Short clearing) {
        this.clearing = clearing;
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public String getIndica() {
        return Indica;
    }

    public void setIndica(String Indica) {
        this.Indica = Indica;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getImporteDebe() {
        return importeDebe;
    }

    public void setImporteDebe(BigDecimal importeDebe) {
        this.importeDebe = importeDebe;
    }

    public BigDecimal getImporteHaber() {
        return importeHaber;
    }

    public void setImporteHaber(BigDecimal importeHaber) {
        this.importeHaber = importeHaber;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }

    public BigDecimal getImporteMonedaSecundaria() {
        return importeMonedaSecundaria;
    }

    public void setImporteMonedaSecundaria(BigDecimal importeMonedaSecundaria) {
        this.importeMonedaSecundaria = importeMonedaSecundaria;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public BigDecimal getImporteDebeMonedaSecundaria() {
        return importeDebeMonedaSecundaria;
    }

    public void setImporteDebeMonedaSecundaria(BigDecimal importeDebeMonedaSecundaria) {
        this.importeDebeMonedaSecundaria = importeDebeMonedaSecundaria;
    }

    public BigDecimal getImporteHaberMonedaSecundaria() {
        return importeHaberMonedaSecundaria;
    }

    public void setImporteHaberMonedaSecundaria(BigDecimal importeHaberMonedaSecundaria) {
        this.importeHaberMonedaSecundaria = importeHaberMonedaSecundaria;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNumeroCuentaBancaria() {
        return numeroCuentaBancaria;
    }

    public void setNumeroCuentaBancaria(String numeroCuentaBancaria) {
        this.numeroCuentaBancaria = numeroCuentaBancaria;
    }

    public String getNumeroLegajo() {
        return numeroLegajo;
    }

    public void setNumeroLegajo(String numeroLegajo) {
        this.numeroLegajo = numeroLegajo;
    }

    public String getCodigoAutorizacionTarjeta() {
        return codigoAutorizacionTarjeta;
    }

    public void setCodigoAutorizacionTarjeta(String codigoAutorizacionTarjeta) {
        this.codigoAutorizacionTarjeta = codigoAutorizacionTarjeta;
    }

    public Short getCuotasTarjeta() {
        return cuotasTarjeta;
    }

    public void setCuotasTarjeta(Short cuotasTarjeta) {
        this.cuotasTarjeta = cuotasTarjeta;
    }

    public Integer getNumeroLoteTarjeta() {
        return numeroLoteTarjeta;
    }

    public void setNumeroLoteTarjeta(Integer numeroLoteTarjeta) {
        this.numeroLoteTarjeta = numeroLoteTarjeta;
    }

    public Integer getPrimeroSeisDigitoTarjeta() {
        return primeroSeisDigitoTarjeta;
    }

    public void setPrimeroSeisDigitoTarjeta(Integer primeroSeisDigitoTarjeta) {
        this.primeroSeisDigitoTarjeta = primeroSeisDigitoTarjeta;
    }

    public Integer getUltimosCuatroDigitoTarjeta() {
        return ultimosCuatroDigitoTarjeta;
    }

    public void setUltimosCuatroDigitoTarjeta(Integer ultimosCuatroDigitoTarjeta) {
        this.ultimosCuatroDigitoTarjeta = ultimosCuatroDigitoTarjeta;
    }

    public String getTerminalTarjeta() {
        return terminalTarjeta;
    }

    public void setTerminalTarjeta(String terminalTarjeta) {
        this.terminalTarjeta = terminalTarjeta;
    }

    public TarjetaCredito getTarjetaCredito() {
        return tarjetaCredito;
    }

    public void setTarjetaCredito(TarjetaCredito tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getSaldoMonedaSecundaria() {
        return saldoMonedaSecundaria;
    }

    public void setSaldoMonedaSecundaria(BigDecimal saldoMonedaSecundaria) {
        this.saldoMonedaSecundaria = saldoMonedaSecundaria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public List<ItemMovimientoTesoreriaCentroCosto> getItemsCentroCosto() {
        return itemsCentroCosto;
    }

    public void setItemsCentroCosto(List<ItemMovimientoTesoreriaCentroCosto> itemsCentroCosto) {
        this.itemsCentroCosto = itemsCentroCosto;
    }

    public boolean isImputaCentroCosto() {
        return imputaCentroCosto;
    }

    public void setImputaCentroCosto(boolean imputaCentroCosto) {
        this.imputaCentroCosto = imputaCentroCosto;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public BigDecimal getImporteImputadoCentroCosto() {
        return importeImputadoCentroCosto;
    }

    public void setImporteImputadoCentroCosto(BigDecimal importeImputadoCentroCosto) {
        this.importeImputadoCentroCosto = importeImputadoCentroCosto;
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(double porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }

    public double getImporteComision() {
        return importeComision;
    }

    public void setImporteComision(double importeComision) {
        this.importeComision = importeComision;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoTesoreria other = (ItemMovimientoTesoreria) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoTesoreria{" + "id=" + id + '}';
    }

}
