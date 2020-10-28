/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.dao;

import bs.administracion.modelo.PerfilCuenta;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class PerfilCuentaDAO extends BaseDAO {

    public PerfilCuenta getPerfilCuenta(int id) {
        return getObjeto(PerfilCuenta.class, id);
    }

    public List<PerfilCuenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM PerfilCuenta e "
                    + "WHERE "
                    + "      ((e.nombre LIKE :nombre) "
                    + "  OR  (e.usuario LIKE :usuario ) "
                    + "     ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("usuario", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<PerfilCuenta>();
        }

    }

    public int getMaxId() {

        try {
            String sQuery = "select MAX(ad_perfil_cuenta.id)+1  from ad_perfil_cuenta where id < 90000 ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxId", e.getMessage());
            return 0;
        }
    }

}
