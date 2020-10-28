/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.ReglamentoDAO;
import bs.educacion.modelo.Reglamento;
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
public class ReglamentoRN implements Serializable {

    @EJB
    private ReglamentoDAO reglamentoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Reglamento guardar(Reglamento reglamento, boolean esNuevo) throws Exception {

        control(reglamento);

        if (esNuevo) {

            if (reglamentoDAO.getObjeto(Reglamento.class, reglamento.getId()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Reglamento con el id " + reglamento.getId());
            }
            reglamentoDAO.crear(reglamento);

        } else {
            reglamento = (Reglamento) reglamentoDAO.editar(reglamento);
        }

        return reglamento;

    }

    private void control(Reglamento reglamento) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (reglamento.getNombre() == null || reglamento.getNombre().isEmpty()) {
            sErrores += "- El Nombre es Obligatorio \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Reglamento reglamento) throws Exception {

        reglamentoDAO.eliminar(reglamento);

    }

    public Reglamento duplicar(Reglamento a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema(" nulo, nada para duplicar");
        }

        Reglamento reglamento = a.clone();
        reglamento.setId(reglamentoDAO.getMaxCodigo());

        reglamento.setNombre(a.getNombre() + "( Duplicado)");

        return reglamento;
    }

    public Reglamento getReglamento(Integer id) {

        return reglamentoDAO.getReglamento(id);
    }

    public List<Reglamento> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return reglamentoDAO.getReglamentoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Reglamento> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return reglamentoDAO.getReglamentoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public Integer getProximoCodigo() {
        int nroCta = reglamentoDAO.getMaxCodigo();
        return nroCta;

    }
}
