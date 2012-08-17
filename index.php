<!DOCTYPE html> 
<html> 
	<head> 
	<title>Page Title</title> 
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.1/jquery.mobile-1.1.1.min.css" />
	<script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.1.1/jquery.mobile-1.1.1.min.js"></script>
	<script src="./js_code.php"></script>

</head> 
<body>
	<?php 
	// TODO hacer una función que incluya todos los archivos existentes en el directorio pages
	include './pages/home_paradas.php' ;
	include './pages/parada.php' ;
	include './pages/acerca.php' ;
	include './pages/contribuir.php' ;

	?>
</body>
</html>