# Práctica 3. Detección de formas

## Contenidos
- [TAREA 1: Captura con monedas no solapadas y objeto externo](#tarea-1-captura-con-monedas-no-solapadas-y-objeto-externo)
- [TAREA 2: Identificación y valoración de monedas](#tarea-2-identificación-y-valoración-de-monedas)
- [TAREA 3: Evaluación de patrones geométricos y matriz de confusión](#tarea-3-evaluación-de-patrones-geométricos-y-matriz-de-confusión)

## TAREA 1: Captura con monedas no solapadas y objeto externo
Captura una o varias imágenes que contengan monedas no solapadas y al menos un objeto que no sea una moneda. Aplica técnicas de procesamiento de imágenes para filtrar los contornos que no correspondan a monedas. Finalmente, muestra y cuenta el número total de monedas presentes en la imagen.

### Documentación de Código: Clase Moneda

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


## TAREA 2: Identificación y valoración de monedas
Realiza capturas de imágenes con monedas no solapadas y con monedas solapadas. Implementa un sistema que permita identificar una moneda de un euro en la imagen, por ejemplo, a través de un clic de ratón. A continuación, calcula la cantidad total de dinero presente en la imagen. Reflexiona y documenta acerca de los problemas que hayas observado durante este proceso.

## TAREA 3: Evaluación de patrones geométricos y matriz de confusión
A partir de tres clases de imágenes extraídas de un conjunto de imágenes de mayor tamaño, determina patrones geométricos característicos de cada clase. Evalúa el rendimiento de tu clasificación comparando los resultados con las imágenes completas utilizando una matriz de confusión. Para cada clase, detalla el número de muestras clasificadas correctamente e incorrectamente, desglosando los errores por cada una de las otras dos clases.



