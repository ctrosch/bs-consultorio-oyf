/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.ProveedorBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CompraProveedorProducto extends InformeBase implements Serializable {

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{proveedorBean}")
    protected ProveedorBean proveedorBean;

    private Date fechaDesde;
    private Date fechaHasta;
    private Producto producto;
    private EntidadComercial proveedor;

    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public CompraProveedorProducto() {
    }

    @PostConstruct
    public void init() {

        fechaDesde = new Date();
        fechaHasta = new Date();

    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        String mensaje = "";
        todoOk = true;

        if (fechaDesde == null) {
            mensaje = "Fecha desde no puede estar en blanco";
        }

        if (fechaHasta == null) {
            mensaje = "Fecha hasta no puede estar en blanco";
        }

        if (fechaHasta.before(fechaDesde)) {
            mensaje = "Fecha desde es mayor a fecha hasta";
        }

        if (!mensaje.isEmpty()) {
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {

        parameters.put("FCHMOV_DESDE", fechaDesde);
        parameters.put("FCHMOV_HASTA", fechaHasta);

        if (proveedor != null) {
            parameters.put("NROCTA", proveedor.getNrocta());
        } else {
            parameters.put("NROCTA", "");
        }

        if (producto != null) {
            parameters.put("ARTCOD", producto.getCodigo());
        } else {
            parameters.put("ARTCOD", "");
        }

        nombreArchivo = "COM_PRODUCTO_PROVEEDOR";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "proveedor/informe/estadistica/COM_PRODUCTO_PROVEEDOR.jasper";

    }

    public void procesarProveedor() {

        if (proveedorBean.getEntidad() != null) {

            proveedor = proveedorBean.getEntidad();
            todoOk = false;
        }
    }

    public void procesarProducto() {

        if (productoBean.getProducto() != null) {

            producto = productoBean.getProducto();
            todoOk = false;

        }
    }

    @Override
    public void resetParametros() {

        fechaDesde = new Date();
        fechaHasta = new Date();
        proveedor = null;
        producto = null;
        todoOk = false;
        muestraReporte = false;

    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public ProveedorBean getEntidadComercialBean() {
        return proveedorBean;
    }

    public void setEntidadComercialBean(ProveedorBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

}
