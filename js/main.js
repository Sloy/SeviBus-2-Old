/**
 * En este archivo va el código JavaScript general de toda la aplicación, como 
 * declaración de listeners al inicio.
 */

$(document).bind('pageinit',function(){
	// Listeners
	$("#search-parada-button").click(function(){
		buscarParada($("#search-parada").val());
	})
});

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

		//TODO: puede haber más de una página queriendo capturar el cambio de página. En tal caso habría que convertir esto en un switch
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