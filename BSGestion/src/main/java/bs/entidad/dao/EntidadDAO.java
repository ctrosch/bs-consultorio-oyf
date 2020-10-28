/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.dao;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.TipoEntidad;
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
public class EntidadDAO extends BaseDAO {

    public EntidadComercial getEntidad(String nrocta) {
        getEm().flush();
        return getObjeto(EntidadComercial.class, nrocta);
    }

    public List<EntidadComercial> getEntidadByBusqueda(Map<String, String> filtro, String txtBusqueda, TipoEntidad tipo, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM EntidadComercial e "
                    + "WHERE "
                    + "      ((e.razonSocial LIKE :razonSocial) "
                    + "  OR  (e.nombreFantasia LIKE :nombreFantasia ) "
                    + "  OR  (e.nombre LIKE :nombre ) "
                    + "  OR  (e.apellido LIKE :apellido ) "
                    + "  OR  (e.nrocta LIKE :nrocta ) "
                    + "  OR  (e.email LIKE :email )"
                    + "  OR  (e.nrodoc LIKE :nrodoc )"
                    + "  OR  (e.direccion LIKE :direccion )"
                    + "     ) "
                    + (tipo == null ? " " : " AND e.tipo.codigo = :tipo ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.nrocta ";

            Query q = getEm().createQuery(sQuery);

            if (tipo != null) {
                q.setParameter("tipo", tipo.getCodigo());
            }

            q.setParameter("razonSocial", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nombreFantasia", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("apellido", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nrocta", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("email", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nrodoc", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("direccion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getEntidadByBusqueda", e.getMessage());
            return new ArrayList<EntidadComercial>();
        }
    }

    public List<String> getNotasByBusqueda(String txtBusqueda, int cantMax) {

        try {

            String sQuery = "SELECT DISTINCT e.notas FROM EntidadComercial e "
                    + "WHERE (e.notas LIKE :notas) ORDER BY e.notas ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("notas", "%" + (txtBusqueda == null ? "" : txtBusqueda) + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getReferencia", e.getMessage());
            return new ArrayList<String>();
        }
    }

    public int getMaxNroCuenta(String codTipo) {

        try {
            String sQuery = "select IFNULL(CAST(MAX(en_entidad.NROCTA) AS SIGNED)+1,1)  from en_entidad where NROCTA < 1000000 and CODTIP = '" + codTipo + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxNroCuenta - Error", e);
            return 0;
        }
    }

    public int getCantidadByTipo(String tipo) {

        try {
            String sQuery = "SELECT COUNT(e) FROM EntidadComercial e "
                    + "WHERE e.auditoria.debaja = 'N' "
                    + (tipo == null ? " " : " AND e.tipo.codigo = :tipo ");

            Query q = getEm().createQuery(sQuery);

            if (tipo != null && !tipo.isEmpty()) {
                q.setParameter("tipo", tipo);
            }

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getEntidadByBusqueda", e.getMessage());
            return 0;
        }

    }
}
