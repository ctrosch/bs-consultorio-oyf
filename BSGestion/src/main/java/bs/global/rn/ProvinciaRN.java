/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.ProvinciaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Provincia;
import bs.global.modelo.ProvinciaPK;
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
public class ProvinciaRN implements Serializable {

    @EJB
    private ProvinciaDAO provinciaDAO;

    public Provincia getProvincia(ProvinciaPK idPK) {

        return provinciaDAO.getObjeto(Provincia.class, idPK);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Provincia p, boolean esNuevo) throws Exception {

        if (esNuevo) {

            ProvinciaPK idPk = new ProvinciaPK(p.getCodpai(), p.getCodigo());

            if (provinciaDAO.getObjeto(Provincia.class, idPk) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una provincia con el código " + p.getCodigo() + " para el pais " + p.getPais().getDescripcion());
            }
            provinciaDAO.crear(p);
        } else {
            provinciaDAO.editar(p);
        }
    }

    public void eliminar(Provincia p) throws Exception {

        provinciaDAO.eliminar(p);
    }

    public List<Provincia> getLista(String codPais, boolean mostrarDebaja) {
        return provinciaDAO.getListaByBusqueda(codPais, "", mostrarDebaja, 30);
    }

    public List<Provincia> getListaByBusqueda(String codPais, String txtBusqueda, boolean mostrarDebaja) {

        return provinciaDAO.getListaByBusqueda(codPais, txtBusqueda, mostrarDebaja, 30);

    }

    public List<Provincia> getListaByBusqueda(String codPais, String txtBusqueda, boolean mostrarDebaja, int cantRegistros) {

        return provinciaDAO.getListaByBusqueda(codPais, txtBusqueda, mostrarDebaja, cantRegistros);

    }

}
