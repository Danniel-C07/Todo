# TO-DO List — Aplicación de Gestión de Tareas con Integración de Base de Datos

Aplicación de consola en Java con persistencia relacional en MySQL. Desarrollada para la **Evaluación Final de Base de Datos**, implementando arquitectura por capas (Modelos, DAO, Vistas CLI), seguridad con hashing criptográfico, transacciones ACID, triggers, vistas, procedimientos almacenados y control de integridad referencial.

---

## 1. Diagrama Entidad-Relación (ER)

El modelo relacional del sistema se encuentra graficado y disponible en:
* **Archivo PDF:** [`database/4_db_relational model.pdf`](database/4_db_relational%20model.pdf) (exportado desde MySQL Workbench).

El diagrama incluye las 7 entidades del sistema, llaves primarias (`PK`), llaves foráneas (`FK`), cardinalidades (1:N y N:M) y la tabla de auditoría.

---

## 2. Diccionario de Datos

La base de datos relacional `todolist_db` consta de las siguientes 7 tablas:

### 2.1. Tabla `users`
Almacena las credenciales y datos de las cuentas de usuario.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_user` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador único del usuario |
| `username` | `VARCHAR(50)` | NO | UNIQUE | Ninguno | Nombre de usuario para login |
| `email` | `VARCHAR(100)` | NO | UNIQUE | Ninguno | Correo electrónico institucional |
| `password_hash` | `VARCHAR(255)` | NO | | Ninguno | Hash criptográfico SHA2-256 de la contraseña |
| `created_at` | `TIMESTAMP` | NO | | `CURRENT_TIMESTAMP` | Fecha y hora de registro |

### 2.2. Tabla `categories`
Categorías para organizar tareas. Cada usuario posee su propia categoría predeterminada ('General') y categorías personalizadas.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_category` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador único de la categoría |
| `name` | `VARCHAR(100)` | NO | | Ninguno | Nombre de la categoría (ej. 'Universidad') |
| `id_user` | `INT` | NO | FK | Ninguno | Referencia al usuario propietario (`users.id_user`) con `ON DELETE CASCADE` |
| `is_default` | `BOOLEAN` | SÍ | | `FALSE` | Bandera que indica si es la categoría por defecto del usuario |

### 2.3. Tabla `tasks`
Entidad principal para almacenar las tareas de los usuarios.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_task` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador único de la tarea |
| `title` | `VARCHAR(200)` | NO | | Ninguno | Título o nombre de la tarea |
| `description` | `TEXT` | SÍ | | `NULL` | Detalle o descripción de la tarea |
| `status` | `ENUM(...)` | SÍ | | `'NUEVA'` | Estado: `'NUEVA'`, `'EN_PROGRESO'`, `'PENDIENTE'`, `'COMPLETADA'` |
| `creation_date` | `TIMESTAMP` | NO | | `CURRENT_TIMESTAMP` | Fecha y hora en que se creó la tarea |
| `due_date` | `DATETIME` | SÍ | | `NULL` | Fecha y hora límite de vencimiento |
| `id_category` | `INT` | NO | FK | Ninguno | Categoría a la que pertenece (`categories.id_category`) con `ON DELETE CASCADE` |

### 2.4. Tabla `subtasks`
Subtareas o pasos secundarios pertenecientes a una tarea principal.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_subtask` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador de la subtarea |
| `title` | `VARCHAR(200)` | NO | | Ninguno | Nombre o descripción del paso |
| `is_completed` | `BOOLEAN` | SÍ | | `FALSE` | Indica si el paso fue completado |
| `id_task` | `INT` | NO | FK | Ninguno | Tarea padre (`tasks.id_task`) con `ON DELETE CASCADE` |

### 2.5. Tabla `tags`
Etiquetas creadas por los usuarios para clasificar transversalmente tareas (ej. 'Urgente', 'Final').

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_tag` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador único de la etiqueta |
| `name` | `VARCHAR(50)` | NO | | Ninguno | Nombre o tag de clasificación |
| `id_user` | `INT` | NO | FK | Ninguno | Usuario propietario (`users.id_user`) con `ON DELETE CASCADE` |

### 2.6. Tabla `task_tags`
Tabla asociativa que resuelve la relación N:M (muchos a muchos) entre `tasks` y `tags`.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_task` | `INT` | NO | PK, FK | Ninguno | Referencia a `tasks.id_task` con `ON DELETE CASCADE` |
| `id_tag` | `INT` | NO | PK, FK | Ninguno | Referencia a `tags.id_tag` con `ON DELETE CASCADE` |

### 2.7. Tabla `task_audit`
Tabla de auditoría poblada automáticamente por el Trigger cuando el estado de una tarea pasa a `'COMPLETADA'`.

| Campo | Tipo | Nulo | Clave | Valor por Defecto | Descripción |
|---|---|:---:|:---:|:---:|---|
| `id_audit` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador del evento de auditoría |
| `id_task` | `INT` | NO | | Ninguno | ID de la tarea modificada |
| `old_status` | `VARCHAR(50)` | SÍ | | `NULL` | Estado previo de la tarea |
| `new_status` | `VARCHAR(50)` | SÍ | | `NULL` | Nuevo estado asignado (`'COMPLETADA'`) |
| `changed_at` | `TIMESTAMP` | NO | | `CURRENT_TIMESTAMP` | Fecha y hora exacta del cambio |

---

## 3. Cómo Funciona

### 3.1. Arquitectura de la Aplicación
La aplicación sigue una arquitectura por capas desacoplada:
* **Capa de Base de Datos (`edu.programacion.db`):** Singleton `DatabaseConnection` que administra la conexión JDBC a MySQL leyendo credenciales externas desde `config.properties`.
* **Capa de Modelos (`edu.programacion.models`):** Clases POJO (`User`, `Category`, `Task`, `Subtask`, `Tag`, `TaskAudit`) que representan el modelo de dominio.
* **Capa de Acceso a Datos (`edu.programacion.dao`):** Implementaciones DAO (`UserDAO`, `CategoryDAO`, `TaskDAO`, `SubtaskDAO`, `TagDAO`) que encapsulan sentencias SQL con `PreparedStatement`, manejo de transacciones y aislamiento de datos por usuario (`id_user`).
* **Capa de Interfaz de Usuario CLI (`edu.programacion.ui`):** Menús enumerados interactivos con validación robusta (`InputValidator`), ocultamiento de contraseña y control de sesión (`SessionData`).

### 3.2. Configuración del Entorno de Desarrollo
1. **Instalar JDK 25 o compatible (JDK 17+)** y configurar la variable `JAVA_HOME`.
2. **Instalar MySQL Server 8.0+** y MySQL Workbench.
3. **Clonar el proyecto** y abrir la carpeta raíz en IntelliJ IDEA o cualquier IDE con soporte para proyectos Maven.
4. **Instalar la base de datos:** Ejecutar los scripts SQL ubicados en la carpeta `database/` en MySQL Workbench en el siguiente orden:
   1. `database/1_schema.sql` (Crea la base de datos `todolist_db` y las 7 tablas).
   2. `database/2_triggers.sql` (Crea el trigger de auditoría `after_task_status_update`).
   3. `database/3_data.sql` (Inserta usuarios, categorías, tareas, subtareas y etiquetas de prueba).
   4. `database/5_views_and_procedures.sql` (Crea la vista `v_resumen_tareas`, los procedimientos almacenados y funciones).
5. **Configurar el archivo local de conexión:**
   Crear un archivo llamado `config.properties` en la raíz del proyecto (junto a `pom.xml`) con los datos de su servidor local de MySQL:
   ```properties
   db.url=jdbc:mysql://localhost:3306/todolist_db
   db.user=TU_USUARIO_MYSQL
   db.password=TU_PASSWORD_MYSQL
   ```

### 3.3. Cómo Ejecutar la Aplicación
* **Desde IntelliJ IDEA:**
  1. Abrir el proyecto y recargar las dependencias Maven.
  2. Localizar `src/main/java/edu/programacion/Main.java`.
  3. Clic derecho en `Main.java` -> **Run 'Main.main()'**.
* **Desde Terminal con Maven:**
  ```bash
  mvn clean compile
  mvn exec:java -Dexec.mainClass="edu.programacion.Main"
  ```

### 3.4. Cómo Interactuar con la Aplicación
1. **Inicio de sesión y Registro:**
   * Al iniciar, se presenta el menú de bienvenida.
   * Seleccione `2. Registrar nuevo usuario` para crear una cuenta (se creará automáticamente su categoría `'General'`).
   * O seleccione `1. Iniciar sesión` e ingrese usuario y contraseña (las credenciales de prueba precargadas son: usuario `daniel_admin`, contraseña `123456`).
2. **Menú Principal:**
   * `1. 📂 Gestionar mis Categorías`: Listar, crear nuevas categorías, renombrarlas o eliminarlas (con opción de cancelar con `0`).
   * `2. ✅ Gestionar mis Tareas`: Operaciones CRUD sobre tareas.
   * `3. 🏷️  Gestionar mis Etiquetas`: Crear y listar tags propios del usuario.
   * `4. 👤 Ver información de mi cuenta`: Ver datos del perfil en sesión.
   * `0. 🚪 Cerrar Sesión`: Cierra la sesión activa con seguridad.
3. **Gestión de Tareas:**
   * Listar todas las tareas propias.
   * Agregar tarea (solicita título, descripción opcional, fecha límite opcional y selección de categoría o uso de la predeterminada `'General'`).
   * Cambiar estado entre: `NUEVA`, `EN_PROGRESO`, `PENDIENTE`, `COMPLETADA`.
   * Marcar directamente como `COMPLETADA` (lo cual dispara el trigger de auditoría en MySQL).
   * Gestionar subtareas (pasos dentro de una tarea).
   * Gestionar etiquetas (asociar o quitar etiquetas a la tarea).
   * **Ver historial de auditoría:** Muestra los registros generados por el trigger en la tabla `task_audit`.

---

## 4. Principales Acciones en Base de Datos

En cumplimiento de los requisitos de evaluación de base de datos, el sistema implementa y aprovecha los siguientes objetos y mecanismos avanzados en MySQL:

### 4.1. Triggers (Disparadores)
* **Trigger:** `after_task_status_update` (en [`database/2_triggers.sql`](database/2_triggers.sql)).
* **Momento:** `AFTER UPDATE ON tasks FOR EACH ROW`.
* **Función:** Detecta transiciones de estado en las tareas. Si el nuevo estado es `'COMPLETADA'` y difiere del estado anterior (`OLD.status != NEW.status`), inserta un registro histórico inmutable en la tabla `task_audit` con el ID de la tarea, el estado anterior, el nuevo estado y la fecha/hora exacta del sistema (`NOW()`).
* **Visualización:** Se puede verificar en MySQL Workbench o directamente desde la opción 10 del menú de tareas en la aplicación.

### 4.2. Vistas (Views)
* **Vista:** `v_resumen_tareas` (en [`database/5_views_and_procedures.sql`](database/5_views_and_procedures.sql)).
* **Función:** Consolida en una sola consulta relacional la información completa de las tareas: datos de la tarea, nombre de su categoría, usuario propietario, conteo total de subtareas, conteo de subtareas completadas y total de etiquetas asociadas. Permite generar reportes analíticos sin necesidad de realizar múltiples `JOIN` en el código de la aplicación.

### 4.3. Procedimientos Almacenados (Stored Procedures)
* **Procedimiento:** `sp_marcar_tarea_completada(IN p_id_task, IN p_id_user, OUT p_filas_afectadas)`.
  * **Función:** Actualiza el estado de una tarea a `'COMPLETADA'` verificando que la tarea pertenezca al usuario autenticado, disparando automáticamente el trigger de auditoría y retornando el número de filas afectadas mediante parámetro de salida.
* **Procedimiento:** `sp_estadisticas_usuario(IN p_id_user)`.
  * **Función:** Calcula agregaciones del usuario en la base de datos (total de tareas creadas, cantidad completadas, en progreso, pendientes y nuevas).

### 4.4. Funciones Almacenadas (Stored Functions) y Funciones Nativas
* **Función nativa `SHA2(str, 256)`:** Utilizada para encriptar unidireccionalmente las contraseñas de los usuarios tanto en la inserción como en la comprobación de login.
* **Función UDF:** `fn_dias_restantes(p_fecha_limite)` (en [`database/5_views_and_procedures.sql`](database/5_views_and_procedures.sql)).
  * **Función:** Calcula los días que restan entre la fecha actual y la fecha límite de una tarea mediante `DATEDIFF`.

### 4.5. Transacciones ACID
* En [`UserDAO.java`](src/main/java/edu/programacion/dao/UserDAO.java), el registro de un nuevo usuario se realiza dentro de una **transacción explícita**:
  1. `connection.setAutoCommit(false);`
  2. Inserta el usuario en la tabla `users` y obtiene la clave generada `id_user`.
  3. Inserta automáticamente la categoría inicial `'General'` con `is_default = TRUE` asociada a ese `id_user`.
  4. Si ambas operaciones son exitosas, ejecuta `connection.commit()`. Si alguna falla, ejecuta `connection.rollback()`, garantizando atomicidad y consistencia.

### 4.6. Integridad Referencial y Restricciones
* Todas las relaciones entre tablas hijas y tablas padres (`categories -> users`, `tasks -> categories`, `subtasks -> tasks`, `task_tags -> tasks`, etc.) cuentan con restricciones `FOREIGN KEY` con cláusula `ON DELETE CASCADE`, garantizando que la eliminación de un usuario o tarea limpie automáticamente sus dependencias asociadas sin dejar registros huérfanos.

---

## 5. Seguridad y Validación

1. **Protección contra Inyección SQL:** Todas las consultas dinámicas en los DAO utilizan `PreparedStatement` con asignación parametrizada (`?`).
2. **Aislamiento Multiusuario:** Todas las sentencias DML (SELECT, UPDATE, DELETE) verifican la propiedad mediante el `id_user` de la sesión activa, evitando que un usuario manipule datos de otro.
3. **Almacenamiento de Contraseñas:** Se generan hashes criptográficos de 256 bits mediante `SHA2(?, 256)`.
4. **Ocultamiento de Contraseña en Consola:** Implementado en [`InputValidator.leerPassword`](src/main/java/edu/programacion/ui/InputValidator.java), utilizando `System.console().readPassword()` para evitar que la contraseña quede visible en pantalla mientras se teclea en terminales.
5. **Validación Exhaustiva de Entradas:**
   * Validación de enteros con límites mínimos y máximos.
   * Validación estricta de fechas (`uuuu-MM-dd HH:mm:ss`) con año de 4 dígitos.
   * Validación de formato de correo electrónico mediante expresiones regulares (`Regex`).
   * Límite de longitud máxima de cadenas en consola antes de enviar a base de datos.
   * Confirmación obligatoria (`s/n`) para operaciones destructivas (eliminación de tareas, categorías y etiquetas).

---

## 6. Estructura del Proyecto

```
.
├── pom.xml                                   # Configuración de dependencias Maven (MySQL Connector/J)
├── config.properties                         # Configuración local de credenciales (excluido de Git)
├── config.example.properties                 # Plantilla de ejemplo de configuración
├── database/                                 # Scripts y artefactos de base de datos
│   ├── 1_schema.sql                          # Definición de tablas y base de datos
│   ├── 2_triggers.sql                        # Trigger after_task_status_update
│   ├── 3_data.sql                            # Datos de prueba iniciales
│   ├── 4_db_relational model.pdf             # Diagrama Entidad-Relación exportado
│   └── 5_views_and_procedures.sql            # Vistas, procedimientos almacenados y funciones
└── src/main/java/edu/programacion/
    ├── Main.java                             # Punto de entrada
    ├── db/DatabaseConnection.java            # Conexión JDBC Singleton
    ├── enums/Status.java                     # Estados: NUEVA, EN_PROGRESO, PENDIENTE, COMPLETADA
    ├── models/                               # Entidades: User, Category, Task, Subtask, Tag, TaskAudit
    ├── dao/                                  # DAOs: UserDAO, CategoryDAO, TaskDAO, SubtaskDAO, TagDAO
    └── ui/                                   # Vistas CLI: Auth, Main, Category, Task, Tag, Session, Validator
```
