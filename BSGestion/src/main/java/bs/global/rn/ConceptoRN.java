/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.ConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Concepto;
import bs.global.modelo.ConceptoPK;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.ImpuestoPorConceptoPK;
import bs.global.util.JsfUtil;
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
 * @author Claudio
 */
@Stateless
public class ConceptoRN implements Serializable {

    @EJB
    private ConceptoDAO conceptoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Concepto guardar(Concepto c, boolean esNuevo) throws Exception {

        control(c);
        preSave(c);

        if (esNuevo) {

            ConceptoPK idPk = new ConceptoPK(c.getModulo(), c.getTipo(), c.getCodigo());

            if (conceptoDAO.getObjeto(Concepto.class, idPk) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un conceto con el código" + idPk);
            }
            conceptoDAO.crear(c);
        } else {
            c = (Concepto) conceptoDAO.editar(c);
        }
        return c;
    }

    public Concepto getConcepto(String modulo, String tipo, String codigo) {

        ConceptoPK idPk = new ConceptoPK(modulo, tipo, codigo);
        return conceptoDAO.getObjeto(Concepto.class, idPk);
    }

    public List<Concepto> getConceptoByTipo(String modulo, String tipo, boolean mostrarDebaja) {

        return conceptoDAO.getConceptoByTipo(modulo, tipo, mostrarDebaja);
    }

    public List<Concepto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return conceptoDAO.getListaByBusqueda(null, null, filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Concepto> getListaByBusqueda(String modulo, String tipo, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return conceptoDAO.getListaByBusqueda(modulo, tipo, null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Concepto> getListaByBusqueda(String modulo, String tipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return conceptoDAO.getListaByBusqueda(modulo, tipo, filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public void eliminar(Concepto concepto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void nuevoItemImpuesto(Concepto concepto) throws ExcepcionGeneralSistema {

        if (concepto == null) {
            throw new ExcepcionGeneralSistema("No existe un concepto seleccionado para agregarle un impuesto");
        }

        if (concepto.getImpuestos() == null) {
            concepto.setImpuestos(new ArrayList<ImpuestoPorConcepto>());
        }

        ImpuestoPorConcepto itemImpuestoPorConcepto = new ImpuestoPorConcepto();
        itemImpuestoPorConcepto.setCodigo(concepto.getCodigo());
        itemImpuestoPorConcepto.setModulo(concepto.getModulo());
        itemImpuestoPorConcepto.setTipo(concepto.getTipo());
        itemImpuestoPorConcepto.setConcepto(concepto);

        concepto.getImpuestos().add(itemImpuestoPorConcepto);

    }

    public void eliminarItemImpuesto(Concepto concepto, ImpuestoPorConcepto itemImpuestoPorConcepto) throws ExcepcionGeneralSistema, Exception {
        try {
            if (concepto.getImpuestos().remove(itemImpuestoPorConcepto)) {

                ImpuestoPorConceptoPK impuestoPorConceptoPK = new ImpuestoPorConceptoPK();
                impuestoPorConceptoPK.setCodigo(itemImpuestoPorConcepto.getCodigo());
                impuestoPorConceptoPK.setModulo(itemImpuestoPorConcepto.getModulo());
                impuestoPorConceptoPK.setTipo(itemImpuestoPorConcepto.getTipo());
                impuestoPorConceptoPK.setCodimp(itemImpuestoPorConcepto.getCodimp());

                conceptoDAO.eliminar(ImpuestoPorConcepto.class, impuestoPorConceptoPK);
            }
        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible eliminar item " + ex);
        }

    }

    public void asignarCodigoImpuesto(ImpuestoPorConcepto impuestoPorConcepto) {

        impuestoPorConcepto.setCodimp(impuestoPorConcepto.getTipoImpuesto().getCodigo());
    }

    private void control(Concepto c) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (c.getTipoConcepto() == null) {
            sErrores += "- El Tipo de Concepto es Obligatorio \n ";
        }

        if (c.getCodigo() == null || c.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (c.getDescripcion() == null || c.getDescripcion().isEmpty()) {
            sErrores += "- La descripción es Obligatoria \n ";
        }

        if (c.getCuentaContable() == null) {
            sErrores += "- La Cuenta Contable es Obligatoria \n ";
        }

        if (c.getImpuestos() != null && !c.getImpuestos().isEmpty()) {

            for (int i = 0; i < c.getImpuestos().size(); i++) {
                int cantRepetidos = 0;
                for (int j = 0; j < c.getImpuestos().size(); j++) {
                    if (c.getImpuestos().get(i).equals(c.getImpuestos().get(j))) {
                        cantRepetidos++;
                    }
                    if (cantRepetidos > 1) {
                        sErrores += "- El Impuesto " + c.getImpuestos().get(i).getTipoImpuesto().getDescripcion() + " está repetido \n ";
                    }
                }
            }
        }
        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void preSave(Concepto c) {

        if (c.getImpuestos() != null && !c.getImpuestos().isEmpty()) {
            c.getImpuestos().forEach(i -> {
                i.setConcepto(c);
                i.setCodigo(c.getCodigo());
                i.setModulo(c.getModulo());
                i.setTipo(c.getTipo());

            });
        }
    }
}
