/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author ctrosch
 */
@Entity
@DiscriminatorValue("A")
public class ItemProductoProveedor extends ItemMovimientoProveedor {

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = true, insertable=true, updatable=true)    
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;
    
    @Column(name = "DESCRP", length = 200)
    private String descripcion;

    //Deposito
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;
        
    @Column(name = "VALDEC", precision = 15, scale = 4)
    private BigDecimal valorDeclarado;
        
    @Column(name = "KGAFOR", precision = 15, scale = 2)
    private BigDecimal kilogramosAforado;
    @Column(name = "KGEFEC", precision = 15, scale = 2)
    private BigDecimal kilogramosEfectivo;
    
    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public BigDecimal getKilogramosAforado() {
        return kilogramosAforado;
    }

    public void setKilogramosAforado(BigDecimal kilogramosAforado) {
        this.kilogramosAforado = kilogramosAforado;
    }

    public BigDecimal getKilogramosEfectivo() {
        return kilogramosEfectivo;
    }

    public void setKilogramosEfectivo(BigDecimal kilogramosEfectivo) {
        this.kilogramosEfectivo = kilogramosEfectivo;
    }
    
}
