$(document).ready(function() {
	
	var dir = "api/v2/tiposenergias";
	
	$('#Actualizar').click(function(){
		
		//PUT
		var dato = [];
		
		//coger datos de los input
		dato.push([$('#country').val(), $('#no_fossil').val(), $('#fossil').val(), $('#temperature').val()]);
	
		//console.log(dato);
		//console.log(dato[0][0]);
		
		var data = '{' + '"name":' + '"' + dato[0][0] + '"' + ',' + '"no_fossil":' + dato[0][1] + ',' + '"fossil":' 
		+ dato[0][2] + ',' + '"temperature":' + dato[0][3] + '}';
		
		//console.log(data);
		
		var direccion = dir + '/' + dato[0][0];
		
		//console.log(direccion);
		
		//hacer llamada put
	
		$.ajax({
			method: "PUT",
			url: direccion,
			data: data,
			dataType: "text"
		}).done(function(data, status, jqXHR){
			alert('success');
		});
		
	});
});