$(document).ready(function() {

	var table = $('#tablaEmisiones').dataTable();
	
	var dir = "/api/v2/emisiones";
	
	//GET
	var aaData = [];

	$.ajax({
		url : dir,
		dataType : "json",
		success : function(data) {
			//console.log(data);
			for (i in data) {
				aaData.push([data[i].country, data[i].CO2emissions, data[i].population, data[i].year]);
				$('#emisiones').append('<tr>');
				$('#tablaEmisiones').dataTable().fnAddData([aaData[i]]);
				$('#emisiones').append('</tr>');
			}
			//console.log(aaData);
		}

	});
	
	//DELETE
	
	$('#tablaEmisiones tbody').on( 'click', 'tr', function () {
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