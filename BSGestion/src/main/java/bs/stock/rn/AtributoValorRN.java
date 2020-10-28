/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.AtributoValorDAO;
import bs.stock.modelo.AtributoValor;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class AtributoValorRN implements Serializable {

    @EJB
    AtributoValorDAO atributoValorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(AtributoValor a, boolean esNuevo) throws Exception {

        control(a);

        if (a.getId() == null) {
            atributoValorDAO.crear(a);
        } else {
            atributoValorDAO.editar(a);
        }
    }

    public void control(AtributoValor valor) throws ExcepcionGeneralSistema {

        if (valor != null) {

            if (valor.getValor() == null || valor.getValor().isEmpty()) {
                throw new ExcepcionGeneralSistema("El valor del atributo no puede estar vacío para el atributo " + valor.getAtributo().getNombre());
            }
        }
    }

    public AtributoValor getAtributoValor(int id) {

        return atributoValorDAO.getObjeto(AtributoValor.class, id);
    }

    public void eliminar(AtributoValor aplicacion) throws Exception {

        atributoValorDAO.eliminar(aplicacion);
    }

    public List<String> getListaByBusqueda(String nombre, String txtBusqueda, boolean mostrarDeBaja, int cantidadRegistros) {

        return atributoValorDAO.getListaByBusqueda(nombre, txtBusqueda, mostrarDeBaja, cantidadRegistros);
    }

}
