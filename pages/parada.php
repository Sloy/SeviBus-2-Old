<div id="parada" data-role="page">
	<div data-role="header">
        <a href="#home-paradas" data-rel="back" data-icon="back" data-iconpos="notext">Atrás</a>
		<h1>Parada ##</h1>
	</div>

	<div data-role="content" style="padding: 15px">
		<h2 id="parada-nombre" style="text-align: center">
            - Nombre de la parada -
        </h2>
        <h5 id="parada-direccion" style="text-align: center">
            - dirección de la parada -
        </h5>
        <!-- Elemento para colocar el mapa desplegable con la dirección 
        <div data-role="collapsible-set" data-theme="e" data-content-theme="">
            <div data-role="collapsible" data-collapsed="true">
                <h3>
                    Av de la Cruz Roja, 3
                </h3>
                <img src="https://maps.googleapis.com/maps/api/staticmap?center=Madison, WI&amp;zoom=14&amp;size=288x200&amp;markers=Madison, WI&amp;sensor=false"
                width="288" height="200">
            </div>
        </div> -->
	
		<ul id="parada-lista-lineas" data-role="listview" data-divider-theme="b" data-inset="true">
            <li data-role="list-divider" role="heading">
                Tiempos de llegada
            </li>
            <!-- <li data-theme="c">
                01: 8 minutos | 22 minutos
            </li> -->
        </ul>
	</div>
</div>