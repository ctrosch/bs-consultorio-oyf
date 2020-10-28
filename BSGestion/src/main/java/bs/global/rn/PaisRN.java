/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.PaisDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Pais;
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
public class PaisRN implements Serializable {

    @EJB
    private PaisDAO paisDAO;

    public Pais getPais(String value) {
        return paisDAO.getObjeto(Pais.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Pais pais, boolean esNuevo) throws Exception {
        if (esNuevo) {

            if (paisDAO.getObjeto(Pais.class, pais.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un pais con el código" + pais.getCodigo());
            }
            paisDAO.crear(pais);
        } else {
            paisDAO.editar(pais);
        }

    }

    public void eliminar(Pais pais) throws Exception {

        paisDAO.eliminar(pais);
    }

    public List<Pais> getLista(boolean mostrarDebaja) {
        return paisDAO.getLista(mostrarDebaja);
    }

    public List<Pais> getPaisByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return paisDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Pais> getPaisByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return paisDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

}
