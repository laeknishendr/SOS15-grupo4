$( document ).ready(function() {
	$("#button").on("click", function(){
		var cadena ="";
		var dir = $("#url").val();   //coge la url
		var datos = "{}";
		var metodo = $(this).val();    //coge el tipo de metodo
		
		console.log("dir: "+dir);			      
		console.log("datos1: "+datos);
		console.log("metodo: "+metodo);
		
		//si es un metodo POST o un PUT guardamos lo que haya en payload
		if(metodo=="PUT" || metodo=="POST"){		       
			datos=$("#payload").val();
			console.log("datos2: "+datos);
		}
		
		$.get(dir, function( data ) {
		  $( "#list" ).html( data );
		});
		var jqxhr = $.get(dir, function() {
			alert( "success" );
		}).done(function(data, status) {
		    alert( "second success" );
		}).fail(function(data, status) {
		    alert( "error" );
		}).always(function() {
		    alert( "finished" );
		});
	})
})