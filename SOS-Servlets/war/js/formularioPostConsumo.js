$(document).ready(function() {
	
	var dir = "api/v2/consumos";
	
	$('#Añadir').click(function(){
	
		//POST
		var dato = [];
		
		//coger datos de los input
		dato.push([$('#country').val(), $('#energy_production').val(), $('#energy_use').val(), $('#energy_import').val(), $('#year').val()]);
	
		console.log(dato);
		console.log(dato[0][0]);
		
		var data = '{' + '"country":' + '"' + dato[0][0] + '"' + ',' + '"energy_production":' + dato[0][1] + ',' + '"energy_use":' 
		+ dato[0][2] + ',' + '"energy_import":' + dato[0][3] + ',' + '"year":' + dato[0][4] + '}';
		
		console.log(data);
		
		//hacer llamada post
	
		$.ajax({
			method: "POST",
			url: dir,
			data: data,
			dataType: "text"
		}).done(function(data, status, jqXHR){
			alert('success');
		});
	});
});