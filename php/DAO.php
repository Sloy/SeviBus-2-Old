<?php



class DAO{


	var $conexion;

	function getTodasParadas($manageConexion = true){
		try{
			$res = array();
			if($manageConexion){
				$this->conexion = conectar();
			}
			$query = "select * from paradas";
			$stmt = $this->conexion->prepare($query);
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Parada($row);
			}

			desconectar($this->conexion);

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}

	function getParada($numero, $manageConexion = true){
		try{
			$res ;
			if($manageConexion){
				$this->conexion = conectar();
			}

			// Saca la información de la parada

			$query = "select * from paradas where numero = :num";
			$stmt = $this->conexion->prepare($query);
			$stmt->bindValue(":num",$numero);
			$stmt->execute();

			$row = $stmt->fetch();
			$res = new Parada($row);

			// Obtiene la lista de líneas que pasan por la parada
			$res->lineas = $this->getLineasFromParada($numero, false); //la conexión ya está abierta

			if($manageConexion){
				desconectar($this->conexion);
			}
			

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}

	function getLineasFromParada($numero, $manageConexion = true){
		try{
			$res = array();
			if($manageConexion){
				$this->conexion = conectar();
			}
			$query = "select l.* from 
				(
					(paradas p join relaciones r on p._id = r.parada_id) 
					join lineas l on r.linea_id = l._id
				)
				where p.numero = :num";

			$stmt = $this->conexion->prepare($query);
			$stmt->bindValue(":num",$numero);
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Linea($row);
			}

			if($manageConexion){
				desconectar($this->conexion);
			}

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}

	function getLineas($manageConexion = true){
		try{
			$res = array();
			if($manageConexion){
				$this->conexion = conectar();
			}
			$query = "select * from lineas order by nombre";

			$stmt = $this->conexion->prepare($query);
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Linea($row);
			}

			if($manageConexion){
				desconectar($this->conexion);
			}

			return $res;
		}catch(PDOException $e){
			echo $e;
		}
	}


	function buscarParadas($busca, $limit = 20, $manageConexion = true){
		$qWild = '%'.$busca.'%';
		try{
			$res = array();
			if($manageConexion){
				$this->conexion = conectar();
			}
			$query = "select * from paradas where (numero like :q) or (nombre like :q) or (direccion like :q) limit :limit";
			$stmt = $this->conexion->prepare($query);
			$stmt->bindValue(":q",$qWild);
			$stmt->bindValue(":limit",$limit,PDO::PARAM_INT);
			//$stmt->debugDumpParams();
			$stmt->execute();

			while($row = $stmt->fetch()){
				$res[] = new Parada($row);
			}

			if($manageConexion){
				desconectar($this->conexion);
			}

			return $res;
		}catch(PDOException $e){
			print_r($e);
		}
	}



}

?>