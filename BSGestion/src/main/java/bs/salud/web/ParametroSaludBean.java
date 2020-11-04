package bs.salud.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.salud.modelo.ParametroSalud;
import bs.salud.rn.ParametroSaludRN;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Guillermo
 */
@ManagedBean
@ViewScoped
public class ParametroSaludBean extends GenericBean implements Serializable {

    @EJB
    private ParametroSaludRN parametrosRN;

    private ParametroSalud parametro;
    private boolean seleccionadoRegistroDiaLunes;
    private boolean seleccionadoRegistroMananaLunes;
    private boolean seleccionadoRegistroTardeLunes;
    private boolean seleccionadoRegistroDiaMartes;
    private boolean seleccionadoRegistroMananaMartes;
    private boolean seleccionadoRegistroTardeMartes;
    private boolean seleccionadoRegistroDiaMiercoles;
    private boolean seleccionadoRegistroMananaMiercoles;
    private boolean seleccionadoRegistroTardeMiercoles;
    private boolean seleccionadoRegistroDiaJueves;
    private boolean seleccionadoRegistroMananaJueves;
    private boolean seleccionadoRegistroTardeJueves;
    private boolean seleccionadoRegistroDiaViernes;
    private boolean seleccionadoRegistroMananaViernes;
    private boolean seleccionadoRegistroTardeViernes;
    private boolean seleccionadoRegistroDiaSabado;
    private boolean seleccionadoRegistroMananaSabado;
    private boolean seleccionadoRegistroTardeSabado;
    private boolean seleccionadoRegistroDiaDomingo;
    private boolean seleccionadoRegistroMananaDomingo;
    private boolean seleccionadoRegistroTardeDomingo;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    /**
     * Creates a new instance of ParametrosBean
     */
    public ParametroSaludBean() {

    }

    @PostConstruct
    private void init() {
        try {
            parametro = parametrosRN.getParametro();
//            seleccionadoRegistroDiaLunes = parametro.getRegistroTurnoDiaLunes().equals("S");
//            seleccionadoRegistroMananaLunes = parametro.getRegistroTurnoMananaLunes().equals("S");
//            seleccionadoRegistroTardeLunes = parametro.getRegistroTurnoTardeLunes().equals("S");
//            seleccionadoRegistroDiaMartes = parametro.getRegistroTurnoDiaMartes().equals("S");
//            seleccionadoRegistroMananaMartes = parametro.getRegistroTurnoMananaMartes().equals("S");
//            seleccionadoRegistroTardeMartes = parametro.getRegistroTurnoTardeMartes().equals("S");
//            seleccionadoRegistroDiaMiercoles = parametro.getRegistroTurnoDiaMiercoles().equals("S");
//            seleccionadoRegistroMananaMiercoles = parametro.getRegistroTurnoMananaMiercoles().equals("S");
//            seleccionadoRegistroTardeMiercoles = parametro.getRegistroTurnoTardeMiercoles().equals("S");
//            seleccionadoRegistroDiaJueves = parametro.getRegistroTurnoDiaJueves().equals("S");
//            seleccionadoRegistroMananaJueves = parametro.getRegistroTurnoMananaJueves().equals("S");
//            seleccionadoRegistroTardeJueves = parametro.getRegistroTurnoTardeJueves().equals("S");
//            seleccionadoRegistroDiaViernes = parametro.getRegistroTurnoDiaViernes().equals("S");
//            seleccionadoRegistroMananaViernes = parametro.getRegistroTurnoMananaViernes().equals("S");
//            seleccionadoRegistroTardeViernes = parametro.getRegistroTurnoTardeViernes().equals("S");
//            seleccionadoRegistroDiaSabado = parametro.getRegistroTurnoDiaSabado().equals("S");
//            seleccionadoRegistroMananaSabado = parametro.getRegistroTurnoMananaSabado().equals("S");
//            seleccionadoRegistroTardeSabado = parametro.getRegistroTurnoTardeSabado().equals("S");
//            seleccionadoRegistroDiaDomingo = parametro.getRegistroTurnoDiaDomingo().equals("S");
//            seleccionadoRegistroMananaDomingo = parametro.getRegistroTurnoMananaDomingo().equals("S");
//            seleccionadoRegistroTardeDomingo = parametro.getRegistroTurnoTardeDomingo().equals("S");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardar() {
        try {
//            preSave(parametro);
            parametrosRN.guardar(parametro);
            aplicacionBean.actualizarDatos();
            JsfUtil.addInfoMessage("Lo datos se guardaron correctamente");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

//    public void preSave(ParametroSalud parametro) {
//
//        if (seleccionadoRegistroDiaLunes) {
//            parametro.setRegistroTurnoDiaLunes("S");
//        } else {
//            parametro.setRegistroTurnoDiaLunes("N");
//        }
//
//        if (seleccionadoRegistroMananaLunes) {
//            parametro.setRegistroTurnoMananaLunes("S");
//        } else {
//            parametro.setRegistroTurnoMananaLunes("N");
//        }
//
//        if (seleccionadoRegistroTardeLunes) {
//            parametro.setRegistroTurnoTardeLunes("S");
//        } else {
//            parametro.setRegistroTurnoTardeLunes("N");
//        }
//
//        if (seleccionadoRegistroDiaMartes) {
//            parametro.setRegistroTurnoDiaMartes("S");
//        } else {
//            parametro.setRegistroTurnoDiaMartes("N");
//        }
//
//        if (seleccionadoRegistroMananaMartes) {
//            parametro.setRegistroTurnoMananaMartes("S");
//        } else {
//            parametro.setRegistroTurnoMananaMartes("N");
//        }
//
//        if (seleccionadoRegistroTardeMartes) {
//            parametro.setRegistroTurnoTardeMartes("S");
//        } else {
//            parametro.setRegistroTurnoTardeMartes("N");
//        }
//
//        if (seleccionadoRegistroDiaMiercoles) {
//            parametro.setRegistroTurnoDiaMiercoles("S");
//        } else {
//            parametro.setRegistroTurnoDiaMiercoles("N");
//        }
//
//        if (seleccionadoRegistroMananaMiercoles) {
//            parametro.setRegistroTurnoMananaMiercoles("S");
//        } else {
//            parametro.setRegistroTurnoMananaMiercoles("N");
//        }
//
//        if (seleccionadoRegistroTardeMiercoles) {
//            parametro.setRegistroTurnoTardeMiercoles("S");
//        } else {
//            parametro.setRegistroTurnoTardeMiercoles("N");
//        }
//
//        if (seleccionadoRegistroDiaJueves) {
//            parametro.setRegistroTurnoDiaJueves("S");
//        } else {
//            parametro.setRegistroTurnoDiaJueves("N");
//        }
//
//        if (seleccionadoRegistroMananaJueves) {
//            parametro.setRegistroTurnoMananaJueves("S");
//        } else {
//            parametro.setRegistroTurnoMananaJueves("N");
//        }
//
//        if (seleccionadoRegistroTardeJueves) {
//            parametro.setRegistroTurnoTardeJueves("S");
//        } else {
//            parametro.setRegistroTurnoTardeJueves("N");
//        }
//
//        if (seleccionadoRegistroDiaViernes) {
//            parametro.setRegistroTurnoDiaViernes("S");
//        } else {
//            parametro.setRegistroTurnoDiaViernes("N");
//        }
//
//        if (seleccionadoRegistroMananaViernes) {
//            parametro.setRegistroTurnoMananaViernes("S");
//        } else {
//            parametro.setRegistroTurnoMananaViernes("N");
//        }
//
//        if (seleccionadoRegistroTardeViernes) {
//            parametro.setRegistroTurnoTardeViernes("S");
//        } else {
//            parametro.setRegistroTurnoTardeViernes("N");
//        }
//
//        if (seleccionadoRegistroDiaSabado) {
//            parametro.setRegistroTurnoDiaSabado("S");
//        } else {
//            parametro.setRegistroTurnoDiaSabado("N");
//        }
//
//        if (seleccionadoRegistroMananaSabado) {
//            parametro.setRegistroTurnoMananaSabado("S");
//        } else {
//            parametro.setRegistroTurnoMananaSabado("N");
//        }
//
//        if (seleccionadoRegistroTardeSabado) {
//            parametro.setRegistroTurnoTardeSabado("S");
//        } else {
//            parametro.setRegistroTurnoTardeSabado("N");
//        }
//
//        if (seleccionadoRegistroDiaDomingo) {
//            parametro.setRegistroTurnoDiaDomingo("S");
//        } else {
//            parametro.setRegistroTurnoDiaDomingo("N");
//        }
//
//        if (seleccionadoRegistroMananaDomingo) {
//            parametro.setRegistroTurnoMananaDomingo("S");
//        } else {
//            parametro.setRegistroTurnoMananaDomingo("N");
//        }
//
//        if (seleccionadoRegistroTardeDomingo) {
//            parametro.setRegistroTurnoTardeDomingo("S");
//        } else {
//            parametro.setRegistroTurnoTardeDomingo("N");
//        }
//
//    }
//
//    public void checkLunes() {
//        if (seleccionadoRegistroDiaLunes) {
//            seleccionadoRegistroMananaLunes = true;
//            seleccionadoRegistroTardeLunes = true;
//
//        } else {
//            seleccionadoRegistroMananaLunes = false;
//            seleccionadoRegistroTardeLunes = false;
//        }
//
//    }
//
//    public void checkMartes() {
//        if (seleccionadoRegistroDiaMartes) {
//            seleccionadoRegistroMananaMartes = true;
//            seleccionadoRegistroTardeMartes = true;
//
//        } else {
//            seleccionadoRegistroMananaMartes = false;
//            seleccionadoRegistroTardeMartes = false;
//        }
//
//    }
//
//    public void checkMiercoles() {
//        if (seleccionadoRegistroDiaMiercoles) {
//            seleccionadoRegistroMananaMiercoles = true;
//            seleccionadoRegistroTardeMiercoles = true;
//
//        } else {
//            seleccionadoRegistroMananaMiercoles = false;
//            seleccionadoRegistroTardeMiercoles = false;
//        }
//
//    }
//
//    public void checkJueves() {
//        if (seleccionadoRegistroDiaJueves) {
//            seleccionadoRegistroMananaJueves = true;
//            seleccionadoRegistroTardeJueves = true;
//
//        } else {
//            seleccionadoRegistroMananaJueves = false;
//            seleccionadoRegistroTardeJueves = false;
//        }
//
//    }
//
//    public void checkViernes() {
//        if (seleccionadoRegistroDiaViernes) {
//            seleccionadoRegistroMananaViernes = true;
//            seleccionadoRegistroTardeViernes = true;
//
//        } else {
//            seleccionadoRegistroMananaViernes = false;
//            seleccionadoRegistroTardeViernes = false;
//        }
//
//    }
//
//    public void checkSabado() {
//        if (seleccionadoRegistroDiaSabado) {
//            seleccionadoRegistroMananaSabado = true;
//            seleccionadoRegistroTardeSabado = true;
//
//        } else {
//            seleccionadoRegistroMananaSabado = false;
//            seleccionadoRegistroTardeSabado = false;
//        }
//
//    }
//
//    public void checkDomingo() {
//        if (seleccionadoRegistroDiaDomingo) {
//            seleccionadoRegistroMananaDomingo = true;
//            seleccionadoRegistroTardeDomingo = true;
//
//        } else {
//            seleccionadoRegistroMananaDomingo = false;
//            seleccionadoRegistroTardeDomingo = false;
//        }
//
//    }
    public ParametroSalud getParametro() {
        return parametro;
    }

    public void setParametro(ParametroSalud parametro) {
        this.parametro = parametro;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public boolean isSeleccionadoRegistroDiaLunes() {
        return seleccionadoRegistroDiaLunes;
    }

    public void setSeleccionadoRegistroDiaLunes(boolean seleccionadoRegistroDiaLunes) {
        this.seleccionadoRegistroDiaLunes = seleccionadoRegistroDiaLunes;
    }

    public boolean isSeleccionadoRegistroMananaLunes() {
        return seleccionadoRegistroMananaLunes;
    }

    public void setSeleccionadoRegistroMananaLunes(boolean seleccionadoRegistroMananaLunes) {
        this.seleccionadoRegistroMananaLunes = seleccionadoRegistroMananaLunes;
    }

    public boolean isSeleccionadoRegistroTardeLunes() {
        return seleccionadoRegistroTardeLunes;
    }

    public void setSeleccionadoRegistroTardeLunes(boolean seleccionadoRegistroTardeLunes) {
        this.seleccionadoRegistroTardeLunes = seleccionadoRegistroTardeLunes;
    }

    public boolean isSeleccionadoRegistroDiaMartes() {
        return seleccionadoRegistroDiaMartes;
    }

    public void setSeleccionadoRegistroDiaMartes(boolean seleccionadoRegistroDiaMartes) {
        this.seleccionadoRegistroDiaMartes = seleccionadoRegistroDiaMartes;
    }

    public boolean isSeleccionadoRegistroMananaMartes() {
        return seleccionadoRegistroMananaMartes;
    }

    public void setSeleccionadoRegistroMananaMartes(boolean seleccionadoRegistroMananaMartes) {
        this.seleccionadoRegistroMananaMartes = seleccionadoRegistroMananaMartes;
    }

    public boolean isSeleccionadoRegistroTardeMartes() {
        return seleccionadoRegistroTardeMartes;
    }

    public void setSeleccionadoRegistroTardeMartes(boolean seleccionadoRegistroTardeMartes) {
        this.seleccionadoRegistroTardeMartes = seleccionadoRegistroTardeMartes;
    }

    public boolean isSeleccionadoRegistroDiaMiercoles() {
        return seleccionadoRegistroDiaMiercoles;
    }

    public void setSeleccionadoRegistroDiaMiercoles(boolean seleccionadoRegistroDiaMiercoles) {
        this.seleccionadoRegistroDiaMiercoles = seleccionadoRegistroDiaMiercoles;
    }

    public boolean isSeleccionadoRegistroMananaMiercoles() {
        return seleccionadoRegistroMananaMiercoles;
    }

    public void setSeleccionadoRegistroMananaMiercoles(boolean seleccionadoRegistroMananaMiercoles) {
        this.seleccionadoRegistroMananaMiercoles = seleccionadoRegistroMananaMiercoles;
    }

    public boolean isSeleccionadoRegistroTardeMiercoles() {
        return seleccionadoRegistroTardeMiercoles;
    }

    public void setSeleccionadoRegistroTardeMiercoles(boolean seleccionadoRegistroTardeMiercoles) {
        this.seleccionadoRegistroTardeMiercoles = seleccionadoRegistroTardeMiercoles;
    }

    public boolean isSeleccionadoRegistroDiaJueves() {
        return seleccionadoRegistroDiaJueves;
    }

    public void setSeleccionadoRegistroDiaJueves(boolean seleccionadoRegistroDiaJueves) {
        this.seleccionadoRegistroDiaJueves = seleccionadoRegistroDiaJueves;
    }

    public boolean isSeleccionadoRegistroMananaJueves() {
        return seleccionadoRegistroMananaJueves;
    }

    public void setSeleccionadoRegistroMananaJueves(boolean seleccionadoRegistroMananaJueves) {
        this.seleccionadoRegistroMananaJueves = seleccionadoRegistroMananaJueves;
    }

    public boolean isSeleccionadoRegistroTardeJueves() {
        return seleccionadoRegistroTardeJueves;
    }

    public void setSeleccionadoRegistroTardeJueves(boolean seleccionadoRegistroTardeJueves) {
        this.seleccionadoRegistroTardeJueves = seleccionadoRegistroTardeJueves;
    }

    public boolean isSeleccionadoRegistroDiaViernes() {
        return seleccionadoRegistroDiaViernes;
    }

    public void setSeleccionadoRegistroDiaViernes(boolean seleccionadoRegistroDiaViernes) {
        this.seleccionadoRegistroDiaViernes = seleccionadoRegistroDiaViernes;
    }

    public boolean isSeleccionadoRegistroMananaViernes() {
        return seleccionadoRegistroMananaViernes;
    }

    public void setSeleccionadoRegistroMananaViernes(boolean seleccionadoRegistroMananaViernes) {
        this.seleccionadoRegistroMananaViernes = seleccionadoRegistroMananaViernes;
    }

    public boolean isSeleccionadoRegistroTardeViernes() {
        return seleccionadoRegistroTardeViernes;
    }

    public void setSeleccionadoRegistroTardeViernes(boolean seleccionadoRegistroTardeViernes) {
        this.seleccionadoRegistroTardeViernes = seleccionadoRegistroTardeViernes;
    }

    public boolean isSeleccionadoRegistroDiaSabado() {
        return seleccionadoRegistroDiaSabado;
    }

    public void setSeleccionadoRegistroDiaSabado(boolean seleccionadoRegistroDiaSabado) {
        this.seleccionadoRegistroDiaSabado = seleccionadoRegistroDiaSabado;
    }

    public boolean isSeleccionadoRegistroMananaSabado() {
        return seleccionadoRegistroMananaSabado;
    }

    public void setSeleccionadoRegistroMananaSabado(boolean seleccionadoRegistroMananaSabado) {
        this.seleccionadoRegistroMananaSabado = seleccionadoRegistroMananaSabado;
    }

    public boolean isSeleccionadoRegistroTardeSabado() {
        return seleccionadoRegistroTardeSabado;
    }

    public void setSeleccionadoRegistroTardeSabado(boolean seleccionadoRegistroTardeSabado) {
        this.seleccionadoRegistroTardeSabado = seleccionadoRegistroTardeSabado;
    }

    public boolean isSeleccionadoRegistroDiaDomingo() {
        return seleccionadoRegistroDiaDomingo;
    }

    public void setSeleccionadoRegistroDiaDomingo(boolean seleccionadoRegistroDiaDomingo) {
        this.seleccionadoRegistroDiaDomingo = seleccionadoRegistroDiaDomingo;
    }

    public boolean isSeleccionadoRegistroMananaDomingo() {
        return seleccionadoRegistroMananaDomingo;
    }

    public void setSeleccionadoRegistroMananaDomingo(boolean seleccionadoRegistroMananaDomingo) {
        this.seleccionadoRegistroMananaDomingo = seleccionadoRegistroMananaDomingo;
    }

    public boolean isSeleccionadoRegistroTardeDomingo() {
        return seleccionadoRegistroTardeDomingo;
    }

    public void setSeleccionadoRegistroTardeDomingo(boolean seleccionadoRegistroTardeDomingo) {
        this.seleccionadoRegistroTardeDomingo = seleccionadoRegistroTardeDomingo;
    }

}
