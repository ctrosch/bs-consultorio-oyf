/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.tesoreria.dao.BancoDAO;
import bs.tesoreria.modelo.Banco;
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
public class BancoRN implements Serializable {

    @EJB
    private BancoDAO bancoDAO;

    public Banco getBanco(String value) {
        return bancoDAO.getObjeto(Banco.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Banco v, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (bancoDAO.getObjeto(Banco.class, v.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un banco con el código " + v.getCodigo());
            }
            bancoDAO.crear(v);
        } else {
            bancoDAO.editar(v);
        }
    }

    public void eliminar(Banco v) throws Exception {

        bancoDAO.eliminar(v);
    }

    public List<Banco> getBancoByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return bancoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Banco> getBancoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return bancoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }
}
