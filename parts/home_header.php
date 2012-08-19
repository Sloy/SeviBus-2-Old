<div data-role="header">
	<a href="acerca.php" data-icon="info" data-transition="flip">Acerca de</a>
	<h1>SeviBus Web (alfa)</h1>
	<a href="contribuir.php" data-rel="dialog" data-transition="slidedown">Contribuye</a>
	
	<?php 
	if(strstr($_SERVER['HTTP_USER_AGENT'], 'Android')){
		?>
		<div class="ui-bar ui-bar-c">
			<h3>Aplicación para Android <a href="https://play.google.com/store/apps/details?id=com.sloy.sevibus" data-role="button">Descargar</a></h3>
		</div>
		<?
	}
	?>

	<div data-role="navbar" style="padding:0px">
		<ul>
			<li><a href="home_paradas.php" class="ui-btn-active">Paradas</a></li>
			<li><a href="home_lineas.php">Líneas</a></li>
			<li><a href="home_mapa.php">Mapa</a></li>
		</ul>
	</div>
	
</div><!-- /header -->