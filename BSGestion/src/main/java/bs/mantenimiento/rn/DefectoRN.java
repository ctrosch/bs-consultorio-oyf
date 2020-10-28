/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.DefectoDAO;
import bs.mantenimiento.modelo.Defecto;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class DefectoRN implements Serializable {

    @EJB
    private DefectoDAO defectoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Defecto guardar(Defecto defecto, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (defectoDAO.getObjeto(Defecto.class, defecto.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Defecto con el código " + defecto.getCodigo());
            }
            defectoDAO.crear(defecto);

        } else {
            defecto = (Defecto) defectoDAO.editar(defecto);
        }

        return defecto;

    }

    public void eliminar(Defecto defecto) throws Exception {

        defectoDAO.eliminar(defecto);

    }

    public Defecto duplicar(Defecto d) throws Exception {

        if (d == null) {
            throw new ExcepcionGeneralSistema("Defecto nulo, nada para duplicar");
        }

        Defecto defecto = d.clone();
        defecto.setCodigo(d.getCodigo());
        defecto.setDescripcion(d.getDescripcion() + "( Duplicado)");

        return defecto;
    }

    public Defecto getDefecto(String codigo) {

        return defectoDAO.getDefecto(codigo);
    }

    public List<Defecto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return defectoDAO.getDefectoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Defecto> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return defectoDAO.getDefectoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
