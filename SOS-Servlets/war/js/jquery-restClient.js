/*$( document ).ready(function() {
	$("#button").on("click", function(){
		
		var statuscode = "{}";
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
		}).done(function( data, status, jqXHR ) {
			$( "#list" ).html( data );
			statuscode = jqXHR.status;
			$("#status").html(statuscode);
		});
		
	})
})*/

$("body").ready(function(){
	
	$("#button").click(function(){
		
		$("#log").text("Sending Request...");
		
		var method = $('input[type=radio]:checked').attr("id");
		
		var request = $.ajax({
			url: $("#url").val(),
			type: method,
			data: $("#payload").val()
		});
		
		request.done(function(data,status,jqXHR){
			if(data != ""){
				$("#data").text( data );
				var parsedData = $.parseJSON(data);
				$("#list").empty();
				if(Array.isArray(parsedData)){
					for(var i in parsedData){
						var dump = "";
						var obj = parsedData[i];
						for (var prop in obj){
							dump += "("+prop+"-"+obj[prop]+")";
						}
						$("#list").append("<li>"+dump+"</li>");
					}
				}else{
					var dump = "";
					var obj = parsedData;
					for(var prop in obj){
						dump += "("+prop+"="+obj[prop]+")";
					}
					$("#list").append("<li>"+dump+"</li>");
				}
			}else{
				$("#list").empty();
				$("#data").empty();
			}
		});
		
		request.always(function(jqXHR,status){
			if(status=="error"){
				$("#status").text(jqXHR.status);
				$("#list").empty();
				$("#data").empty();
			}else
				$("#status").text("200");
				$("#log").text("Done. ");
		});
	});
	
		$("#button").hover(function(){
			$(this).addClass("active");
		},function(){
			$(this).removeClass("active");
		});

});