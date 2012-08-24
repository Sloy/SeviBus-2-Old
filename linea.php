<?php
    // Obtiene la información de la parada
    require_once './php/DAO.php';
    require_once './php/BDConexion.php';
    require_once './php/model/Parada.php';
    require_once './php/model/Linea.php';
    require_once './php/model/Llegada.php';

    $dao = new DAO();

    $paradas = $dao->getParadasFromLinea($_GET['n']);
    if(!count($paradas)>0){
        //TODO: línea inválida, error 404 o algo
        echo "ERROR! ERROR!";
    }
    $json = json_encode($paradas);

?>

<!DOCTYPE html> 
<html> 
    <head> 
        <?php include './parts/header.php'; ?>
    </head> 
    <body>
        <div id="linea" data-role="page">
            <script type="text/javascript">     
            </script>

            <div data-role="header">
                <a href="#home-lineas" data-rel="back" data-icon="back" data-iconpos="notext">Atrás</a>
                <h1>Línea <?= $_GET['n'] ?></h1>
            </div>

            <div data-role="content" style="padding: 15px">

                <ul id="linea-lista-paradas" data-role="listview" data-divider-theme="b" data-inset="true">
                    <li data-role="list-divider" role="heading">
                        Paradas:
                    </li>
                    <?
                    foreach($paradas as $parada){
                    echo '<li><a href="parada.php?n='.$parada->numero.'">Parada nº '.$parada->numero.'</a></li>';
                }
                    ?>
                </ul>
            </div>
        </div>
    </body>
</html>

