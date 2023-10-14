package test.dataAccess;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Event;
import domain.Question;
import domain.Quote;
import exceptions.EventNotFinished;

public class EmaitzakIpiniDABTest {

	protected EntityManager db;
	protected EntityManagerFactory emf;
	private DataAccess dataAccess;

	ConfigXML c = ConfigXML.getInstance();
	

    @Before
    public void setUp() {
        dataAccess = new DataAccess();
    }


	public EmaitzakIpiniDABTest() {

		System.out.println("Creating EmaitzakIpiniDABTest instance");

		open();

	}

	public void open() {

		System.out.println("Opening TestDataAccess instance ");

		String fileName = c.getDbFilename();

		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());

			emf = Persistence.createEntityManagerFactory(
					"objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);

			db = emf.createEntityManager();
		}

	}

	public void close() {
		db.close();
		System.out.println("DataBase closed");
	}

	@Test
	public void testEmaitzakIpini() throws EventNotFinished {
		// Crear un Quote con una fecha de evento en el pasado
		System.out.println(">> Test: emaitzakIpini");
		try {
			db.getTransaction().begin();
			Event event = new Event("Leganes vs Mallorca", new Date(2023 - 10 - 12));
			Question question = new Question(1, "Quien va a ganar", 5, event);
			Quote quote = new Quote(10.5, "Mallorca", question);
			dataAccess.EmaitzakIpini(quote);
			System.out.println("Llega hasta aqui");
			db.persist(quote);
			db.getTransaction().commit();
			
		} catch (EventNotFinished e) {
			fail("No debería haber lanzado una excepción EventNotFinished");
		}
	}
}
