google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

google.setOnLoadCallback(dibujar);

function dibujar() {
	var request = $.ajax({
		url : "/api/v2/tiposenergias", // http://sos-2015-04.appspot.com
		type : "GET",
		dataR : $("#payload").val()
	});

	request.done(function(dataR, status, jqXHR) {

		var data = new google.visualization.DataTable();
		data.addColumn('string', 'name');
		data.addColumn('number', 'fossil');

		var jsonArray = JSON.parse(dataR);

		var count;
		for (count = 0; count < jsonArray.length; count++) {
			var name = JSON.stringify(jsonArray[count].name);
			var fossil = parseFloat(JSON.stringify(jsonArray[count].fossil));
			data.addRows([ [ name, fossil ] ]);
			// console.log(name+" - "+temperature+"\n"); en funcion de donde
			// esta me va imprimendo lo que quiero ver....//mas herramientas ->
			// consola JavaScript -> console//
		}

		var opciones = {
			'title' : 'fossil in Country',
			'width' : 800,
			'height' : 600
		};
		var grafica = new google.visualization.PieChart(document
				.getElementById('charts'));
		grafica.draw(data, opciones);
	});

}