/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.Arancel;
import bs.global.dao.BaseDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class ArancelDAO extends BaseDAO {

    public Arancel getArancel(String id) {
        return getObjeto(Arancel.class, id);
    }

    public List<Arancel> getLista() {
        return getLista(Arancel.class, true, -1, -1);
    }

    public List<Arancel> getArancelByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Arancel e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            log.log(Level.SEVERE, "getArancelByBusqueda", e);
            return new ArrayList<>();
        }
    }

    public int getMaxCodigo(String codCarrera) {

        try {
            String sQuery = "select CAST(MAX(ed_arancel.codigo) AS SIGNED)+1  from ed_arancel where codigo < 900000 and CODCAR = '" + codCarrera + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }

    public int getCantidadRegistros() {

        try {
            String sQuery = "SELECT COUNT(e) FROM Arancel e WHERE e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCantidadRegistros", e);
            return 0;
        }

    }

    public String arancelUtilizado(Arancel arancel) {

        try {
            String sQuery = "select if(count(*)>0,'S','N') from ed_movimiento m where m.CODARA  = '" + arancel.getCodigo() + "' limit 1";

            Query q = getEm().createNativeQuery(sQuery);

            return (String) q.getSingleResult();

        } catch (Exception e) {
            log.log(Level.SEVERE, "arancelUtilizado", e);
            return "N";
        }

    }

    public void sincronizarImportesInscripciones(Arancel i) throws ExcepcionGeneralSistema {

        try {
//            String sQuery = " UPDATE ItemMovimientoEducacion "
//                    + " SET precio = :precio "
//                    + " WHERE itemArancel.id = :idArancel "
//                    + " AND movimiento.estado.codigo = 'A' "
//                    + " AND movimiento.comprobante.tipoComprobante = 'IN' ";
//
//            Query q = getEm().createQuery(sQuery);
//
//            q.setParameter("precio", i.getImporte());
//            q.setParameter("idArancel", i.getId());
//            q.executeUpdate();
//
//            String sQuery1 = " UPDATE ItemMovimientoEducacion "
//                    + " SET importe = (cantidad * precio )"
//                    + " WHERE itemArancel.id = :idArancel "
//                    + " AND movimiento.estado.codigo = 'A' "
//                    + " AND movimiento.comprobante.tipoComprobante = 'IN' ";
//
//            q = getEm().createQuery(sQuery1);
//            q.setParameter("idArancel", i.getId());
//            q.executeUpdate();
//
//            String sQuery2 = " UPDATE AplicacionCuentaCorrienteEducacion "
//                    + " SET importe = itemMovimiento.importe "
//                    + " WHERE itemArancel.id = :idArancel "
//                    + " AND movimiento.estado.codigo = 'A' "
//                    + " AND movimiento.comprobante.tipoComprobante = 'IN' ";
//
//            q = getEm().createQuery(sQuery2);
//            q.setParameter("idArancel", i.getId());
//            q.executeUpdate();

            String sQuery = " update ed_movimiento_item i \n"
                    + " inner join ed_movimiento m on m.ID  = i.ID_MCAB \n"
                    + " inner join ed_arancel_item ai on ai.ID  = i.ITMARA \n"
                    + " inner join ed_arancel a on a.CODIGO  = ai.CODARA \n"
                    + " inner join gr_comprobante c on m.MODCOM  = c.MODCOM  and m.CODCOM  = c.CODCOM  \n"
                    + " set i.PRECIO = ai.IMPORT,\n"
                    + " i.IMPORT  = i.CANTID * ai.IMPORT \n"
                    + " where m.CODEST in ('A')\n"
                    + " and c.TIPCOM  = 'IN'\n"
                    + " and a.CODIGO  = '" + i.getCodigo() + "'\n"
                    + " and i.PRECIO <> ai.IMPORT;";
            Query q = getEm().createNativeQuery(sQuery);
            q.executeUpdate();

            sQuery = " update ed_cuenta_corriente i \n"
                    + " inner join ed_movimiento m on m.ID  = i.ID_MOV \n"
                    + " inner join ed_arancel_item ai on ai.ID  = i.ITMARA \n"
                    + " inner join ed_arancel a on a.CODIGO  = ai.CODARA \n"
                    + " inner join gr_comprobante c on m.MODCOM  = c.MODCOM  and m.CODCOM  = c.CODCOM  \n"
                    + " set i.IMPORT = ai.IMPORT\n"
                    + " where m.CODEST in ('A')\n"
                    + " and c.TIPCOM  = 'IN'\n"
                    + " and a.CODIGO  = ''\n"
                    + " and i.IMPPEN > 0\n"
                    + " and i.IMPORT <> ai.IMPORT";

            q = getEm().createNativeQuery(sQuery);
            q.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcepcionGeneralSistema("No es posible sincronizar Importes Inscripciones");
        }

    }
}
