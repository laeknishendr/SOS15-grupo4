$(document).ready(
		function() {

			var dir = "/api/v2/emisiones";

			$('#Añadir').click(
					function() {

						// POST
						var dato = [];

						// coger datos de los input
						dato.push([ $('#country').val(),
								$('#CO2emissions').val(),
								$('#population').val(), $('#year').val() ]);

						// console.log(dato);
						// console.log(dato[0][0]);

						var data = '{' + '"country":' + '"' + dato[0][0] + '"'
								+ ',' + '"CO2emissions":' + dato[0][1] + ','
								+ '"population":' + dato[0][2] + ','
								+ '"year":' + dato[0][3] + '}';

						// console.log(data);

						// hacer llamada post

						$.ajax({
							method : "POST",
							url : dir,
							data : data,
							dataType : "text"
						}).done(function(data, status, jqXHR) {
							alert('success');
						});
					});
		});