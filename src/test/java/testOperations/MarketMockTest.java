package testOperations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.*;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mockito.Mockito;

import businessLogic.*;
import configuration.*;
import domain.*;
import gui.MainGUI;

public class MarketMockTest {
	static BLFacade appFacadeMock = Mockito.mock(BLFacade.class);
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		configureMockQuerySales();
		MainGUI sut = new MainGUI("jon@gmail.com");
		MainGUI.setBussinessLogic(appFacadeMock);
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		sut.setVisible(true);
	}
	
	public static void configureMockQuerySales() {
		Seller seller1=new Seller("seller1@gmail.com","Aitor Fernandez");
		Date today = UtilDate.trim(new Date());
		List<Sale> sales=new Vector<Sale>();
		sales.add(new Sale("futbol baloia", "oso polita, gutxi erabilita", 2, 10,
		today, null, seller1));
		sales.add(new Sale("salomon mendiko botak", "44 zenbakia, 3 ateraldi",2, 20,
		today, null, seller1));
		sales.add(new Sale("samsung 42\" telebista", "berria, erabili gabe", 1, 175,
		today, null, seller1));
		Mockito.when(appFacadeMock.getPublishedSales(anyString(),
		any(Date.class))).thenReturn(sales);
		
	}
}