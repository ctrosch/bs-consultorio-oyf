/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.modelo;

import bs.contabilidad.modelo.CuentaContable;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "mt_movimiento_recurso")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoMantenimientoRecurso implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    MovimientoMantenimiento movimiento;

    @Column(name = "CANTID")
    private BigDecimal cantidad;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private double precio;

    @Column(name = "PRESEC", precision = 15, scale = 4)
    private double precioSecundario;

    //Importe nacional
    @Column(name = "IMPORT", precision = 15, scale = 2)
    private double importe;

    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private double importeSecundario;

    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;

    //Importe debe
    @Column(name = "IMPDEB", precision = 15, scale = 4)
    private double importeDebe;

    //Importe haber
    @Column(name = "IMPHAB", precision = 15, scale = 4)
    private double importeHaber;

    //Importe debe
    @Column(name = "DEBSEC", precision = 15, scale = 4)
    private double importeDebeSecundario;

    //Importe haber
    @Column(name = "HABSEC", precision = 15, scale = 4)
    private double importeHaberSecundario;

    //Importe haber
    @Column(name = "PCOSTO", precision = 15, scale = 4)
    private double precioCosto;

    //Importe haber
    @Column(name = "COSSEC", precision = 15, scale = 4)
    private double precioCostoSecundario;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private double porcentaTotalBonificacion;

    @JoinColumn(name = "CODACT", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Actividad actividad;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @Size(max = 120)
    @Column(name = "DES_AC", nullable = false)
    private String desccripcionActividad;

    @Column(name = "ID_REC", nullable = false)
    private int codigoRecurso;

    @Size(max = 120)
    @Column(name = "DES_RE", nullable = false)
    private String desccripcionRecurso;

    @Size(max = 6)
    @Column(name = "UNIMED", nullable = false)
    private String unidadMedida;

    @Column(name = "NORDAC", nullable = false)
    private int nroOrdenActividad;

//    @OneToMany(mappedBy = "itemMovimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @OrderBy(value = "importe")
//    private List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente;
    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemMovimientoMantenimientoRecurso() {

        precio = 0;
        precioSecundario = 0;
        cantidad = BigDecimal.ZERO;

        importe = 0;
        importeSecundario = 0;

        importeDebe = 0;
        importeDebeSecundario = 0;
        importeHaber = 0;
        importeHaberSecundario = 0;

        porcentaTotalBonificacion = 0;

        cotizacion = 1;

//        itemsCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteEducacion>();
        auditoria = new Auditoria();

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

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(double precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(double importeSecundario) {
        this.importeSecundario = importeSecundario;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public double getImporteDebe() {
        return importeDebe;
    }

    public void setImporteDebe(double importeDebe) {
        this.importeDebe = importeDebe;
    }

    public double getImporteHaber() {
        return importeHaber;
    }

    public void setImporteHaber(double importeHaber) {
        this.importeHaber = importeHaber;
    }

    public double getImporteDebeSecundario() {
        return importeDebeSecundario;
    }

    public void setImporteDebeSecundario(double importeDebeSecundario) {
        this.importeDebeSecundario = importeDebeSecundario;
    }

    public double getImporteHaberSecundario() {
        return importeHaberSecundario;
    }

    public void setImporteHaberSecundario(double importeHaberSecundario) {
        this.importeHaberSecundario = importeHaberSecundario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getPorcentaTotalBonificacion() {
        return porcentaTotalBonificacion;
    }

    public void setPorcentaTotalBonificacion(double porcentaTotalBonificacion) {
        this.porcentaTotalBonificacion = porcentaTotalBonificacion;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public MovimientoMantenimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoMantenimiento movimiento) {
        this.movimiento = movimiento;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public double getPrecioCostoSecundario() {
        return precioCostoSecundario;
    }

    public void setPrecioCostoSecundario(double precioCostoSecundario) {
        this.precioCostoSecundario = precioCostoSecundario;
    }

    public String getDesccripcionActividad() {
        return desccripcionActividad;
    }

    public void setDesccripcionActividad(String desccripcionActividad) {
        this.desccripcionActividad = desccripcionActividad;
    }

    public int getCodigoRecurso() {
        return codigoRecurso;
    }

    public void setCodigoRecurso(int codigoRecurso) {
        this.codigoRecurso = codigoRecurso;
    }

    public String getDesccripcionRecurso() {
        return desccripcionRecurso;
    }

    public void setDesccripcionRecurso(String desccripcionRecurso) {
        this.desccripcionRecurso = desccripcionRecurso;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public int getNroOrdenActividad() {
        return nroOrdenActividad;
    }

    public void setNroOrdenActividad(int nroOrdenActividad) {
        this.nroOrdenActividad = nroOrdenActividad;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

//    public List<AplicacionCuentaCorrienteEducacion> getItemsCuentaCorriente() {
//        return itemsCuentaCorriente;
//    }
//
//    public void setItemsCuentaCorriente(List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente) {
//        this.itemsCuentaCorriente = itemsCuentaCorriente;
//    }
    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final ItemMovimientoMantenimientoRecurso other = (ItemMovimientoMantenimientoRecurso) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoMantenimiento{" + "id=" + id + '}';
    }

}
