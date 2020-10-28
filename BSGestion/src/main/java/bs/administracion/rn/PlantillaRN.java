/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.PlantillaDAO;
import bs.administracion.modelo.Parametro;
import bs.administracion.modelo.Plantilla;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Guillermo Vallejos
 */
@Stateless
public class PlantillaRN implements Serializable {

    @EJB
    private PlantillaDAO plantillaDAO;
    @EJB
    private ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Plantilla guardar(Plantilla p, boolean esNuevo) throws Exception {

        if (getPlantilla(p.getCodigo()) == null) {
            plantillaDAO.crear(p);
        } else {
            p = (Plantilla) plantillaDAO.editar(p);
        }

        return p;
    }

    public void control(Plantilla plantilla) throws ExcepcionGeneralSistema {
        String sErrores = "";

        Parametro parametro = parametrosRN.getParametro("01");

        if (plantilla.getOrigen().equals("SIS") && !parametro.getOrigenPorDefecto().equals("SIS")) {

            sErrores += "- No es posible modificar una plantilla del sistema, duplicar la plantilla y guardarlo como plantilla usuario. \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Plantilla plantilla) throws Exception {

        plantillaDAO.eliminar(plantilla);
    }

    public Plantilla getPlantilla(String codigo) {

        return plantillaDAO.getObjeto(Plantilla.class, codigo);
    }

    public List<Plantilla> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return plantillaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Plantilla> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return plantillaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 50);
    }

    public List<Plantilla> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMaxima) {

        return plantillaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMaxima);
    }

    public String obtenerSiguienteCogido(String origen) {

        String codigo = plantillaDAO.obtenerSiguienteCodigo(origen);

        if (codigo == null || codigo.isEmpty()) {
            codigo = origen.toUpperCase() + "_00001";
        }

        return codigo;
    }

    public Plantilla duplicar(Plantilla plantilla) throws CloneNotSupportedException {

        Parametro parametro = parametrosRN.getParametro("01");

        plantilla = plantilla.clone();
        plantilla.setNombre(plantilla.getNombre() + " (duplicado)");
        plantilla.setOrigen(parametro.getOrigenPorDefecto());
        String codigo = obtenerSiguienteCogido(plantilla.getOrigen());
        plantilla.setCodigo(codigo);

        return plantilla;

    }

}
