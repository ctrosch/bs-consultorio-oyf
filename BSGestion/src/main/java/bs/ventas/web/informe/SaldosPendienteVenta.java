/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.ClienteBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class SaldosPendienteVenta extends InformeBase implements Serializable {

    @ManagedProperty(value = "#{clienteBean}")
    protected ClienteBean clienteBean;

    private EntidadComercial cliente;
    private String codigoMonedaRegistracion;
    private String codigoMonedaVisualizacion;

    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public SaldosPendienteVenta() {
    }

    @PostConstruct
    @Override
    public void init() {

        super.init();
        codigoMonedaRegistracion = parametrosRN.getParametro().getCodigoMonedaPrimaria();
        codigoMonedaVisualizacion = parametrosRN.getParametro().getCodigoMonedaPrimaria();

    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        String mensaje = "";
        todoOk = true;

        if (codigoReporte == null || codigoReporte.isEmpty()) {
            mensaje = "El código de reporte no puede ser nulo";
        }

        if (!mensaje.isEmpty()) {
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {

        if (cliente != null) {
            parameters.put("NROCTA", cliente.getNrocta());
        } else {
            parameters.put("NROCTA", "");
        }

        if (codigoMonedaRegistracion != null && !codigoMonedaRegistracion.isEmpty()) {
            parameters.put("MONREG", codigoMonedaRegistracion);
        }

        if (codigoMonedaVisualizacion != null && !codigoMonedaVisualizacion.isEmpty()) {
            parameters.put("MONVIS", codigoMonedaVisualizacion);
        }

        nombreArchivo = "VT_SALDOS_PENDIENTES";
        reporte = reporteRN.getReporte(codigoReporte);

    }

    @Override
    public void resetParametros() {

        cliente = null;
        todoOk = false;
        muestraReporte = false;

    }

    public void procesarCliente() {

        if (clienteBean.getEntidad() != null) {

            cliente = clienteBean.getEntidad();
            todoOk = false;
        }
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public ClienteBean getEntidadComercialBean() {
        return clienteBean;
    }

    public void setEntidadComercialBean(ClienteBean clienteBean) {
        this.clienteBean = clienteBean;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public String getCodigoMonedaVisualizacion() {
        return codigoMonedaVisualizacion;
    }

    public void setCodigoMonedaVisualizacion(String codigoMonedaVisualizacion) {
        this.codigoMonedaVisualizacion = codigoMonedaVisualizacion;
    }

}
