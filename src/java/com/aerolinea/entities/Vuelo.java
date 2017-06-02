/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "vuelo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vuelo.findAll", query = "SELECT v FROM Vuelo v")
    , @NamedQuery(name = "Vuelo.findById", query = "SELECT v FROM Vuelo v WHERE v.id = :id")
    , @NamedQuery(name = "Vuelo.findByOrigen", query = "SELECT v FROM Vuelo v WHERE v.origen = :origen")
    , @NamedQuery(name = "Vuelo.findByDestino", query = "SELECT v FROM Vuelo v WHERE v.destino = :destino")
    , @NamedQuery(name = "Vuelo.findBySalida", query = "SELECT v FROM Vuelo v WHERE v.salida = :salida")
    , @NamedQuery(name = "Vuelo.findByLlegada", query = "SELECT v FROM Vuelo v WHERE v.llegada = :llegada")
    , @NamedQuery(name = "Vuelo.findByRegreso", query = "SELECT v FROM Vuelo v WHERE v.regreso = :regreso")
    , @NamedQuery(name = "Vuelo.findByPasajeros", query = "SELECT v FROM Vuelo v WHERE v.pasajeros = :pasajeros")})
public class Vuelo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Column(name = "origen")
    private String origen;
    @Column(name = "destino")
    private String destino;
    @Column(name = "salida")
    @Temporal(TemporalType.TIMESTAMP)
    private Date salida;
    @Column(name = "llegada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date llegada;
    @Column(name = "regreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regreso;
    @Column(name = "pasajeros")
    private Integer pasajeros;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vueloid")
    private List<Tiquete> tiqueteList;
    @JoinColumn(name = "Avion_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Avion avionid;
    @JoinColumn(name = "Ruta_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Ruta rutaid;

    public Vuelo() {
    }

    public Vuelo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Date getSalida() {
        return salida;
    }

    public void setSalida(Date salida) {
        this.salida = salida;
    }

    public Date getLlegada() {
        return llegada;
    }

    public void setLlegada(Date llegada) {
        this.llegada = llegada;
    }

    public Date getRegreso() {
        return regreso;
    }

    public void setRegreso(Date regreso) {
        this.regreso = regreso;
    }

    public Integer getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(Integer pasajeros) {
        this.pasajeros = pasajeros;
    }

    @XmlTransient
    public List<Tiquete> getTiqueteList() {
        return tiqueteList;
    }

    public void setTiqueteList(List<Tiquete> tiqueteList) {
        this.tiqueteList = tiqueteList;
    }

    public Avion getAvionid() {
        return avionid;
    }

    public void setAvionid(Avion avionid) {
        this.avionid = avionid;
    }

    public Ruta getRutaid() {
        return rutaid;
    }

    public void setRutaid(Ruta rutaid) {
        this.rutaid = rutaid;
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
        if (!(object instanceof Vuelo)) {
            return false;
        }
        Vuelo other = (Vuelo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sicom.entities.Vuelo[ id=" + id + " ]";
    }
    
}
