/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.MenuDAO;
import bs.seguridad.modelo.Grupo;
import bs.seguridad.modelo.ItemGrupoUsuario;
import bs.seguridad.modelo.ItemMenuGrupo;
import bs.seguridad.modelo.ItemMenuGrupoPK;
import bs.seguridad.modelo.ItemMenuUsuario;
import bs.seguridad.modelo.ItemMenuUsuarioPK;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.MenuParametro;
import bs.seguridad.modelo.Usuario;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MenuRN implements Serializable {

    @EJB
    private MenuDAO menuDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Menu guardar(Menu m) throws Exception {

        if (getMenu(m.getCodigo()) == null) {
            menuDAO.crear(m);
        } else {
            m = (Menu) menuDAO.editar(m);
        }

        return m;
    }

    public Menu getMenu(String id) {
        return menuDAO.getMenu(id);
    }

//    public void actualizarMenuFromBase() throws CloneNotSupportedException {
//
//        List<Menu> listaBase = menuDAO.getListaBase();
//
//        Map<String, String> filtro = menuDAO.getFiltro();
//        filtro.put("origen = ", "'SIS'");
//
//        List<Menu> listaOrganizacion = menuDAO.getListaByBusqueda(filtro, "", true, 0);
//
//        Menu menu = null;
//        MenuParametro parametro = null;
//
//        if (listaBase != null && listaOrganizacion !=null ){
//
//            for (Menu mBase : listaBase) {
//
//                menu = menuDAO.getMenu(mBase.getCodigo());
//
//                if(menu==null){
//
//                    menu = mBase.clone();
//
//                    try {
//                        guardar(menu);
//                    } catch (Exception ex) {
//                        Logger.getLogger(MenuRN.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }else{
//
//                    menu.setNombre(mBase.getNombre());
//                    menu.setOrden(mBase.getOrden());
//                    menu.setTipo(mBase.getTipo());
//                    menu.setIcono(mBase.getIcono());
//                    menu.setUrl(mBase.getUrl());
//                    menu.setNivel(mBase.getNivel());
//                    menu.setAyuda(mBase.getAyuda());
//                    menu.setModulo(mBase.getModulo());
//                    menu.setVista(mBase.getVista());
//                    menu.setMenuPrincipal(mBase.getMenuPrincipal());
//                    menu.setReporte(mBase.getReporte());
//
//                    menuDAO.editar(menu);
//
//                    if(mBase.getParametros()!=null){
//                        for (MenuParametro mpBase: mBase.getParametros()){
//
//                            parametro = menuParametroDAO.getMenuParametro(mpBase.getId());
//
//                            if(parametro==null){
//
//                                parametro = mpBase.clone();
//                                menuParametroDAO.crear(parametro);
//                            }else{
//                                parametro.setNombre(mpBase.getNombre());
//                                parametro.setValor(mpBase.getValor());
//                                menuParametroDAO.editar(parametro);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("lista")
    public List<Menu> getLista() {

        return menuDAO.getLista();
    }

    public List<Menu> getLista(int maxResults, int firstResult) {

        return menuDAO.getLista(maxResults, firstResult);
    }

    public List<Menu> getListaByNivel(Integer nivel, Map<String, String> filtro, boolean soloActivos) {

        return menuDAO.getListaByNivel(nivel, filtro, soloActivos);
    }

    public List<Menu> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return menuDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public Menu getMenuByNombre(String value) {

        return menuDAO.getMenuByNombre(value);
    }

    public void eliminar(Menu m) throws Exception {
        menuDAO.borrar(m);
    }

    public ItemMenuUsuario getItemMenuUsuario(int idUsuario, String codMenu) {

        return menuDAO.getItemMenuUsuario(idUsuario, codMenu);
    }

    public List<ItemMenuUsuario> getMenuByUsuario(Usuario usuario) {

        return menuDAO.getMenuByUsuario(usuario);
    }

    public List<ItemMenuUsuario> getUsuarioByMenu(Menu menu) {

        return menuDAO.getUsuarioByMenu(menu);
    }

    public List<ItemMenuGrupo> getMenuByGruposByUsuario(List<ItemGrupoUsuario> grupos) {

        return menuDAO.getMenuByGruposByUsuario(grupos);
    }

    public List<ItemMenuGrupo> getMenuByGrupo(Grupo grupo) {

        return menuDAO.getMenuByGrupo(grupo);
    }

    public List<ItemMenuGrupo> getGrupoByMenu(Menu menu) {

        return menuDAO.getGrupoByMenu(menu);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void HabilitarMenu(Usuario usuario, Menu m) throws Exception {

        ItemMenuUsuario us = new ItemMenuUsuario(usuario.getId(), m.getCodigo());
        us.setUsuario(usuario);
        us.setMenu(m);

        //Si el menú no se encuentra activado, entonces los habilitamos
        if (menuDAO.getItemMenuUsuario(us) == null) {
            menuDAO.crear(us);
        }

        //Habilitamos recursivamente los items hijos del menú a habilitar
        if (!m.getMenuItem().isEmpty()) {

            for (Menu mi : m.getMenuItem()) {
                HabilitarMenu(usuario, mi);
            }
        }

        //Habilitamos hacia atras, los menues principales
        habilitarMenuPrincipal(usuario, m);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void HabilitarMenu(Grupo grupo, Menu m) throws Exception {

        ItemMenuGrupo itemMenuGrupo = new ItemMenuGrupo(grupo.getCodigo(), m.getCodigo());
        itemMenuGrupo.setGrupo(grupo);
        itemMenuGrupo.setMenu(m);

        //Si el menú no se encuentra activado, entonces los habilitamos
        if (menuDAO.getItemMenuGrupo(itemMenuGrupo) == null) {
            menuDAO.crear(itemMenuGrupo);
        }

        //Habilitamos recursivamente los items hijos del menú a habilitar
        if (!m.getMenuItem().isEmpty()) {

            for (Menu mi : m.getMenuItem()) {
                HabilitarMenu(grupo, mi);
            }
        }

        //Habilitamos hacia atras, los menues principales
        habilitarMenuPrincipal(grupo, m);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void habilitarMenuPrincipal(Usuario usuario, Menu m) throws Exception {

        if (m.getMenuPrincipal() != null) {

            ItemMenuUsuario us = new ItemMenuUsuario(usuario.getId(), m.getMenuPrincipal().getCodigo());

            //Si el menú no se encuentra activado, entonces los habilitamos
            if (menuDAO.getItemMenuUsuario(us) == null) {
                menuDAO.crear(us);
            }
            habilitarMenuPrincipal(usuario, m.getMenuPrincipal());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void habilitarMenuPrincipal(Grupo grupo, Menu m) throws Exception {

        if (m.getMenuPrincipal() != null) {

            ItemMenuGrupo itemMenuGrupo = new ItemMenuGrupo(grupo.getCodigo(), m.getMenuPrincipal().getCodigo());

            //Si el menú no se encuentra activado, entonces los habilitamos
            if (menuDAO.getItemMenuGrupo(itemMenuGrupo) == null) {
                menuDAO.crear(itemMenuGrupo);
            }
            habilitarMenuPrincipal(grupo, m.getMenuPrincipal());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void BloquearMenu(Usuario usuario, Menu m) throws Exception {

        ItemMenuUsuario us = new ItemMenuUsuario(usuario.getId(), m.getCodigo());

        //Si el menú no se encuentra activado, entonces los habilitamos
        if (menuDAO.getItemMenuUsuario(us) != null) {
            ItemMenuUsuarioPK idPK = new ItemMenuUsuarioPK(us.getIdUsuario(), us.getCodMenu());
            menuDAO.eliminar(ItemMenuUsuario.class, idPK);
        }

        //Habilitamos recursivamente los items hijos del menú a habilitar
        if (!m.getMenuItem().isEmpty()) {

            for (Menu mi : m.getMenuItem()) {
                BloquearMenu(usuario, mi);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void BloquearMenu(Grupo usuario, Menu m) throws Exception {

        ItemMenuGrupo us = new ItemMenuGrupo(usuario.getCodigo(), m.getCodigo());

        //Si el menú no se encuentra activado, entonces los habilitamos
        if (menuDAO.getItemMenuGrupo(us) != null) {
            ItemMenuGrupoPK idPK = new ItemMenuGrupoPK(us.getCodGrupo(), us.getCodMenu());
            menuDAO.eliminar(ItemMenuGrupo.class, idPK);
        }

        //Habilitamos recursivamente los items hijos del menú a habilitar
        if (!m.getMenuItem().isEmpty()) {

            for (Menu mi : m.getMenuItem()) {
                BloquearMenu(usuario, mi);
            }
        }
    }

    public List<Menu> getItemsByMenu(Menu m, Map<String, String> filtro) {

        return menuDAO.getItemsByMenu(m, filtro);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<Menu> getItemsByTexto(int idUsuario, String txtBusqueda) {

        return menuDAO.getItemsByTexto(idUsuario, txtBusqueda);
    }

    public String obtenerSiguienteCogido(String origen) {

        String codigo = menuDAO.obtenerSiguienteCodigo(origen);

        if (codigo == null || codigo.isEmpty()) {
            codigo = origen.toUpperCase() + "_00001";
        }

        return codigo;

    }

    public MenuParametro nuevoItemParametro(Menu menu) {

        MenuParametro nItem = new MenuParametro();
        nItem.setMenu(menu);
        return nItem;

    }

    public void eliminarMenuParametro(Menu menu, MenuParametro parametro) throws ExcepcionGeneralSistema {

        if (menu == null) {
            throw new ExcepcionGeneralSistema("Menú vacío, nada para eliminar");
        }

        if (parametro == null) {
            throw new ExcepcionGeneralSistema("Parametro vacío, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        for (MenuParametro par : menu.getParametros()) {

            if (par.getNombre().equals(parametro.getNombre())) {

                indiceItemProducto = i;
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            MenuParametro remove = menu.getParametros().remove(indiceItemProducto);
            if (remove != null) {
                if (parametro.getId() != null && remove.getId() != null) {
                    menuDAO.eliminar(MenuParametro.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

}
