<!DOCTYPE html> 
<html> 
	<head> 
		<?php include './parts/header.php'; ?>

	</head> 
<body>
	<!-- Página de inicio. Contiene el buscador de paradas y la lista de resultados -->
	<div id="home-paradas" data-role="page" data-dom-cache="true">
		<!-- el Javascript debe ir dentro del elemento 'page' para que se cargue al abrir la página de forma directa o dinámica -->
		<script src="./js/home_paradas.js"></script>

		<?php include './parts/home_header.php'; ?>

		<div data-role="content">	
			<p>Busca una parada para ver los tiempos de llegada:</p>
			<input id="search-parada" name="search-parada" placeholder="Número, nombre o dirección" value="" type="search" /> 
			<a id="search-parada-button" href="#" data-role="button" data-theme="b" data-icon="search" >Buscar</a>
			<ul id="lista-resultados" data-role="listview" data-divider-theme="d" data-inset="true" class="ui-listview-inset">
				<!-- Aquí van los resultados de la búsqueda. Vacío por defecto -->		
				<!-- <li data-role="list-divider" role="heading">Resultados</li>
				<li><a href="#parada">Parada 1</a></li>
				<li><a href="#parada">Parada 2</a></li>
				<li><a href="#parada">Parada n</a></li> -->
			</ul>
		</div><!-- /content -->

	</div>
</body>
</html>