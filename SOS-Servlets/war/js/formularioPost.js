$(document).ready(function() {
	
	var dir = "api/v2/tiposenergias";
	
	$('#Añadir').click(function(){
	
		//POST
		var dato = [];
		
		//coger datos de los input
		dato.push([$('#country').val(), $('#no_fossil').val(), $('#fossil').val(), $('#temperature').val()]);
	
		//console.log(dato);
		//console.log(dato[0][0]);
		
		var data = '{' + '"name":' + '"' + dato[0][0] + '"' + ',' + '"no_fossil":' + dato[0][1] + ',' + '"fossil":' 
		+ dato[0][2] + ',' + '"temperature":' + dato[0][3] + '}';
		
		//console.log(data);
		
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