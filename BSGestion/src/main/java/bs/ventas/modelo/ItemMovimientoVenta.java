/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "vt_movimiento_item")
@EntityListeners(AuditoriaListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPCPT", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class ItemMovimientoVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    MovimientoVenta movimiento;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;

    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;

    //Importe nacional
    @Column(name = "IMPNAC", precision = 15, scale = 2)
    private BigDecimal importe;

    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private BigDecimal importeSecundario;

    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;

    //Importe debe
    @Column(name = "IMPDEB", precision = 15, scale = 4)
    private BigDecimal importeDebe;

    //Importe haber
    @Column(name = "IMPHAB", precision = 15, scale = 4)
    private BigDecimal importeHaber;

    //Importe debe
    @Column(name = "DEBSEC", precision = 15, scale = 4)
    private BigDecimal importeDebeSecundario;

    //Importe haber
    @Column(name = "HABSEC", precision = 15, scale = 4)
    private BigDecimal importeHaberSecundario;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion2;
    @Column(name = "PCTBF3", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion3;
    @Column(name = "PCTBF4", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion4;
    @Column(name = "PCTBF5", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion5;
    @Column(name = "PCTBF6", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion6;
    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private BigDecimal porcentaTotalBonificacion;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private Concepto conceptoAsociado;

    @Transient
    private BigDecimal precioFinalConIVA;

    @Transient
    private BigDecimal precioFinalSinIVA;

    @Transient
    private BigDecimal totalLineaConIVA;

    public ItemMovimientoVenta() {

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;

        importe = BigDecimal.ZERO;
        importeSecundario = BigDecimal.ZERO;

        importeDebe = BigDecimal.ZERO;
        importeDebeSecundario = BigDecimal.ZERO;
        importeHaber = BigDecimal.ZERO;
        importeHaberSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();

    }

    public ItemMovimientoVenta(Integer id) {
        this.id = id;
        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;

        importe = BigDecimal.ZERO;
        importeSecundario = BigDecimal.ZERO;

        importeDebe = BigDecimal.ZERO;
        importeDebeSecundario = BigDecimal.ZERO;
        importeHaber = BigDecimal.ZERO;
        importeHaberSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
    }

    public ItemMovimientoVenta(Integer id, Concepto concepto) {
        this.id = id;
        this.concepto = concepto;
        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroItem() {
        return nroItem;
    }

    public void setNroItem(int nroItem) {
        this.nroItem = nroItem;
    }

    public MovimientoVenta getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoVenta movimiento) {
        this.movimiento = movimiento;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(BigDecimal precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(BigDecimal importeSecundario) {
        this.importeSecundario = importeSecundario;
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

    public BigDecimal getImporteDebeSecundario() {
        return importeDebeSecundario;
    }

    public void setImporteDebeSecundario(BigDecimal importeDebeSecundario) {
        this.importeDebeSecundario = importeDebeSecundario;
    }

    public BigDecimal getImporteHaberSecundario() {
        return importeHaberSecundario;
    }

    public void setImporteHaberSecundario(BigDecimal importeHaberSecundario) {
        this.importeHaberSecundario = importeHaberSecundario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getPorcentajeBonificacion1() {
        return porcentajeBonificacion1;
    }

    public void setPorcentajeBonificacion1(BigDecimal porcentajeBonificacion1) {
        this.porcentajeBonificacion1 = porcentajeBonificacion1;
    }

    public BigDecimal getPorcentajeBonificacion2() {
        return porcentajeBonificacion2;
    }

    public void setPorcentajeBonificacion2(BigDecimal porcentajeBonificacion2) {
        this.porcentajeBonificacion2 = porcentajeBonificacion2;
    }

    public BigDecimal getPorcentajeBonificacion3() {
        return porcentajeBonificacion3;
    }

    public void setPorcentajeBonificacion3(BigDecimal porcentajeBonificacion3) {
        this.porcentajeBonificacion3 = porcentajeBonificacion3;
    }

    public BigDecimal getPorcentajeBonificacion4() {
        return porcentajeBonificacion4;
    }

    public void setPorcentajeBonificacion4(BigDecimal porcentajeBonificacion4) {
        this.porcentajeBonificacion4 = porcentajeBonificacion4;
    }

    public BigDecimal getPorcentajeBonificacion5() {
        return porcentajeBonificacion5;
    }

    public void setPorcentajeBonificacion5(BigDecimal porcentajeBonificacion5) {
        this.porcentajeBonificacion5 = porcentajeBonificacion5;
    }

    public BigDecimal getPorcentajeBonificacion6() {
        return porcentajeBonificacion6;
    }

    public void setPorcentajeBonificacion6(BigDecimal porcentajeBonificacion6) {
        this.porcentajeBonificacion6 = porcentajeBonificacion6;
    }

    public BigDecimal getPorcentaTotalBonificacion() {
        return porcentaTotalBonificacion;
    }

    public void setPorcentaTotalBonificacion(BigDecimal porcentaTotalBonificacion) {
        this.porcentaTotalBonificacion = porcentaTotalBonificacion;
    }

    public BigDecimal getPrecioFinalConIVA() {
        return precioFinalConIVA;
    }

    public void setPrecioFinalConIVA(BigDecimal precioFinalConIVA) {
        this.precioFinalConIVA = precioFinalConIVA;
    }

    public BigDecimal getTotalLineaConIVA() {
        return totalLineaConIVA;
    }

    public void setTotalLineaConIVA(BigDecimal totalLineaConIVA) {
        this.totalLineaConIVA = totalLineaConIVA;
    }

    public BigDecimal getPrecioFinalSinIVA() {
        try {
            if (precio != null) {

                precioFinalSinIVA = BigDecimal.ZERO;
                BigDecimal bonif = precio.multiply(porcentaTotalBonificacion.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal impUnit = precio.add(bonif);
                precioFinalSinIVA = impUnit;
            }

            return precioFinalSinIVA;

        } catch (Exception e) {

            return BigDecimal.ZERO;
        }
    }

    public void setPrecioFinalSinIVA(BigDecimal precioFinalSinIVA) {
        this.precioFinalSinIVA = precioFinalSinIVA;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public Concepto getConceptoAsociado() {
        return conceptoAsociado;
    }

    public void setConceptoAsociado(Concepto conceptoAsociado) {
        this.conceptoAsociado = conceptoAsociado;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
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
        if (!(object instanceof ItemMovimientoVenta)) {
            return false;
        }
        ItemMovimientoVenta other = (ItemMovimientoVenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoVenta{" + "id=" + id + ", nroItem=" + nroItem + ", movimiento=" + movimiento + ", concepto=" + concepto + ", unidadMedida=" + unidadMedida + ", cantidad=" + cantidad + ", cotizacion=" + cotizacion + ", precio=" + precio + ", precioSecundario=" + precioSecundario + ", importe=" + importe + ", importeSecundario=" + importeSecundario + ", debeHaber=" + debeHaber + ", importeDebe=" + importeDebe + ", importeHaber=" + importeHaber + ", importeDebeSecundario=" + importeDebeSecundario + ", importeHaberSecundario=" + importeHaberSecundario + ", observaciones=" + observaciones + ", porcentajeBonificacion1=" + porcentajeBonificacion1 + ", porcentajeBonificacion2=" + porcentajeBonificacion2 + ", porcentajeBonificacion3=" + porcentajeBonificacion3 + ", porcentajeBonificacion4=" + porcentajeBonificacion4 + ", porcentajeBonificacion5=" + porcentajeBonificacion5 + ", porcentajeBonificacion6=" + porcentajeBonificacion6 + ", porcentaTotalBonificacion=" + porcentaTotalBonificacion + ", cuentaContable=" + cuentaContable + ", auditoria=" + auditoria + ", conceptoAsociado=" + conceptoAsociado + ", precioFinalConIVA=" + precioFinalConIVA + ", precioFinalSinIVA=" + precioFinalSinIVA + ", totalLineaConIVA=" + totalLineaConIVA + '}';
    }

}
