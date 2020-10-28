///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.informe;
//
//import isd.general.excepciones.ExcepcionGeneralSistema;
//import isd.general.util.InformeBase;
//import isd.general.util.JsfUtil;
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
//public class SeguimientoNroSerieBean extends InformeBase {
//      
//    private Integer nroSerie;
//
//    @ManagedProperty(value = "#{buscadorProductoBean}")
//    protected BuscadorProductoBean buscadorProductoBean;
//
//
//    /** Creates a new instance of ImpresionComprobanteProduccionBean */
//    public SeguimientoNroSerieBean() {
//        
//
//    }
//
//    @PostConstruct
//    public void init(){
//
//        titulo = "Seguimiento de número de serie";
//        pathReporte = "/isd/reporte/produccion/SEGSER.jasper";
//        nombreArchivo="SEGUIMIENTO-SERIE";
//
//        JsfUtil.addInfoMessage("Ingrese el número de seria a buscar");
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
//        parameters.put("NROSER", nroSerie);
//        parameters.put("CODPRD", "");
//
//        if(buscadorProductoBean.getProducto()!=null){
//            if(buscadorProductoBean.getProducto().getArtcod()!=null){
//                parameters.put("CODPRD", buscadorProductoBean.getProducto().getArtcod());
//            }
//        }
//    }
//
//    public Integer getNroSerie() {
//        return nroSerie;
//    }
//
//    public void setNroSerie(Integer nroSerie) {
//        this.nroSerie = nroSerie;
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
//    public BuscadorProductoBean getBuscadorProductoBean() {
//        return buscadorProductoBean;
//    }
//
//    public void setBuscadorProductoBean(BuscadorProductoBean buscadorProductoBean) {
//        this.buscadorProductoBean = buscadorProductoBean;
//    }
// 
//}
