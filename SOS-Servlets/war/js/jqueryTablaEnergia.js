$(document).ready(function() {

	var table = $('#tablaEnergia').dataTable();
	
	var dir = "api/v2/tiposenergias";
	
	var uri = "api/v2/servletCSVEnergia";
	
	//GET
	var aaData = [];

	$.ajax({
		url : dir,
		dataType : "json",
		success : function(data) {
			//console.log(data);
			for (i in data) {
				aaData.push([data[i].name, data[i].no_fossil, data[i].fossil, data[i].temperature]);
				$('#energia').append('<tr>');
				$('#tablaEnergia').dataTable().fnAddData([aaData[i]]);
				$('#energia').append('</tr>');
			}
			//console.log(aaData);
		}

	});
	
	//DELETE
	
	$('#tablaEnergia tbody').on( 'click', 'tr', function () {
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
		
	//CSV
	
	$('#CSV').click(function(){
		$.ajax({
			url: uri,
			type:"GET"
		}).done(function(data, status, jqXHR){
			window.location.reload();
		});
	});
	
});
