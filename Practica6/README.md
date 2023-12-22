# Práctica 6. Análisis facial

## Propuesta 1: Juego de razas

### Descripción General

El Juego de Razas consiste en una aplicación interactiva que utiliza el análisis de rostros con DeepFace y ViolaJones para superponer diferentes tipos de sombreros según la raza detectada en las caras de las personas. El juego ofrece una interfaz dinámica que se adapta a diferentes resoluciones de pantalla y proporciona varias funcionalidades como la elección de sombreros y la detección automática basada en rasgos raciales.

### Componentes del Juego

Los componentes del juego se han separado en diferentes clases para mantener una mejor modularidad del juego y que este sea más sencillo de comprender, mantener y modificar. A continuación se detalla el funcionamiento de cada uno de los componentes.

#### Clases para Gestionar la Interfaz

Estas clases se encargan de gestionar los componentes de la interfaz del juego. Se definen clases para poder utilizar botones dinámicos, es decir, botones que se adapten a la resolución de la fuente de vídeo pasada como parámetro y utilizada para analizar. También permite superponer los diseños de interfaz al video procesado por el juego.

#### `DynamicButton`

- **Descripción**: Esta clase representa un botón interactivo cuyo tamaño y posición se ajustan dinámicamente según la resolución de la pantalla.
- **Constructor**:
  - `x1, y1`: Coordenadas iniciales del botón en una resolución de referencia.
  - `width, height`: Dimensiones del botón en la resolución de referencia.
  - `res_width, res_height`: Resolución actual de la pantalla.
- **Métodos**:
  - `get_coordinates()`: Devuelve las coordenadas ajustadas del botón según la resolución actual.

#### `DynamicButtons`

- **Descripción**: Gestiona un conjunto de `DynamicButton`, agrupándolos en diferentes categorías como MENU, DETECTAR, ELEGIR, y CAMBIAR_SOMBRERO.
- **Constructor**:
  - `res_width, res_height`: Resolución actual de la pantalla para ajustar los botones.

#### `GameInterface`

- **Descripción**: Encargada de gestionar las interfaces gráficas del juego, como los fondos de pantalla para diferentes estados del juego.
- **Constructor**:
  - `res_width, res_height`: Resolución actual de la pantalla.
- **Métodos**:
  - `cargar_y_redimensionar(ruta_imagen)`: Carga y redimensiona una imagen de interfaz según la resolución actual.
  - `add_interface(frame, interface)`: Superpone una interfaz gráfica en el frame de video.

#### Clase para Gestionar el Estado del Juego

#### `GameState`

- **Descripción**: Mantiene el estado actual del juego y el sombrero seleccionado.
- **Métodos**:
  - `change_state(new_state)`: Cambia el estado actual del juego.
  - `set_selected_hat(hat)`: Establece el sombrero seleccionado.

### Ejemplo de Uso

Las clases `DynamicButtons`, `GameInterface`, y `GameState` se pueden instanciar y utilizar de la siguiente manera:

```python
# Instanciación de las clases
dynamic_buttons = DynamicButtons(width, height)  
game_interface = GameInterface(1280, 720)
game_state = GameState()

# Cambiar a estado "DETECTAR"
game_state.change_state("DETECTAR")
```

Estas clases facilitan la gestión y adaptabilidad de la interfaz gráfica del juego, así como el control del estado del juego en función de las interacciones del usuario.

#### Clases para Gestionar los Sombreros

Se definen dos clases, HatManager y HatOverlayManager. La primera de ellas gestiona los distintos sombreros disponibles, mientras que la segunda se encarga de posicionar el sombrero correspondiente sobre la cara detectada.

#### Descripción de Clases

##### `HatManager`

- **Descripción**: Esta clase gestiona los diferentes tipos de sombreros que se pueden superponer en las caras detectadas en el juego. Cada sombrero está asociado a una raza específica.
- **Constructor**: No requiere parámetros específicos. Inicializa un diccionario de sombreros basado en la raza.
- **Métodos**:
  - `load_hat(path)`: Carga un sombrero desde un archivo de imagen.
  - `get_hat(hat_name)`: Devuelve el sombrero asociado al nombre de la raza proporcionado. Si no se encuentra, devuelve el sombrero por defecto.
  - `show_available_hats()`: Imprime los nombres y las dimensiones de los sombreros disponibles.

##### `HatOverlayManager`

- **Descripción**: Se encarga de analizar las caras en los frames de video y superponer los sombreros correspondientes basándose en la raza detectada.
- **Constructor**:
  - `hat_manager`: Una instancia de `HatManager` para gestionar los sombreros.
  - `min_confidence`: Umbral mínimo de confianza para considerar válida una detección de cara.
  - `analysis_frequency`: Frecuencia con la que se realiza el análisis de rostros en los frames.
- **Métodos**:
  - `analyze_and_overlay(frame)`: Analiza las caras en el frame y superpone los sombreros correspondientes.
  - `overlay_hat(frame, hat, x, y, w, h)`: Superpone un sombrero en un frame en las coordenadas especificadas.
  - `choose_and_overlay(frame, selected_hat)`: Superpone un sombrero seleccionado en todas las caras detectadas en el frame.

#### Ejemplo de Uso

Las clases `HatManager` y `HatOverlayManager` se pueden instanciar y utilizar de la siguiente manera:

```python
# Instanciación de las clases
hat_manager = HatManager()
hat_overlay_manager = HatOverlayManager(hat_manager)

# Procesar un frame para superponer gorros basados en la raza detectada
frame = ... # Obtener el frame del video
processed_frame = hat_overlay_manager.analyze_and_overlay(frame)

# Procesar un frame para superponer un gorro seleccionado en todas las caras
selected_hat = 'white' # Nombre del gorro seleccionado
processed_frame = hat_overlay_manager.choose_and_overlay(frame, selected_hat)
```

### `Game`: Clase Principal de Gestión del Juego

La clase `Game` es el núcleo del juego, que integra todos los componentes y maneja el flujo general. Es responsable de configurar y ejecutar el bucle principal del juego, procesar entradas de usuario y actualizar la interfaz gráfica en consecuencia.

#### Constructor:

- **Parámetros**:
  - `source_video`: Ruta al archivo de video o índice de la cámara web que se utilizará como fuente de video.
  - `game_interface` (opcional): Una instancia predefinida de `GameInterface` para manejar la interfaz gráfica.
  - `dynamic_buttons` (opcional): Una instancia predefinida de `DynamicButtons` para manejar los botones dinámicos.
  - `overlay_manager` (opcional): Una instancia predefinida de `HatOverlayManager` para la superposición de gorros.

#### Métodos Principales:

- `start()`: Inicia el bucle principal del juego. Captura frames de video, gestiona los estados del juego y actualiza la interfaz gráfica.
- `exit()`: Establece el estado del juego en "SALIR", lo que causa que el bucle principal del juego finalice.
- `manage_game_state(frame)`: Gestiona los diferentes estados del juego ("MENU", "DETECTAR", "ELEGIR") y llama a las funciones correspondientes para actualizar la interfaz gráfica y la superposición de gorros.
- `integrate_interface(frame, pantalla, botones)`: Superpone la interfaz gráfica en el frame y dibuja los botones correspondientes.
- `clic_mouse(event, x, y, flags, param)`: Maneja los eventos de clic del ratón y actualiza el estado del juego o el gorro seleccionado.

#### Ejemplo de Uso:

```python
# Para usar la cámara por defecto:
game = Game(0)
game.start()

# Para usar un archivo de vídeo personalizado con overlay manager personalizado:
ov_manager_2 = HatOverlayManager(hat_manager=HatManager(), min_confidence=3, analysis_frequency=10)
game = Game('./video_test_5.mp4', overlay_manager=ov_manager_2)
game.start()
```

Esta clase proporciona una estructura modular y flexible para el juego, permitiendo una fácil personalización y ampliación de su funcionalidad. La integración de diferentes componentes, como la interfaz gráfica, la gestión de estados y la superposición de gorros, facilita la comprensión y el mantenimiento del código.

## Propuesta 2: Reconocimiento facial para identificación de personas


