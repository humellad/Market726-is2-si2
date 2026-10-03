package domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Movimientos implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    private String mota; 
    private Date data;
    
    @ManyToOne
    private Seller seller;
    
    @ManyToOne
    private Sale sale;
    
    @ManyToOne
    private Pedido eskaera;
    
    @ManyToOne
    private Oferta eskaintza;

 
    public Movimientos() {
        super();
    }

  
    public Movimientos(String mota, Date data, Seller seller) {
        this.mota = mota;
        this.data = data;
        this.seller = seller;
    }

   
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMota() { return mota; }
    public void setMota(String mota) { this.mota = mota; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public Pedido getEskaera() { return eskaera; }
    public void setEskaera(Pedido eskaera) { this.eskaera = eskaera; }

    public Oferta getEskaintza() { return eskaintza; }
    public void setEskaintza(Oferta eskaintza) { this.eskaintza = eskaintza; }
}