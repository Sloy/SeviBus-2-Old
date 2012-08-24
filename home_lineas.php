<?
require_once './php/DAO.php';
require_once './php/BDConexion.php';
require_once './php/model/Linea.php';

$tab = 2;

$dao = new DAO();
$lineas = $dao->getLineas();
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
			<ul data-role="listview" data-inset="true">
				<?
				foreach($lineas as $linea){
					echo '<li><a href="linea.php?n='.$linea->nombre.'">'.$linea->nombre.' | '.$linea->trayecto.'</a></li>';
				}
				?>
			</ul>
		</div><!-- /content -->

	</div>
</body>
</html>