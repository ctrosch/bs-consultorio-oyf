/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.Atributo;
import bs.stock.modelo.AtributoValor;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.TipoProductoRN;
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
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class TipoProductoBean extends GenericBean implements Serializable {

    @EJB
    private TipoProductoRN tipoProductoRN;

    private List<TipoProducto> lista;
    private TipoProducto tipoProducto;
    private String codigo;

    // Variables para filtros
    private String gestionaStock;

    /**
     * Creates a new instance of TipoProductoBean
     */
    public TipoProductoBean() {
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
                    seleccionar(tipoProductoRN.getTipoProducto(codigo));
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
        tipoProducto = new TipoProducto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoProducto != null) {

                tipoProductoRN.guardar(tipoProducto, esNuevo);
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
            tipoProducto.getAuditoria().setDebaja(habDes);
            tipoProductoRN.guardar(tipoProducto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoProductoRN.eliminar(tipoProducto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoProductoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();
        if (gestionaStock != null && !gestionaStock.isEmpty()) {

            filtro.put("gestionaStock = ", "'" + gestionaStock + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        gestionaStock = "";

        buscar();

    }

    public List<TipoProducto> complete(String query) {
        try {
            lista = tipoProductoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoProducto>();
        }
    }

    public void onSelect(SelectEvent event) {

        tipoProducto = (TipoProducto) event.getObject();
        seleccionar(tipoProducto);
    }

    public void seleccionar(TipoProducto u) {

        if (u == null) {
            return;
        }

        tipoProducto = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tipoProducto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoRubro01() {

        try {
            tipoProductoRN.nuevoRubro01(tipoProducto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarRubro01(Rubro01 rubro01) {

        try {
            tipoProductoRN.eliminarRubro01(tipoProducto, rubro01);
            JsfUtil.addWarningMessage("Se ha borrado el rubro " + (rubro01.getDescripcion() != null ? rubro01.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (rubro01.getDescripcion() != null ? rubro01.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoRubro02() {

        try {
            tipoProductoRN.nuevoRubro02(tipoProducto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarRubro02(Rubro02 rubro02) {

        try {
            tipoProductoRN.eliminarRubro02(tipoProducto, rubro02);
            JsfUtil.addWarningMessage("Se ha borrado el rubro " + (rubro02.getDescripcion() != null ? rubro02.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (rubro02.getDescripcion() != null ? rubro02.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoRubro03() {

        try {
            tipoProductoRN.nuevoRubro03(tipoProducto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item familia: " + ex);
        }
    }

    public void eliminarRubro03(Rubro03 rubro03) {

        try {
            tipoProductoRN.eliminarRubro03(tipoProducto, rubro03);
            JsfUtil.addWarningMessage("Se ha borrado el rubro " + (rubro03.getDescripcion() != null ? rubro03.getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (rubro03.getDescripcion() != null ? rubro03.getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoAtributo() {

        try {
            tipoProductoRN.nuevoAtributo(tipoProducto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item atributo: " + ex);
        }
    }

    public void eliminarAtributo(Atributo atributo) {

        try {
            tipoProductoRN.eliminarAtributo(tipoProducto, atributo);
            JsfUtil.addWarningMessage("Se ha borrado el atributo " + (atributo.getNombre() != null ? atributo.getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (atributo.getNombre() != null ? atributo.getNombre() : "") + " " + ex);
        }
    }

    public void nuevoAtributoValor(Atributo atributo) {

        try {
            tipoProductoRN.nuevoAtributoValor(atributo);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item atributo: " + ex);
        }
    }

    public void eliminarAtributoValor(Atributo atributo, AtributoValor valor) {

        try {
            tipoProductoRN.eliminarAtributoValor(atributo, valor);
            JsfUtil.addWarningMessage("Se ha borrado el item valor " + (valor.getValor() != null ? valor.getValor() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (valor.getValor() != null ? valor.getValor() : "") + " " + ex);
        }
    }

    //-----------------------------------------------------------------------------------
    public List<TipoProducto> getLista() {
        return lista;
    }

    public void setLista(List<TipoProducto> lista) {
        this.lista = lista;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public TipoProductoRN getTipoProductoRN() {
        return tipoProductoRN;
    }

    public void setTipoProductoRN(TipoProductoRN tipoProductoRN) {
        this.tipoProductoRN = tipoProductoRN;
    }

    public String getGestionaStock() {
        return gestionaStock;
    }

    public void setGestionaStock(String gestionaStock) {
        this.gestionaStock = gestionaStock;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
