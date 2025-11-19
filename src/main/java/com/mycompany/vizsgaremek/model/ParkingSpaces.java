/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hokal
 */
@Entity
@Table(name = "parking_spaces")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParkingSpaces.findAll", query = "SELECT p FROM ParkingSpaces p"),
    @NamedQuery(name = "ParkingSpaces.findById", query = "SELECT p FROM ParkingSpaces p WHERE p.id = :id"),
    @NamedQuery(name = "ParkingSpaces.findByCode", query = "SELECT p FROM ParkingSpaces p WHERE p.code = :code"),
    @NamedQuery(name = "ParkingSpaces.findByIsOccupied", query = "SELECT p FROM ParkingSpaces p WHERE p.isOccupied = :isOccupied"),
    @NamedQuery(name = "ParkingSpaces.findByType", query = "SELECT p FROM ParkingSpaces p WHERE p.type = :type"),
    @NamedQuery(name = "ParkingSpaces.findBySize", query = "SELECT p FROM ParkingSpaces p WHERE p.size = :size"),
    @NamedQuery(name = "ParkingSpaces.findByCreatedAt", query = "SELECT p FROM ParkingSpaces p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "ParkingSpaces.findByUpdatedAt", query = "SELECT p FROM ParkingSpaces p WHERE p.updatedAt = :updatedAt")})
public class ParkingSpaces implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 20)
    @Column(name = "code")
    private String code;
    @Column(name = "is_occupied")
    private Boolean isOccupied;
    @Size(max = 50)
    @Column(name = "type")
    private String type;
    @Size(max = 20)
    @Column(name = "size")
    private String size;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JoinColumn(name = "parking_spot_id", referencedColumnName = "id")
    @ManyToOne
    private ParkingSpots parkingSpotId;
    @OneToMany(mappedBy = "parkingSpaceId")
    private Collection<Bookings> bookingsCollection;

    public ParkingSpaces() {
    }

    public ParkingSpaces(Integer id) {
        this.id = id;
    }

    public ParkingSpaces(Integer id, Date createdAt, Date updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ParkingSpots getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(ParkingSpots parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    @XmlTransient
    public Collection<Bookings> getBookingsCollection() {
        return bookingsCollection;
    }

    public void setBookingsCollection(Collection<Bookings> bookingsCollection) {
        this.bookingsCollection = bookingsCollection;
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
        if (!(object instanceof ParkingSpaces)) {
            return false;
        }
        ParkingSpaces other = (ParkingSpaces) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.ParkingSpaces[ id=" + id + " ]";
    }
    
}
