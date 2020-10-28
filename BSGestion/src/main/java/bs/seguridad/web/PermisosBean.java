/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.administracion.modelo.Modulo;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.ItemMenuUsuario;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.Usuario;
import bs.seguridad.rn.UsuarioRN;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class PermisosBean extends GenericBean implements Serializable {

    @EJB
    private UsuarioRN usuarioRN;

    private Usuario usuario;
    private Modulo modulo;
    List<ItemMenuUsuario> permisosAsignados;
    private List<Usuario> lista;
    private List<Menu> menuLista;
    private TreeNode menu;
    private TreeNode nodoSeleccionado;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    /**
     * Creates a new instance of UsuarioMB
     */
    public PermisosBean() {

    }

    @PostConstruct
    public void init() {
        lista = usuarioRN.getLista();
        menu = new DefaultTreeNode();

    }

    public void guardar() {

    }

    public void seleccionar(Usuario u) {
        usuario = u;
        //Actualizamos la lista de permisos asignados
        permisosAsignados = menuRN.getMenuByUsuario(usuario);
        //Generamos un nuevo arbol
        cargarMenu();
    }

    public void cargarMenu() {

        menu = new DefaultTreeNode();
        cargarFiltroBusqueda();
        generarArbol(menu, menuRN.getListaByNivel(1, filtro, false));
    }

    private void generarArbol(TreeNode raiz, List<Menu> items) {

        if (items.isEmpty()) {
            return;
        }

        if (usuario == null) {
            return;
        }

        //Recorremos menu principal
        for (Menu m : items) {

            ItemMenuUsuario um = new ItemMenuUsuario(usuario.getId(), m.getCodigo());
            TreeNode hoja = null;

            if (permisosAsignados.contains(um)) {
                hoja = new DefaultTreeNode("habilitado", m, raiz);
                hoja.setExpanded(true);
            } else {
                hoja = new DefaultTreeNode("bloqueado", m, raiz);
                hoja.setExpanded(true);
            }

            List<Menu> menuItems = menuRN.getItemsByMenu(m, filtro);
            generarArbol(hoja, menuItems);
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (modulo != null) {
            filtro.put("modulo.codigo =", "'" + modulo.getCodigo() + "'");
        }
    }

    public void habilitarMenu() {
        try {
            Menu m = (Menu) nodoSeleccionado.getData();

            if (!usuarioSessionBean.esAdministrador()
                    && (m.getModulo().getCodigo().equals("AS")
                    || m.getModulo().getCodigo().equals("SG")
                    || m.getModulo().getCodigo().equals("WS"))) {
                JsfUtil.addWarningMessage("", "Solo el administrador puede habilitar este permiso");
                return;
            }

            menuRN.HabilitarMenu(usuario, m);
            permisosAsignados = menuRN.getMenuByUsuario(usuario);
            cargarMenu();

            JsfUtil.addInfoMessage("El menú fue habilitado");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible la habilitación");
        }
    }

    public void bloquearMenu() {
        try {
            Menu m = (Menu) nodoSeleccionado.getData();

            if (!usuarioSessionBean.esAdministrador()
                    && (m.getModulo().getCodigo().equals("AS")
                    || m.getModulo().getCodigo().equals("SG")
                    || m.getModulo().getCodigo().equals("WS"))) {
                JsfUtil.addWarningMessage("", "Solo el administrador puede bloquear este permiso");
                return;
            }

            menuRN.BloquearMenu(usuario, m);
            permisosAsignados = menuRN.getMenuByUsuario(usuario);
            cargarMenu();

            JsfUtil.addInfoMessage("El menú fue bloqueado");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible el bloqueo");
        }
    }

    public void eliminar() {

    }

    public List<Usuario> getLista() {
        return lista;
    }

    public void setLista(List<Usuario> lista) {
        this.lista = lista;
    }

    public TreeNode getMenu() {
        return menu;
    }

    public void setMenu(TreeNode menu) {
        this.menu = menu;
    }

    public TreeNode getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(TreeNode nodoSeleccionado) {
        this.nodoSeleccionado = nodoSeleccionado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemMenuUsuario> getPermisosAsignados() {
        return permisosAsignados;
    }

    public void setPermisosAsignados(List<ItemMenuUsuario> permisosAsignados) {
        this.permisosAsignados = permisosAsignados;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public List<Menu> getMenuLista() {
        return menuLista;
    }

    public void setMenuLista(List<Menu> menuLista) {
        this.menuLista = menuLista;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

}
