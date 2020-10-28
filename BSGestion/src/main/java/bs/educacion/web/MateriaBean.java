/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.ItemMateriaProfesor;
import bs.educacion.modelo.Materia;
import bs.educacion.rn.MateriaRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class MateriaBean extends GenericBean implements Serializable {

    private String codigo;
    private Materia materia;
    private List<Materia> lista;

    @EJB
    private MateriaRN materiaRN;

    public MateriaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(materiaRN.getMateria(codigo));
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
        materia = new Materia();

    }

    public void guardar(boolean nuevo) {

        try {
            if (materia != null) {

                materia = materiaRN.guardar(materia, esNuevo);
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
            materia.getAuditoria().setDebaja(habDes);
            materiaRN.guardar(materia, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            materiaRN.eliminar(materia);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = materiaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Materia> complete(String query) {
        try {

            lista = materiaRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Materia>();
        }
    }

    public void onSelect(SelectEvent event) {
        materia = (Materia) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Materia m) {

        if (m == null) {
            return;
        }

        materia = m;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (materia == null) {
                JsfUtil.addErrorMessage("No hay Materia activa");
                return;
            }

            materia = materiaRN.duplicar(materia);
            esNuevo = true;
            modoVista = "D";

        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (materia == null) {
            JsfUtil.addErrorMessage("No hay Materia seleccionada, nada para imprimir");
        }
    }

    public List<Materia> getLista() {
        return lista;
    }

    public void setLista(List<Materia> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public void nuevoItemProfesor() {

        try {
            materiaRN.nuevoItemProfesor(materia);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item profesor: " + ex);
        }
    }

    public void eliminarItemProfesor(ItemMateriaProfesor item) {

        try {
            materiaRN.eliminarItemProfesor(materia, item);
            JsfUtil.addWarningMessage("Se ha borrado el item profesor " + (item.getProfesor() != null ? item.getProfesor().getNombreComplete() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getProfesor() != null ? item.getProfesor().getNombreComplete() : "") + " " + ex);
        }
    }

}
