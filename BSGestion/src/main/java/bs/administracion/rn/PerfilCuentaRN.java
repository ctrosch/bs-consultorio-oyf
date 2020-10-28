/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.PerfilCuentaDAO;
import bs.administracion.modelo.PerfilCuenta;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
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
public class PerfilCuentaRN implements Serializable {

    @EJB
    private PerfilCuentaDAO perfilCuentaDAO;

    public PerfilCuenta getPerfilCuenta(Integer id) {

        return perfilCuentaDAO.getPerfilCuenta(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PerfilCuenta guardar(PerfilCuenta p, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (perfilCuentaDAO.getObjeto(PerfilCuenta.class, p.getId()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Perfil de Cuenta Email con ese id " + p.getId());
            }
            perfilCuentaDAO.crear(p);

        } else {
            p = (PerfilCuenta) perfilCuentaDAO.editar(p);
        }

        return p;

    }

    public void eliminar(PerfilCuenta p) throws Exception {

        perfilCuentaDAO.eliminar(p);
    }

    public List<PerfilCuenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return perfilCuentaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public PerfilCuenta duplicar(PerfilCuenta perfilCuenta) throws CloneNotSupportedException {

        perfilCuenta = perfilCuenta.clone();
        perfilCuenta.setId(null);
        perfilCuenta.setNombre(perfilCuenta.getNombre() + " (duplicado)");
        perfilCuenta.setUsuario(perfilCuenta.getUsuario() + " (duplicado)");

        return perfilCuenta.clone();
    }

    public Integer getProximoId() {
        return perfilCuentaDAO.getMaxId();
    }
}
