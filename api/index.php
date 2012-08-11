<?php
require_once './Slim/Slim.php';
require_once '../php/DAO.php';
require_once '../php/BDConexion.php';
require_once '../php/model/Parada.php';
require_once '../php/model/Linea.php';
require_once '../php/model/Llegada.php';

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

/**
 * --- Hace la petición de tiempo al servidor de Tussam ---
 * El método original es SOAP, pero lo hago mediante Http y parseando el resultado a mano.
 * 
 * TODO: Control de errores!! Madre del amó hermozo... -.-'
 */
$app->get('/tiempo/:parada/:linea',function($parada, $linea){

	// Construye la petición según mandaría SOAP
	$soap_str = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetPasoParada xmlns=\"http://tempuri.org/\"><linea>$linea</linea><parada>$parada</parada><status>1</status></GetPasoParada></soap:Body></soap:Envelope>";
	$r = new HttpRequest("http://www.infobustussam.com:9001/services/dinamica.asmx",HttpRequest::METH_POST);
	$r->addHeaders(array(
		'Content-Type' => 'text/xml'
	));
	$r->setBody($soap_str);

	// Y la ejecuta. Fire!
	$res= $r->send()->getBody();


	// Transforma la respuesta para evitar problemas de incompatibilidad
	$res = str_replace("soap:", "", $res);
	// Create the XML
	$envelope = simplexml_load_string($res);

	// Obtiene el 'PasoParada', que contiene las llegadas de los 2 buses
	$PasoParada = $envelope->Body->GetPasoParadaResponse->GetPasoParadaResult->PasoParada;

	// Crea el primer bus, tiempo y distancia
	// Casting para que devuelva el texto y no un objeto SimpleXMLElement
	$e1 = $PasoParada->e1;
	$bus1 = new Bus((string)$e1->minutos,(string) $e1->metros);

	// Lo mismo con el segundo
	$e2 = $PasoParada->e2;
	$bus2 = new Bus((string)$e2->minutos, (string)$e2->metros);

	// Constuye el objeto llegada, fasi fasi
	$llegada = new Llegada((string)$PasoParada->linea, (string)$PasoParada->parada, $bus1, $bus2);

	// Listo, muestra la llegada
	echo json_encode($llegada);

});

// 4. Run!
$app->run();
?>