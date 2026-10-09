package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Queja;
import domain.Sale;

public class ResolveReportBDWhiteTest {
	/*

    static DataAccess sut = new DataAccess();

    static TestDataAccess testDA = new TestDataAccess();

    private String sellerMail;
    private String sellerName;
    private String title;
    private String description;
    private int status;
    private float price;
    private Date pubDate;

    @Before
    public void defaultValues() {
        sellerMail = "sellerTestReport@ehu.eus";
        sellerName = "Seller Test Report";
        title = "Objeto Reportado";
        description = "Descripción de prueba";
        status = 0;
        price = 20;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pubDate = sdf.parse("05/10/2026");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // CP1: Excepción en DB -> Capturada en catch, ejecuta rollback y devuelve false
    public void test1() {
        try {
            sut.open();
            Queja quejaInvalida = new Queja("Queja invalida", null);
            quejaInvalida.setId(-1);

            boolean res = sut.resolveReport(-1, quejaInvalida, true);
            sut.close();

            assertFalse(res);
        } catch (Exception e) {
            e.printStackTrace();
            fail("El metodo resolveReport debe capturar la excepcion internamente y devolver false");
        }
    }

    @Test
    // CP2: sale == null -> Retorna false y ejecuta rollback
    public void test2() {
        Queja salaketa = new Queja("Queja test", null);
        salaketa.setId(100);

        try {
            sut.open();
            boolean res = sut.resolveReport(-1, salaketa, true);
            sut.close();

            assertFalse(res);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    // CP3: salaketa == null -> Retorna false y ejecuta rollback
    public void test3() {
        Sale sale = null;
        try {
            testDA.open();
            sale = testDA.addSellerWithSale(sellerMail, sellerName, title, description, status, price, pubDate, null);
            testDA.close();

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), null, true);
            sut.close();

            assertFalse(res);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeSeller(sellerMail);
            testDA.close();
        }
    }

    @Test
    // CP4: s != null, aceptar = true, owner != null -> Elimina sale, actualiza owner y queja
    public void test4() {
        Sale sale = null;
        Queja queja = null;
        try {
            testDA.open();
            sale = testDA.addSellerWithSale(sellerMail, sellerName, title, description, status, price, pubDate, null);
            queja = testDA.addQuejaToSale(sale.getSaleNumber(), "Queja test 4");
            testDA.close();

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), queja, true);
            sut.close();

            assertTrue(res);

            // Comprobar que la venta se ha eliminado de la BD
            testDA.open();
            boolean exist = testDA.existSale(sellerMail, title);
            assertFalse(exist);
            testDA.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeSeller(sellerMail);
            testDA.close();
        }
    }

    @Test
    // CP5: s != null, aceptar = true, owner == null -> Elimina sale y actualiza queja (sin owner)
    public void test5() {
        Sale sale = null;
        Queja queja = null;
        try {
            testDA.open();
            sale = testDA.createSaleWithoutSeller(title, description, status, price, pubDate);
            queja = testDA.addQuejaToSale(sale.getSaleNumber(), "Queja test 5");
            testDA.close();

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), queja, true);
            sut.close();

            assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (sale != null) {
                testDA.open();
                testDA.removeSale(sale.getSaleNumber());
                testDA.close();
            }
        }
    }

    @Test
    // CP6: s != null, aceptar = false -> Mantiene sale, actualiza sale y queja
    public void test6() {
        Sale sale = null;
        Queja queja = null;
        try {
            testDA.open();
            sale = testDA.addSellerWithSale(sellerMail, sellerName, title, description, status, price, pubDate, null);
            queja = testDA.addQuejaToSale(sale.getSaleNumber(), "Queja test 6");
            testDA.close();

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), queja, false);
            sut.close();

            assertTrue("El metodo resolveReport debe devolver true", res);

            // Verificar en BD que la venta sigue existiendo
            testDA.open();
            boolean exist = testDA.existSaleByNumber(sale.getSaleNumber());
            assertTrue("La venta debe permanecer en la BD al rechazar el reporte", exist);
            testDA.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Excepción inesperada: " + e.getMessage());
        } finally {
            testDA.open();
            testDA.removeSeller(sellerMail);
            testDA.close();
        }
    }

    @Test
    // CP7: s == null, aceptar = true, owner != null -> Elimina sale y actualiza owner (sin merge a queja)
    public void test7() {
        Sale sale = null;
        try {
            testDA.open();
            sale = testDA.addSellerWithSale(sellerMail, sellerName, title, description, status, price, pubDate, null);
            testDA.close();

            Queja quejaTransient = new Queja("Queja no persistida", null);
            quejaTransient.setId(-999);

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), quejaTransient, true);
            sut.close();

            assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeSeller(sellerMail);
            testDA.close();
        }
    }

    @Test
    // CP8: s == null, aceptar = true, owner == null -> Solo elimina sale de BD
    public void test8() {
        Sale sale = null;
        try {
            testDA.open();
            sale = testDA.createSaleWithoutSeller(title, description, status, price, pubDate);
            testDA.close();

            Queja quejaTransient = new Queja("Queja no persistida", null);
            quejaTransient.setId(-999);

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), quejaTransient, true);
            sut.close();

            assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (sale != null) {
                testDA.open();
                testDA.removeSale(sale.getSaleNumber());
                testDA.close();
            }
        }
    }

    @Test
    // CP9: s == null, aceptar = false -> Mantiene sale y ejecuta merge(sale) únicamente
    public void test9() {
        Sale sale = null;
        try {
            testDA.open();
            sale = testDA.addSellerWithSale(sellerMail, sellerName, title, description, status, price, pubDate, null);
            testDA.close();

            Queja quejaTransient = new Queja("Queja no persistida", null);
            quejaTransient.setId(-999);

            sut.open();
            boolean res = sut.resolveReport(sale.getSaleNumber(), quejaTransient, false);
            sut.close();

            assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            testDA.open();
            testDA.removeSeller(sellerMail);
            testDA.close();
        }
    }
    */
}