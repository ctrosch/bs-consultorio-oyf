/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Categoria;
import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadActividadComercial;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.EntidadTransporte;
import bs.entidad.modelo.EstadoEntidad;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.entidad.modelo.Origen;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.CategoriaRN;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.OrigenRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.modelo.Localidad;
import bs.global.modelo.Sucursal;
import bs.global.rn.SucursalRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.ventas.modelo.CanalVenta;
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
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ClienteBean extends GenericBean implements Serializable {

    @EJB
    private EntidadRN entidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;
    @EJB
    private CategoriaRN categoriaRN;
    @EJB
    private OrigenRN origenRN;
    @EJB
    private SucursalRN sucursalRN;

    private List<EntidadComercial> lista;
    private EntidadComercial entidad;
    private String nrocta;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;
    private final String CODTIP = "1";

    private boolean mostrarImpuestoDebaja;
    private boolean mostrarRetencionDebaja;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    private MapModel simpleModel;

    // Variables para filtros
    private EstadoEntidad estado;
    private Sucursal sucursal;
    private CanalVenta canalVenta;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public ClienteBean() {

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
//            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void nuevoItemDireccionEntrega() {

        entidadRN.nuevoItemDireccionEntrega(entidad, false);
    }

    public void onLocalidadDireccionEntregaSelect(SelectEvent event) {

        entidadRN.asignarProvinciaDireccionEntrega(entidad);
    }

    public void eliminarItemDireccionEntrega(DireccionEntregaEntidad direccionEntrega) {

        try {
            entidadRN.eliminarItemDireccionEntrega(direccionEntrega);
            JsfUtil.addWarningMessage("Se ha borrado el item");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public void nuevoItemImpuesto() {

        entidadRN.nuevoItemImpuesto(entidad);
    }

    public void eliminarItemImpuesto(ImpuestoPorEntidad itemImpuesto) {

        try {
            entidadRN.eliminarItemImpuesto(entidad, itemImpuesto);
            JsfUtil.addWarningMessage("Se ha borrado el item");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public void nuevoContacto() {

        if (entidad != null) {

            if (entidad.getContactos() == null) {
                entidad.setContactos(new ArrayList<Contacto>());
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

    private void generoDireccionEntrega() {

        if (esNuevo && entidad.getDireccionesDeEntrega() != null) {
            entidad.getDireccionesDeEntrega().clear();
        }

        entidadRN.nuevoItemDireccionEntrega(entidad, true);

    }

    public void nuevoItemActividad() {

        entidadRN.nuevoItemActividad(entidad);
    }

    public void eliminarItemActividad(EntidadActividadComercial itemActividad) {

        try {
            entidadRN.eliminarItemActividad(entidad, itemActividad);
            JsfUtil.addWarningMessage("Se ha borrado el item");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    public void nuevoItemTransporte() {

        entidadRN.nuevoItemTransporte(entidad);
    }

    public void eliminarItemTransporte(EntidadTransporte itemTransporte) {

        try {
            entidadRN.eliminarItemTransporte(entidad, itemTransporte);
            JsfUtil.addWarningMessage("Se ha borrado el item");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
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

        if (estado != null) {

            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }

        if (sucursal != null) {
            filtro.put("sucursal.codigo = ", "'" + sucursal.getCodigo() + "'");
        }

        if (canalVenta != null) {
            filtro.put("canalVenta.codigo = ", "'" + canalVenta.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        estado = null;
        sucursal = null;
        canalVenta = null;
        buscar();

    }

    public void onSelect(SelectEvent event) {

        entidad = (EntidadComercial) event.getObject();
        esNuevo = false;
        modoVista = "D";
        generarMarcadorMapa();
    }

    public void seleccionar(EntidadComercial e) {

        if (e == null) {
            return;
        }

        entidad = e;
        esNuevo = false;
        modoVista = "D";
        generarMarcadorMapa();

    }

    public List<EntidadComercial> complete(String query) {
        try {

            lista = entidadRN.getListaByBusqueda(query, tipoEntidad, mostrarDebaja, 10);
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

    public List<Categoria> getListaCategoria() {
        return categoriaRN.getCategoriaByBusqueda("", tipoEntidad, false);
    }

    public List<Origen> getListaOrigen() {
        return origenRN.getOrigenByBusqueda("", tipoEntidad, false);
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

    public boolean isMostrarImpuestoDebaja() {
        return mostrarImpuestoDebaja;
    }

    public void setMostrarImpuestoDebaja(boolean mostrarImpuestoDebaja) {
        this.mostrarImpuestoDebaja = mostrarImpuestoDebaja;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
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

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

}
