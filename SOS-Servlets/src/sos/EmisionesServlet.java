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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class EmisionesServlet extends HttpServlet {
	//persistencia de datos 
	DatastoreService persistance = DatastoreServiceFactory.getDatastoreService(); 
	
	
	/*	métodos principales (do) */

	
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
	
	public void process(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException, ServletException {
		/*Este método, que utilizaremos como método por
		 * defecto para todos los comandos, tendremos que
		 * pillar el contenido de la URL, el método
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
			 * recurso en concreto. Aquí podremos hacer GET, POST y DELETE.  
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
	

	
	/* métodos CRUD relacionados con la obtención de la lista de elementos del dataset
	 * 
	 * - GET
	 * - POST
	 * - DELETE
	 */
	
	@SuppressWarnings("static-access")
	public void processResourceList(HttpServletRequest req, HttpServletResponse resp,  String method) 
			throws IOException{
		
		switch(method){
		
		case "GET": getEmissions(req, resp); break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break; 
		
		case "POST": postEmissions(req, resp);break; 
		
		case "DELETE": //persistance.delete(persistance.Query());
		
		
		}
	}
	
	public void getEmissions(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		//TODO
		
		Query q = new Query ("Emission");
		PreparedQuery pq = persistance.prepare(q); 
		Entity e = pq.asSingleEntity(); 
		
		
		Gson gson = new Gson(); 
		String emisionesJson = null;
		try {
			emisionesJson = gson.toJson(persistance.get(e.getKey()));
		} catch (EntityNotFoundException e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		resp.getWriter().println(emisionesJson);
		 
		
	}
	
	@SuppressWarnings("static-access")
	private void postEmissions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		//TODO
		
		Entity e = extractEntity(req); 
		Query q = new Query("Emission").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, "e.name")); 
		if(e == null){ resp.setStatus(resp.SC_BAD_REQUEST); }
		
		else if(q != null){ 
			//si la entidad ya esta, fallo
			resp.setStatus(resp.SC_CONFLICT); 
		}else{
			persistance.put(e); 
		} 
		
		}
	
	
	
	/* métodos CRUD para tratamiento de elementos concretos del datase
	* - GET
	* - PUT
	* - DELETE
	*/ 
	
	@SuppressWarnings("static-access")
	public void processResource(HttpServletRequest req, HttpServletResponse resp, String resource, String method) 
			throws IOException{
			
			
			Query q = new Query ("Emission").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, resource)); 
			PreparedQuery pq = persistance.prepare(q); 
			Entity e = pq.asSingleEntity(); 
			
			if(e==null) {
				resp.setStatus(resp.SC_NOT_FOUND); 
				return;	
			}
			
			switch(method){
			
			case "GET": getEmission(req, resp, e); 
				
				//getEmission(req, resp, resource); break;
			
			case "PUT": updateEmission(req, resp, resource); break; 
			
			case "POST": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;
						
			case "DELETE": persistance.delete(e.getKey());
			
			
			}
	}
	
	private void getEmission(HttpServletRequest req, HttpServletResponse resp, Entity e) 
			throws IOException{
		
		
		Emission em = new Emission( 
			(String) e.getProperty("country"), 
			(Double) e.getProperty("CO2emissions"),
			(Integer)e.getProperty("population"), 
			(Integer)e.getProperty("year")); 
		
		Gson gson = new Gson(); 
		String gsonString = null;
		
		gsonString = gson.toJson(em); 
	
		
		resp.getWriter().println(gsonString);
		
	}
	
	private void updateEmission(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		//TODO
			Entity emission = extractEntity(req);
			
			if(emission == null){
				resp.sendError(400);
			}else if (emission.getProperty("country")!= resource){
				resp.sendError(403); 
			}else{
			persistance.put(emission); 
			}
		
	}
	
	
	
	//método general para obtener emisiones transferidas via JSON
	
	private Emission extractEmission(HttpServletRequest req) throws IOException{
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
		System.out.println("el método del req es "+ req.getMethod());
		
		jsonString = sb.toString(); 
		
		try{
			System.out.println("String to be parsed: <"+jsonString+">");
			e = gson.fromJson(jsonString, Emission.class); 
			
			System.out.println("Emission extracted: "+e+" (name = '"+e.country+", "+e.year+"')");
		}catch(Exception ex){
			System.out.println("ERROR parsing Emison: ");
				System.out.println("ERROR parsing Emission: " + ex.getMessage()); 
			}
		return e; 
		
		
	}
	
	private Entity extractEntity(HttpServletRequest req) throws IOException{
		Emission e = extractEmission(req); 
		
		Entity ee = new Entity("Emission"); 
		ee.setProperty("country", e.country); 
		ee.setProperty("CO2emissions", e.CO2emissions); 
		ee.setProperty("population", e.population);
		ee.setProperty("year",e.year);
		
		return ee; 
	}
	
	
	
}
