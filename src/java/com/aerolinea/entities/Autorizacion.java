/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "autorizacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Autorizacion.findAll", query = "SELECT a FROM Autorizacion a")
    , @NamedQuery(name = "Autorizacion.findById", query = "SELECT a FROM Autorizacion a WHERE a.id = :id")
    , @NamedQuery(name = "Autorizacion.findByDescripcion", query = "SELECT a FROM Autorizacion a WHERE a.descripcion = :descripcion")
    , @NamedQuery(name = "Autorizacion.findByNivel", query = "SELECT a FROM Autorizacion a WHERE a.nivel = :nivel")})
public class Autorizacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "nivel")
    private int nivel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "autorizacionid")
    private List<Usuario> usuarioList;

    public Autorizacion() {
    }

    public Autorizacion(Integer id) {
        this.id = id;
    }

    public Autorizacion(Integer id, String descripcion, int nivel) {
        this.id = id;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    @XmlTransient
    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Autorizacion)) {
            return false;
        }
        Autorizacion other = (Autorizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sicom.entities.Autorizacion[ id=" + id + " ]";
    }
    
}
