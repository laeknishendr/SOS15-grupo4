package sos;

/** 
 * Creado por Manuel Aguilar Arroyo
 * laeknishendr314@gmail.com
 * 
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

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
		System.out.println("Llega hasta el doGet");
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
		System.out.println("Llega hasta el Process");
		if(path != null){
			
			/* En caso de que el path no sea nulo, accedemos al 
			 * recurso en concreto. Aquí podremos hacer GET, POST y DELETE.  
			 * 
			 * */
			
			String[] resource = path.split("/"); 
			processResource(req, resp, resource[1], method); System.out.println("Llega hasta el Process");
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
		System.out.println("Llega hasta el ProcessResourceList");
		switch(method){
		
		case "GET": getEmissions(req, resp); System.out.println("Llega hasta el ProcessResourceList");  break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break; 
		
		case "POST": postEmissions(req, resp);break; 
		
		case "DELETE": //persistance.delete(persistance.Query());
		
		
		}
	}
	
	//TODO
	public void getEmissions(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		System.out.println("Estamos en el GetEmissions");
		Gson gson = new Gson();
		List<String> jsonString = null;  //gson.toJson(ds.values());
		
		Query q = new Query("Emissions");
		PreparedQuery pq = persistance.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		System.out.println(" -- La query ha sido realizada con éxito");
		
		if(!it.hasNext()){ System.out.println(" -- No me sale de los mismisimos entrar en el bucle xd"); }
		while(it.hasNext()){
			System.out.println(" -- Ha entrado en el bucle");
			String aux = gson.toJson(it.next()); //asi va pisando las anteriores iteraciones del while, buscar metodo 
			//(guardarlo en un mapa con clave el pais y meterle los valores solo?)	
			//jsonString sea una List<String> y un string aux que si se pueda ir pisando?
			System.out.println(" -- El obj es: "+aux);
			jsonString.add(aux);
			System.out.println(" -- JsonString es: "+jsonString);
		}
		
		resp.getWriter().println(jsonString);
		 
		
	}
	
	@SuppressWarnings({ "static-access", "unused" })
	private void postEmissions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		//TODO
		System.out.println("Llega hasta el PostEmissions");
		Entity e = extractEntity(req); 
		Query q = new Query("Emission").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, e.getProperty("name"))); 
		PreparedQuery pq = persistance.prepare(q); 
		Entity entity = pq.asSingleEntity(); 
		System.out.println("La entidad que se ha sacado de la query es " + entity); 
		if(e == null){ 
			resp.setStatus(resp.SC_BAD_REQUEST); 
		}
		
		else if(entity != null){ 
			System.out.println("El error 409 es por esto so pedazo de gilipipas");
			System.out.println("La query es: "+q);
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
			System.out.println("Llega hasta el ProcessResource");
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
		System.out.println("Llega hasta el GetEmission");
		
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
		System.out.println("Estamos en el ExtractEmission"); 
		Emission e = null; 
		Gson gson = new Gson(); 
		StringBuilder sb = new StringBuilder(); 
		BufferedReader br = req.getReader(); 

	
		String jsonString; 
		
		while((jsonString = br.readLine()) != null){
			sb.append(jsonString);
			
		}
		
		System.out.println(" -- el StringBuilder es "+ sb);
		System.out.println(" -- el método del req es "+ req.getMethod());
		
		jsonString = sb.toString(); 
		
		try{
			System.out.println(" -- String to be parsed: <"+jsonString+">");
			e = gson.fromJson(jsonString, Emission.class); 
			
			System.out.println(" -- Emission extracted: "+e+" (name = '"+e.country+", "+e.year+"')");
			System.out.println(" -- Emisión extraída con éxito"); 
		}catch(Exception ex){
			System.out.println(" -- ERROR parsing Emison: ");
				System.out.println(" -- ERROR parsing Emission: " + ex.getMessage()); 
			}
		return e; 
		
		
	}
	
	private Entity extractEntity(HttpServletRequest req) throws IOException{
		System.out.println("Llega al ExtractEntity"); 

		Emission e = extractEmission(req); 
		
		Entity ee = new Entity("Emission"); 
		ee.setProperty("country", e.country); 
		ee.setProperty("CO2emissions", e.CO2emissions); 
		ee.setProperty("population", e.population);
		ee.setProperty("year",e.year);
		System.out.println(" -- La emisión quedaría tal que: "+ ee.toString()); 
		return ee; 
	}
	
	
	
}
