

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
public class ConsumosEnergiasServlet extends HttpServlet {
	
	static HashMap<String, Consumos> ds = new HashMap<String, Consumos>();
 
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		process(req, resp);
	}
	
	public void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*Este método, que utilizaremos como método por
		 * defecto para todos los comandos, tendremos que
		 * pillar el contenido de la URL, el método
		 * por el que se ha accedido y generar una respuesta.
		 *
		 * */
		Consumos em = new Consumos("USA", 1685852.5, 2164458.4, 370887.55, 2009); 
		ds.put(em.country, em);	
		
		PrintWriter out = resp.getWriter(); 
		String path = req.getPathInfo(); 
		String method = req.getMethod(); 
		
	 
		
		System.out.println(req.getRequestURI()+":["+method+"|"+path+"]");
		
		if(path != null){
			
			/* En caso de que el path no sea nulo, accedemos al 
			 * recurso en concreto. Aquí podremos hacer GET, POST y DELETE.  
			 * 
			 * */
			
			String[] pathComponents = path.split("/"); 
			String resource = pathComponents[1];
			
			System.out.println("Single action over resource'"+resource+"'");
			
			processResource(method, pathComponents[1], req, resp ); 
		}
		
		else{
			System.out.println("Action over the list of resources");
			processResourceList(method, req, resp );
		}
		
		out.close();
	}
	@SuppressWarnings("static-access")

	private void processResourceList(String method, HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		switch(method){
		
		case "GET": getConsumos(req, resp); break;
		
		case "PUT": resp.sendError(resp.SC_METHOD_NOT_ALLOWED);break;
		
		case "POST": postConsumos(req, resp);break; 
		
		case "DELETE": ds.clear();  break;//se elimina todo el contenido del mapa!
		
		
		}
	}
	
	private void getConsumos(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		//devolver todo el mapa
		Gson gson = new Gson(); 
		resp.getWriter().println(gson.toString());
		String consumosJson = gson.toJson(ds.values()); 
		resp.getWriter().println(consumosJson);
		resp.getWriter().println(ds.toString()); 
		
	}
	
	@SuppressWarnings("static-access")
	private void postConsumos(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		Consumos con = extractConsumos(req); 
		
		if(ds.containsKey(con)){
			resp.setStatus(resp.SC_CONFLICT);
		}else if (con == null){
			resp.setStatus(resp.SC_BAD_REQUEST); 
			resp.getWriter().println("no me vale");
		}else{
			ds.put(con.country, con); 
		}
		
	}
	
	
	private void processResource(String method, String resource, HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		
		if(method == "POST"){
			resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
			
		
		if(!ds.containsKey(resp)) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND); 
			return; 
				
		}
								
			
		switch(method){
			
		case "GET": getConsumo(resource, req, resp); break;
			
		case "PUT": updateConsumos(resource, req, resp); break; 
			
		case "DELETE": ds.remove(resource); break;//se elimina únicamente el resource del mapa
			
			
		}
	}
	/*
	private void getConsumos(String resource, HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Gson gson = new Gson(); 
		String jsonString = gson.toJson(ds.get(resource)); 
		resp.getWriter().println(jsonString);
		
	}
*/

	
	private void updateConsumos(String resource, HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
			
		Consumos consumo = extractConsumos(req);
			if(consumo == null){
				resp.sendError(400);
			}else if (consumo.country != resource){
				resp.sendError(403); 
			}else{
			ds.put(consumo.country, consumo); 
			}
		
	}
	
	private Consumos extractConsumos(HttpServletRequest req) throws IOException{
		Consumos e = null; 
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
			e = gson.fromJson(jsonString, Consumos.class); 
			System.out.println("Consumos extracted: "+e+" (name = '"+e.country+", "+e.year+"')");
		}catch(Exception ex){
			System.out.println("ERROR parsing Consumos: ");
				System.out.println("ERROR parsing Emission: " + ex.getMessage()); 
			}

		return e;
		
	}
	
	private void getConsumo(String resource, HttpServletRequest req, HttpServletResponse resp
			) throws IOException{
		Gson gson = new Gson(); 
		String gsonString = gson.toJson(resource); 
		resp.getWriter().println(gsonString);
		
	}
	
	
	
}	