/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.stock.modelo.*;
import java.io.Serializable;
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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pr_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroPrestamo implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "DESCL1", length = 80)
    private String descripcionClasificacion1;
    @Column(name = "DESCL2", length = 80)
    private String descripcionClasificacion2;
    @Column(name = "DESCL3", length = 80)
    private String descripcionClasificacion3;
    @Column(name = "DESCL4", length = 80)
    private String descripcionClasificacion4;
    @Column(name = "DESCL5", length = 80)
    private String descripcionClasificacion5;

    @Column(name = "tipcl1", length = 80)
    private String tipoVistaClasificacion1;
    @Column(name = "tipcl2", length = 80)
    private String tipoVistaClasificacion2;
    @Column(name = "tipcl3", length = 80)
    private String tipoVistaClasificacion3;
    @Column(name = "tipcl4", length = 80)
    private String tipoVistaClasificacion4;
    @Column(name = "tipcl5", length = 80)
    private String tipoVistaClasificacion5;

    @Column(name = "gccanc")
    private Integer caracteresParaGeneracionCodigo;
    @Column(name = "gccla01", length = 1)
    private String utilizaClasificacion01GeneracionCodigo;
    @Column(name = "gccla02", length = 1)
    private String utilizaClasificacion02GeneracionCodigo;
    @Column(name = "gccla03", length = 1)
    private String utilizaClasificacion03GeneracionCodigo;
    @Column(name = "gccla04", length = 1)
    private String utilizaClasificacion04GeneracionCodigo;
    @Column(name = "gccla05", length = 1)
    private String utilizaClasificacion05GeneracionCodigo;

    @JoinColumn(name = "estado", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoPrestamo estado;

    @JoinColumn(name = "codpro", referencedColumnName = "ID")
    @ManyToOne
    private Promotor promotor;

    @JoinColumn(name = "codfin", referencedColumnName = "ID")
    @ManyToOne
    private Financiador financiador;

    @JoinColumn(name = "codlin", referencedColumnName = "ID")
    @ManyToOne
    private LineaCredito lineaCredito;

    @JoinColumn(name = "codamo", referencedColumnName = "CODIGO")
    @ManyToOne
    private AmortizacionPrestamo amortizacion;

    @JoinColumn(name = "clas01", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion01 clasificacion01;

    @JoinColumn(name = "clas02", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion02 clasificacion02;

    @JoinColumn(name = "clas03", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion03 clasificacion03;

    @JoinColumn(name = "clas04", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion04 clasificacion04;

    @JoinColumn(name = "clas05", referencedColumnName = "CODIGO")
    @ManyToOne
    private Clasificacion05 clasificacion05;

    @JoinColumn(name = "unidev", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadTiempoDevolucion;

    @Column(name = "cntdev")
    private Integer tiempoDevolucion;

    @Column(name = "cntcuo")
    private Integer cantidadCuotas;

    @Column(name = "cntpag")
    private Integer tiempoEntreCuotas;

    @Embedded
    private Auditoria auditoria;

    public ParametroPrestamo() {

        this.auditoria = new Auditoria();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcionClasificacion1() {
        return descripcionClasificacion1;
    }

    public void setDescripcionClasificacion1(String descripcionClasificacion1) {
        this.descripcionClasificacion1 = descripcionClasificacion1;
    }

    public String getDescripcionClasificacion2() {
        return descripcionClasificacion2;
    }

    public void setDescripcionClasificacion2(String descripcionClasificacion2) {
        this.descripcionClasificacion2 = descripcionClasificacion2;
    }

    public String getDescripcionClasificacion3() {
        return descripcionClasificacion3;
    }

    public void setDescripcionClasificacion3(String descripcionClasificacion3) {
        this.descripcionClasificacion3 = descripcionClasificacion3;
    }

    public String getDescripcionClasificacion4() {
        return descripcionClasificacion4;
    }

    public void setDescripcionClasificacion4(String descripcionClasificacion4) {
        this.descripcionClasificacion4 = descripcionClasificacion4;
    }

    public String getDescripcionClasificacion5() {
        return descripcionClasificacion5;
    }

    public void setDescripcionClasificacion5(String descripcionClasificacion5) {
        this.descripcionClasificacion5 = descripcionClasificacion5;
    }

    public String getTipoVistaClasificacion1() {
        return tipoVistaClasificacion1;
    }

    public void setTipoVistaClasificacion1(String tipoVistaClasificacion1) {
        this.tipoVistaClasificacion1 = tipoVistaClasificacion1;
    }

    public String getTipoVistaClasificacion2() {
        return tipoVistaClasificacion2;
    }

    public void setTipoVistaClasificacion2(String tipoVistaClasificacion2) {
        this.tipoVistaClasificacion2 = tipoVistaClasificacion2;
    }

    public String getTipoVistaClasificacion3() {
        return tipoVistaClasificacion3;
    }

    public void setTipoVistaClasificacion3(String tipoVistaClasificacion3) {
        this.tipoVistaClasificacion3 = tipoVistaClasificacion3;
    }

    public String getTipoVistaClasificacion4() {
        return tipoVistaClasificacion4;
    }

    public void setTipoVistaClasificacion4(String tipoVistaClasificacion4) {
        this.tipoVistaClasificacion4 = tipoVistaClasificacion4;
    }

    public String getTipoVistaClasificacion5() {
        return tipoVistaClasificacion5;
    }

    public void setTipoVistaClasificacion5(String tipoVistaClasificacion5) {
        this.tipoVistaClasificacion5 = tipoVistaClasificacion5;
    }

    public Integer getCaracteresParaGeneracionCodigo() {
        return caracteresParaGeneracionCodigo;
    }

    public void setCaracteresParaGeneracionCodigo(Integer caracteresParaGeneracionCodigo) {
        this.caracteresParaGeneracionCodigo = caracteresParaGeneracionCodigo;
    }

    public String getUtilizaClasificacion01GeneracionCodigo() {
        return utilizaClasificacion01GeneracionCodigo;
    }

    public void setUtilizaClasificacion01GeneracionCodigo(String utilizaClasificacion01GeneracionCodigo) {
        this.utilizaClasificacion01GeneracionCodigo = utilizaClasificacion01GeneracionCodigo;
    }

    public String getUtilizaClasificacion02GeneracionCodigo() {
        return utilizaClasificacion02GeneracionCodigo;
    }

    public void setUtilizaClasificacion02GeneracionCodigo(String utilizaClasificacion02GeneracionCodigo) {
        this.utilizaClasificacion02GeneracionCodigo = utilizaClasificacion02GeneracionCodigo;
    }

    public String getUtilizaClasificacion03GeneracionCodigo() {
        return utilizaClasificacion03GeneracionCodigo;
    }

    public void setUtilizaClasificacion03GeneracionCodigo(String utilizaClasificacion03GeneracionCodigo) {
        this.utilizaClasificacion03GeneracionCodigo = utilizaClasificacion03GeneracionCodigo;
    }

    public String getUtilizaClasificacion04GeneracionCodigo() {
        return utilizaClasificacion04GeneracionCodigo;
    }

    public void setUtilizaClasificacion04GeneracionCodigo(String utilizaClasificacion04GeneracionCodigo) {
        this.utilizaClasificacion04GeneracionCodigo = utilizaClasificacion04GeneracionCodigo;
    }

    public String getUtilizaClasificacion05GeneracionCodigo() {
        return utilizaClasificacion05GeneracionCodigo;
    }

    public void setUtilizaClasificacion05GeneracionCodigo(String utilizaClasificacion05GeneracionCodigo) {
        this.utilizaClasificacion05GeneracionCodigo = utilizaClasificacion05GeneracionCodigo;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public void setPromotor(Promotor promotor) {
        this.promotor = promotor;
    }

    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador financiador) {
        this.financiador = financiador;
    }

    public LineaCredito getLineaCredito() {
        return lineaCredito;
    }

    public void setLineaCredito(LineaCredito lineaCredito) {
        this.lineaCredito = lineaCredito;
    }

    public AmortizacionPrestamo getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(AmortizacionPrestamo amortizacion) {
        this.amortizacion = amortizacion;
    }

    public Clasificacion01 getClasificacion01() {
        return clasificacion01;
    }

    public void setClasificacion01(Clasificacion01 clasificacion01) {
        this.clasificacion01 = clasificacion01;
    }

    public Clasificacion02 getClasificacion02() {
        return clasificacion02;
    }

    public void setClasificacion02(Clasificacion02 clasificacion02) {
        this.clasificacion02 = clasificacion02;
    }

    public Clasificacion03 getClasificacion03() {
        return clasificacion03;
    }

    public void setClasificacion03(Clasificacion03 clasificacion03) {
        this.clasificacion03 = clasificacion03;
    }

    public Clasificacion04 getClasificacion04() {
        return clasificacion04;
    }

    public void setClasificacion04(Clasificacion04 clasificacion04) {
        this.clasificacion04 = clasificacion04;
    }

    public Clasificacion05 getClasificacion05() {
        return clasificacion05;
    }

    public void setClasificacion05(Clasificacion05 clasificacion05) {
        this.clasificacion05 = clasificacion05;
    }

    public UnidadMedida getUnidadTiempoDevolucion() {
        return unidadTiempoDevolucion;
    }

    public void setUnidadTiempoDevolucion(UnidadMedida unidadTiempoDevolucion) {
        this.unidadTiempoDevolucion = unidadTiempoDevolucion;
    }

    public Integer getTiempoDevolucion() {
        return tiempoDevolucion;
    }

    public void setTiempoDevolucion(Integer tiempoDevolucion) {
        this.tiempoDevolucion = tiempoDevolucion;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public Integer getTiempoEntreCuotas() {
        return tiempoEntreCuotas;
    }

    public void setTiempoEntreCuotas(Integer tiempoEntreCuotas) {
        this.tiempoEntreCuotas = tiempoEntreCuotas;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ParametroPrestamo other = (ParametroPrestamo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + "}";
    }

}
