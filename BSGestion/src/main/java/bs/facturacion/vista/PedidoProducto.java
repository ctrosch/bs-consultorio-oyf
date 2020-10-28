/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.vista;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
//@Entity
//@Table(name = "FC_PEPRPE")
public class PedidoProducto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 49)
    private String id;
    @Basic(optional = false)
    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;
    @Column(name = "TIPPRO", length = 6)
    private String tippro;
    @Column(name = "ARTCOD", length = 30)
    private String artcod;
    @Basic(optional = false)
    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codfor;
    @Basic(optional = false)
    @Column(name = "MODFOR", nullable = false, length = 2)
    private String modfor;
    @Basic(optional = false)
    @Column(name = "NROFOR", nullable = false)
    private int nrofor;
    @Basic(optional = false)
    @Column(name = "SUCURS", nullable = false)
    private int sucurs;
    @Basic(optional = false)
    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;
    @Basic(optional = false)
    @Column(name = "PRECIO", nullable = false, precision = 15, scale = 4)
    private BigDecimal precio;
    @Basic(optional = false)
    @Column(name = "IVA", nullable = false, precision = 15, scale = 4)
    private BigDecimal iva;
    @Column(name = "PENDIENTE", precision = 15, scale = 4)
    private BigDecimal pendiente;
    @Column(name = "IMPORTE_TOTAL", precision = 15, scale = 4)
    private BigDecimal importeTotal;

    @Transient
    private BigDecimal importeTotalConIVA;

//    @JoinColumns({
//        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", insertable=false, updatable=false),
//        @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", insertable=false, updatable=false),
//        @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", insertable=false, updatable=false)
//    })
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ProductoPendiente productoPendiente;

    public PedidoProducto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getTippro() {
        return tippro;
    }

    public void setTippro(String tippro) {
        this.tippro = tippro;
    }

    public String getCodigo() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    public int getNrofor() {
        return nrofor;
    }

    public void setNrofor(int nrofor) {
        this.nrofor = nrofor;
    }

    public Date getFchmov() {
        return fechaMovimiento;
    }

    public void setFchmov(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public int getSucurs() {
        return sucurs;
    }

    public void setSucurs(int sucurs) {
        this.sucurs = sucurs;
    }

//    public ProductoPendiente getProductoPendiente() {
//        return productoPendiente;
//    }
//
//    public void setProductoPendiente(ProductoPendiente productoPendiente) {
//        this.productoPendiente = productoPendiente;
//    }

    public BigDecimal getImporteTotalConIVA() {
         try{
            importeTotalConIVA = importeTotal.add(importeTotal.multiply(iva).divide(new BigDecimal(100),2,RoundingMode.HALF_UP));
        }catch(Exception e){
            importeTotalConIVA = BigDecimal.ZERO;
        }
        return importeTotalConIVA;
    }

    public void setImporteTotalConIVA(BigDecimal importeTotalConIVA) {
        this.importeTotalConIVA = importeTotalConIVA;
    }


    
}
