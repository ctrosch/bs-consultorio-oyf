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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_composicion_formula")
@EntityListeners(AuditoriaListener.class)
@IdClass(ComposicionFormulaPK.class)
public class ComposicionFormula implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 8)
    private String codfor;
    
    @Id
    @Column(name = "ARTCOD", nullable = false, length = 30)
    private String artcod;
    
    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false, insertable=false, updatable=false)    
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    /**
     *  Formula
     */
    @JoinColumn(name = "CODIGO", referencedColumnName = "CODIGO", nullable = false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Formula formula;

    /**
     * Tipo de formula
     */
    @Column(name = "TIPFOR", nullable = false, length = 1)
    private String tipfor;
    /**
     * Lote optimo
     */
    @Column(name = "LOTOPT", precision = 15, scale = 2)
    private BigDecimal lotopt;

    /*
     * Departamento asociado
     */
    @Column(name = "DEPTOS", length = 6)
    private String deptos;
    /**
     * Factor alternativo 1
     */
    @Column(name = "EXPLOT")
    private Character explot;
    /**
     * Fecha inicio
     */
    @Column(name = "FCHINI")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    /**
     * Fecha Fin
     */
    @Column(name = "FCHFIN")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    
    @Column(name = "SECLIN", length = 40)
    private String seclin;
    /**
     * Valor nominal
     */
    @Column(name = "VALNOM", precision = 15, scale = 2)
    private BigDecimal valnom;
    /**
     * Factor Multiplicador
     */
    @Column(name = "FACMUL", precision = 15, scale = 4)
    private BigDecimal facmul;
     /**
     *
     */
    @Column(name = "FACONS", precision = 15, scale = 4)
    private BigDecimal facons;
    /**
     * Factor alternativo 1
     */
    @Column(name = "FACON1", precision = 15, scale = 4)
    private BigDecimal facon1;
    /**
     * Factor alternativo 2
     */
    @Column(name = "FACON2", precision = 15, scale = 4)
    private BigDecimal facon2;
    /**
     * Factor alternativo 3
     */
    @Column(name = "FACON3", precision = 15, scale = 4)
    private BigDecimal facon3;
    /**
     * Observaciones
     */
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observ;
    /**
     * Generada automáticamente por el sistema
     */
    @Column(name = "GENAUT")
    private Character Genaut;
    /**
     * Tipo de producto aplicación
     */
    @Column(name = "TIPAPL", length = 6)
    private String tipapl;
    /**
     * Codigo de producto aplicación
     */
    @Column(name = "ARTAPL", length = 30)
    private String artapl;

    /*
     * Formular de aplicación
     */
    @Column(name = "FORAPL", length = 8)
    private String forapl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "composicionFormula", fetch=FetchType.LAZY)    
    private List<ItemComposicionFormulaComponente> itemsComponente;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "composicionFormula", fetch=FetchType.LAZY)    
    private List<ItemComposicionFormulaProceso> itemsProceso;
    
    @Embedded
    private Auditoria auditoria;

    public ComposicionFormula() {        
        this.auditoria = new Auditoria();    
        this.tipfor = "";
        this.itemsComponente = new ArrayList<ItemComposicionFormulaComponente>();
        this.itemsProceso = new ArrayList<ItemComposicionFormulaProceso>();
    }

    public ComposicionFormula(String artcod, String codfor) {
        
        this.artcod = artcod;
        this.codfor = codfor;
        this.auditoria = new Auditoria();     
        this.tipfor = "";
        this.itemsComponente = new ArrayList<ItemComposicionFormulaComponente>();
        this.itemsProceso = new ArrayList<ItemComposicionFormulaProceso>();
        
    }

    public String getArtcod() {
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

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }
    
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTipfor() {
        return tipfor;
    }

    public void setTipfor(String tipfor) {
        this.tipfor = tipfor;
    }

    public BigDecimal getLotopt() {
        return lotopt;
    }

    public void setLotopt(BigDecimal lotopt) {
        this.lotopt = lotopt;
    }

    public String getDeptos() {
        return deptos;
    }

    public void setDeptos(String deptos) {
        this.deptos = deptos;
    }

    public Character getExplot() {
        return explot;
    }

    public void setExplot(Character explot) {
        this.explot = explot;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public String getSeclin() {
        return seclin;
    }

    public void setSeclin(String seclin) {
        this.seclin = seclin;
    }

    public BigDecimal getValnom() {
        return valnom;
    }

    public void setValnom(BigDecimal valnom) {
        this.valnom = valnom;
    }

    public BigDecimal getFacmul() {
        return facmul;
    }

    public void setFacmul(BigDecimal facmul) {
        this.facmul = facmul;
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

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public Character getGenaut() {
        return Genaut;
    }

    public void setGenaut(Character Genaut) {
        this.Genaut = Genaut;
    }

    public String getTipapl() {
        return tipapl;
    }

    public void setTipapl(String tipapl) {
        this.tipapl = tipapl;
    }

    public String getArtapl() {
        return artapl;
    }

    public void setArtapl(String artapl) {
        this.artapl = artapl;
    }

    public String getForapl() {
        return forapl;
    }

    public void setForapl(String forapl) {
        this.forapl = forapl;
    }
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemComposicionFormulaComponente> getItemsComponente() {
        return itemsComponente;
    }

    public void setItemsComponente(List<ItemComposicionFormulaComponente> itemsComponente) {
        this.itemsComponente = itemsComponente;
    }

    public List<ItemComposicionFormulaProceso> getItemsProceso() {
        return itemsProceso;
    }

    public void setItemsProceso(List<ItemComposicionFormulaProceso> itemsProceso) {
        this.itemsProceso = itemsProceso;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.artcod != null ? this.artcod.hashCode() : 0);
        hash = 17 * hash + (this.codfor != null ? this.codfor.hashCode() : 0);
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
        final ComposicionFormula other = (ComposicionFormula) obj;
        if ((this.artcod == null) ? (other.artcod != null) : !this.artcod.equals(other.artcod)) {
            return false;
        }
        if ((this.codfor == null) ? (other.codfor != null) : !this.codfor.equals(other.codfor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ComposicionFormula{" + "artcod=" + artcod + ", codfor=" + codfor + '}';
    }
    
    
}
