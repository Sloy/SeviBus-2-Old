<?php
/**
* conecta con la base de datos, y devuelve el objeto de la conexion
*/
function conectar(){
	//TODO los datos de la conexión deben ir en un archivo aparte, incluído en el gitignore para que no se hagan públicos 
	//$host = 'mysql:host=localhost;dbname=sevibus;charset=utf8'; //Usa codificaciÃ³n UTF8 en la conexiÃ³n
	$host = 'mysql:host=localhost;dbname=sevibus';
	$username = 'root';
	$password = '';

	try{
		$conexion = new PDO($host, $username, $password);
		$conexion->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);

		return $conexion;
	}
	catch(PDOException $e){
		echo "error de conexion: ".$e->GetMessage();
	}
}

/**
* desconecta de la base de datos
*/
function desconectar($conexion){
	$conexion = null;
}


?>