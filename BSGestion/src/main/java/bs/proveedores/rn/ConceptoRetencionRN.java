/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.ConceptoRetencionDAO;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.proveedores.modelo.ConceptoRetencionPK;
import bs.proveedores.modelo.ConceptoRetencionValor;
import bs.proveedores.modelo.ConceptoRetencionValorPK;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class ConceptoRetencionRN implements Serializable {

    @EJB
    private ConceptoRetencionDAO conceptoRetencionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ConceptoRetencion guardar(ConceptoRetencion s, boolean esNuevo) throws Exception {

        if (esNuevo) {

            ConceptoRetencionPK idPk = new ConceptoRetencionPK(s.getTipo(), s.getCodigo());

            if (conceptoRetencionDAO.getObjeto(ConceptoRetencion.class, idPk) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + s.getClass().getName() + " con el código" + idPk);
            }
            conceptoRetencionDAO.crear(s);
        } else {
            s = (ConceptoRetencion) conceptoRetencionDAO.editar(s);
        }

        return s;
    }

    public ConceptoRetencion getConcepto(String tipo, String codigo) {

        ConceptoRetencionPK idPk = new ConceptoRetencionPK(tipo, codigo);
        return conceptoRetencionDAO.getObjeto(ConceptoRetencion.class, idPk);

        // ConceptoRetencion concepto = conceptoRetencionDAO.getConcepto(tipo, codigo);
        // return concepto;
    }

    public List<ConceptoRetencion> getLista() {
        return conceptoRetencionDAO.getListaByBusqueda(null, null, false, 100);
    }

    public List<ConceptoRetencion> getListaByBusqueda(String tipo, String txtBusqueda, boolean mostrarDebaja) {

        return conceptoRetencionDAO.getListaByBusqueda(tipo, txtBusqueda, mostrarDebaja, 15);
    }

    public List<ConceptoRetencion> getListaByBusqueda(String tipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return conceptoRetencionDAO.getListaByBusqueda(tipo, filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<ConceptoRetencion> getLista(boolean mostrarDebaja) {

        return conceptoRetencionDAO.getListaByBusqueda(null, null, mostrarDebaja, 100);

    }

    public void eliminar(ConceptoRetencion s) throws Exception {

        conceptoRetencionDAO.eliminar(s);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void nuevoItemValor(ConceptoRetencion concepto) {

        if (concepto != null) {

            if (concepto.getValores() == null) {
                concepto.setValores(new ArrayList<ConceptoRetencionValor>());
            }

            ConceptoRetencionValor itemValor = new ConceptoRetencionValor();
            itemValor.setTipo(concepto.getTipo());
            itemValor.setCodigo(concepto.getCodigo());
            itemValor.setNroitm(concepto.getValores().size() + 1);
            itemValor.setConcepto(concepto);

            concepto.getValores().add(itemValor);
        }
    }

    public void eliminarItemValor(ConceptoRetencion concepto, ConceptoRetencionValor itemValor) throws ExcepcionGeneralSistema, Exception {

        if (itemValor == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ConceptoRetencionValor item : concepto.getValores()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemValor.getNroitm()
                        && item.getConcepto().equals(itemValor.getConcepto())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ConceptoRetencionValor remove = concepto.getValores().remove(indiceItemBorrar);

            if (remove != null) {

                ConceptoRetencionValorPK idPK = new ConceptoRetencionValorPK(remove.getTipo(), remove.getCodigo(), remove.getNroitm());

                conceptoRetencionDAO.eliminar(ConceptoRetencionValor.class, idPK);

                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

}
