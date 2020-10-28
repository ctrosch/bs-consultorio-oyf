/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.vista;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.TipoDocumento;
import bs.proveedores.modelo.Comprador;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ListaCosto;
import bs.stock.modelo.Deposito;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Table(name = "co_pendiente_grupo")
@IdClass(PendienteCompraGrupoPK.class)
@EntityListeners(AuditoriaListener.class)
public class PendienteCompraGrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CIRCOM", length = 2)
    private String circom;
    @Id
    @Column(name = "NROCTA")
    private String nrocta;
    @Id
    @Column(name = "NRODOC", length = 50)
    private String nrodoc;
    @Id
    @Column(name = "CNDPAG", length = 6)
    private String cndpag;
    @Id
    @Column(name = "COMDOR", length = 6)
    private String comdor;

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
    @Column(name = "CODLOC", length = 6)
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

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private EntidadComercial proveedor;

    @Column(name = "RAZONS")
    private String razonSocial;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionPagoProveedor condicionDePago;

    //VT_Vendedor
    @JoinColumn(name = "COMDOR", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprador comprador;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaCosto listaDePrecio;

    //Codigo postal entrega
    @JoinColumn(name = "CODLOC", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    //Transportista
    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    public PendienteCompraGrupo() {

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

    public String getComdor() {
        return comdor;
    }

    public void setComdor(String comdor) {
        this.comdor = comdor;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public CondicionPagoProveedor getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionPagoProveedor condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public ListaCosto getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaCosto listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public long getPendiente() {
        return pendiente;
    }

    public void setPendiente(long pendiente) {
        this.pendiente = pendiente;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 43 * hash + (this.nrocta != null ? this.nrocta.hashCode() : 0);
        hash = 43 * hash + (this.razonSocial != null ? this.razonSocial.hashCode() : 0);
        hash = 43 * hash + (this.nrodoc != null ? this.nrodoc.hashCode() : 0);
        hash = 43 * hash + (this.cndpag != null ? this.cndpag.hashCode() : 0);
        hash = 43 * hash + (this.comdor != null ? this.comdor.hashCode() : 0);
        hash = 43 * hash + (this.deping != null ? this.deping.hashCode() : 0);
        hash = 43 * hash + (this.depegr != null ? this.depegr.hashCode() : 0);
        hash = 43 * hash + (this.codlis != null ? this.codlis.hashCode() : 0);
        hash = 43 * hash + (this.direccion != null ? this.direccion.hashCode() : 0);
        hash = 43 * hash + (this.codpos != null ? this.codpos.hashCode() : 0);
        hash = 43 * hash + (this.codtra != null ? this.codtra.hashCode() : 0);
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
        final PendienteCompraGrupo other = (PendienteCompraGrupo) obj;
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.nrocta == null) ? (other.nrocta != null) : !this.nrocta.equals(other.nrocta)) {
            return false;
        }
        if ((this.razonSocial == null) ? (other.razonSocial != null) : !this.razonSocial.equals(other.razonSocial)) {
            return false;
        }
        if ((this.nrodoc == null) ? (other.nrodoc != null) : !this.nrodoc.equals(other.nrodoc)) {
            return false;
        }
        if ((this.cndpag == null) ? (other.cndpag != null) : !this.cndpag.equals(other.cndpag)) {
            return false;
        }
        if ((this.comdor == null) ? (other.comdor != null) : !this.comdor.equals(other.comdor)) {
            return false;
        }
        if ((this.deping == null) ? (other.deping != null) : !this.deping.equals(other.deping)) {
            return false;
        }
        if ((this.depegr == null) ? (other.depegr != null) : !this.depegr.equals(other.depegr)) {
            return false;
        }
        if ((this.codlis == null) ? (other.codlis != null) : !this.codlis.equals(other.codlis)) {
            return false;
        }
        if ((this.direccion == null) ? (other.direccion != null) : !this.direccion.equals(other.direccion)) {
            return false;
        }
        if ((this.codpos == null) ? (other.codpos != null) : !this.codpos.equals(other.codpos)) {
            return false;
        }
        if ((this.codtra == null) ? (other.codtra != null) : !this.codtra.equals(other.codtra)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteCompraGrupo{" + "circom=" + circom + ", nrocta=" + nrocta + ", cndpag=" + cndpag + ", vnddor=" + comdor + ", deping=" + deping + ", depegr=" + depegr + ", codlis=" + codlis + ", codpos=" + codpos + ", codtra=" + codtra + '}';
    }

}
