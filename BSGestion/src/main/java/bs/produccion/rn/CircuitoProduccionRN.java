package bs.produccion.rn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.produccion.dao.CircuitoProduccionDAO;
import bs.produccion.modelo.CircuitoProduccion;
import bs.produccion.modelo.CircuitoProduccionPK;
import bs.produccion.modelo.ItemCircuitoProduccionProduccion;
import bs.produccion.modelo.ItemCircuitoProduccionStock;
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
 * @author Agustin
 */
@Stateless
public class CircuitoProduccionRN implements Serializable {

    @EJB
    private CircuitoProduccionDAO circuitoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(CircuitoProduccion c, boolean esNuevo) throws Exception {

        if (esNuevo) {

            CircuitoProduccionPK idPK = new CircuitoProduccionPK(c.getCircom(), c.getCirapl());

            if (circuitoDAO.getObjeto(CircuitoProduccion.class, idPK) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un circuito con el código " + idPK);
            }
            circuitoDAO.crear(c);

        } else {
            circuitoDAO.editar(c);
        }
    }

    public CircuitoProduccion getCircuito(CircuitoProduccionPK idPK) {

        return circuitoDAO.getObjeto(CircuitoProduccion.class, idPK);

    }

    public CircuitoProduccion getCircuito(String circom, String cirapl) {

        CircuitoProduccionPK idPK = new CircuitoProduccionPK(circom, cirapl);
        return circuitoDAO.getObjeto(CircuitoProduccion.class, idPK);

    }

    public List<CircuitoProduccion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoProduccion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<CircuitoProduccion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return circuitoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public void eliminar(CircuitoProduccion codigoCircuitoProduccion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Comprobante getComprobanteProduccion(String circom, String cirapl, String modcom, String codcom) throws ExcepcionGeneralSistema {

//        System.out.println(circuitoDAO.getComprobanteProduccion(circom, cirapl ,modcom,codcom));
        Comprobante cf = circuitoDAO.getComprobanteProduccion(circom, cirapl, modcom, codcom);
        if (cf == null) {
            throw new ExcepcionGeneralSistema("El comprobante de producción (" + codcom + ") no está configurado para este circuito");
        }
        return cf;
    }

    public Comprobante getComprobanteStock(String circom, String cirapl, String modcom, String codcom) throws ExcepcionGeneralSistema {

        Comprobante cs = circuitoDAO.getComprobanteStock(circom, cirapl, modcom, codcom);
        if (cs == null) {
            throw new ExcepcionGeneralSistema("El comprobante de stock (" + codcom + ") no está configurado para este circuito");
        }
        return cs;
    }

    public void cargarCircuitosRelacionados(CircuitoProduccion circuito) {

        Map<String, String> filtro = circuitoDAO.getFiltro();

        filtro.put("circom = ", "'" + circuito.getCircom() + "'");
        filtro.put("cirapl <> ", "'" + circuito.getCircom() + "'");
        getListaByBusqueda(filtro, "", true);
        circuito.setCircuitosRelacionados(getListaByBusqueda(filtro, "", true));
    }

    /**
     * Se utiliza para generar movimientos no predeterminados
     *
     * @param circuito
     * @return
     * @throws ExcepcionGeneralSistema
     */
    public List<Comprobante> getComprobantesCircuito(CircuitoProduccion circuito) throws ExcepcionGeneralSistema {

        List<Comprobante> listaComprobante = new ArrayList<Comprobante>();

        if (circuito != null) {

            if (circuito.getActualizaProduccion().equals("S")) {
                //Cargamos la lista de comprobantes de produccion disponibles
                for (ItemCircuitoProduccionProduccion i : circuito.getItemCircuitoProduccion()) {
                    //System.out.println("Comprobante produccion: " + i.getComprobante());
                    listaComprobante.add(i.getComprobante());
                }
            }

            if (circuito.getActualizaStock().equals("S")) {
                //Cargamos la lista de comprobantes de stock disponibles
                for (ItemCircuitoProduccionStock i : circuito.getItemCircuitoStock()) {
                    //System.out.println("Comprobante stock: " + i.getComprobante());
                    listaComprobante.add(i.getComprobante());
                }
            }

            if (listaComprobante.isEmpty()) {
                throw new ExcepcionGeneralSistema("No existen comprobantes para el circuito seleccionado");
            }

        } else {
            throw new ExcepcionGeneralSistema("El circuito no puede ser nulo");
        }

        return listaComprobante;
    }

    public CircuitoProduccion iniciarCircuito(
            String circom, String cirapl,
            String modPD, String codPD, String sucPD) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, modPD, codPD, sucPD, null, null, null, null, null, null, null, null, null);
    }

    public CircuitoProduccion iniciarCircuito(
            String circom, String cirapl,
            String modPD, String codPD, String sucPD,
            String modST, String codST, String sucST) throws ExcepcionGeneralSistema {

        return iniciarCircuito(circom, cirapl, modPD, codPD, sucPD, modST, codST, sucST, null, null, null, null, null, null);

    }

    public CircuitoProduccion iniciarCircuito(
            String circom, String cirapl,
            String modPD, String codPD, String sucPD,
            String modST, String codST, String sucST,
            String modPR, String codPR, String sucPR,
            String modVC, String codVC, String sucVC) throws ExcepcionGeneralSistema {

        CircuitoProduccion circuito = null;

        if (circom != null && cirapl != null) {
            circuito = getCircuito(circom, cirapl);
        }

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No se encontró circuito");
        }

        cargarCircuitosRelacionados(circuito);

        if (circuito.getActualizaProduccion().equals("S") && modPD != null && codPD != null) {

            circuito.setComprobanteProduccion(getComprobanteProduccion(circom, cirapl, modPD, codPD));

        } else if (circuito.getActualizaStock().equals("S") && modST != null && codST != null) {

            circuito.setComprobanteStock(getComprobanteStock(circom, cirapl, modST, codST));
        }

        if (circuito.getAutomatizaParteProduccion().equals("S")) {

            circuito.setComprobanteParteProceso(getComprobanteProduccion(circom, cirapl, modPR, codPR));
            circuito.setComprobanteValeConsumo(getComprobanteStock(circom, cirapl, modVC, codVC));
        }

        return circuito;
    }

}
