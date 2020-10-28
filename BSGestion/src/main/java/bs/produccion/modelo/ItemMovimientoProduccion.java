/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Formula;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "pd_movimiento_item")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "tipitm", discriminatorType = DiscriminatorType.STRING, length = 1)
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoProduccion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM")
    private int nroitm;

    @Column(name = "TIPITM")
    private String tipitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "id", nullable = false)
    private MovimientoProduccion movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MORI", referencedColumnName = "id", nullable = false)
    private MovimientoProduccion movimientoOriginal;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemProducto", fetch = FetchType.LAZY)
    private List<ItemDetalleMovimientoProduccion> itemDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "id")
    private ItemMovimientoProduccion itemAplicado;

    @OneToMany(mappedBy = "itemAplicado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoProduccion> itemsAplicacion;

    @Column(name = "ITMORI")
    private Integer idItemOriginal;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @JoinColumn(name = "ARTORI", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoOriginal;

    @JoinColumn(name = "ARTSUS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoSustituto;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(optional = false)
    private Deposito deposito;

    @JoinColumn(name = "CODFOR", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(optional = false)
    private Formula formula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "ARTCOD", referencedColumnName = "ARTCOD", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    })
    private ComposicionFormula composicionFormula;

    @JoinColumn(name = "OPERAR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private Operario operario;

    @Column(name = "CANTID", precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "CANTST", precision = 10, scale = 2)
    private BigDecimal cantidadStock;

    @Column(name = "CNTORI", precision = 10, scale = 2)
    private BigDecimal cantidadOriginal;

    @Column(name = "CNTPEN", precision = 10, scale = 2)
    private BigDecimal cantidadPendiente;

    @Column(name = "CNTNOM", precision = 10, scale = 2)
    private BigDecimal cantidadNominal;

    @Column(name = "CNTSEC", precision = 10, scale = 2)
    private BigDecimal cantidadSecundaria;

    @Column(name = "GRUPO", length = 20)
    private String grupo;

    @ManyToOne
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(name = "PRECIO", precision = 4, scale = 6)
    private BigDecimal precio;

    @Column(name = "FCHENT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEntrega;

    @Column(name = "FCHHAS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHasta;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "STOCKS", length = 1)
    private String actualizaStock;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private BigDecimal pendiente;

    @Transient
    private BigDecimal produccion;

    @Transient
    private boolean conError;

    @Transient
    private boolean todoOk;

    @Transient
    private String atributo1;
    @Transient
    private String atributo2;
    @Transient
    private String atributo3;
    @Transient
    private String atributo4;
    @Transient
    private String atributo5;
    @Transient
    private String atributo6;
    @Transient
    private String atributo7;

    @Transient
    private List<ItemDetalleMovimientoProduccion> itemDetalleTemporal;

    public ItemMovimientoProduccion() {

        cantidad = BigDecimal.ZERO;
        cantidadStock = BigDecimal.ZERO;
        cantidadOriginal = BigDecimal.ZERO;
        pendiente = BigDecimal.ZERO;
        produccion = BigDecimal.ZERO;
        precio = BigDecimal.ZERO;
        auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public MovimientoProduccion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoProduccion movimiento) {
        this.movimiento = movimiento;
    }

    public MovimientoProduccion getMovimientoOriginal() {
        return movimientoOriginal;
    }

    public void setMovimientoOriginal(MovimientoProduccion movimientoOriginal) {
        this.movimientoOriginal = movimientoOriginal;
    }

    public Integer getIdItemOriginal() {
        return idItemOriginal;
    }

    public void setIdItemOriginal(Integer idItemOriginal) {
        this.idItemOriginal = idItemOriginal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Producto getProductoOriginal() {
        return productoOriginal;
    }

    public void setProductoOriginal(Producto productoOriginal) {
        this.productoOriginal = productoOriginal;
    }

    public Producto getProductoSustituto() {
        return productoSustituto;
    }

    public void setProductoSustituto(Producto productoSustituto) {
        this.productoSustituto = productoSustituto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario operario) {
        this.operario = operario;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(BigDecimal cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public BigDecimal getCantidadPendiente() {
        return cantidadPendiente;
    }

    public void setCantidadPendiente(BigDecimal cantidadPendiente) {
        this.cantidadPendiente = cantidadPendiente;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    public boolean isTodoOk() {
        return todoOk;
    }

    public void setTodoOk(boolean todoOk) {
        this.todoOk = todoOk;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public BigDecimal getProduccion() {
        return produccion;
    }

    public void setProduccion(BigDecimal produccion) {
        this.produccion = produccion;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public BigDecimal getCantidadNominal() {
        return cantidadNominal;
    }

    public void setCantidadNominal(BigDecimal cantidadNominal) {
        this.cantidadNominal = cantidadNominal;
    }

    public ComposicionFormula getComposicionFormula() {
        return composicionFormula;
    }

    public void setComposicionFormula(ComposicionFormula composicionFormula) {
        this.composicionFormula = composicionFormula;
    }

    public BigDecimal getCantidadSecundaria() {
        return cantidadSecundaria;
    }

    public void setCantidadSecundaria(BigDecimal cantidadSecundaria) {
        this.cantidadSecundaria = cantidadSecundaria;
    }

    public List<ItemDetalleMovimientoProduccion> getItemDetalle() {
        return itemDetalle;
    }

    public void setItemDetalle(List<ItemDetalleMovimientoProduccion> itemDetalle) {
        this.itemDetalle = itemDetalle;
    }

    public ItemMovimientoProduccion getItemAplicado() {
        return itemAplicado;
    }

    public void setItemAplicado(ItemMovimientoProduccion itemAplicado) {
        this.itemAplicado = itemAplicado;
    }

    public List<ItemMovimientoProduccion> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemMovimientoProduccion> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public List<ItemDetalleMovimientoProduccion> getItemDetalleTemporal() {
        return itemDetalleTemporal;
    }

    public void setItemDetalleTemporal(List<ItemDetalleMovimientoProduccion> itemDetalleTemporal) {
        this.itemDetalleTemporal = itemDetalleTemporal;
    }

    public String getTipitm() {
        return tipitm;
    }

    public void setTipitm(String tipitm) {
        this.tipitm = tipitm;
    }

    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoProduccion other = (ItemMovimientoProduccion) obj;
        if (this.nroitm != other.nroitm) {
            return false;
        }
        if (!Objects.equals(this.tipitm, other.tipitm)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "ItemMovimientoProduccion{" + "id=" + id + '}';
    }

}
