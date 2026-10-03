package test;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import domain.Queja;
import domain.Sale;
import domain.Seller;

public class TestDataAccess {

    protected EntityManager db;
    protected EntityManagerFactory emf;

    public void open() {
        // Lee la misma configuración de BD que usa DataAccess
        ConfigXML c = ConfigXML.getInstance();
        String fileName = c.getDbFilename();
        
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", fileName);
        emf = Persistence.createEntityManagerFactory("objectdb:" + fileName, properties);
        db = emf.createEntityManager();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public Seller createSeller(String email, String name) {
        db.getTransaction().begin();
        Seller seller = new Seller(email, name);
        db.persist(seller);
        db.getTransaction().commit();
        return seller;
    }

    public Sale addSellerWithSale(String email, String name, String title, String description, int status, float price, Date pubDate, File extra) {
        db.getTransaction().begin();
        Seller seller = db.find(Seller.class, email);
        if (seller == null) {
            seller = new Seller(email, name);
            db.persist(seller);
        }
        Sale sale = seller.addSale(title, description, status, price, pubDate, extra);
        db.persist(sale);
        db.getTransaction().commit();
        return sale;
    }

    public Sale createSaleWithoutSeller(String title, String description, int status, float price, Date pubDate) {
        db.getTransaction().begin();
        Sale sale = new Sale(title, description, status, price, pubDate, null, null);
        db.persist(sale);
        db.getTransaction().commit();
        return sale;
    }

    public Queja addQuejaToSale(Integer saleNumber, String quejaText) {
        db.getTransaction().begin();
        Sale sale = db.find(Sale.class, saleNumber);
        Queja queja = new Queja(quejaText, "sellerTestReport@ehu.eus");
        db.persist(queja);

        if (sale != null) {
            if (sale.getSalaketak() == null) {
                sale.setSalaketak(new java.util.ArrayList<>());
            }
            sale.getSalaketak().add(queja);
            db.merge(sale);
        }
        db.getTransaction().commit();
        return queja;
    }
    
    public boolean existSale(String email, String title) {
        try {
            TypedQuery<Sale> query = db.createQuery(
                "SELECT s FROM Sale s WHERE s.seller.email = :email AND s.title = :title", Sale.class);
            query.setParameter("email", email);
            query.setParameter("title", title);
            List<Sale> results = query.getResultList();
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existSaleByNumber(Integer saleNumber) {
        if (saleNumber == null) return false;
        Sale s = db.find(Sale.class, saleNumber);
        return s != null;
    }

    public boolean removeSeller(String email) {
        db.getTransaction().begin();
        Seller seller = db.find(Seller.class, email);
        if (seller != null) {
            for (Sale sale : seller.getSales()) {
                db.remove(sale);
            }
            db.remove(seller);
            db.getTransaction().commit();
            return true;
        }
        db.getTransaction().rollback();
        return false;
    }

    public boolean removeSale(Integer saleNumber) {
        db.getTransaction().begin();
        Sale sale = db.find(Sale.class, saleNumber);
        if (sale != null) {
            db.remove(sale);
            db.getTransaction().commit();
            return true;
        }
        db.getTransaction().rollback();
        return false;
    }
}