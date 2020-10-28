/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.dao;

import bs.entidad.modelo.EntidadComercial;
import bs.global.dao.BaseDAO;
import bs.ventas.modelo.ItemHistoricoCuentaCorriente;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
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

/**
 *
 * @author Claudio
 */
@Stateless

public class CuentaCorrienteDAO extends BaseDAO {

    public List<ItemHistoricoCuentaCorriente> getHistoricoMovimientos(String nroCuenta, String monReg, String comprobanteInterno, Date fDesde, Date fHasta) {

        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_MOV,c.NROCTA,c.FCHMOV,m.CODFOR,m.NROFOR,m.SUCURS,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA,"
                    + " SUM(CASE WHEN c.IMPNAC >= 0 THEN c.IMPNAC ELSE 0 END) AS DEBE, "
                    + " SUM(CASE WHEN c.IMPNAC < 0 THEN  c.IMPNAC * (- 1) ELSE 0 END) AS HABER, "
                    + " SUM(CASE WHEN c.IMPSEC >= 0 THEN c.IMPSEC ELSE 0 END) AS DEBE_SEC, "
                    + " SUM(CASE WHEN c.IMPSEC < 0 THEN  c.IMPSEC * (- 1) ELSE 0 END) AS HABER_SEC "
                    + " FROM vt_cuenta_corriente c INNER JOIN vt_movimiento m ON c.ID_MOV = m.ID  "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM "
                    + //" WHERE c.ID_MOV = c.ID_APL " +
                    " WHERE m.CODCOM NOT LIKE 'AP%' "
                    + " AND (c.NROCTA = ?1) AND (c.FCHMOV BETWEEN ?2 AND ?3) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_MOV, c.NROCTA, c.FCHMOV "
                    + " ORDER BY c.FCHMOV ";

            Query q = getEm().createNativeQuery(sQuery, ItemHistoricoCuentaCorriente.class);

            q.setParameter(1, nroCuenta);
            q.setParameter(2, fDesde);
            q.setParameter(3, fHasta);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemHistoricoCuentaCorriente>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getHistoricoMovimientos", e);
            return new ArrayList<ItemHistoricoCuentaCorriente>();
        }
    }

    public List<ItemPendienteCuentaCorriente> getPendientesByNroCuenta(String nroCuenta, String monReg, String comprobanteInterno, String debeHaber) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_APL AS ID,c.CUOTAS,c.NROCTA, c.FCHVNC, m.CODFOR,m.NROFOR,m.SUCURS,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA, "
                    + " ABS(SUM(c.IMPNAC)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC, "
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM vt_cuenta_corriente c INNER JOIN vt_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro "
                    + " WHERE (c.NROCTA = ?1) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_APL, c.CUOTAS, c.NROCTA, c.FCHVNC "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (Sum(c.IMPNAC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (Sum(c.IMPSEC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END "
                    + " ORDER BY  c.FCHVNC, m.CODFOR, c.CUOTAS ";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorriente.class);
            q.setParameter(1, nroCuenta);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemPendienteCuentaCorriente>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<ItemPendienteCuentaCorriente>();
        }
    }

    public List<ItemPendienteCuentaCorriente> getPendientesTotal(String monReg, String comprobanteInterno, String debeHaber) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_APL AS ID,c.CUOTAS,c.NROCTA, c.FCHVNC, m.CODFOR,m.NROFOR,m.SUCURS,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA, "
                    + " ABS(SUM(c.IMPNAC)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC, "
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM vt_cuenta_corriente c INNER JOIN vt_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro "
                    + " WHERE c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_APL, c.CUOTAS, c.NROCTA, c.FCHVNC "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (Sum(c.IMPNAC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (Sum(c.IMPSEC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END ";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorriente.class);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemPendienteCuentaCorriente>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<ItemPendienteCuentaCorriente>();
        }
    }

    public List<EntidadComercial> getClientesConSaldos(Map<String, String> filtro, String monReg, String comprobanteInterno) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT e FROM EntidadComercial e "
                    + "WHERE EXISTS(SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteVenta a "
                    + "             WHERE (a.cliente.nrocta = e.nrocta) "
                    + "             AND a.monedaRegistracion.codigo = :monReg"
                    + "             AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno ";
            sQuery += generarStringFiltro(filtro, "a", false);
            sQuery += "             HAVING SUM(a.importe) <> 0 ) ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("monReg", monReg);
            q.setParameter("comprobanteInterno", comprobanteInterno);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<EntidadComercial>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getClientesConSaldos", e);
            return new ArrayList<EntidadComercial>();
        }
    }

    public BigDecimal getSaldoAFecha(String nrocta, String monReg, String comprobanteInterno, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteVenta a "
                    + "WHERE (a.cliente.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno " : "")
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            if (comprobanteInterno != null && !comprobanteInterno.isEmpty()) {
                q.setParameter("comprobanteInterno", comprobanteInterno);
            }

            BigDecimal saldo = (BigDecimal) q.getSingleResult();
            return (saldo != null ? saldo : BigDecimal.ZERO);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoSecundarioAFecha(String nrocta, String monReg, String comprobanteInterno, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importeSecundario) FROM AplicacionCuentaCorrienteVenta a "
                    + "WHERE (a.cliente.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno " : "")
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            if (comprobanteInterno != null && !comprobanteInterno.isEmpty()) {
                q.setParameter("comprobanteInterno", comprobanteInterno);
            }

            BigDecimal saldo = (BigDecimal) q.getSingleResult();
            return (saldo != null ? saldo : BigDecimal.ZERO);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return BigDecimal.ZERO;
        }
    }

    public String conDeuda(String nroCuenta, String monReg, String comprobanteInterno) {

        try {
            String query = " SELECT CASE WHEN SUM(a.importe) > 0 THEN 'D' WHEN SUM(a.importe) < 0 THEN 'H' ELSE 'N' END "
                    + " FROM AplicacionCuentaCorrienteVenta a "
                    + " WHERE (a.cliente.nrocta = :nrocta) "
                    + " AND a.monedaRegistracion.codigo = :monReg "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno " : "")
                    + " HAVING SUM(a.importe) <> 0 ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nroCuenta);
            q.setParameter("monReg", monReg);

            if (comprobanteInterno != null && !comprobanteInterno.isEmpty()) {
                q.setParameter("comprobanteInterno", comprobanteInterno);
            }

            return (String) q.getSingleResult();
        } catch (NoResultException nre) {
            return "N";
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "conDeuda", e);
            return "N";
        }

    }
}
