<?
$tab = 2;
?>
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
			<ul data-role="listview" class="ui-listview-inset">
				<li><a href="#parada">Parada 1</a></li>
				<li><a href="#parada">Parada 2</a></li>
				<li><a href="#parada">Parada n</a></li> 
			</ul>
		</div><!-- /content -->

	</div>
</body>
</html>