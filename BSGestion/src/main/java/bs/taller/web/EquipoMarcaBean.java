/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.EquipoMarca;
import bs.taller.rn.EquipoMarcaRN;
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
public class EquipoMarcaBean extends GenericBean implements Serializable {

    private EquipoMarca equipoMarca;
    private List<EquipoMarca> lista;
    private Integer codigo;

    @EJB
    private EquipoMarcaRN equipoMarcaRN;

    // Variables para filtros
    ///
    public EquipoMarcaBean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && codigo > 0) {
                    seleccionar(equipoMarcaRN.getEquipoMarca(codigo));
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
        equipoMarca = new EquipoMarca();
    }

    public void guardar(boolean nuevo) {

        try {
            if (equipoMarca != null) {

                equipoMarcaRN.guardar(equipoMarca, esNuevo);
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
            equipoMarca.getAuditoria().setDebaja(habDes);
            equipoMarcaRN.guardar(equipoMarca, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            equipoMarcaRN.eliminar(equipoMarca);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = equipoMarcaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<EquipoMarca> complete(String query) {
        try {
            lista = equipoMarcaRN.getListaByBusqueda(null, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EquipoMarca>();
        }
    }

    public void onSelect(SelectEvent event) {
        equipoMarca = (EquipoMarca) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(EquipoMarca u) {

        if (u == null) {
            return;
        }

        equipoMarca = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (equipoMarca == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public EquipoMarca getEquipoMarca() {
        return equipoMarca;
    }

    public void setEquipoMarca(EquipoMarca unidadMedida) {
        this.equipoMarca = unidadMedida;
    }

    public List<EquipoMarca> getLista() {
        return lista;
    }

    public void setLista(List<EquipoMarca> lista) {
        this.lista = lista;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

}
