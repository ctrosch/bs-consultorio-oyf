/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.CondicionPagoProveedor;
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
public class CondicionPagoProveedorDAO extends BaseDAO {

    public List<CondicionPagoProveedor> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM CondicionPagoProveedor e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "
                    + "     ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<CondicionPagoProveedor>();
        }

    }

    public List<CondicionPagoProveedor> getListaByBusquedaByImputacion(String imputaCuentaCorriente, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM CondicionPagoProveedor e "
                    + "WHERE e.imputaCuentaCorriente = :imputaCuentaCorriente "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("imputaCuentaCorriente", imputaCuentaCorriente);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {

            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<CondicionPagoProveedor>();
        }

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
