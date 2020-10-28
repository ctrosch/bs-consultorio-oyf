/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.facturacion.rn.CodigoCircuitoFacturacionRN;
import bs.global.modelo.Concepto;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.rn.ConceptoRN;
import bs.global.rn.FormularioRN;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.modelo.Cobrador;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
import bs.ventas.rn.CanalVentaRN;
import bs.ventas.rn.CobradorRN;
import bs.ventas.rn.CondicionPagoVentaRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import bs.ventas.rn.RepartidorRN;
import bs.ventas.rn.VendedorRN;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Claudio
 */
@ManagedBean(name = "conversoresVenta")
@ViewScoped
public class ConversoresVenta implements Serializable {

    @EJB
    private CobradorRN cobradorRN;
    @EJB
    private ConceptoRN conceptoRN;
    @EJB
    private CondicionPagoVentaRN condicionDePagoVentRN;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;
    @EJB
    private VendedorRN vendedorRN;
    @EJB
    private CanalVentaRN canalVentaRN;
    @EJB
    private RepartidorRN repartidorRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private CodigoCircuitoFacturacionRN codigoCircuitoFacturacionRN;

    public Converter getCobrador() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return cobradorRN.getCobrador(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Cobrador) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCondicionDePagoVenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return condicionDePagoVentRN.getCondicionDePagoVenta(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CondicionDePagoVenta) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getConceptoVenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String modulo[] = a[0].split("=");
                String tipo[] = a[1].split("=");
                String codigo[] = a[2].split("=");

                return conceptoRN.getConcepto(modulo[1], tipo[1], codigo[1]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {

                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Concepto) value).toString() + "";
                }
            }
        };
    }

    public Converter getListaPrecioVenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return listaPrecioRN.getListaPrecio(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((ListaPrecioVenta) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getVendedor() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return vendedorRN.getVendedor(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Vendedor) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCanalVenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return canalVentaRN.getCanalVenta(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CanalVenta) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getRepartidor() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return repartidorRN.getRepartidor(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Repartidor) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getFormulario() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                FormularioPK idPK = new FormularioPK("VT", value);
                return formularioRN.getFormulario(idPK);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Formulario) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCodigoCircuito() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return codigoCircuitoFacturacionRN.getCodigoCircuitoFacturacion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CodigoCircuitoFacturacion) value).getCodigo() + "";
                }
            }
        };
    }

}
