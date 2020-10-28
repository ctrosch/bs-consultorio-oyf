/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.modelo.Concepto;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.rn.ConceptoRN;
import bs.global.rn.FormularioRN;
import bs.proveedores.modelo.Comprador;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ListaCosto;
import bs.proveedores.modelo.TipoRetencion;
import bs.proveedores.rn.CompradorRN;
import bs.proveedores.rn.ConceptoRetencionRN;
import bs.proveedores.rn.CondicionPagoProveedorRN;
import bs.proveedores.rn.ListaCostoRN;
import bs.proveedores.rn.TipoRetencionRN;
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
@ManagedBean(name = "conversoresProveedor")
@ViewScoped
public class ConversoresProveedor {

    @EJB
    private ConceptoRN conceptoRN;
    @EJB
    private CondicionPagoProveedorRN condicionPagoProveedorRN;
    @EJB
    private ListaCostoRN listaCostoRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private CompradorRN compradorRN;
    @EJB
    private TipoRetencionRN tipoRetencionRN;
    @EJB
    private ConceptoRetencionRN conceptoRetencionRN;

    public Converter getComprador() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return compradorRN.getComprador(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Comprador) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCondicionPagoProveedor() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return condicionPagoProveedorRN.getCondicionPagoProveedor(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CondicionPagoProveedor) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getConceptoProveedor() {
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

    public Converter getListaCosto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return listaCostoRN.getListaCosto(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((ListaCosto) value).getCodigo() + "";
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

                FormularioPK idPK = new FormularioPK("PV", value);
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
    
    public Converter getTipoRetencion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoRetencionRN.getTipoRetencion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoRetencion) value).getCodigo() + "";
                }
            }
        };
    }
    
    public Converter getConceptoRetencion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");                
                String tipo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                return conceptoRetencionRN.getConcepto(tipo[1], codigo[1]);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {

                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((ConceptoRetencion) value).toString() + "";
                }
            }
        };
    }

}
