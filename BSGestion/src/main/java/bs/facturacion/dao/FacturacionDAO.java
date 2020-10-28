/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.dao;

import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.vista.PendienteFacturacionDetalle;
import bs.facturacion.vista.PendienteFacturacionGrupo;
import bs.global.dao.BaseDAO;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class FacturacionDAO extends BaseDAO {

    public MovimientoFacturacion editar(MovimientoFacturacion m) {
        return (MovimientoFacturacion) super.editar(m);
    }

    public List<MovimientoFacturacion> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoFacturacion m ";
            if (soloPendientes) {
                sQuery += "WHERE EXISTS(SELECT i FROM ItemMovimientoFacturacion i WHERE i.movimiento.id = m.id and i.cantidadPendiente > 0) ";
            }
            sQuery += generarStringFiltro(filtro, "m", !soloPendientes);
            sQuery += " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de facturacion");
            return new ArrayList<MovimientoFacturacion>();
        }
    }

    public MovimientoFacturacion getMovimientoFacturacionById(Integer id) {
        try {
            return getEm().find(MovimientoFacturacion.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }

    public List<Object[]> getPendienteGrupoNew(String consultaGrupo) throws SQLException {

        System.err.println("executeSQL(consultaGrupo) " + executeSQL(consultaGrupo));

        return executeSQL(consultaGrupo);
    }

    public List<Object[]> getPendienteDetalleNew(String consultaDetalle) {

        try {
            return executeSQL(consultaDetalle);

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error getPendienteDetalle: " + e);
            return null;
        }
    }

    public List<PendienteFacturacionGrupo> getPendienteGrupo(String consultaGrupo) {

        Query q = getEm().createNativeQuery(consultaGrupo, PendienteFacturacionGrupo.class);

        return q.getResultList();
    }

    public List<PendienteFacturacionDetalle> getPendienteDetalle(String consultaDetalle) {

        try {

            Query q = getEm().createNativeQuery(consultaDetalle, PendienteFacturacionDetalle.class);
//            q.setMaxResults(1000);

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<PendienteFacturacionDetalle>();
        } catch (Exception e) {
            System.err.println("Error getPendienteDetalle: " + e);
            return new ArrayList<PendienteFacturacionDetalle>();
        }
    }

    public MovimientoFacturacion getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoFacturacion m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoFacturacion) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de facturacion");
            return null;
        }

    }

    public MovimientoFacturacion getMovimiento(Integer id) {

        return getObjeto(MovimientoFacturacion.class, id);
    }

    public BigDecimal getCantidadAplicadaItem(Integer id) {

        try {
            //String sQuery = " select ifnull(sum(i.cantid),0) from fc_movimiento_item i where i.id_iapl = " ;

            String sQuery = " select ifnull((sum(case when i.id_iapl = " + String.valueOf(id) + " then i.cantid "
                    + "                               when i.ID_IREV = " + String.valueOf(id) + " then i.cantid*(-1) "
                    + "                               else 0 end)),0) "
                    + " from fc_movimiento_item i where i.id_iapl = " + String.valueOf(id) + " or i.ID_IREV = " + String.valueOf(id);

            Query q = getEm().createNativeQuery(sQuery);

            BigDecimal cantidad = (BigDecimal) q.getSingleResult();

            if (cantidad == null) {
                cantidad = BigDecimal.ZERO;
            }

            return cantidad;

        } catch (NoResultException e) {

            return BigDecimal.ZERO;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener getCantidadAplicadaItem");
            return BigDecimal.ZERO;
        }
    }

    public double getComprometidoByProducto(Producto producto, Deposito deposito) {

        if (producto == null) {
            return 0;
        }

        try {
            String sQuery = "Select "
                    + " ifnull(sum(i.cntpen),0) + (Select ifnull(sum(k.CANTID),0) from fc_movimiento m  "
                    + "     inner join fc_movimiento_item i on m.ID = i.ID_MCAB  "
                    + "     inner join fc_movimiento_item_kit k on i.ID = k.ID_IDET  "
                    + "     inner join fc_circuito c on c.CIRCOM = m.CIRCOM and c.CIRAPL = m.CIRAPL  "
                    + "     inner join st_producto p on p.CODIGO = k.ARTCOD "
                    + "     where c.COMPST = 'S' and i.CNTPEN > 0  and p.STOCKS = 'S' "
                    + "     and k.ARTCOD = '" + producto.getCodigo() + "' and m.DEPOSI = '" + deposito.getCodigo() + "')  "
                    + " from fc_movimiento m  "
                    + " inner join fc_movimiento_item i on m.ID = i.ID_MCAB "
                    + " inner join fc_circuito c on c.CIRCOM = m.CIRCOM and c.CIRAPL = m.CIRAPL  "
                    + " inner join st_producto p on p.CODIGO = i.ARTCOD "
                    + " where c.COMPST = 'S' and i.CNTPEN > 0  and p.STOCKS = 'S' "
                    + " and i.ARTCOD = '" + producto.getCodigo() + "' and m.DEPOSI = '" + deposito.getCodigo() + "' ;";

            Query q = getEm().createNativeQuery(sQuery);
            q.setMaxResults(1);
            return ((BigDecimal) q.getSingleResult()).doubleValue();

        } catch (NoResultException e) {

            return 0;

        } catch (Exception e) {
            System.err.println("Error getComprometidoByProducto " + e);
            return 0;
        }
    }

}
