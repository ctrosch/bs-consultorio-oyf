/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.AnioLectivoDAO;
import bs.educacion.modelo.AnioLectivo;
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
public class AnioLectivoRN implements Serializable {

    @EJB
    private AnioLectivoDAO anioLectivoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AnioLectivo guardar(AnioLectivo anio, boolean esNuevo) throws Exception {

        control(anio);
        
        if (esNuevo) {

            if (anioLectivoDAO.getObjeto(AnioLectivo.class, anio.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Año Lectivo con el código " + anio.getCodigo());
            }
            anioLectivoDAO.crear(anio);

        } else {
            anio = (AnioLectivo) anioLectivoDAO.editar(anio);
        }

        return anio;

    }

    private void control(AnioLectivo anio) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (anio.getCodigo() == null || anio.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (anio.getDescripcion() == null || anio.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(AnioLectivo anio) throws Exception {

        anioLectivoDAO.eliminar(anio);

    }

    public AnioLectivo duplicar(AnioLectivo a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Año Lectivo nulo, nada para duplicar");
        }

        AnioLectivo anio = a.clone();
        anio.setCodigo(a.getCodigo());
        anio.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return anio;
    }

    public AnioLectivo getAnioLectivo(String codigo) {

        return anioLectivoDAO.getAnioLectivo(codigo);
    }

    public List<AnioLectivo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return anioLectivoDAO.getAnioLectivoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<AnioLectivo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return anioLectivoDAO.getAnioLectivoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
