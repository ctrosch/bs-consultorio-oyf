/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Sucursal;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.EstadoUsuario;
import bs.seguridad.modelo.Grupo;
import bs.seguridad.modelo.ItemGrupoUsuario;
import bs.seguridad.modelo.ItemSucursalUsuario;
import bs.seguridad.modelo.TipoUsuario;
import bs.seguridad.modelo.Usuario;
import bs.seguridad.rn.EstadoUsuarioRN;
import bs.seguridad.rn.UsuarioRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class UsuarioBean extends GenericBean implements Serializable {

    private Integer id;
    private Usuario usuario;
    private List<Usuario> lista;

    @EJB
    private UsuarioRN usuarioRN;
    @EJB
    private EstadoUsuarioRN estadoUsuarioRN;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    // Variables para filtros
    private EstadoUsuario estado;
    private TipoUsuario tipo;
    private Grupo grupo;
    private Sucursal sucursal;

    public UsuarioBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (id != null && id > 0) {
                    seleccionar(usuarioRN.getUsuario(id));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
//            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        if (usuarioSessionBean.estaRegistrado
                && usuarioSessionBean.getUsuario() != null
                && usuarioSessionBean.getUsuario().getTipo().getId() != 1) {

            JsfUtil.addWarningMessage("", "Solo el administrador puede agregar usuarios");
        }
        esNuevo = true;
        modoVista = "D";
        buscaMovimiento = false;
        usuario = new Usuario();
    }

    public void guardar(boolean nuevo) {

        try {
            if (usuario != null) {

                if (usuarioSessionBean.estaRegistrado
                        && usuarioSessionBean.getUsuario() != null
                        && usuarioSessionBean.getUsuario().getTipo().getId() != 1
                        && esNuevo) {
                    JsfUtil.addWarningMessage("", "Solo el administrador puede agregar usuarios");
                    return;
                }

                usuario = usuarioRN.guardar(usuario);
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

    public void nuevoItemGrupo() {

        try {
            usuarioRN.nuevoItemGrupo(usuario);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item grupo: " + ex);
        }
    }

    public void eliminarItemGrupo(ItemGrupoUsuario itemGrupo) {

        try {
            usuarioRN.eliminarItemGrupo(usuario, itemGrupo);
            JsfUtil.addWarningMessage("Se ha borrado el item grupo " + (itemGrupo.getGrupo() != null ? itemGrupo.getGrupo().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemGrupo.getGrupo() != null ? itemGrupo.getGrupo().getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoItemSucursal() {

        try {
            usuarioRN.nuevoItemSucursal(usuario);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sucursal: " + ex);
        }
    }

    public void eliminarItemSucursal(ItemSucursalUsuario item) {

        try {
            usuarioRN.eliminarItemSucursal(usuario, item);
            JsfUtil.addWarningMessage("Se ha borrado el item sucursal " + (item.getSucursal() != null ? item.getSucursal().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getSucursal() != null ? item.getSucursal().getNombre() : "") + " " + ex);
        }
    }

//    public void asignarGrupo() {
//
//        if (grupo == null) {
//            JsfUtil.addErrorMessage("Seleccione el grupo a asignar al usuario");
//            return;
//        }
//
//        for (ItemGrupoUsuario itemGrupo : usuario.getGrupos()) {
//
//            if (itemGrupo.getGrupo().equals(grupo)) {
//                JsfUtil.addErrorMessage("El ususario ya tiene asignado el grupo " + grupo.getDescripcion());
//                return;
//            }
//        }
//
//        try {
//            ItemGrupoUsuario itemGrupo = new ItemGrupoUsuario();
//
//            itemGrupo.setCodGrupo(grupo.getCodigo());
//            itemGrupo.setGrupo(grupo);
//            itemGrupo.setIdUsuario(usuario.getId());
//            itemGrupo.setUsuario(usuario);
//
//            usuario.getGrupos().add(itemGrupo);
//
//            usuarioRN.guardar(usuario);
//            grupo = null;
//            JsfUtil.addInfoMessage("El grupo fue asignado correctamente");
//
//        } catch (Exception ex) {
//            JsfUtil.addInfoMessage("No es posible asignar grupo al usuario " + ex);
//        }
//    }
    public void habilitaDeshabilita(String habDes) {

        try {

            if (habDes.equals("S")) {
                usuario.setEstado(estadoUsuarioRN.getEstado(1));

            } else {
                usuario.setEstado(estadoUsuarioRN.getEstado(9));

            }

            usuario = usuarioRN.guardar(usuario);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(UsuarioBean.class
                    .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            usuarioRN.eliminar(usuario);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = usuarioRN.getUsuarioByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (estado != null) {
            filtro.put("estado.id=", "'" + estado.getId() + "'");
        }

        if (tipo != null) {
            filtro.put("tipo.id=", "'" + tipo.getId() + "'");
        }

        if (grupo != null) {
            filtro.put("'" + grupo.getCodigo() + "'", "grupos.grupo.codigo");
        }

        if (sucursal != null) {
            filtro.put("'" + sucursal.getCodigo() + "'", "sucursales.sucursal.codigo");
            //filtro.put("sucursales.sucursal.codigo IN ", "('" + sucursal.getCodigo() + "')");
        }
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        estado = null;
        tipo = null;
        sucursal = null;
        grupo = null;

        buscar();

    }

    public List<Usuario> complete(String query) {
        try {
            lista = usuarioRN.getUsuarioByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Usuario>();
        }
    }

    public void onSelect(SelectEvent event) {
        usuario = (Usuario) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Usuario d) {

        if (d == null) {
            return;
        }

        usuario = d;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (usuario == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getLista() {
        return lista;
    }

    public void setLista(List<Usuario> lista) {
        this.lista = lista;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

}
