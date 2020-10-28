/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Periodo;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
//@Entity
//@Table(name = "ed_matricula_item")
//@EntityListeners(AuditoriaListener.class)
public class ItemMatricula implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    MovimientoEducacion movimiento;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @Column(name = "CANTID")
    private int cantidad;

    @JoinColumn(name = "PERIOD", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Periodo periodo;

    @Column(name = "VTODIP")
    private int diaVencimientoSegunPeriodo;

    @Column(name = "OFCVTO")
    private String origenFechaCalculoVencimiento;  // M = Movimiento | I = Inicio Cursado

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

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private double porcentaTotalBonificacion;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

//    @OneToMany(mappedBy = "itemMovimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @OrderBy(value = "importe")
//    private List<AplicacionCuentaCorrienteEducacion> itemsCuentaCorriente;
    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemMatricula() {

        precio = 0;
        precioSecundario = 0;
        cantidad = 0;

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

    public MovimientoEducacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoEducacion movimiento) {
        this.movimiento = movimiento;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
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

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public String getOrigenFechaCalculoVencimiento() {
        return origenFechaCalculoVencimiento;
    }

    public void setOrigenFechaCalculoVencimiento(String origenFechaCalculoVencimiento) {
        this.origenFechaCalculoVencimiento = origenFechaCalculoVencimiento;
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
        final ItemMatricula other = (ItemMatricula) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoEducacion{" + "id=" + id + '}';
    }

}
