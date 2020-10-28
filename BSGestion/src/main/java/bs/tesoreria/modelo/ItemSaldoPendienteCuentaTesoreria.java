/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "zz_cj_pendiente_cc")
public class ItemSaldoPendienteCuentaTesoreria implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CHEQUE", nullable = false, length = 80)
    private String cheque;    
       
//    @Id
//    @Column(name = "NROINT", nullable = false, length = 80)
//    private String nroInterno; 
    
    @JoinColumn(name = "CODBCO", referencedColumnName = "CODIGO")    
    @ManyToOne(fetch = FetchType.LAZY)
    private Banco banco;
    
    @JoinColumn(name = "NROCTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaTesoreria cuentaTesoreria;
    
    @Column(name = "FCHCHE")
    @Temporal(TemporalType.DATE)
    private Date fechaCheque;
    
    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "IMPORTE", precision = 15, scale = 2)
    private BigDecimal importe;
    
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "FIRMANTE")
    private String firmante;
    
    @Column(name = "NRODOC")
    private String numeroDocumento;
    
    @Column(name = "CATEGO")
    private String categoria;
    
    @Transient
    private boolean seleccionado;
    @Transient
    private BigDecimal importeAplicar;

    public ItemSaldoPendienteCuentaTesoreria() {
        
        importeAplicar = BigDecimal.ZERO;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }
    
    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFirmante() {
        return firmante;
    }

    public void setFirmante(String firmante) {
        this.firmante = firmante;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public BigDecimal getImporteAplicar() {
        return importeAplicar;
    }

    public void setImporteAplicar(BigDecimal importeAplicar) {
        this.importeAplicar = importeAplicar;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
        
    public Date getFechaCheque() {
        return fechaCheque;
    }

    public void setFechaCheque(Date fechaCheque) {
        this.fechaCheque = fechaCheque;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.cheque != null ? this.cheque.hashCode() : 0);
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
        final ItemSaldoPendienteCuentaTesoreria other = (ItemSaldoPendienteCuentaTesoreria) obj;
        if (this.cheque != other.cheque && (this.cheque == null || !this.cheque.equals(other.cheque))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemSaldoPendienteCuentaTesoreria{" + "cheque=" + cheque + '}';
    }
    
    
}
