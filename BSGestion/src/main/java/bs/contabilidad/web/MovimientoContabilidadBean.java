/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.ItemMovimientoContabilidad;
import bs.contabilidad.modelo.ItemMovimientoContabilidadCentroCosto;
import bs.contabilidad.modelo.ItemMovimientoContabilidadSubcuenta;
import bs.contabilidad.modelo.MascaraContabilidad;
import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.ContabilidadRN;
import bs.contabilidad.rn.CuentaContableRN;
import bs.contabilidad.rn.PeriodoContableRN;
import bs.contabilidad.rn.SubCuentaRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoContabilidadBean extends GenericBean implements Serializable {

    private @EJB
    ContabilidadRN contabilidadRN;
    private @EJB
    PeriodoContableRN ejercicioRN;
    private @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    private @EJB
    CuentaContableRN cuentaContableRN;

    @EJB
    private SubCuentaRN subCuentaRN;

    @EJB
    private MailFactory mailFactoryBean;

    //Datos inicializacion registracion
    protected String SUCCG = "";
    protected String CODCG = "";

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected List<MovimientoContabilidad> movimientos;

    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{cuentaContableBean}")
    protected CuentaContableBean cuentaContableBean;

    @ManagedProperty(value = "#{formularioContabilidadBean}")
    protected FormularioContabilidadBean formularioContabilidadBean;

    protected MovimientoContabilidad m;
    protected MovimientoContabilidad mReversion;
    protected MovimientoContabilidad mBusqueda;
    protected ItemMovimientoContabilidad item;

    private CentroCosto centroCosto;

    public MovimientoContabilidadBean() {

        titulo = "Movimiento de Contabilidad";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                formulario = new Formulario();
                iniciarMovimiento();
                modoVista = "D";
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void iniciarMovimiento() {

        super.iniciar();

        try {

            nombreArchivo = "";
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = contabilidadRN.nuevoMovimiento(CODCG, SUCCG);
            cargarFormulariosBusqueda();

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Error: iniciarMovimiento: " + ex);

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error: iniciarMovimiento: " + ex);
        }
    }

    public void nuevo() {

        m = null;
        iniciarMovimiento();
        esNuevo = true;
        modoVista = "D";

    }

    public void nuevoItem() {

        try {

            cuentaContableBean.setCuentaContable(null);
            //Cargarmos un nuevo item en blanco
            m.getItemsDetalle().add(contabilidadRN.nuevoItemMovimientoContabilidad(m));

        } catch (ExcepcionGeneralSistema ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            m = contabilidadRN.guardar(m);

            if (m.getComprobante().getTipoComprobante().equals("R")) {

                mBusqueda.setMovimientoReversion(m);
                contabilidadRN.guardar(mBusqueda);
            }

            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            cuentaContableBean.setCuentaContable(null);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void eliminarItem(ItemMovimientoContabilidad nItem) {

        try {
            contabilidadRN.eliminarItemMovimientoContabilidad(m, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (nItem.getCuentaContable() != null ? nItem.getCuentaContable().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + nItem.getCuentaContable().getDescripcion() + " " + ex);
        }
    }

    public void seleccionar(MovimientoContabilidad m) {
        if (m == null) {
            return;
        }

        this.m = m;
        modoVista = "D";
    }

    public void cargarImputacionCentroCosto() {

        if (m != null) {

            try {
                contabilidadRN.cargarImputacionCentroCosto(m);

            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("No es posible asignar cuenta contable al item " + ex);
            }
        }
    }

    public void procesarCuentaContable() {

        if (cuentaContableBean.getCuentaContable() != null && m != null && item != null) {

            try {

                CuentaContable cuentaContable = cuentaContableBean.getCuentaContable();
                contabilidadRN.asignarCuentaContable(item, cuentaContable);

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar cuenta contable al item " + ex);
            }
        }
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoContabilidad movimiento) {

        m = movimiento;
        imprimir();
    }

    public void generarReporte() {

        try {

            Map parameters = new HashMap();

            if (m == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - No existe movimiento seleccionado");
            }

            if (m.getFormulario().getReporte() == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de contabilidad no tienen reporte asociado");
            }

            if (m.getPuntoVenta().getUnidadNegocio().getLogo() != null && !m.getPuntoVenta().getUnidadNegocio().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + m.getPuntoVenta().getUnidadNegocio().getLogo()));
            }

            parameters.put("ID", m.getId());
            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);

            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + ex);
            muestraReporte = false;
        }
    }

    public void preparoEnvioNotificaciones() {

        if (m == null) {
            JsfUtil.addWarningMessage("No hay comprobante seleccionado para enviar por e-mail");
            return;
        }

        solicitaEmail = true;
        emailEnvioComprobante = "";
        informacionAdicional = "";

        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }

    public void nuevaBusqueda() {

        if (m != null && m.getFormulario() != null) {
            formulario = m.getFormulario();
        }
        modoVista = "B";
    }

    public void revertirMovimiento(MovimientoContabilidad mSel) {
        try {

            mReversion = mSel;
            m = contabilidadRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            buscarMovimiento();
            nuevo();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void resetParametros() {

//        formulario = null;
        mBusqueda = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        indiceWizard = 0;
    }

    public void buscarMovimiento() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = contabilidadRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public boolean validarParametros() {

        if (formulario == null) {
            JsfUtil.addWarningMessage("Seleccione un formulario");
            return false;
        }

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        filtro.put("movimientoReversion", " IS NULL");

        if (formulario != null) {
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }

        if (seleccionaPendiente) {
            //filtro.put("itemsCuentaContable.")
        }
    }

    public void seleccionarMovimiento(MovimientoContabilidad mSel) {

        try {
            m = mSel;
            contabilidadRN.calcularImportes(mSel);
            modoVista = "D";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void procesarFormulario() {

        if (formularioContabilidadBean.getFormulario() != null) {
            formulario = formularioContabilidadBean.getFormulario();
        }
    }

    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     */
    private void cargarFormulariosBusqueda() {

        if (m != null) {
            formularioContabilidadBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getComprobante()));
        }
    }

    public void calcularImportes() {

        contabilidadRN.calcularImportes(m);
    }

    public void onMascaraSelect(SelectEvent event) {

        try {
            MascaraContabilidad mascara = (MascaraContabilidad) event.getObject();

            contabilidadRN.asignarMascara(m, mascara);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public void onFechaSelect(SelectEvent event) {

        try {

            contabilidadRN.asignarPeriodoContable(m);

            if (m.getPeriodoContable() == null) {
                JsfUtil.addErrorMessage("No existe un período contable activo para la fecha seleccionada, no podrá guardar el comprobante");
            }

        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoContabilidadBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Problemas para asignar período contable");
        }

    }

    public void onRowToggle(ToggleEvent event) {

        centroCosto = ((ItemMovimientoContabilidadCentroCosto) event.getData()).getCentroCosto();
    }

    public List<SubCuenta> completeSubCuenta(String query) {

        try {
            return subCuentaRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }
    }

    public void nuevoItemSubCuenta(ItemMovimientoContabilidadCentroCosto cc) {

        try {
            contabilidadRN.nuevoItemMovimientoSubCuenta(cc);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item Sub Cuenta " + ex);
        }
    }

    public void eliminarItemSubCuenta(ItemMovimientoContabilidadCentroCosto itemCentroCosto, ItemMovimientoContabilidadSubcuenta itemSubCuenta) {

        try {
            contabilidadRN.eliminarItemSubCuenta(itemCentroCosto, itemSubCuenta);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible quitar item Sub Cuenta " + ex);
        }
    }

    public void importarExcel(FileUploadEvent event) {

        try {
            Iterator<Row> rowIterator = procesarArchivoExcel(event);

            int numeroRow = 0;

            if (rowIterator != null) {

                Row row;
                while (rowIterator.hasNext()) {

                    numeroRow++;

//                    System.err.println("Fila Numero " + numeroRow);
                    if (numeroRow == 1) {
//                        System.err.println("Primera fila, sale");
                        continue;
                    }

                    row = rowIterator.next();

                    String cuenta = "";
                    double importeDebe;
                    double importeHaber;

                    try {
                        cuenta = (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING ? row.getCell(0).getStringCellValue() : String.valueOf(row.getCell(0).getNumericCellValue()));

                        if (cuenta == null || cuenta.isEmpty()) {
//                            System.err.println("cuenta vacía");
                            continue;
                        }

//                        System.err.println("cell 0 " + (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING ? row.getCell(0).getStringCellValue() : String.valueOf(row.getCell(0).getNumericCellValue())));
//                        System.err.print(" cell 2 " + (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC ? row.getCell(2).getNumericCellValue() : Double.valueOf(row.getCell(2).getStringCellValue())));
//                        System.err.print(" cell 3 " + (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC ? row.getCell(3).getNumericCellValue() : Double.valueOf(row.getCell(3).getStringCellValue())));
                        importeDebe = (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC ? row.getCell(2).getNumericCellValue() : Double.valueOf(row.getCell(2).getStringCellValue()));
                        importeHaber = (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC ? row.getCell(3).getNumericCellValue() : Double.valueOf(row.getCell(3).getStringCellValue()));
                    } catch (Exception e) {

//                        System.err.println("sale por acá " + e);
                        continue;
                    }

//                    System.err.println("cuenta " + cuenta + " debe " + importeDebe + " haber " + importeHaber);
                    CuentaContable cuentaContable = cuentaContableRN.getCuentaContable(cuenta.replace(".", ""));

                    if (cuentaContable == null) {
//                        System.err.println("No encontró cuenta");
                        continue;
                    }

                    item = contabilidadRN.nuevoItemMovimientoContabilidad(m);

                    item.setCuentaContable(cuentaContable);

                    if (importeDebe > 0) {

                        item.setDebeHaber("D");
                        item.setImporteDebe(importeDebe);
                        item.setImporteHaber(0);
                    }

                    if (importeHaber > 0) {

                        item.setDebeHaber("H");
                        item.setImporteDebe(0);
                        item.setImporteHaber(importeHaber);
                    }

                    m.getItemsDetalle().add(item);
                }

//                System.err.println("items generados " + m.getItemsDetalle());
            } else {
                JsfUtil.addErrorMessage("No se encontraron datos para procesar");
            }
        } catch (IOException ex) {
            Logger.getLogger(MovimientoContabilidadBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible procesar el archivo " + ex);
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoContabilidadBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible procesar el archivo " + ex);
        }
    }

//------------------------------------------------------------------------------------------------------------
    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public String getSUCCG() {
        return SUCCG;
    }

    public void setSUCCG(String SUCCG) {
        this.SUCCG = SUCCG;
    }

    public String getCODCG() {
        return CODCG;
    }

    public void setCODCG(String CODCG) {
        this.CODCG = CODCG;
    }

    public List<MovimientoContabilidad> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoContabilidad> movimientos) {
        this.movimientos = movimientos;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public CuentaContableBean getCuentaContableBean() {
        return cuentaContableBean;
    }

    public void setCuentaContableBean(CuentaContableBean cuentaContableBean) {
        this.cuentaContableBean = cuentaContableBean;
    }

    public FormularioContabilidadBean getFormularioContabilidadBean() {
        return formularioContabilidadBean;
    }

    public void setFormularioContabilidadBean(FormularioContabilidadBean formularioContabilidadBean) {
        this.formularioContabilidadBean = formularioContabilidadBean;
    }

    public MovimientoContabilidad getM() {
        return m;
    }

    public void setM(MovimientoContabilidad m) {
        this.m = m;
    }

    public ItemMovimientoContabilidad getItem() {
        return item;
    }

    public void setItem(ItemMovimientoContabilidad item) {
        this.item = item;
    }

    public MovimientoContabilidad getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoContabilidad mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoContabilidad getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoContabilidad mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

}
