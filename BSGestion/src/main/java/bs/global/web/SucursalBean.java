/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Sucursal;
import bs.global.rn.SucursalRN;
import bs.global.util.JsfUtil;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ViewScoped
@ManagedBean
public class SucursalBean extends GenericBean implements Serializable {

    private Sucursal sucursal;
    private List<Sucursal> lista;

    @EJB
    private SucursalRN sucursalRN;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    public SucursalBean() {

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
        buscaMovimiento = false;
        sucursal = new Sucursal();
    }

    public void guardar(boolean nuevo) {

        try {
            if (sucursal != null) {

                sucursalRN.guardar(sucursal, esNuevo);
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
            sucursal.getAuditoria().setDebaja(habDes);
            sucursalRN.guardar(sucursal, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            sucursalRN.eliminar(sucursal);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = sucursalRN.getListaByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<Sucursal> complete(String query) {
        try {
            cargarFiltroBusqueda();
            lista = sucursalRN.getListaByBusqueda(filtro, query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Sucursal>();
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos solo préstamos por unidad de negocio a la que pertenece el usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("codigo IN", "(" + sFiltro + ")");
        }

    }

    public void onSelect(SelectEvent event) {
        sucursal = (Sucursal) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Sucursal s) {

        if (s == null) {
            return;
        }

        sucursal = s;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (sucursal == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public List<Sucursal> getLista() {
        return lista;
    }

    public void setLista(List<Sucursal> lista) {
        this.lista = lista;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

}
