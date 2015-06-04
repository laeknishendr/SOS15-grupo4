package sos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class mariajoseProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 7329647817610291333L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			URL myURL = new URL("http://transparenciadecuentaspublicas.es/api/1.0/territories/?page=1&format=json");
			BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				// System.out.println(inputLine);
				resp.getWriter().append(inputLine);
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
