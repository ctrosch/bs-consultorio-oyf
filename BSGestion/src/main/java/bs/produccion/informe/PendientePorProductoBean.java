///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.informe;
//
//import isd.general.excepciones.ExcepcionGeneralSistema;
//import isd.general.util.InformeBase;
//import isd.inventario.modelo.ST_Producto;
//import isd.inventario.web.BuscadorProductoBean;
//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.ViewScoped;
//
///**
// *
// * @author Claudio
// */
//@ManagedBean
//@ViewScoped
//public class PendientePorProductoBean extends InformeBase {
//
//    private Integer codigoProducto;
//    private ST_Producto producto;
//
//    @ManagedProperty(value = "#{buscadorProductoBean}")
//    protected BuscadorProductoBean buscadorProductoBean;
//    
//    /** Creates a new instance of ImpresionComprobanteProduccionBean */
//    public PendientePorProductoBean() {
//        
//        titulo = "Pendientes por producto";
//        pathReporte = "/isd/reporte/produccion/PENPRD.jasper";
//        nombreArchivo = "PENDIENTE-PRODUCTO";
//
//    }
//
//    @Override
//    public void validarDatos() throws ExcepcionGeneralSistema {
//        
//    }
//
//    @Override
//    public void cargarParametros() throws ExcepcionGeneralSistema {
//     
//        if(buscadorProductoBean.getProducto()!=null){
//
//            if(buscadorProductoBean.getProducto().getArtcod()!=null){
//                parameters.put("CODPRD", buscadorProductoBean.getProducto().getArtcod());
//            }else{
//                parameters.put("CODPRD", "");
//            }
//        }else{
//            parameters.put("CODPRD", "");
//        }
//    }
//
//    @PostConstruct
//    public void init(){
//
//    }
//
//    public String getTitulo() {
//        return titulo;
//    }
//
//    public void setTitulo(String titulo) {
//        this.titulo = titulo;
//    }
//
//    public Integer getCodigoProducto() {
//        return codigoProducto;
//    }
//
//    public void setCodigoProducto(Integer codigoProducto) {
//        this.codigoProducto = codigoProducto;
//    }
//
//    public BuscadorProductoBean getBuscadorProductoBean() {
//        return buscadorProductoBean;
//    }
//
//    public void setBuscadorProductoBean(BuscadorProductoBean buscadorProductoBean) {
//        this.buscadorProductoBean = buscadorProductoBean;
//    }
//
//    public ST_Producto getProducto() {
//        return producto;
//    }
//
//    public void setProducto(ST_Producto producto) {
//        this.producto = producto;
//    }
//    
//}
