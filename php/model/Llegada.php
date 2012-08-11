<?php

class Bus{
	var $tiempo;
	var $distancia;

	function __construct($tiempo, $distancia){
		$this->tiempo = $tiempo;
		$this->distancia = $distancia;
	}
}
class Llegada{

	var $linea;
	var $parada;
	var $bus1;
	var $bus2;

	function __construct($linea, $parada, $bus1, $bus2){
		$this->linea = $linea;
		$this->parada = $parada;
		$this->bus1 = $bus1;
		$this->bus2 = $bus2;
	}

}
?>