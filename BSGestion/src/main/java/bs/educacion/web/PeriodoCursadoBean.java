/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.PeriodoCursado;
import bs.educacion.rn.PeriodoCursadoRN;
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
public class PeriodoCursadoBean extends GenericBean implements Serializable {

    private PeriodoCursado periodoCursado;
    private List<PeriodoCursado> lista;
    private String codigo;

    @EJB
    private PeriodoCursadoRN periodoCursadoRN;

    // Variables para filtros
    //
    public PeriodoCursadoBean() {

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
                    seleccionar(periodoCursadoRN.getPeriodoCursado(codigo));
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
        periodoCursado = new PeriodoCursado();
    }

    public void guardar(boolean nuevo) {

        try {
            if (periodoCursado != null) {

                periodoCursadoRN.guardar(periodoCursado, esNuevo);
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
            periodoCursado.getAuditoria().setDebaja(habDes);
            periodoCursadoRN.guardar(periodoCursado, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            periodoCursadoRN.eliminar(periodoCursado);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();
        lista = periodoCursadoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<PeriodoCursado> complete(String query) {
        try {
            lista = periodoCursadoRN.getListaByBusqueda(query, false, 15);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<PeriodoCursado>();
        }
    }

    public void onSelect(SelectEvent event) {
        periodoCursado = (PeriodoCursado) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(PeriodoCursado u) {

        if (u == null) {
            return;
        }

        periodoCursado = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (periodoCursado == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public PeriodoCursado getPeriodoCursado() {
        return periodoCursado;
    }

    public void setPeriodoCursado(PeriodoCursado periodoCursado) {
        this.periodoCursado = periodoCursado;
    }

    public List<PeriodoCursado> getLista() {
        return lista;
    }

    public void setLista(List<PeriodoCursado> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
