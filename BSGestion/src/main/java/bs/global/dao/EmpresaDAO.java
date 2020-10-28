/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Empresa;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class EmpresaDAO extends BaseDAO {

    public Empresa getEmpresa(String id) {
        return getObjeto(Empresa.class, id);
    }

    public List<Empresa> getLista() {
        return getLista(Empresa.class, true, -1, -1);
    }

    public List<Empresa> getEmpresaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Empresa e "
                    + " WHERE (e.nombreFantasia LIKE :nombre "
                    + " OR e.razonSocial LIKE :razonSocial) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("razonSocial", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getEmpresaByBusqueda", e);
            return new ArrayList<Empresa>();
        }
    }

    public int getMaxCodigo() {

        try {
            String sQuery = "select CAST(MAX(gr_empresa.codigo) AS SIGNED)+1  from gr_empresa where codigo < 90000 ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }
}
