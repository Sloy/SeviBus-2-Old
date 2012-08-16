<!-- Página de inicio. Contiene el buscador de paradas y la lista de resultados -->
<div id="home-paradas" data-role="page">

	<div data-role="header">
		<h1>SeviBus Web (alfa)</h1>
		<a href="#acerca" data-icon="info">Acerca de</a>
		<a href="#donar" >Donar</a>
		<div data-role="navbar" style="padding:0px">
		<ul>
			<li><a href="#home-paradas" class="ui-btn-active">Paradas</a></li>
			<li><a href="#home-lineas">Líneas</a></li>
			<li><a href="#home-mapa">Mapa</a></li>
		</ul>
	</div>
	</div><!-- /header -->

	<div data-role="content">	
		<p>Busca una parada para ver los tiempos de llegada:</p>
		<input id="search-parada" name="search-parada" placeholder="Número, nombre o dirección" value="" type="search" /> 
		<a id="search-parada-button" href="#" data-role="button" data-theme="b" data-icon="search" >Buscar</a>
	</div><!-- /content -->

	<ul id="lista-resultados" data-role="listview" data-divider-theme="d" data-inset="true" class="ui-listview-inset">
		<!-- Aquí van los resultados de la búsqueda. Vacío por defecto -->		
		<!-- <li data-role="list-divider" role="heading">Resultados</li>
		<li><a href="#parada">Parada 1</a></li>
		<li><a href="#parada">Parada 2</a></li>
		<li><a href="#parada">Parada n</a></li> -->
	</ul>

	
</div>