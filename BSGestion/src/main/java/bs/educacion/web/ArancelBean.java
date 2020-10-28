/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.ItemArancel;
import bs.educacion.rn.ArancelRN;
import bs.educacion.rn.CarreraRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class ArancelBean extends GenericBean implements Serializable {

    private String CODIGO;
    private Arancel arancel;
    private List<Arancel> lista;
    private boolean existenRegistros;

    @EJB
    private ArancelRN arancelRN;

    @EJB
    private CarreraRN carreraRN;

    // Variables para filtros
    private Carrera carrera;
    private Integer anioLectivo;
    private List<Carrera> carreras;

    public ArancelBean() {

    }

    @PostConstruct
    public void init() {

        carreras = carreraRN.getListaByBusqueda(txtBusqueda, false, 100);
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(arancelRN.getArancel(CODIGO));
                } else {
                    //buscar();
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        existenRegistros = false;
        modoVista = "D";
        arancel = new Arancel();
        if (carrera != null) {
            arancel.setCarrera(carrera);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (arancel != null) {

                arancel = arancelRN.guardar(arancel, esNuevo);
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

    public void asignarCodigo() {

        try {
            if (esNuevo) {
                arancelRN.asignarCodigo(arancel);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código de producto " + ex);
        }
    }

    public void procesarItem(ItemArancel item) {

        if (item.getCantidad() == 1) {
            item.setCuotaDesde(1);
            item.setCuotaHasta(1);
        }

        if (item.getCantidad() > 1) {
            item.setCuotaDesde(1);
            item.setCuotaHasta(item.getCantidad());
        }

    }

    public void nuevoItemArancel() {

        try {
            arancelRN.nuevoItemArancel(arancel);
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

    public void sincronizarImportesInscripciones() {

        if (arancel != null && arancelRN.arancelUtilizado(arancel)) {

            try {
                arancelRN.sincronizarImportesInscripciones(arancel);
                JsfUtil.addInfoMessage("Importes de inscripciones actualizados correctamente");
            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("Error sincronizando inscripciones " + ex);
            }

        }

    }

    public void habilitaDeshabilita(String habDes) {

        try {
            arancel.getAuditoria().setDebaja(habDes);
            arancelRN.guardar(arancel, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            arancelRN.eliminar(arancel);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = arancelRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (carrera != null) {

            filtro.put("carrera.codigo = ", "'" + carrera.getCodigo() + "'");
        }

        if (anioLectivo != null && anioLectivo >= 0) {
            filtro.put("anioLectivo = ", "" + anioLectivo);
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaDesde >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaHasta <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    public void limpiarFiltroBusqueda() {

        txtBusqueda = "";
        mostrarDebaja = false;
        carrera = null;
        anioLectivo = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        buscar();

    }

    public List<Arancel> complete(String query) {
        try {

            lista = arancelRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Arancel>();
        }
    }

    public void onSelect(SelectEvent event) {
        arancel = (Arancel) event.getObject();

        esNuevo = false;
        existenRegistros = arancelRN.arancelUtilizado(arancel);
        modoVista = "D";
    }

    public void onSelectCarrera(SelectEvent event) {

        carrera = (Carrera) event.getObject();
        buscar();
    }

    public void seleccionar(Arancel d) {

        if (d == null) {
            return;
        }

        arancel = d;
        esNuevo = false;
        existenRegistros = arancelRN.arancelUtilizado(arancel);
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (arancel == null) {
                JsfUtil.addErrorMessage("No hay arancel activa");
                return;
            }

            arancel = arancelRN.duplicar(arancel);
            esNuevo = true;
            existenRegistros = false;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (arancel == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public List<Arancel> getLista() {
        return lista;
    }

    public void setLista(List<Arancel> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public Arancel getArancel() {
        return arancel;
    }

    public void setArancel(Arancel arancel) {
        this.arancel = arancel;
    }

    public Arancel getArea() {
        return arancel;
    }

    public void setArea(Arancel arancel) {
        this.arancel = arancel;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public List<Carrera> getCarreras() {
        return carreras;
    }

    public void setCarreras(List<Carrera> carreras) {
        this.carreras = carreras;
    }

    public boolean isExistenRegistros() {
        return existenRegistros;
    }

    public void setExistenRegistros(boolean existenRegistros) {
        this.existenRegistros = existenRegistros;
    }

}
