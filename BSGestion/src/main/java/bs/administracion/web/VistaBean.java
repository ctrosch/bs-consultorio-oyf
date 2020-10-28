/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Vista;
import bs.administracion.modelo.VistaDetalle;
import bs.administracion.rn.VistaRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class VistaBean extends GenericBean implements Serializable {

    private List<Vista> lista;
    private Parametro parametro;
    private String codigo;

    //Atributos para filtro
    private String origen;
    private Modulo modulo;

    private boolean seleccionaTodoVisible;
    private boolean seleccionaTodoSoloLectura;
    private boolean seleccionaTodoRequerido;

    @EJB
    private VistaRN vistaRN;

    public VistaBean() {

    }

    @PostConstruct
    public void init() {
        parametro = parametrosRN.getParametro("01");
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(vistaRN.getVista(codigo));
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
        vista = new Vista();
        vista.setOrigen(parametro.getOrigenPorDefecto());
        vistaRN.obtenerCodigo(vista);
        modoVista = "D";
    }

    public void guardar(boolean nuevo) {

        try {
            if (vista != null) {

//                if (vista.getOrigen().equals("SIS") && !parametro.getOrigenPorDefecto().equals("SIS")) {
//                    JsfUtil.addErrorMessage("No es posible modificar un menú de sistema, duplicar el menú y guardarlo como menu usuario.");
//                    return;
//                }
                vista = vistaRN.guardar(vista, esNuevo);
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

    public void nuevoItem(String tipo) {

        try {
            if (vista.getOrigen().equals("SIS") && !parametro.getOrigenPorDefecto().equals("SIS")) {
                JsfUtil.addErrorMessage("No es posible modificar agregar un item en una vista de sistema, solo modificar datos. Duplicar la vista y guardarla como vista de usuario.");
                return;
            }

            vistaRN.nuevoItemDetalle(vista, tipo);

        } catch (Exception ex) {

            ex.printStackTrace();

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void eliminarItemDetalle(VistaDetalle itemDetalle) {

        try {

            if (vista.getOrigen().equals("SIS") && !parametro.getOrigenPorDefecto().equals("SIS")) {
                JsfUtil.addErrorMessage("No es posible borrar un item en una vista de sistema, duplicar la vista y guardarla como vista de usuario.");
                return;
            }
            vistaRN.eliminarItemDetalle(vista, itemDetalle);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible eliminar item " + ex);
        }
    }

    public void duplicar() {

        if (vista == null || vista.getDetalle() == null) {
            JsfUtil.addErrorMessage("No hay vista activa o la lista de parámetros es nula");
            return;
        }

        Vista vistaAux = vista;
        vista = new Vista();
        vista.setNombre(vistaAux.getNombre() + "(Duplicado)");
        vista.setPath(vistaAux.getPath());
        vista.setOrigen(parametro.getOrigenPorDefecto());
        vista.setModulo(vistaAux.getModulo());
        vista.setVistaReferencia(vistaAux.getVistaReferencia());

        if (vistaAux.getDetalle() != null) {

            vista.setDetalle(new ArrayList<VistaDetalle>());

            for (VistaDetalle d : vistaAux.getDetalle()) {

                VistaDetalle dnew = new VistaDetalle();
                //codigo;
                dnew.setNombre(d.getNombre());
                dnew.setTipo(d.getTipo());
                dnew.setCampo(d.getCampo());
                dnew.setVisible(d.isVisible());
                dnew.setSoloLectura(d.isSoloLectura());
                dnew.setRequerido(d.isRequerido());
                dnew.setOrigen(d.getOrigen());
                dnew.setVista(vista);
                vista.getDetalle().add(dnew);
            }
        }

        vistaRN.obtenerCodigo(vista);
        vistaRN.reorganizarItems(vista);
        esNuevo = true;
    }

    public void sincronizarFromReferencia() {

        try {
            vistaRN.sincronizarFromReferencia(vista);
            JsfUtil.addInfoMessage("Sicronizacion exitosa ");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible sinronizar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            vista.getAuditoria().setDebaja(habDes);
            vista = vistaRN.guardar(vista, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            vistaRN.eliminar(vista);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = vistaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        modulo = null;
        origen = null;

        buscar();

    }

    public List<Vista> complete(String query) {
        try {
            lista = vistaRN.getListaByBusqueda(null, query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Vista>();
        }
    }

    public void onSelect(SelectEvent event) {
        vista = (Vista) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Vista d) {

        if (d == null) {
            return;
        }

        vista = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (vista == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void seleccionarTodo(String campo) {

        for (VistaDetalle i : vista.getDetalle()) {

            if (campo.equals("visible")) {
                i.setVisible(seleccionaTodoVisible);
            }

            if (campo.equals("soloLectura")) {
                i.setVisible(seleccionaTodoSoloLectura);
            }

            if (campo.equals("requerido")) {
                i.setVisible(seleccionaTodoRequerido);
            }
        }
    }

    @Override
    public Vista getVista() {
        return vista;
    }

    @Override
    public void setVista(Vista vista) {
        this.vista = vista;
    }

    public List<Vista> getLista() {
        return lista;
    }

    public void setLista(List<Vista> lista) {
        this.lista = lista;
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

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
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

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

}
