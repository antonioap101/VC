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


El script que has proporcionado es para un sistema de reconocimiento facial que utiliza machine learning para identificar a personas. El proceso se divide en tres etapas principales: captura de imágenes, entrenamiento del modelo y reconocimiento en tiempo real. A continuación, se detallan las diferentes partes del script:

## Funciones
### 1. Captura de Imágenes (`image_capture`)

Esta función captura imágenes de rostros desde una cámara web y las almacena en un directorio específico. Cada imagen capturada se asocia con un nombre de clase o etiqueta, lo que permite recopilar datos para diferentes personas.

- **Parámetros**:
  - `class_model_name`: El nombre de la clase o etiqueta para las imágenes capturadas.

- **Proceso**:
  - Crea un directorio específico para cada persona (si no existe).
  - Captura imágenes del rostro detectado utilizando el clasificador Haar Cascade.
  - Guarda las imágenes en el directorio correspondiente.

### 2. Entrenamiento del Modelo (`train_model`)

Esta función entrena un modelo de clasificación de máquina de vectores de soporte (SVM) utilizando el método de Análisis de Componentes Principales (PCA) para la reducción de la dimensionalidad.

- **Proceso**:
  - Carga las imágenes capturadas, las convierte a escala de grises y las redimensiona a un tamaño estándar.
  - Divide los datos en conjuntos de entrenamiento y prueba.
  - Aplica PCA para reducir la dimensionalidad.
  - Entrena el modelo SVM con los datos transformados.
  - Evalúa la precisión del modelo y lo devuelve junto con el objeto PCA.

### 3. Reconocimiento en Tiempo Real (`real_time_recognition`)

Esta función utiliza el modelo entrenado y PCA para identificar rostros en tiempo real a través de una cámara web.

- **Parámetros**:
  - `classifier`: El modelo SVM entrenado.
  - `pca`: El objeto PCA para la transformación de datos.

- **Proceso**:
  - Captura imágenes en tiempo real de una cámara web.
  - Detecta rostros utilizando el clasificador Haar Cascade.
  - Aplica PCA y el modelo SVM para identificar el rostro.
  - Muestra el resultado en la ventana de video.

## Proceso
### 4. Capturar Imágenes para el Dataset
```python
name = 'Antonio'
image_capture(name)
```
- **Descripción**: 
  - Esta parte del código llama a la función `image_capture` con el nombre 'Antonio'. 
  - La función capturará imágenes de la persona llamada Antonio utilizando una cámara web y las almacenará en un directorio específico creado para él.

- **Proceso**:
  - El script abrirá una ventana de captura de video.
  - Detectará rostros en tiempo real y los enmarcará con un rectángulo verde.
  - Guardará imágenes del rostro detectado en la carpeta correspondiente a Antonio en el directorio 'Images_class_model'.

### 5. Entrenar el Modelo
```python
data_dir = 'Images_class_model'
classifier, pca = train_model(data_dir)
```
- **Descripción**: 
  - Esta línea establece la variable `data_dir` en el directorio donde se almacenan las imágenes capturadas.
  - Luego llama a la función `train_model`, pasándole el directorio de datos, para entrenar un modelo de clasificación facial.

- **Proceso**:
  - La función `train_model` carga todas las imágenes de cada persona en el directorio especificado.
  - Entrena un modelo SVM utilizando PCA para reducir la dimensionalidad de las imágenes.
  - Devuelve el clasificador entrenado y el objeto PCA.

### 6. Reconocimiento Facial en Tiempo Real
```python
real_time_recognition(classifier, pca)
```
- **Descripción**:
  - Esta línea ejecuta la función `real_time_recognition`, proporcionándole el clasificador SVM y el objeto PCA obtenidos en el paso anterior.

- **Proceso**:
  - La función abre una ventana de captura de video en tiempo real.
  - Utiliza el clasificador Haar Cascade para detectar rostros en el video.
  - Aplica PCA y el modelo SVM para identificar los rostros detectados.
  - Muestra el nombre del sujeto reconocido en la ventana de video.


Esta propuesta proporciona un enfoque sencillo pero efectivo para el reconocimiento facial, utilizando técnicas de aprendizaje automático, aunque para un correcto funcionamiento, es crucial tener una buena cantidad de imágenes de cada persona para entrenar eficazmente el modelo.El rendimiento y precisión del modelo pueden variar según la calidad de las imágenes y la cantidad de datos disponibles para cada clase.
