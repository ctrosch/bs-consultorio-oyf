/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.entidad.web.ContactoBean;
import bs.entidad.web.EntidadComercialBean;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.Equipo;
import bs.taller.modelo.EquipoMarca;
import bs.taller.modelo.EquipoModelo;
import bs.taller.modelo.EquipoTipo;
import bs.taller.rn.EquipoRN;
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
public class EquipoBean extends GenericBean implements Serializable {

    private Equipo equipo;
    private List<Equipo> lista;
    private String codigo;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    @ManagedProperty(value = "#{contactoBean}")
    protected ContactoBean contactoBean;

    @ManagedProperty(value = "#{equipoModeloBean}")
    protected EquipoModeloBean equipoModeloBean;

    @EJB
    private EquipoRN equipoRN;

    // Variables para filtros
    private EquipoModelo modelo;
    private EquipoMarca marca;
    private EquipoTipo tipo;

    public EquipoBean() {

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
                    seleccionar(equipoRN.getEquipo(codigo));
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
        equipo = new Equipo();
        equipo.setCodigo(equipoRN.getProximoNroEquipo());
    }

    public void guardar(boolean nuevo) {

        try {
            if (equipo != null) {

                equipoRN.guardar(equipo, esNuevo);
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
            equipo.getAuditoria().setDebaja(habDes);
            equipoRN.guardar(equipo, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            equipoRN.eliminar(equipo);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = equipoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (tipo != null) {
            filtro.put("tipo.codigo=", "'" + tipo.getCodigo() + "'");
        }

        if (marca != null) {
            filtro.put("marca.codigo=", "'" + marca.getCodigo() + "'");
        }

        if (modelo != null) {
            filtro.put("modelo.codigo=", "'" + modelo.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipo = null;
        marca = null;
        modelo = null;

        buscar();

    }

    public List<Equipo> complete(String query) {
        try {
            lista = equipoRN.getListaByBusqueda(null, query, false, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Equipo>();
        }
    }

    public void onSelect(SelectEvent event) {
        equipo = (Equipo) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
        filtrarContacto();
        equipoModeloBean.setCodMarca(equipo.getMarca().getCodigo());
    }

    public void seleccionar(Equipo u) {

        if (u == null) {
            return;
        }

        equipo = u;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (equipo == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarEntidadComercial() {

        if (entidadComercialBean.getEntidad() != null && equipo != null) {

            equipo.setEntidadComercial(entidadComercialBean.getEntidad());
        }
    }

    public void filtrarModelo() {

        equipo.setModelo(null);

        if (equipo.getMarca() != null) {
            equipoModeloBean.setCodMarca(equipo.getMarca().getCodigo());
        }
    }

    public void filtrarContacto() {

        if (equipo.getEntidadComercial() != null) {
            contactoBean.setEntidadComercial(equipo.getEntidadComercial());
        }
    }

    //-----------------------------------------------------------------------------------------
    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo unidadMedida) {
        this.equipo = unidadMedida;
    }

    public List<Equipo> getLista() {
        return lista;
    }

    public void setLista(List<Equipo> lista) {
        this.lista = lista;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public EquipoModeloBean getEquipoModeloBean() {
        return equipoModeloBean;
    }

    public void setEquipoModeloBean(EquipoModeloBean equipoModeloBean) {
        this.equipoModeloBean = equipoModeloBean;
    }

    public ContactoBean getContactoBean() {
        return contactoBean;
    }

    public void setContactoBean(ContactoBean contactoBean) {
        this.contactoBean = contactoBean;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public EquipoModelo getModelo() {
        return modelo;
    }

    public void setModelo(EquipoModelo modelo) {
        this.modelo = modelo;
    }

    public EquipoMarca getMarca() {
        return marca;
    }

    public void setMarca(EquipoMarca marca) {
        this.marca = marca;
    }

    public EquipoTipo getTipo() {
        return tipo;
    }

    public void setTipo(EquipoTipo tipo) {
        this.tipo = tipo;
    }

}
