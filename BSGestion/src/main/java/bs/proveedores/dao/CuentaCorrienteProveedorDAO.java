/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.ItemHistoricoCuentaCorrienteProveedor;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class CuentaCorrienteProveedorDAO extends BaseDAO {

    public List<ItemHistoricoCuentaCorrienteProveedor> getHistoricoMovimientos(String nroCuenta, String monReg, String comprobanteInterno, Date fDesde, Date fHasta) {

        try {
            String sQuery = " SELECT c.ID_MOV,c.NROCTA,c.FCHMOV,m.CODFOR,m.NROFOR,m.SUCURS,g.DESCRP AS DESCOM, m.SUCORI, m.NROORI, c.MONREG, c.COTIZA,"
                    + " SUM(CASE WHEN c.IMPNAC >= 0 THEN c.IMPNAC ELSE 0 END) AS DEBE, "
                    + " SUM(CASE WHEN c.IMPNAC < 0 THEN  c.IMPNAC * (- 1) ELSE 0 END) AS HABER, "
                    + " SUM(CASE WHEN c.IMPSEC >= 0 THEN c.IMPSEC ELSE 0 END) AS DEBE_SEC, "
                    + " SUM(CASE WHEN c.IMPSEC < 0 THEN  c.IMPSEC * (- 1) ELSE 0 END) AS HABER_SEC "
                    + " FROM pv_cuenta_corriente c INNER JOIN pv_movimiento m ON c.ID_MOV = m.ID  "
                    + "    INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM "
                    + " WHERE m.CODCOM NOT LIKE 'AP%' "
                    + " AND (c.NROCTA = ?1) AND (c.FCHMOV BETWEEN ?2 AND ?3) "
                    + " AND c.MONREG = '" + (monReg == null || monReg.isEmpty() ? "" : monReg) + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_MOV, c.NROCTA, c.FCHMOV,m.SUCORI, m.NROORI "
                    + " ORDER BY c.FCHMOV ";

            Query q = getEm().createNativeQuery(sQuery, ItemHistoricoCuentaCorrienteProveedor.class);

            q.setParameter(1, nroCuenta);
            q.setParameter(2, fDesde);
            q.setParameter(3, fHasta);
            return q.getResultList();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getHistoricoMovimientos", e);
            return new ArrayList<ItemHistoricoCuentaCorrienteProveedor>();
        }
    }

    public List<ItemPendienteCuentaCorrienteProveedor> getPendientesByNroCuenta(String nroCuenta, String monReg, String comprobanteInterno, String debeHaber) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_APL AS ID,c.CUOTAS,c.NROCTA, c.FCHVNC, m.CODFOR,m.NROFOR, c.MONREG, c.COTIZA, "
                    + " m.SUCURS,g.DESCRP AS DESCOM, m.SUCORI, m.NROORI, "
                    + " ABS(SUM(c.IMPNAC)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC,"
                    + " (SELECT SUM(i.IMPNAC) FROM pv_movimiento_item i WHERE i.ID_MCAB = c.ID_APL AND i.TIPCPT = 'A')PENDIENTE_NET,"
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM pv_cuenta_corriente c INNER JOIN pv_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro"
                    + " WHERE (c.NROCTA = ?1) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_APL, c.CUOTAS, c.NROCTA, c.FCHVNC, m.SUCORI, m.NROORI "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (Sum(c.IMPNAC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (Sum(c.IMPSEC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END "
                    + " ORDER BY c.FCHVNC, m.CODFOR,c.CUOTAS ";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorrienteProveedor.class);
            q.setParameter(1, nroCuenta);

            return q.getResultList();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<ItemPendienteCuentaCorrienteProveedor>();
        }
    }

    public BigDecimal getSaldoAFecha(String nrocta, String monReg, String comprobanteInterno, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteProveedor a "
                    + "WHERE (a.proveedor.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + " AND a.monedaRegistracion.codigo = :monReg "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno " : "");

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
            String query = "SELECT SUM(a.importeSecundario) FROM AplicacionCuentaCorrienteProveedor a "
                    + "WHERE (a.proveedor.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + " AND a.monedaRegistracion.codigo = :monReg "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND a.movimiento.comprobante.comprobanteInterno = :comprobanteInterno " : "");

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
                    + " FROM AplicacionCuentaCorrienteProveedor a "
                    + " WHERE (a.proveedor.nrocta = :nrocta) "
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
