package bs.global.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.administracion.modelo.Parametro;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Empresa;
import bs.global.util.JsfUtil;
import bs.seguridad.modelo.ItemMenuFavorito;
import bs.seguridad.modelo.ItemMenuGrupo;
import bs.seguridad.modelo.ItemMenuReciente;
import bs.seguridad.modelo.ItemMenuUsuario;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.Usuario;
import bs.seguridad.rn.ItemMenuFavoritoRN;
import bs.seguridad.rn.ItemMenuRecienteRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author ctrosch
 */
@Named
@SessionScoped
public class PrincipalBean extends GenericBean implements Serializable {

    private Empresa empresa;

    private String paginaActiva = "/seguridad/login";
    private String contextPath = "";
    private MenuModel modelo;
    private MenuModel modeloHistorial;
    private MenuModel modeloFavoritos;

    private List<Menu> resultadoBusqueda;
    private List<ItemMenuReciente> recientes;
    private List<ItemMenuFavorito> favoritos;
    private Usuario usuario;
    private Parametro parametro;

    @EJB
    private ItemMenuRecienteRN itemMenuRecienteRN;
    @EJB
    private ItemMenuFavoritoRN itemMenuFavoritoRN;

    @PostConstruct
    public void init() {
        recientes = new ArrayList<ItemMenuReciente>();
        parametro = parametrosRN.getParametro();
    }

    public PrincipalBean() {

        modelo = new DefaultMenuModel();
        modeloFavoritos = new DefaultMenuModel();
        modeloHistorial = new DefaultMenuModel();
        contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    }

    public void buscarMenuItem() {

        if (txtBusqueda == null) {
            return;
        }

        if (usuario == null) {
            return;
        }

        if (!txtBusqueda.isEmpty()) {

            resultadoBusqueda = menuRN.getItemsByTexto(usuario.getId(), txtBusqueda);

            if (resultadoBusqueda == null || resultadoBusqueda.isEmpty()) {
                JsfUtil.addInfoMessage("No se encontraron resultados para \"" + txtBusqueda + "\"");
            }
        } else if (resultadoBusqueda != null) {

            resultadoBusqueda.clear();
        }
    }

    public void agregarReciente(Menu itemMenu) {

        if (recientes == null || itemMenu == null || usuario == null) {
            return;
        }

        try {

            ItemMenuReciente ir = new ItemMenuReciente();

            ir.setCodMenu(itemMenu.getCodigo());
            ir.setIdUsuario(usuario.getId());
            ir.setUsuario(usuario);
            ir.setMenu(itemMenu);

            if (recientes == null) {
                recientes = new ArrayList<ItemMenuReciente>();
            }

            if (recientes.contains(ir)) {

                if (recientes.remove(ir)) {
                    recientes.add(0, ir);
                    reordenarRecientes();
                }
                return;
            }

            if (recientes.size() >= 15) {
                recientes.remove(recientes.size() - 1);
            }
            recientes.add(0, ir);
            reordenarRecientes();
            ir = itemMenuRecienteRN.guardar(ir);

//            EventBusFactory ebf = EventBusFactory.getDefault();
//
//            if (ebf != null) {
//                EventBus eventBus = ebf.eventBus();
//                eventBus.publish("/reciente/"+usuario.getUsuario()+"/", usuario.getUsuario());
//            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para agregar recientes");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reordenarRecientes() {

        int orden = 0;

        for (ItemMenuReciente ir : recientes) {
            try {
                ir.setOrden(orden);
                ir = itemMenuRecienteRN.guardar(ir);
                orden++;
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void agregarFavorito(Menu itemMenu) {

        if (itemMenu == null) {
            return;
        }

        try {
            ItemMenuFavorito i = new ItemMenuFavorito();

            i.setCodMenu(itemMenu.getCodigo());
            i.setIdUsuario(usuario.getId());
            i.setUsuario(usuario);
            i.setMenu(itemMenu);

            if (favoritos == null) {
                favoritos = new ArrayList<ItemMenuFavorito>();
            }

            if (favoritos.contains(i)) {

                if (favoritos.remove(i)) {
                    favoritos.add(0, i);
                }
                return;
            }

            if (favoritos.size() >= 20) {
                favoritos.remove(favoritos.size() - 1);
            }
            favoritos.add(0, i);
            i = itemMenuFavoritoRN.guardar(i);
            JsfUtil.addInfoMessage("El acceso se ha guardado en favoritos");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para agregar a favoritos");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarFavoritoById(String idMenu) {

        Menu m = menuRN.getMenu(idMenu);
        agregarFavorito(m);
    }

    public void agregarFavorito() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();

//        if (params == null) {
//            System.err.println("parametros nulo");
//        }
        //System.err.println(params.get("COD_MENU"));
        if (params.get("COD_MENU") != null) {
            agregarFavoritoById((String) params.get("COD_MENU"));
        }
    }

    public void quitarFavorito(ItemMenuFavorito itemMenu) {

        try {

            if (favoritos == null || favoritos.isEmpty()) {
                return;
            }

            if (favoritos.contains(itemMenu)) {

                if (favoritos.remove(itemMenu)) {
                    itemMenuFavoritoRN.eliminar(itemMenu);
                }
                return;
            }

            JsfUtil.addInfoMessage("El acceso ha sido quitado de sus favoritos");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para quitar de favoritos " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarRecienteById(String idMenu) {

        Menu m = menuRN.getMenu(idMenu);
        agregarReciente(m);
    }

    public void agregarHistorial() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
//        System.err.println(params.get("COD_MENU"));

        if (params.get("COD_MENU") != null) {
            agregarFavoritoById((String) params.get("COD_MENU"));
        }
    }

    public String navegar(ActionEvent ae) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(true);

        if (session.isNew()) {
            paginaActiva = "../seguridad/login.xhtml";
        }

        MenuItem itm = (MenuItem) ae.getSource();
        Menu menu = (Menu) itm.getValue();
//        System.out.println("valor de item = " + itm.getValue());
        paginaActiva = menu.getAccion();

//        System.out.println("Pagina activa: " + paginaActiva);
        //return ".."+paginaActiva+".xhtml";
        paginaActiva = "../seguridad/login.xhtml";
        return paginaActiva;
    }

    public void cargarMenu(Usuario usuario) throws ExcepcionGeneralSistema {

        if (usuario == null) {
            JsfUtil.addWarningMessage("El usuario no existe");
            return;
        }

        this.usuario = usuario;
        modelo = new DefaultMenuModel();

        List<ItemMenuUsuario> permisosAsignadoUsuario = menuRN.getMenuByUsuario(usuario);
        agregarPermisosDeGrupo(permisosAsignadoUsuario);

        if (usuario.getTipo().getId() != 1 && permisosAsignadoUsuario == null) {
            throw new ExcepcionGeneralSistema("El usuario no tiene permisos asignados");
        }

        if (usuario.getTipo().getId() != 1 && permisosAsignadoUsuario.isEmpty()) {
            throw new ExcepcionGeneralSistema("El usuario no tiene permisos asignados");
        }

        List<Menu> menuPrincipal = menuRN.getListaByNivel(1, null, true);

        if (menuPrincipal == null) {
            throw new ExcepcionGeneralSistema("No es posible obtener el menu del sistema");
        }

        generarMenuModel(usuario, menuPrincipal, permisosAsignadoUsuario);

        recientes = itemMenuRecienteRN.getItemMenuRecienteByUsuario(usuario);
        favoritos = itemMenuFavoritoRN.getItemMenuFavoritoByUsuario(usuario);
    }

    private void generarMenuModel(Usuario usuario, List<Menu> menuPrincipal, List<ItemMenuUsuario> permisosAsignadoUsuario) {

        for (Menu mp : menuPrincipal) {

            if (mp.getMenuItem() == null || mp.getMenuItem().isEmpty()) {
                continue;
            }

            if (mp.getActivo().equals("N") || mp.getModulo().getActivo().equals("N")) {
                continue;
            }

            DefaultSubMenu subMenu = new DefaultSubMenu(mp.getNombre());
            subMenu.setId("Item" + mp.getCodigo());
            subMenu.setIcon("fa " + mp.getIcono());

            List<Menu> itemsMenu = menuRN.getItemsByMenu(mp, null);

            generarArbol(usuario, subMenu, itemsMenu, permisosAsignadoUsuario);

            if (subMenu.getElementsCount() > 0) {
                modelo.addElement(subMenu);
            }
        }
    }

    private void generarArbol(Usuario usuario, DefaultSubMenu menuRaiz, List<Menu> items, List<ItemMenuUsuario> permisosAsignadoUsuario) {

        if (items == null || items.isEmpty()) {
            return;
        }

        //Recorremos menu principal
        for (Menu m : items) {

            ItemMenuUsuario um = new ItemMenuUsuario(usuario.getId(), m.getCodigo());

            if (m.getActivo().equals("N") || m.getModulo().getActivo().equals("N")) {
                continue;
            }

            if (usuario.getTipo().getId() != 1 && !permisosAsignadoUsuario.contains(um)) {
                continue;
            }

            if (m.getMenuItem() == null || m.getMenuItem().isEmpty()) {

//                System.out.println("\tMenú item: " +m.getNombre());
                DefaultMenuItem item = new DefaultMenuItem(m.getNombre());
                item.setId("Item" + m.getCodigo());
                item.setUrl(contextPath + m.getUrlCompleta());

                if (parametro != null && parametro.getTipoNavegacion().equals("M")) {
                    item.setTarget("_blank");
                }

                item.setAjax(false);
                item.setIcon("fa " + m.getIcono());

                if (m.getOrigen() != null && m.getOrigen().equals("USR")) {
                    item.setStyleClass("menuOrigenUsuario");
                }

                //item.setOncomplete("mostrarAlerta('" + m.getCodigo() + "')");
//              item.setCommand("#{principalBean.probar}");
//              item.setUpdate(":formulario");
                menuRaiz.addElement(item);
            } else {

//                System.out.println("\tMenú: " +m.getNombre());
                DefaultSubMenu subMenu = new DefaultSubMenu(m.getNombre());
                subMenu.setId("Item" + m.getCodigo());
                subMenu.setIcon("fa " + m.getIcono());

                List<Menu> itemsMenu = menuRN.getItemsByMenu(m, null);

                generarArbol(usuario, subMenu, itemsMenu, permisosAsignadoUsuario);

                if (subMenu.getElementsCount() > 0) {
                    menuRaiz.addElement(subMenu);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    public List<Menu> getResultadoBusqueda() {
        return resultadoBusqueda;
    }

    public void setResultadoBusqueda(List<Menu> resultadoBusqueda) {
        this.resultadoBusqueda = resultadoBusqueda;
    }

    public MenuModel getModelo() {
        return modelo;
    }

    public void setModelo(MenuModel modelo) {
        this.modelo = modelo;
    }

    public String getPaginaActiva() {
        return paginaActiva;
    }

    public void setPaginaActiva(String paginaActiva) {
        this.paginaActiva = paginaActiva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemMenuReciente> getRecientes() {
        return recientes;
    }

    public void setRecientes(List<ItemMenuReciente> recientes) {
        this.recientes = recientes;
    }

    public List<ItemMenuFavorito> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<ItemMenuFavorito> favoritos) {
        this.favoritos = favoritos;
    }

    private void agregarPermisosDeGrupo(List<ItemMenuUsuario> permisosAsignadoUsuario) {

        List<ItemMenuGrupo> permisosAsignadoGrupo = menuRN.getMenuByGruposByUsuario(usuario.getGrupos());

        if (permisosAsignadoGrupo == null || permisosAsignadoUsuario == null) {
            return;
        }

        for (ItemMenuGrupo itemGrupoUsuario : permisosAsignadoGrupo) {

            ItemMenuUsuario itemMenuUsuario = new ItemMenuUsuario(usuario.getId(), itemGrupoUsuario.getCodMenu());

            if (!permisosAsignadoUsuario.contains(itemMenuUsuario)) {
                permisosAsignadoUsuario.add(itemMenuUsuario);
            }
        }
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Parametro getParametro() {
        return parametro;
    }

}
