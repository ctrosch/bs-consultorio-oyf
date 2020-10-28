/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.Tecnico;
import bs.taller.rn.TecnicoRN;
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
public class TecnicoBean extends GenericBean implements Serializable {

    private Tecnico tecnico;
    private List<Tecnico> lista;
    private Integer codigo;

    @EJB
    private TecnicoRN tecnicoRN;

    // Variables para filtros
    //
    public TecnicoBean() {

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
                    seleccionar(tecnicoRN.getTecnico(codigo));
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
        tecnico = new Tecnico();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tecnico != null) {

                tecnicoRN.guardar(tecnico, esNuevo);
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
            tecnico.getAuditoria().setDebaja(habDes);
            tecnicoRN.guardar(tecnico, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tecnicoRN.eliminar(tecnico);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tecnicoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Tecnico> complete(String query) {
        try {
            lista = tecnicoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Tecnico>();
        }
    }

    public void onSelect(SelectEvent event) {
        tecnico = (Tecnico) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Tecnico u) {

        if (u == null) {
            return;
        }

        tecnico = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tecnico == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico unidadMedida) {
        this.tecnico = unidadMedida;
    }

    public List<Tecnico> getLista() {
        return lista;
    }

    public void setLista(List<Tecnico> lista) {
        this.lista = lista;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

}
