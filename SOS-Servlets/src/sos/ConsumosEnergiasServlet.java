package sos;

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

@
SuppressWarnings("serial")
public class ConsumosEnergiasServlet extends HttpServlet {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        process(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        process(req, resp);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        process(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        process(req, resp);


    }

    public void process(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();
        String method = req.getMethod();

        if (path != null) {
            String[] pathComponents = path.split("/");
            String resource = "";
			if(pathComponents.length>=2){
				resource = pathComponents[1];
			}
			
			processResource(method, resource, req, resp);
        } else {

            processResourceList(method, req, resp);
        }

        out.close();
    }

    @
    SuppressWarnings("static-access")
    private void processResourceList(String method, HttpServletRequest req,
        HttpServletResponse resp) throws IOException {

        switch (method) {

            case "GET":
                getConsumos(req, resp);
                break;

            case "PUT":
                resp.setStatus(resp.SC_METHOD_NOT_ALLOWED);
                break;
            case "POST":
                postConsumos(req, resp);
                break;

            case "DELETE":
                removeList(req, resp);
                break;


        }

        System.out.println("processLIst");
    }

    @
    SuppressWarnings({
        "static-access"
    })
    private void postConsumos(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {

        Entity e = extractEntity(req);

        if (e == null) {
            resp.setStatus(resp.SC_BAD_REQUEST);
        } else {
            Query q = new Query("Consumos").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, e.getProperty("country")));
            PreparedQuery pq = datastore.prepare(q);
            Entity en = pq.asSingleEntity();

            if (en != null) {
                resp.setStatus(resp.SC_CONFLICT);
            } else {

                datastore.put(e);
            }
        }
    }

    private void getConsumos(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Gson gson = new Gson();
        List < String > jsonString = new ArrayList < String > (); //gson.toJson(ds.values());

        Query q = new Query("Consumos");
        PreparedQuery pq = datastore.prepare(q);
        Iterator < Entity > it = pq.asIterator();

        while (it.hasNext()) {

            Entity aux1 = it.next();

            Consumos en = new Consumos((String) aux1.getProperty("country"), (Double) aux1.getProperty("energy_production"), (Double) aux1.getProperty("energy_use"), (Double) aux1.getProperty("energy_import"), (Long) aux1.getProperty("year"));
            String aux2 = gson.toJson(en);

            jsonString.add(aux2);
        }

        resp.getWriter().println(jsonString);

    }


    private void removeList(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Query q = new Query("Consumos");
        PreparedQuery pq = datastore.prepare(q);
        Iterator < Entity > it = pq.asIterator();

        while (it.hasNext()) {
            Entity e = it.next();
            datastore.delete(e.getKey());
        }

    }

    private void processResource(String method, String resource,
        HttpServletRequest req, HttpServletResponse resp)
    throws IOException {


        Query q = new Query("Consumos").setFilter(new FilterPredicate(
            "country", Query.FilterOperator.EQUAL, resource));
        PreparedQuery pq = datastore.prepare(q);

        Entity e = pq.asSingleEntity();

        if (method == "POST") {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if (q == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (method) {

            case "GET":
                getConsumo(req, resp, e);
                break;

            case "PUT":
                updateConsumos(resource, req, resp);
                break;

            case "DELETE":
                removeResource(req, resp, e); //datastore.delete(extractEntity(req).getKey()); break;
        }

    }

    @
    SuppressWarnings("static-access")
    private void getConsumo(HttpServletRequest req, HttpServletResponse resp, Entity e) throws IOException {

        Gson gson = new Gson();
        String jsonString = null;

        if (e == null) {
            resp.setStatus(resp.SC_NOT_FOUND);
            return;
        } else {
            Consumos c = new Consumos((String) e.getProperty("country"), (double) e.getProperty("energy_production"), (double) e.getProperty("energy_use"), (double) e.getProperty("energy_import"), (Long) e.getProperty("year"));

            jsonString = gson.toJson(c);
        }


        resp.getWriter().println(jsonString);
    }


    @
    SuppressWarnings("static-access")
    private void updateConsumos(String resource, HttpServletRequest req,
        HttpServletResponse resp) throws IOException {

        Entity e = extractEntity(req);

        if (e == null) {
            resp.setStatus(resp.SC_BAD_REQUEST);
        } else if (e.getProperty("country") != resource) {
            resp.setStatus(resp.SC_FORBIDDEN);
        } else {
            Query q = new Query("Consumos").setFilter(new FilterPredicate("country", Query.FilterOperator.EQUAL, resource));
            PreparedQuery pq = datastore.prepare(q);
            Entity original = pq.asSingleEntity();

            datastore.delete(original.getKey());

            //guardar atributos en una lista
            List < String > atributos = new ArrayList < String > ();
            atributos.add("country");
            atributos.add("energy_production");
            atributos.add("energy_use");
            atributos.add("energy_import");
            atributos.add("year");

            for (String aux: atributos) {
                if (!(e.getProperty(aux).equals(original.getProperty(aux)))) {
                    original.setProperty(aux, e.getProperty(aux));
                }
            }
            datastore.put(original);
        }
    }

    @
    SuppressWarnings("static-access")
    private void removeResource(HttpServletRequest req, HttpServletResponse resp, Entity e) {

        if (e == null) {
            resp.setStatus(resp.SC_NOT_FOUND);
            return;
        } else {
            datastore.delete(e.getKey());
        }
    }

    private Consumos extractConsumos(HttpServletRequest req) throws IOException {
        Consumos e = null;
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String jsonString;

        while ((jsonString = br.readLine()) != null) {
            sb.append(jsonString);

        }
        jsonString = sb.toString();

        try {
            e = gson.fromJson(jsonString, Consumos.class);
        } catch (Exception em) {
            System.out.println("Error parsin Emissions: " + em.getMessage());
        }

        return e;

    }


    private Entity extractEntity(HttpServletRequest req) throws IOException {

        Consumos e = this.extractConsumos(req);
        Entity entity = new Entity("Consumos");

        if (e != null) {
            entity.setProperty("country", e.country);
            entity.setProperty("energy_production", e.energy_production);
            entity.setProperty("energy_use", e.energy_use);
            entity.setProperty("energy_import", e.energy_import);
            entity.setProperty("year", e.year);

        } else {
            entity = null;
        }

        return entity;
    }




}