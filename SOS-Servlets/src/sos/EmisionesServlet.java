package sos;

/** 
 * Creado por Manuel Aguilar Arroyo
 * laeknishendr314@gmail.com
 * 
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

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
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class EmisionesServlet extends HttpServlet {
	//persistencia de datos 
	DatastoreService persistance = DatastoreServiceFactory.getDatastoreService(); 
	
	
	/*	m�todos principales (do) */

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		process(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		process(req, resp);
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		process(req, resp);
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		process(req, resp);
	}
	
	public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		/*Este m�todo, que utilizaremos como m�todo por
		 * defecto para todos los comandos, tendremos que
		 * pillar el contenido de la URL, el m�todo
		 * por el que se ha accedido y generar una respuesta.
		 *
		 * */
		
		PrintWriter out = resp.getWriter(); 
		String path = req.getPathInfo(); 
		String method = req.getMethod(); 
		
	//	Emission yeahmision = new Emission("Spain", 9999.99, 9999, 2033);
	//	persistance.put(yeahmision.country, yeahmision); 
		
		if(path != null){
			
			/* En caso de que el path no sea nulo, accedemos al 
			 * recurso en concreto. Aqu� podremos hacer GET, POST y DELETE.  
			 * 
			 * */
			
			String[] resource = path.split("/"); 
			processResource(req, resp, resource[1], method); 
		}
		
		else{
			processResourceList(req, resp, method);
		}
		out.close(); 
	}
	

	
	/* m�todos CRUD relacionados con la obtenci�n de la lista de elementos del dataset
	 * 
	 * - GET
	 * - POST
	 * - DELETE
	 */
	
	@SuppressWarnings("static-access")
	public void processResourceList(HttpServletRequest req, HttpServletResponse resp, String method) 
			throws IOException{
		
		switch(method){
		
		case "GET": getEmissions(req, resp); break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break; 
		
		case "POST": postEmissions(req, resp);break; 
		
		case "DELETE": persistance.;  break;//se elimina todo el contenido del mapa!
		
		
		}
	}
	
	public void getEmissions(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		//devolver todo el mapa
		Gson gson = new Gson(); 
		
		String emisionesJson = gson.toJson(persistance.values()); 
		resp.getWriter().println(emisionesJson);
		 
		
	}
	
	@SuppressWarnings("static-access")
	public void postEmissions(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		Emission emiss = extractEmission(req); 
		
		if(persistance.containsKey(emiss)){
			resp.setStatus(resp.SC_CONFLICT);
		}else if (emiss == null){
			resp.setStatus(resp.SC_BAD_REQUEST); 
			resp.getWriter().println("error");
		}else{
			persistance.put(emiss.country, emiss); 
		}
		
	}
	
	
	/* m�todos CRUD para tratamiento de elementos concretos del datase
	* - GET
	* - PUT
	* - DELETE
	*/ 
	
	@SuppressWarnings("static-access")
	public void processResource(HttpServletRequest req, HttpServletResponse resp, String method, String resource) 
			throws IOException{
			
			
			Query q = new Query ("Emission").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, resource)); 
			PreparedQuery pq = persistance.prepare(q); 
			Entity e = pq.asSingleEntity(); 
			
			if(e==null) {
				resp.setStatus(resp.SC_NOT_FOUND); return;
				
			}
			
			/*
			Emission em = new Emission("Spain", 9999.99, 10, 2033); 
			persistance.put(em.country, em);						
			*/ 
			
			switch(method){
			
			case "GET": getEmission(req, resp, resource); break;
			
			case "PUT": updateEmission(req, resp, resource); break; 
			
			case "POST": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;
			
			
			
			case "DELETE": persistance.delete(e.getKey());
			
			
			}
	}
	
	private void getEmission(HttpServletRequest req, HttpServletResponse resp,
			String resource) throws IOException{
		Gson gson = new Gson(); 
		String gsonString = gson.toJson(persistance.get(resource)); 
		resp.getWriter().println(gsonString);
		
	}
	
	private void updateEmission(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
			
			Entity emission = extractEmission(req);
			
			if(emission == null){
				resp.sendError(400);
			}else if (emission.getProperty("country")!= resource){
				resp.sendError(403); 
			}else{
			persistance.put(emission); 
			}
		
	}
	
	
	
	//m�todo general para obtener emisiones transferidas via JSON
	
	private Entity extractEmission(HttpServletRequest req) throws IOException{
		Emission e = null; 
		Gson gson = new Gson(); 
		StringBuilder sb = new StringBuilder(); 
		BufferedReader br = req.getReader(); 
		System.out.println("El bufferedreader dice que " + br.toString()); 
		System.out.println("El httpservletrequest dice que " + req.getReader());
	
		String jsonString; 
		
		while((jsonString = br.readLine()) != null){
			sb.append(jsonString);
			
		}
		
		System.out.println("el StringBuilder ahora es "+ sb);
		System.out.println("el m�todo del req es "+ req.getMethod());
		
		jsonString = sb.toString(); 
		
		try{
			System.out.println("String to be parsed: <"+jsonString+">");
			e = gson.fromJson(jsonString, Emission.class); 
			
			System.out.println("Emission extracted: "+e+" (name = '"+e.country+", "+e.year+"')");
		}catch(Exception ex){
			System.out.println("ERROR parsing Emison: ");
				System.out.println("ERROR parsing Emission: " + ex.getMessage()); 
			}

		
		Entity ee = new Entity("Emission"); 
		ee.setProperty("country", e.country); 
		ee.setProperty("CO2emissions", e.CO2emissions); 
		ee.setProperty("population", e.population);
		ee.setProperty("year",e.year);
		
		return ee; 
	}
	
	
	
}
