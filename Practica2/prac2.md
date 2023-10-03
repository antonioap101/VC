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
