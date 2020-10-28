/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Concepto;
import bs.global.rn.ConceptoRN;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.stock.modelo.AplicacionProducto;
import bs.stock.modelo.Atributo;
import bs.stock.modelo.AtributoDefecto;
import bs.stock.modelo.AtributoProducto;
import bs.stock.modelo.EquivalenciaProveedor;
import bs.stock.modelo.GrupoProducto;
import bs.stock.modelo.GrupoStock;
import bs.stock.modelo.ImagenProducto;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.ProductoPresentacion;
import bs.stock.modelo.ProductoSugerido;
import bs.stock.modelo.ProductoSustituto;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.Stock;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.AtributoValorRN;
import bs.stock.rn.ParametroStockRN;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.stock.rn.TipoProductoRN;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.rn.ItemListaPrecioVentaRN;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ProductoBean extends GenericBean implements Serializable {

    @EJB
    private ProductoRN productoRN;
    @EJB
    private ParametroStockRN parametroStockRN;
    @EJB
    private TipoProductoRN tipoProductoRN;
    @EJB
    private AtributoValorRN atributoValorRN;
    @EJB
    private ConceptoRN conceptoRN;
    @EJB
    private StockRN stockRN;
    @EJB
    private ItemListaPrecioVentaRN itemListaPrecioVentaRN;

    private ParametroStock parametroStock;

    private Producto producto;
    private List<Producto> lista;
    private List<Producto> productosSeleccionados;

    private List<TipoProducto> tipos;
    private List<Rubro01> rubros1;
    private List<Rubro02> rubros2;
    private List<Rubro03> rubros3;

    private String CODIGO;

    //Para filtrar
    private TipoProducto tipoProducto;
    private Rubro01 rubro01;
    private Rubro02 rubro02;
    private Rubro03 rubro03;
    private EntidadComercial proveedorHabitual;

    private List<Concepto> conceptosVenta;
    private List<Concepto> conceptosProveedor;
    private ImagenProducto imagenProducto;

    // para consulta completa de productos
    private List<ItemListaPrecioVenta> precios;
    private List<Stock> stock;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    /**
     * Creates a new instance of ProductoBean
     */
    public ProductoBean() {

    }

    @PostConstruct
    public void init() {

        tipos = tipoProductoRN.getLista(false);
        productosSeleccionados = new ArrayList<>();
        buscar();

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                parametroStock = parametroStockRN.getParametro();
                tipos = tipoProductoRN.getLista(false);
                conceptosVenta = conceptoRN.getListaByBusqueda("VT", "A", "", false, 100);
                conceptosProveedor = conceptoRN.getListaByBusqueda("PV", "A", "", false, 100);

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(productoRN.getProducto(CODIGO));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void setearTipo(String codTipo) {

        if (codTipo != null) {
            tipoProducto = tipoProductoRN.getTipoProducto(codTipo);
//           buscar();
        }
    }

    public void nuevo() {

        try {
            producto = productoRN.nuevo();
            cargarRubros();
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible crear un nuevo producto " + ex);
        }
    }

    public void guardar(boolean nuevo) {
        try {
            if (producto != null) {

                producto = productoRN.guardar(producto, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }

            } else {
                JsfUtil.addErrorMessage("No hay datos para guardar");
            }

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void duplicar(Producto p) {

        try {
            producto = productoRN.duplicar(p);
            JsfUtil.addInfoMessage("Se ha duplicado el producto");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible duplicar el producto" + ex);
        }

    }

    public void habilitaDeshabilita(String habDes) {

        try {
            producto.getAuditoria().setDebaja(habDes);
            productoRN.guardar(producto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            productoRN.eliminar(producto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();
        lista = productoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (tipoProducto != null) {
            filtro.put("tipoProducto.codigo = ", "'" + tipoProducto.getCodigo() + "'");
        }

        if (rubro01 != null) {
            filtro.put("rubr01.codigo = ", "'" + rubro01.getCodigo() + "'");
        }

        if (rubro02 != null) {
            filtro.put("rubr02.codigo = ", "'" + rubro02.getCodigo() + "'");
        }

        if (rubro03 != null) {
            filtro.put("rubr03.codigo = ", "'" + rubro03.getCodigo() + "'");
        }

        if (proveedorHabitual != null) {
            filtro.put("proveedorHabitual.nrocta = ", "'" + proveedorHabitual.getNrocta() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        tipoProducto = null;
        rubro01 = null;
        rubro02 = null;
        rubro03 = null;
        proveedorHabitual = null;

        buscar();
    }

    public void onSelect(SelectEvent event) {

        producto = (Producto) event.getObject();
        esNuevo = false;
        modoVista = "D";
        cargarRubros();
    }

    public void onSelectCompleto(SelectEvent event) {

        producto = (Producto) event.getObject();
        esNuevo = false;
        modoVista = "D";
        cargarRubros();
        stock = stockRN.getStockByProducto(producto, true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(producto);
    }

    public void seleccionar(Producto e) {

        if (e == null) {
            return;
        }

        producto = e;
        esNuevo = false;
        modoVista = "D";
        cargarRubros();

    }

    public void seleccionarCompleto(Producto e) {

        producto = e;
        esNuevo = false;
        modoVista = "D";
        cargarRubros();
        stock = stockRN.getStockByProducto(producto, true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(producto);
    }

    public List<Producto> complete(String query) {
        try {
            return productoRN.getListaByBusqueda("", query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public void asignarCodigoProducto() {

        try {
            if (esNuevo) {
                productoRN.asignarCodigoProducto(producto);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código de producto " + ex);
        }
    }

    public void limpiarCodigo() {

        cargarRubros();

        if (esNuevo) {

            producto.setRubr01(null);
            producto.setRubr02(null);
            producto.setCodigo("");
        }
    }

    public void cargarRubros() {

        if (producto.getTipoProducto() != null) {
            rubros1 = producto.getTipoProducto().getRubro01();
            rubros2 = producto.getTipoProducto().getRubro02();
            rubros3 = producto.getTipoProducto().getRubro03();

            cargarAtributos();
        }
    }

    public void cargarAtributos() {

        if (producto.getAtributos() == null) {
            producto.setAtributos(new ArrayList<AtributoProducto>());
        }

        if (esNuevo) {
            producto.getAtributos().clear();
        }

        if (producto.getTipoProducto().getAtributos() == null || producto.getTipoProducto().getAtributos().isEmpty()) {
            return;
        }

        for (Atributo atrTipo : producto.getTipoProducto().getAtributos()) {

            boolean existe = false;

            for (AtributoProducto atrProducto : producto.getAtributos()) {

                if (atrTipo.getNombre().equals(atrProducto.getNombre())) {
                    existe = true;
                }
            }

            if (!existe) {
                try {
                    productoRN.nuevoItemAtributo(producto, atrTipo.getNombre());
                } catch (ExcepcionGeneralSistema ex) {
                    JsfUtil.addErrorMessage("Problemas para agregar atributos desde tipo de producto");
                }
            }

        }
    }

    @Override
    public void upload(FileUploadEvent event) {

        try {
            copiarArchivoImagen(event.getFile().getFileName(), event.getFile().getInputstream());
            JsfUtil.addInfoMessage("La imagen ha sido procesada con éxito");
        } catch (IOException e) {
            System.err.println("Error subiendo archivo " + e);
            JsfUtil.addErrorMessage("No es posible procesar el archivo");
        }
    }

    /**
     * Copiar un archivo a la carpeta temporales de la organizacion
     *
     * @param fileName
     * @param in
     * @return Path del archivo generado
     */
    public void copiarArchivoImagen(String fileName, InputStream in) {
        try {

//            if (imagenProducto == null) {
//                JsfUtil.addErrorMessage("No hay ningún item de imagen seleccionado");
//                return;
//            }
            nuevoItemImagen();

            ImagenProducto imagenProducto = producto.getImagenes().get(producto.getImagenes().size() - 1);

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");

            String[] split = fileName.split("\\.");
            String extension = split[split.length - 1].toLowerCase();
            String archivo = producto.getCodigo() + "_" + sdf.format(new Date()) + "_" + JsfUtil.getCadenaAlfanumAleatoria(5) + "." + extension;

            File file = new File(aplicacionBean.getParametro().getPathCarpetaProductos() + archivo);

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            imagenProducto.setUrlBase(aplicacionBean.getParametro().getUrlCarpetaProductos());
            imagenProducto.setArchivo(archivo);

            if (!esNuevo) {
                productoRN.guardar(producto, esNuevo);
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Error cargando imagen archivo: " + e);
        }
    }

    public void procesarProveedor() {

        if (producto != null && entidadComercialBean.getEntidad() != null) {
            producto.setProveedorHabitual(entidadComercialBean.getEntidad());
        }
    }

    //---------------------------------------------------------------------------
    //EQUIVALENCIAS PROVEEDOR
    //---------------------------------------------------------------------------
    public void nuevoItemEquivalenciaProveedor() {

        try {
            productoRN.nuevoItemEquivalenciaProveedor(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item equivalencia proveedor: " + ex);
        }
    }

    public void onEquivalenciaProveedorSelect(SelectEvent event) {

        try {
            EntidadComercial proveedor = (EntidadComercial) event.getObject();
            productoRN.asignarEntidadEquivalenciaProveedor(producto, proveedor);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar proveedor: " + ex);
        }
    }

    public void eliminarItemEquivalenciaProveedor(EquivalenciaProveedor itemEquivalenciaProveedor) {

        try {
            productoRN.eliminarItemEquivalenciaProveedor(producto, itemEquivalenciaProveedor);
            JsfUtil.addWarningMessage("Se ha borrado el item equivalencia " + (itemEquivalenciaProveedor.getProveedor() != null ? itemEquivalenciaProveedor.getProveedor().getRazonSocial() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemEquivalenciaProveedor.getProveedor() != null ? itemEquivalenciaProveedor.getProveedor().getRazonSocial() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //ATRIBUTOS
    //---------------------------------------------------------------------------
    public void nuevoItemAtributo() {

        try {
            productoRN.nuevoItemAtributo(producto, null);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item atributo: " + ex);
        }
    }

    public void eliminarItemAtributo(AtributoProducto itemAtributo) {

        try {
            productoRN.eliminarItemAtributo(producto, itemAtributo);
            JsfUtil.addWarningMessage("Se ha borrado el item atributo");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public List<String> atributoComplete(String query) {

        try {

            System.out.println("Query " + query);

            List<String> lista = new ArrayList<String>();

            lista.add("sddsfsdfs");
            lista.add("sddsfsdfs");
            lista.add("sddsfsdfs");

            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<String>();
        }
    }

    public List<String> atributoLista(String nombre) {

        try {

            List<String> lista = atributoValorRN.getListaByBusqueda(nombre, "", mostrarDebaja, 100);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<String>();
        }
    }

    //---------------------------------------------------------------------------
    //APLICACIONES
    //---------------------------------------------------------------------------
    public void nuevoItemAplicacion() {

        try {
            productoRN.nuevoItemAplicacion(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item aplicación: " + ex);
        }
    }

    public void eliminarItemAplicacion(AplicacionProducto itemAplicacion) {

        try {
            productoRN.eliminarItemAplicacion(producto, itemAplicacion);
            JsfUtil.addWarningMessage("Se ha borrado el item aplicación");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //SUGERIDOS
    //---------------------------------------------------------------------------
    public void nuevoItemSugerido() {

        try {
            productoRN.nuevoItemSugerido(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void onSugeridoSelect(SelectEvent event) {

        try {
            Producto productoSugerido = (Producto) event.getObject();
            productoRN.asignarProductoSugerido(producto, productoSugerido);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar producto sugerido: " + ex);
        }
    }

    public void eliminarItemSugerido(ProductoSugerido itemSugerido) {

        try {
            productoRN.eliminarItemSugerido(producto, itemSugerido);
            JsfUtil.addWarningMessage("Se ha borrado el item sugerido " + (itemSugerido.getProductoSugerido() != null ? itemSugerido.getProductoSugerido().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemSugerido.getProductoSugerido() != null ? itemSugerido.getProductoSugerido().getDescripcion() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //SUSTITUTOS
    //---------------------------------------------------------------------------
    public void nuevoItemSustituto() {

        try {
            productoRN.nuevoItemSustituto(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sustituto: " + ex);
        }
    }

    public void onSustitutoSelect(SelectEvent event) {

        try {
            Producto productoSustituto = (Producto) event.getObject();
            productoRN.asignarProductoSustituto(producto, productoSustituto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible asignar producto sustituto: " + ex);
        }
    }

    public void eliminarItemSustituto(ProductoSustituto itemSustituto) {

        try {
            productoRN.eliminarItemSustituto(producto, itemSustituto);
            JsfUtil.addWarningMessage("Se ha borrado el item sustituto " + (itemSustituto.getProductoSustituto() != null ? itemSustituto.getProductoSustituto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemSustituto.getProductoSustituto() != null ? itemSustituto.getProductoSustituto().getDescripcion() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //FAMILIAS
    //---------------------------------------------------------------------------
    public void nuevoItemGrupo() {

        try {

            productoRN.nuevoItemGrupo(producto);

        } catch (ExcepcionGeneralSistema ex) {

            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void onGrupoSelect(SelectEvent event) {

        try {

            GrupoStock grupo = (GrupoStock) event.getObject();
            productoRN.asignarGrupo(producto, grupo);

        } catch (ExcepcionGeneralSistema ex) {

            JsfUtil.addErrorMessage("No es posible asignar grupo: " + ex);
        }

    }

    public void eliminarItemGrupo(GrupoProducto itemGrupo) {

        try {
            productoRN.eliminarItemGrupo(producto, itemGrupo);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemGrupo.getGrupo() != null ? itemGrupo.getGrupo().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemGrupo.getGrupo() != null ? itemGrupo.getGrupo().getDescripcion() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //IMAGENES
    //---------------------------------------------------------------------------
    public void nuevoItemImagen() {

        try {

            productoRN.nuevoItemImagen(producto);

        } catch (ExcepcionGeneralSistema ex) {

            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void eliminarItemImagen(ImagenProducto itemImagen) {

        try {
            productoRN.eliminarItemImagen(producto, itemImagen);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemImagen != null ? itemImagen.getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemImagen != null ? itemImagen.getNombre() : "") + " " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //PRESENTACIONES
    //---------------------------------------------------------------------------
    public void nuevoItemPresentacion() {

        try {
            productoRN.nuevoItemPresentacion(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item presentación: " + ex);
        }
    }

    public void eliminarItemPresentacion(ProductoPresentacion itemPresentacion) {

        try {
            productoRN.eliminarItemPresentacion(producto, itemPresentacion);
            JsfUtil.addWarningMessage("Se ha borrado el item presentación");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    //---------------------------------------------------------------------------
    //ATRIBUTOS POR DEFECTO
    //---------------------------------------------------------------------------
    public void nuevoItemAtributoPorDefecto() {

        try {
            productoRN.nuevoItemAtributoPorDefecto(producto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item atributo por defecto: " + ex);
        }
    }

    public void eliminarItemAtributoPorDefecto(AtributoDefecto itemAtributoPorDefecto) {

        try {
            productoRN.eliminarItemAtributoPorDefecto(producto, itemAtributoPorDefecto);
            JsfUtil.addWarningMessage("Se ha borrado el item atributo por defecto");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    //---------------------------------------------------------------------------
    public List<Producto> getLista() {
        return lista;
    }

    public void setLista(List<Producto> lista) {
        this.lista = lista;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<TipoProducto> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoProducto> tipos) {
        this.tipos = tipos;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public List<Concepto> getConceptosVenta() {
        return conceptosVenta;
    }

    public void setConceptosVenta(List<Concepto> conceptosVenta) {
        this.conceptosVenta = conceptosVenta;
    }

    public List<Concepto> getConceptosProveedor() {
        return conceptosProveedor;
    }

    public void setConceptosProveedor(List<Concepto> conceptosProveedor) {
        this.conceptosProveedor = conceptosProveedor;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public List<ItemListaPrecioVenta> getPrecios() {
        return precios;
    }

    public void setPrecios(List<ItemListaPrecioVenta> precios) {
        this.precios = precios;
    }

    public List<Stock> getStock() {
        return stock;
    }

    public void setStock(List<Stock> stock) {
        this.stock = stock;
    }

    public ParametroStock getParametroStock() {
        return parametroStock;
    }

    public void setParametroStock(ParametroStock parametro) {
        this.parametroStock = parametro;
    }

    public List<Rubro01> getRubros1() {
        return rubros1;
    }

    public void setRubros1(List<Rubro01> rubros1) {
        this.rubros1 = rubros1;
    }

    public List<Rubro02> getRubros2() {
        return rubros2;
    }

    public void setRubros2(List<Rubro02> rubros2) {
        this.rubros2 = rubros2;
    }

    public List<Rubro03> getRubros3() {
        return rubros3;
    }

    public void setRubros3(List<Rubro03> rubros3) {
        this.rubros3 = rubros3;
    }

    public ImagenProducto getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(ImagenProducto imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public Rubro01 getRubro01() {
        return rubro01;
    }

    public void setRubro01(Rubro01 rubro01) {
        this.rubro01 = rubro01;
    }

    public Rubro02 getRubro02() {
        return rubro02;
    }

    public void setRubro02(Rubro02 rubro02) {
        this.rubro02 = rubro02;
    }

    public Rubro03 getRubro03() {
        return rubro03;
    }

    public void setRubro03(Rubro03 rubro03) {
        this.rubro03 = rubro03;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public List<Producto> getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public void setProductosSeleccionados(List<Producto> productosSeleccionados) {
        this.productosSeleccionados = productosSeleccionados;
    }

}
