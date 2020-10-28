/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.dao;

import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.compra.vista.PendienteCompraDetalle;
import bs.compra.vista.PendienteCompraGrupo;
import bs.global.dao.BaseDAO;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import java.math.BigDecimal;
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
public class CompraDAO extends BaseDAO {

    public MovimientoCompra editar(MovimientoCompra m) {
        return (MovimientoCompra) super.editar(m);
    }

    public List<MovimientoCompra> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoCompra m ";
            if (soloPendientes) {
                sQuery += "WHERE EXISTS(SELECT i FROM ItemMovimientoCompra i WHERE i.movimiento.id = m.id and i.cantidadPendiente > 0) ";
            }
            sQuery += generarStringFiltro(filtro, "m", !soloPendientes);
            sQuery += " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de compras");
            return new ArrayList<MovimientoCompra>();
        }
    }

    public MovimientoCompra getMovimientoCompraById(Integer id) {
        try {
            return getEm().find(MovimientoCompra.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }

    public MovimientoCompra getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoCompra m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoCompra) q.getSingleResult();

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de compra");
            return null;
        }

    }

    public List<PendienteCompraGrupo> getPendienteGrupo(String consultaGrupo) {

        Query q = getEm().createNativeQuery(consultaGrupo, PendienteCompraGrupo.class);

        return q.getResultList();
    }

    public List<PendienteCompraDetalle> getPendienteDetalle(String consultaDetalle) {

        try {

            Query q = getEm().createNativeQuery(consultaDetalle, PendienteCompraDetalle.class);
//            q.setMaxResults(1000);

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<PendienteCompraDetalle>();
        } catch (Exception e) {
            System.err.println("Error getPendienteDetalle: " + e);
            return new ArrayList<PendienteCompraDetalle>();
        }
    }

    public List<ItemMovimientoCompra> getAplicacionesByItem(Integer idItem) {

        try {
            String sQuery = "SELECT i FROM ItemAplicacionCompra i "
                    + "WHERE i.idItemAplicacion = :idItem "
                    + "AND i.id <> i.idItemAplicacion "
                    + "AND i.auditoria.debaja = 'N' "
                    + "ORDER BY i.producto.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("idItem", idItem);

            return q.getResultList();

        } catch (Exception e) {
            System.err.println("Error al obtener ItemAplicacionCompra");
            return null;
        }
    }

    public ItemMovimientoCompra getItemProductoByItemAplicacion(Integer idMovimiento, Integer nroItem, String artcod) {

        try {
            String sQuery = "SELECT i FROM ItemMovimientoCompra i "
                    + "WHERE i.movimiento.id = :idMovimiento "
                    + "AND i.nroitm =:nroItem "
                    + "AND i.producto.codigo =:artcod "
                    + "AND i.id = i.idItemAplicacion "
                    + "AND i.auditoria.debaja = 'N' "
                    + "ORDER BY i.producto.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("idMovimiento", idMovimiento);
            q.setParameter("nroItem", nroItem);
            q.setParameter("artcod", artcod);

            q.setMaxResults(1);

            return (ItemMovimientoCompra) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener ItemAplicacionCompra de compra");
            return null;
        }

    }

    public BigDecimal getCantidadAplicadaItem(Integer id) {

        try {
//            String sQuery = "select ifnull(sum(i.cantid),0) from co_movimiento_item i where i.id_iapl = " + String.valueOf(id);

            String sQuery = " select ifnull((sum(case when i.id_iapl = " + String.valueOf(id) + " then i.cantid "
                    + "                               when i.ID_IREV = " + String.valueOf(id) + " then i.cantid*(-1) "
                    + "                               else 0 end)),0) "
                    + " from co_movimiento_item i where i.id_iapl = " + String.valueOf(id) + " or i.ID_IREV = " + String.valueOf(id);

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

    public MovimientoCompra getMovimientoByPuntoVentaNumeroOriginal(String nrocta, String puntoVentaOriginal, String numeroOriginal) {

        try {
            String sQuery = "SELECT m FROM MovimientoCompra m "
                    + " WHERE m.puntoVentaOriginal = :puntoVentaOriginal "
                    + " AND m.numeroOriginal = :numeroOriginal "
                    + " AND m.proveedor.nrocta = :nrocta ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nrocta", nrocta);
            q.setParameter("puntoVentaOriginal", puntoVentaOriginal);
            q.setParameter("numeroOriginal", numeroOriginal);

            q.setMaxResults(1);
            return (MovimientoCompra) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos de proveedor " + e);
            return null;
        }
    }

    public double getPendienteIngresoByProducto(Producto producto, Deposito deposito) {

        if (producto == null || deposito == null) {
            return 0;
        }

        try {
            String sQuery = " select IFNULL(SUM(i.CNTPEN),0)"
                    + "   from co_movimiento_item i "
                    + "   inner Join co_movimiento m On i.ID_MCAB = m.ID "
                    + "   inner join co_circuito c on c.CIRCOM = m.CIRCOM and c.CIRAPL = m.CIRAPL"
                    + "   Where c.PENING = 'S' "
                    + "   and m.DEPOSI = '" + deposito.getCodigo() + "' "
                    + "   and i.ARTCOD = '" + producto.getCodigo() + "' "
                    + "   and i.CNTPEN > 0 ";

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
