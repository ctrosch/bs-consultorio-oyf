/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.dao;

import bs.global.dao.BaseDAO;
import bs.global.util.Numeros;
import bs.prestamo.modelo.ItemHistoricoCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
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
public class CuentaCorrientePrestamoDAO extends BaseDAO {

    public List<ItemHistoricoCuentaCorrientePrestamo> getHistoricoMovimientos(String nroCuenta, String monReg, Date fDesde, Date fHasta) {

        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String sQuery = " SELECT c.ID_MOV,c.NROCTA,c.FCHMOV,m.CODFOR,m.NROFOR,m.SUCURS,g.DESCRP AS DESCOM, c.MONREG, c.COTIZA,"
                    + " SUM(CASE WHEN c.IMPNAC >= 0 THEN c.IMPNAC ELSE 0 END) AS DEBE, "
                    + " SUM(CASE WHEN c.IMPNAC < 0 THEN  c.IMPNAC * (- 1) ELSE 0 END) AS HABER, "
                    + " SUM(CASE WHEN c.IMPSEC >= 0 THEN c.IMPSEC ELSE 0 END) AS DEBE_SEC, "
                    + " SUM(CASE WHEN c.IMPSEC < 0 THEN  c.IMPSEC * (- 1) ELSE 0 END) AS HABER_SEC "
                    + " FROM pr_cuenta_corriente c INNER JOIN pr_movimiento m ON c.ID_MOV = m.ID  "
                    + "    INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM "
                    + //" WHERE c.ID_MOV = c.ID_APL " +
                    " WHERE m.CODCOM NOT LIKE 'AP%' "
                    + " AND (c.NROCTA = ?1) AND (c.FCHMOV BETWEEN ?2 AND ?3) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + " GROUP BY c.ID_MOV, c.NROCTA, c.FCHMOV "
                    + " ORDER BY c.FCHMOV ";

            Query q = getEm().createNativeQuery(sQuery, ItemHistoricoCuentaCorrientePrestamo.class);

            q.setParameter(1, nroCuenta);
            q.setParameter(2, fDesde);
            q.setParameter(3, fHasta);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemHistoricoCuentaCorrientePrestamo>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getHistoricoMovimientos", e);
            return new ArrayList<ItemHistoricoCuentaCorrientePrestamo>();
        }
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getPendientesByNroCuenta(Integer idPrestamo, String monReg, String debeHaber) {
        try {

            if (monReg == null || monReg.isEmpty()) {
                monReg = "";
            }

            String nQuery = "select * from pr_cuenta_corriente limit 1 ";
            List<Object[]> resultado = new ArrayList<Object[]>();
            resultado = getEm().createNativeQuery(nQuery).getResultList();

            String sQuery = " SELECT c.ID_APL AS ID,"
                    + " c.ID_PRES,"
                    + " c.CUOTAS,"
                    + " c.NROCTA, "
                    + " c.ID_IPR, "
                    + " c.FCHVNC, "
                    + " m.CODFOR,m.NROFOR,m.SUCURS,"
                    + " g.DESCRP AS DESCOM, "
                    + " c.MONREG, "
                    + " c.COTIZA, "
                    + " ABS(SUM(c.IMPNAC)) AS PENDIENTE, "
                    + " ABS(SUM(c.IMPSEC)) AS PENDIENTE_SEC, "
                    + " ABS(SUM(c.IMPCAP)) AS IMPCAP, "
                    + " ABS(SUM(c.IMPINT)) AS IMPINT, "
                    + " ABS(SUM(c.IMPGOT)) AS IMPGOT, "
                    + " ABS(SUM(c.IMPMCS)) AS IMPMCS, "
                    + " ABS(SUM(c.INTMOR)) AS INTMOR, "
                    + " 1 AS CALMOR, "
                    + " ABS(SUM(c.DESINT)) AS DESINT,"
                    + " 1 AS CALDES, "
                    + " ad_parametro.MONPRI, "
                    + " ad_parametro.MONSEC "
                    + " FROM pr_cuenta_corriente c INNER JOIN pr_movimiento m ON c.ID_APL = m.ID "
                    + " INNER JOIN pr_prestamo_item i on i.ID = c.ID_IPR "
                    + " INNER JOIN gr_comprobante g ON m.MODCOM = g.MODCOM and m.CODCOM = g.CODCOM, ad_parametro "
                    + " WHERE (c.ID_PRES = ?1) "
                    + " AND c.MONREG = '" + monReg + "' "
                    + " GROUP BY c.ID_APL, c.CUOTAS, c.NROCTA, c.ID_IPR, c.FCHVNC "
                    + " HAVING case c.MONREG "
                    + " WHEN ad_parametro.MONPRI THEN (ROUND(Sum(c.IMPNAC),2) " + (debeHaber.equals("D") ? ">" : "<") + " 0) "
                    + " WHEN ad_parametro.MONSEC THEN (ROUND(Sum(c.IMPSEC),2) " + (debeHaber.equals("D") ? ">" : "<") + " 0) END ";

            Query q = getEm().createNativeQuery(sQuery, ItemPendienteCuentaCorrientePrestamo.class);
            q.setParameter(1, idPrestamo);

//            String sQuery = "Select e from ItemPendienteCuentaCorrientePrestamo e "
//                    + " where e.codigoMonedaRegistracion = :monReg "
//                    + " and e.idPrestamo = :idPrestamo ";
//
//            Query q = getEm().createQuery(sQuery);
//            q.setParameter("idPrestamo", idPrestamo);
//            q.setParameter("monReg", monReg);
            return q.getResultList();

        } catch (NoResultException nre) {
            return new ArrayList<ItemPendienteCuentaCorrientePrestamo>();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getPendientesByNroCuenta", e);
            return new ArrayList<ItemPendienteCuentaCorrientePrestamo>();
        }
    }

    public double getSaldoAFecha(Integer idPrestamo, String monReg, Date fDesde) {

        try {
            String query = "SELECT SUM(a.importe) FROM AplicacionCuentaCorrientePrestamo a "
                    + "WHERE (a.prestamo.id = :idPrestamo) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("idPrestamo", idPrestamo);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            Double saldo = (Double) q.getSingleResult();
            return Numeros.redondear((saldo != null ? saldo : 0));
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return 0;
        }
    }

    public double getSaldoSecundarioAFecha(Integer idPrestamo, String monReg, Date fDesde) {

        try {

            String query = "SELECT SUM(a.importeSecundario) FROM AplicacionCuentaCorrientePrestamo a "
                    + "WHERE (a.prestamo.id = :idPrestamo) "
                    + "AND a.fechaAplicacion <= :fDesde "
                    + "AND a.monedaRegistracion.codigo = :monReg ";

            Query q = getEm().createQuery(query);
            q.setParameter("idPrestamo", idPrestamo);
            q.setParameter("fDesde", fDesde);
            q.setParameter("monReg", monReg);

            Double saldo = (Double) q.getSingleResult();
            return Numeros.redondear((saldo != null ? saldo : 0));

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getSaldoAFecha", e);
            return 0;
        }
    }

    public String conDeuda(String nroCuenta, String monReg) {

        try {
            String query = " SELECT CASE WHEN SUM(a.importe) > 0 THEN 'D' WHEN SUM(a.importe) < 0 THEN 'H' ELSE 'N' END "
                    + " FROM AplicacionCuentaCorrientePrestamo a "
                    + " WHERE (a.cliente.nrocta = :nrocta) "
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
