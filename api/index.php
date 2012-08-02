<?php
require 'Slim/Slim.php';
require '../php/DAO.php';
require '../php/BDConexion.php';
require '../php/model/Parada.php';
require '../php/model/Linea.php';

// 2. Initialize
$app = new Slim();


// 3. Define routes
$app->get('/hello/:name',function($name){
	echo "Hola, ".$name;
});

$app->get('/paradas//',function(){
	$dao = new DAO();
	echo json_encode($dao->getTodasParadas());
});

$app->get('/paradas/:numero',function($numero){
	$dao = new DAO();
	echo json_encode($dao->getParada($numero));
});


$app->get('/paradas/buscar/:query',function($query){
	$dao = new DAO();
	echo '{"resultados":'.json_encode($dao->buscarParadas($query))."}";
});

// 4. Run!
$app->run();
?>