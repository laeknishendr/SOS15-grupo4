google.load('visualization','1.0',{'packages':['corechart']});
		
		google.setOnLoadCallback(dibujar);
		
		function dibujar()
		{ 
			var request = $.ajax({
				url: "/api/v2/consumos", //http://sos-2015-04.appspot.com
				type: "GET",
				dataR: $("#payload").val()
			});
			
			request.done(function(dataR, status, jqXHR){
					
				var data = new google.visualization.DataTable();
				data.addColumn('string','country');
				data.addColumn('number','energy_use');
				
				var jsonArray = JSON.parse(dataR);
				
				var count;
				for (count=0;count<jsonArray.length;count++){
					var country = JSON.stringify(jsonArray[count].country);
					var energy_use = parseFloat(JSON.stringify(jsonArray[count].energy_use));
					data.addRows(
						[
							[country,energy_use]
						]
						);
					//console.log(name+" - "+temperature+"\n"); en funcion de donde esta me va imprimendo lo que quiero ver....//mas herramientas -> consola JavaScript -> console//
				}
				
				//style="align: center; width: 700px; height: 300px;"
				
				var opciones = {'title':'energy_use in country',
								'width': 800,
								'height':600,
								backgroundColor : {
									fill : 'transparent'
								}
								
				};
				var grafica = new google.visualization.GeoChart(document.getElementById('charts'));
				grafica.draw(data, opciones);
			});
		
		}