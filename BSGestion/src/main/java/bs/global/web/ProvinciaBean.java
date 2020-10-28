/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Pais;
import bs.global.modelo.Provincia;
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
public class ProvinciaBean extends GenericBean implements Serializable {

    private Provincia provincia;
    private List<Provincia> lista;
    private Pais pais;
    private List<Pais> paises;

    @EJB
    private ProvinciaRN provinciaRN;
    @EJB
    private PaisRN paisRN;

    public ProvinciaBean() {

    }

    @PostConstruct
    public void init() {

        pais = paisRN.getPais("054");
        paises = paisRN.getLista(false);

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        provincia = new Provincia();

        if (pais != null) {
            provincia.setPais(pais);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (provincia != null) {

                if (provincia.getPais() == null) {
                    JsfUtil.addErrorMessage("Seleccione el país");
                    return;
                } else {
                    provincia.setCodpai(provincia.getPais().getCodigo());
                }

                provinciaRN.guardar(provincia, esNuevo);
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
            provincia.getAuditoria().setDebaja(habDes);
            provinciaRN.guardar(provincia, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            provinciaRN.eliminar(provincia);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        String codPais = "";

        if (pais != null) {
            codPais = pais.getCodigo();
        }

        lista = provinciaRN.getListaByBusqueda(codPais, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        pais = null;

        buscar();

    }

    public List<Provincia> complete(String query) {
        try {
            String codPais = "";

            if (pais != null) {
                codPais = pais.getCodigo();
            }

            lista = provinciaRN.getListaByBusqueda(codPais, query, false, 8);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Provincia>();
        }
    }

    public void onSelect(SelectEvent event) {

        provincia = (Provincia) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Provincia v) {

        if (v == null) {
            return;
        }

        provincia = v;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (provincia == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void setearPais(Pais pais) {

        this.pais = pais;

    }

    //---------------------------------------------------------------------------
    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public List<Provincia> getLista() {
        return lista;
    }

    public void setLista(List<Provincia> lista) {
        this.lista = lista;
    }

    public ProvinciaRN getProvinciaRN() {
        return provinciaRN;
    }

    public void setProvinciaRN(ProvinciaRN provinciaRN) {
        this.provinciaRN = provinciaRN;
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

}
