/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author ctrosch
 */
@Stateless
public class FormularioDAO extends BaseDAO {

    public Formulario getFormulario(FormularioPK idPK) {

        return getEm().find(Formulario.class, idPK);
    }

    public List<Formulario> getFormularioByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM Formulario e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductoByBusqueda", e);
            return new ArrayList<Formulario>();
        }
    }

    public int sincronizarUltimoNumeroFormulario(Formulario formulario) {

        if (formulario == null) {
            return -1;
        }

        String tabla = formulario.getModfor() + "_movimiento";

        if (formulario.getModfor().equals("TA")) {
            tabla = "ta_tarea";
        }

        if (formulario.getModfor().equals("BR")) {
            tabla = "br_reserva";
        }

        String sQuery = "select ifnull(max(m.nrofor),0) from " + tabla + " m "
                + "where m.MODFOR = '" + formulario.getModfor() + "' "
                + "and m.CODFOR = '" + formulario.getCodigo() + "'";

        Query q = getEm().createNativeQuery(sQuery);

        return ((Long) q.getSingleResult()).intValue();

    }

    public boolean existeMovimiento(Formulario formulario, Integer numeroFormulario) {

        try {

            if (formulario == null) {
                return false;
            }

            String tabla = formulario.getModfor() + "_movimiento";

            if (formulario.getModfor().equals("TA")) {
                tabla = "ta_tarea";
            }

            if (formulario.getModfor().equals("BR")) {
                tabla = "br_reserva";
            }

            String sQuery = " SELECT COUNT(*) FROM " + tabla + " "
                    + "WHERE CODFOR = '" + formulario.getCodigo() + "' AND NROFOR = " + numeroFormulario + " ";

            Query q = getEm().createNativeQuery(sQuery);
            return ((Long) q.getSingleResult()).intValue() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos en existeMovimiento");
            return false;
        }

    }

}
