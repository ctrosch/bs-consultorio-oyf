/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Categoria;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.CategoriaRN;
import bs.entidad.rn.TipoEntidadRN;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CategoriaBean extends GenericBean implements Serializable {

    @EJB
    private CategoriaRN categoriaRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    private Categoria categoria;
    private List<Categoria> lista;
    private String codigo;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;

    // Variables para filtros
    ///
    public CategoriaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo == null || codigo.isEmpty()) {
                    buscar();
                } else {
                    seleccionar(categoriaRN.getCategoria(codigo, tipoEntidad));
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
        categoria = new Categoria();
    }

    public void guardar(boolean nuevo) {

        try {
            if (categoria != null) {
                categoria = categoriaRN.guardar(categoria, esNuevo);
                esNuevo = false;
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
            categoria.getAuditoria().setDebaja(habDes);
            categoria = categoriaRN.guardar(categoria, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            categoriaRN.eliminar(categoria);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = categoriaRN.getCategoriaByBusqueda(filtro, txtBusqueda, tipoEntidad, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipoEntidad = null;
        buscar();

    }

    public List<Categoria> complete(String query) {
        try {

            lista = categoriaRN.getCategoriaByBusqueda(query, tipoEntidad, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Categoria>();
        }
    }

    public void onSelect(SelectEvent event) {
        categoria = (Categoria) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Categoria c) {

        if (c == null) {
            return;
        }

        categoria = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (categoria == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Categoria> getLista() {
        return lista;
    }

    public void setLista(List<Categoria> lista) {
        this.lista = lista;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public List<TipoEntidad> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoEntidad> tipos) {
        this.tipos = tipos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
