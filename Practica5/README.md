
---

# Detector de Matrículas de Vehículos

## Descripción
El sistema desarrollado es un prototipo para la identificación de matrículas de vehículos, ya sea a partir de imágenes o vídeos. El sistema utiliza una combinación de detección de objetos y reconocimiento de texto (OCR) para identificar matrículas de vehículos, particularmente enfocado en matrículas españolas.

## Componentes del Sistema

## 1. Detección de Vehículos
#### `IDetectorVehiculos` (Interfaz Abstracta)
- Define el método abstracto `detectar`, el cual debe ser implementado por cualquier detector de vehículos.

#### `DetectorVehiculos` (Implementación)
- Utiliza YOLOv8 para detectar vehículos en una imagen.
- Método `detectar`:  Este método toma una imagen como entrada y devuelve dos listas: `boxes` y `class_names`. `boxes` contiene las coordenadas de las cajas delimitadoras de los vehículos detectados, y `class_names` contiene los nombres de las clases correspondientes (por ejemplo, coche, motocicleta, autobús). Utiliza el modelo YOLOv8 para detectar los vehículos y extraer estas informaciones.

## 2. Procesamiento de Imagen
#### `IFiltro` (Interfaz Abstracta)
- Define métodos abstractos `aplicar` y `mostrar` para los filtros de imagen.

#### Filtros Implementados
- `FiltroCanny`: Aplica el algoritmo de Canny para la detección de bordes.
- `FiltroSobel`: Usa el operador Sobel para la detección de bordes.
- `FiltroUmbralizacion`: Realiza una umbralización para convertir la imagen a blanco y negro basado en un umbral definido.

## 3. Reconocimiento de Texto (OCR)
#### `IOCR` (Interfaz Abstracta)
- Define el método abstracto `localizar_matriculas` para la identificación de texto en las matrículas.

#### `OCR` (Implementación)
- Utiliza EasyOCR para reconocer texto en una región de interés (ROI).
- Método `localizar_matriculas`: Este método procesa una región de interés (ROI), generalmente la parte de la imagen donde se espera encontrar la matrícula. Utiliza EasyOCR para extraer texto de esta ROI. Se emplea una expresión regular para capturar y uniformizar los formatos de matrículas, facilitando la identificación de diferentes formatos (por ejemplo, NNNNLLL o LLLNNNN).


## 4. Procesamiento de Matrículas
### Descripción `ProcesadorMatriculas` 
- Procesa la matrícula detectada en un vehículo.
- Aplica filtros y usa OCR para extraer el texto de la matrícula.
- Aisla la región de la matrícula y ajusta las coordenadas para su correcta visualización.

### Estructura y Funciones `ProcesadorMatriculas` 

#### Inicialización

- **`__init__(self, ocr, filtros=[])`**: Al inicializar la instancia de `ProcesadorMatriculas`, se le pasa una instancia de `OCR` y una lista de filtros (como `FiltroCanny`, `FiltroSobel`, `FiltroUmbralizacion`). Esto permite que el procesador aplique técnicas específicas de filtrado y OCR a las imágenes.

#### Métodos Privados

- **`__aplicar_filtros(self, img)`**: Este método toma una imagen (generalmente una ROI que contiene una matrícula) y la procesa a través de la lista de filtros proporcionada durante la inicialización. Cada filtro se aplica secuencialmente, transformando la imagen para mejorar la eficacia del reconocimiento de texto por OCR. La imagen resultante se devuelve para su posterior procesamiento.

- **`__aislar_matricula(self, vehicle_roi)`**: Dado un ROI de un vehículo, este método intenta aislar aún más la región específica donde se espera encontrar la matrícula. Basado en suposiciones sobre la posición y el tamaño de las matrículas en los vehículos, este método recorta la imagen para centrarse en la región donde es más probable que se encuentre la matrícula. Devuelve esta región junto con las coordenadas relativas dentro del ROI original.

- **`__ajustar_y_devolver_datos_matricula(self, matricula, area_matricula, coords_vehiculo, coords_matricula)`**: Este método toma la matrícula reconocida, las coordenadas de la matrícula dentro del ROI de la matrícula, y las coordenadas del vehículo dentro de la imagen original. Ajusta las coordenadas de la matrícula a las coordenadas de la imagen original y devuelve la matrícula junto con estas coordenadas ajustadas.

#### Métodos Públicos

- **`procesar_matricula(self, vehicle_roi, coords_vehiculo, class_name)`**: Este es el método principal de `ProcesadorMatriculas`. Procesa un ROI de vehículo dado, aplicando los filtros de imagen y utilizando OCR para reconocer la matrícula. Si se detecta una matrícula, devuelve la matrícula y su ubicación ajustada dentro de la imagen original. Si no se encuentra ninguna matrícula, devuelve `None`.

---

### Flujo de Trabajo

1. **Extracción y Filtrado de ROI**: Se extrae el ROI del vehículo de la imagen general y se aplica una serie de filtros para mejorar la claridad y legibilidad de la matrícula.
2. **Aislamiento de la Matrícula**: Se intenta aislar aún más la región donde se espera encontrar la matrícula dentro del ROI del vehículo.
3. **Reconocimiento de Matrícula**: Se aplica OCR a la región aislada para extraer el texto de la matrícula.
4. **Ajuste de Coordenadas**: Se ajustan las coordenadas de la matrícula a las de la imagen original para su correcta visualización y marcado.

Este proceso integral asegura que el sistema no solo detecte la presencia de un vehículo, sino que también localice y reconozca con precisión la matrícula, un aspecto crítico para el objetivo del sistema.

## 5. Almacenamiento y Visualización de Imágenes
#### `Visualizador`
- Maneja la visualización y almacenamiento de las imágenes procesadas.
- Métodos `mostrar_y_guardar` y `mostrar_imagen` para visualizar y guardar imágenes con las matrículas detectadas.

## 6. Clase Principal de Detección de Matrículas
#### Descripción `DetectorDeMatriculas`
- Orquesta el proceso de detección de matrículas.
- Utiliza `DetectorVehiculos` y `ProcesadorMatriculas` para detectar y procesar matrículas en imágenes o vídeos.

Por supuesto, profundizaremos en la clase `DetectorDeMatriculas`, una pieza central en el sistema de detección de matrículas.

---

### Estructura y Funciones `DetectorDeMatriculas`

#### Inicialización

- **`__init__(self, detector_vehiculos, procesador_matriculas)`**: Al inicializar la instancia de `DetectorDeMatriculas`, se le pasan dos componentes esenciales: una instancia de `DetectorVehiculos` para la detección de vehículos y una instancia de `ProcesadorMatriculas` para el procesamiento de las matrículas. Además, se inicializa un `Visualizador` para mostrar y guardar los resultados.

#### Métodos Privados

- **`__mostrar_matricula(self, img, resultado_matricula)`**: Dado un resultado de matrícula (texto y coordenadas), este método visualiza el texto de la matrícula en la imagen con un fondo blanco para destacar el texto. También dibuja un rectángulo alrededor de la región de la matrícula. Devuelve el texto de la matrícula para su uso posterior.

- **`__procesar_vehiculo(self, img, box, class_name)`**: Procesa un vehículo individual dentro de la imagen. Utiliza el `box` (coordenadas del vehículo) y `class_name` (tipo de vehículo) para extraer el ROI del vehículo y pasar esta región al `ProcesadorMatriculas`. Si se detecta una matrícula, muestra la matrícula en la imagen y devuelve el texto de la matrícula.

- **`__procesar_fotograma(self, img)`**: Este método procesa un fotograma completo de una imagen o video. Utiliza `DetectorVehiculos` para identificar todos los vehículos en la imagen y luego llama a `__procesar_vehiculo` para cada vehículo detectado. Devuelve la imagen con las matrículas marcadas y una lista de textos de matrículas detectadas.

- **`__procesar_video(self, video_path)`** y **`__procesar_imagen(self, img_path)`**: Estos métodos manejan la entrada del sistema, ya sea un video o una imagen. Cargan el video o la imagen, procesan cada fotograma (o la imagen completa) y utilizan el `Visualizador` para mostrar y guardar los resultados.

#### Método Público

- **`detectar(self, path, es_video=False)`**: Este es el método principal que se llama para iniciar el proceso de detección. Dependiendo de si `es_video` es `True` o `False`, llama a `__procesar_video` o `__procesar_imagen`, respectivamente.

---

### Flujo de Trabajo y Uso

1. **Inicialización**: Se crean instancias de las clases necesarias para la detección de vehículos, procesamiento de matrículas y visualización.
2. **Detección de Vehículos**: Para cada imagen o fotograma de video, se detectan los vehículos utilizando `DetectorVehiculos`.
3. **Procesamiento de Matrículas**: Cada vehículo detectado se procesa para extraer y reconocer la matrícula.
4. **Visualización y Almacenamiento**: Los resultados se muestran en la imagen original y se guardan si es necesario.

La clase `DetectorDeMatriculas` encapsula todo el proceso desde la detección de vehículos hasta la presentación de los resultados, lo que facilita su uso en diferentes escenarios, ya sea procesando imágenes individuales o secuencias de video.


## Uso del Detector de Matrículas
Para utilizar el detector, se deben crear instancias de las clases de procesamiento y luego llamar al método `detectar` de la clase `DetectorDeMatriculas` con la ruta de la imagen o vídeo a procesar.

### Ejemplo de Uso con Imágenes
```python
detector.detectar('./images/image1.png')
```

### Ejemplo de Uso con Vídeo
```python
detector.detectar('./videos/video1.mp4', es_video=True)
```

Se adjuntan algunas de las imagenes utilizadas para las pruebas. Por otro lado, la prueba de vídeo se ha realizado con el video obtenido por una "dashcam" mientras se conduce un coche. 


---
