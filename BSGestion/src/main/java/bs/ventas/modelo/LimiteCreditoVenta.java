/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "vt_limite_credito")
@EntityListeners(AuditoriaListener.class)
public class LimiteCreditoVenta implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    //Codigo limite de credito
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    //Descripcion de limite de credito
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    //Importe de limite de credito
    @Basic(optional = false)
    @Column(name = "IMPORTE", nullable = false, precision = 15, scale = 2)
    private BigDecimal importe;

    @JoinColumn(name = "CODCOF", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda moneda;
   
    //Trabaja con control de vencimiento
    @Column(name = "TRAVEN")
    private Character trabajaConControlVencimiento;

    //Cantidad de dias (Minimo)
    @Column(name = "DIAMIN")
    private Short cantidadDiasMinimo;
    
    //Porcentaje minimo
    @Column(name = "PORMIN", precision = 15, scale = 4)
    private BigDecimal porcentaMinimo;

    /**
    @Lob
    @Column(name = "SQLVEN", length = 2147483647)
    private String sqlven;
    @Lob
    @Column(name = "SQLFAC", length = 2147483647)
    private String sqlfac;
    @Lob
    @Column(name = "SQLTES", length = 2147483647)
    private String sqltes;
    @Lob
    @Column(name = "SQLVAV", length = 2147483647)
    private String sqlvav;
    @Lob
    @Column(name = "SQLVVE", length = 2147483647)
    private String sqlvve;
    */

    @Embedded
    private Auditoria auditoria;

    public LimiteCreditoVenta() {
    }

    public LimiteCreditoVenta(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    
    public Character getTrabajaConControlVencimiento() {
        return trabajaConControlVencimiento;
    }

    public void setTrabajaConControlVencimiento(Character trabajaConControlVencimiento) {
        this.trabajaConControlVencimiento = trabajaConControlVencimiento;
    }

    public Short getCantidadDiasMinimo() {
        return cantidadDiasMinimo;
    }

    public void setCantidadDiasMinimo(Short cantidadDiasMinimo) {
        this.cantidadDiasMinimo = cantidadDiasMinimo;
    }

    public BigDecimal getPorcentaMinimo() {
        return porcentaMinimo;
    }

    public void setPorcentaMinimo(BigDecimal porcentaMinimo) {
        this.porcentaMinimo = porcentaMinimo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final LimiteCreditoVenta other = (LimiteCreditoVenta) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LimiteDeCreditoVenta{" + "codigo=" + codigo + '}';
    }

}
