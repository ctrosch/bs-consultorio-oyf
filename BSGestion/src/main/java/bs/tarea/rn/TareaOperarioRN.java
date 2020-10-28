/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.rn;

import bs.tarea.dao.TareaOperarioDAO;
import bs.tarea.modelo.TareaOperario;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless

public class TareaOperarioRN implements Serializable {

    @EJB
    private TareaOperarioDAO tareaDAO;

    public List<TareaOperario> getListaByBusqueda(Map<String, String> filtro, boolean mostrarDebaja, int cantidadRegistros) {

        return tareaDAO.getListaByBusqueda(filtro, mostrarDebaja, cantidadRegistros);
    }

}
