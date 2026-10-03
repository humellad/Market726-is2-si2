package domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
@Entity
public class Oferta implements Serializable {
    @Id 
    @GeneratedValue
    private Integer id;
    private float price;
    private String message;

    @ManyToOne 
    private Seller seller;

    @ManyToOne 
    private Pedido eskaera;

    public Oferta(float price, String message, Seller seller, Pedido eskaera) {
        this.price = price;
        this.message = message;
        this.seller = seller;
        this.eskaera = eskaera;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    @XmlTransient
    public Pedido getEskaera() { 
        return eskaera; 
    }
    public void setEskaera(Pedido eskaera) { this.eskaera = eskaera; }

    public Oferta() {
    }
    public void setId(Integer id) {
        this.id = id;
    }
}