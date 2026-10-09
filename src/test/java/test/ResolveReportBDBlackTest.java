package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import dataAccess.DataAccess;
import domain.Queja;
import domain.Sale;
import domain.Seller;

public class ResolveReportBDBlackTest {

	static DataAccess sut = new DataAccess();

	static TestDataAccess testDA = new TestDataAccess();

	private String sellerMail;
	private String sellerName;
	private Queja queja;

	@Before
	public void defaultValues() {
		sellerMail = "sellerTest@ehu.eus";
		sellerName = "Seller Test";
		queja = new Queja("Reporte de prueba", "reporterTest@ehu.eus");
	}

	@Test
	// BD sin errores, dueño = null, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado: Elimina salaketak, elimina sale y retorna True
	public void test1() {
		testDA.open();
		Integer saleNumber = testDA.addSaleWithQueja(null, queja);
		testDA.close();

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, queja, true);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(!exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSale(saleNumber);
			testDA.removeQueja(queja);
			testDA.close();
		}
	}

	@Test
	// BD sin errores, dueño != null, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado: Elimina salaketak, elimina venta de owner, elimina sale y retorna True
	public void test2() {
		testDA.open();
		Seller owner = testDA.createSeller(sellerMail, sellerName);
		Integer saleNumber = testDA.addSaleWithQueja(owner, queja);
		testDA.close();

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, queja, true);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(!exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.removeSale(saleNumber);
			testDA.removeQueja(queja);
			testDA.close();
		}
	}

	@Test
	// BD sin errores, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = false
	// Resultado: Elimina salaketak, marca tratatuta=true, actualiza sale en BD y retorna True
	public void test3() {
		testDA.open();
		Integer saleNumber = testDA.addSaleWithQueja(null, queja);
		testDA.close();

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, queja, false);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSale(saleNumber);
			testDA.removeQueja(queja);
			testDA.close();
		}
	}

	@Test
	// BD sin errores, dueño = null, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado: Elimina salaketak de sale, elimina sale y retorna True
	public void test4() {
		testDA.open();
		Sale sale = testDA.addSale(null);
		Integer saleNumber = sale.getSaleNumber();
		testDA.close();

		Queja quejaNoEnBD = new Queja("Reporte no en BD", "reporterTest@ehu.eus");
		quejaNoEnBD.setId(9999);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, quejaNoEnBD, true);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(!exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSale(saleNumber);
			testDA.close();
		}
	}

	@Test
	// BD sin errores, dueño != null, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado: Elimina salaketak, elimina venta de owner, elimina sale y retorna True
	public void test5() {
		testDA.open();
		Seller owner = testDA.createSeller(sellerMail, sellerName);
		Sale sale = testDA.addSale(owner);
		Integer saleNumber = sale.getSaleNumber();
		testDA.close();

		Queja quejaNoEnBD = new Queja("Reporte no en BD", "reporterTest@ehu.eus");
		quejaNoEnBD.setId(9999);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, quejaNoEnBD, true);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(!exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.removeSale(saleNumber);
			testDA.close();
		}
	}

	@Test
	// BD sin errores, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = false
	// Resultado: Elimina salaketak, actualiza sale en BD y retorna True
	public void test6() {
		testDA.open();
		Sale sale = testDA.addSale(null);
		Integer saleNumber = sale.getSaleNumber();
		testDA.close();

		Queja quejaNoEnBD = new Queja("Reporte no en BD", "reporterTest@ehu.eus");
		quejaNoEnBD.setId(9999);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, quejaNoEnBD, false);
			sut.close();

			assertTrue(res);

			testDA.open();
			boolean exist = testDA.existSale(saleNumber);
			assertTrue(exist);
			testDA.close();

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSale(saleNumber);
			testDA.close();
		}
	}

	@Test
	// Error de BD
	// Resultado: No cambia estado BD y retorna False
	public void test7() {
		try {
			boolean res = sut.resolveReport(1, queja, true);
			assertFalse(res);

		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	// BD sin errores, saleNumber = null
	// Resultado: No cambia estado BD y retorna False
	public void test8() {
		try {
			sut.open();
			boolean res = sut.resolveReport(null, queja, true);
			sut.close();

			assertFalse(res);

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber ∉ BD
	// Resultado: No cambia estado BD y retorna False
	public void test9() {
		Integer saleNumberInexistente = 99999;

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumberInexistente, queja, true);
			sut.close();

			assertFalse(res);

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber ∈ BD, salaketa = null
	// Resultado: No cambia estado BD y retorna False
	public void test10() {
		testDA.open();
		Sale sale = testDA.addSale(null);
		Integer saleNumber = sale.getSaleNumber();
		testDA.close();

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, null, true);
			sut.close();

			assertFalse(res);

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSale(saleNumber);
			testDA.close();
		}
	}
}
