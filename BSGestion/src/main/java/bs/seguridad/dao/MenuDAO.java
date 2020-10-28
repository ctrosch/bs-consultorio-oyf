/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.dao;

import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.Grupo;
import bs.seguridad.modelo.ItemGrupoUsuario;
import bs.seguridad.modelo.ItemMenuGrupo;
import bs.seguridad.modelo.ItemMenuGrupoPK;
import bs.seguridad.modelo.ItemMenuUsuario;
import bs.seguridad.modelo.ItemMenuUsuarioPK;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class MenuDAO extends BaseDAO {

    public void borrar(Menu m) throws Exception {

        eliminar(getObjeto(Menu.class, m.getCodigo()));
    }

    public Menu getMenu(String id) {
        return getObjeto(Menu.class, id);
    }

    public List<Menu> getLista() {
        return getLista(Menu.class, true, -1, -1);
    }

    public List<Menu> getLista(int maxResults, int firstResult) {
        return getLista(Menu.class, false, maxResults, firstResult);
    }

    /**
     *
     * @param filtro
     * @param txtBusqueda
     * @param mostrarDebaja
     * @param cantMax
     * @return
     */
    public List<Menu> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Menu e "
                    + "WHERE "
                    + "      ((e.nombre LIKE :nombre) "
                    + "  OR  (e.url LIKE :url ) "
                    + "  OR  (e.codigo LIKE :codigo ) "
                    + "     ) "
                    + " AND e.activo = 'S' "
                    + " AND e.modulo.activo = 'S' "
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.modulo.codigo, e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("url", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<Menu>();
        }
    }

    public List<Menu> getListaByNivel(Integer nivel, Map<String, String> filtro, boolean activos) {

        String sQuery = "SELECT m FROM Menu m "
                + " WHERE m.nivel = " + nivel
                + " AND m.modulo.activo = 'S' "
                + generarStringFiltro(filtro, "m", false)
                + (activos ? " AND m.activo = 'S'" : "")
                + " ORDER BY m.orden";

        return queryList(Menu.class, sQuery);
    }

    public List<Menu> getListaBase() {

        String sQuery = "SELECT m FROM Menu m ORDER BY m.id ";
        Query q = (Query) getEm().createQuery(sQuery);
        return q.getResultList();
    }

    public Menu getMenuByNombre(String value) {
        return getObjeto(Menu.class, "nombre", value);
    }

    public List<ItemMenuUsuario> getMenuByUsuario(Usuario usuario) {

        try {
            String sQuery = "SELECT m FROM ItemMenuUsuario m "
                    + " WHERE m.idUsuario = :idUsuario "
                    + " AND m.menu.activo = :activo "
                    + " AND m.menu.modulo.activo = 'S' "
                    + " ORDER BY m.menu.orden ";

            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("idUsuario", usuario.getId());
            q.setParameter("activo", "S");

            return q.getResultList();
        } catch (Exception e) {
            System.out.println("No se puede obtener lista de menu por usuario " + e.getMessage());
            return null;
        }
    }

    public List<ItemMenuUsuario> getUsuarioByMenu(Menu menu) {

        try {
            String sQuery = "SELECT m FROM ItemMenuUsuario m "
                    + " WHERE m.menu.codigo = :menu "
                    + " ORDER BY m.menu.modulo.codigo, m.menu.orden ";

            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("menu", menu.getCodigo());

//            System.err.println("q.getResultList(); " + q.getResultList());
            return q.getResultList();
        } catch (Exception e) {
            System.out.println("No se puede obtener lista de menu por usuario " + e.getMessage());
            return null;
        }
    }

    public ItemMenuUsuario getItemMenuUsuario(ItemMenuUsuario us) {

        ItemMenuUsuarioPK idPK = new ItemMenuUsuarioPK(us.getIdUsuario(), us.getCodMenu());
        return getEm().find(ItemMenuUsuario.class, idPK);
    }

    public ItemMenuUsuario getItemMenuUsuario(int idUsuario, String codMenu) {

        ItemMenuUsuarioPK idPK = new ItemMenuUsuarioPK(idUsuario, codMenu);
        return getEm().find(ItemMenuUsuario.class, idPK);
    }

    public ItemMenuGrupo getItemMenuGrupo(ItemMenuGrupo img) {

        ItemMenuGrupoPK idPK = new ItemMenuGrupoPK(img.getCodGrupo(), img.getCodMenu());
        return getEm().find(ItemMenuGrupo.class, idPK);
    }

    public List<Menu> getItemsByMenu(Menu m, Map<String, String> filtro) {

        try {
            String sQuery = "SELECT m FROM Menu m "
                    + " WHERE m.menuPrincipal.codigo = :codPpal "
                    + generarStringFiltro(filtro, "m", false)
                    + " ORDER BY m.orden ";
            Query q = (Query) getEm().createQuery(sQuery);
            q.setParameter("codPpal", m.getCodigo());
            return q.getResultList();

        } catch (Exception e) {
            System.out.println("No se puede obtener lista de items por menú " + e.getMessage());
            return null;
        }
    }

    public List<Menu> getItemsByTexto(int idUsuario, String txtBusqueda) {
        try {

//            String sQuery = "SELECT DISTINCT m FROM Menu m "
//                    + " WHERE"
//                    + "     (m.nombre LIKE '%"+txtBusqueda.replaceAll(" ", "%")+"%') "
//                    + " AND (m.url <> '' OR m.url IS NOT NULL) "
//                    + " AND (m.nivel > 1 )"
//                    + " AND (EXISTS(SELECT i FROM ItemMenuUsuario i WHERE i.codMenu = m.codigo AND i.idUsuario = "+idUsuario+" )"
//                    + "      OR"
//                    + "      EXISTS(SELECT g FROM ItemMenuGrupo g INNER JOIN ItemGrupoUsuario u ON g.codGrupo = u.codGrupo  "
//                    + "             WHERE g.codMenu = m.codigo AND u.idUsuario = "+idUsuario+" ))"
//                    + " ORDER BY m.modulo.codigo, m.nombre ";
            String sQuery = " SELECT DISTINCT m.* FROM sg_menu m inner join gr_modulos mo on mo.CODIGO = m.MODULO "
                    + " WHERE (m.NOMBRE LIKE '%" + txtBusqueda.replaceAll(" ", "%") + "%')  "
                    + " AND (m.url <> '' OR m.url IS NOT NULL)  "
                    + " AND (m.nivel > 1 ) "
                    + " AND (m.ACTIVO = 'S' )"
                    + " AND (mo.ACTIVO = 'S') "
                    + " AND (EXISTS(SELECT i.* FROM sg_menu_usuario i WHERE i.COD_MENU = m.codigo AND i.ID_USUARIO = " + idUsuario + " ) "
                    + " OR   EXISTS(SELECT g.* FROM sg_menu_grupo g INNER JOIN sg_usuario_grupo u ON g.cod_grupo = u.cod_grupo "
                    + "            WHERE g.cod_menu = m.codigo AND u.id_usuario = " + idUsuario + " )"
                    + " OR (EXISTS(select u.* from sg_usuario u where u.ID = " + idUsuario + " and u.ID_TIPO = 1))) "
                    + " ORDER BY m.modulo, m.nombre ";

            Query q = (Query) getEm().createNativeQuery(sQuery, Menu.class);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("No se puede obtener lista de items " + e.getMessage());
            return null;
        }
    }

    public String obtenerSiguienteCodigo(String origen) {

        try {
            String sQuery = " SELECT CONCAT(ORIGEN,'_',RIGHT(CONCAT('00000',IFNULL(max(RIGHT(codigo,5)) + 1,1)),5)) from sg_menu where sg_menu.ORIGEN = '" + origen.toUpperCase() + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((String) q.getSingleResult());

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "obtenerSiguienteCodigo", e.getMessage());
            return origen.toUpperCase() + "_000000";
        }
    }

    public List<ItemMenuGrupo> getMenuByGrupo(Grupo grupo) {

        try {
            String sQuery = "SELECT m FROM ItemMenuGrupo m "
                    + " WHERE m.codGrupo = :codGrupo "
                    + " AND m.menu.activo = :activo "
                    + " AND m.menu.modulo.activo = 'S' "
                    + " ORDER BY m.menu.orden ";

            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("codGrupo", grupo.getCodigo());
            q.setParameter("activo", "S");

            return q.getResultList();
        } catch (Exception e) {
            System.out.println("No se puede obtener lista de menu por grupo " + e.getMessage());
            return null;
        }
    }

    public List<ItemMenuGrupo> getGrupoByMenu(Menu menu) {

        try {
            String sQuery = "SELECT m FROM ItemMenuGrupo m "
                    + " WHERE m.menu.codigo = :codigo "
                    + " ORDER BY m.menu.nombre, m.menu.orden ";

            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("codigo", menu.getCodigo());

            return q.getResultList();
        } catch (Exception e) {
            System.out.println("No se puede obtener lista de menu por grupo " + e.getMessage());
            return null;
        }
    }

    public List<ItemMenuGrupo> getMenuByGruposByUsuario(List<ItemGrupoUsuario> grupos) {

        try {

            if (grupos == null) {
                return null;
            }

            String filtroGrupos = "";

            for (ItemGrupoUsuario itemGrupo : grupos) {
                if (filtroGrupos.isEmpty()) {
                    filtroGrupos += "'" + itemGrupo.getGrupo().getCodigo() + "'";
                } else {
                    filtroGrupos += ",'" + itemGrupo.getGrupo().getCodigo() + "'";
                }
            }

            if (filtroGrupos.isEmpty()) {
                filtroGrupos += "'ZZZZ'";
            }

            String sQuery = "SELECT DISTINCT m FROM ItemMenuGrupo m "
                    + " WHERE m.codGrupo IN (" + filtroGrupos + ") "
                    + " AND m.menu.activo = :activo "
                    + " AND m.menu.modulo.activo = 'S' "
                    + " ORDER BY m.menu.orden ";

            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("activo", "S");

            return q.getResultList();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.out.println("No se puede obtener lista de menu por grupo " + e.getMessage());
            return null;
        }
    }

}
