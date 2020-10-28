/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.dao;

import bs.global.dao.BaseDAO;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoTesoreriaDAO extends BaseDAO {

    public List<ItemMovimientoTesoreria> getItemMovientoTesoreriaByFiltro(Map<String, String> filtro, int cantMax) {
        try {

            String sQuery = "SELECT e FROM ItemMovimientoTesoreria e ";
            sQuery += generarStringFiltro(filtro, "e", true);
            sQuery += " ORDER BY e.movimiento.fechaMovimiento";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getItemMovientoTesoreriaByFiltro", e);
            return new ArrayList<ItemMovimientoTesoreria>();
        }
    }

    public List<ItemMovimientoTesoreria> getItemMovientoTesoreriaByFiltro(Map<String, String> filtro, Date fechaMovimiento, int cantMax) {
        try {

            String sQuery = "SELECT e FROM ItemMovimientoTesoreria e "
                    + "WHERE e.movimiento.fechaMovimiento = :fechaMovimiento";
            sQuery += generarStringFiltro(filtro, "e", false);
            sQuery += " ORDER BY e.movimiento.fechaMovimiento";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("fechaMovimiento", fechaMovimiento, TemporalType.DATE);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getItemMovientoTesoreriaByFiltro", e);
            return new ArrayList<ItemMovimientoTesoreria>();
        }
    }

    public BigDecimal getSaldoAFecha(String nrocta, Date fHasta) {

        try {
            String query = "SELECT SUM(e.importeDebe - e.importeHaber) FROM ItemMovimientoTesoreria e "
                    + "WHERE (e.cuentaTesoreria.codigo = :nrocta) "
                    + "AND e.movimiento.fechaMovimiento <= :fHasta ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fHasta", fHasta);

            BigDecimal saldo = (BigDecimal) q.getSingleResult();
            return (saldo != null ? saldo : BigDecimal.ZERO);

        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoMonedaSecundariaAFecha(String nrocta, Date fHasta) {

        try {
            String query = "SELECT SUM(e.importeDebeMonedaSecundaria - e.importeHaberMonedaSecundaria) FROM ItemMovimientoTesoreria e "
                    + "WHERE (e.cuentaTesoreria.codigo = :nrocta) "
                    + "AND e.movimiento.fechaMovimiento <= :fHasta ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fHasta", fHasta);

            BigDecimal saldo = (BigDecimal) q.getSingleResult();
            return (saldo != null ? saldo : BigDecimal.ZERO);

        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return BigDecimal.ZERO;
        }
    }

    public MovimientoTesoreria getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoTesoreria m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoTesoreria) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de tesorería");
            return null;
        }
    }

    public List<MovimientoTesoreria> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoTesoreria m ";
            sQuery += generarStringFiltro(filtro, "m", true);
            sQuery += (orderBy == null || orderBy.isEmpty() ? " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC" : generarStringOrden(orderBy, "m"));

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<MovimientoTesoreria>();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de proveedor");
            return new ArrayList<MovimientoTesoreria>();
        }
    }

}
