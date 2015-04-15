$(document).ready(function(){
	$("#button").on("click", function(){
		var cadena ="";
		var dir = $("#url").val();   //coge la url
		var datos = "";
		var metodo = $(this).val();    //coge el tipo de metodo
		
		console.log("dir: "+dir);			      
		console.log("datos1: "+datos);
		console.log("metodo: "+metodo);
		
		//si es un metodo POST o un PUT guardamos lo que haya en payload
		if(metodo=="PUT" || metodo=="POST"){		       
			datos=$("#payload").val();
			console.log("datos2: "+datos);
		}
		
		//peticion ajax
		var request = $.ajax({					
			url: dir,
			dataType : "json",
			type : metodo,
			contentType : "application/json",
			data: datos
		});

		request.done(function(data, status, jqXHR){
			if(metodo=="GET"){
				$.each(data, function(index, data){					
					cadena += "<li> <b>Country:</b> "+ data.name+" ; <b>No_fossil:</b> "+ data.no_fossil +" ; <b>Fossil:</b> "+ data.fossil+" ; <b>Temperature:</b> "+ data.temperature+"</li>";
				});	
				$("#list").html(cadena);
			}else{
				$("#list").html("<li></li>");
			}
			console.log(jqXHR.status);
		});

		request.fail(function(jqXHR,status) {
				$("#data").text("ERROR");
				$("#status").text(jqXHR.status + " " +jqXHR.statusText);
				$("#list").text("");
		});
	})
})