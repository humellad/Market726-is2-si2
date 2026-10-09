package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Queja;
import domain.Sale;
import domain.Seller;

public class ResolveReportMockWhiteTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private Integer saleNumber;
    private Queja salaketa;
    private Queja quejaInDb;
    private Sale sale;
    private Seller owner;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);

        saleNumber = 1;
        
        salaketa = mock(Queja.class);
        when(salaketa.getId()).thenReturn(10);

        quejaInDb = mock(Queja.class);
        when(quejaInDb.getId()).thenReturn(10);

        owner = mock(Seller.class);
        sale = mock(Sale.class);

        ArrayList<Queja> quejasList = new ArrayList<>();
        quejasList.add(quejaInDb);
        when(sale.getSalaketak()).thenReturn(quejasList);
    }

    @After
    public void tearDown() {
        if (persistenceMock != null) {
            persistenceMock.close();
        }
    }

    @Test
    // CP1: Excepción en DB -> Capturada en catch, ejecuta rollback y devuelve false
    public void test1() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(quejaInDb);
        doThrow(new RuntimeException("Error en persistencia")).when(db).merge(quejaInDb);

        boolean res = sut.resolveReport(saleNumber, salaketa, false);

        assertFalse(res);
        verify(et).rollback();
    }
    
    @Test
    // CP2: sale == null -> Retorna false y ejecuta rollback
    public void test2() {
        when(db.find(Sale.class, saleNumber)).thenReturn(null);

        boolean res = sut.resolveReport(saleNumber, salaketa, true);

        assertFalse(res);
        verify(et).rollback();
    }

    @Test
    // CP3: salaketa == null -> Retorna false y ejecuta rollback
    public void test3() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);

        boolean res = sut.resolveReport(saleNumber, null, true);

        assertFalse(res);
        verify(et).rollback();
    }

    @Test
    // CP4: s != null, aceptar = true, owner != null -> Elimina sale, actualiza owner y queja
    public void test4() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(quejaInDb);
        when(sale.getSeller()).thenReturn(owner);

        boolean res = sut.resolveReport(saleNumber, salaketa, true);

        assertTrue(res);
        verify(quejaInDb).setTratatuta(true);
        verify(db).merge(quejaInDb);
        verify(owner).removeSale(sale);
        verify(db).merge(owner);
        verify(db).remove(sale);
        verify(et).commit();
    }

    @Test
    // CP5: s != null, aceptar = true, owner == null -> Elimina sale y actualiza queja (sin owner)
    public void test5() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(quejaInDb);
        when(sale.getSeller()).thenReturn(null);

        boolean res = sut.resolveReport(saleNumber, salaketa, true);

        assertTrue(res);
        verify(quejaInDb).setTratatuta(true);
        verify(db).merge(quejaInDb);
        verify(db).remove(sale);
        verify(et).commit();
    }
    
    @Test
    // CP6: s != null, aceptar = false -> Mantiene sale, actualiza sale y queja
    public void test6() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(quejaInDb);

        boolean res = sut.resolveReport(saleNumber, salaketa, false);

        assertTrue(res);
        verify(quejaInDb).setTratatuta(true);
        verify(db).merge(quejaInDb);
        verify(db).merge(sale);
        verify(et).commit();
    }

    @Test
    // CP7: s == null, aceptar = true, owner != null -> Elimina sale y actualiza owner (sin merge a queja)
    public void test7() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(null);
        when(sale.getSeller()).thenReturn(owner);

        boolean res = sut.resolveReport(saleNumber, salaketa, true);

        assertTrue(res);
        verify(owner).removeSale(sale);
        verify(db).merge(owner);
        verify(db).remove(sale);
        verify(et).commit();
    }
/*
    @Test
    // CP8: s == null, aceptar = true, owner == null -> Solo elimina sale de BD
    public void test8() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(null);
        when(sale.getSeller()).thenReturn(null);

        boolean res = sut.resolveReport(saleNumber, salaketa, true);

        assertTrue(res);
        verify(db).remove(sale);
        verify(et).commit();
    }

    @Test
    // CP9: s == null, aceptar = false -> Mantiene sale y ejecuta merge(sale) únicamente
    public void test9() {
        when(db.find(Sale.class, saleNumber)).thenReturn(sale);
        when(db.find(Queja.class, salaketa.getId())).thenReturn(null);

        boolean res = sut.resolveReport(saleNumber, salaketa, false);

        assertTrue(res);
        verify(db).merge(sale);
        verify(et).commit();
    }
    */
}