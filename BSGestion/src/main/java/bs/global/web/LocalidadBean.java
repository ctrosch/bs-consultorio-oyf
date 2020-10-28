/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Localidad;
import bs.global.modelo.Pais;
import bs.global.modelo.Provincia;
import bs.global.rn.LocalidadRN;
import bs.global.rn.PaisRN;
import bs.global.rn.ProvinciaRN;
import bs.global.util.JsfUtil;
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
public class LocalidadBean extends GenericBean implements Serializable {

    private Localidad localidad;
    private List<Localidad> lista;
    private Integer id;

    private Pais pais;
    private List<Pais> paises;

    private Provincia provincia;
    private List<Provincia> provincias;

    @EJB
    private LocalidadRN localidadRN;
    @EJB
    private ProvinciaRN provinciaRN;
    @EJB
    private PaisRN paisRN;

    public LocalidadBean() {

    }

    @PostConstruct
    public void init() {

        pais = paisRN.getPais("054");
        paises = paisRN.getLista(false);

        provincias = provinciaRN.getListaByBusqueda("054", "", false);

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (id != null) {
                    seleccionar(localidadRN.getLocalidad(id));
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
        localidad = new Localidad();
    }

    public void guardar(boolean nuevo) {

        try {
            if (localidad != null) {

                if (localidad.getPais() == null) {
                    JsfUtil.addErrorMessage("Seleccione el país");
                    return;
                }

                if (localidad.getProvincia() == null) {
                    JsfUtil.addErrorMessage("Seleccione la provincia");
                    return;
                }

                localidad = localidadRN.guardar(localidad);
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
            localidad.getAuditoria().setDebaja(habDes);
            localidad = localidadRN.guardar(localidad);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            localidadRN.eliminar(localidad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        String codPais = null;
        String codProv = null;

        if (pais != null) {
            codPais = pais.getCodigo();
        }
        if (provincia != null) {
            codProv = provincia.getCodigo();
        }

        lista = localidadRN.getListaByBusqueda(codPais, codProv, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        pais = null;
        provincia = null;

        buscar();

    }

    public List<Localidad> complete(String query) {
        try {

            String codPais = null;
            String codProv = null;

            if (pais != null) {
                codPais = pais.getCodigo();
            }
            if (provincia != null) {
                codProv = provincia.getCodigo();
            }

            lista = localidadRN.getListaByBusqueda(codPais, codProv, query, false, 15);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Localidad>();
        }
    }

    public void onSelect(SelectEvent event) {

        localidad = (Localidad) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Localidad v) {

        if (v == null) {
            return;
        }

        localidad = v;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void filtrarProvinciaBuscar() {
        if (pais != null) {
            provincias = provinciaRN.getLista(pais.getCodigo(), false);
        }
        buscar();
    }

    public void filtrarProvincia() {

        if (localidad != null) {

            if (localidad.getPais() != null) {
                provincias = provinciaRN.getLista(localidad.getPais().getCodigo(), false);
            }
        }

    }

    public void imprimir() {

        if (localidad == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public List<Localidad> getLista() {
        return lista;
    }

    public void setLista(List<Localidad> lista) {
        this.lista = lista;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public List<Pais> getPaises() {
        return paises;
    }

    public void setPaises(List<Pais> paises) {
        this.paises = paises;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public List<Provincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<Provincia> provincias) {
        this.provincias = provincias;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
