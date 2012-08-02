<?php



class DAO{

	function getTodasParadas(){
		try{
			$res = array();
			$conexion = conectar();
			$query = "select * from paradas";
			$stmt = $conexion->prepare($query);
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Parada($row);
			}

			desconectar($conexion);

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}

	function getParada($numero){
		try{
			$res ;
			$conexion = conectar();
			$query = "select * from paradas where numero = :num";
			$stmt = $conexion->prepare($query);
			$stmt->bindValue(":num",$numero);
			$stmt->execute();

			$row = $stmt->fetch();
			$res = new Parada($row);

			desconectar($conexion);

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}


	function buscarParadas($busca, $limit = 20){
		$qWild = '%'.$busca.'%';
		try{
			$res = array();
			$conexion = conectar();
			$query = "select * from paradas where (numero like :q) or (nombre like :q) or (direccion like :q) limit :limit";
			$stmt = $conexion->prepare($query);
			$stmt->bindValue(":q",$qWild);
			$stmt->bindValue(":limit",$limit,PDO::PARAM_INT);
			//$stmt->debugDumpParams();
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Parada($row);
			}

			desconectar($conexion);

			return $res;
		}catch(PDOException $e){
			print_r($e);
		}
	}



}

?>