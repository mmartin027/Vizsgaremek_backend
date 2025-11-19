/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hokal
 */
@Entity
@Table(name = "features")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Features.findAll", query = "SELECT f FROM Features f"),
    @NamedQuery(name = "Features.findById", query = "SELECT f FROM Features f WHERE f.id = :id"),
    @NamedQuery(name = "Features.findByName", query = "SELECT f FROM Features f WHERE f.name = :name")})
public class Features implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "featuresCollection")
    private Collection<ParkingSpots> parkingSpotsCollection;

    public Features() {
    }

    public Features(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Collection<ParkingSpots> getParkingSpotsCollection() {
        return parkingSpotsCollection;
    }

    public void setParkingSpotsCollection(Collection<ParkingSpots> parkingSpotsCollection) {
        this.parkingSpotsCollection = parkingSpotsCollection;
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
        if (!(object instanceof Features)) {
            return false;
        }
        Features other = (Features) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Features[ id=" + id + " ]";
    }
    
}
