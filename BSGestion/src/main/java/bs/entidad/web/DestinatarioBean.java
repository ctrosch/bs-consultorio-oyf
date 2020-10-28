/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.EstadoEntidad;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.entidad.modelo.RetencionPorEntidad;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.DireccionEntregaRN;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.ImpuestoPorEntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.modelo.Localidad;
import bs.global.modelo.Sucursal;
import bs.global.rn.SucursalRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.GeocodeResult;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class DestinatarioBean extends GenericBean implements Serializable {

    @EJB
    private EntidadRN entidadRN;
    @EJB
    private DireccionEntregaRN direccionEntregaRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;
    @EJB
    private ImpuestoPorEntidadRN impuestoPorEntidadRN;
    @EJB
    private SucursalRN sucursalRN;

    private List<EntidadComercial> lista;
    private EntidadComercial entidad;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;
    private String nrocta;
    private final String CODTIP = "3";

    private boolean mostrarDireccionDebaja;
    private boolean mostrarImpuestoDebaja;
    private boolean mostrarRetencionDebaja;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{impuestoPorEntidadBean}")
    protected ImpuestoPorEntidadBean impuestoPorEntidadBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    private MapModel simpleModel;
    private String centerGeoMap = "41.850033, -87.6500523";
    private MapModel geoModel;

    // Variables para filtros
    private EstadoEntidad estado;
    private Sucursal sucursal;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public DestinatarioBean() {

    }

    @PostConstruct
    public void init() {
        tipoEntidad = tipoEntidadRN.getTipoEntidad(CODTIP);
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                if (nrocta != null && !nrocta.isEmpty()) {
                    seleccionar(entidadRN.getEntidad(nrocta));
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

            System.err.println("Nuevo");
            entidad = entidadRN.nuevo(CODTIP);
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible crear una nueva entidad " + ex);
        }
    }

    public void guardar(boolean nuevo) {
        try {
            if (entidad != null) {

                if (esNuevo) {
                    generoDireccionEntrega();
                }

                entidad = entidadRN.guardar(entidad, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                } else {
                    generarMarcadorMapa();
                }

            } else {
                JsfUtil.addErrorMessage("No hay datos para guardar");
            }

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void nuevaDireccionEntrega() {

        if (entidad != null) {

            if (entidad.getDireccionesDeEntrega() == null) {
                entidad.setDireccionesDeEntrega(new ArrayList<>());
            }

            String newCodigo = direccionEntregaRN.generarCodigo(entidad);
            String newDescrp = "Dirección " + newCodigo;

            DireccionEntregaEntidad direccionEntrega = new DireccionEntregaEntidad(newCodigo, entidad.getNrocta());
            direccionEntrega.setDescripcion(newDescrp);

            direccionEntregaBean.nuevo(direccionEntrega);

        } else {

            JsfUtil.addErrorMessage("No existe una entidad para asociarle una dirección de entrega");
        }
    }

    public void nuevoImpuesto() {

        if (entidad != null) {

            if (entidad.getImpuestos() == null) {
                entidad.setImpuestos(new ArrayList<>());
            }
            impuestoPorEntidadBean.nuevo(entidad);

        } else {

            JsfUtil.addErrorMessage("No existe una entidad para asociarle un impuesto");
        }
    }

    public void nuevoContacto() {

        if (entidad != null) {

            if (entidad.getImpuestos() == null) {
                entidad.setImpuestos(new ArrayList<>());
            }

            Contacto contacto = new Contacto();
            contacto.setEntidadComercial(entidad);
            entidad.getContactos().add(contacto);

        } else {
            JsfUtil.addErrorMessage("No existe una entidad para asociarle un contacto");
        }
    }

    public void eliminarContacto(Contacto contacto) {

        try {
            entidadRN.eliminarContacto(entidad, contacto);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible eliminar el contacto " + ex);
        }
    }

    public void nuevaRetencion() {

        if (entidad != null) {

            if (entidad.getDireccionesDeEntrega() == null) {
                entidad.setRetenciones(new ArrayList<RetencionPorEntidad>());
            }

            int nroItem = entidad.getRetenciones().size() + 1;
            RetencionPorEntidad retencionEntidad = new RetencionPorEntidad(nroItem, entidad.getNrocta());

        } else {

            JsfUtil.addErrorMessage("No existe un entidad para asociar una retención");
        }
    }

    public void seleccionarDireccionEntrega(DireccionEntregaEntidad de) {

        if (entidad != null) {

            direccionEntregaBean.seleccionar(de);

        } else {

            JsfUtil.addErrorMessage("No existe una entidad para asociarle una dirección de entrega");
        }
    }

    public void seleccionarImpuesto(ImpuestoPorEntidad i) {

        if (entidad != null) {

            impuestoPorEntidadBean.seleccionar(i);

        } else {

            JsfUtil.addErrorMessage("No existe una entidad para asociarle una dirección de entrega");
        }
    }

    public void seleccionarRetencion(RetencionPorEntidad re) {

//        if(entidad!=null){
//
//            retencionEntidadBean.seleccionar(re);
//
//        }else{
//
//            JsfUtil.addErrorMessage("No existe una entidad para asociar una retención");
//        }
    }

    public void sincronizarDireccionEntrega() {

        if (entidad != null) {
            entidad.setDireccionesDeEntrega(direccionEntregaRN.getListaByNroCuenta(entidad.getNrocta(), mostrarDireccionDebaja));
        }
    }

    public void sincronizarImpuesto() {

        if (entidad != null) {
            entidad.setImpuestos(impuestoPorEntidadRN.getListaByNroCuenta(entidad.getNrocta(), mostrarImpuestoDebaja));
        }
    }

    private void generoDireccionEntrega() {

        if (entidad.getDireccionesDeEntrega() == null) {
            entidad.setDireccionesDeEntrega(new ArrayList<DireccionEntregaEntidad>());
        } else {
            entidad.getDireccionesDeEntrega().clear();
        }

        String newCodigo = direccionEntregaRN.generarCodigo(entidad);
        String newDescrp = "Dirección " + newCodigo;

        DireccionEntregaEntidad direccionEntrega = new DireccionEntregaEntidad(newCodigo, entidad.getNrocta());
        direccionEntrega.setDescripcion(newDescrp);
        direccionEntrega.setDireccion(entidad.getDireccion());
        direccionEntrega.setNumero(entidad.getNumero());
        direccionEntrega.setDepartamentoPiso(entidad.getDepartamentoPiso());
        direccionEntrega.setDepartamentoNumero(entidad.getDepartamentoNumero());
        direccionEntrega.setLocalidad(entidad.getLocalidad());
        direccionEntrega.setProvincia(entidad.getProvincia());

        entidad.getDireccionesDeEntrega().add(direccionEntrega);
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            entidad.getAuditoria().setDebaja(habDes);
            entidad = entidadRN.guardar(entidad, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            entidadRN.eliminar(entidad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();
        lista = entidadRN.getListaByBusqueda(filtro, txtBusqueda, tipoEntidad, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos solo destinatarios por unidad de negocio a la que pertenece el usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

        if (sucursal != null) {

            filtro.put("sucursal.codigo = ", "'" + sucursal.getCodigo() + "'");
        }

        if (estado != null) {

            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        estado = null;
        sucursal = null;
        buscar();

    }

    public void onSelect(SelectEvent event) {

        entidad = (EntidadComercial) event.getObject();
        sincronizarDireccionEntrega();
        esNuevo = false;
        modoVista = "D";
        generarMarcadorMapa();
    }

    public void seleccionar(EntidadComercial e) {

        if (e == null) {
            return;
        }

        entidad = e;
        sincronizarDireccionEntrega();
        sincronizarImpuesto();
        esNuevo = false;
        modoVista = "D";
        generarMarcadorMapa();

    }

    public List<EntidadComercial> complete(String query) {
        try {
            cargarFiltroBusqueda();
            lista = entidadRN.getListaByBusqueda(filtro, query, tipoEntidad, mostrarDebaja, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EntidadComercial>();
        }
    }

    public void onSelectLocalidad(SelectEvent event) {

        entidad.setLocalidad((Localidad) event.getObject());
        entidad.setProvincia(entidad.getLocalidad().getProvincia());
    }

    public void onSelectNota(String dato) {

        entidad.setNotas((dato == null ? "" : dato));
    }

    public void procesarLocalidad() {

        if (entidad != null && entidad.getLocalidad() != null) {

            entidad.setProvincia(entidad.getLocalidad().getProvincia());
        }
    }

    public void procesarLocalidadDlg() {

        if (entidad != null && localidadBean.getLocalidad() != null) {

            entidad.setLocalidad(localidadBean.getLocalidad());
            entidad.setProvincia(entidad.getLocalidad().getProvincia());
        }
    }

    public void generarMarcadorMapa() {

        simpleModel = new DefaultMapModel();

        if (entidad == null) {
            return;
        }
        if (entidad.getCoordenadaLatitud() == null || entidad.getCoordenadaLongitud() == null) {
            return;
        }

        LatLng coord = new LatLng(Double.valueOf(entidad.getCoordenadaLatitud().toString()), Double.valueOf(entidad.getCoordenadaLongitud().toString()));
        //Basic marker
        simpleModel.addOverlay(new Marker(coord, entidad.getNombreFantasia()));

    }

    public void onGeocode(GeocodeEvent event) {
        List<GeocodeResult> results = event.getResults();

        if (results != null && !results.isEmpty()) {
            LatLng center = results.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < results.size(); i++) {
                GeocodeResult result = results.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
            }
        }
    }

    public List<EntidadComercial> getLista() {
        return lista;
    }

    public void setLista(List<EntidadComercial> lista) {
        this.lista = lista;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    public DireccionEntregaBean getDireccionEntregaBean() {
        return direccionEntregaBean;
    }

    public void setDireccionEntregaBean(DireccionEntregaBean direccionEntregaBean) {
        this.direccionEntregaBean = direccionEntregaBean;
    }

    public boolean isMostrarDireccionDebaja() {
        return mostrarDireccionDebaja;
    }

    public void setMostrarDireccionDebaja(boolean mostrarDireccionDebaja) {
        this.mostrarDireccionDebaja = mostrarDireccionDebaja;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public List<TipoEntidad> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoEntidad> tipos) {
        this.tipos = tipos;
    }

    public MapModel getSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(MapModel simpleModel) {
        this.simpleModel = simpleModel;
    }

    public boolean isMostrarRetencionDebaja() {
        return mostrarRetencionDebaja;
    }

    public void setMostrarRetencionDebaja(boolean mostrarRetencionDebaja) {
        this.mostrarRetencionDebaja = mostrarRetencionDebaja;
    }

    public ImpuestoPorEntidadBean getImpuestoPorEntidadBean() {
        return impuestoPorEntidadBean;
    }

    public void setImpuestoPorEntidadBean(ImpuestoPorEntidadBean impuestoPorEntidadBean) {
        this.impuestoPorEntidadBean = impuestoPorEntidadBean;
    }

    public boolean isMostrarImpuestoDebaja() {
        return mostrarImpuestoDebaja;
    }

    public void setMostrarImpuestoDebaja(boolean mostrarImpuestoDebaja) {
        this.mostrarImpuestoDebaja = mostrarImpuestoDebaja;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public void setCenterGeoMap(String centerGeoMap) {
        this.centerGeoMap = centerGeoMap;
    }

    public MapModel getGeoModel() {
        return geoModel;
    }

    public void setGeoModel(MapModel geoModel) {
        this.geoModel = geoModel;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public EstadoEntidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntidad estado) {
        this.estado = estado;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

}
