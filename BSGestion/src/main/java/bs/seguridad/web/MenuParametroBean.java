/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.MenuParametro;
import bs.seguridad.rn.MenuParametroRN;
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
public class MenuParametroBean extends GenericBean implements Serializable {

    private MenuParametro parametro;
    private List<MenuParametro> lista;

    private Menu menu;

    @EJB
    private MenuParametroRN parametroRN;

    /**
     * Creates a new instance of MenuParametroBean
     */
    public MenuParametroBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        parametro = new MenuParametro();

        if (menu != null) {
            parametro.setMenu(menu);
        }
    }

    public void nuevo(MenuParametro p) {

        esNuevo = true;
        parametro = p;
        menu = p.getMenu();
    }

    public void guardar(boolean nuevo) {

        try {
            if (parametro != null) {

                if (menu != null && menu.getParametros() != null && esNuevo) {
                    int idParametro = 1;

                    menu.getParametros().add(parametro);

                    for (MenuParametro p : menu.getParametros()) {
                        p.setId(menu.getCodigo() + "_" + String.valueOf(idParametro));
                        idParametro++;
                    }
                }

                parametroRN.guardar(parametro, esNuevo);
                esNuevo = false;
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
            parametro.getAuditoria().setDebaja(habDes);
            parametroRN.guardar(parametro, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            parametroRN.eliminar(parametro);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = parametroRN.getListaByBusqueda(txtBusqueda, (menu == null ? null : menu.getCodigo()), mostrarDebaja);
    }

    public List<MenuParametro> complete(String query) {
        try {
            lista = parametroRN.getListaByBusqueda(query, (menu == null ? null : menu.getCodigo()), false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<MenuParametro>();
        }
    }

    public void onSelect(SelectEvent event) {
        parametro = (MenuParametro) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(MenuParametro v) {

        if (v == null) {
            return;
        }

        parametro = v;
        esNuevo = false;
    }

    public void imprimir() {

        if (parametro == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public MenuParametro getMenuParametro() {
        return parametro;
    }

    public void setMenuParametro(MenuParametro vendedor) {
        this.parametro = vendedor;
    }

    public List<MenuParametro> getLista() {
        return lista;
    }

    public void setLista(List<MenuParametro> lista) {
        this.lista = lista;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

}
