package sos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.opencsv.CSVReader;

public class ServletCSVConsumo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		FileReader fr = null;
		try {
			fr = new FileReader("csv/consumos.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		CSVReader reader = new CSVReader(fr);

		String[] nextLine;

		try {
			while ((nextLine = reader.readNext()) != null) {
				createAndStoreEntity(nextLine[0], new Double(nextLine[1]), new Double(nextLine[2]), new Double(nextLine[3]), new Long(nextLine[4]));
			}
		} catch (NumberFormatException | TooManyResultsException | IOException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createAndStoreEntity(String country, Double energy_production, Double energy_use, Double energy_import, Long year) {
		Entity entity = new Entity("Consumos");
		entity.setProperty("country", country);
		entity.setProperty("energy_production", energy_production);
		entity.setProperty("energy_use", energy_use);
		entity.setProperty("energy_import", energy_import);
		entity.setProperty("year", year);

		System.out.println(entity);

		Query q = new Query("Consumos").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, entity.getProperty("country")));
		PreparedQuery pq = datastore.prepare(q);
		Entity en = pq.asSingleEntity();

		System.out.println(en);

		if (en != null) {
			datastore.delete(en.getKey());
			datastore.put(entity);
		} else {
			datastore.put(entity);
		}

	}
}