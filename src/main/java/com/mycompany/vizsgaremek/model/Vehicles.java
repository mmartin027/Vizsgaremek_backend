/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hokal
 */
@Entity
@Table(name = "vehicles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vehicles.findAll", query = "SELECT v FROM Vehicles v"),
    @NamedQuery(name = "Vehicles.findById", query = "SELECT v FROM Vehicles v WHERE v.id = :id"),
    @NamedQuery(name = "Vehicles.findByLicensePlate", query = "SELECT v FROM Vehicles v WHERE v.licensePlate = :licensePlate"),
    @NamedQuery(name = "Vehicles.findByBrand", query = "SELECT v FROM Vehicles v WHERE v.brand = :brand"),
    @NamedQuery(name = "Vehicles.findByModel", query = "SELECT v FROM Vehicles v WHERE v.model = :model"),
    @NamedQuery(name = "Vehicles.findByColor", query = "SELECT v FROM Vehicles v WHERE v.color = :color"),
    @NamedQuery(name = "Vehicles.findByYear", query = "SELECT v FROM Vehicles v WHERE v.year = :year"),
    @NamedQuery(name = "Vehicles.findByIsDefault", query = "SELECT v FROM Vehicles v WHERE v.isDefault = :isDefault"),
    @NamedQuery(name = "Vehicles.findByCreatedAt", query = "SELECT v FROM Vehicles v WHERE v.createdAt = :createdAt"),
    @NamedQuery(name = "Vehicles.findByUpdatedAt", query = "SELECT v FROM Vehicles v WHERE v.updatedAt = :updatedAt")})
public class Vehicles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 20)
    @Column(name = "license_plate")
    private String licensePlate;
    @Size(max = 255)
    @Column(name = "brand")
    private String brand;
    @Size(max = 255)
    @Column(name = "model")
    private String model;
    @Size(max = 50)
    @Column(name = "color")
    private String color;
    @Column(name = "year")
    private Integer year;
    @Column(name = "is_default")
    private Boolean isDefault;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    public Vehicles() {
    }

    public Vehicles(Integer id) {
        this.id = id;
    }

    public Vehicles(Integer id, Date createdAt, Date updatedAt) {
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof Vehicles)) {
            return false;
        }
        Vehicles other = (Vehicles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Vehicles[ id=" + id + " ]";
    }
    
}
