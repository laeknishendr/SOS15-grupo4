package sos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class EmisionesServlet extends HttpServlet {
	
	static HashMap<String, Emission> persistance = new HashMap<String, Emission>();
 
	
	
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
	@SuppressWarnings("static-access")

	public void processResourceList(HttpServletRequest req, HttpServletResponse resp, String method) 
			throws IOException{
		
		switch(method){
		
		case "GET": getEmissions(req, resp); break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break; 
		
		case "POST": postEmissions(req, resp);break; 
		
		case "DELETE": persistance.clear();  break;//se elimina todo el contenido del mapa!
		
		
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
	
	
	@SuppressWarnings("static-access")
	public void processResource(HttpServletRequest req, HttpServletResponse resp, String method, String resource) 
			throws IOException{
			
			if(!persistance.containsKey(resource)) {
				resp.setStatus(resp.SC_NOT_FOUND); return;
				
			}
			Emission em = new Emission("Spain", 9999.99, 10, 2033); 
			persistance.put(em.country, em);						
			
			switch(method){
			
			case "GET": getEmission(req, resp, resource); break;
			
			case "PUT": updateEmission(req, resp, resource); break; 
			
			case "POST": resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"); break;
			
			
			
			case "DELETE": persistance.remove(resource); break;//se elimina únicamente el resource del mapa
			
			
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
			
			Emission emission = extractEmission(req);
			if(emission == null){
				resp.sendError(400);
			}else if (emission.country != resource){
				resp.sendError(403); 
			}else{
			persistance.put(emission.country, emission); 
			}
		
	}
	
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
	
	
	
}
