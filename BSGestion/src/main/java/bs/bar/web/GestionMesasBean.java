/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.ItemMovimientoBar;
import bs.bar.modelo.Mesa;
import bs.bar.modelo.MovimientoBar;
import bs.bar.modelo.Mozo;
import bs.bar.modelo.Salon;
import bs.bar.rn.EstadoBarRN;
import bs.bar.rn.MesaRN;
import bs.bar.rn.MovimientoBarRN;
import bs.bar.rn.SalonRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class GestionMesasBean extends GenericBean implements Serializable {

    @EJB
    MesaRN mesasRN;
    @EJB
    SalonRN salonRN;
    @EJB
    MovimientoBarRN movimientoRN;
    @EJB
    private EstadoBarRN estadoRN;

    protected MovimientoBar m;
    protected Salon salon;
    protected Mesa mesa;
    protected Mozo mozo;
    protected String notas;
    protected Producto producto;
    protected double cantidad;
    protected ItemMovimientoBar item;
    protected Comprobante comprobante;

    protected List<Mesa> mesas;
    protected List<Salon> salones;
    protected Date fechaMovimiento;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    //Datos inicializacion registracion
    protected String SUCBR = "";
    protected String CODBR = "";

    /**
     * Creates a new instance of GestionMesasBean
     */
    public GestionMesasBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                iniciarMovimiento();
                modoVista = "D";
                cantidad = 1;
                salones = salonRN.getListaByBusqueda("", false, 0);
                fechaMovimiento = new Date();
//                fechaRegistracionCorrecta = selecionarFechaRegistracion();
//                fechaRegistracion = selecionarFechaRegistracion();

                if (salones != null && !salones.isEmpty()) {
                    salon = salones.get(0);
                }

                actualizarMesas();
                beanIniciado = true;

            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void iniciarMovimiento() {

        super.iniciar();

        try {

            nombreArchivo = "";
            muestraReporte = false;
//            mBusqueda = null;
//            mReversion = null;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error: iniciarMovimiento: " + ex);
        }
    }

    public void abrir() {

        try {
            if (salon == null) {
                JsfUtil.addErrorMessage("Seleccione un salón por favor");
                return;
            }

            if (mesa == null) {
                JsfUtil.addErrorMessage("Seleccione una mesa por favor");
                return;
            }

            if (mozo == null) {
                JsfUtil.addErrorMessage("Seleccione un mozo por favor");
                return;
            }

            iniciarMovimiento();

            m = movimientoRN.nuevoMovimiento(CODBR, SUCBR, new Date());
            m.setSalon(salon);
            m.setMesa(mesa);
            m.setMozo(mozo);
            m.setEstado(estadoRN.getEstadoBar("B"));

            movimientoRN.guardar(m);

            actualizarMesas();

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para abrir la mesa " + ex);
            Logger.getLogger(GestionMesasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void facturarMesa() {
        try {
            if (mesa == null) {
                JsfUtil.addErrorMessage("No existe una mesa seleccionada");
                return;
            }
            if (m == null) {
                JsfUtil.addErrorMessage("No existe movimiento a cerrar");
                return;
            }
            m.setEstado(estadoRN.getEstadoBar("C"));
            movimientoRN.cerrarMesa(m);
            m = null;
            JsfUtil.addInfoMessage("La mesa fue cerrada exitosamente");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al cerrar la mesa " + ex.getMessage());
        }
    }

    public void cerrarMesa() {
        try {
            if (mesa == null) {
                JsfUtil.addErrorMessage("No existe una mesa seleccionada");
                return;
            }
            if (m == null) {
                JsfUtil.addErrorMessage("No existe movimiento a cerrar");
                return;
            }
            movimientoRN.cerrarMesa(m);
            m.setEstado(estadoRN.getEstadoBar("A"));
            m = null;

            JsfUtil.addInfoMessage("La mesa fue cerrada exitosamente");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al cerrar la mesa " + ex.getMessage());
        }
    }

    public void actualizarMesas() {

        mesas = mesasRN.getListaBySalon(salon, false, 0);
        mesas = mesasRN.sincronizarEstadoMesas(mesas);
    }

    public void seleccionarMesa(Mesa m) {

        this.mesa = m;

        if (mesa.getMovimiento() != null) {
            this.m = mesa.getMovimiento();
        } else {
            this.m = null;
        }
    }

    public void agregarItem() {
        try {

            item = movimientoRN.nuevoItemMovimiento(m);
            item.setCantidad(cantidad);
            item.setProducto(producto);
            item.setDescripcion(producto.getDescripcion());
            item.setPrecio(100);
            item.setTotalLinea(100);
            m.getItems().add(item);

            cantidad = 0;
            producto = null;

            movimientoRN.guardar(m);

            JsfUtil.addInfoMessage("El produco se agregó correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public void eliminarItem(ItemMovimientoBar nItem) {

        try {
            if (movimientoRN.eliminarItem(m, nItem)) {
                JsfUtil.addWarningMessage("Se ha borrado el item " + nItem.getProducto().getDescripcion() + "");
            } else {
                JsfUtil.addWarningMessage("No se ha borrado el item " + nItem.getProducto().getDescripcion() + "");
            }
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(GestionMesasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //--------------------------------------------------------------------------
    public MovimientoBarRN getMovimientoRN() {
        return movimientoRN;
    }

    public void setMovimientoRN(MovimientoBarRN movimientoRN) {
        this.movimientoRN = movimientoRN;
    }

    public MovimientoBar getM() {
        return m;
    }

    public void setM(MovimientoBar m) {
        this.m = m;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Mozo getMozo() {
        return mozo;
    }

    public void setMozo(Mozo mozo) {
        this.mozo = mozo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public ItemMovimientoBar getItem() {
        return item;
    }

    public void setItem(ItemMovimientoBar item) {
        this.item = item;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(List<Mesa> mesas) {
        this.mesas = mesas;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public String getSUCBR() {
        return SUCBR;
    }

    public void setSUCBR(String SUCBR) {
        this.SUCBR = SUCBR;
    }

    public String getCODBR() {
        return CODBR;
    }

    public void setCODBR(String CODBR) {
        this.CODBR = CODBR;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public MesaRN getMesasRN() {
        return mesasRN;
    }

    public void setMesasRN(MesaRN mesasRN) {
        this.mesasRN = mesasRN;
    }

    public List<Salon> getSalones() {
        return salones;
    }

    public void setSalones(List<Salon> salones) {
        this.salones = salones;
    }

}
