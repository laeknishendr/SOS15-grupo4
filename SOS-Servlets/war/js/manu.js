google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

google.setOnLoadCallback(dibujar);

function dibujar() {
	var request = $.ajax({
		url : "api/v2/manuProxyServlet", // http://sos-2015-04.appspot.com
		type : "GET",
		dataR : $("").val()
	// #payload --> en este caso al usar el "GET" no nos hace falta definirlo.
	});

	request.done(function(dataR, status, jqXHR) {

		var data = new google.visualization.DataTable();
		data.addColumn('string', 'city');
		data.addColumn('number', 'year');

		var jsonArray = JSON.parse(dataR);

		var count;
		for (count = 0; count < jsonArray.length; count++) {
			var city = JSON.stringify(jsonArray[count].city);
			var year = parseFloat(JSON.stringify(jsonArray[count].year));
			data.addRows([ [ city, year ] ]);
			// console.log(name+" - "+temperature+"\n"); en funcion de donde
			// esta me va imprimendo lo que quiero ver....//mas herramientas ->
			// consola JavaScript -> console//
		}

		var opciones = {
			'title' : 'Estudiantes',
			'width' : 800,
			'height' : 600,
			backgroundColor : {
				fill : 'transparent'
			}
		};
		var grafica = new google.visualization.BarChart(document
				.getElementById('charts'));
		grafica.draw(data, opciones);
	});

}