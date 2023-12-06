let marcadores = [];
let map;
let directionsService;
let directionsRenderer;

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 9.7489, lng: -83.7534 },
        zoom: 8
    });

    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);

    obtenerNodos();
    obtenerAristas();

    map.addListener('click', function(e) {
        agregarNodoEnClick(e.latLng);
    });

    map.addListener('mousemove', function(e) {
        document.getElementById('xCoord').textContent = e.latLng.lat().toFixed(6);
        document.getElementById('yCoord').textContent = e.latLng.lng().toFixed(6);
    });
}

function agregarNodoEnClick(latLng) {
    const id = prompt('Ingrese el ID para el nuevo nodo:');
    if (id) {
        const x = latLng.lat();
        const y = latLng.lng();

        const nodoData = { id: parseInt(id), x, y }; // Asegúrate de que esto coincide con la estructura esperada por tu backend

        fetch('http://localhost:8080/api/grafo/nodos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nodoData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al agregar nodo');
                }
                return response.json();
            })
            .then(nodo => {
                agregarMarcador(nodo.id, `Nodo ${nodo.id}`, nodo.x, nodo.y);
                obtenerNodos(); // Actualizar la lista de nodos
            })
            .catch(error => console.error('Error:', error));
    }
}


// Agregar un marcador al mapa
function agregarMarcador(id, nombre, lat, lng) {
    const marker = new google.maps.Marker({
        position: { lat, lng },
        map: map,
        title: nombre
    });

    marker.myCustomId = id;
    marcadores.push({ id, marker });
}

// Obtener nodos desde el backend y agregarlos al mapa
function obtenerNodos() {
    fetch('http://localhost:8080/api/grafo/nodos')
        .then(response => response.json())
        .then(nodos => {
            actualizarListaNodos(nodos);
            nodos.forEach(nodo => {
                agregarMarcador(nodo.id, `Nodo ${nodo.id}`, nodo.x, nodo.y);

            });
        })
        .catch(error => console.error('Error al obtener nodos:', error));
}
function obtenerAristas() {
    fetch('http://localhost:8080/api/grafo/aristas')
        .then(response => response.json())
        .then(aristas => {
            actualizarListaArista(aristas);
            aristas.forEach(arista => {
                // Encuentra los marcadores de los nodos origen y destino
                const origenMarcador = marcadores.find(marcador => marcador.id === arista.origen.id);
                const destinoMarcador = marcadores.find(marcador => marcador.id === arista.destino.id);
                if (origenMarcador && destinoMarcador) {
                    const aristaPath = new google.maps.Polyline({
                        path: [origenMarcador.marker.getPosition(), destinoMarcador.marker.getPosition()],
                        geodesic: true,
                        strokeColor: '#FF0000',
                        strokeOpacity: 1.0,
                        strokeWeight: 2,
                        map: map
                    });
                }
            });
        })
        .catch(error => console.error('Error al obtener aristas:', error));
}

function agregarNodo() {
    const id = parseInt(document.getElementById('id').value);
    const x = parseFloat(document.getElementById('x').value);
    const y = parseFloat(document.getElementById('y').value);

    fetch('http://localhost:8080/api/grafo/nodos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, x, y })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al agregar nodo');
            }
            return response.json();
        })
        .then(nodo => {
            agregarMarcador(nodo.id, `Nodo ${nodo.id}`, nodo.x, nodo.y);
            obtenerNodos();
        })
        .catch(error => console.error('Error:', error));
}

// Agregar una arista
function agregarArista() {
    const origenId = parseInt(document.getElementById('inicioId').value);
    const destinoId = parseInt(document.getElementById('finId').value);

    const origenMarcador = marcadores.find(marcador => marcador.id === origenId);
    const destinoMarcador = marcadores.find(marcador => marcador.id === destinoId);

    if (!origenMarcador || !destinoMarcador) {
        alert('Nodos de inicio o fin no encontrados');
        return;
    }

    // Obteniendo las coordenadas de los marcadores
    const origenLatLng = origenMarcador.marker.getPosition();
    const destinoLatLng = destinoMarcador.marker.getPosition();

    // Calculando la distancia
    const distancia = calcularDistancia(origenLatLng, destinoLatLng);

    const aristaData = {
        origen: {
            id: origenMarcador.id,
            x: origenMarcador.marker.getPosition().lat(),
            y: origenMarcador.marker.getPosition().lng()
        },
        destino: {
            id: destinoMarcador.id,
            x: destinoMarcador.marker.getPosition().lat(),
            y: destinoMarcador.marker.getPosition().lng()
        },
        peso: distancia
    };

    fetch('http://localhost:8080/api/grafo/aristas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(aristaData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al agregar arista');
            }
            return response.json();
        })
        .then(arista => {
            console.log('Arista agregada:', arista);
            obtenerAristas(); // Actualizar la lista de aristas
            const aristaPath = new google.maps.Polyline({
                path: [origenMarcador.marker.getPosition(), destinoMarcador.marker.getPosition()],
                geodesic: true,
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 2,
                map: map
            });
        })
        .catch(error => console.error('Error:', error));
}
function calcularDistancia(latlng1, latlng2) {
    function toRad(x) {
        return x * Math.PI / 180;
    }

    const R = 6371; // Radio de la Tierra en km
    const dLat = toRad(latlng2.lat() - latlng1.lat());
    const dLng = toRad(latlng2.lng() - latlng1.lng());
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(toRad(latlng1.lat())) * Math.cos(toRad(latlng2.lat())) *
        Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distancia = R * c;

    return distancia;
}


// Calcular ruta
function calcularRuta(origenId, destinoId) {
    const origenMarcador = marcadores.find(m => m.id === origenId);
    const destinoMarcador = marcadores.find(m => m.id === destinoId);

    if (origenMarcador && destinoMarcador) {
        const origen = origenMarcador.marker.getPosition();
        const destino = destinoMarcador.marker.getPosition();

        directionsService.route({
            origin: origen,
            destination: destino,
            travelMode: 'DRIVING'
        }, function(response, status) {
            if (status === 'OK') {
                directionsRenderer.setDirections(response);
            } else {
                window.alert('No se pudo calcular la ruta debido a: ' + status);
            }
        });
    } else {
        window.alert('No se pudieron encontrar los marcadores de origen y destino.');
    }
}

// Event listeners para formularios
document.getElementById('nodoForm').addEventListener('submit', function(e) {
    e.preventDefault();
    agregarNodo();
});

document.getElementById('aristaForm').addEventListener('submit', function(e) {
    e.preventDefault();
    agregarArista();

});
function actualizarListaArista(aristas) {
    const lista = document.getElementById('aristasList').querySelector('tbody');
    lista.innerHTML = '';
    aristas.forEach(arista => {
        if (arista !== null) {
            let fila = lista.insertRow();
            fila.innerHTML = `<td>${arista.origen.id}</td><td>${arista.destino.id}</td><td>${arista.peso}</td>`;
        }
    });
}
document.getElementById('rutaForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const origenId = parseInt(document.getElementById('origenId').value);
    const destinoId = parseInt(document.getElementById('destinoId').value);
    calcularRuta(origenId, destinoId);
});

// Asegura que initMap se llame cuando Google Maps esté listo
if (typeof window !== 'undefined') {
    window.initMap = initMap;
}

function actualizarListaNodos(nodos) {
    const lista = document.getElementById('nodosList').querySelector('tbody');
    lista.innerHTML = '';
    nodos.forEach(nodo => {
        if (nodo !== null) {
            let fila = lista.insertRow();
            fila.innerHTML = `<td>${nodo.id}</td><td>${nodo.x}</td><td>${nodo.y}</td>`;
        }
    });
}
