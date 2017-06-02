/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "flota")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Flota.findAll", query = "SELECT f FROM Flota f")
    , @NamedQuery(name = "Flota.findById", query = "SELECT f FROM Flota f WHERE f.id = :id")
    , @NamedQuery(name = "Flota.findByHorario", query = "SELECT f FROM Flota f WHERE f.horario = :horario")
    , @NamedQuery(name = "Flota.findByNombreAvion", query = "SELECT f FROM Flota f WHERE f.nombreAvion = :nombreAvion")})
public class Flota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Column(name = "horario")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horario;
    @Column(name = "nombreAvion")
    private String nombreAvion;

    public Flota() {
    }

    public Flota(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public String getNombreAvion() {
        return nombreAvion;
    }

    public void setNombreAvion(String nombreAvion) {
        this.nombreAvion = nombreAvion;
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
        if (!(object instanceof Flota)) {
            return false;
        }
        Flota other = (Flota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sicom.entities.Flota[ id=" + id + " ]";
    }
    
}
