/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.administracion.modelo.Modulo;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.Grupo;
import bs.seguridad.modelo.ItemMenuGrupo;
import bs.seguridad.modelo.Menu;
import bs.seguridad.rn.GrupoRN;
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
public class PermisosGrupoBean extends GenericBean implements Serializable {

    @EJB
    private GrupoRN grupoRN;

    private Grupo grupo;
    private Modulo modulo;
    List<ItemMenuGrupo> permisosAsignados;
    private List<Grupo> lista;
    private TreeNode menu;
    private TreeNode nodoSeleccionado;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    /**
     * Creates a new instance of GrupoMB
     */
    public PermisosGrupoBean() {

    }

    @PostConstruct
    public void init() {

        lista = grupoRN.getListaByBusqueda("", false, 0);
        menu = new DefaultTreeNode();
    }

    public void guardar() {

    }

    public void seleccionar(Grupo u) {

        grupo = u;
        //Actualizamos la lista de permisos asignados
        permisosAsignados = menuRN.getMenuByGrupo(grupo);
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

        if (grupo == null) {
            return;
        }

        //Recorremos menu principal
        for (Menu m : items) {

            ItemMenuGrupo um = new ItemMenuGrupo(grupo.getCodigo(), m.getCodigo());
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

            menuRN.HabilitarMenu(grupo, m);
            permisosAsignados = menuRN.getMenuByGrupo(grupo);
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

            menuRN.BloquearMenu(grupo, m);
            permisosAsignados = menuRN.getMenuByGrupo(grupo);
            cargarMenu();

            JsfUtil.addInfoMessage("El menú fue bloqueado");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible el bloqueo");
        }
    }

    public void eliminar() {

    }

    public List<Grupo> getLista() {
        return lista;
    }

    public void setLista(List<Grupo> lista) {
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

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<ItemMenuGrupo> getPermisosAsignados() {
        return permisosAsignados;
    }

    public void setPermisosAsignados(List<ItemMenuGrupo> permisosAsignados) {
        this.permisosAsignados = permisosAsignados;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

}
