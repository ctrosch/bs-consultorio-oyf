/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.entidad.modelo.RetencionPorEntidad;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.CategoriaRN;
import bs.entidad.rn.DireccionEntregaRN;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.EstadoEntidadRN;
import bs.entidad.rn.ImpuestoPorEntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.modelo.Localidad;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.proveedores.rn.CompradorRN;
import bs.proveedores.rn.CondicionPagoProveedorRN;
import bs.proveedores.rn.ListaCostoRN;
import bs.ventas.rn.CobradorRN;
import bs.ventas.rn.CondicionPagoVentaRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import bs.ventas.rn.VendedorRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
public class EntidadComercialBean extends GenericBean implements Serializable {

    @EJB
    private EntidadRN entidadRN;
    @EJB
    private EstadoEntidadRN estadoEntidadRN;
    @EJB
    private CategoriaRN categoriaRN;
    @EJB
    private DireccionEntregaRN direccionEntregaRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;
    @EJB
    private CompradorRN compradorRN;
    @EJB
    private VendedorRN vendedorRN;
    @EJB
    private CobradorRN cobradorRN;
    @EJB
    private ListaCostoRN listaCostoRN;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;
    @EJB
    private CondicionPagoProveedorRN condicionPagoProveedorRN;
    @EJB
    private CondicionPagoVentaRN condicionPagoVentaRN;
    @EJB
    private ImpuestoPorEntidadRN impuestoPorEntidadRN;

    private List<EntidadComercial> lista;
    private EntidadComercial entidad;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;

    private boolean mostrarDireccionDebaja;
    private boolean mostrarImpuestoDebaja;
    private boolean mostrarRetencionDebaja;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{impuestoPorEntidadBean}")
    protected ImpuestoPorEntidadBean impuestoPorEntidadBean;

    private MapModel simpleModel;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public EntidadComercialBean() {

    }

//    @PostConstruct
    public void init(String codTipo) {

        if (!beanIniciado) {

            txtBusqueda = "";
            mostrarDebaja = false;
            mostrarDireccionDebaja = false;

            tipos = tipoEntidadRN.getListaByBusqueda(false);

            if (codTipo != null) {
                tipoEntidad = tipoEntidadRN.getTipoEntidad(codTipo);
            }

            if (tipoEntidad == null) {
                if (!tipos.isEmpty()) {
                    tipoEntidad = tipos.get(0);
                }
            }

            buscar();
            beanIniciado = true;
        }
    }

    public void nuevo() {

        if (tipoEntidad != null) {

            entidad = new EntidadComercial();
            //Aplicamos valores por defecto;
            entidad.setNrocta(entidadRN.getProximoNroCuenta(""));
            entidad.setTipo(tipoEntidad);

            esNuevo = true;
            buscaMovimiento = false;
        }
    }

    public void nuevaDireccionEntrega() {

        if (entidad != null) {

            if (entidad.getDireccionesDeEntrega() == null) {
                entidad.setDireccionesDeEntrega(new ArrayList<DireccionEntregaEntidad>());
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
                entidad.setImpuestos(new ArrayList<ImpuestoPorEntidad>());
            }
            impuestoPorEntidadBean.nuevo(entidad);

        } else {

            JsfUtil.addErrorMessage("No existe una entidad para asociarle un impuesto");
        }
    }

    public void nuevoContacto() {

        if (entidad != null) {

            if (entidad.getImpuestos() == null) {
                entidad.setImpuestos(new ArrayList<ImpuestoPorEntidad>());
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

    public void sincronizarRetenciones() {

        if (entidad != null) {

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

    public void guardar(boolean nuevo) {
        try {
            if (entidad != null) {

                if (esNuevo) {
                    asignarValoresPorDefecto();
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
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
        lista = entidadRN.getListaByBusqueda(txtBusqueda, tipoEntidad, mostrarDebaja, cantidadRegistros);
    }

    public void onSelect(SelectEvent event) {

        entidad = (EntidadComercial) event.getObject();
        sincronizarDireccionEntrega();
        esNuevo = false;
        buscaMovimiento = false;
        generarMarcadorMapa();
    }

    public void seleccionar(EntidadComercial e) {

        if (e == null) {
            return;
        }

        entidad = e;
        sincronizarDireccionEntrega();
        sincronizarImpuesto();
        sincronizarRetenciones();
        esNuevo = false;
        buscaMovimiento = false;
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

        if (localidadBean.getLocalidad() != null && entidad != null) {

            entidad.setLocalidad(localidadBean.getLocalidad());
            entidad.setProvincia(localidadBean.getLocalidad().getProvincia());
        }
    }

//    public void procesarTransporte(){
//
//        if(transporteBean.getTransporte()!=null && entidad!=null){
//            entidad.setTransporte(transporteBean.getTransporte());
//        }
//    }
    private void asignarValoresPorDefecto() {

        entidad.setNrosub(entidad.getNrocta());

        entidad.setEstado(estadoEntidadRN.getEstadoEntidad("N"));
        entidad.setCategoria(categoriaRN.getCategoria("01", tipoEntidad));

        //A clientes asigno datos para utilizarlo como proveedor
        if (entidad.getTipo().getCodigo().equals("1")) {

            entidad.setComprador(compradorRN.getComprador("99"));
            entidad.setCondicionPagoProveedor(condicionPagoProveedorRN.getCondicionPagoProveedor("X"));
            entidad.setListaCosto(listaCostoRN.getListaCosto("X"));
        }

        //A proveedores asigno datos para utilizarlo como cliente
        if (entidad.getTipo().getCodigo().equals("2")) {

            entidad.setVendedor(vendedorRN.getVendedor("99"));
            entidad.setCobrador(cobradorRN.getCobrador("99"));
            entidad.setCondicionDePagoVentas(condicionPagoVentaRN.getCondicionDePagoVenta("X"));
            entidad.setListaDePrecioVenta(listaPrecioRN.getListaPrecio("X"));
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

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
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

}
