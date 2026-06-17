package com.fic.mobile_app_base_compose.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fic.mobile_app_base_compose.data.remote.AvisoApiDto
import com.fic.mobile_app_base_compose.ui.screens.Aviso

const val ROL_DOCENTE = "DOCENTE"
const val ROL_ALUMNO = "ALUMNO"

data class Usuario(
    val id: Int,
    val matricula: String,
    val nombre: String,
    val rol: String,
    val password: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                matricula TEXT NOT NULL UNIQUE,
                nombre TEXT NOT NULL,
                rol TEXT NOT NULL,
                password TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE avisos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                apiId INTEGER UNIQUE,
                docente TEXT NOT NULL,
                materia TEXT NOT NULL,
                mensaje TEXT NOT NULL,
                esUrgente INTEGER NOT NULL,
                archivoUri TEXT, -- NUEVA COLUMNA
                origen TEXT NOT NULL DEFAULT 'LOCAL',
                fecha TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """.trimIndent()
        )

        insertarUsuariosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS avisos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun validarUsuario(matricula: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, matricula, nombre, rol, password FROM usuarios WHERE matricula = ? AND password = ? LIMIT 1",
            arrayOf(matricula.trim(), password.trim())
        )

        cursor.use {
            return if (it.moveToFirst()) {
                Usuario(
                    id = it.getInt(0),
                    matricula = it.getString(1),
                    nombre = it.getString(2),
                    rol = it.getString(3),
                    password = it.getString(4)
                )
            } else {
                null
            }
        }
    }

    fun obtenerAvisos(): List<Aviso> {
        val avisos = mutableListOf<Aviso>()
        val db = readableDatabase
        // CORRECCIÓN: Agregamos 'archivoUri' al SELECT para poder recuperarlo de la base de datos
        val cursor = db.rawQuery(
            "SELECT id, docente, materia, mensaje, esUrgente, archivoUri FROM avisos ORDER BY id DESC",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                avisos.add(
                    Aviso(
                        id = it.getInt(0),
                        docente = it.getString(1),
                        materia = it.getString(2),
                        mensaje = it.getString(3),
                        esUrgente = it.getInt(4) == 1,
                        archivoUri = it.getString(5) // CORRECCIÓN: Asignamos la ruta de la imagen
                    )
                )
            }
        }
        return avisos
    }

    fun guardarAviso(docente: String, materia: String, mensaje: String, esUrgente: Boolean, archivoUri: String?) {
        val values = ContentValues().apply {
            put("docente", docente.trim())
            put("materia", materia.trim())
            put("mensaje", mensaje.trim())
            put("esUrgente", if (esUrgente) 1 else 0)
            put("origen", "LOCAL")
            put("archivoUri", archivoUri)
        }
        writableDatabase.insert("avisos", null, values)
    }

    // CAMBIO: Añadimos la operación "DELETE" para eliminar avisos.
    fun eliminarAviso(id: Int): Boolean {
        val db = writableDatabase
        val filasAfectadas = db.delete("avisos", "id = ?", arrayOf(id.toString()))
        return filasAfectadas > 0
    }

    fun guardarAvisosExternos(avisosApi: List<AvisoApiDto>) {
        val db = writableDatabase

        // Evita que se acumulen muchas publicaciones de prueba de la API.
        db.delete("avisos", "origen = ?", arrayOf("API"))

        avisosApi.forEach { avisoApi ->
            val values = ContentValues().apply {
                put("apiId", avisoApi.id)
                put("docente", "API externa REST")
                put("materia", "Noticia académica")
                put(
                    "mensaje",
                    "Aviso académico sincronizado desde una API externa.\n\n" +
                            "La aplicación descargó información en línea y la guardó en la base de datos local para poder consultarla sin conexión."
                )
                put("esUrgente", 0)
                put("origen", "API")
            }

            db.insertWithOnConflict(
                "avisos",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    private fun insertarUsuariosIniciales(db: SQLiteDatabase) {
        val usuarios = listOf(
            Usuario(0, "M001", "Mtro. Alexis Moises Montaño Araujo", ROL_DOCENTE, "123456"),
            Usuario(0, "M002", "Mtra. Sandra Luz Lara Devora", ROL_DOCENTE, "123456"),
            Usuario(0, "M003", "Mtro. Alejandro Guajardo Cruztitla", ROL_DOCENTE, "123456"),
            Usuario(0, "M004", "Mtro. Julio César Solís Velázquez", ROL_DOCENTE, "123456"),
            Usuario(0, "M005", "Mtro. Juan Ulisses Gallardo Zazueta", ROL_DOCENTE, "123456"),
            Usuario(0, "M006", "Mtra. Cynthia Patricia Villar Piña", ROL_DOCENTE, "123456"),
            Usuario(0, "A001", "Alumno Daniel Meza", ROL_ALUMNO, "123456"),
            Usuario(0, "A002", "Alumno Paul Rodriguez", ROL_ALUMNO, "123456"),
            Usuario(0, "A003", "Alumno Uriel Retamoza", ROL_ALUMNO, "123456"),
            Usuario(0, "A004", "Alumno Axel Alejandro", ROL_ALUMNO, "123456"),

            )

        usuarios.forEach { usuario ->
            val values = ContentValues().apply {
                put("matricula", usuario.matricula)
                put("nombre", usuario.nombre)
                put("rol", usuario.rol)
                put("password", usuario.password)
            }
            db.insert("usuarios", null, values)
        }
    }

    companion object {
        private const val DATABASE_NAME = "muro_academico.db"
        private const val DATABASE_VERSION = 3
    }
}
