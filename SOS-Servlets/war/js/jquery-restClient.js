$( document ).ready(function() {
	$("#button").on("click", function(){

		var dir = $("#url").val();   //coge la url
		var datos = "{}";
		var metodo = $('input[name=method]:checked').val();    //coge el tipo de metodo
		
		console.log("dir: "+dir);			      
		console.log("datos1: "+datos);
		console.log("metodo: "+metodo);
		
		//si es un metodo POST o un PUT guardamos lo que haya en payload
		if(metodo=="PUT" || metodo=="POST"){		       
			datos=$("#payload").val();
			console.log("datos2: "+datos);
		}
		
		$.ajax({
			method: metodo,
			url: dir,
			data: datos
		}).done(function( data ) {
			$( "#list" ).html( data );
		});
	})
})