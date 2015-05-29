$(document).ready(function() {

	var table = $('#tablaConsumo').dataTable();
	
	var dir = "api/v2/consumos";
	
	//GET
	var aaData = [];

	$.ajax({
		url : dir,
		dataType : "json",
		success : function(data) {
			//console.log(data);
			for (i in data) {
				aaData.push([data[i].country, data[i].energy_production, data[i].energy_use, data[i].energy_import, data[i].year]);
				$('#consumo').append('<tr>');
				$('#tablaConsumo').dataTable().fnAddData([aaData[i]]);
				$('#consumo').append('</tr>');
			}
			//console.log(aaData);
		}

	});
	
	//DELETE
	
	$('#tablaConsumo tbody').on( 'click', 'tr', function () {
        if ($(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
        	table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    } );
    
	$("#Delete").click(function(){
		var seleccionado = $("tr.selected");
		var pais = seleccionado.find("td")[0].innerHTML;
		$.ajax({
		    url: dir + '/' + pais,
		    type: 'DELETE'
		}).done(function(data, status, jqXHR){
			window.location.reload();
		});	
    });
	
	//DELETE ALL
	
	$('#DeleteAll').click(function(){
		$.ajax({
			url: dir,
			type: "DELETE"
		}).done(function(data, status, jqXHR){
			window.location.reload();
		});
	});
		
});