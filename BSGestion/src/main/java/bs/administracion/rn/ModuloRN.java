/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.ModuloDAO;
import bs.administracion.modelo.Modulo;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.UtilFechas;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ModuloRN implements Serializable {

    @EJB
    private ModuloDAO moduloDAO;

    public Modulo getModulo(String value) {
        return moduloDAO.getObjeto(Modulo.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Modulo guardar(Modulo m, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (moduloDAO.getObjeto(Modulo.class, m.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + m.getCodigo());
            }
            moduloDAO.crear(m);
        } else {
            m = (Modulo) moduloDAO.editar(m);
        }
        return m;
    }

    public String canSaveModulo(String codigo, Date fechaMovimiento) {

        Modulo modulo = getModulo(codigo);

        if (modulo == null) {
            return "No se encontró módulo de registración";
        }

        if (modulo.getActivo() == null) {
            try {
                modulo.setActivo("N");
                guardar(modulo, false);
            } catch (Exception ex) {

            }
            return "El modulo " + modulo.getDescripcion() + " no está activo";
        }

        if (modulo.getActivo().equals("N")) {
            return "El modulo " + modulo.getDescripcion() + " no está activo";
        }

        if (modulo.getFechaDesde() == null) {
            modulo.setFechaDesde(UtilFechas.getDate(2000, 0, 1));
        }

        if (modulo.getFechaHasta() == null) {
            modulo.setFechaHasta(UtilFechas.getDate(2100, 0, 1));
        }

        if (fechaMovimiento.before(modulo.getFechaDesde())
                || fechaMovimiento.after(modulo.getFechaHasta())) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            return "- La fecha del comprobante (" + sdf.format(fechaMovimiento) + ") no se encuentra dentro del rango de fechas habilitadas para el módulo " + modulo.getDescripcion() + " \n";
        }

        return "";
    }

    public boolean isActiveModulo(String codigo) {

        Modulo modulo = getModulo(codigo);
        return modulo != null && modulo.getActivo().equals("S");

    }

    public void eliminar(Modulo m) throws Exception {

        moduloDAO.eliminar(m);
    }

    public List<Modulo> getModuloByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return moduloDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 0);
    }

    public List<Modulo> getModuloByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return moduloDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Modulo> getModuloForPasajeContable() {

        return moduloDAO.getModuloForPasajeContable();
    }
}
