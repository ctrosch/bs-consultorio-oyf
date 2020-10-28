/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.ReporteDAO;
import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Reporte;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ReporteRN implements Serializable {

    @EJB
    private ReporteDAO reporteDAO;
    @EJB
    private ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Reporte guardar(Reporte r, boolean esNuevo) throws Exception {

        if (getReporte(r.getCodigo()) == null) {
            reporteDAO.crear(r);
        } else {
            r = (Reporte) reporteDAO.editar(r);
        }

        return r;
    }

    public void control(Reporte reporte) throws ExcepcionGeneralSistema {
        String sErrores = "";
        
        Parametro parametro = parametrosRN.getParametro("01");

        if (reporte.getOrigen().equals("SIS") && !parametro.getOrigenPorDefecto().equals("SIS")) {
            
            sErrores += "- No es posible modificar un reporte de sistema, duplicar el reporte y guardarlo como reporte usuario. \n";                        
        }
        
        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Reporte visita) throws Exception {

        reporteDAO.eliminar(visita);
    }

    public Reporte getReporte(String codigo) {

        return reporteDAO.getObjeto(Reporte.class, codigo);
    }

    public List<Reporte> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return reporteDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Reporte> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return reporteDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Reporte> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return reporteDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public String obtenerSiguienteCogido(String origen) {

        String codigo = reporteDAO.obtenerSiguienteCodigo(origen);

        if (codigo == null || codigo.isEmpty()) {
            codigo = origen.toUpperCase() + "_00001";
        }

        return codigo;
    }

    public Reporte duplicar(Reporte reporte) throws CloneNotSupportedException {
        
        Parametro parametro = parametrosRN.getParametro("01");
        
        reporte = reporte.clone();
        reporte.setNombre(reporte.getNombre() + " (duplicado)");
        reporte.setOrigen(parametro.getOrigenPorDefecto());        
        String codigo = obtenerSiguienteCogido(reporte.getOrigen());
        reporte.setCodigo(codigo);
        
        return reporte;

    }

}
