/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.AreaEducacion;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.ItemArancel;
import bs.educacion.modelo.ItemCarreraSucursal;
import bs.educacion.modelo.ModalidadCursado;
import bs.educacion.modelo.TipoCarrera;
import bs.educacion.rn.CarreraRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.UnidadNegocio;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CarreraBean extends GenericBean implements Serializable {

    private List<Carrera> lista;
    private Carrera carrera;
    private String CODIGO;

    // Variables para filtros
    ModalidadCursado modalidad;
    AreaEducacion area;
    UnidadNegocio unidadNegocio;
    TipoCarrera tipoCarrera;

    @EJB
    private CarreraRN carreraRN;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public CarreraBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(carreraRN.getCarrera(CODIGO));
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
        carrera = new Carrera();

    }

    public void asignarCodigo() {

        try {
            if (esNuevo) {
                carreraRN.asignarCodigo(carrera);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código de carrera " + ex);
        }
    }

    public void guardar(boolean nuevo) {
        try {
            if (carrera != null) {

                carrera = carreraRN.guardar(carrera, esNuevo);
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
//            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void nuevoArancel() {

        try {
            carreraRN.nuevoArancel(carrera);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item arancel : " + ex);
        }
    }

    public void eliminarArancel(Arancel item) {

        try {
            carreraRN.eliminarArancel(carrera, item);
            JsfUtil.addWarningMessage("Se ha borrado el item arancel " + (item.getCodigo() != null ? item.getCodigo() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getCodigo() != null ? item.getCodigo() : "") + " " + ex);
        }
    }

    public void nuevoItemArancel(Arancel arancel) {

        try {
            carreraRN.nuevoItemArancel(arancel);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item arancel : " + ex);
        }
    }

    public void eliminarItemArancel(ItemArancel item) {

//        try {
//            carreraRN.eliminarArancel(carrera, item);
//            JsfUtil.addWarningMessage("Se ha borrado el item arancel " + (item.getCodigo() != null ? item.getCodigo() : "") + "");
//        } catch (Exception ex) {
//            JsfUtil.addErrorMessage("Error " + (item.getCodigo() != null ? item.getCodigo() : "") + " " + ex);
//        }
    }

    public void nuevoItemSucursal() {

        try {
            carreraRN.nuevoItemSucursal(carrera);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sucursal: " + ex);
        }
    }

    public void eliminarItemSucursal(ItemCarreraSucursal item) {

        try {
            carreraRN.eliminarItemSucursal(carrera, item);
            JsfUtil.addWarningMessage("Se ha borrado el item sucursal " + (item.getSucursal() != null ? item.getSucursal().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getSucursal() != null ? item.getSucursal().getNombre() : "") + " " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            carrera.getAuditoria().setDebaja(habDes);
            carrera = carreraRN.guardar(carrera, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            carreraRN.eliminar(carrera);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = carreraRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public List<Carrera> complete(String query) {
        try {

            lista = carreraRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Carrera>();
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (area != null) {

            filtro.put("area.codigo = ", "'" + area.getCodigo() + "'");
        }

        if (modalidad != null) {
            filtro.put("modalidadCursado.codigo = ", "'" + modalidad.getCodigo() + "'");
        }

        if (unidadNegocio != null) {
            filtro.put("unidadNegocio.codigo=", "'" + unidadNegocio.getCodigo() + "'");
        }

        if (tipoCarrera != null) {
            filtro.put("tipoCarrera.codigo=", "'" + tipoCarrera.getCodigo() + "'");
        }
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        area = null;
        modalidad = null;
        unidadNegocio = null;
        tipoCarrera = null;
        buscar();

    }

    public void onSelect(SelectEvent event) {

        carrera = (Carrera) event.getObject();
        esNuevo = false;
        modoVista = "D";

    }

    public void seleccionar(Carrera e) {

        if (e == null) {
            return;
        }

        carrera = e;
        esNuevo = false;
        modoVista = "D";

    }

    public void duplicar() throws Exception {

        try {
            if (carrera == null) {
                JsfUtil.addErrorMessage("No hay Carrera activo");
                return;
            }

            carrera = carreraRN.duplicar(carrera);
            // carrera.setCodigo(carreraRN.getProximoCodigo());
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (carrera == null) {
            JsfUtil.addErrorMessage("No hay Carrera seleccionado, nada para imprimir");
        }
    }

    public List<Carrera> getLista() {
        return lista;
    }

    public void setLista(List<Carrera> lista) {
        this.lista = lista;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public ModalidadCursado getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadCursado modalidad) {
        this.modalidad = modalidad;
    }

    public AreaEducacion getArea() {
        return area;
    }

    public void setArea(AreaEducacion area) {
        this.area = area;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public TipoCarrera getTipoCarrera() {
        return tipoCarrera;
    }

    public void setTipoCarrera(TipoCarrera tipoCarrera) {
        this.tipoCarrera = tipoCarrera;
    }

}
