/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.vista;

import bs.produccion.modelo.MovimientoProduccion;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pd_pendiente_detalle")
@XmlRootElement
@IdClass(PendienteProduccionDetallePK.class)
public class PendienteProduccionDetalle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_IAPL")
    private Integer idIapl;
    @Id
    @NotNull
    @Column(name = "ID_MAPL", nullable = false)
    private Integer idMapl;
    
    @NotNull
    @Column(nullable = false)
    private String tipitm;

    @NotNull
    @Column(nullable = false)
    private String modfor;

    @NotNull
    @Size(min = 1, max = 6)
    @Column(nullable = false, length = 6)
    private String codfor;

    @NotNull
    @Column(nullable = false)
    private int nrofor;

    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fchmov;

    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String artcod;

    @NotNull
    @Size(min = 1, max = 6)
    @Column(nullable = false, length = 6)
    private String formul;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(nullable = false, length = 1)
    private String stocks;

    @Column(nullable = false, length = 20)
    private String grupo;

    @Lob
    @Size(max = 65535)
    @Column(length = 65535)
    private String observ;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 10, scale = 2)
    private BigDecimal pendiente;
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(precision = 10, scale = 2)
    private BigDecimal presec;

    @JoinColumn(name = "artcod", referencedColumnName = "codigo", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    //Unidad de medida
    @JoinColumn(name = "unimed", referencedColumnName = "codigo")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mapl", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    MovimientoProduccion movimientoAplicacion;

    @Transient
    private BigDecimal cantidad;

    @Transient
    private boolean seleccionado;

    public PendienteProduccionDetalle() {

        cantidad = BigDecimal.ZERO;
    }

    public Integer getIdIapl() {
        return idIapl;
    }

    public void setIdIapl(Integer idIapl) {
        this.idIapl = idIapl;
    }

    public Integer getIdMapl() {
        return idMapl;
    }

    public void setIdMapl(Integer idMapl) {
        this.idMapl = idMapl;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public int getNrofor() {
        return nrofor;
    }

    public void setNrofor(int nrofor) {
        this.nrofor = nrofor;
    }

    public Date getFchmov() {
        return fchmov;
    }

    public void setFchmov(Date fchmov) {
        this.fchmov = fchmov;
    }

    public String getArtcod() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public String getStocks() {
        return stocks;
    }

    public void setStocks(String stocks) {
        this.stocks = stocks;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPresec() {
        return presec;
    }

    public void setPresec(BigDecimal presec) {
        this.presec = presec;
    }

    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    public String getFormul() {
        return formul;
    }

    public void setFormul(String formul) {
        this.formul = formul;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public MovimientoProduccion getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoProduccion movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getTipitm() {
        return tipitm;
    }

    public void setTipitm(String tipitm) {
        this.tipitm = tipitm;
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.idIapl != null ? this.idIapl.hashCode() : 0);
        hash = 37 * hash + (this.idMapl != null ? this.idMapl.hashCode() : 0);
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
        final PendienteProduccionDetalle other = (PendienteProduccionDetalle) obj;
        if (this.idIapl != other.idIapl && (this.idIapl == null || !this.idIapl.equals(other.idIapl))) {
            return false;
        }
        if (this.idMapl != other.idMapl && (this.idMapl == null || !this.idMapl.equals(other.idMapl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteProduccionDetalle{" + "idIapl=" + idIapl + ", idMapl=" + idMapl + '}';
    }
}
