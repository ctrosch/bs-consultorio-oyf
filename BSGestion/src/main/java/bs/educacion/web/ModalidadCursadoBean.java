/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.ModalidadCursado;
import bs.educacion.rn.ModalidadCursadoRN;
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
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class ModalidadCursadoBean extends GenericBean implements Serializable {

    private String codigo;
    private ModalidadCursado modalidad;
    private List<ModalidadCursado> lista;

    @EJB
    private ModalidadCursadoRN modalidadCursadoRN;

    public ModalidadCursadoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(modalidadCursadoRN.getModalidadCursado(codigo));
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
        modoVista = "D";
        modalidad = new ModalidadCursado();
    }

    public void guardar(boolean nuevo) {

        try {
            if (modalidad != null) {

                modalidad = modalidadCursadoRN.guardar(modalidad, esNuevo);
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            modalidad.getAuditoria().setDebaja(habDes);
            modalidadCursadoRN.guardar(modalidad, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            modalidadCursadoRN.eliminar(modalidad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = modalidadCursadoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<ModalidadCursado> complete(String query) {
        try {

            lista = modalidadCursadoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ModalidadCursado>();
        }
    }

    public void onSelect(SelectEvent event) {
        modalidad = (ModalidadCursado) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ModalidadCursado d) {

        if (d == null) {
            return;
        }

        modalidad = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (modalidad == null) {
                JsfUtil.addErrorMessage("No hay modalidad de Cursado activa");
                return;
            }

            modalidad = modalidadCursadoRN.duplicar(modalidad);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (modalidad == null) {
            JsfUtil.addErrorMessage("No hay Modalidad de Cursado seleccionada, nada para imprimir");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ModalidadCursado getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadCursado modalidad) {
        this.modalidad = modalidad;
    }

    public List<ModalidadCursado> getLista() {
        return lista;
    }

    public void setLista(List<ModalidadCursado> lista) {
        this.lista = lista;
    }

}
