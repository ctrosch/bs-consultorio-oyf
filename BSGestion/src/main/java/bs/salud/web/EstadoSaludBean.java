/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.salud.modelo.EstadoSalud;
import bs.salud.rn.EstadoSaludRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Guillermo
 */
@Named
@ViewScoped
public class EstadoSaludBean extends GenericBean implements Serializable {

    private EstadoSalud estado;
    private List<EstadoSalud> lista;
    private String codigo;

    @EJB
    private EstadoSaludRN estadoRN;

    // Variables para filtros
    //
    public EstadoSaludBean() {

    }

    @PostConstruct
    public void init() {

        //Ya no vá mas el buscar en el init por que no se utilizan selectonemenu para esta entidad
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(estadoRN.getEstadoSalud(codigo));
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
        buscaMovimiento = false;
        modoVista = "D";
        estado = new EstadoSalud();
    }

    public void guardar(boolean nuevo) {

        try {
            if (estado != null) {

                estadoRN.guardar(estado, esNuevo);
                esNuevo = false;
                buscaMovimiento = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            estado.getAuditoria().setDebaja(habDes);
            estadoRN.guardar(estado, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            estadoRN.eliminar(estado);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = estadoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;

        buscar();

    }

    public List<EstadoSalud> complete(String query) {
        try {
            lista = estadoRN.getListaByBusqueda(query, false, 15);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EstadoSalud>();
        }
    }

    public void onSelect(SelectEvent event) {
        estado = (EstadoSalud) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(EstadoSalud u) {

        if (u == null) {
            return;
        }

        estado = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (estado == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public EstadoSalud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSalud estado) {
        this.estado = estado;
    }

    public List<EstadoSalud> getLista() {
        return lista;
    }

    public void setLista(List<EstadoSalud> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
