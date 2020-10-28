/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "gr_empresa")
@EntityListeners(AuditoriaListener.class)
public class Empresa implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", length = 6)
    private String codigo;
    @Column(name = "NOMBRE_FANTASIA", length = 80)
    private String nombreFantasia;
    @Column(name = "RAZON_SOCIAL", length = 80)
    private String razonSocial;
    @Column(name = "DIRECCION", length = 80)
    private String direccion;
    @Column(name = "CIUDAD", length = 80)
    private String ciudad;
    @Column(name = "PROVINCIA", length = 80)
    private String provincia;
    @Column(name = "PAIS", length = 80)
    private String pais;

    @Column(name = "CUIT", length = 13)
    private String cuit;
    @Column(name = "LOGO", length = 80)
    private String logo;
    @Column(name = "CONDICION_IVA", length = 80)
    private String condicionIVA;
    @Column(name = "NRO_IB", length = 80)
    private String nroIB;
    @Column(name = "NRO_AG", length = 80)
    private String nroAG;
    @Column(name = "IMP_MUNIC", length = 80)
    private String impuestoMunicipal;
    @Column(name = "INICIO_ACTIVIDADES", length = 80)
    private String inicioActividades;
    @Column(name = "EMAIL", length = 80)
    private String email;
    @Column(name = "TELEFONO", length = 80)
    private String telefono;
    @Column(name = "FAX", length = 80)
    private String fax;

    @Column(name = "NOMBCO", length = 100)
    private String bancoEmisor;
    @Column(name = "CBUBCO", length = 30)
    private String cbu;
    @Column(name = "ALIBCO", length = 60)
    private String alias;

    @JoinColumn(name = "CODORG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organizacion organizacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<UnidadNegocio> unidadesNegocios;

    @OrderBy("codigo")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Sucursal> sucursales;

    @Embedded
    private Auditoria auditoria;

    public Empresa() {

        this.auditoria = new Auditoria();
    }

    public Empresa(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getImpuestoMunicipal() {
        return impuestoMunicipal;
    }

    public void setImpuestoMunicipal(String impuestoMunicipal) {
        this.impuestoMunicipal = impuestoMunicipal;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCondicionIVA() {
        return condicionIVA;
    }

    public void setCondicionIVA(String condicionIVA) {
        this.condicionIVA = condicionIVA;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNroIB() {
        return nroIB;
    }

    public void setNroIB(String nroIB) {
        this.nroIB = nroIB;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getBancoEmisor() {
        return bancoEmisor;
    }

    public void setBancoEmisor(String bancoEmisor) {
        this.bancoEmisor = bancoEmisor;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getInicioActividades() {
        return inicioActividades;
    }

    public void setInicioActividades(String inicioActividades) {
        this.inicioActividades = inicioActividades;
    }

    public String getNroAG() {
        return nroAG;
    }

    public void setNroAG(String nroAG) {
        this.nroAG = nroAG;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public List<UnidadNegocio> getUnidadesNegocios() {
        return unidadesNegocios;
    }

    public void setUnidadesNegocios(List<UnidadNegocio> unidadesNegocios) {
        this.unidadesNegocios = unidadesNegocios;
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public Empresa clone() throws CloneNotSupportedException {
        return (Empresa) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Empresa other = (Empresa) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Empresa{" + "codigo=" + codigo + "nombreFantasia=" + nombreFantasia + '}';
    }

}
