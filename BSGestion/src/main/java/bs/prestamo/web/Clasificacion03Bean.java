/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.prestamo.modelo.Clasificacion03;
import bs.prestamo.rn.Clasificacion03RN;
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
public class Clasificacion03Bean extends GenericBean implements Serializable {

    private Clasificacion03 clasificacion03;
    private List<Clasificacion03> lista;
    private String codigo;

    @EJB
    private Clasificacion03RN clasificacion03RN;

    // Variables para filtros
    ///
    public Clasificacion03Bean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(clasificacion03RN.getClasificacion03(codigo));
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
        clasificacion03 = new Clasificacion03();
    }

    public void guardar(boolean nuevo) {

        try {
            if (clasificacion03 != null) {

                clasificacion03RN.guardar(clasificacion03, esNuevo);
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
            clasificacion03.getAuditoria().setDebaja(habDes);
            clasificacion03RN.guardar(clasificacion03, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            clasificacion03RN.eliminar(clasificacion03);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = clasificacion03RN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Clasificacion03> complete(String query) {
        try {
            lista = clasificacion03RN.getListaByBusqueda(query, false, 5);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Clasificacion03>();
        }
    }

    public void onSelect(SelectEvent event) {
        clasificacion03 = (Clasificacion03) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Clasificacion03 u) {

        if (u == null) {
            return;
        }

        clasificacion03 = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (clasificacion03 == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Clasificacion03 getClasificacion03() {
        return clasificacion03;
    }

    public void setClasificacion03(Clasificacion03 unidadMedida) {
        this.clasificacion03 = unidadMedida;
    }

    public List<Clasificacion03> getLista() {
        return lista;
    }

    public void setLista(List<Clasificacion03> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
