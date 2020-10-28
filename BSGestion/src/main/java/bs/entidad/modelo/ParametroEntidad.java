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
import bs.ventas.modelo.LimiteCreditoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Claudio
 */
@Entity
@Table(name = "en_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroEntidad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "FMTNRC")
    private String formatoNroCuenta;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @JoinColumns({
        @JoinColumn(name = "CODCAT", referencedColumnName = "CODIGO"),
        @JoinColumn(name = "CODTIP", referencedColumnName = "CODTIP")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria;

    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumn(name = "CODZON", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Zona zona;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoEntidad estado;

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

    @Column(name = "CONTAD", length = 1)
    private String soloContado;

    @Column(name = "TIPING", length = 2)
    private String tipoIngresosBrutos;

    @Column(name = "VALDUP", length = 1)
    private String validaDuplicidad;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Column(name = "PIDECA", length = 1)
    private String pideCodigoAutorizacion;

    @JoinColumn(name = "CANVTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVenta;

    @Embedded
    private Auditoria auditoria;

    public ParametroEntidad() {

        this.soloContado = "N";
        this.auditoria = new Auditoria();
    }

    public ParametroEntidad(String id) {

        this.id = "1";
        this.soloContado = "N";
        this.auditoria = new Auditoria();
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

    public String getFormatoNroCuenta() {
        return formatoNroCuenta;
    }

    public void setFormatoNroCuenta(String formatoNroCuenta) {
        this.formatoNroCuenta = formatoNroCuenta;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public EstadoEntidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntidad estado) {
        this.estado = estado;
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

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
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

    public String getSoloContado() {
        return soloContado;
    }

    public void setSoloContado(String soloContado) {
        this.soloContado = soloContado;
    }

    public String getTipoIngresosBrutos() {
        return tipoIngresosBrutos;
    }

    public void setTipoIngresosBrutos(String tipoIngresosBrutos) {
        this.tipoIngresosBrutos = tipoIngresosBrutos;
    }

    public String getValidaDuplicidad() {
        return validaDuplicidad;
    }

    public void setValidaDuplicidad(String validaDuplicidad) {
        this.validaDuplicidad = validaDuplicidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public String getPideCodigoAutorizacion() {
        return pideCodigoAutorizacion;
    }

    public void setPideCodigoAutorizacion(String pideCodigoAutorizacion) {
        this.pideCodigoAutorizacion = pideCodigoAutorizacion;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
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
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ParametroEntidad other = (ParametroEntidad) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ParametroEntidad{" + "id=" + id + ", formatoNroCuenta=" + formatoNroCuenta + ", provincia=" + provincia + ", categoria=" + categoria + ", localidad=" + localidad + ", zona=" + zona + ", estado=" + estado + ", condicionDeIva=" + condicionDeIva + ", tipoDocumento=" + tipoDocumento + ", tipoDocumento1=" + tipoDocumento1 + ", tipoDocumento2=" + tipoDocumento2 + ", tipoDocumento3=" + tipoDocumento3 + ", nrodoc=" + nrodoc + ", vendedor=" + vendedor + ", cobrador=" + cobrador + ", comprador=" + comprador + ", condicionDePagoVentas=" + condicionDePagoVentas + ", listaDePrecioVenta=" + listaDePrecioVenta + ", limiteDeCreditoVenta=" + limiteDeCreditoVenta + ", condicionPagoProveedor=" + condicionPagoProveedor + ", listaCosto=" + listaCosto + ", soloContado=" + soloContado + ", tipoIngresosBrutos=" + tipoIngresosBrutos + ", validaDuplicidad=" + validaDuplicidad + ", unidadNegocio=" + unidadNegocio + ", pideCodigoAutorizacion=" + pideCodigoAutorizacion + ", auditoria=" + auditoria + '}';
    }

}
