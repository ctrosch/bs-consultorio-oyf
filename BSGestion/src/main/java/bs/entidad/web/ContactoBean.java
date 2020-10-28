/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.ContactoRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
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
/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ContactoBean extends GenericBean implements Serializable {

    private Contacto contacto;
    private EntidadComercial entidadComercial;
    private List<Contacto> lista;
    private Integer ID;

    @EJB
    private ContactoRN contactoRN;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    // Variables para filtros
    /**
     * Creates a new instance of ContactoBean
     */
    public ContactoBean() {

    }

    @PostConstruct
    public void init() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (ID != null && ID > 0) {
                    seleccionar(contactoRN.getContacto(ID));
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
        contacto = new Contacto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (contacto != null) {

                contacto = contactoRN.guardar(contacto, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addErrorMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            //  Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            contacto.getAuditoria().setDebaja(habDes);
            contacto = contactoRN.guardar(contacto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos" + ex);
        }
    }

    public void eliminar() {
        try {
            contactoRN.eliminar(contacto);
            nuevo();

            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos" + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = contactoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (entidadComercial != null) {

            filtro.put("entidadComercial.nrocta = ", "'" + entidadComercial.getNrocta() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        entidadComercial = null;
        buscar();

    }

    public List<Contacto> complete(String query) {
        try {

            lista = contactoRN.getListaByBusqueda(null, query, mostrarDebaja, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Contacto>();
        }
    }

    public void onSelect(SelectEvent event) {
        contacto = (Contacto) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Contacto c) {

        if (c == null) {
            return;
        }

        contacto = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (contacto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarLocalidad() {

        if (localidadBean.getLocalidad() != null && contacto != null) {

            contacto.setLocalidad(localidadBean.getLocalidad());
            contacto.setProvincia(localidadBean.getLocalidad().getProvincia());
        }
    }

    public void procesarEntidadComercial() {

        if (entidadComercialBean.getEntidad() != null && contacto != null) {

            contacto.setEntidadComercial(entidadComercialBean.getEntidad());

        }
    }

    //-------------------------------------------------------------------------
    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto proyecto) {
        this.contacto = proyecto;
    }

    public List<Contacto> getLista() {
        return lista;
    }

    public void setLista(List<Contacto> lista) {
        this.lista = lista;
    }

    public ContactoRN getContactoRN() {
        return contactoRN;
    }

    public void setContactoRN(ContactoRN contactoRN) {
        this.contactoRN = contactoRN;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

}
