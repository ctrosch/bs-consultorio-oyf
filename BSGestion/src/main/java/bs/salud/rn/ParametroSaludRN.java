/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.rn;

import bs.administracion.rn.ParametrosRN;
import bs.salud.dao.ParametroSaludDAO;
import bs.salud.modelo.ParametroSalud;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Guillermo
 */
@Stateless
public class ParametroSaludRN implements Serializable {

    @EJB
    ParametroSaludDAO parametroDAO;
    @EJB
    EstadoSaludRN estadoRN;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroSalud p) throws Exception {
//        control(p);
        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

//    public void control(ParametroSalud p) throws ExcepcionGeneralSistema {
//
//        String sError = "";
//
//        if (p.getRegistroTurnoDiaLunes().equals("S")) {
//            if (p.getRegistroTurnoMananaLunes().equals("S") && p.getRegistroTurnoTardeLunes().equals("S")) {
//                if (p.getHoraInicioMananaLunes().after(p.getHoraFinTardeLunes())) {
//                    sError += "Lunes : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaLunes().after(p.getHoraInicioTardeLunes())) {
//                    sError += "Lunes : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaLunes().equals("S")) {
//                if (p.getHoraInicioMananaLunes().after(p.getHoraFinMananaLunes()) || p.getHoraInicioMananaLunes().equals(p.getHoraFinMananaLunes())) {
//                    sError += "Lunes : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeLunes().equals("S")) {
//                if (p.getHoraInicioTardeLunes().after(p.getHoraFinTardeLunes()) || p.getHoraInicioTardeLunes().equals(p.getHoraFinTardeLunes())) {
//                    sError += "Lunes : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaMartes().equals("S")) {
//            if (p.getRegistroTurnoMananaMartes().equals("S") && p.getRegistroTurnoTardeMartes().equals("S")) {
//                if (p.getHoraInicioMananaMartes().after(p.getHoraFinTardeMartes())) {
//                    sError += "Martes : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaMartes().after(p.getHoraInicioTardeMartes())) {
//                    sError += "Martes : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaMartes().equals("S")) {
//                if (p.getHoraInicioMananaMartes().after(p.getHoraFinMananaMartes()) || p.getHoraInicioMananaMartes().equals(p.getHoraFinMananaMartes())) {
//                    sError += "Martes : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeMartes().equals("S")) {
//                if (p.getHoraInicioTardeMartes().after(p.getHoraFinTardeMartes()) || p.getHoraInicioTardeMartes().equals(p.getHoraFinTardeMartes())) {
//                    sError += "Martes : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaMiercoles().equals("S")) {
//            if (p.getRegistroTurnoMananaMiercoles().equals("S") && p.getRegistroTurnoTardeMiercoles().equals("S")) {
//                if (p.getHoraInicioMananaMiercoles().after(p.getHoraFinTardeMiercoles())) {
//                    sError += "Miércoles : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaMiercoles().after(p.getHoraInicioTardeMiercoles())) {
//                    sError += "Miércoles : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaMiercoles().equals("S")) {
//                if (p.getHoraInicioMananaMiercoles().after(p.getHoraFinMananaMiercoles()) || p.getHoraInicioMananaMiercoles().equals(p.getHoraFinMananaMiercoles())) {
//                    sError += "Miércoles : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeMiercoles().equals("S")) {
//                if (p.getHoraInicioTardeMiercoles().after(p.getHoraFinTardeMiercoles()) || p.getHoraInicioTardeMiercoles().equals(p.getHoraFinTardeMiercoles())) {
//                    sError += "Miércoles : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaJueves().equals("S")) {
//            if (p.getRegistroTurnoMananaJueves().equals("S") && p.getRegistroTurnoTardeJueves().equals("S")) {
//                if (p.getHoraInicioMananaJueves().after(p.getHoraFinTardeJueves())) {
//                    sError += "Jueves : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaJueves().after(p.getHoraInicioTardeJueves())) {
//                    sError += "Jueves : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaJueves().equals("S")) {
//                if (p.getHoraInicioMananaJueves().after(p.getHoraFinMananaJueves()) || p.getHoraInicioMananaJueves().equals(p.getHoraFinMananaJueves())) {
//                    sError += "Jueves : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeJueves().equals("S")) {
//                if (p.getHoraInicioTardeJueves().after(p.getHoraFinTardeJueves()) || p.getHoraInicioTardeJueves().equals(p.getHoraFinTardeJueves())) {
//                    sError += "Jueves : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaViernes().equals("S")) {
//            if (p.getRegistroTurnoMananaViernes().equals("S") && p.getRegistroTurnoTardeViernes().equals("S")) {
//                if (p.getHoraInicioMananaViernes().after(p.getHoraFinTardeViernes())) {
//                    sError += "Viernes : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaViernes().after(p.getHoraInicioTardeViernes())) {
//                    sError += "Viernes : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaViernes().equals("S")) {
//                if (p.getHoraInicioMananaViernes().after(p.getHoraFinMananaViernes()) || p.getHoraInicioMananaViernes().equals(p.getHoraFinMananaViernes())) {
//                    sError += "Viernes : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeViernes().equals("S")) {
//                if (p.getHoraInicioTardeViernes().after(p.getHoraFinTardeViernes()) || p.getHoraInicioTardeViernes().equals(p.getHoraFinTardeViernes())) {
//                    sError += "Viernes : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaSabado().equals("S")) {
//            if (p.getRegistroTurnoMananaSabado().equals("S") && p.getRegistroTurnoTardeSabado().equals("S")) {
//                if (p.getHoraInicioMananaSabado().after(p.getHoraFinTardeSabado())) {
//                    sError += "Sábado : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaSabado().after(p.getHoraInicioTardeSabado())) {
//                    sError += "Sábado : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaSabado().equals("S")) {
//                if (p.getHoraInicioMananaSabado().after(p.getHoraFinMananaSabado()) || p.getHoraInicioMananaSabado().equals(p.getHoraFinMananaSabado())) {
//                    sError += "Sábado : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeSabado().equals("S")) {
//                if (p.getHoraInicioTardeSabado().after(p.getHoraFinTardeSabado()) || p.getHoraInicioTardeSabado().equals(p.getHoraFinTardeSabado())) {
//                    sError += "Sábado : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (p.getRegistroTurnoDiaDomingo().equals("S")) {
//            if (p.getRegistroTurnoMananaDomingo().equals("S") && p.getRegistroTurnoTardeDomingo().equals("S")) {
//                if (p.getHoraInicioMananaDomingo().after(p.getHoraFinTardeDomingo())) {
//                    sError += "Domingo : La Hora Fin de la Tarde es menor a la Hora Inicio de la Mañana | ";
//                }
//                if (p.getHoraFinMananaDomingo().after(p.getHoraInicioTardeDomingo())) {
//                    sError += "Domingo : La Hora Inicio de la Tarde es menor a la Hora Fin de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoMananaDomingo().equals("S")) {
//                if (p.getHoraInicioMananaDomingo().after(p.getHoraFinMananaDomingo()) || p.getHoraInicioMananaDomingo().equals(p.getHoraFinMananaDomingo())) {
//                    sError += "Domingo : La Hora Fin de la Mañana es menor o igual a la Hora Inicio de la Mañana | ";
//                }
//            }
//            if (p.getRegistroTurnoTardeDomingo().equals("S")) {
//                if (p.getHoraInicioTardeDomingo().after(p.getHoraFinTardeDomingo()) || p.getHoraInicioTardeDomingo().equals(p.getHoraFinTardeDomingo())) {
//                    sError += "Domingo : La Hora Fin de la Tarde es menor o igual a la Hora Inicio de la Tarde | ";
//                }
//            }
//        }
//
//        if (!sError.isEmpty()) {
//            throw new ExcepcionGeneralSistema(sError);
//        }
//
//    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroSalud getParametro() {

        ParametroSalud p = parametroDAO.getParametros();
        return p;
    }
}
