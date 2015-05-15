$(document).ready(function() {

	var aaData = [];
	//GET
	$.ajax({
		url : "api/v1/tiposenergias",
		dataType : "json",
		success : function(data) {
			console.log(data);
			for (i in data) {
				aaData.push([data[i].name, data[i].no_fossil, data[i].fossil, data[i].temperature]);
			}
			console.log(aaData);
			$("#tablaEnergia").dataTable({
				"aaData" : aaData,
				"bJQueryUI" : true,
				"aoColumns" : [ {
					"sTitle" : "name",
					"sWidth" : "200px"
				}, {
					"sTitle" : "no_fossil",
					"sWidth" : "200px"
				}, {
					"sTitle" : "fossil",
					"sWidth" : "200px"
				}, {
					"sTitle" : "temperature",
					"sWidth" : "200px"
				} ]

			})

		}

	})
	
	//DELETE
	
	
});

/*
 * $(document).ready(function() { var t = $('#tablaEnergia').dataTable();
 * 
 * //hacer el GET aqui
 * 
 * var request = $.ajax({ url: "/api/v1/tiposenergias", type: "GET", datatype:
 * "JSON", asyn: false });
 * 
 * request.done(function(){ var indice = 0; $each(data, function(){
 * $('#energia').append('<tr>');
 * $('#tablaEnergia').datatable().fnAddData([data[i].name, data[i].no_fossil,
 * data[i].fossil, data[i].year]); $('#energia').append('</tr>'); indice++;
 * }); }); /* $("#Post").click(function(){
 * 
 * 
 * 
 * });
 * 
 * $("#Put").click(function(){
 * 
 * 
 * 
 * });
 * 
 * $("#Delete").click(function(){
 * 
 * 
 * 
 * });
 * 
 * $("#DeleteAll").click(function(){
 * 
 * 
 * 
 * }); } );
 * 
 */
/*
 * tiene que hacer:
 * 
 * -un GET automatico, nada mas entrar en la pagina -al pulsar un boton_
 * 
 * -un POST -un PUT -un DELETE -un DELETE ALL
 * 
 * hacerlo con llamada AJAX
 */