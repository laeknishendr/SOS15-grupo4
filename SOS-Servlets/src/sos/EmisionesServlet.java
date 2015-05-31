package sos;

/** 
 * Creado por Manuel Aguilar Arroyo
 * laeknishendr314@gmail.com
 * 
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		
		if(path != null){
			
			/* En caso de que el path no sea nulo, accedemos al 
			 * recurso en concreto. Aquí podremos hacer GET, POST y DELETE.  
			 * 
			 * */
			
			String[] pathComponents = path.split("/");
			String resource = "";
			if(pathComponents.length>=2){
				resource = pathComponents[1];
			}
			
			processResource(req, resp, resource, method); System.out.println("Llega hasta el Process");
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
		
		case "GET": getEmissions(req, resp); System.out.println("Llega hasta el ProcessResourceList");  break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break; 
		
		case "POST": postEmissions(req, resp);break; 
		
		case "DELETE": removeList(req,resp); break; 
		
		
		}
	}
	
	private void removeList(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("En removeEmissions");

		Query q = new Query("Emission"); 
		PreparedQuery pq = persistance.prepare(q); 
		Iterator<Entity> it = pq.asIterator(); 
		while(it.hasNext()){
			Entity e = it.next(); 
			persistance.delete(e.getKey());
		}
		System.out.println(" -- [OK] Emisiones eliminadas con éxito");
	}

	public void getEmissions(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		System.out.println("En GetEmissions");
		
		Gson gson = new Gson();
		List<String> jsonString = new ArrayList<String>();  //gson.toJson(ds.values());
		
		Query q = new Query("Emission");
		PreparedQuery pq = persistance.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		
		System.out.println(" [OK] La query ha sido realizada con éxito");
		
		if(!it.hasNext()){ System.out.println(" -- No entra en el bucle"); }
		
		while(it.hasNext()){
			System.out.println(" [OK] Ha entrado en el bucle");
	        
			Entity e = it.next();
			Emission em = new Emission( 
					(String) e.getProperty("country"), 
					(Double) e.getProperty("CO2emissions"),
					(Long)e.getProperty("population"), 
					(Long)e.getProperty("year")); 
				String gsonator = gson.toJson(em);	
				jsonString.add(gsonator);
		}
		
		//resp.getWriter().println(jsonString);	 
		
	}
	
	@SuppressWarnings({ "static-access", "unused" })
	private void postEmissions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		System.out.println("En postEnergies");
		Entity e = extractEntity(req);
		System.out.println(" [OK] La entidad extraída es: " + e.toString());
		Query q = new Query ("Emission").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, e.getProperty("country")));
		System.out.println(" [OK] La query es: "+q);
		PreparedQuery pq = persistance.prepare(q);
		
		Entity en = pq.asSingleEntity();
		
		
		System.out.println(" [OK] La entidad sacada de la query es: "+en);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(en != null){
			resp.setStatus(resp.SC_CONFLICT);
		}else{
			System.out.println(" [OK] Ejecutando el comando POST...");
			persistance.put(e);
			System.out.println(" [OK] POST se ha realizado con éxito");
		}
		
		
	}
	
	
	/* métodos CRUD para tratamiento de elementos concretos del dataset
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
			
			case "GET": getEmission(req, resp, e); break;
				
				//getEmission(req, resp, resource); break;
			
			case "PUT": updateEmission(req, resp, resource); break; 
			
			case "POST": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;
						
			case "DELETE": removeResource(req, resp,e); break;   
			
			
			}
	}
	
	private void getEmission(HttpServletRequest req, HttpServletResponse resp, Entity e) 
			throws IOException{
		System.out.println("Llega hasta el GetEmission");
		
		Emission em = new Emission( 
			(String) e.getProperty("country"), 
			(Double) e.getProperty("CO2emissions"),
			(Long)e.getProperty("population"), 
			(Long)e.getProperty("year")); 
		System.out.println("Crea la entidad");
		Gson gson = new Gson(); 
		String gsonString = gson.toJson(em);	
		resp.getWriter().println(gsonString);
		System.out.println("Petición a recurso realizada con éxito");
		
	}
	
	@SuppressWarnings("static-access")
	private void updateEmission(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
			System.out.println("En UpdateEmission");
			Entity emission = extractEntity(req);
			
			if(emission == null){
				resp.setStatus(resp.SC_BAD_REQUEST);
			}else if (!(emission.getProperty("country").equals(resource))){
				resp.setStatus(resp.SC_FORBIDDEN);
				
			}else{
				System.out.println(" -- [OK] Emisión extraída con éxito");
				Query q = new Query("Emission").setFilter(new FilterPredicate("country",Query.FilterOperator.EQUAL, resource));
				PreparedQuery pq = persistance.prepare(q);
				Entity original = pq.asSingleEntity();
				persistance.delete(original.getKey());
				List<String> atributos = new ArrayList<String>(); 
				atributos.add("country"); 
				atributos.add("CO2emissions");
				atributos.add("population");
				atributos.add("year");
				System.out.println(" -- [OK] Emisión actualizada creada con éxito");
				
				for(String a:atributos){
					if(!(emission.getProperty(a).equals(original.getProperty(a))))
						original.setProperty(a, emission.getProperty(a));	
				}
				System.out.println(" -- Preparándose para actualizar la emisión...");
				persistance.put(original);
				System.out.println(" -- [OK] Emisión actualizada con éxito");
				
				
			}
		
	}
	@SuppressWarnings("static-access")
	private void removeResource(HttpServletRequest req, HttpServletResponse resp, Entity e){
		
		if(e == null){
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}else{
			persistance.delete(e.getKey());
		}
	}
	
	
	//método general para obtener emisiones transferidas via JSON
	
	private Emission extractEmission(HttpServletRequest req) throws IOException{
		System.out.println(" -- En extractEmission"); 
		Emission e = null; 
		Gson gson = new Gson(); 
		StringBuilder sb = new StringBuilder(); 
		BufferedReader br = req.getReader(); 

	
		String jsonString; 
		
		while((jsonString = br.readLine()) != null){
			sb.append(jsonString);
			
		}
		
		System.out.println(" ---- [OK] el StringBuilder es "+ sb);
		System.out.println(" ---- [OK] el método del req es "+ req.getMethod());
		
		jsonString = sb.toString(); 
		
		try{
			System.out.println(" ---- [OK] String to be parsed: <"+jsonString+">");
			e = gson.fromJson(jsonString, Emission.class); 
			
			System.out.println(" ---- [OK] Emission extracted: "+e+" (name = '"+e.country+", "+e.year+"')");
			System.out.println(" ---- [OK] Emisión extraída con éxito"); 
		}catch(Exception ex){
			System.out.println(" ---- [ERROR] ERROR parsing Emison: ");
				System.out.println(" ---- [ERROR] ERROR parsing Emission: " + ex.getMessage()); 
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
