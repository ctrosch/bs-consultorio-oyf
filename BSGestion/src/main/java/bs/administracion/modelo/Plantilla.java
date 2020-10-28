/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Guillermo vallejos"
 */
@Entity
@Table(name = "ad_plantilla")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class Plantilla implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false)
    private String codigo;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Size(min = 3, max = 3)
    @Column(name = "ORIGEN", nullable = false, length = 3)
    private String origen;

    @Lob
    @Column(name = "DETALL", length = 2147483647)
    private String detalle;

    @JoinColumn(name = "MODULO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modulo modulo;

    @Embedded
    private Auditoria auditoria;

    public Plantilla() {
        this.auditoria = new Auditoria();
        this.origen = "USR";
        this.detalle = "<table width=\"600\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n"
                + " <tbody><tr>\n"
                + "  <td>\n"
                + "  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n"
                + "   <tbody><tr>\n"
                + "    <td width=\"15%\" height=\"60\" align=\"center\" valign=\"middle\" bgcolor=\"#5199C7\">\n"
                + "    <p class=\"MsoNormal\"><img width=\"51\" height=\"50\" id=\"_x0000_i1025\" src=\"http://www.beansoft.com.ar/bs-gestion/icono-sobre.png\" alt=\"Icono-sobre\"></p>\n"
                + "    </td>\n"
                + "    <td width=\"50%\" height=\"60\" valign=\"middle\" bgcolor=\"#5199C7\">\n"
                + "    <p style=\"color:#FFF;\"><font face=\"Arial\" size=\"3\" color=\"#FFF\"><strong><em>sTitulo</em></strong></font></p>\n"
                + "    </td>\n"
                + "    <td width=\"35%\" height=\"60\" valign=\"middle\" bgcolor=\"#5199C7\">\n"
                + "    <p class=\"MsoNormal\" align=\"center\" style=\"text-align:center\"><img width=\"150\" height=\"30\" src=\"http://www.beansoft.com.ar/bs-gestion/logo-b.png\" alt=\"logo-emp\"></p>\n"
                + "    </td>\n"
                + "   </tr>\n"
                + "  </tbody></table>\n"
                + "  </td>\n"
                + " </tr>\n"
                + "\n"
                + " <tr>\n"
                + "  <td><br>\n"
                + "  <p align=\"center\" style=\"text-align:left; padding:20px;\">\n"
                + "  <font face=\"Arial\" size=\"2\">\n"
                + "         sContenido\n"
                + "   </font>\n"
                + "   </p>\n"
                + "\n"
                + "  </td>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "  <td align=\"center\" style=\"padding:0cm 0cm 0cm 0cm\">\n"
                + "  <p><br></p>\n"
                + "  <p><font face=\"Arial\" size=\"2\">Este es\n"
                + "  un correo generado automáticamente por BSGestión, por cualquier\n"
                + "  consulta dirigirse a <a style=\"text-decoration:none;\" href=\"mailto:ctrosch@beansoft.com.ar\">ctrosch@beansoft.com.ar</a></font></p>\n"
                + "  <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n"
                + "   <tbody>\n"
                + "  \n"
                + "  \n"
                + "   <tr>\n"
                + "    <td height=\"60\" align=\"center\" bgcolor=\"#5199C7\">\n"
                + "    <p><font face=\"Verdana, Geneva, sans-serif\" size=\"2\" color=\"#FFFFFF\"><strong>2015 BeanSoft</strong>\n"
                + "    <br> Software factory - Hosting &amp; dominios </font><font color=\"#FFFFFF\" face=\"Verdana, Geneva, sans-serif\" size=\"2\">\n"
                + "      <br><a style=\"text-decoration:none;\" href=\"http://www.beansoft.com.ar\">www.beansoft.com.ar</a></font><br>\n"
                + "      <font face=\"Verdana, Geneva, sans-serif\" size=\"2\" color=\"#FFFFFF\">Cel.: +54 3482 633426 <br></font></p></td>\n"
                + "   </tr>\n"
                + "  </tbody></table>\n"
                + "  </td>\n"
                + " </tr>\n"
                + "</tbody></table>";
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (nombre != null ? nombre : "");
    }

    @Override
    public Plantilla clone() throws CloneNotSupportedException {
        return (Plantilla) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.codigo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Plantilla other = (Plantilla) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.administracion.modelo.Plantilla[ codigo=" + codigo + " ]";
    }

}
