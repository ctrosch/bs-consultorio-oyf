/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.ItemHistoricoCuentaCorrienteEducacion;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.global.dao.BaseDAO;
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
public class CuentaCorrienteEducacionDAO extends BaseDAO {

    public List<ItemHistoricoCuentaCorrienteEducacion> getHistoricoMovimientos(String nroCuenta, String monReg, Date fDesde, Date fHasta) {

        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_MOV, c.NROCTA,c.FCHMOV,m.CODFOR,m.NROFOR,m.PTOVTA,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA,"
                    + " SUM(CASE WHEN c.IMPORT >= 0 THEN c.IMPORT ELSE 0 END) AS DEBE, "
                    + " SUM(CASE WHEN c.IMPORT < 0 THEN  c.IMPORT * (- 1) ELSE 0 END) AS HABER, "
                    + " SUM(CASE WHEN c.IMPSEC >= 0 THEN c.IMPSEC ELSE 0 END) AS DEBE_SEC, "
                    + " SUM(CASE WHEN c.IMPSEC < 0 THEN  c.IMPSEC * (- 1) ELSE 0 END) AS HABER_SEC "
                    + " FROM ed_cuenta_corriente c INNER JOIN ed_movimiento m ON c.ID_MOV = m.ID  "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM "
                    + //" WHERE c.ID_MOV = c.ID_APL " +
                    " WHERE m.CODCOM NOT LIKE 'AP%' "
                    + " AND (c.NROCTA = ?1) AND (c.FCHMOV BETWEEN ?2 AND ?3) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + " GROUP BY c.ID_MOV, c.NROCTA, c.FCHMOV "
                    + " ORDER BY c.FCHMOV, c.FECALT ";

            Query q = getEm().createNativeQuery(sQuery, ItemHistoricoCuentaCorrienteEducacion.class);

            q.setParameter(1, nroCuenta);
            q.setParameter(2, fDesde);
            q.setParameter(3, fHasta);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemHistoricoCuentaCorrienteEducacion>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getHistoricoMovimientos", e);
            return new ArrayList<ItemHistoricoCuentaCorrienteEducacion>();
        }
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getPendientesByNroCuenta(
            String nroCuenta,
            String monReg,
            String comprobanteInterno,
            String debeHaber,
            Integer idMovimiento) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_IAPL AS ID,c.OBSERV,c.CUOTAS,c.NROCTA, c.FCHVNC, m.CODFOR,m.NROFOR,m.PTOVTA,g.DESCRP AS DESCOM, "
                    + " c.MONREG, c.COTIZA, "
                    + " ABS(SUM(c.IMPORT)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC, "
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM ed_cuenta_corriente c INNER JOIN ed_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro "
                    + " WHERE (c.NROCTA = ?1) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + (idMovimiento != null && idMovimiento > 0 ? " AND c.ID_APL = " + idMovimiento : "")
                    + " GROUP BY c.ID_IAPL, c.OBSERV,c.CUOTAS, c.NROCTA, c.FCHVNC "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (Sum(c.IMPORT) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (Sum(c.IMPSEC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END "
                    + " ORDER BY c.FCHVNC, c.CUOTAS";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorrienteEducacion.class);
            q.setParameter(1, nroCuenta);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<>();
        }
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getPendientesTotal(String monReg, String comprobanteInterno, String debeHaber) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_APL AS ID, c.OBSERV, c.CUOTAS,c.NROCTA, c.FCHVNC, m.CODFOR,m.NROFOR,m.PTOVTA,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA, "
                    + " ABS(SUM(c.IMPORT)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC, "
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM ed_cuenta_corriente c INNER JOIN ed_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro "
                    + " WHERE c.MONREG = '" + monReg + "' "
                    + (comprobanteInterno != null && !comprobanteInterno.isEmpty() ? " AND g.COMINT = '" + comprobanteInterno + "' " : "")
                    + " GROUP BY c.ID_APL, c.OBSERV, c.CUOTAS, c.NROCTA, c.FCHVNC "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (Sum(c.IMPORT) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (Sum(c.IMPSEC) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END ";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorrienteEducacion.class);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemPendienteCuentaCorrienteEducacion>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<ItemPendienteCuentaCorrienteEducacion>();
        }
    }

    public List<EntidadComercial> getAlumnosConSaldos(Map<String, String> filtro, String monReg, String comprobanteInterno) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT e FROM EntidadComercial e "
                    + "WHERE EXISTS(SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteEducacion a "
                    + "             WHERE (a.alumno.nrocta = e.nrocta) "
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getAlumnosConSaldos", e);
            return new ArrayList<EntidadComercial>();
        }
    }

    public double getSaldoAFecha(String nrocta, String monReg, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteEducacion a "
                    + "WHERE (a.alumno.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            Double saldo = (Double) q.getSingleResult();
            return (saldo != null ? saldo : 0);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return 0;
        }
    }

    public double getSaldoByMovimiento(Integer idMovimiento) {

        try {
            String query = "SELECT SUM(a.importe) FROM AplicacionCuentaCorrienteEducacion a "
                    + "WHERE (a.movimientoAplicado.id = :idMovimiento) ";

            Query q = getEm().createQuery(query);
            q.setParameter("idMovimiento", idMovimiento);

            Double saldo = (Double) q.getSingleResult();
            return (saldo != null ? saldo : 0);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoByMovimiento ", e);
            return 0;
        }
    }

    public double getSaldoSecundarioAFecha(String nrocta, String monReg, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importeSecundario) FROM AplicacionCuentaCorrienteEducacion a "
                    + "WHERE (a.alumno.nrocta = :nrocta) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nrocta);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            Double saldo = (Double) q.getSingleResult();
            return (saldo != null ? saldo : 0);

        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return 0;
        }
    }

    public String conDeuda(String nroCuenta, String monReg) {

        try {
            String query = " SELECT CASE WHEN SUM(a.importe) > 0 THEN 'D' WHEN SUM(a.importe) < 0 THEN 'H' ELSE 'N' END "
                    + " FROM AplicacionCuentaCorrienteEducacion a "
                    + " WHERE (a.alumno.nrocta = :nrocta) "
                    + " AND a.monedaRegistracion.codigo = :monReg "
                    + " HAVING SUM(a.importe) <> 0 ";

            Query q = getEm().createQuery(query);
            q.setParameter("nrocta", nroCuenta);
            q.setParameter("monReg", monReg);

            return (String) q.getSingleResult();
        } catch (NoResultException nre) {
            return "N";
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "conDeuda", e);
            return "N";
        }

    }

}
