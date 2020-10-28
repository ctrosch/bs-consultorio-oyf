/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.vista;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "FC_PRODUCTOS_PENDIENTES")
public class ProductoPendiente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, length = 30)
    private String id;
    @Basic(optional = false)
    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;
    @Column(name = "TIPPRO", length = 6)
    private String tippro;
    @Column(name = "ARTCOD", length = 30)
    private String artcod;
    @Column(name = "INDCOD", length = 6)
    private String indcod;
    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;
    @Column(name = "IVA", precision = 15, scale = 4)
    private BigDecimal iva;
    @Column(name = "DESCRIPCION", length = 120)
    private String descripcion;
    @Column(name = "PENDIENTE", precision = 15, scale = 4)
    private BigDecimal pendiente;
    @Column(name = "IMPORTE_TOTAL", precision = 15, scale = 4)
    private BigDecimal importeTotalSinIVA;

    @Transient
    private BigDecimal importeTotalConIVA;

    @Transient
    private String codigo;

//    @OneToMany(mappedBy = "productoPendiente", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//    private List<PedidoProducto> pedidos;

    public ProductoPendiente() {
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }    

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getImporteTotalSinIVA() {
        return importeTotalSinIVA;
    }

    public void setImporteTotalSinIVA(BigDecimal importeTotalSinIVA) {
        this.importeTotalSinIVA = importeTotalSinIVA;
    }

    public BigDecimal getImporteTotalConIVA() {

        try{
            importeTotalConIVA = importeTotalSinIVA.add(importeTotalSinIVA.multiply(iva).divide(new BigDecimal(100),2,RoundingMode.HALF_UP));
        }catch(Exception e){
            importeTotalConIVA = BigDecimal.ZERO;
        }
        return importeTotalConIVA;
    }

    public void setImporteTotalConIVA(BigDecimal importeTotalConIVA) {
        this.importeTotalConIVA = importeTotalConIVA;
    }

//    public List<PedidoProducto> getPedidos() {
//        return pedidos;
//    }
//
//    public void setPedidos(List<PedidoProducto> pedidos) {
//        this.pedidos = pedidos;
//    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getIndcod() {
        return indcod;
    }

    public void setIndcod(String indcod) {
        this.indcod = indcod;
    }


}
