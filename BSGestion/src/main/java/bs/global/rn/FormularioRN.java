/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.FormularioDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.FormularioPK;
import bs.global.modelo.FormularioPorSituacionIVA;
import bs.global.modelo.PuntoVenta;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class FormularioRN implements Serializable {

    @EJB
    private FormularioDAO formularioDAO;
    @EJB
    private FormularioPorSituacionIVARN formularioPorSituacionIVARN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Formulario guardar(Formulario formulario, boolean esNuevo) throws ExcepcionGeneralSistema {

        if (esNuevo) {
            if (formularioDAO.getFormulario(new FormularioPK(formulario.getModfor(), formulario.getCodigo())) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un formulario con el código" + formulario.getCodigo());
            }
            formularioDAO.crear(formulario);
        } else {
            formulario = (Formulario) formularioDAO.editar(formulario);
        }

        return formulario;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Formulario guardar(Formulario formulario) throws Exception {

        FormularioPK idPK = new FormularioPK(formulario.getModfor(), formulario.getCodigo());

        if (formularioDAO.getFormulario(idPK) == null) {
            formularioDAO.crear(formulario);
        } else {
            formulario = (Formulario) formularioDAO.editar(formulario);
        }

        return formulario;
    }

    public Formulario getFormulario(FormularioPK idPK) {

        return formularioDAO.getFormulario(idPK);
    }

    public Formulario getFormulario(String modulo, String codigo) {

        FormularioPK idPK = new FormularioPK(modulo, codigo);
        return formularioDAO.getFormulario(idPK);
    }

    public Formulario getFormulario(Comprobante comprobante, PuntoVenta puntoVenta, String cndIva) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No es posible obtener formulario, comprobante en blanco");
        }

        if (cndIva == null) {
            throw new ExcepcionGeneralSistema("No es posible obtener formulario, la entidad comercial seleccionado no tienen condición de iva asignada");
        }

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No es posible obtener formulario, punto de venta en blanco");
        }

        /**
         * Validamos el tipo de movimiento para buscar el formulario por
         * situacion de iva lo correcto es verificar desde donde registra para
         * ver si se busca la situacion de iva
         */
        FormularioPorSituacionIVA fpsi;
        fpsi = formularioPorSituacionIVARN.getFormulario(comprobante.getModulo(),
                comprobante.getCodigo(),
                puntoVenta.getCodigo(),
                cndIva);

        if (fpsi == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario asociado al comprobante "
                    + "(" + comprobante.getModulo() + "-" + comprobante.getCodigo() + ") " + comprobante.getDescripcion()
                    + " para el punto de venta (" + puntoVenta.getCodigo() + ") "
                    + " y condición de IVA: " + cndIva);
        }

        return fpsi.getFormulario();
    }

    public void actualizarUltimoNumero(Formulario formulario) throws Exception {

        formulario.setUltimoNumero(formulario.getUltimoNumero() + 1);
        guardar(formulario);
    }

    public synchronized int tomarProximoNumero(Formulario formulario) throws ExcepcionGeneralSistema {
        try {
            if (formulario == null) {
                throw new ExcepcionGeneralSistema("No se encontró formulario ");
            }
            int ultimoNumeroFromMov = formularioDAO.sincronizarUltimoNumeroFormulario(formulario);

//            System.err.println("ultimoNumeroFromMov " + ultimoNumeroFromMov);
//            System.err.println("ultimoNumero " + formulario.getUltimoNumero());
//            //Esta comparación se realiza por si no existe ningún movimiento en las bases
//            int ultimoNumero = f.getUltimoNumero();
//
//            if(ultimoNumero<ultimoNumeroFromMov){
//                ultimoNumero = ultimoNumeroFromMov;
//            }
            ultimoNumeroFromMov++;
            formulario.setUltimoNumero(ultimoNumeroFromMov);

//            System.err.println("f " + formulario.getUltimoNumero());
            formulario = guardar(formulario);

            return formulario.getUltimoNumero();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "No es posible actualizar formulario ", ex);

            return 0;
        }
    }

    public boolean existeMovimiento(Formulario formulario, Integer numeroFormulario) {

        return formularioDAO.existeMovimiento(formulario, numeroFormulario);
    }

    public List<Formulario> getFormularioByBusqueda(String modulo, String puntoVenta, String txtBusqueda, boolean mostrarDebaja) {

        Map<String, String> filtro = formularioDAO.getFiltro();
        filtro.put("modfor", " = '" + modulo + "'");
        filtro.put("puntoVenta.codigo ", " = '" + puntoVenta + "'");
        return formularioDAO.getFormularioByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Formulario> getFormularioByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja) {

        Map<String, String> filtro = formularioDAO.getFiltro();
        filtro.put("modfor", " = '" + modulo + "'");
        return formularioDAO.getFormularioByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Formulario> getFormularioByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return formularioDAO.getFormularioByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Formulario> getFormularioByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return formularioDAO.getFormularioByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<Formulario> getFormularioByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return formularioDAO.getFormularioByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public void eliminar(Formulario formulario) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Formulario duplicar(Formulario formulario) throws CloneNotSupportedException {

        formulario = formulario.clone();
        formulario.setCodigo("");
        return formulario;

    }

}
