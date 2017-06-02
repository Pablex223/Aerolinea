/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "tiquete")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiquete.findAll", query = "SELECT t FROM Tiquete t")
    , @NamedQuery(name = "Tiquete.findById", query = "SELECT t FROM Tiquete t WHERE t.id = :id")})
public class Tiquete implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "Movimiento_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Movimiento movimientoid;
    @JoinColumn(name = "Usuario_cedula", referencedColumnName = "cedula")
    @ManyToOne(optional = false)
    private Usuario usuariocedula;
    @JoinColumn(name = "Vuelo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Vuelo vueloid;

    public Tiquete() {
    }

    public Tiquete(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Movimiento getMovimientoid() {
        return movimientoid;
    }

    public void setMovimientoid(Movimiento movimientoid) {
        this.movimientoid = movimientoid;
    }

    public Usuario getUsuariocedula() {
        return usuariocedula;
    }

    public void setUsuariocedula(Usuario usuariocedula) {
        this.usuariocedula = usuariocedula;
    }

    public Vuelo getVueloid() {
        return vueloid;
    }

    public void setVueloid(Vuelo vueloid) {
        this.vueloid = vueloid;
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
        if (!(object instanceof Tiquete)) {
            return false;
        }
        Tiquete other = (Tiquete) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sicom.entities.Tiquete[ id=" + id + " ]";
    }
    
}
