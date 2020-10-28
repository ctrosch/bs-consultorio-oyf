/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.rn.ModuloRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Concepto;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.Pais;
import bs.global.modelo.Periodo;
import bs.global.modelo.Provincia;
import bs.global.modelo.ProvinciaPK;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoComprobante;
import bs.global.modelo.TipoComprobantePK;
import bs.global.modelo.TipoConcepto;
import bs.global.modelo.TipoConceptoPK;
import bs.global.modelo.TipoDocumento;
import bs.global.modelo.TipoImpuesto;
import bs.global.modelo.UnidadNegocio;
import bs.global.modelo.Zona;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.ConceptoRN;
import bs.global.rn.CondicionDeIvaRN;
import bs.global.rn.EmpresaRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.LocalidadRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PaisRN;
import bs.global.rn.PeriodoRN;
import bs.global.rn.ProvinciaRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.rn.SucursalRN;
import bs.global.rn.TipoComprobanteRN;
import bs.global.rn.TipoConceptoRN;
import bs.global.rn.TipoDocumentoRN;
import bs.global.rn.TipoImpuestoRN;
import bs.global.rn.UnidadNegocioRN;
import bs.global.rn.ZonaRN;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author ctrosch
 */
@ManagedBean(name = "conversoresGeneral")
@ViewScoped
public class ConversoresGeneral implements Serializable {

    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private LocalidadRN localidadRN;
    @EJB
    private CondicionDeIvaRN condicionDeIvaRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private PaisRN paisRN;
    @EJB
    private ProvinciaRN provinciaRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    private SucursalRN sucursalRN;
    @EJB
    private UnidadNegocioRN unidadNegocioRN;
    @EJB
    private TipoConceptoRN tipoConceptoRN;
    @EJB
    private TipoComprobanteRN tipoComprobanteRN;
    @EJB
    private TipoDocumentoRN tipoDocumentoRN;
    @EJB
    private ZonaRN zonaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private TipoImpuestoRN tipoImpuestoRN;
    @EJB
    private ConceptoRN conceptoRN;
    @EJB
    private PeriodoRN periodoRN;
    @EJB
    private EmpresaRN empresaRN;

    /**
     * Creates a new instance of ConversoresBean
     */
    public ConversoresGeneral() {

    }

    public Converter getComprobante() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String modcom[] = a[0].split("=");
                String codcom[] = a[1].split("=");

                Comprobante c = null;
                try {
                    c = comprobanteRN.getComprobante(modcom[1], codcom[1]);
                } catch (ExcepcionGeneralSistema ex) {
                    Logger.getLogger(ConversoresGeneral.class.getName()).log(Level.SEVERE, null, ex);
                }
                return c;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return "modulo=" + ((Comprobante) value).getModulo() + ", codigo=" + ((Comprobante) value).getCodigo() + "";

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

                String a[] = value.split(",");
                String modulo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                Formulario f = formularioRN.getFormulario(modulo[1], codigo[1]);
                return f;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return "modulo=" + ((Formulario) value).getModfor() + ", codigo=" + ((Formulario) value).getCodigo() + "";

                }
            }
        };
    }

    public Converter getConcepto() {
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

    public Converter getLocalidad() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return localidadRN.getLocalidad(Integer.parseInt(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Localidad) value).getId() + "";
                }
            }
        };
    }

    public Converter getCondicionDeIva() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return condicionDeIvaRN.getCondicionDeIva(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((CondicionDeIva) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getMoneda() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return monedaRN.getMoneda(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Moneda) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPais() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return paisRN.getPais(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Pais) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getProvincia() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                String a[] = value.split(",");
                String pais[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                ProvinciaPK idPK = new ProvinciaPK(pais[1], codigo[1]);

                Provincia p = provinciaRN.getProvincia(idPK);

                return p;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return "codpai=" + ((Provincia) value).getCodpai() + ",codigo=" + ((Provincia) value).getCodigo();
                }
            }
        };
    }

    public Converter getPuntoVenta() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return puntoVentaRN.getPuntoVenta(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((PuntoVenta) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getSucursal() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return sucursalRN.getSucursal(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Sucursal) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getUnidadNegocio() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return unidadNegocioRN.getUnidadNegocio(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((UnidadNegocio) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoConcepto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }
                String a[] = value.split(",");
                String modulo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                TipoConceptoPK idPK = new TipoConceptoPK(modulo[1], codigo[1]);
                return tipoConceptoRN.getTipoConcepto(idPK);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoConcepto) value).toString() + "";
                }
            }
        };
    }

    public Converter getTipoComprobante() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }
                String a[] = value.split(",");
                String modulo[] = a[0].split("=");
                String codigo[] = a[1].split("=");

                TipoComprobantePK idPK = new TipoComprobantePK(modulo[1], codigo[1]);
                return tipoComprobanteRN.getTipoComprobante(idPK);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoComprobante) value).toString() + "";
                }
            }
        };
    }

    public Converter getTipoDocumento() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoDocumentoRN.getTipoDocumento(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoDocumento) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getZona() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return zonaRN.getZona(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Zona) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getModulo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return moduloRN.getModulo(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Modulo) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoImpuesto() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoImpuestoRN.getTipoImpuesto(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoImpuesto) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPeriodo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Periodo d = periodoRN.getPeriodo(Integer.valueOf(value));
                return d;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Periodo) value).getId() + "";
                }
            }
        };
    }

    public Converter getEmpresa() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return empresaRN.getEmpresa(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Empresa) value).getCodigo() + "";
                }
            }
        };
    }

}
