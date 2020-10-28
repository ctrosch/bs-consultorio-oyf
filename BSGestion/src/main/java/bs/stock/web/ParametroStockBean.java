package bs.stock.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.global.modelo.Concepto;
import bs.global.rn.ConceptoRN;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.stock.modelo.ParametroStock;
import bs.stock.rn.ParametroStockRN;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class ParametroStockBean extends GenericBean implements Serializable {

    @EJB
    private ParametroStockRN parametrosRN;
    @EJB
    private ConceptoRN conceptoRN;

    private ParametroStock parametro;

    private List<Concepto> conceptosVenta;
    private List<Concepto> conceptosCompra;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    /**
     * Creates a new instance of ParametrosBean
     */
    public ParametroStockBean() {

    }

    @PostConstruct
    private void init() {
        try {
            parametro = parametrosRN.getParametro();
            conceptosVenta = conceptoRN.getListaByBusqueda("VT", "A", "", false, 100);
            conceptosCompra = conceptoRN.getListaByBusqueda("PV", "A", "", false, 100);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String verParametros() {
        return "../stock/tablas/parametroStock.xhtml";
    }

    public void guardar() {
        try {
            parametrosRN.guardar(parametro);
            aplicacionBean.actualizarDatos();
            JsfUtil.addInfoMessage("Lo datos se guardaron correctamente");
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al guardar datos", ex);
        }
    }

    public void limpiarCodigo() {

        parametro.setRubr01(null);
        parametro.setRubr02(null);
    }

    public ParametroStock getParametro() {
        return parametro;
    }

    public void setParametro(ParametroStock parametro) {
        this.parametro = parametro;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public List<Concepto> getConceptosVenta() {
        return conceptosVenta;
    }

    public void setConceptosVenta(List<Concepto> conceptosVenta) {
        this.conceptosVenta = conceptosVenta;
    }

    public List<Concepto> getConceptosCompra() {
        return conceptosCompra;
    }

    public void setConceptosCompra(List<Concepto> conceptosCompra) {
        this.conceptosCompra = conceptosCompra;
    }

}
