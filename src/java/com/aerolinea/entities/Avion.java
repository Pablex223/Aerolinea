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
@Table(name = "avion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Avion.findAll", query = "SELECT a FROM Avion a")
    , @NamedQuery(name = "Avion.findById", query = "SELECT a FROM Avion a WHERE a.id = :id")
    , @NamedQuery(name = "Avion.findByA\u00f1o", query = "SELECT a FROM Avion a WHERE a.a\u00f1o = :a\u00f1o")
    , @NamedQuery(name = "Avion.findByModelo", query = "SELECT a FROM Avion a WHERE a.modelo = :modelo")
    , @NamedQuery(name = "Avion.findByMarca", query = "SELECT a FROM Avion a WHERE a.marca = :marca")
    , @NamedQuery(name = "Avion.findByCapacidad", query = "SELECT a FROM Avion a WHERE a.capacidad = :capacidad")
    , @NamedQuery(name = "Avion.findByFilas", query = "SELECT a FROM Avion a WHERE a.filas = :filas")
    , @NamedQuery(name = "Avion.findByAsiento", query = "SELECT a FROM Avion a WHERE a.asiento = :asiento")})
public class Avion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Basic(optional = false)
    @Column(name = "a\u00f1o")
    private int año;
    @Basic(optional = false)
    @Column(name = "modelo")
    private String modelo;
    @Basic(optional = false)
    @Column(name = "marca")
    private String marca;
    @Basic(optional = false)
    @Column(name = "capacidad")
    private int capacidad;
    @Basic(optional = false)
    @Column(name = "filas")
    private int filas;
    @Basic(optional = false)
    @Column(name = "asiento")
    private int asiento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "avionid")
    private List<Vuelo> vueloList;

    public Avion() {
    }

    public Avion(String id) {
        this.id = id;
    }

    public Avion(String id, int año, String modelo, String marca, int capacidad, int filas, int asiento) {
        this.id = id;
        this.año = año;
        this.modelo = modelo;
        this.marca = marca;
        this.capacidad = capacidad;
        this.filas = filas;
        this.asiento = asiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getAsiento() {
        return asiento;
    }

    public void setAsiento(int asiento) {
        this.asiento = asiento;
    }

    @XmlTransient
    public List<Vuelo> getVueloList() {
        return vueloList;
    }

    public void setVueloList(List<Vuelo> vueloList) {
        this.vueloList = vueloList;
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
        if (!(object instanceof Avion)) {
            return false;
        }
        Avion other = (Avion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sicom.entities.Avion[ id=" + id + " ]";
    }
    
}
