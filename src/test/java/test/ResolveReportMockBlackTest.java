package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.*;

public class ResolveReportMockBlackTest {

	static DataAccess sut;

	protected MockedStatic<Persistence> persistenceMock;

	@Mock
	protected EntityManagerFactory entityManagerFactory;
	@Mock
	protected EntityManager db;
	@Mock
	protected EntityTransaction et;

	private Integer saleNumber;
	private Sale sale;
	private Queja salaketa;
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

		// Datos por defecto para las pruebas
		saleNumber = 1;
		owner = new Seller("ownerTest@ehu.eus", "Owner Test");
		sale = new Sale();
		sale.setSaleNumber(saleNumber);
		salaketa = new Queja();
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}

	@Test
	// BD sin errores, dueño = null, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado esperado: BD actualizada, retorna True
	public void test1() {
		sale.setSeller(null);
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(salaketa);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, dueño != null, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado esperado: BD actualizada (elimina venta de owner), retorna True
	public void test2() {
		sale.setSeller(owner);
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(salaketa);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber ∈ BD, salaketa ∈ BD
	// Parámetros: Salaketa != null, aceptar = false
	// Resultado esperado: Actualiza sale en BD, marca salaketa tratatuta=true, retorna True
	public void test3() {
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(salaketa);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, false);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}
	/*

	@Test
	// BD sin errores, dueño = null, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado esperado: Elimina sale, retorna True
	public void test4() {
		sale.setSeller(null);
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(null);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}
	
	*/
	@Test
	// BD sin errores, dueño != null, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = true
	// Resultado esperado: Elimina venta de owner y elimina sale, retorna True
	public void test5() {
		sale.setSeller(owner);
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(null);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}
/*
	@Test
	// BD sin errores, saleNumber ∈ BD, salaketa ∉ BD
	// Parámetros: Salaketa != null, aceptar = false
	// Resultado esperado: Actualiza sale en BD, retorna True
	public void test6() {
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		Mockito.when(db.find(Queja.class, salaketa.getId())).thenReturn(null);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, false);
			sut.close();

			assertTrue(res);
		} catch (Exception e) {
			fail();
		}
	}
*/
	@Test
	// Error de BD
	// Resultado esperado: No cambia estado BD, retorna False
	public void test7() {
		Mockito.when(db.find(Mockito.any(), Mockito.any())).thenThrow(new PersistenceException("Error BD"));

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertFalse(res);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber = null
	// Resultado esperado: No cambia estado BD, retorna False
	public void test8() {
		saleNumber = null;

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertFalse(res);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber ∉ BD
	// Resultado esperado: No cambia estado BD, retorna False
	public void test9() {
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(null);

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertFalse(res);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BD sin errores, saleNumber ∈ BD, Salaketa = null
	// Resultado esperado: No cambia estado BD, retorna False
	public void test10() {
		Mockito.when(db.find(Sale.class, saleNumber)).thenReturn(sale);
		salaketa = null;

		try {
			sut.open();
			boolean res = sut.resolveReport(saleNumber, salaketa, true);
			sut.close();

			assertFalse(res);
		} catch (Exception e) {
			fail();
		}
	}
}