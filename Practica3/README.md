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

#### Función `detectar_y_mostrar_monedas`

La función `detectar_y_mostrar_monedas` toma como argumento la ruta a una imagen y realiza los siguientes pasos:

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
im1, t1 = detectar_y_mostrar_monedas("Monedas_Solapadas.jpg")
# ... (más llamadas a la función) ...
im6, t6 = detectar_y_mostrar_monedas("Monedas_mechero2.jpg")

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

Este código proporciona una forma sistemática de detectar y contar monedas en una serie de imágenes, facilitando la visualización y análisis de los resultados.

Este documento en Markdown explica de manera estructurada y detallada el propósito y funcionamiento de cada parte del script proporcionado.


## TAREA 2: Identificación y valoración de monedas
Realiza capturas de imágenes con monedas no solapadas y con monedas solapadas. Implementa un sistema que permita identificar una moneda de un euro en la imagen, por ejemplo, a través de un clic de ratón. A continuación, calcula la cantidad total de dinero presente en la imagen. Reflexiona y documenta acerca de los problemas que hayas observado durante este proceso.

## TAREA 3: Evaluación de patrones geométricos y matriz de confusión
A partir de tres clases de imágenes extraídas de un conjunto de imágenes de mayor tamaño, determina patrones geométricos característicos de cada clase. Evalúa el rendimiento de tu clasificación comparando los resultados con las imágenes completas utilizando una matriz de confusión. Para cada clase, detalla el número de muestras clasificadas correctamente e incorrectamente, desglosando los errores por cada una de las otras dos clases.



