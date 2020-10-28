/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web.informe;

import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.rn.FacturacionRN;
import bs.facturacion.web.FormularioFacturacionBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class SeguimientoComprobanteFacturacionBean extends InformeBase implements Serializable {

    @EJB
    FormularioRN formularioRN;
    @EJB
    FacturacionRN facturacionRN;

    protected String MODFOR = "";
    protected String CODFOR = "";
    protected Integer NROFOR = 0;

    private SimpleDateFormat sdf;

    private Integer numeroFormulario;
    private MovimientoFacturacion movimiento;
    private TreeNode rootMovimiento;

    @ManagedProperty(value = "#{formularioFacturacionBean}")
    protected FormularioFacturacionBean formularioFacturacionBean;

    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public SeguimientoComprobanteFacturacionBean() {
    }

    @PostConstruct
    @Override
    public void init() {

        super.init();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        buscaMovimiento = true;
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " IN ('FC','VT','ST')");

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                MODFOR = (MODFOR == null ? "" : MODFOR);
                CODFOR = (CODFOR == null ? "" : CODFOR);
                NROFOR = (NROFOR == null ? 0 : NROFOR);

                if (!MODFOR.isEmpty() && !CODFOR.isEmpty() && NROFOR != null && NROFOR > 0) {

                    formulario = formularioRN.getFormulario(MODFOR, CODFOR);
                    numeroFormulario = NROFOR;
                    buscaMovimiento = false;
                    verReporte();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("iniciarVariables: " + ex);
        }
    }

    @Override
    public void verReporte() {

        try {
            validarDatos();
            cargarParametros();
            movimiento = facturacionRN.generarSeguimiento(movimiento);
            generarArbolSeguimiento();

            muestraReporte = true;
            todoOk = true;

        } catch (ExcepcionGeneralSistema e) {
//            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede ejecutar reporte pdf " + e.getMessage());
            todoOk = false;
            muestraReporte = false;
        }
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("Seleccione un formulario");
        }

        if (formulario.getReporte() == null) {
            throw new ExcepcionGeneralSistema("El formulario seleccionado no tiene asignado reporte");
        }

        if (numeroFormulario == null || numeroFormulario <= 0) {
            throw new ExcepcionGeneralSistema("Ingrese un número de comprobante válido");
        }

        movimiento = facturacionRN.getMovimiento(formulario.getCodigo(), numeroFormulario);

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + formulario.getCodigo() + " nro " + numeroFormulario + " no existe");
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {

        parameters.put("ID", movimiento.getId());

        if (copias != null && copias > 0) {

            if (copias > 4) {
                parameters.put("CANT_COPIAS", 4);
            } else {
                parameters.put("CANT_COPIAS", copias);
            }
        } else {
            parameters.put("CANT_COPIAS", movimiento.getComprobante().getCopias());
        }

        nombreArchivo = movimiento.getFormulario().getCodigo() + "-" + movimiento.getNumeroFormulario();
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = formulario.getReporte().getPath();

    }

    public void generarArbolSeguimiento() {

        if (movimiento == null) {
            return;
        }

        String formularioRaiz = movimiento.getFormulario().getDescripcion() + " - " + movimiento.getNumeroFormulario();

        rootMovimiento = new DefaultTreeNode(formularioRaiz, null);
        rootMovimiento.setExpanded(true);

        for (ItemMovimientoFacturacion ip : movimiento.getItemsProducto()) {

            obtenerComprobantes(formularioRaiz, rootMovimiento, ip.getItemsAplicacion());
        }
    }

    public void obtenerComprobantes(String formularioRaiz, TreeNode raiz, List<ItemMovimientoFacturacion> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        for (ItemMovimientoFacturacion ia : items) {

            String formularioHoja = ia.getMovimiento().getFormulario().getDescripcion() + " - " + ia.getMovimiento().getNumeroFormulario();

            TreeNode hoja = getHoja(raiz, formularioHoja);

            if (hoja == null) {
                hoja = new DefaultTreeNode(formularioHoja, raiz);
                hoja.setExpanded(true);
            }
            obtenerComprobantes(formularioHoja, hoja, ia.getItemsAplicacion());
        }
    }

    public TreeNode getHoja(TreeNode raiz, String informacion) {

        if (raiz.getData().equals(informacion)) {
            return raiz;
        } else {

            if (raiz.getChildren() == null) {
                return raiz;
            }

            for (TreeNode n : raiz.getChildren()) {
                return getHoja(n, informacion);
            }
        }

        return null;
    }

    public List<Formulario> completeFormulario(String query) {
        try {
            return formularioRN.getFormularioByBusqueda(filtro, query, false);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "complete", ex);
            return new ArrayList<Formulario>();
        }
    }

    public void onItemSelect(SelectEvent event) {
        formulario = (Formulario) event.getObject();
    }

    public void procesarFormulario() {

        if (formularioFacturacionBean.getFormulario() != null) {
            formulario = formularioFacturacionBean.getFormulario();
        }
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {

        todoOk = false;
        muestraReporte = false;
        solicitaEmail = false;
        emailEnvioComprobante = "";
        formulario = null;
        numeroFormulario = null;
        movimiento = null;
    }

    //--------------------------------------------------------------------------
    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public MovimientoFacturacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoFacturacion movimiento) {
        this.movimiento = movimiento;
    }

    public FormularioRN getFormularioRN() {
        return formularioRN;
    }

    public void setFormularioRN(FormularioRN formularioRN) {
        this.formularioRN = formularioRN;
    }

    public FacturacionRN getFacturacionRN() {
        return facturacionRN;
    }

    public void setFacturacionRN(FacturacionRN facturacionRN) {
        this.facturacionRN = facturacionRN;
    }

    public FormularioFacturacionBean getFormularioFacturacionBean() {
        return formularioFacturacionBean;
    }

    public void setFormularioFacturacionBean(FormularioFacturacionBean formularioFacturacionBean) {
        this.formularioFacturacionBean = formularioFacturacionBean;
    }

    public TreeNode getRootMovimiento() {
        return rootMovimiento;
    }

    public void setRootMovimiento(TreeNode rootMovimiento) {
        this.rootMovimiento = rootMovimiento;
    }

    public String getMODFOR() {
        return MODFOR;
    }

    public void setMODFOR(String MODFOR) {
        this.MODFOR = MODFOR;
    }

    public String getCODFOR() {
        return CODFOR;
    }

    public void setCODFOR(String CODFOR) {
        this.CODFOR = CODFOR;
    }

    public Integer getNROFOR() {
        return NROFOR;
    }

    public void setNROFOR(Integer NROFOR) {
        this.NROFOR = NROFOR;
    }

}
