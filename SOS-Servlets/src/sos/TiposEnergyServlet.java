package sos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class TiposEnergyServlet extends HttpServlet {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
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
	
	public void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, 
		IOException {
		/*Este método, que utilizaremos como método por
		 * defecto para todos los comandos, tendremos que
		 * pillar el contenido de la URL, el método
		 * por el que se ha accedido y generar una respuesta.
		 * 
		 * 
		 * */
		PrintWriter out = resp.getWriter(); 
		String path = req.getPathInfo(); 
		String method = req.getMethod(); 
		
		if(path != null){
			String[] pathComponents = path.split("/");
			@SuppressWarnings("unused")
			String resource = pathComponents[1];
			
			processResource(method, pathComponents[1], req, resp);
			
		}else{
			
			processResourceList(method, req, resp);
		}
		
		out.close();

	}
	
	@SuppressWarnings("static-access")
	private void processResourceList(String method, HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		switch(method){
		
			case "POST": postEnergies(req, resp); break; 
		
			case "GET": getEnergies(req, resp); break;  
		
			case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;  
		
			case "DELETE": ds.clear(); break; 
		}
	}
	
	@SuppressWarnings("static-access")
	private void processResource(String method, String resource, HttpServletRequest req, 
			HttpServletResponse resp) throws IOException{
		
		Query q = new Query("Emissions").setFilter(new FilterPredicate("name",Query.FilterOperator.EQUAL, resource));
		PreparedQuery pq = datastore.prepare(q);
		Entity e = pq.asSingleEntity();
		
		if(method == "POST"){ 
			resp.setStatus(resp.SC_METHOD_NOT_ALLOWED); return;
		}
		
		if(q == null){  
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}
		
		switch(method){
		
			case "GET": getEnergy(req, resp, resource); break; 
		
			case "PUT": updateEnergy(req, resp, resource); break; 
		
			case "DELETE": datastore.delete(extractEntity(req).getKey()); break;
		}
	}
	
	@SuppressWarnings("static-access")
	private void postEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Entity e = extractEntity(req);
		Query q = new Query("Emissions").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, "e.name"));
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(q != null){
			//si la entidad ya esta, fallo
			resp.setStatus(resp.SC_CONFLICT);
		}else{
			datastore.put(e);
		}
	}
	
	private void getEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(ds.values());
		
		resp.getWriter().println(jsonString);
		
	}
	
	private Energy extractEnergy(HttpServletRequest req) throws IOException{
		
		Energy e = null;
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
		String jsonString;
		
		while((jsonString = br.readLine()) != null){
			sb.append(jsonString);
		}
		jsonString = sb.toString();
		
		try{
			e = gson.fromJson(jsonString, Energy.class);
		}catch(Exception em){
			System.out.println("Error parsin Emissions: "+em.getMessage());
		}
		
		return e;
	}
	
	@SuppressWarnings("static-access")
	private void updateEnergy(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Entity e = extractEntity(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(e.name != resource){
			resp.setStatus(resp.SC_FORBIDDEN);
		}else{
			datastore.put(e);
		}
	}
	
	private void getEnergy(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(ds.get(resource));
		
		resp.getWriter().println(jsonString);
		
	}
	
	private Entity extractEntity(HttpServletRequest req) throws IOException{
		
		Energy e = extractEnergy(req);
		Entity entity = new Entity("Emissions");
		
		entity.setProperty("name", e.name);
		entity.setProperty("no_fossil", e.no_fossil);
		entity.setProperty("fossil", e.fossil);
		entity.setProperty("temperature", e.temperature);
		
		return entity;
	}
}
