/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.AreaEducacionDAO;
import bs.educacion.modelo.AreaEducacion;
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
public class AreaEducacionRN implements Serializable {

    @EJB
    private AreaEducacionDAO areaEducacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AreaEducacion guardar(AreaEducacion area, boolean esNuevo) throws Exception {

        control(area);

        if (esNuevo) {

            if (areaEducacionDAO.getObjeto(AreaEducacion.class, area.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un área con el código " + area.getCodigo());
            }
            areaEducacionDAO.crear(area);

        } else {
            area = (AreaEducacion) areaEducacionDAO.editar(area);
        }

        return area;

    }

    private void control(AreaEducacion area) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (area.getCodigo() == null || area.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (area.getDescripcion() == null || area.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(AreaEducacion area) throws Exception {

        areaEducacionDAO.eliminar(area);

    }

    public AreaEducacion duplicar(AreaEducacion a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Area nula, nada para duplicar");
        }

        AreaEducacion area = a.clone();
        area.setCodigo(a.getCodigo());
        area.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return area;
    }

    public AreaEducacion getAreaEducacion(String codigoArea) {

        return areaEducacionDAO.getAreaEducacion(codigoArea);
    }

    public List<AreaEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return areaEducacionDAO.getAreaEducacionByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<AreaEducacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return areaEducacionDAO.getAreaEducacionByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
