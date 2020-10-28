/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.TipoCarreraDAO;
import bs.educacion.modelo.Materia;
import bs.educacion.modelo.TipoCarrera;
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
public class TipoCarreraRN implements Serializable {

    @EJB
    private TipoCarreraDAO tipoCarreraDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoCarrera guardar(TipoCarrera tipoCarrera, boolean esNuevo) throws Exception {

        control(tipoCarrera);

        if (esNuevo) {

            if (tipoCarreraDAO.getObjeto(Materia.class, tipoCarrera.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Tipo de Carrera con el código " + tipoCarrera.getCodigo());
            }
            tipoCarreraDAO.crear(tipoCarrera);

        } else {
            tipoCarrera = (TipoCarrera) tipoCarreraDAO.editar(tipoCarrera);
        }

        return tipoCarrera;

    }

    private void control(TipoCarrera tipoCarrera) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (tipoCarrera.getCodigo() == null || tipoCarrera.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (tipoCarrera.getDescripcion() == null || tipoCarrera.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(TipoCarrera tipoCarrera) throws Exception {

        tipoCarreraDAO.eliminar(tipoCarrera);

    }

    public TipoCarrera duplicar(TipoCarrera t) throws Exception {

        if (t == null) {
            throw new ExcepcionGeneralSistema("Tipo de Carrera nulo, nada para duplicar");
        }

        TipoCarrera tipoCarrera = t.clone();
        tipoCarrera.setCodigo(t.getCodigo());
        tipoCarrera.setDescripcion(t.getDescripcion() + "( Duplicado)");

        return tipoCarrera;
    }

    public TipoCarrera getTipoCarrera(String codigo) {

        return tipoCarreraDAO.getTipoCarrera(codigo);
    }

    public List<TipoCarrera> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoCarreraDAO.getTipoCarreraByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<TipoCarrera> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoCarreraDAO.getTipoCarreraByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
