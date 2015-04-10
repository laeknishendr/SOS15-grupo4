package sos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class TiposEnergyServlet extends HttpServlet {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		process(req, resp);
		System.out.println("doGet");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		process(req, resp);
		System.out.println("doPost");
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		process(req, resp);
		System.out.println("doPut");
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		process(req, resp);
		System.out.println("doDelete");
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
		System.out.println("metodo process");
		out.close();
	}
	
	@SuppressWarnings("static-access")
	private void processResourceList(String method, HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		switch(method){
		
			case "POST": postEnergies(req, resp); break; 
		
			case "GET": getEnergies(req, resp); break;  
		
			case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;  
		
			case "DELETE": removeList(req, resp); break; 
		}
		System.out.println("processLIst");
	}
	
	@SuppressWarnings("static-access")
	private void postEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Entity e = extractEntity(req);
		Query q = new Query ("Energy").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, e.getProperty("name")));
		PreparedQuery pq = datastore.prepare(q);
		Entity en = pq.asSingleEntity();
		System.out.println(en);
		System.out.println(e);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(en != null){
			resp.setStatus(resp.SC_CONFLICT);
		}else{System.out.println("entra");
			datastore.put(e);
		}
		System.out.println("post lista");
		
	}
	
	private void getEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		//TODO
		Gson gson = new Gson();
		List<String> jsonString = new ArrayList<String>();  //gson.toJson(ds.values());
		
		Query q = new Query("Energy");
		PreparedQuery pq = datastore.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		
		while(it.hasNext()){
			String aux = gson.toJson(it.next(), String.class); //asi va pisando las anteriores iteraciones del while, buscar metodo 
			//(guardarlo en un mapa con clave el pais y meterle los valores solo?)	
			//jsonString sea una List<String> y un string aux que si se pueda ir pisando?
			jsonString.add(aux);
		}
		
		resp.getWriter().println(jsonString);	
		System.out.println("get lista");
	}
	
	private void removeList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Query q = new Query("Energy");
		PreparedQuery pq = datastore.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		
		while(it.hasNext()){
			Entity e = it.next();
			datastore.delete(e.getKey());
		}
		System.out.println("borrar lista");
	}
	
	@SuppressWarnings("static-access")
	private void processResource(String method, String resource, HttpServletRequest req, 
			HttpServletResponse resp) throws IOException{
		
		Query q = new Query("Energy").setFilter(new FilterPredicate("name",Query.FilterOperator.EQUAL, resource));
		PreparedQuery pq = datastore.prepare(q);
		Entity e = pq.asSingleEntity();
		
		if(method == "POST"){ 
			resp.setStatus(resp.SC_METHOD_NOT_ALLOWED); return;
		}
		
		if(q == null){  
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}
		
		switch(method){
		
			case "GET": getEnergy(req, resp, e); break; 
		
			case "PUT": updateEnergy(req, resp, resource); break; 
		
			case "DELETE": removeResource(req, resp, e);/*datastore.delete(e.getKey()); System.out.println("borrar"); */break;
		}
		System.out.println("process elto");
	}
	
	private void getEnergy(HttpServletRequest req, HttpServletResponse resp, Entity e) 
			throws IOException{
		//pasar la entidad a objeto y el objeto a json y devolverla por pantalla
		Gson gson = new Gson();
		String jsonString = null;
		if(e == null){
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}else{
			Energy en = new Energy((String) e.getProperty("name"), 
					(double) e.getProperty("no_fossil"), 
					(double) e.getProperty("fossil"), 
					(double) e.getProperty("temperature"));
			
			jsonString = gson.toJson(en);
		}
		
		
		resp.getWriter().println(jsonString);
		System.out.println("get elto");
	}
	
	@SuppressWarnings("static-access")
	private void updateEnergy(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Entity e = extractEntity(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(e.getProperty("name") != resource){
			resp.setStatus(resp.SC_FORBIDDEN);
		}else{
			datastore.put(e);
		}
		System.out.println("put elto");
	}
	
	private void removeResource(HttpServletRequest req, HttpServletResponse resp, Entity e){
		
		if(e == null){
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}else{
			datastore.delete(e.getKey());
		}
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
			System.out.println("Error parsin Energy: "+em.getMessage());
		}
		
		return e;
	}
	
	private Entity extractEntity(HttpServletRequest req) throws IOException{
		
		Energy e = extractEnergy(req);
		Entity entity = new Entity("Energy");
		
		entity.setProperty("name", e.name);
		entity.setProperty("no_fossil", e.no_fossil);
		entity.setProperty("fossil", e.fossil);
		entity.setProperty("temperature", e.temperature);
		
		return entity;
	}
}
