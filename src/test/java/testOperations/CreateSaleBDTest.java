package testOperations;

import static org.junit.Assert.*;

import java.text.*;
import java.util.*;

import org.junit.*;

import dataAccess.*;
import exceptions.*;

public class CreateSaleBDTest {
	static DataAccess sut=new DataAccess();
	static TestDataAccess testOp=new TestDataAccess ();
	@Test
	public void test4() {
		String title="futbol baloia";
		String description="Used one hour";
		int status=0;
		float price=10;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date pubDate=null;
		try {
			pubDate = sdf.parse("05/10/2026");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sellerMail="sellerTest1@ehu.eus";
		String sellerName="Seller Test 1";
		testOp.open();
		testOp.addSellerWithSale(sellerMail, sellerName, title, description,
				status, price, pubDate, null);
		testOp.close();
		try {
			sut.open();
			sut.createSale(title, description, status, price, pubDate,
					sellerMail, null);
			sut.close();
			fail();
		} catch (SaleAlreadyExistException e ) {
			// if the program goes to this point true
			assertTrue(true);
		} catch (ParamNullException | MustBeLaterThanTodayException e ) {
			// if the program goes to this point fail
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			fail();
		} catch (Exception e) {
			fail();
		} finally {
			testOp.open();
			testOp.removeSeller(sellerMail);
			testOp.close();
		}
	}
}