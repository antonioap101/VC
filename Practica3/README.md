# Práctica 3. Detección de formas

## Contenidos
- [TAREA 1: Captura con monedas no solapadas y objeto externo](#tarea-1-captura-con-monedas-no-solapadas-y-objeto-externo)
- [TAREA 2: Identificación y valoración de monedas](#tarea-2-identificación-y-valoración-de-monedas)
- [TAREA 3: Evaluación de patrones geométricos y matriz de confusión](#tarea-3-evaluación-de-patrones-geométricos-y-matriz-de-confusión)

## TAREA 1: Captura con monedas no solapadas y objeto externo
Captura una o varias imágenes que contengan monedas no solapadas y al menos un objeto que no sea una moneda. Aplica técnicas de procesamiento de imágenes para filtrar los contornos que no correspondan a monedas. Finalmente, muestra y cuenta el número total de monedas presentes en la imagen.

### Documentación: Clase Moneda

Este bloque de código define y utiliza una clase `Moneda`, diseñada para representar diferentes denominaciones de monedas y calcular su tamaño en píxeles en base a una moneda de referencia. Esta implementación puede ser especialmente útil en contextos de procesamiento de imágenes, donde es necesario identificar y comparar monedas de diferentes denominaciones.

#### Clase `Moneda`

#### Atributos
- `diametros_reales`: Un diccionario de clase que contiene los diámetros reales de diferentes denominaciones de monedas.
- `nombre`: El nombre de la moneda.
- `diametro_real`: El diámetro real de la moneda, asignado durante la inicialización.
- `tamano_pixel_min`: El tamaño mínimo estimado en píxeles de la moneda.
- `tamano_pixel_max`: El tamaño máximo estimado en píxeles de la moneda.

#### Métodos
- `__init__(self, nombre)`: Constructor que inicializa la moneda con el nombre proporcionado y el diámetro correspondiente.
- `calcular_tamanos(monedas, referencia_nombre, referencia_pixel)`: Método estático para calcular los tamaños en píxeles de un conjunto de monedas, basándose en una moneda de referencia.

#### Uso del Código

1. Se crea una lista de instancias de `Moneda` para cada denominación disponible.
2. Se elige una moneda de referencia y se proporciona su tamaño en píxeles.
3. Se llama al método `calcular_tamanos` para calcular y asignar el tamaño en píxeles de todas las monedas en la lista.
4. Se imprimen los resultados, mostrando el nombre de cada moneda junto con su tamaño en píxeles calculado.

### Documentación: Procesamiento de Imagen y conteo de Monedas

Este script de Python está diseñado para detectar y contar monedas en una serie de imágenes utilizando técnicas de visión por computadora con la biblioteca OpenCV.

#### Función `detectar_y_marcar_monedas`

La función `detectar_y_marcar_monedas` toma como argumento la ruta a una imagen y realiza los siguientes pasos:

#### 1. Carga de la Imagen
```python
imagen = cv2.imread(ruta_imagen)
```
Carga la imagen de monedas de euro a partir de la ruta proporcionada.

#### 2. Conversión a Escala de Grises
```python
gris = cv2.cvtColor(imagen, cv2.COLOR_BGR2GRAY)
```
Convierte la imagen a escala de grises para facilitar el procesamiento.

#### 3. Suavizado de la Imagen
```python
pimg = cv2.medianBlur(gris, 7)
```
Aplica un filtro de mediana para suavizar la imagen y reducir el ruido.

#### 4. Detección de Círculos
```python
circulos_detectados = cv2.HoughCircles(pimg, cv2.HOUGH_GRADIENT, 1, 100, param1=100, param2=50, minRadius=37, maxRadius=150)
```
Utiliza la transformada de Hough para detectar círculos en la imagen.

#### 5. Verificación de Círculos Detectados
```python
assert circulos_detectados is not None, "No se encontraron círculos en la imagen"
```
Asegura que se hayan detectado círculos en la imagen.

#### 6. Redondeo y Conversión de Tipos
```python
circulos_detectados = np.uint16(np.around(circulos_detectados))
```
Redondea los valores de los círculos detectados y los convierte a enteros de 16 bits sin signo.

#### 7. Dibujo de Círculos y Conteo de Monedas
```python
total_monedas = 0
for i in circulos_detectados[0, :]:
    total_monedas += 1
    centro = (i[0], i[1])
    radio = i[2]
    cv2.circle(imagen, centro, radio, (0, 255, 0), 2)
```
Recorre los círculos detectados, dibuja un círculo verde alrededor de cada moneda en la imagen original y cuenta el número total de monedas.

#### 8. Retorno de Resultados
```python
return imagen, total_monedas
```
Devuelve la imagen con los círculos dibujados y el total de monedas detectadas.

#### Llamadas a la Función y Visualización de Resultados

Se realiza la detección de monedas en varias imágenes y se almacenan los resultados en listas.

```python
im1, t1 = detectar_y_marcar_monedas("Monedas_Solapadas.jpg")
# ... (más llamadas a la función) ...
im6, t6 = detectar_y_marcar_monedas("Monedas_mechero2.jpg")

arr_imagenes = [im1, im2, im3, im4, im5, im6]
arr_titulos = [t1, t2, t3, t4, t5, t6]
```

#### Visualización Conjunta
```python
mostrar_imagen(imagenes=[cv2.cvtColor(img, cv2.COLOR_BGR2RGB) for img in arr_imagenes], titulos=[f'Total de monedas: {t}' for t in arr_titulos])
```
Convierte las imágenes a RGB y las muestra todas juntas, cada una con su título correspondiente.

#### Visualización Individual (Opcional)
```python
# for i in range(len(arr_imagenes)):
#     mostrar_imagen(imagenes=cv2.cvtColor(arr_imagenes[i], cv2.COLOR_BGR2RGB), titulos=f'Total de monedas: {arr_titulos[i]}')
```
(Opcional) Puede descomentar este bloque para visualizar las imágenes de una en una.

---


## TAREA 2: Identificación y valoración de monedas
Realiza capturas de imágenes con monedas no solapadas y con monedas solapadas. Implementa un sistema que permita identificar una moneda de un euro en la imagen, por ejemplo, a través de un clic de ratón. A continuación, calcula la cantidad total de dinero presente en la imagen. Reflexiona y documenta acerca de los problemas que hayas observado durante este proceso.

### Documentación: Funciones para clasificar monedas y contabilizar dinero

Este script en Python está diseñado para procesar imágenes de monedas, clasificarlas, y calcular el valor total de las monedas presentes en la imagen. 

### Importaciones y Configuración Inicial

Antes de la porción de código proporcionada, se asume que hay importaciones necesarias y configuraciones iniciales, incluyendo la definición de clases como `Moneda` y funciones como `detectar_y_marcar_monedas`.

#### Creación y Reseteo de Contadores de Monedas

```python
monedas = Moneda.crear_lista_monedas()

def reset_contador_monedas():
    global monedas_contador
    monedas_contador = { 
        "1 Cent": [0, 0.01], 
        "2 Cent": [0, 0.02], 
        ...
    }
```
- `monedas`: Una lista de objetos Moneda.
- `reset_contador_monedas()`: Resetea el contador de monedas a 0 para cada denominación.

#### Funciones de Clasificación y Cálculo
```python
def agregar_moneda(nombre_moneda):
    monedas_contador[nombre_moneda][0] += 1

def calcular_total():
    ...
```
- `agregar_moneda()`: Incrementa el contador de una moneda específica.
- `calcular_total()`: Calcula el valor total de las monedas contadas.
- `clasificar_monedas`(): Clasifica las monedas en la imagen basándose en su tamaño.

### Documentación: Clasificación de monedas (PROGRAMA INTERACTIVO)

A continuación, se desglosa la función `procesar_imagen()` en secciones para una mejor comprensión. Esta función contiene el programa interactivo que reconoce las coordenadas del click del usuario sobre la moneda de 1 EURO en la imagen. 

#### 1. Inicialización y Preparación
```python
reset_contador_monedas()
clicked_point = None
```
- **`reset_contador_monedas()`:** Esta función se llama para inicializar o reiniciar los contadores de todas las monedas a 0. Esto es importante para asegurarse de que cada vez que procesemos una nueva imagen, los contadores estén en un estado limpio.
- **`clicked_point`:** Se establece como `None`. Esta variable se usará más adelante para almacenar las coordenadas (x, y) del punto donde el usuario hace clic en la imagen.

#### 2. Definición de la Función de Evento de Clic
```python
def click_event(event, x, y, flags, params):
    nonlocal clicked_point
    if event == cv2.EVENT_LBUTTONDOWN:
        clicked_point = (x, y)
        cv2.destroyAllWindows()
```
- **`click_event`:** Esta función se define para manejar eventos de clic en la imagen. Si el evento es un clic izquierdo del ratón (`EVENT_LBUTTONDOWN`), guarda las coordenadas del clic en `clicked_point` y cierra las ventanas de OpenCV. 

#### 3. Carga y Procesamiento de la Imagen
```python
imagen = cv2.imread(ruta_imagen)
assert imagen is not None, "No se pudo cargar la imagen."
imagen, _ = detectar_y_marcar_monedas(ruta_imagen)
```
- **`cv2.imread(ruta_imagen)`:** Intenta cargar la imagen de la ruta especificada.
- **`assert imagen is not None`:** Asegura que la imagen se haya cargado correctamente. Si no es así, se lanza un error.
- **`detectar_y_marcar_monedas(ruta_imagen)`:** Asumimos que esta función detecta las monedas en la imagen, las marca y devuelve la imagen procesada. (Esta función no está definida en el fragmento de código proporcionado).

#### 4. Interacción con el Usuario
```python
cv2.imshow('image', imagen)
cv2.setWindowTitle('image', "Haga click sobre la moneda de 1 EURO")
cv2.setMouseCallback('image', click_event)
cv2.waitKey(0)
assert clicked_point is not None, "No se hizo click en ningún punto"
```
- **`cv2.imshow()`:** Muestra la imagen al usuario.
- **`cv2.setWindowTitle()`:** Establece el título de la ventana de la imagen.
- **`cv2.setMouseCallback()`:** Configura la función `click_event` para manejar los eventos de clic en la imagen.
- **`cv2.waitKey(0)`:** Espera hasta que el usuario presione una tecla. En este caso, estamos esperando específicamente a que el usuario haga clic en algún lugar de la imagen.
- **`assert clicked_point is not None`:** Asegura que se haya hecho clic en algún lugar de la imagen. Si no, se lanza un error.

#### 5. Determinación de la Moneda Clickeada
```python
x, y = clicked_point
print(f"El usuario hizo clic en: (X: {x}, Y: {y})")
for borde in circulos_detectados[0, :]:
    cx, cy, radio = borde
    if (x - cx) ** 2 + (y - cy) ** 2 <= radio ** 2:
        print("El usuario hizo click en una moneda")
        Moneda.calcular_tamanos(monedas, "1 Euro", radio*2)
        break
else:
    raise Exception("El usuario no hizo clic dentro de ninguna moneda.")
```
- **`(x, y) = clicked_point`:** Se obtienen las coordenadas del punto donde el usuario hizo clic.
- **`print`:** Se imprime la posición del clic.
- **`for borde in circulos_detectados[0, :]:`:** Itera sobre los círculos detectados (asumimos que `circulos_detectados` contiene esta información).
- **`if (x - cx) ** 2 + (y - cy) ** 2 <= radio ** 2`:** Si las coordenadas del clic están dentro de uno de los círculos (monedas), se procede a calcular los tamaños de las monedas.
- **`Moneda.calcular_tamanos()`:** Se calculan los tamaños de las monedas utilizando el radio de la moneda en la que se hizo clic como referencia.

#### 6. Mostrar y Clasificar Monedas
```python
Moneda.mostrar_lista_monedas(monedas)
clasificar_monedas(monedas)
```
- **`Moneda.mostrar_lista_monedas(monedas)`:** Se muestra la lista de monedas junto con sus tamaños.
- **`clasificar_monedas(monedas)`:** Se clasifican las monedas en la imagen.

#### 7. Cálculo y Muestra del Total
```python
print("Dinero Total : " , calcular_total(), 
      end="\n*******************************************\n")
```
- **`calcular_total()`:** Se calcula el valor total de las monedas detectadas.
- **`print`:** Se imprime el valor total junto con una línea divisoria para separar los resultados de diferentes imágenes.

### Dificultades encontradas durante la clasificación

Durante el proceso de clasificación de monedas en nuestro programa, hemos encontrado ciertas dificultades particularmente notables al intentar diferenciar entre algunas monedas específicas. Las monedas de 20 céntimos tienden a confundirse frecuentemente con las de 1 euro, y las de 2 euros con las de 50 céntimos. Aunque en ocasiones el programa logra realizar la clasificación de manera precisa y eficaz, no siempre se obtienen resultados consistentes.

Estas inconsistencias podrían deberse a una variedad de factores que afectan la percepción visual de las monedas en las imágenes. La calidad y resolución de las fotos juegan un papel crucial, ya que imágenes de baja calidad pueden no capturar los detalles necesarios para una clasificación precisa. Asimismo, las condiciones de iluminación al momento de capturar las fotos pueden alterar la apariencia de las monedas. Además, la orientación o ángulo desde el cual se toman las fotos también puede influir, ya que variaciones en la perspectiva pueden llevar a errores en la estimación del tamaño y forma de las monedas. 

Para abordar y potencialmente superar estas limitaciones, un enfoque que se está considerando es la implementación de redes neuronales. Este método de aprendizaje profundo podría proporcionar una herramienta más robusta y precisa para la detección y clasificación de monedas, aprendiendo de grandes cantidades de datos y haciendo predicciones basadas en las características aprendidas. Pensamos que una transición a este enfoque podría mejorar significativamente la precisión de la clasificación de monedas y ofrecer resultados más consistentes y confiables.

## TAREA 3: Evaluación de patrones geométricos y matriz de confusión
A partir de tres clases de imágenes extraídas de un conjunto de imágenes de mayor tamaño, determina patrones geométricos característicos de cada clase. Evalúa el rendimiento de tu clasificación comparando los resultados con las imágenes completas utilizando una matriz de confusión. Para cada clase, detalla el número de muestras clasificadas correctamente e incorrectamente, desglosando los errores por cada una de las otras dos clases.

### Documentación: Clasificación y Análisis de Microplásticos


#### Función: `obtenerCaracteristicas`

```python
def obtenerCaracteristicas(contorno):
    area = cv2.contourArea(contorno)
    if area <= 250:
        return None  

    perimetro = cv2.arcLength(contorno, True)
    compacidad = (perimetro ** 2) / area 
    
    x, y, w, h = cv2.boundingRect(contorno)
    relacion_area_contenedor = area / (w * h)
    
    if contorno.shape[0] > 5:
        elipse = cv2.fitEllipse(contorno)
        relacion_ejes_elipse = elipse[1][0] / elipse[1][1]
    
    return area, perimetro, compacidad, relacion_area_contenedor, relacion_ejes_elipse
```
Esta función toma un contorno de un objeto en la imagen, calcula varias características geométricas y las devuelve. Si el área del contorno es muy pequeña, se considera ruido y se devuelve `None`.

#### Extracción de Características y Análisis Estadístico

```python
imagenes = ['TAR.png', 'FRA.png', 'PEL.png']
caracteristicas_pellets = []
caracteristicas_alquitran = []
caracteristicas_fragmentos = []

for img_path in imagenes:
    # Preprocesamiento y extracción de contornos
    # ...

    for contorno in contornos:
        caracteristicas = obtenerCaracteristicas(contorno)
        if caracteristicas is not None:
            # Clasificación y almacenamiento de características
            # ...
```
Para cada imagen, se realiza un preprocesamiento, se extraen los contornos y se calculan sus características. Luego, estas características se almacenan en listas según el tipo de microplástico.

#### Análisis Estadístico

```python
maxPellets = np.max(caracteristicas_pellets, axis=0)
mediasPellets = np.mean(caracteristicas_pellets, axis=0)
minPellets = np.min(caracteristicas_pellets, axis=0)
# Se repite para alquitrán y fragmentos
```
Se calculan y muestran las estadísticas (máximo, media, mínimo) de las características para cada tipo de microplástico.

#### Función: `clasificarMicroplasticos`

```python
def clasificarMicroplasticos(contorno_prediccion):
    caracteristicas = obtenerCaracteristicas(contorno_prediccion)
    if caracteristicas is None:
        return None
    # Comparación de características y clasificación
    # ...
    return 'PEL', 'FRA' o 'TAR'
```
Esta función clasifica un contorno de microplástico basándose en sus características geométricas y umbrales predefinidos.

#### Predicción de Microplásticos y Matriz de Confusión

```python
imagenes_prediccion = ['fragment-03-olympus-10-01-2020.JPG', 'pellet-03-olympus-10-01-2020.JPG', 'tar-03-olympus-10-01-2020.JPG']
confusion_matrix = np.zeros((3, 3), dtype=int)

for img_path_prediccion in imagenes_prediccion:
    # Preprocesamiento y extracción de contornos
    # ...

    for contorno_prediccion in contornos_prediccion:
        resultado_prediccion = clasificarMicroplasticos(contorno_prediccion)
        # Actualización de la matriz de confusión
        # ...
```
Para cada imagen de predicción, se extraen los contornos, se clasifican y se actualiza la matriz de confusión según los resultados.

#### Visualización de la Matriz de Confusión

```python
ax = sns.heatmap(confusion_matrix, annot=True, fmt='d', cbar=False, cmap='flag')
# Configuración de etiquetas y visualización
# ...
```
Se visualiza la matriz de confusión usando Seaborn para evaluar la precisión de las clasificaciones.


