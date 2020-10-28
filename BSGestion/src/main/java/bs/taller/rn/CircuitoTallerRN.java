/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.taller.dao.CircuitoTallerDAO;
import bs.taller.modelo.CircuitoTaller;
import bs.taller.modelo.CircuitoTallerPK;
import bs.taller.modelo.CodigoCircuitoTaller;
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
public class CircuitoTallerRN implements Serializable {

    @EJB
    private CircuitoTallerDAO circuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CircuitoTaller c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            CircuitoTallerPK idPK = new CircuitoTallerPK(c.getCircom(), c.getCirapl());

            if (circuitoDAO.getObjeto(CircuitoTaller.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un circuito con el código " + idPK);
            }
            circuitoDAO.crear(c);

        } else {
            c = (CircuitoTaller) circuitoDAO.editar(c);
        }
    }

    public CircuitoTaller getCircuito(CircuitoTallerPK idPK) {
        return circuitoDAO.getCircuito(idPK);
    }

    public CircuitoTaller getCircuito(String circom, String cirapl) {

        CircuitoTallerPK idPK = new CircuitoTallerPK(circom, cirapl);
        return circuitoDAO.getCircuito(idPK);
    }

    public List<CircuitoTaller> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoTaller> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoTaller> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public Comprobante getComprobanteTaller(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

//        System.out.println(circuitoDAO.getComprobanteProduccion(circom, cirapl ,modcom,codcom));
        Comprobante cf = circuitoDAO.getComprobanteTaller(circom, cirapl, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de taller (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cf;
    }

    public Comprobante getComprobanteFacturacion(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

//        System.out.println(circuitoDAO.getComprobanteProduccion(circom, cirapl ,modcom,codcom));
        Comprobante cf = circuitoDAO.getComprobanteFacturacion(circom, cirapl, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de facturacion (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cf;
    }

    public Comprobante getComprobanteStock(String circom, String cirapl, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cs = circuitoDAO.getComprobanteStock(circom, cirapl, codcom);
        if (cs == null) {
            throw new ExcepcionGeneralSistema("El comprobante de stock (" + codcom + ") no está configurado para el circuito (" + circom + " - " + cirapl + ")");
        }
        return cs;
    }

    public CodigoCircuitoTaller getCodigoCircuito(String codigo) {

        return circuitoDAO.getCodigoCircuito(codigo);
    }

    public List<CodigoCircuitoTaller> getListaCodigoCircuito() {

        return circuitoDAO.getListaCodigoCircuito();
    }

    public void eliminar(CircuitoTaller circuito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cargarCircuitosRelacionados(CircuitoTaller circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", false));
    }

    public CircuitoTaller iniciarCircuito(String circom, String cirapl, String codTL, String codFC, String codST) throws ExcepcionGeneralSistema {

        CircuitoTaller circuito = null;

        if (circom != null && cirapl != null) {
            circuito = getCircuito(circom, cirapl);
        }

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No se encontró circuito " + circom + "-" + cirapl);
        }

        cargarCircuitosRelacionados(circuito);

        if (circuito.getActualizaTaller().equals("S") && codTL != null) {

            circuito.setComprobanteTaller(getComprobanteTaller(circom, cirapl, codTL));

        } else if (circuito.getActualizaFacturacion().equals("S") && codFC != null) {

            circuito.setComprobanteFacturacion(getComprobanteFacturacion(circom, cirapl, codFC));

            if (circuito.getCircuitoComienzoFacturacion() == null || circuito.getCircuitoAplicacionFacturacion() == null) {
                throw new ExcepcionGeneralSistema("El circuito define que actualiza el módulo de facturación pero los circuitos de facturación no está correctamente configurados");
            }

        } else if (circuito.getActualizaStock().equals("S") && codST != null) {

            circuito.setComprobanteStock(getComprobanteStock(circom, cirapl, codST));
        }

        return circuito;
    }

    public CircuitoTaller iniciarCircuito(String circom, String cirapl, String codTL) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, codTL, null, null);
    }

}
