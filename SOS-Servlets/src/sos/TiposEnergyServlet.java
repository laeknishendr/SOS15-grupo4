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
			String resource = "";
			if(pathComponents.length>=2){
				resource = pathComponents[1];
			}
			
			processResource(method, resource, req, resp);
			
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
		
			case "PUT": resp.setStatus(resp.SC_METHOD_NOT_ALLOWED); break;  
		
			case "DELETE": removeList(req, resp); break; 
		}
	}
	
	@SuppressWarnings("static-access")
	private void postEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Entity e = extractEntity(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else{
			Query q = new Query ("Energy").setFilter(new FilterPredicate("name", Query.FilterOperator.EQUAL, e.getProperty("name")));
			PreparedQuery pq = datastore.prepare(q);
			Entity en = pq.asSingleEntity();
			
			if(en != null){
				resp.setStatus(resp.SC_CONFLICT);
			}else{
			
				datastore.put(e);
			}
		}
	}
	
	private void getEnergies(HttpServletRequest req, HttpServletResponse resp) throws IOException{

		Gson gson = new Gson();
		List<String> jsonString = new ArrayList<String>();  
		
		Query q = new Query("Energy");
		PreparedQuery pq = datastore.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		
		while(it.hasNext()){
			Entity aux1 = it.next();

			Energy en = new Energy((String) aux1.getProperty("name"), 
					(Double) aux1.getProperty("no_fossil"), 
					(Double) aux1.getProperty("fossil"), 
					(Double) aux1.getProperty("temperature"));
			String aux2 = gson.toJson(en); 

			jsonString.add(aux2);
		}
		
		resp.getWriter().println(jsonString);	
	}
	
	private void removeList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Query q = new Query("Energy");
		PreparedQuery pq = datastore.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		
		while(it.hasNext()){
			Entity e = it.next();
			datastore.delete(e.getKey());
		}
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
		
			case "DELETE": removeResource(req, resp, e); break;
		}
	}
	
	@SuppressWarnings("static-access")
	private void getEnergy(HttpServletRequest req, HttpServletResponse resp, Entity e) 
			throws IOException{

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
	}
	
	@SuppressWarnings("static-access")
	private void updateEnergy(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Entity e = extractEntity(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(!(e.getProperty("name").equals(resource))){
			resp.setStatus(resp.SC_FORBIDDEN);
		}else{//recorrer comparando los atributos y cambiar los diferentes

			Query q = new Query("Energy").setFilter(new FilterPredicate("name",Query.FilterOperator.EQUAL, resource));
			PreparedQuery pq = datastore.prepare(q);
			Entity original = pq.asSingleEntity();
			
			datastore.delete(original.getKey());
			
			//guardar atributos en una lista
			List<String> atributos = new ArrayList<String>();
			atributos.add("name");
			atributos.add("no_fossil");
			atributos.add("fossil");
			atributos.add("temperature");
			
			for(String aux: atributos){
				if(!(e.getProperty(aux).equals(original.getProperty(aux)))){
					original.setProperty(aux, e.getProperty(aux));
				}
			}
			datastore.put(original);
		}
	}
	
	@SuppressWarnings("static-access")
	private void removeResource(HttpServletRequest req, HttpServletResponse resp, Entity e){
		System.out.println(e);
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
		
		if(e != null){
			entity.setProperty("name", e.name);
			entity.setProperty("no_fossil", e.no_fossil);
			entity.setProperty("fossil", e.fossil);
			entity.setProperty("temperature", e.temperature);
		}else{
			entity = null;
		}
		
		return entity;
	}
}
