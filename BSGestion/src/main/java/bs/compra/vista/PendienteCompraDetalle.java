/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.vista;

import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.global.auditoria.AuditoriaListener;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "co_pendiente_detalle")
@IdClass(PendienteCompraDetallePK.class)
@EntityListeners(AuditoriaListener.class)
public class PendienteCompraDetalle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_IAPL")
    private Integer idItemAplicacion;
    @Id
    @Column(name = "ID_MAPL")
    private Integer idMovimientoAplicacion;

    @Column(name = "CIRCOM", length = 2)
    private String circom;

    @Column(name = "CIRAPL", length = 6)
    private String cirapl;

    @Column(name = "NROCTA")
    private String nrocta;

    @Column(name = "CODFOR")
    private String codfor;

    @Column(name = "NROFOR")
    private Integer nrofor;

    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    //Unidad de medida
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "DESCRP")
    private String descripcion;

    @Column(name = "FCHENT")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;

    @Column(name = "OBSERV")
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    MovimientoCompra movimientoAplicacion;

    @Column(name = "PENDIENTE", precision = 15, scale = 2)
    private double cantidad;

    @Column(name = "PRECIO", precision = 15, scale = 2)
    private BigDecimal precio;

    @Column(name = "PRESEC", precision = 15, scale = 2)
    private BigDecimal precioSecundario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    ItemMovimientoCompra itemProducto;

    @Transient
    private boolean seleccionado;

    @Transient
    private double cantidadSeleccionada;

    public PendienteCompraDetalle() {

    }

    public Integer getIdItemAplicacion() {
        return idItemAplicacion;
    }

    public void setIdItemAplicacion(Integer idItemAplicacion) {
        this.idItemAplicacion = idItemAplicacion;
    }

    public Integer getIdMovimientoAplicacion() {
        return idMovimientoAplicacion;
    }

    public void setIdMovimientoAplicacion(Integer idMovimientoAplicacion) {
        this.idMovimientoAplicacion = idMovimientoAplicacion;
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getCirapl() {
        return cirapl;
    }

    public void setCirapl(String cirapl) {
        this.cirapl = cirapl;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public Integer getNrofor() {
        return nrofor;
    }

    public void setNrofor(Integer nrofor) {
        this.nrofor = nrofor;
    }

    public Date getFchmov() {
        return fechaMovimiento;
    }

    public void setFchmov(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public MovimientoCompra getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoCompra movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public BigDecimal getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(BigDecimal precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public ItemMovimientoCompra getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(ItemMovimientoCompra itemProducto) {
        this.itemProducto = itemProducto;
    }

    public double getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    public void setCantidadSeleccionada(double cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.idItemAplicacion != null ? this.idItemAplicacion.hashCode() : 0);
        hash = 53 * hash + (this.idMovimientoAplicacion != null ? this.idMovimientoAplicacion.hashCode() : 0);
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
        final PendienteCompraDetalle other = (PendienteCompraDetalle) obj;
        if (this.idItemAplicacion != other.idItemAplicacion && (this.idItemAplicacion == null || !this.idItemAplicacion.equals(other.idItemAplicacion))) {
            return false;
        }
        if (this.idMovimientoAplicacion != other.idMovimientoAplicacion && (this.idMovimientoAplicacion == null || !this.idMovimientoAplicacion.equals(other.idMovimientoAplicacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemPendienteFacturacion{" + "idItemAplicacion=" + idItemAplicacion + ", idMovimientoAplicacion=" + idMovimientoAplicacion + '}';
    }

}
