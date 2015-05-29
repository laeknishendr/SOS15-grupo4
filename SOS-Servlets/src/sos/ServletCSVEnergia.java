package sos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.opencsv.CSVReader;

@SuppressWarnings("serial")
public class ServletCSVEnergia extends HttpServlet {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	@SuppressWarnings("resource")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
	
		CSVReader reader = new CSVReader(new FileReader("war/csv/tiposEnergia.csv"));
		
		String [] nextLine;
		
		while ((nextLine = reader.readNext()) != null) {
			
			Entity entity = new Entity("Energy");
		
			entity.setProperty("name", nextLine[0]);
			entity.setProperty("no_fossil", new Double (nextLine[1]));
			entity.setProperty("fossil", new Double (nextLine[2]));
			entity.setProperty("temperature", new Double (nextLine[3]));
			
			System.out.println(entity);
			
			Query q = new Query ("Energy").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, entity.getProperty("name")));
			PreparedQuery pq = datastore.prepare(q);
			Entity en = pq.asSingleEntity();
			
			System.out.println(en);
			
			if(en != null){
				datastore.delete(en.getKey());
				datastore.put(entity);
			}else{
				datastore.put(entity);
			}	
		}
	}
}	