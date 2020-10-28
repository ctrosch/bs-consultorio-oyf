/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Plantilla;
import bs.administracion.modelo.Reporte;
import bs.administracion.rn.PlantillaRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Guillermo Vallejos
 */
@ManagedBean
@ViewScoped
public class PlantillaBean extends GenericBean implements Serializable {

    private List<Plantilla> lista;
    private Plantilla plantilla;
    private Parametro parametro;
    private String origen;
    private String codigo;
    private String grupo;
    private Modulo modulo;

    private boolean seleccionaTodoVisible;
    private boolean seleccionaTodoSoloLectura;
    private boolean seleccionaTodoRequerido;

    @EJB
    private PlantillaRN plantillaRN;

    public PlantillaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                parametro = parametrosRN.getParametro("01");

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(plantillaRN.getPlantilla(codigo));
                } else {
                    buscar();
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        plantilla = new Plantilla();
        plantilla.setOrigen(parametro.getOrigenPorDefecto());
        obtenerCodigo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (plantilla != null) {

                plantilla = plantillaRN.guardar(plantilla, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void duplicar() {

        try {
            if (plantilla == null) {
                JsfUtil.addErrorMessage("No hay plantilla activa o la lista de parámetros es nula");
                return;
            }

            plantilla = plantillaRN.duplicar(plantilla);
            esNuevo = true;
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            plantilla.getAuditoria().setDebaja(habDes);
            plantillaRN.guardar(plantilla, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            plantillaRN.eliminar(plantilla);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = plantillaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (modulo != null) {

            filtro.put("modulo.codigo = ", "'" + modulo.getCodigo() + "'");
        }

        if (origen != null && !origen.isEmpty()) {
            filtro.put("origen = ", "'" + origen + "'");
        }

        if (grupo != null && !grupo.isEmpty()) {
            filtro.put("grupo = ", "'" + grupo + "'");
        }
    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        modulo = null;
        origen = null;
        grupo = null;

        buscar();

    }

    public List<Plantilla> complete(String query) {
        try {
            lista = plantillaRN.getListaByBusqueda(null, query, false, 5);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Plantilla>();
        }
    }

    public void onSelect(SelectEvent event) {
        plantilla = (Plantilla) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Plantilla p) {

        if (p == null) {
            return;
        }

        plantilla = p;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (plantilla == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void obtenerCodigo() {

        if (plantilla == null || plantilla.getOrigen() == null) {
            return;
        }

        String codigo = plantillaRN.obtenerSiguienteCogido(plantilla.getOrigen());
        plantilla.setCodigo(codigo);

    }

    //--------------------------------------------------------------------------
    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public List<Plantilla> getLista() {
        return lista;
    }

    public void setLista(List<Plantilla> lista) {
        this.lista = lista;
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
    }

    public boolean isSeleccionaTodoVisible() {
        return seleccionaTodoVisible;
    }

    public void setSeleccionaTodoVisible(boolean seleccionaTodoVisible) {
        this.seleccionaTodoVisible = seleccionaTodoVisible;
    }

    public boolean isSeleccionaTodoSoloLectura() {
        return seleccionaTodoSoloLectura;
    }

    public void setSeleccionaTodoSoloLectura(boolean seleccionaTodoSoloLectura) {
        this.seleccionaTodoSoloLectura = seleccionaTodoSoloLectura;
    }

    public boolean isSeleccionaTodoRequerido() {
        return seleccionaTodoRequerido;
    }

    public void setSeleccionaTodoRequerido(boolean seleccionaTodoRequerido) {
        this.seleccionaTodoRequerido = seleccionaTodoRequerido;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }

}
