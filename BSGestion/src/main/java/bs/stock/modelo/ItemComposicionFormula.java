/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "st_composicion_formula_item")
@EntityListeners(AuditoriaListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPITM", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class ItemComposicionFormula implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "NROITM", nullable = false)
    private int nroitm;
    

    /**
     * Composición de codigoa
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "ARTCOD", referencedColumnName = "ARTCOD"),
        @JoinColumn(name = "CODIGO", referencedColumnName = "CODIGO")
    })
    private ComposicionFormula composicionFormula;

    /**
     * Producto componente
     */
    @JoinColumn(name = "ARTITM", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoComponente;

    /**
     * Unidad de medida del componente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIITM", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    private UnidadMedida unidadMedidaItem;
    /**
     * Cantidad nominal
     */
    @Column(name = "CNTNOM", precision = 15, scale = 4)
    private BigDecimal cantidadNominal;
    /**
     * Cantidad real
     */
    @Column(name = "CNTREA", precision = 15, scale = 4)
    private BigDecimal cntrea;

    /**
     * Registro de calidad
     */
    @Column(name = "REGCAL", length = 14)
    private String regcal;
    /**
     * Secuencia de fabricación
     */
    @Column(name = "SECFAB", precision = 15, scale = 4)
    private BigDecimal secfab;

    /**
     * Factor de consumo
     */
    @Column(name = "FACONS", precision = 15, scale = 4)
    private BigDecimal facons;
    /**
     * Factor de consumo 1
     */
    @Column(name = "FACON1", precision = 15, scale = 4)
    private BigDecimal facon1;
    /**
     * Factor de consumo 2
     */
    @Column(name = "FACON2", precision = 15, scale = 4)
    private BigDecimal facon2;
    /**
     * Factor de consumo 3
     */
    @Column(name = "FACON3", precision = 15, scale = 4)
    private BigDecimal facon3;

    /**
     * Verifica disponibilidad de stock
     */
    @Column(name = "VERDIS", nullable = false)
    private String verificaDisponibilidadStock;
    /**
     * Reposición automática
     */
    @Column(name = "REPAUT", nullable = false)
    private String reposicionAutomatica;

    @Embedded
    private Auditoria auditoria;

    public ItemComposicionFormula() {

        this.verificaDisponibilidadStock = "S";
        this.reposicionAutomatica = "N";
        this.auditoria = new Auditoria();

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

    public ComposicionFormula getComposicionFormula() {
        return composicionFormula;
    }

    public void setComposicionFormula(ComposicionFormula composicionFormula) {
        this.composicionFormula = composicionFormula;
    }

    public Producto getProductoComponente() {
        return productoComponente;
    }

    public void setProductoComponente(Producto productoComponente) {
        this.productoComponente = productoComponente;
    }

    public UnidadMedida getUnidadMedidaItem() {
        return unidadMedidaItem;
    }

    public void setUnidadMedidaItem(UnidadMedida unidadMedidaItem) {
        this.unidadMedidaItem = unidadMedidaItem;
    }

    public BigDecimal getCantidadNominal() {
        return cantidadNominal;
    }

    public void setCantidadNominal(BigDecimal cantidadNominal) {
        this.cantidadNominal = cantidadNominal;
    }

    public BigDecimal getCntrea() {
        return cntrea;
    }

    public void setCntrea(BigDecimal cntrea) {
        this.cntrea = cntrea;
    }

    public String getRegcal() {
        return regcal;
    }

    public void setRegcal(String regcal) {
        this.regcal = regcal;
    }

    public BigDecimal getSecfab() {
        return secfab;
    }

    public void setSecfab(BigDecimal secfab) {
        this.secfab = secfab;
    }

    public BigDecimal getFacons() {
        return facons;
    }

    public void setFacons(BigDecimal facons) {
        this.facons = facons;
    }

    public BigDecimal getFacon1() {
        return facon1;
    }

    public void setFacon1(BigDecimal facon1) {
        this.facon1 = facon1;
    }

    public BigDecimal getFacon2() {
        return facon2;
    }

    public void setFacon2(BigDecimal facon2) {
        this.facon2 = facon2;
    }

    public BigDecimal getFacon3() {
        return facon3;
    }

    public void setFacon3(BigDecimal facon3) {
        this.facon3 = facon3;
    }

    public String getVerificaDisponibilidadStock() {
        return verificaDisponibilidadStock;
    }

    public void setVerificaDisponibilidadStock(String verificaDisponibilidadStock) {
        this.verificaDisponibilidadStock = verificaDisponibilidadStock;
    }

    public String getReposicionAutomatica() {
        return reposicionAutomatica;
    }

    public void setReposicionAutomatica(String reposicionAutomatica) {
        this.reposicionAutomatica = reposicionAutomatica;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemComposicionFormula other = (ItemComposicionFormula) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemComposicionFormula{" + "id=" + id + '}';
    }
    
}
