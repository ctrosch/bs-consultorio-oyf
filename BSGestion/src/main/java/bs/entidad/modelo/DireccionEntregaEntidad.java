/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Localidad;
import bs.global.modelo.Provincia;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "en_direccion_entrega")
@IdClass(DireccionEntregaEntidadPK.class)
@EntityListeners(AuditoriaListener.class)
public class DireccionEntregaEntidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Id
    @Column(name = "NROCTA", nullable = false, length = 13)
    private String nrocta;

    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial entidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false)
    private Localidad localidad;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @Column(name = "DIRECC", length = 255)
    private String direccion;
    @Column(name = "NUMERO", length = 20)
    private String numero;
    @Column(name = "DPPISO", length = 6)
    private String departamentoPiso;
    @Column(name = "DEPTOS", length = 6)
    private String departamentoNumero;
    @Column(name = "BARRIO", length = 6)
    private String barrio;
    @Column(name = "DIAENT")
    private Short dias;
    @Column(name = "HORENT", length = 60)
    private String hora;
    @Column(name = "CONTAC", length = 60)
    private String contacto;
    @Column(name = "TELEFN", length = 30)
    private String telefono;

    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @Embedded
    private Auditoria auditoria;

    public DireccionEntregaEntidad() {

        this.auditoria = new Auditoria();
    }

    public DireccionEntregaEntidad(String codigo, String nrocta) {

        this.auditoria = new Auditoria();
        this.codigo = codigo;
        this.nrocta = nrocta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNrocta() {
        return nrocta;
    }

    public void setNrocta(String nrocta) {
        this.nrocta = nrocta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Short getDias() {
        return dias;
    }

    public void setDias(Short dias) {
        this.dias = dias;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDepartamentoPiso() {
        return departamentoPiso;
    }

    public void setDepartamentoPiso(String departamentoPiso) {
        this.departamentoPiso = departamentoPiso;
    }

    public String getDepartamentoNumero() {
        return departamentoNumero;
    }

    public void setDepartamentoNumero(String departamentoNumero) {
        this.departamentoNumero = departamentoNumero;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 13 * hash + (this.nrocta != null ? this.nrocta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DireccionEntregaEntidad other = (DireccionEntregaEntidad) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.nrocta == null) ? (other.nrocta != null) : !this.nrocta.equals(other.nrocta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DireccionesDeEntregaVenta{" + "codigo=" + codigo + ", nrocta=" + nrocta + '}';
    }

}
