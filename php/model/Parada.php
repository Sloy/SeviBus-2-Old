<?php

Class Parada{
	var $id;
	var $numero;
	var $nombre;
	var $direccion;
	var $latitud;
	var $longitud;

	var $lineas; //opcional

	function __construct($atributos){
		if($atributos!=null){
			extract($atributos);
			$this->id = $_id;
			$this->numero = $numero;
			$this->nombre = $nombre;
			$this->direccion = $direccion;
			$this->latitud = $latitud;
			$this->longitud = $longitud;
		}//TODO else->error
	}

	function getId(){
		return $this->id;
	}

	function getNumero(){
		return $this->numero;
	}

	function getNombre(){
		return $this->nombre;
	}

	function getDireccion(){
		return $this->direccion;
	}

	function getLatitud(){
		return $this->latitud;
	}

	function getLongitud(){
		return $this->longitud;
	}


	function getJSON(){
		return json_encode($this);
	}
}

?>