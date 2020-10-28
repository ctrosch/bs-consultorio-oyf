/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.seguridad.dao.UsuarioDAO;
import bs.seguridad.modelo.ItemGrupoUsuario;
import bs.seguridad.modelo.ItemSucursalUsuario;
import bs.seguridad.modelo.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class UsuarioRN implements Serializable {

    @EJB
    UsuarioDAO usuarioDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Usuario guardar(Usuario u) throws Exception {

        control(u);
        reorganizarNroItem(u);

        if (u.getId() == null) {
            if (getUsuarioByNombre(u.getUsuario()) != null) {
                throw new ExcepcionGeneralSistema("El usuario " + u.getNombre() + " ya existe");
            }
            usuarioDAO.crear(u);
        } else {
            u = usuarioDAO.editar(u);
        }

        return u;
    }

    private void control(Usuario usuario) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            sErrores += "- El nombre no puede estar vacío \n";
        }

        if (usuario.getUsuario() == null || usuario.getUsuario().isEmpty()) {

            sErrores += "- El nombre de usuario no puede estar vacío \n";
        }

        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {

            sErrores += "- El password no puede estar vacío \n";
        }

        if (usuario.getSucursales() == null || usuario.getSucursales().size() == 0) {
            sErrores += "- Debe asignar al menos 1 sucursal al usuario \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public Usuario getUsuario(int id) {

        return usuarioDAO.getUsuario(id);
    }

    public void refreshUsuario(Usuario u) {

        usuarioDAO.refreshUsuario(u);
    }

    public List<Usuario> getLista() {

        return usuarioDAO.getLista();
    }

    public void eliminar(Usuario usuario) throws Exception {

        usuarioDAO.eliminar(usuario);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eliminarGrupo(Usuario usuario, ItemGrupoUsuario itemGrupo) throws Exception {

        usuarioDAO.eliminar(ItemGrupoUsuario.class, itemGrupo.getId());
        usuario.getGrupos().remove(itemGrupo);
    }

    public List<Usuario> getUsuarioByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return usuarioDAO.getUsuarioByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public List<Usuario> getUsuarioByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return usuarioDAO.getUsuarioByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Usuario getUsuarioByNombre(String usuario) {

        return usuarioDAO.getUsuarioByNombre(usuario);

    }

    public Usuario getUsuarioByEmail(String email) {

        return usuarioDAO.getUsuarioByEmail(email);

    }

    public void reorganizarNroItem(Usuario usuario) {

        //Reorganizamos los números de items
        int i;

        if (usuario.getGrupos() != null) {

            i = 1;
            for (ItemGrupoUsuario item : usuario.getGrupos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (usuario.getSucursales() != null) {

            i = 1;
            for (ItemSucursalUsuario item : usuario.getSucursales()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    public void nuevoItemGrupo(Usuario usuario) throws ExcepcionGeneralSistema {

        if (usuario == null) {
            throw new ExcepcionGeneralSistema("No existe un usuario seleccionado para agregarle un grupo");
        }

        if (usuario.getGrupos() == null) {
            usuario.setGrupos(new ArrayList<ItemGrupoUsuario>());
        }

        ItemGrupoUsuario itemGrupo = new ItemGrupoUsuario();
        itemGrupo.setNroitm(usuario.getGrupos().size() + 1);
        itemGrupo.setUsuario(usuario);

        usuario.getGrupos().add(itemGrupo);

    }

    public void eliminarItemGrupo(Usuario usuario, ItemGrupoUsuario itemGrupo) throws ExcepcionGeneralSistema {

        if (usuario == null) {
            throw new ExcepcionGeneralSistema("No existe un usuario seleccionado para agregarle un grupo");
        }

        if (itemGrupo == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemGrupoUsuario item : usuario.getGrupos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemGrupo.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemGrupoUsuario remove = usuario.getGrupos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    usuarioDAO.eliminar(ItemGrupoUsuario.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(usuario);

    }

    public void nuevoItemSucursal(Usuario usuario) throws ExcepcionGeneralSistema {

        if (usuario == null) {
            throw new ExcepcionGeneralSistema("No existe un usuario seleccionado para agregarle una sucursal");
        }

        if (usuario.getSucursales() == null) {
            usuario.setSucursales(new ArrayList<ItemSucursalUsuario>());
        }

        ItemSucursalUsuario item = new ItemSucursalUsuario();
        item.setNroitm(usuario.getSucursales().size() + 1);
        item.setUsuario(usuario);

        usuario.getSucursales().add(item);
    }

    public void eliminarItemSucursal(Usuario usuario, ItemSucursalUsuario itemSucursal) throws ExcepcionGeneralSistema {

        if (usuario == null) {
            throw new ExcepcionGeneralSistema("No existe un usuario seleccionado para agregarle una sucursal");
        }

        if (itemSucursal == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemSucursalUsuario item : usuario.getSucursales()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemSucursal.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemSucursalUsuario remove = usuario.getSucursales().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    usuarioDAO.eliminar(ItemSucursalUsuario.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(usuario);

    }

}
