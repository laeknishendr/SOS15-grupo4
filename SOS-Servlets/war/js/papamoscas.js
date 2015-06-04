google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

google.setOnLoadCallback(dibujar);

function dibujar() {
	var request = $.ajax({
		url : "/api/v2/papamoscas", // http://sos-2015-04.appspot.com
		type : "GET",
		dataR : $("").val()
	// #payload --> en este caso al usar el "GET" no nos hace falta definirlo.
	});

	request.done(function(dataR, status, jqXHR) {

		var data = new google.visualization.DataTable();
		data.addColumn('string', 'specie');
		data.addColumn('number', 'hatches');

		var jsonArray = JSON.parse(dataR);

		var count;
		for (count = 0; count < jsonArray.length; count++) {
			var specie = JSON.stringify(jsonArray[count].specie);
			var hatches = parseFloat(JSON.stringify(jsonArray[count].hatches));
			data.addRows([ [ specie, hatches ] ]);
			// console.log(name+" - "+temperature+"\n"); en funcion de donde
			// esta me va imprimendo lo que quiero ver....//mas herramientas ->
			// consola JavaScript -> console//
		}

		var opciones = {
			'title' : 'Papamoscas',
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