$(document).bind('pageinit',function(){
	// Listeners
	$("#search-parada-button").click(function(){
		buscarParada($("#search-parada").val());
	})
});

/** Hace una búsqueda de paradas vía ajax y muestra los resultados en la página */
function buscarParada(query){
	//Hace la búsqueda con la API
	$.ajax({
		url:"./api/paradas/buscar/"+query,
		type:"GET",
		dataType: "json",
		success: function(json){
			//TODO: respuesta válida
			muestraResultados(json);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("erró: " + errorThrown);
		}
	});
}

/** Muestra los resultados de la búsqueda en una lista */
function muestraResultados(res){
	var lista = $("#lista-resultados");
	lista.empty();
	lista.append('<li data-role="list-divider" role="heading">Resultados</li>')
	$.each(res.resultados,function(){
		lista.append('<li><a href="#parada?n='+this.numero+'"> Parada nº'+this.numero+'</a></li>')
	});
	lista.listview("refresh");

}

/* 	Código copiado y pegado de la documentación de jQueryMobile.
	Necesita refactorizar */
// Listen for any attempts to call changePage().
$(document).bind( "pagebeforechange", function( e, data ) {

	// We only want to handle changePage() calls where the caller is
	// asking us to load a page by URL.
	if ( typeof data.toPage === "string" ) {

		// We are being asked to load a page by URL, but we only
		// want to handle URLs that request the data for a specific
		// category.
		var u = $.mobile.path.parseUrl( data.toPage ),
			re = /^#parada/;

		if ( u.hash.search(re) !== -1 ) {

			// We're being asked to display the items for a specific category.
			// Call our internal method that builds the content for the category
			// on the fly based on our in-memory category data structure.
			showParada( u, data.options );

			// Make sure to tell changePage() we've handled this call so it doesn't
			// have to do anything.
			e.preventDefault();
		}
	}
});

/* 	Código copiado y pegado de la documentación de jQueryMobile.
	Necesita refactorizar */
// Load the data for a specific category, based on
// the URL passed in. Generate markup for the items in the
// category, inject it into an embedded page, and then make
// that page the current active page.
function showParada( urlObj, options )
{
	var categoryName = urlObj.hash.replace( /.*n=/, "" ),

		// The pages we use to display our content are already in
		// the DOM. The id of the page we are going to write our
		// content into is specified in the hash before the '?'.
		pageSelector = urlObj.hash.replace( /\?.*$/, "" );

	// Get the object that represents the category we
	// are interested in. Note, that at this point we could
	// instead fire off an ajax request to fetch the data, but
	// for the purposes of this sample, it's already in memory.
	// var category = categoryData[ categoryName ],


	$.ajax({
		url:"./api/paradas/"+categoryName,
		type:"GET",
		dataType: "json",
		success: function(json){
			//Una vez cargada la parada...
			var $page = $(pageSelector),
				// Get the header for the page.
				$header = $page.children( ":jqmData(role=header)" ),
				// Get the content area element for the page.
				$content = $page.children( ":jqmData(role=content)" );


				// Find the h1 element in our header and inject the name of
				// the category into it.
				$header.find("h1").html("Parada "+json.numero);

			// Inject the category items markup into the content element.
			$content.html("<p>Éste es el contenido de la parada número "+json.numero+", "+json.nombre+"</p1>");

			// Pages are lazily enhanced. We call page() on the page
			// element to make sure it is always enhanced before we
			// attempt to enhance the listview markup we just injected.
			// Subsequent calls to page() are ignored since a page/widget
			// can only be enhanced once.
			$page.page();

			// We don't want the data-url of the page we just modified
			// to be the url that shows up in the browser's location field,
			// so set the dataUrl option to the URL for the category
			// we just loaded.
			options.dataUrl = urlObj.href;

			// Now call changePage() and tell it to switch to
			// the page we just modified.
			$.mobile.changePage( $page, options );

		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("erró: " + errorThrown);
		}
	});	
}