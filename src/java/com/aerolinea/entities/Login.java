/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "login")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l")
    , @NamedQuery(name = "Login.findByUsuario", query = "SELECT l FROM Login l WHERE l.usuario = :usuario")
    , @NamedQuery(name = "Login.findByClave", query = "SELECT l FROM Login l WHERE l.clave = :clave")
    , @NamedQuery(name = "Login.findByUltimoAcceso", query = "SELECT l FROM Login l WHERE l.ultimoAcceso = :ultimoAcceso")})
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "usuario")
    private String usuario;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;
    @Column(name = "ultimoAcceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoAcceso;
    
     @Transient
     private Boolean autenticado;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "loginUsuario")
    private Usuario user;

    public Login() {
    }

    public Login(String usuario) {
        this.usuario = usuario;
    }

    public Login(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    
      public Usuario getUser() {
        return user;
    }

    public void setUsuario(Usuario user) {
        this.user = user;
    }
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuario != null ? usuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Login)) {
            return false;
        }
        Login other = (Login) object;
        if ((this.usuario == null && other.usuario != null) || (this.usuario != null && !this.usuario.equals(other.usuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.aerolinea.entities.Login[ usuario=" + usuario + " ]";
    }
    
    /**
     * @return the autenticado
     */
    public Boolean getAutenticado() {
        return autenticado;
    }

    /**
     * @param autenticado the autenticado to set
     */
    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }
    
    

}
