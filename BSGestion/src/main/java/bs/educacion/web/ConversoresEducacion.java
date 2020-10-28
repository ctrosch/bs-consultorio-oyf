/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.AreaEducacion;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.CodigoCircuitoEducacion;
import bs.educacion.modelo.Curso;
import bs.educacion.modelo.EstadoEducacion;
import bs.educacion.modelo.Materia;
import bs.educacion.modelo.ModalidadCursado;
import bs.educacion.modelo.PeriodoCursado;
import bs.educacion.modelo.PlanEstudio;
import bs.educacion.modelo.Reglamento;
import bs.educacion.modelo.TipoCarrera;
import bs.educacion.modelo.TipoCertificacion;
import bs.educacion.rn.ArancelRN;
import bs.educacion.rn.AreaEducacionRN;
import bs.educacion.rn.CarreraRN;
import bs.educacion.rn.CodigoCircuitoEducacionRN;
import bs.educacion.rn.CursoRN;
import bs.educacion.rn.EstadoEducacionRN;
import bs.educacion.rn.MateriaRN;
import bs.educacion.rn.ModalidadCursadoRN;
import bs.educacion.rn.PeriodoCursadoRN;
import bs.educacion.rn.PlanEstudioRN;
import bs.educacion.rn.ReglamentoRN;
import bs.educacion.rn.TipoCarreraRN;
import bs.educacion.rn.TipoCertificacionRN;
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
@ManagedBean(name = "conversoresEducacion")
@ViewScoped
public class ConversoresEducacion implements Serializable {

    @EJB
    private ReglamentoRN reglamentoRN;
    @EJB
    private AreaEducacionRN areaEducacionRN;
    @EJB
    private ModalidadCursadoRN modalidadCursadoRN;
    @EJB
    private TipoCertificacionRN tipoCertificacionRN;
    @EJB
    private CarreraRN carreraRN;
    @EJB
    private PlanEstudioRN planEstudioRN;
    @EJB
    private MateriaRN materiaRN;
    @EJB
    private TipoCarreraRN tipoCarreraRN;
    @EJB
    private CursoRN cursoRN;
    @EJB
    private EstadoEducacionRN estadoEducacionRN;
    @EJB
    private PeriodoCursadoRN periodoCursadoRN;
    @EJB
    private ArancelRN arancelRN;
    @EJB
    private CodigoCircuitoEducacionRN codigoCircuitoEducacionRN;

    public Converter getPeriodoCursado() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return periodoCursadoRN.getPeriodoCursado(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((PeriodoCursado) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getEstado() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return estadoEducacionRN.getEstadoEducacion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((EstadoEducacion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getArancel() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return arancelRN.getArancel(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Arancel) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getReglamento() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return reglamentoRN.getReglamento(Integer.parseInt(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((Reglamento) value).getId() + "";
                }
            }
        };
    }

    public Converter getAreaEducacion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return areaEducacionRN.getAreaEducacion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((AreaEducacion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getModalidadCursado() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return modalidadCursadoRN.getModalidadCursado(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((ModalidadCursado) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoCertificacion() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                return tipoCertificacionRN.getTipoCertificacion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {

                    return ((TipoCertificacion) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getMateria() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Materia e = materiaRN.getMateria(value);
                return e;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Materia) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCarrera() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Carrera e = carreraRN.getCarrera(value);
                return e;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Carrera) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getPlanEstudio() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                PlanEstudio e = planEstudioRN.getPlanEstudio(value);
                return e;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((PlanEstudio) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getCurso() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                Curso e = cursoRN.getCurso(value);
                return e;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Curso) value).getCodigo() + "";
                }
            }
        };
    }

    public Converter getTipoCarrera() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null) {
                    return null;
                }

                TipoCarrera t = tipoCarreraRN.getTipoCarrera(value);

                return t;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((TipoCarrera) value).getCodigo() + "";
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

                return codigoCircuitoEducacionRN.getCodigoCircuitoEducacion(value);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((CodigoCircuitoEducacion) value).getCodigo() + "";
                }
            }
        };
    }

}
