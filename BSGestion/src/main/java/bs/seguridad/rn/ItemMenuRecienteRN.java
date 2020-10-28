/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.rn;

import bs.seguridad.dao.ItemMenuRecienteDAO;
import bs.seguridad.modelo.ItemMenuReciente;
import bs.seguridad.modelo.ItemMenuRecientePK;
import bs.seguridad.modelo.Usuario;
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
public class ItemMenuRecienteRN implements Serializable {

    @EJB
    ItemMenuRecienteDAO itemMenuRecienteDAO;

    /**
     * No editar, solo agrega si no existe.
     *
     * @param ir
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ItemMenuReciente guardar(ItemMenuReciente ir) throws Exception {

        ItemMenuRecientePK idPK = new ItemMenuRecientePK(ir.getIdUsuario(), ir.getCodMenu());

        if (itemMenuRecienteDAO.getObjeto(ItemMenuReciente.class, idPK) == null) {
            itemMenuRecienteDAO.crear(ir);
        } else {
            ir = (ItemMenuReciente) itemMenuRecienteDAO.editar(ir);
        }
        return ir;
    }

    public ItemMenuReciente getItemMenuReciente(int id) {
        return itemMenuRecienteDAO.getObjeto(ItemMenuReciente.class, id);
    }

    public List<ItemMenuReciente> getItemMenuRecienteByUsuario(Usuario usuario) {

        return itemMenuRecienteDAO.getItemMenuRecienteByUsuario(usuario);
    }

}
