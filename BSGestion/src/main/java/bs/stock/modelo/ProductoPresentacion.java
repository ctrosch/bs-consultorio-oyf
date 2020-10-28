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
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Guillermo
 */
@Entity
@Table(name = "st_producto_presentacion")
@EntityListeners(AuditoriaListener.class)
public class ProductoPresentacion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "TIPENV", nullable = false, length = 30)
    private String tipoEnvase;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidadUnitaria;

    @Column(name = "UNIMED", nullable = false, length = 50)
    private String unidadMedida;

    @Column(name = "CODBAR", length = 100)
    private String codigoBarra;

    @Column(name = "ALTO", precision = 15, scale = 4)
    private BigDecimal alto;

    @Column(name = "ANCHO", precision = 15, scale = 4)
    private BigDecimal ancho;

    @Column(name = "PROFUN", precision = 15, scale = 4)
    private BigDecimal profundidad;

    @Column(name = "PESO", precision = 15, scale = 4)
    private BigDecimal peso;

    @Column(name = "UNIPES", nullable = false, length = 50)
    private String unidadPeso;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ProductoPresentacion() {
        this.auditoria = new Auditoria();
        this.cantidadUnitaria = BigDecimal.ZERO;
        this.alto = BigDecimal.ZERO;
        this.ancho = BigDecimal.ZERO;
        this.profundidad = BigDecimal.ZERO;
        this.peso = BigDecimal.ZERO;

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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTipoEnvase() {
        return tipoEnvase;
    }

    public void setTipoEnvase(String tipoEnvase) {
        this.tipoEnvase = tipoEnvase;
    }

    public BigDecimal getCantidadUnitaria() {
        return cantidadUnitaria;
    }

    public void setCantidadUnitaria(BigDecimal cantidadUnitaria) {
        this.cantidadUnitaria = cantidadUnitaria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public BigDecimal getAlto() {
        return alto;
    }

    public void setAlto(BigDecimal alto) {
        this.alto = alto;
    }

    public BigDecimal getAncho() {
        return ancho;
    }

    public void setAncho(BigDecimal ancho) {
        this.ancho = ancho;
    }

    public BigDecimal getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(BigDecimal profundidad) {
        this.profundidad = profundidad;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getUnidadPeso() {
        return unidadPeso;
    }

    public void setUnidadPeso(String unidadPeso) {
        this.unidadPeso = unidadPeso;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final ProductoPresentacion other = (ProductoPresentacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProductoPresentacion{" + "id=" + id + '}';
    }

}
