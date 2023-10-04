## TAREA 1 : Conteo de Píxeles Blancos

### Objetivo

El objetivo de esta tarea es contar el número de píxeles blancos por fila en una imagen procesada, determinar el máximo número de píxeles blancos para las filas y las columnas, y calcular cuántos valores superan el \(95\%\) del valor máximo en cada caso.

### Explicación del Código

A continuación, se describe el fragmento de código proporcionado:

1. **`row_counts = cv2.reduce(canny, 1, cv2.REDUCE_SUM, dtype=cv2.CV_32SC1)`**:
 - Esta línea cuenta la suma de los valores de los píxeles por fila. Utiliza la función `reduce` de OpenCV con el parámetro `1` para indicar que la operación se realiza por fila. El resultado es una matriz (`row_counts`) donde cada elemento representa la suma de los valores de los píxeles en una fila específica de la imagen `canny`.
2. **Normalización de `row_counts`**:
 - `rows = row_counts[:, 0] / (255 * canny.shape[1])`: Esta línea normaliza `row_counts` dividiendo cada elemento por $(255\times canny.shape[1])$, el número de columnas en `canny`. El resultado es un array `rows` que contiene el número de píxeles blancos por fila expresado como un porcentaje del total de píxeles en la fila.
3. **Visualización**:
 - Se genera una figura utilizando `matplotlib` para visualizar tanto la imagen `canny` como el array `rows`.
 - `plt.subplot(1, 2, 1)`: Se crea el primer subplot para mostrar la imagen `canny`.
 - `plt.imshow(canny, cmap='gray')`: Se muestra la imagen `canny` en escala de grises.
 - `plt.subplot(1, 2, 2)`: Se crea el segundo subplot para mostrar el array `rows`.
 - `plt.plot(rows)`: Se traza el array `rows`, mostrando el número de píxeles blancos por fila.
 - `plt.xlim([0, canny.shape[0]])`: Se define el rango en el eje x para que corresponda con el número de filas en `canny`.

### Código

```python
# Si muestras el contenido de la imagen resultado, son valores 0 o 255
# print(canny)
# Cuenta el número de píxeles blancos (255) por fila
# Suma los valores de los pixeles por fila
row_counts = cv2.reduce(canny, 1, cv2.REDUCE_SUM, dtype=cv2.CV_32SC1)

# Normaliza en base al número de columnas y al valor máximo del píxel (255)
# El resultado será el número de píxeles blancos por fila
rows = row_counts[:, 0] / (255 * canny.shape[1])

# Muestra dicha cuenta gráficamente
plt.figure()
plt.subplot(1, 2, 1)
plt.axis("off")
plt.title("Canny")
plt.imshow(canny, cmap='gray') 

# Muestra la gráfica
plt.subplot(1, 2, 2)
plt.tight_layout(pad=3.0) #separación entre plots
plt.title("Respuesta de Canny por Filas")
plt.xlabel("Número de Fila")
plt.ylabel("% píxeles blancos")
plt.plot(rows)

# Rango en x definido por las filas
plt.xlim([0, canny.shape[0]])
```


## TAREA 2: Visualización de Imagen con Filtro Sobel Antes y Después de Ajustar Escala
### Objetivo
El propósito de esta tarea es cargar una imagen, aplicar el operador Sobel para destacar los bordes, y visualizar los resultados antes y después de ajustar la escala.

### Descripción del Código
A continuación, se presenta un desglose del código proporcionado:

1. **Cargar y Mostrar Imagen Original**:
 ```python
 imagen_leopardo = cv2.imread('leopardo.jpg') 
 print(imagen_leopardo.shape)
 plt.imshow(imagen_leopardo) 
 plt.show()
 ```
 Carga una imagen del archivo 'leopardo.jpg' y muestra sus dimensiones. Visualiza la imagen original. Conversión de Color y Visualización:
 
 ```python
 imagen_rgb = cv2.cvtColor(imagen_leopardo, cv2. COLOR_BGR2RGB)
 plt.imshow(imagen_rgb) 
 plt.show()
 ```
 Convierte la imagen de BGR a RGB para visualización correcta y la muestra.
 Conversión a Escala de Grises y Filtro Gaussiano:

 ```python
 imagen_gris = cv2.cvtColor(imagen_leopardo, cv2. COLOR_BGR2GRAY)
 img_gauss_leopardo = cv2.GaussianBlur(imagen_gris, (3, 3), 0)
 ```
 Convierte la imagen a escala de grises y aplica un filtro Gaussiano para suavizarla.
 Aplicación del Operador Sobel:

 ```python
 derivada_x = cv2.Sobel(img_gauss_leopardo, cv2.CV_64F, 1, 0) 
 derivada_y = cv2.Sobel(img_gauss_leopardo, cv2.CV_64F, 0, 1) 
 sobel_combinada_leopardo = cv2.add(derivada_x, derivada_y)
 ```
 Calcula las derivadas en direcciones x y y utilizando Sobel. Combina ambas derivadas para obtener bordes en todas las direcciones.
 Visualización de Resultados del Sobel:

 ```python
 plt.imshow(cv2.convertScaleAbs(derivada_x), cmap='gray') 
 plt.imshow(cv2.convertScaleAbs(derivada_y), cmap='gray') 
 plt.imshow(cv2.convertScaleAbs (sobel_combinada_leopardo), cmap='gray') 
 plt.show()
 ```
 Muestra los resultados de Sobel: derivadas vertical, horizontal y combinada.
 Se utiliza convertScaleAbs para ajustar la escala y visualizar correctamente los bordes detectados.
## Análisis de Resultados
 El operador Sobel resalta los bordes de la imagen original, permitiendo visualizar de manera clara las zonas donde hay cambios bruscos de intensidad. Además, la escala calculada con $$convertScaleAbs$$ afecta la visualización, ayudando a interpretar más fácilmente los resultados obtenidos con Sobel.

 ## TAREA: Análisis y Umbralización de Imagen con Filtro Sobel

### Objetivo:
1. Aplicar umbralizado a la imagen resultante del filtro Sobel y realizar un conteo por filas y columnas.
2. Identificar y remarcar las filas y columnas que están por encima del 95% del valor máximo obtenido en el conteo.
3. Comparar los resultados obtenidos con los del filtro Canny.

### Descripción del Código:

#### Parte 1: Umbralización y Conteo de Píxeles

```python
# Convierte la imagen sobel_combinada_mandril a 8 bits
sobel8 = np.uint8(sobel_combinada_mandril)

# Umbraliza la imagen y obtiene tanto el valor umbral como la imagen umbralizada
umbral_valor, umbralizada = cv2.threshold(sobel8, 0.6 * np.max(sobel8), 255, cv2.THRESH_BINARY)
print("Umbral utilizado: ", umbral_valor)

# Realiza conteo de píxeles blancos por filas y columnas y normaliza
conteo_filas = np.sum(umbralizada, axis=1) / 255
conteo_columnas = np.sum(umbralizada, axis=0) / 255

# Visualiza los conteos de píxeles por filas y columnas
plt.figure(figsize=(12,6))
plt.subplot(1, 2, 1)
plt.plot(conteo_filas, color='blue')
plt.title('Conteo de Píxeles Blancos por Filas')
plt.xlabel('Número de Fila')
plt.ylabel('Cantidad de Píxeles Blancos')
plt.subplot(1, 2, 2)
plt.plot(conteo_columnas, color='green')
plt.title('Conteo de Píxeles Blancos por Columnas')
plt.xlabel('Número de Columna')
plt.ylabel('Cantidad de Píxeles Blancos')
plt.tight_layout()
plt.show()
```
Parte 2: Cálculo de Máximos
# Calcula máximos por filas y columnas
max_filas = np.max(np.sum(umbralizada, axis=1))
max_columnas = np.max(np.sum(umbralizada, axis=0))
