/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.dao;

import bs.contrato.modelo.Contrato;
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
public class ContratoDAO extends BaseDAO {

    public Contrato getContrato(Integer id) {
        return getObjeto(Contrato.class, id);
    }

    public List<Contrato> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Contrato e "
                    + " WHERE (e.descripcion LIKE :descripcion "
                    + " OR e.nroContrato LIKE :nroContrato) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.id";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nroContrato", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Contrato>();
        }
    }

    public int getMaxNroContrato() {

        try {
            String sQuery = "select IFNULL(CAST(MAX(cv_contrato.NROCON) AS SIGNED)+1,1)  from cv_contrato ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }

}
