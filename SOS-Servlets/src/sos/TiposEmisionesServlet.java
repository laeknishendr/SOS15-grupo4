package sos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class TiposEmisionesServlet extends HttpServlet {
	
	static HashMap<String, Emissions> ds = new HashMap<String, Emissions>();
	Emissions emissions = new Emissions("USA", 5.0, 5.0, 5.0);
	
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
		
		ds.put(emissions.name, emissions);
		
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
		
			case "POST": postEmissions(req, resp); break; 
		
			case "GET": getEmissions(req, resp); break;  
		
			case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;  
		
			case "DELETE": ds.clear(); break; 
		}
	}
	
	@SuppressWarnings("static-access")
	private void processResource(String method, String resource, HttpServletRequest req, 
			HttpServletResponse resp) throws IOException{
		
		if(method == "POST"){ 
			resp.setStatus(resp.SC_METHOD_NOT_ALLOWED); return;
		}
		
		if(!ds.containsKey(resource)){  
			resp.setStatus(resp.SC_NOT_FOUND); return;
		}
		
		switch(method){
		
			case "GET": getEmission(req, resp, resource); break; 
		
			case "PUT": updateEmissions(req, resp, resource); break; 
		
			case "DELETE": ds.remove(resource); break;
		}
	}
	
	@SuppressWarnings("static-access")
	private void postEmissions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Emissions e = extractEmissions(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(ds.containsKey(e.name)){
			resp.setStatus(resp.SC_CONFLICT);
		}else{
			ds.put(e.name, e);
		}
	}
	
	private void getEmissions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(ds.values());
		
		resp.getWriter().println(jsonString);
		
	}
	
	private Emissions extractEmissions(HttpServletRequest req) throws IOException{
		
		Emissions e = null;
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = req.getReader();
		String jsonString;
		
		while((jsonString = br.readLine()) != null){
			sb.append(jsonString);
		}
		jsonString = sb.toString();
		
		try{
			e = gson.fromJson(jsonString, Emissions.class);
		}catch(Exception em){
			System.out.println("Error parsin Emissions: "+em.getMessage());
		}
		
		return e;
	}
	
	@SuppressWarnings("static-access")
	private void updateEmissions(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Emissions e = extractEmissions(req);
		
		if(e == null){
			resp.setStatus(resp.SC_BAD_REQUEST);
		}else if(e.name != resource){
			resp.setStatus(resp.SC_FORBIDDEN);
		}else{
			ds.put(e.name, e);
		}
	}
	
	private void getEmission(HttpServletRequest req, HttpServletResponse resp, String resource) 
			throws IOException{
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(ds.get(resource));
		
		resp.getWriter().println(jsonString);
		
	}
}
