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
//import bs.produccion.modelo.ComprobanteProduccion;
//import bs.produccion.rn.PDComprobantesRN;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//
///**
// *
// * @author Claudio
// */
//@ManagedBean
//@ViewScoped
//public class SeguimientoComprobante extends InformeBase {    
//    
//    @EJB private PDComprobantesRN comprobanteProduccionRN;
//    
//    private List<ComprobanteProduccion> comprobantes;
//    private ComprobanteProduccion comprobante;
//    private Integer nrofor;
//   
//
//    @PostConstruct
//    public void init(){
//
//        titulo = "Seguimiento de comprobantes";
//        pathReporte = "/isd/reporte/produccion/SEGCOM.jasper";
//        nombreArchivo = "SEGCOM";
//        comprobantes = comprobanteProduccionRN.getLista();
//
//        JsfUtil.addInfoMessage("Seleccione un comprobante");
//    }
//
//    @Override
//    public void validarDatos() throws ExcepcionGeneralSistema {
//
//        if(comprobante==null){
//            throw new ExcepcionGeneralSistema("Seleccione un comprobante");
//        }
//
//        if(nrofor<=0){
//            throw new ExcepcionGeneralSistema("Ingrese un número de comprobante válido");
//        }
//
//    }
//
//    @Override
//    public void cargarParametros() throws ExcepcionGeneralSistema {
//
//        parameters.put("MODFOR", comprobante.getModulo());
//        parameters.put("CODFOR", comprobante.getCodigo());
//        
//        if(nrofor!=null){
//            parameters.put("NROFOR", nrofor);
//        }else{
//            parameters.put("NROFOR", null);
//        }
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
//    public ComprobanteProduccion getComprobante() {
//        return comprobante;
//    }
//
//    public void setComprobante(ComprobanteProduccion comprobante) {
//        this.comprobante = comprobante;
//    }
//
//    public List<ComprobanteProduccion> getComprobantes() {
//        return comprobantes;
//    }
//
//    public void setComprobantes(List<ComprobanteProduccion> comprobantes) {
//        this.comprobantes = comprobantes;
//    }
//
//    public Integer getNrofor() {
//        return nrofor;
//    }
//
//    public void setNrofor(Integer nrofor) {
//        this.nrofor = nrofor;
//    }
//
//    public PDComprobantesRN getComprobanteProduccionRN() {
//        return comprobanteProduccionRN;
//    }
//
//    public void setComprobanteProduccionRN(PDComprobantesRN comprobanteProduccionRN) {
//        this.comprobanteProduccionRN = comprobanteProduccionRN;
//    }
//
//    
//    
//}
