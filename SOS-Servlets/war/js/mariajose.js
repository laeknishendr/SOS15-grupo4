google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

google.setOnLoadCallback(dibujar);

function dibujar() {
	var request = $.ajax({
		url : "api/v2/mariajoseProxyServlet", // http://sos-2015-04.appspot.com
		type : "GET",
		dataR : $("").val()
	// #payload --> en este caso al usar el "GET" no nos hace falta definirlo.
	});

	request.done(function(dataR, status, jqXHR) {

		var data = new google.visualization.DataTable();
		data.addColumn('string', 'name');
		data.addColumn('number', 'total_population');

		// console.log(dataR);

		var aux = JSON.parse(dataR);

		// console.log(aux);

		var jsonArray = aux.results;

		// console.log(jsonArray);

		var count;
		for (count = 0; count < jsonArray.length; count++) {
			var name = JSON.stringify(jsonArray[count].name);
			var total_population = parseFloat(JSON
					.stringify(jsonArray[count].total_population));
			data.addRows([ [ name, total_population ] ]);
			// console.log(name+" - "+temperature+"\n"); en funcion de donde
			// esta me va imprimendo lo que quiero ver....//mas herramientas ->
			// consola JavaScript -> console//
		}

		var opciones = {
			'title' : 'public accounts',
			'width' : 1600,
			'height' : 1200,
			backgroundColor : {
				fill : 'transparent'
			}
		};
		var grafica = new google.visualization.BarChart(document
				.getElementById('charts'));
		grafica.draw(data, opciones);
	});

}