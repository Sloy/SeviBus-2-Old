/**
 * En este archivo va el código JavaScript específico de la página 'home_paradas'
 */


 $(document).bind('pageinit',function(){
	// Listeners de búsqueda
	$("#search-parada-button").click(function(){
		buscarParada($("#search-parada").val());
	})
});


 /** 
 * Hace una búsqueda de paradas vía ajax y muestra los resultados en la página 
 */
function buscarParada(query){
	//Hace la búsqueda con la API
	$.ajax({
		url:"./api/paradas/buscar/"+query,
		type:"GET",
		dataType: "json",
		success: function(res){
			//TODO: validar respuesta
			/** Muestra los resultados de la búsqueda en una lista */
			var $lista = $("#lista-resultados");
			$lista.empty();
			$lista.append('<li data-role="list-divider" role="heading">Resultados</li>')
			$.each(res.resultados,function(){
				$lista.append('<li><a href="parada.php?n='+this.numero+'"><h1>Parada nº '+this.numero+'</h1><p>'+this.nombre+'</p></a></li>')
			});
			$lista.listview("refresh");
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("erró: " + errorThrown);
		}
	});
}