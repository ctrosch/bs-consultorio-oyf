/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.global.dao.ComprobanteDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.ComprobantePK;
import bs.global.modelo.Concepto;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPorSituacionIVA;
import bs.global.modelo.FormularioPorSituacionIVAPK;
import bs.global.modelo.ItemDistribucionConcepto;
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
public class ComprobanteRN implements Serializable {

    @EJB
    private ComprobanteDAO comprobanteDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Comprobante guardar(Comprobante comprobante, boolean esNuevo) throws Exception {

        control(comprobante);
        reorganizarNroItem(comprobante);
        asignarDatos(comprobante);

        if (esNuevo) {

            if (comprobanteDAO.getComprobante(new ComprobantePK(comprobante.getModulo(), comprobante.getCodigo())) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + comprobante.getCodigo());
            }
            comprobanteDAO.crear(comprobante);

        } else {

            comprobante = comprobanteDAO.editar(comprobante);
        }

        return comprobante;
    }

    private void asignarDatos(Comprobante comprobante) throws ExcepcionGeneralSistema, Exception {

        if (!comprobante.getModulo().equals("PV")
                && !comprobante.getModulo().equals("VT")
                && !comprobante.getModulo().equals("PR")
                && !comprobante.getModulo().equals("ED")) {

            comprobante.setDebeHaber("X");
            comprobante.setTipoImputacion("ND");
            comprobante.setSignoAplicacionCuentaCorriente("X");
            comprobante.setSeIncluyeEnCiti("N");
        }

        if (!comprobante.getModulo().equals("PV") && !comprobante.getModulo().equals("VT") && !comprobante.getModulo().equals("CJ")) {
            comprobante.setSeIncluyeEnSubDiario("N");
        }

        if (!comprobante.getModulo().equals("PV")
                && !comprobante.getModulo().equals("VT")
                && !comprobante.getModulo().equals("CO")
                && !comprobante.getModulo().equals("FC")) {

            comprobante.setSeIncluyeEnEstadisticas("N");
        }

        if (comprobante.getFormulariosPorSituacionIVA() != null) {

            for (FormularioPorSituacionIVA fsi : comprobante.getFormulariosPorSituacionIVA()) {

                if (fsi.getModcom() == null || fsi.getModcom().isEmpty()) {
                    fsi.setModcom(comprobante.getModulo());
                }

                if (fsi.getCodcom() == null || fsi.getCodcom().isEmpty()) {
                    fsi.setCodcom(comprobante.getCodigo());
                }

                if (fsi.getFormulario() != null && fsi.getSucurs() == null || fsi.getSucurs().isEmpty()) {
                    fsi.setSucurs(fsi.getFormulario().getPuntoVenta().getCodigo());
                }

                if (fsi.getCondicionDeIva() != null && fsi.getCndiva() == null || fsi.getCndiva().isEmpty()) {
                    fsi.setCndiva(fsi.getCondicionDeIva().getCodigo());
                }
            }
        }
    }

    private void control(Comprobante comprobante) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (comprobante.getCodigo() == null || comprobante.getCodigo().isEmpty()) {
            sErrores += "- El código no puede estar vacío \n";
        }

        if (comprobante.getDescripcion() == null || comprobante.getDescripcion().isEmpty()) {

            sErrores += "- La descripción no puede estar vacía \n";
        }

//        if (comprobante.getEsComprobanteReversion() != null && comprobante.getEsComprobanteReversion().equals("S") && comprobante.getComprobanteReversion() == null) {
//            sErrores += "- El comprobante es comprobante de reversión, debe indicar el comprobante a revertir. \n";
//        }
        if (comprobante.getModulo().equals("PV") && comprobante.getModulo().equals("VT") && comprobante.getModulo().equals("CJ")) {

            if (comprobante.getSeIncluyeEnSubDiario() == null || comprobante.getSeIncluyeEnSubDiario().isEmpty()) {

                sErrores += "- Indique si el comprobante se incluye en el Subdiario \n";
            }

            if (comprobante.getConceptos() == null && comprobante.getEsComprobanteReversion().equals("N")) {

                sErrores += "- Debe tener al menos un concepto para el debe y un concepto para el haber.\n";

            }
        }

        if (comprobante.getModulo().equals("CJ") && comprobante.getEsComprobanteReversion().equals("N")) {

            if (comprobante.getConceptos() != null) {

                boolean hayConceptoD = false;
                boolean hayConceptoH = false;

                for (ConceptoComprobante cc : comprobante.getConceptos()) {

                    if (cc.getConcepto() != null) {

                        if (cc.getDebeHaber().equals("D")) {

                            hayConceptoD = true;
                        }

                        if (cc.getDebeHaber().equals("H")) {

                            hayConceptoH = true;
                        }
                    }
                }

                if (!hayConceptoD) {
                    sErrores += "- El comprobante debe tener al menos un concepto para la columna debe\n";
                }

                if (!hayConceptoH) {
                    sErrores += "- El comprobante debe tener al menos un concepto para la columna haber\n";
                }
            }
        }

        if (comprobante.getModulo().equals("PV") || comprobante.getModulo().equals("VT")) {

            if (comprobante.getDebeHaber() == null || comprobante.getDebeHaber().isEmpty()) {

                sErrores += "- Indique si es el comprobante que impacta en el debe o en el haber \n";
            }

            if (comprobante.getTipoImputacion() == null || comprobante.getTipoImputacion().isEmpty()) {

                sErrores += "- Indique el tipo de imputación en cuenta corriente \n";
            }

            if (comprobante.getSignoAplicacionCuentaCorriente() == null || comprobante.getSignoAplicacionCuentaCorriente().isEmpty()) {

                sErrores += "- Indique el signo de imputación en cuenta corriente \n";
            }

            if (comprobante.getSeIncluyeEnCiti() == null || comprobante.getSeIncluyeEnCiti().isEmpty()) {

                sErrores += "- Indique si el comprobante se incluye en el CITI \n";
            }

        }

        if (comprobante.getModulo().equals("PV")
                && comprobante.getModulo().equals("VT")
                && comprobante.getModulo().equals("CO")
                && comprobante.getModulo().equals("CG")
                && comprobante.getModulo().equals("FC")) {

            if (comprobante.getSeIncluyeEnEstadisticas() == null || comprobante.getSeIncluyeEnEstadisticas().isEmpty()) {

                sErrores += "- Indique si el comprobante se incluye en las estadísticas \n";
            }
        }

        if (comprobante.getConceptos() != null) {

            for (ConceptoComprobante cc : comprobante.getConceptos()) {

                if (cc.getConcepto() == null) {
                    cc.setConError(true);
                    sErrores += "- Existe un item sin concepto asignado \n";
                }
            }
        }

        if (comprobante.getFormulariosPorSituacionIVA() != null) {

            for (FormularioPorSituacionIVA fsi : comprobante.getFormulariosPorSituacionIVA()) {

                if (fsi.getFormulario() == null) {
                    fsi.setConError(true);
                    sErrores += "- Existe un item sin formulario asignado\n";
                }

                if (fsi.getCondicionDeIva() == null) {
                    fsi.setConError(true);
                    sErrores += "- Existe un item sin condición de iva asignada\n";
                }
            }
        }

        //Si se lo deshabilita no controlamos nada
        if (comprobante.getAuditoria().getDebaja().equals("S")) {
            sErrores = "";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public Comprobante getComprobante(ComprobantePK idPK) throws ExcepcionGeneralSistema {

        Comprobante ct = comprobanteDAO.getComprobante(idPK);
        if (ct == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante (" + idPK.getModulo() + " - " + idPK.getCodigo() + ")");
        }
        return ct;
    }

    public Comprobante getComprobante(String modulo, String codigo) throws ExcepcionGeneralSistema {

        Comprobante ct = comprobanteDAO.getComprobante(new ComprobantePK(modulo, codigo));
        if (ct == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante (" + modulo + " - " + codigo + ")");
        }
        return ct;
    }

    public void eliminar(Comprobante comprobante) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Comprobante> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja) {

        return comprobanteDAO.getListaByBusqueda(modulo, null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Comprobante> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return comprobanteDAO.getListaByBusqueda(modulo, null, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public List<Comprobante> getListaByBusqueda(String modulo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return comprobanteDAO.getListaByBusqueda(modulo, filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Comprobante> getListaByBusqueda(String modulo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return comprobanteDAO.getListaByBusqueda(modulo, filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    //----------------------------------------------------------------------------
    public void nuevoItemConcepto(Comprobante comprobante) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No existe un comprobante seleccionado para agregarle un concepto");
        }

        if (comprobante.getConceptos() == null) {
            comprobante.setConceptos(new ArrayList<ConceptoComprobante>());
        }

        ConceptoComprobante itemConcepto = new ConceptoComprobante();
        itemConcepto.setComprobante(comprobante);
        itemConcepto.setNroitm(comprobante.getConceptos().size() + 1);

        comprobante.getConceptos().add(itemConcepto);
        reorganizarNroItem(comprobante);
        cargarCentrosCostos(comprobante);
    }

    public void eliminarItemConcepto(Comprobante comprobante, ConceptoComprobante itemConcepto) throws ExcepcionGeneralSistema, Exception {

        if (itemConcepto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ConceptoComprobante item : comprobante.getConceptos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemConcepto.getNroitm()
                        && item.getConcepto().equals(itemConcepto.getConcepto())) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ConceptoComprobante remove = comprobante.getConceptos().remove(indiceItemBorrar);

            if (remove != null) {

                if (remove.getId() != null) {
                    comprobanteDAO.eliminar(ConceptoComprobante.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(comprobante);
        comprobante = guardar(comprobante, false);

    }

    public void asignarConceptoItemConcepto(Comprobante comprobante, Concepto concepto) throws ExcepcionGeneralSistema {

        if (existeConceptoItemConcepto(comprobante, concepto)) {
            eliminarConceptoItemConcepto(comprobante, concepto);
            throw new ExcepcionGeneralSistema("El concepto seleccionado ya existe para este comprobante");
        }
    }

    public void eliminarConceptoItemConcepto(Comprobante comprobante, Concepto concepto) {

        if (comprobante == null || concepto == null) {
            return;
        }

        if (comprobante.getConceptos() != null) {

            for (ConceptoComprobante item : comprobante.getConceptos()) {

                if (item.getConcepto() != null) {
                    if (item.getConcepto().equals(concepto)) {
                        item.setConcepto(null);
                        return;
                    }
                }
            }
        }
    }

    public boolean existeConceptoItemConcepto(Comprobante comprobante, Concepto concepto) {

        int cont = 0;

        if (comprobante != null && comprobante.getConceptos() != null && concepto != null) {

            for (ConceptoComprobante item : comprobante.getConceptos()) {

                if (item.getConcepto() != null) {

                    if (item.getConcepto().equals(concepto)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    public void asignarConceptoAsociadoItemConcepto(Comprobante comprobante, Concepto concepto) throws ExcepcionGeneralSistema {

        if (existeConceptoAsociadoItemConcepto(comprobante, concepto)) {
            eliminarConceptoAsociadoItemConcepto(comprobante, concepto);
            throw new ExcepcionGeneralSistema("El concepto seleccionado ya existe para este comprobante");
        }
    }

    public void eliminarConceptoAsociadoItemConcepto(Comprobante comprobante, Concepto concepto) {

        if (comprobante == null || concepto == null) {
            return;
        }

        if (comprobante.getConceptos() != null) {

            for (ConceptoComprobante item : comprobante.getConceptos()) {

                if (item.getConceptoAsociado().equals(concepto)) {

                    if (item.getConceptoAsociado() != null) {

                        item.setConceptoAsociado(null);
                        return;

                    }
                }
            }
        }
    }

    public boolean existeConceptoAsociadoItemConcepto(Comprobante comprobante, Concepto concepto) {

        int cont = 0;

        if (comprobante != null && comprobante.getConceptos() != null && concepto != null) {

            for (ConceptoComprobante item : comprobante.getConceptos()) {

                if (item.getConceptoAsociado() != null) {

                    if (item.getConceptoAsociado().equals(concepto)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    public Comprobante duplicar(Comprobante comprobante) throws CloneNotSupportedException {

        comprobante = comprobante.clone();
        comprobante.setCodigo("");
        comprobante.setDescripcion(comprobante.getDescripcion() + " (duplicado)");

        comprobante.getConceptos().clear();
        comprobante.getFormulariosPorSituacionIVA().clear();

        return comprobante;

    }

    //----------------------------------------------------------------------------
    public void nuevoItemFormulario(Comprobante comprobante) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No existe un comprobante seleccionado para agregarle un formulario");
        }

        if (comprobante.getFormulariosPorSituacionIVA() == null) {
            comprobante.setFormulariosPorSituacionIVA(new ArrayList<FormularioPorSituacionIVA>());
        }

        FormularioPorSituacionIVA itemFormulario = new FormularioPorSituacionIVA();
        itemFormulario.setModcom(comprobante.getModulo());
        itemFormulario.setCodcom(comprobante.getCodigo());
        itemFormulario.setComprobante(comprobante);

        comprobante.getFormulariosPorSituacionIVA().add(itemFormulario);
    }

    public void eliminarItemFormulario(Comprobante comprobante, FormularioPorSituacionIVA itemFormulario) throws ExcepcionGeneralSistema, Exception {

        if (itemFormulario == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemFormulario.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            FormularioPorSituacionIVA remove = comprobante.getFormulariosPorSituacionIVA().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getCodcom() != null) {
                    FormularioPorSituacionIVAPK idPK = new FormularioPorSituacionIVAPK(remove.getModcom(), remove.getCodcom(), remove.getSucurs(), remove.getCndiva());

                    remove = comprobanteDAO.getObjeto(FormularioPorSituacionIVA.class, idPK);

                    if (remove != null) {
                        comprobanteDAO.eliminar(FormularioPorSituacionIVA.class, idPK);
                    }

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(comprobante);
        comprobante = guardar(comprobante, false);
    }

    public void asignarFormularioItemFormulario(Comprobante comprobante, Formulario formulario) throws ExcepcionGeneralSistema {

        if (existeFormularioItemFormulario(comprobante, formulario)) {
            eliminarFormularioItemFormulario(comprobante, formulario);
            throw new ExcepcionGeneralSistema("El formulario seleccionado ya existe para este comprobante");
        }

    }

    public boolean existeFormularioItemFormulario(Comprobante comprobante, Formulario formulario) {

        int cont = 0;

        if (comprobante != null && comprobante.getFormulariosPorSituacionIVA() != null && formulario != null) {

            for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {

                if (item.getFormulario() != null && item.getPuntoVenta() != null && item.getCondicionDeIva() != null) {

                    if (item.getFormulario().equals(formulario)
                            && item.getPuntoVenta().equals(item)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    public void eliminarFormularioItemFormulario(Comprobante comprobante, Formulario formulario) {

        if (comprobante == null || formulario == null) {
            return;
        }

        if (comprobante.getConceptos() != null) {

            for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {

                if (item.getFormulario().equals(formulario)) {
                    item.setFormulario(null);
                    return;
                }
            }
        }
    }

    public void asignarSituacionIvaItemFormulario(Comprobante comprobante, CondicionDeIva condicionIva) throws ExcepcionGeneralSistema {

        if (existeSituacionIvaItemFormulario(comprobante, condicionIva)) {
            eliminarSituacionIvaItemFormulario(comprobante, condicionIva);
            throw new ExcepcionGeneralSistema("El concepto seleccionado ya existe para este comprobante");
        }
    }

    public boolean existeSituacionIvaItemFormulario(Comprobante comprobante, CondicionDeIva condicionIva) {

        int cont = 0;

        if (comprobante != null && comprobante.getFormulariosPorSituacionIVA() != null && condicionIva != null) {

            for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {

                if (item.getFormulario() != null) {

                    if (item.getCondicionDeIva().equals(condicionIva)) {
                        cont++;
                    }
                }
            }
        }
        return (cont > 1);
    }

    public void eliminarSituacionIvaItemFormulario(Comprobante comprobante, CondicionDeIva condicionIva) {

        if (comprobante == null || condicionIva == null) {
            return;
        }

        if (comprobante.getConceptos() != null) {

            for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {

                if (item.getCondicionDeIva().equals(condicionIva)) {
                    item.setCondicionDeIva(null);
                    return;
                }
            }
        }
    }

    public void reorganizarNroItem(Comprobante comprobante) {

        //Reorganizamos los números de items
        int i;

        if (comprobante.getConceptos() != null) {

            i = 1;
            for (ConceptoComprobante item : comprobante.getConceptos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (comprobante.getFormulariosPorSituacionIVA() != null) {

            i = 1;
            for (FormularioPorSituacionIVA item : comprobante.getFormulariosPorSituacionIVA()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void nuevoItemDistribucion(ConceptoComprobante conceptoComprobante) throws ExcepcionGeneralSistema {

        if (conceptoComprobante == null) {
            throw new ExcepcionGeneralSistema("No existe un item concepto seleccionado para agregarle una distribución automática");
        }

        if (conceptoComprobante.getItemsDistribucion() == null) {
            conceptoComprobante.setItemsDistribucion(new ArrayList<ItemDistribucionConcepto>());
        }

        ItemDistribucionConcepto itemDistribucion = new ItemDistribucionConcepto();
        itemDistribucion.setNroItem(conceptoComprobante.getItemsDistribucion().size() + 1);
        itemDistribucion.setConceptoComprobante(conceptoComprobante);

    }

    public void eliminarItemDistribucion(ItemDistribucionConcepto distribucion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cargarCentrosCostos(Comprobante comprobante) {

        if (comprobante.getConceptos() == null) {
            return;
        }

        for (ConceptoComprobante itemConcepto : comprobante.getConceptos()) {

            if (itemConcepto.getConcepto() == null) {
                continue;
            }

            CuentaContable cuentaContable = (itemConcepto.getCuentaContable() == null ? itemConcepto.getConcepto().getCuentaContable() : itemConcepto.getCuentaContable());

            if (cuentaContable == null) {
                return;
            }

            //Recorremos los centros de costos en lo que debe imputar la cuenta contable
            if (cuentaContable.getCentrosCostos() != null) {

                if (itemConcepto.getItemsDistribucion() == null) {
                    itemConcepto.setItemsDistribucion(new ArrayList<ItemDistribucionConcepto>());
                }

                for (CuentaContableCentroCosto centroCosto : cuentaContable.getCentrosCostos()) {

                    boolean existe = false;

                    //Recorremos los centros de costos cargados en el concepto
                    for (ItemDistribucionConcepto id : itemConcepto.getItemsDistribucion()) {

                        if (id.getCentroCosto().equals(centroCosto.getCentroCosto())) {
                            existe = true;
                        }
                    }

                    if (!existe) {

                        ItemDistribucionConcepto itemDistribucion = new ItemDistribucionConcepto();
                        itemDistribucion.setNroItem(itemConcepto.getItemsDistribucion().size() + 1);
                        itemDistribucion.setConceptoComprobante(itemConcepto);
                        itemDistribucion.setCentroCosto(centroCosto.getCentroCosto());

                        itemConcepto.getItemsDistribucion().add(itemDistribucion);
                    }
                }
            }
        }
    }
}
