/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.ContactoSector;
import bs.entidad.rn.ContactoSectorRN;
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
public class ContactoSectorBean extends GenericBean implements Serializable {

    private String codigo;
    private ContactoSector contactoSector;
    private List<ContactoSector> lista;

    @EJB
    private ContactoSectorRN contactoSectorRN;

    public ContactoSectorBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(contactoSectorRN.getContactoSector(codigo));
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
        contactoSector = new ContactoSector();

    }

    public void guardar(boolean nuevo) {

        try {
            if (contactoSector != null) {

                contactoSector = contactoSectorRN.guardar(contactoSector, esNuevo);
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
            contactoSector.getAuditoria().setDebaja(habDes);
            contactoSectorRN.guardar(contactoSector, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            contactoSectorRN.eliminar(contactoSector);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = contactoSectorRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<ContactoSector> complete(String query) {
        try {

            lista = contactoSectorRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ContactoSector>();
        }
    }

    public void onSelect(SelectEvent event) {
        contactoSector = (ContactoSector) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ContactoSector s) {

        if (s == null) {
            return;
        }

        contactoSector = s;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (contactoSector == null) {
                JsfUtil.addErrorMessage("No hay Sector activo");
                return;
            }

            contactoSector = contactoSectorRN.duplicar(contactoSector);
            esNuevo = true;
            modoVista = "D";

        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (contactoSector == null) {
            JsfUtil.addErrorMessage("No hay Sector seleccionado, nada para imprimir");
        }
    }

    public List<ContactoSector> getLista() {
        return lista;
    }

    public void setLista(List<ContactoSector> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ContactoSector getContactoSector() {
        return contactoSector;
    }

    public void setContactoSector(ContactoSector contactoSector) {
        this.contactoSector = contactoSector;
    }

}
