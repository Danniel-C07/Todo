# TO-DO List — Base de Datos 2

Aplicación de consola Java con persistencia en MySQL. Permite registrar usuarios, iniciar sesión, gestionar categorías, crear y administrar tareas y agregar/completar subtareas.

## Requisitos

- JDK 25, según el POM compartido. IntelliJ y Maven deben usar una versión compatible con source/target 25.
- MySQL Server en ejecución y MySQL Workbench para instalar/verificar la base.
- IntelliJ IDEA con soporte Maven, o Maven instalado para usar sus comandos desde terminal.
- Dependencia JDBC declarada en el POM: mysql:mysql-connector-java:8.0.33.

## Instalación de la base

Los scripts suministrados por el equipo definen el esquema, el trigger y los datos iniciales. Ejecutar en este orden:

1. Script de esquema: crea todolist_db y las siete tablas.
2. Script del trigger after_task_status_update.
3. Script de datos de prueba, únicamente sobre una base nueva y sin cargas anteriores.

Los nombres originales enviados fueron 1_schema-3.sql, 2_triggers-4.sql y 3_data-5.sql. En el repositorio pueden tener otros sufijos; identifica su función antes de ejecutarlos.

El esquema usa CREATE TABLE IF NOT EXISTS, pero eso no migra tablas que ya existan con otra estructura. El script original de datos NO es repetible de forma segura: usa IDs fijos y usuarios únicos. No volver a ejecutarlo sobre una base utilizada ni borrar la base para evitar duplicados.

El registro desde la aplicación crea al usuario y su categoría General en una transacción. El segundo usuario del script original no tiene categoría: para la demostración es preferible registrar dos usuarios nuevos desde la aplicación.

Antes de repetir la instalación del trigger, comprobar si existe:

```sql
USE todolist_db;
SHOW TRIGGERS;
```

Si existe y su definición es correcta, no hace falta recrearlo. Si se acuerda reinstalarlo, agregar DROP TRIGGER IF EXISTS after_task_status_update antes de su CREATE TRIGGER; esto cambia el trigger, no borra la auditoría.

## Configuración local

Crear config.properties en la raíz del proyecto, junto a pom.xml:

```properties
db.url=jdbc:mysql://localhost:3306/todolist_db
db.user=USUARIO_LOCAL
db.password=TU_CONTRASEÑA_LOCAL
```

No subir ese archivo. config.example.properties solo debe incluir valores ficticios.

DatabaseConnection utiliza FileInputStream("config.properties"): la ubicación se resuelve desde el directorio de trabajo del proceso. En IntelliJ, configurar ese directorio como la raíz que contiene pom.xml y config.properties.

## Ejecutar en IntelliJ

1. Abrir la carpeta raíz como proyecto Maven.
2. Recargar Maven desde su panel.
3. Configurar Project SDK y el JDK del runner de Maven de acuerdo con el POM.
4. Abrir src/main/java/edu/programacion/Main.java.
5. Ejecutar edu.programacion.Main.

## Uso

1. Registrar usuario: crea también su categoría General.
2. Iniciar sesión.
3. Categorías: listar, crear, renombrar y eliminar categorías propias no predeterminadas y sin tareas.
4. Tareas: listar todas, crear, cambiar estado, completar, editar y eliminar con confirmación.
5. Opción 7 de tareas: listar por categoría.
6. Opción 8 de tareas: listar, agregar y completar subtareas.
7. Opción 0 del menú principal: cerrar sesión y volver a autenticación.

Fecha de entrada: AAAA-MM-DD HH:mm:ss, por ejemplo 2026-10-21 18:30:00. Enter deja la fecha vacía. Se rechazan fechas imposibles; se admiten fechas pasadas para permitir tareas vencidas.

Al editar, los valores nuevos reemplazan los anteriores: descripción vacía borra la descripción y fecha vacía elimina la fecha límite. No significan «conservar valor anterior».

## Diseño

- models: User, Category, Task, Subtask y Tag.
- enums: Status.
- db: DatabaseConnection existente.
- dao: UserDAO, CategoryDAO, TaskDAO y SubtaskDAO.
- ui: menús, InputValidator y SessionData.
- Main: punto de entrada.

Los DAO nuevos filtran por usuario a través de categories.id_user. No se acepta que escribir un ID permita operar sobre tareas ajenas. Las categorías predeterminadas se buscan por usuario, no suponiendo ID 1.

SessionData conserva una copia del usuario sin passwordHash. El objeto de autenticación original aún contiene el hash porque UserDAO existente lo recupera. La sesión se limpia en finally al terminar el menú principal. Esta es una sesión local de una aplicación CLI, no un mecanismo de autenticación de servidor web.

## Seguridad y límites

- PreparedStatement para parámetros de consultas.
- Comprobación de propiedad en las consultas de tareas y subtareas.
- Validación de enteros, fechas, títulos, correo de registro y longitud en los formularios modificados.
- Descripción limitada por la aplicación a 5000 caracteres.
- Credenciales externas al código y excluidas de Git.
- Las contraseñas se procesan con SHA2(..., 256), tal como está implementado por el equipo. Es hash, no cifrado reversible, y no es una opción adecuada de almacenamiento de contraseñas para producción. Una evolución debería usar Argon2id, bcrypt o PBKDF2 con sal y una migración compatible.
- La contraseña se introduce de forma visible en la consola actual.
- Las validaciones nuevas no convierten automáticamente todos los formularios antiguos en formularios con validación exhaustiva.
- La conexión compartida no se reconecta automáticamente si se pierde. Reiniciar la aplicación tras resolver la causa.
- Los nuevos DAO notifican errores mediante IllegalStateException, capturada por TaskMenuView. Los DAO antiguos mantienen su tratamiento previo de errores; no se reescriben silenciosamente.
