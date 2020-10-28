/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.web;

import bs.contrato.modelo.Contrato;
import bs.contrato.modelo.EstadoContrato;
import bs.contrato.modelo.ItemContrato;
import bs.contrato.modelo.TipoContrato;
import bs.contrato.rn.ContratoRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Moneda;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
public class ContratoBean extends GenericBean implements Serializable {

    private Integer id;
    private Contrato contrato;
    private List<Contrato> lista;

    @EJB
    private ContratoRN contratoRN;

    // Variables para filtros
    private TipoContrato tipoContrato;
    private EstadoContrato estadoContrato;
    private EntidadComercial cliente;
    private Date fechaDesde;
    private Date fechaHasta;
    private Moneda monedaRegistracion;

    public ContratoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (id != null && id != 0) {
                    seleccionar(contratoRN.getContrato(id));
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
        try {

            esNuevo = true;
            modoVista = "D";
            contrato = contratoRN.nuevo();

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible generar un nuevo Contrato " + ex);
            Logger.getLogger(ContratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (contrato != null) {

                contrato = contratoRN.guardar(contrato, esNuevo);
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
            contrato.getAuditoria().setDebaja(habDes);
            contratoRN.guardar(contrato, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            contratoRN.eliminar(contrato);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = contratoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (tipoContrato != null) {

            filtro.put("tipoContrato.codigo = ", "'" + tipoContrato.getCodigo() + "'");
        }

        if (cliente != null) {

            filtro.put("cliente.nrocta = ", "'" + cliente.getNrocta() + "'");
        }

        if (estadoContrato != null) {

            filtro.put("estado.codigo = ", "'" + estadoContrato.getCodigo() + "'");
        }

        if (monedaRegistracion != null) {

            filtro.put("monedaRegistracion.codigo = ", "'" + monedaRegistracion.getCodigo() + "'");
        }

        if (fechaDesde != null) {

            filtro.put("fechaDesde >= ", JsfUtil.getFechaSQL(fechaDesde));
        }

        if (fechaHasta != null) {

            filtro.put("fechaHasta <= ", JsfUtil.getFechaSQL(fechaHasta));
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        tipoContrato = null;
        cliente = null;
        estadoContrato = null;
        monedaRegistracion = null;
        fechaDesde = null;
        fechaHasta = null;

        buscar();

    }

    public List<Contrato> complete(String query) {
        try {

            lista = contratoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Contrato>();
        }
    }

    public void onSelect(SelectEvent event) {
        contrato = (Contrato) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Contrato c) {

        if (c == null) {
            return;
        }

        contrato = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (contrato == null) {
                JsfUtil.addErrorMessage("No hay Contrato activo");
                return;
            }

            contrato = contratoRN.duplicar(contrato);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void nuevoItem() {

        try {
            contratoRN.nuevoItem(contrato);

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarItem(ItemContrato itemContrato) {

        try {
            contratoRN.eliminarItem(contrato, itemContrato);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemContrato.getProducto().getDescripcion() != null ? itemContrato.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemContrato.getProducto().getDescripcion() != null ? itemContrato.getProducto().getDescripcion() : "") + " " + ex);
        }
    }

    public void procesarProducto(ItemContrato itemContrato) {

        if (itemContrato != null && itemContrato.getProducto() != null) {

            try {
                contratoRN.asignarProducto(itemContrato, itemContrato.getProducto());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar producto " + ex);
            }
        }
    }

    public void procesarFechaDesde(ItemContrato itemContrato) {

        if (itemContrato != null && itemContrato.getFechaDesde() != null) {

            try {
                contratoRN.procesarFechaDesde(itemContrato, contrato);

            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("Error en Fecha Desde Item ->  " + ex);
            }
        }
    }

    public void procesarFechaHasta(ItemContrato itemContrato) {

        if (itemContrato != null && itemContrato.getFechaHasta() != null) {

            try {
                contratoRN.procesarFechaHasta(itemContrato, contrato);

            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("Error en Fecha Hasta Item -> " + ex);
            }
        }
    }

    public void generarFactura() {

        List<Contrato> movimientos = contratoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 0);

    }

    public void imprimir() {

        if (contrato == null) {
            JsfUtil.addErrorMessage("No hay Contrato seleccionado, nada para imprimir");
        }
    }

    public List<Contrato> getLista() {
        return lista;
    }

    public void setLista(List<Contrato> lista) {
        this.lista = lista;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public EstadoContrato getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(EstadoContrato estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

}
