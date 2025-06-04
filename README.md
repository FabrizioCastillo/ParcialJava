# 🎰 Ruleta

Este proyecto es un simulador de apuestas en una ruleta, hecho completamente en Java. Permite a los jugadores registrarse, apostar y ver cómo su saldo se modifica según los resultados de la apuestas.  
Toda la información queda guardada en una base de datos H2 que se genera automáticamente.

---

## ✅ Recomendación

Abrí el proyecto con **IntelliJ IDEA** para tener mejor organización. Aunque no es necesario. Ocupar JDK-17.


---

## 🚀 ¿Cómo ejecutar?

1. Ejecutá la clase principal: `main`.
2. Tienes datos ingresados le recomiendo no reiniciar la tabla.
3. Selecciona el casino que mas le guste o crea el casino donde quiera apostar
4. Despues, registrarte como jugador y empezar a apostar.

📌 La base de datos se crea automáticamente la primera vez. No hace falta tocar nada extra, ya queda lista para guardar jugadores, apuestas y el saldo del casino.

---

## 🎮 Funcionalidades

- Registro de jugadores (nombre, contraseña y saldo inicial).
- Iniciar sesión como jugador.
- Apuestas en diferentes tipos de jugadas:
  - ✅ **Número** (paga 36x)
  - ✅ **Color** (rojo o negro)
  - ✅ **Par o impar**
  - ✅ **Docena** (1–12, 13–24, 25–36)
  - ✅ **Columna** (1ra, 2da o 3ra)
- Simulación real de la ruleta (número aleatorio entre 0 y 36).
- Registro de cada apuesta con:
  - Tipo de jugada
  - Monto
  - Resultado (ganó o perdió)
  - Fecha y hora
- Actualización automática del saldo del jugador y del casino.

---

## 💾 Persistencia

- Conexión mediante **JDBC** a una base de datos **H2 en modo archivo**.
- Se genera automáticamente si no existe.
- Toda la información se guarda entre ejecuciones.

---

## 📁 Estructura del proyecto

- `modelo/`: Clases principales (`Jugador`, `Casino`, `Apuesta`, etc.)
- `dao/`: Clases de acceso a datos (`JugadorDAO`, `ApuestaDAO`, etc.)
- `util/`: Conexión con la base (`BDDconnection`)
- `main/`: Lógica de menú y ejecución (`Main.java`)

Organizado en capas y con patrón DAO para mantener una arquitectura limpia.

---

## 🛠 Tecnologías

- Java 17  
- JDBC  
- H2 Database (Se crea la carpeta Data y ahi se encuentra la base de datos)  
- IntelliJ IDEA  

---

## by Fabrizio Castillo
