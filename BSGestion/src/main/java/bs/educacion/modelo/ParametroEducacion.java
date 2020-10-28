/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import bs.global.modelo.Periodo;
import java.io.Serializable;
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
 * @author ctrosch
 */
@Entity
@Table(name = "ed_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroEducacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", length = 2)
    private String id;

    @JoinColumn(name = "CODEST", referencedColumnName = "CODIGO")
    @ManyToOne
    private EstadoEducacion estado;

    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial alumnoPredeterminado;

    @Column(name = "IMPRCV", precision = 15, scale = 4)
    private double importeRecargoCuotaVencida;

    @Column(name = "PORINT", precision = 15, scale = 4)
    private double porcentajeInteres;

    @Column(name = "IMPREI", precision = 15, scale = 4)
    private double importeReinscripcion;

    @Column(name = "CNTCVR")
    private int cantidadCuotasVencidasParaReinscripcion;

    @JoinColumn(name = "PERIOD", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Periodo periodo;

    @Column(name = "VTODIP")
    private int diaVencimientoSegunPeriodo;

    @Column(name = "OFCVTO")
    private String origenFechaCalculoVencimiento;

    @Embedded
    private Auditoria auditoria;

    public ParametroEducacion() {
    }

    public ParametroEducacion(String codigo) {
        this.auditoria = new Auditoria();
        this.id = codigo;
        this.cantidadCuotasVencidasParaReinscripcion = 2;
        this.diaVencimientoSegunPeriodo = 10;
        this.importeRecargoCuotaVencida = 150;
        this.importeReinscripcion = 200;
        this.origenFechaCalculoVencimiento = "I";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public EntidadComercial getAlumnoPredeterminado() {
        return alumnoPredeterminado;
    }

    public void setAlumnoPredeterminado(EntidadComercial alumnoPredeterminado) {
        this.alumnoPredeterminado = alumnoPredeterminado;
    }

    public double getImporteRecargoCuotaVencida() {
        return importeRecargoCuotaVencida;
    }

    public void setImporteRecargoCuotaVencida(double importeRecargoCuotaVencida) {
        this.importeRecargoCuotaVencida = importeRecargoCuotaVencida;
    }

    public double getImporteReinscripcion() {
        return importeReinscripcion;
    }

    public void setImporteReinscripcion(double importeReinscripcion) {
        this.importeReinscripcion = importeReinscripcion;
    }

    public int getCantidadCuotasVencidasParaReinscripcion() {
        return cantidadCuotasVencidasParaReinscripcion;
    }

    public void setCantidadCuotasVencidasParaReinscripcion(int cantidadCuotasVencidasParaReinscripcion) {
        this.cantidadCuotasVencidasParaReinscripcion = cantidadCuotasVencidasParaReinscripcion;
    }

    public EstadoEducacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoEducacion estado) {
        this.estado = estado;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public int getDiaVencimientoSegunPeriodo() {
        return diaVencimientoSegunPeriodo;
    }

    public void setDiaVencimientoSegunPeriodo(int diaVencimientoSegunPeriodo) {
        this.diaVencimientoSegunPeriodo = diaVencimientoSegunPeriodo;
    }

    public String getOrigenFechaCalculoVencimiento() {
        return origenFechaCalculoVencimiento;
    }

    public void setOrigenFechaCalculoVencimiento(String origenFechaCalculoVencimiento) {
        this.origenFechaCalculoVencimiento = origenFechaCalculoVencimiento;
    }

    public double getPorcentajeInteres() {
        return porcentajeInteres;
    }

    public void setPorcentajeInteres(double porcentajeInteres) {
        this.porcentajeInteres = porcentajeInteres;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParametroEducacion other = (ParametroEducacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "tv.global.modelo.Parametro[usrprmttvid=" + id + "]";
    }

}
