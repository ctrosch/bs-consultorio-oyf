/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.EquipoMarca;
import bs.taller.modelo.EquipoModelo;
import bs.taller.rn.EquipoModeloRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class EquipoModeloBean extends GenericBean implements Serializable {

    private EquipoModelo equipoModelo;
    private List<EquipoModelo> lista;
    private Integer codigo;

    private Integer codMarca = null;

    @EJB
    private EquipoModeloRN equipoModeloRN;

    @ManagedProperty(value = "#{equipoMarcaBean}")
    protected EquipoMarcaBean equipoMarcaBean;

    // Variables para filtros
    private EquipoMarca marca;

    public EquipoModeloBean() {

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
                    seleccionar(equipoModeloRN.getEquipoModelo(codigo));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void setearMarcar(Integer codigo) {
        codMarca = codigo;
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        modoVista = "D";
        equipoModelo = new EquipoModelo();
    }

    public void guardar(boolean nuevo) {

        try {
            if (equipoModelo != null) {

                equipoModeloRN.guardar(equipoModelo, esNuevo);
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
            equipoModelo.getAuditoria().setDebaja(habDes);
            equipoModeloRN.guardar(equipoModelo, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            equipoModeloRN.eliminar(equipoModelo);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        // Si no tiene la marca seteada, entonces no busca nada
        // if (codMarca == null) {
        //     return;
        // }
        cargarFiltroBusqueda();

        lista = equipoModeloRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (marca != null) {

            filtro.put("marca.codigo = ", "'" + marca.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        marca = null;

        buscar();

    }

    public List<EquipoModelo> complete(String query) {
        try {

            // Si no tiene la marca seteada, entonces no busca nada
            // if (codMarca == null) {
            //    return new ArrayList<EquipoModelo>();
            // }
            // lista = equipoModeloRN.getListaByBusqueda(codMarca, null, query, false, 10);
            lista = equipoModeloRN.getListaByBusqueda(null, null, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EquipoModelo>();
        }
    }

    public void onSelect(SelectEvent event) {
        equipoModelo = (EquipoModelo) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(EquipoModelo u) {

        if (u == null) {
            return;
        }

        equipoModelo = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (equipoModelo == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public EquipoModelo getEquipoModelo() {
        return equipoModelo;
    }

    public void setEquipoModelo(EquipoModelo unidadMedida) {
        this.equipoModelo = unidadMedida;
    }

    public List<EquipoModelo> getLista() {
        return lista;
    }

    public void setLista(List<EquipoModelo> lista) {
        this.lista = lista;
    }

    public Integer getCodMarca() {
        return codMarca;
    }

    public void setCodMarca(Integer codMarca) {
        this.codMarca = codMarca;
    }

    public EquipoMarcaBean getEquipoMarcaBean() {
        return equipoMarcaBean;
    }

    public void setEquipoMarcaBean(EquipoMarcaBean equipoMarcaBean) {
        this.equipoMarcaBean = equipoMarcaBean;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public EquipoMarca getMarca() {
        return marca;
    }

    public void setMarca(EquipoMarca marca) {
        this.marca = marca;
    }

}
