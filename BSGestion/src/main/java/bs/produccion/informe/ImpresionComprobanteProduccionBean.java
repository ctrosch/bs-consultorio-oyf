///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package bs.produccion.informe;
//
//import isd.general.excepciones.ExcepcionGeneralSistema;
//import isd.general.modelo.GR_Formulario;
//import isd.general.rn.FormularioRN;
//import isd.general.util.InformeBase;
//import bs.produccion.modelo.ComprobanteProduccion;
//import bs.produccion.modelo.MovimientoProduccion;
//import bs.produccion.rn.PDComprobantesRN;
//import bs.produccion.rn.ProduccionRN;
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
//public class ImpresionComprobanteProduccionBean extends InformeBase {
//    
//    @EJB private ProduccionRN produccionRN;
//    @EJB private FormularioRN formularioRN;
//    @EJB private PDComprobantesRN comprobanteProduccionRN;
//
//    private MovimientoProduccion m;    
//    private Integer nrofor;
//    GR_Formulario f;
//
//    private List<ComprobanteProduccion> comprobantes;
//    private ComprobanteProduccion comprobante;
//
//    /** Creates a new instance of ImpresionComprobanteProduccionBean */
//    public ImpresionComprobanteProduccionBean() {
//        
//        titulo = "Impresión comprobante producción";
//
//    }
//
//    @PostConstruct
//    public void init(){
//
//
//        comprobantes = comprobanteProduccionRN.getLista();
//
//    }
//
//    @Override
//    public void validarDatos() throws ExcepcionGeneralSistema {
//
//        f = formularioRN.getFormularioProduccion(comprobante);
//
//        if(f==null){
//            throw new ExcepcionGeneralSistema("Seleccione un comprobante");
//        }
//
//        if(f.getNombreReporte()==null){
//            throw new ExcepcionGeneralSistema("El comprobante seleccionado no tiene asignado reporte");
//        }
//
//        if(nrofor<=0){
//            throw new ExcepcionGeneralSistema("Ingrese un número de comprobante válido");
//        }
//
//        m = produccionRN.getMovimiento(f.getModfor(), f.getCodfor(), nrofor);
//
//        if(m==null){
//            throw new ExcepcionGeneralSistema("El comprobante solicitado no existe");
//        }
//    }
//
//    @Override
//    public void cargarParametros() throws ExcepcionGeneralSistema {
//
//        parameters.put("MODFOR", m.getModfor());
//        parameters.put("CODFOR", m.getCodfor());
//        parameters.put("NROFOR", m.getNrofor());
//
//        nombreArchivo = m.getComprobante().getCodigo()+"-"+m.getNrofor();
//        pathReporte = f.getNombreReporte()+".jasper";
//    }
//
//    /**
//    public void imprimirSSSS(){
//
//        try {
//            //JasperReport masterReport = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResourceAsStream("/isd/reporte/plantilla/A4_FORMULARIO.jasper"));
//            JasperReport masterReport = (JasperReport) JRLoader.loadObject(getClass().getClassLoader().getResourceAsStream(f.getNombreReporte()+".jasper"));
//            reportFactory.verReportePDF(masterReport, nombreArchivo, parameters);
//
//        } catch (Exception e){
//            e.printStackTrace();
//            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e.getMessage());
//        }
//    }
//    */
//
//    public MovimientoProduccion getM() {
//        return m;
//    }
//
//    public void setM(MovimientoProduccion m) {
//        this.m = m;
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
//
//
//    
//}
