///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.informe;
//
//import isd.general.excepciones.ExcepcionGeneralSistema;
//import isd.general.util.InformeBase;
//import java.math.BigDecimal;
//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//
///**
// *
// * @author Claudio
// */
//@ManagedBean
//@ViewScoped
//public class PendienteRequerimientoBean extends InformeBase {
// 
//    private Integer nrofor;
// 
//    @PostConstruct
//    public void init(){
//
//        nombreArchivo = "PENREQ";
//        titulo = "Pendientes para Requeriento";
//        pathReporte = "/isd/reporte/produccion/PENREQ.jasper";
// 
//    }
//
//    public void preCargaDatos(Integer n){
//        nrofor = n;
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
//        //Dejamos el comprobante por defecto
//        parameters.put("CODFOR", "HR");
//
//        if(nrofor!=null){
//            parameters.put("NROFOR", nrofor);
//        }else{
//            parameters.put("NROFOR", null);
//        }
//
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
//    public String getTitulo() {
//        return titulo;
//    }
//
//    public void setTitulo(String titulo) {
//        this.titulo = titulo;
//    }
//    
//}
