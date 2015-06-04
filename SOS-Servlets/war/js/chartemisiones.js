google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

google.setOnLoadCallback(dibujar);

function dibujar() {
	var request = $.ajax({
		url : "/api/v2/emisiones", // http://sos-2015-04.appspot.com
		type : "GET",
		dataR : $("#payload").val()
	});

	request.done(function(dataR, status, jqXHR) {

		var data = new google.visualization.DataTable();
		data.addColumn('string', 'country');
		data.addColumn('number', 'CO2emissions');

		var jsonArray = JSON.parse(dataR);

		var count;
		for (count = 0; count < jsonArray.length; count++) {
			var country = JSON.stringify(jsonArray[count].country);
			var CO2emissions = parseFloat(JSON
					.stringify(jsonArray[count].CO2emissions));
			data.addRows([ [ country, CO2emissions ] ]);
			// console.log(name+" - "+temperature+"\n"); en funcion de donde
			// esta me va imprimendo lo que quiero ver....//mas herramientas ->
			// consola JavaScript -> console//
		}

		var opciones = {
			'title' : 'CO2emissions in country',
			'width' : 800,
			'height' : 600
		};
		var grafica = new google.visualization.BarChart(document
				.getElementById('charts'));
		grafica.draw(data, opciones);
	});

}