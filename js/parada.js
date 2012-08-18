/**
 * En este archivo va el código JavaScript específico de la página 'parada'
 */

function obtenerTiempos(parada){
	var lineas = parada.lineas;
	// Lanza una petición por cada línea cargada
	$.each(lineas,function(){
		// Pide el tiempo de llegada para esta parada y esta línea
		$.ajax({
			url:"./api/tiempo/"+parada.numero+"/"+this.nombre,
			type:"GET",
			dataType: "json",
			success: function(json){
				// Comprueba que esté bien
				if(!json.error){
					// Coloca el tiempo en su lugar correspondiente 
					$("#llegada-"+json.linea).html(json.bus1.tiempo+" minutos | "+json.bus2.tiempo+" minutos");
				}else{
					$("#llegada-"+json.linea).html(json.error);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("erró: " + errorThrown);
			}
		});	
	});//each
}
