<?php

Class Linea{
	var $id;
	var $nombre;
	var $trayecto;
	var $color;
	var $circular;

	function __construct($atributos){
		if($atributos != null){
			extract($atributos);
			$this->id = $_id;
			$this->nombre = $nombre;
			$this->trayecto = $trayecto;
			$this->color = $color;
			$this->circular = $circular;
		}
	}

	function getId(){
		return $this->id;
	}

	function getNombre(){
		return $this->nombre;
	}

	function getTrayecto(){
		return $this->trayecto;
	}

	function getColor(){
		return $this->color;
	}

	function getCircular(){
		return $this->circular;
	}

	function getJSON(){
		return json_encode($this);
	}
}

?>