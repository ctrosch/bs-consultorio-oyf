/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Comprobante;
import bs.global.modelo.ComprobantePK;
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
public class ComprobanteDAO extends BaseDAO {

    public Comprobante getComprobante(ComprobantePK idPK) {

        return getEm().find(Comprobante.class, idPK);
    }

    public Comprobante editar(Comprobante c) {

        return (Comprobante) super.editar(c);

    }

    public Comprobante getComprobante(String modulo, String codigo) {
        ComprobantePK idPk = new ComprobantePK(modulo, codigo);
        return getComprobante(idPk);
    }

    public void eliminar(Comprobante o) throws Exception {

        ComprobantePK idPK = new ComprobantePK(o.getModulo(), o.getCodigo());

        super.eliminar(Comprobante.class, idPK);
    }

    public List<Comprobante> getListaByBusqueda(String modulo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Comprobante e "
                    + " WHERE ((e.codigo LIKE :codigo) OR (e.descripcion LIKE :descripcion)) "
                    + " AND (e.modulo = :modulo )"
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.esComprobanteReversion, e.codigo ";

            Query q = getEm().createQuery(sQuery);

//            System.err.println("modulo " + modulo);
//            System.err.println("filtro " + filtro);
//            System.err.println("txtBusqueda " + txtBusqueda);
//            System.err.println("sQuery " + sQuery);
            q.setParameter("modulo", modulo);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<Comprobante>();
        }
    }

}
