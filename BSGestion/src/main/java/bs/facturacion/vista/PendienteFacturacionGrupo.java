/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.vista;

import bs.educacion.modelo.MovimientoEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoDocumento;
import bs.stock.modelo.Deposito;
import bs.taller.modelo.MovimientoTaller;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "fc_pendiente_grupo")
@IdClass(PendienteFacturacionGrupoPK.class)
public class PendienteFacturacionGrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CIRCOM", length = 2)
    private String circom;
    @Id
    @Column(name = "CANVTA", length = 6)
    private String canvta;
    @Id
    @Column(name = "CODSUC", length = 6)
    private String codsuc;
    @Id
    @Column(name = "NROCTA")
    private String nrocta;
    @Id
    @Column(name = "RAZONS")
    private String razonSocial;
    @Id
    @Column(name = "NRODOC", length = 50)
    private String nrodoc;
    @Id
    @Column(name = "CNDPAG", length = 6)
    private String cndpag;
    @Id
    @Column(name = "VNDDOR", length = 6)
    private String vnddor;
    @Id
    @Column(name = "DEPOSI", length = 6)
    private String deping;
    @Id
    @Column(name = "DEPTRA", length = 6)
    private String depegr;
    @Id
    @Column(name = "CODLIS", length = 6)
    private String codlis;
    @Id
    @Column(name = "DIRECC", length = 60)
    private String direccion;
    @Id
    @Column(name = "CODPOS", length = 6)
    private String codpos;
    @Id
    @Column(name = "CODTRA", length = 6)
    private String codtra;
    @Id
    @Column(name = "MONREG", length = 6)
    private String monreg;
    @Id
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "MODULO", length = 2)
    private String modulo;

    @Column(name = "PENDIENTE", nullable = false)
    private long pendiente;

    @JoinColumn(name = "CANVTA", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVenta;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private EntidadComercial cliente;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePago;

    //VT_Vendedor
    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;

    //VT_Vendedor
    @JoinColumn(name = "REPDOR", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Repartidor repartidor;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;

    //Codigo postal entrega
    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    //Transportista
    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MTL", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    MovimientoTaller movimientoTaller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MED", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    MovimientoEducacion movimientoEducacion;

    public PendienteFacturacionGrupo() {

    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public CondicionDePagoVenta getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionDePagoVenta condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public long getPendiente() {
        return pendiente;
    }

    public void setPendiente(long pendiente) {
        this.pendiente = pendiente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
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

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public EntidadComercial getTransporte() {
        return transporte;
    }

    public void setTransporte(EntidadComercial transporte) {
        this.transporte = transporte;
    }

    public String getVnddor() {
        return vnddor;
    }

    public void setVnddor(String vnddor) {
        this.vnddor = vnddor;
    }

    public String getDeping() {
        return deping;
    }

    public void setDeping(String deping) {
        this.deping = deping;
    }

    public String getDepegr() {
        return depegr;
    }

    public void setDepegr(String depegr) {
        this.depegr = depegr;
    }

    public String getCodlis() {
        return codlis;
    }

    public void setCodlis(String codlis) {
        this.codlis = codlis;
    }

    public String getCodpos() {
        return codpos;
    }

    public void setCodpos(String codpos) {
        this.codpos = codpos;
    }

    public String getCodtra() {
        return codtra;
    }

    public void setCodtra(String codtra) {
        this.codtra = codtra;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public String getMonreg() {
        return monreg;
    }

    public void setMonreg(String monreg) {
        this.monreg = monreg;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public MovimientoTaller getMovimientoTaller() {
        return movimientoTaller;
    }

    public void setMovimientoTaller(MovimientoTaller movimientoTaller) {
        this.movimientoTaller = movimientoTaller;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public MovimientoEducacion getMovimientoEducacion() {
        return movimientoEducacion;
    }

    public void setMovimientoEducacion(MovimientoEducacion movimientoEducacion) {
        this.movimientoEducacion = movimientoEducacion;
    }

    public String getCanvta() {
        return canvta;
    }

    public void setCanvta(String canvta) {
        this.canvta = canvta;
    }

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public String getCodsuc() {
        return codsuc;
    }

    public void setCodsuc(String codsuc) {
        this.codsuc = codsuc;
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
        hash = 71 * hash + Objects.hashCode(this.circom);
        hash = 71 * hash + Objects.hashCode(this.codsuc);
        hash = 71 * hash + Objects.hashCode(this.nrocta);
        hash = 71 * hash + Objects.hashCode(this.razonSocial);
        hash = 71 * hash + Objects.hashCode(this.nrodoc);
        hash = 71 * hash + Objects.hashCode(this.cndpag);
        hash = 71 * hash + Objects.hashCode(this.vnddor);
        hash = 71 * hash + Objects.hashCode(this.deping);
        hash = 71 * hash + Objects.hashCode(this.depegr);
        hash = 71 * hash + Objects.hashCode(this.codlis);
        hash = 71 * hash + Objects.hashCode(this.direccion);
        hash = 71 * hash + Objects.hashCode(this.codpos);
        hash = 71 * hash + Objects.hashCode(this.codtra);
        hash = 71 * hash + Objects.hashCode(this.monreg);
        hash = 71 * hash + Objects.hashCode(this.canvta);
        hash = 71 * hash + Objects.hashCode(this.cotizacion);
        hash = 71 * hash + Objects.hashCode(this.modulo);
        hash = 71 * hash + (int) (this.pendiente ^ (this.pendiente >>> 32));
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
        final PendienteFacturacionGrupo other = (PendienteFacturacionGrupo) obj;
        if (this.pendiente != other.pendiente) {
            return false;
        }
        if (!Objects.equals(this.circom, other.circom)) {
            return false;
        }
        if (!Objects.equals(this.nrocta, other.nrocta)) {
            return false;
        }
        if (!Objects.equals(this.razonSocial, other.razonSocial)) {
            return false;
        }
        if (!Objects.equals(this.nrodoc, other.nrodoc)) {
            return false;
        }
        if (!Objects.equals(this.cndpag, other.cndpag)) {
            return false;
        }
        if (!Objects.equals(this.codsuc, other.codsuc)) {
            return false;
        }
        if (!Objects.equals(this.vnddor, other.vnddor)) {
            return false;
        }
        if (!Objects.equals(this.deping, other.deping)) {
            return false;
        }
        if (!Objects.equals(this.depegr, other.depegr)) {
            return false;
        }
        if (!Objects.equals(this.codlis, other.codlis)) {
            return false;
        }
        if (!Objects.equals(this.direccion, other.direccion)) {
            return false;
        }
        if (!Objects.equals(this.codpos, other.codpos)) {
            return false;
        }
        if (!Objects.equals(this.codtra, other.codtra)) {
            return false;
        }
        if (!Objects.equals(this.monreg, other.monreg)) {
            return false;
        }
        if (!Objects.equals(this.canvta, other.canvta)) {
            return false;
        }
        if (!Objects.equals(this.modulo, other.modulo)) {
            return false;
        }
        if (!Objects.equals(this.cotizacion, other.cotizacion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteFacturacionGrupo{" + "circom=" + circom + ", codsuc=" + codsuc + ", nrocta=" + nrocta + ", razonSocial=" + razonSocial + ", nrodoc=" + nrodoc + ", cndpag=" + cndpag + ", vnddor=" + vnddor + ", deping=" + deping + ", depegr=" + depegr + ", codlis=" + codlis + ", direccion=" + direccion + ", codpos=" + codpos + ", codtra=" + codtra + ", monreg=" + monreg + ", canvta=" + canvta + ", cotizacion=" + cotizacion + ", modulo=" + modulo + ", pendiente=" + pendiente + '}';
    }

}
