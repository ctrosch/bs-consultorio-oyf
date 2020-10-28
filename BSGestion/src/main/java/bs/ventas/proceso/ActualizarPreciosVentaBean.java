/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.proceso;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import bs.proveedores.rn.ItemListaPrecioCostoRN;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.ProductoRN;
import bs.stock.web.ProductoBean;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.rn.ItemListaPrecioVentaRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ActualizarPreciosVentaBean extends GenericBean implements Serializable {

    @EJB
    private ItemListaPrecioVentaRN itemListaPrecioVentaRN;
    @EJB
    private ItemListaPrecioCostoRN itemListaPrecioCostoRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private ListaPrecioVentaRN listaRN;

    private ItemListaPrecioVenta itemListaPrecio;
    private List<ItemListaPrecioVenta> precios;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    private String parteDe;
    private ListaPrecioVenta listaVentaPartida;
    private ListaCosto listaCostoPartida;
    private ListaPrecioVenta listaVentaActualizar;

    private Date fechaVigencia;
    private BigDecimal porcentajeFijo;
    private BigDecimal valorFijo;
    private boolean aplicaUtilidad;

    private boolean actualizoPrecios;
    private String pathArchivoExcel;

    private Producto productoDesde;
    private Producto productoHasta;
    private TipoProducto tipoProducto;
    private Rubro01 rubro01Desde;
    private Rubro01 rubro01Hasta;
    private Rubro02 rubro02Desde;
    private Rubro02 rubro02Hasta;
    private EntidadComercial proveedorHabitual;

    /**
     * Creates a new instance of ActualizarPreciosCostoBean
     */
    public ActualizarPreciosVentaBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        cantidadRegistros = 5000;
        fechaVigencia = new Date();
        mostrarDebaja = false;
        itemListaPrecio = new ItemListaPrecioVenta();
    }

    public String onFlowProcess(FlowEvent event) {
        
        if (event.getNewStep().equals("inicio")) {
            resetParametros();
        }

        if (event.getOldStep().equals("inicio")) {

            if(!buscarItemsPrecio()){
                return event.getOldStep();
            }
        }

        if (event.getOldStep().equals("precios")) {

            actualizarDatos();
        }

        return event.getNewStep();
    }

    public boolean buscarItemsPrecio() {

        if (listaVentaActualizar == null) {
            JsfUtil.addErrorMessage("Seleccione la lista que desea actualizar");
            return false;
        }

        if (parteDe == null || parteDe.isEmpty()) {
            JsfUtil.addErrorMessage("Seleccione una opción para obtener los datos");
            return false;
        }

        if (parteDe.equals("A") && listaVentaPartida == null) {
            JsfUtil.addErrorMessage("Seleccione la lista de venta para obtener los datos");
            return false;
        }

        if (parteDe.equals("D") && listaCostoPartida == null) {
            JsfUtil.addErrorMessage("Seleccione la lista de costo para obtener los datos");
            return false;
        }

        if (parteDe.equals("C")) {
            
            if(pathArchivoExcel == null || pathArchivoExcel.isEmpty()){
                JsfUtil.addErrorMessage("Busque y suba el archivo excel a procesar");
                return false;
            }

            File archivo = new File(pathArchivoExcel);

            if (!archivo.isFile()) {
                JsfUtil.addErrorMessage("Busque y suba el archivo excel a procesar");
                return false;
            }

        }

        precios = new ArrayList<ItemListaPrecioVenta>();

        if (parteDe.equals("A")) {
            cargarFiltrosForItemLista();
            generarItemListaPrecioFromLista();

        }

        if (parteDe.equals("B")) {
            cargarFiltrosForCatalogo();
            generarItemListaPrecioFromCatalogo();

        }

        if (parteDe.equals("C")) {
            procesarArchivoExcel();
        }

        if (parteDe.equals("D")) {
            cargarFiltrosForItemLista();
            generarItemListaPrecioFromListaCosto();
        }

        actualizoPrecios = false;
        valorFijo = null;
        porcentajeFijo = null;
        fechaVigencia = new Date();
        
        return true;
    }

    public void cargarFiltrosForItemLista() {

        filtro.clear();

        if (productoDesde != null) {
            filtro.put("producto.codigo >=", "'" + productoDesde.getCodigo() + "'");
        }

        if (productoHasta != null) {
            filtro.put("producto.codigo <=", "'" + productoHasta.getCodigo() + "'");
        }

        if (tipoProducto != null) {
            filtro.put("producto.tipoProducto.codigo =", "'" + tipoProducto.getCodigo() + "'");
        }

        if (rubro01Desde != null) {
            filtro.put("producto.rubr01.codigo >=", "'" + rubro01Desde.getCodigo() + "'");
        }

        if (rubro01Hasta != null) {
            filtro.put("producto.rubr01.codigo <=", "'" + rubro01Hasta.getCodigo() + "'");
        }

        if (rubro02Desde != null) {
            filtro.put("producto.rubr02.codigo >=", "'" + rubro02Desde.getCodigo() + "'");
        }

        if (rubro02Hasta != null) {
            filtro.put("producto.rubr02.codigo <=", "'" + rubro02Hasta.getCodigo() + "'");
        }

        if (proveedorHabitual != null) {
            filtro.put("producto.proveedorHabitual.nrocta =", "'" + proveedorHabitual.getNrocta() + "'");
        }

    }

    public void cargarFiltrosForCatalogo() {

        filtro.clear();

        if (productoDesde != null) {
            filtro.put("codigo >=", "'" + productoDesde.getCodigo() + "'");
        }

        if (productoHasta != null) {
            filtro.put("codigo <=", "'" + productoHasta.getCodigo() + "'");
        }

        if (tipoProducto != null) {
            filtro.put("tipoProducto.codigo =", "'" + tipoProducto.getCodigo() + "'");
        }

        if (rubro01Desde != null) {
            filtro.put("rubr01.codigo >=", "'" + rubro01Desde.getCodigo() + "'");
        }

        if (rubro01Hasta != null) {
            filtro.put("rubr01.codigo <=", "'" + rubro01Hasta.getCodigo() + "'");
        }

        if (rubro02Desde != null) {
            filtro.put("rubr02.codigo >=", "'" + rubro02Desde.getCodigo() + "'");
        }

        if (rubro02Hasta != null) {
            filtro.put("rubr02.codigo <=", "'" + rubro02Hasta.getCodigo() + "'");
        }

        if (proveedorHabitual != null) {
            filtro.put("proveedorHabitual.nrocta =", "'" + proveedorHabitual.getNrocta() + "'");
        }
    }

    public void generarItemListaPrecioFromLista() {

        List<ItemListaPrecioVenta> preciosAux = itemListaPrecioVentaRN.getListaByBusqueda(listaVentaPartida, filtro, txtBusqueda, false, true, cantidadRegistros);

        for (ItemListaPrecioVenta p : preciosAux) {

            ItemListaPrecioVenta i = new ItemListaPrecioVenta();
            i.setCodlis(listaVentaActualizar.getCodigo());
            i.setListaDePrecio(listaVentaActualizar);
            i.setArtcod(p.getArtcod());
            i.setProducto(p.getProducto());

            i.setPrecioBase(p.getPrecio());
            i.setPrecioActual(p.getPrecio());
            i.setPrecio(p.getPrecio());

            if (p.getFechaVigencia().before(fechaVigencia)) {
                p.setFechaVigencia(fechaVigencia);
            } else {
                i.setFechaVigencia(new Date());
            }

            if (listaVentaActualizar.getPriorizaMonedaProducto().equals("S")) {
                i.setMonedaRegistracion(p.getProducto().getMonedaReposicion().getCodigo());
            } else {
                i.setMonedaRegistracion(listaVentaActualizar.getMoneda().getCodigo());
            }

            precios.add(i);
        }

    }

    public void generarItemListaPrecioFromListaCosto() {

        List<ItemListaPrecioCosto> preciosAux = itemListaPrecioCostoRN.getListaByBusqueda(listaCostoPartida, filtro, txtBusqueda, false, true, cantidadRegistros);

        for (ItemListaPrecioCosto p : preciosAux) {

            ItemListaPrecioVenta i = new ItemListaPrecioVenta();
            i.setCodlis(listaVentaActualizar.getCodigo());
            i.setListaDePrecio(listaVentaActualizar);
            i.setArtcod(p.getArtcod());
            i.setProducto(p.getProducto());

            i.setPrecioBase(p.getPrecio());
            i.setPrecioActual(itemListaPrecioVentaRN.getPrecioByProducto(i.getCodlis(), i.getArtcod(), i.getFechaVigencia()));
            i.setPrecio(p.getPrecioActual());

            if (p.getFechaVigencia().before(fechaVigencia)) {
                p.setFechaVigencia(fechaVigencia);
            } else {
                i.setFechaVigencia(new Date());
            }

            if (listaVentaActualizar.getPriorizaMonedaProducto().equals("S")) {
                i.setMonedaRegistracion(p.getProducto().getMonedaReposicion().getCodigo());
            } else {
                i.setMonedaRegistracion(listaVentaActualizar.getMoneda().getCodigo());
            }

            precios.add(i);
        }

    }

    public void generarItemListaPrecioFromCatalogo() {

        List<Producto> productos = productoRN.getListaByBusqueda(null, filtro, "", false, cantidadRegistros);

        for (Producto p : productos) {

            ItemListaPrecioVenta i = new ItemListaPrecioVenta();
            i.setCodlis(listaVentaActualizar.getCodigo());
            i.setListaDePrecio(listaVentaActualizar);
            i.setArtcod(p.getCodigo());
            i.setProducto(p);
            i.setFechaVigencia(fechaVigencia);

            i.setPrecioBase(p.getPrecioReposicion()!= null ? p.getPrecioReposicion() : BigDecimal.ZERO);
            i.setPrecioActual(itemListaPrecioVentaRN.getPrecioByProducto(i.getCodlis(), i.getArtcod(), i.getFechaVigencia()));
            i.setPrecio(i.getPrecioActual());

            if (listaVentaActualizar.getPriorizaMonedaProducto().equals("S")) {
                i.setMonedaRegistracion(p.getMonedaReposicion().getCodigo());
            } else {
                i.setMonedaRegistracion(listaVentaActualizar.getMoneda().getCodigo());
            }

            precios.add(i);
        }
    }

    public void procesarArchivoExcel() {

        try {
            FileInputStream file = new FileInputStream(new File(pathArchivoExcel));

            String[] split = pathArchivoExcel.split("\\.");
            String extension = split[split.length - 1].toLowerCase();

            if (extension.equals("xls")) {
                HSSFWorkbook workbook = new HSSFWorkbook(file);
                HSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                leerDatosExcel(rowIterator);
            } else if (extension.equals("xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                leerDatosExcel(rowIterator);
            } else {
                JsfUtil.addErrorMessage("Formato de archivo incorrecto. El archivo debe tener extensión xls o xlsx");
            }

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leerDatosExcel(Iterator<Row> rowIterator) {

        Row row;
        while (rowIterator.hasNext()) {

            row = rowIterator.next();
            Cell celda;

            String codigo;
            BigDecimal precio;

            try {
                codigo = row.getCell(0).getStringCellValue();
                precio = new BigDecimal(row.getCell(2).getNumericCellValue());
            } catch (Exception e) {
                continue;
            }

            if (codigo == null || codigo.isEmpty()) {
                continue;
            }

            Producto p = productoRN.getProducto(codigo);

            if (p == null) {
                continue;
            }

            ItemListaPrecioVenta i = new ItemListaPrecioVenta();
            i.setCodlis(listaVentaActualizar.getCodigo());
            i.setListaDePrecio(listaVentaActualizar);
            i.setArtcod(p.getCodigo());
            i.setProducto(p);
            i.setFechaVigencia(new Date());
            i.setPrecioBase(precio);
            i.setPrecio(precio);
            i.setPrecioActual(itemListaPrecioVentaRN.getPrecioByProducto(i.getCodlis(), i.getArtcod(), i.getFechaVigencia()));

            if (listaVentaActualizar.getPriorizaMonedaProducto().equals("S")) {
                i.setMonedaRegistracion(p.getMonedaReposicion().getCodigo());
            } else {
                i.setMonedaRegistracion(listaVentaActualizar.getMoneda().getCodigo());
            }

            precios.add(i);
        }
    }

    public void upload(FileUploadEvent event) {

        try {
            pathArchivoExcel = aplicacionBean.copiarArchivo(event.getFile().getFileName(), event.getFile().getInputstream());            
            JsfUtil.addInfoMessage("El archivo ha sido recibido con éxito, haga clic en \"Siguiente\" para ver los productos ");
        } catch (IOException e) {
            System.err.println("Error subiendo archivo " + e);
            JsfUtil.addErrorMessage("No es posible procesar el archivo");
        }
    }

    public void marcarModificado(ItemListaPrecioVenta i) {

        if (i.getPrecio().compareTo(listaVentaActualizar.getPrecioMinimo()) < 0) {
            i.setPrecio(listaVentaActualizar.getPrecioMinimo());
        }

        i.setModificado(true);
    }

    public void deshacerModificado(ItemListaPrecioVenta i) {

        i.setModificado(false);
        i.setPrecio(i.getPrecioActual());
    }

    public boolean actualizarDatos() {

        if (precios == null || precios.isEmpty()) {
            JsfUtil.addInfoMessage("No hay datos para actualizar");
            return false;
        }

        try {
            itemListaPrecioVentaRN.guardarLista(precios);
            actualizoPrecios = true;
            
            JsfUtil.addInfoMessage("Los datos se guardaron correctamente");
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos. ERROR: " + ex);
            return false;
        }
        
        return true;
    }

    public void guardarDatos() {

        try {
            if (listaVentaActualizar != null) {

                listaRN.guardar(listaVentaActualizar, false);
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void actualizarFechaVigencia() {

        if (fechaVigencia.before(new Date())) {
            fechaVigencia = new Date();
            JsfUtil.addErrorMessage("La fecha de vigencia no puede ser anterior a la fecha actual");
            return;
        }

        for (ItemListaPrecioVenta pr : precios) {

            pr.setFechaVigencia(fechaVigencia);
            pr.setModificado(true);
        }
    }

    public void calcularPorcentaje() {

        for (ItemListaPrecioVenta pr : precios) {

            BigDecimal nuevoPrecio = pr.getPrecioBase().add(pr.getPrecioBase().multiply(porcentajeFijo).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

            if (nuevoPrecio.compareTo(listaVentaActualizar.getPrecioMinimo()) < 0) {
                nuevoPrecio = listaVentaActualizar.getPrecioMinimo();
            }

            pr.setPrecio(nuevoPrecio);
            pr.setModificado(true);
        }
    }

    public void calcularImporte() {

        for (ItemListaPrecioVenta pr : precios) {

            BigDecimal nuevoPrecio = pr.getPrecioBase().add(valorFijo);

            if (nuevoPrecio.compareTo(listaVentaActualizar.getPrecioMinimo()) < 0) {
                nuevoPrecio = listaVentaActualizar.getPrecioMinimo();
            }

            pr.setPrecio(nuevoPrecio);
            pr.setModificado(true);
        }
    }

    public void aplicarUtilidad() {

        for (ItemListaPrecioVenta pr : precios) {

            if (pr.getProducto().getUtilidad() == null) {
                pr.getProducto().setUtilidad(BigDecimal.ZERO);
            }

            BigDecimal nuevoPrecio = pr.getPrecioBase().add(
                    pr.getPrecioBase().multiply(pr.getProducto().getUtilidad()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

            if (nuevoPrecio.compareTo(listaVentaActualizar.getPrecioMinimo()) < 0) {
                nuevoPrecio = listaVentaActualizar.getPrecioMinimo();
            }

            pr.setPrecio(nuevoPrecio);
            pr.setModificado(true);
        }
    }

    public void resetParametros() {

        listaVentaActualizar = null;
        listaVentaPartida = null;
        listaCostoPartida = null;
        parteDe = null;
        pathArchivoExcel = null;        
        
        tipoProducto = null;
        rubro01Desde = null;
        rubro01Hasta = null;
        rubro02Desde = null;
        rubro02Hasta = null;
        proveedorHabitual = null;
        
        valorFijo = null;
        porcentajeFijo = null;
        fechaVigencia = new Date();
        proveedorHabitual = null;
    }

    public void procesarProveedor() {

        if (entidadComercialBean.getEntidad() != null) {
            proveedorHabitual = entidadComercialBean.getEntidad();
        }
    }

    public ItemListaPrecioVenta getItemListaPrecio() {
        return itemListaPrecio;
    }

    public void setItemListaPrecio(ItemListaPrecioVenta itemListaPrecio) {
        this.itemListaPrecio = itemListaPrecio;
    }

    public List<ItemListaPrecioVenta> getPrecios() {
        return precios;
    }

    public void setPrecios(List<ItemListaPrecioVenta> precios) {
        this.precios = precios;
    }

    public ListaPrecioVenta getListaVentaPartida() {
        return listaVentaPartida;
    }

    public void setListaVentaPartida(ListaPrecioVenta listaVentaPartida) {
        this.listaVentaPartida = listaVentaPartida;
    }

    public ListaPrecioVenta getListaVentaActualizar() {
        return listaVentaActualizar;
    }

    public void setListaVentaActualizar(ListaPrecioVenta listaVentaActualizar) {
        this.listaVentaActualizar = listaVentaActualizar;
    }

    public String getPathArchivoExcel() {
        return pathArchivoExcel;
    }

    public void setPathArchivoExcel(String pathArchivoExcel) {
        this.pathArchivoExcel = pathArchivoExcel;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public String getParteDe() {
        return parteDe;
    }

    public void setParteDe(String parteDe) {
        this.parteDe = parteDe;
    }

    public Producto getProductoDesde() {
        return productoDesde;
    }

    public void setProductoDesde(Producto productoDesde) {
        this.productoDesde = productoDesde;
    }

    public Producto getProductoHasta() {
        return productoHasta;
    }

    public void setProductoHasta(Producto productoHasta) {
        this.productoHasta = productoHasta;
    }

    public Rubro01 getRubro01Desde() {
        return rubro01Desde;
    }

    public void setRubro01Desde(Rubro01 rubro01Desde) {
        this.rubro01Desde = rubro01Desde;
    }

    public Rubro01 getRubro01Hasta() {
        return rubro01Hasta;
    }

    public void setRubro01Hasta(Rubro01 rubro01Hasta) {
        this.rubro01Hasta = rubro01Hasta;
    }

    public Rubro02 getRubro02Desde() {
        return rubro02Desde;
    }

    public void setRubro02Desde(Rubro02 rubro02Desde) {
        this.rubro02Desde = rubro02Desde;
    }

    public Rubro02 getRubro02Hasta() {
        return rubro02Hasta;
    }

    public void setRubro02Hasta(Rubro02 rubro02Hasta) {
        this.rubro02Hasta = rubro02Hasta;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public BigDecimal getPorcentajeFijo() {
        return porcentajeFijo;
    }

    public void setPorcentajeFijo(BigDecimal porcentajeFijo) {
        this.porcentajeFijo = porcentajeFijo;
    }

    public BigDecimal getValorFijo() {
        return valorFijo;
    }

    public void setValorFijo(BigDecimal valorFijo) {
        this.valorFijo = valorFijo;
    }

    public boolean isActualizoPrecios() {
        return actualizoPrecios;
    }

    public void setActualizoPrecios(boolean actualizoPrecios) {
        this.actualizoPrecios = actualizoPrecios;
    }

    public boolean isAplicaUtilidad() {
        return aplicaUtilidad;
    }

    public void setAplicaUtilidad(boolean aplicaUtilidad) {
        this.aplicaUtilidad = aplicaUtilidad;
    }

    public ListaCosto getListaCostoPartida() {
        return listaCostoPartida;
    }

    public void setListaCostoPartida(ListaCosto listaCostoPartida) {
        this.listaCostoPartida = listaCostoPartida;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

}
