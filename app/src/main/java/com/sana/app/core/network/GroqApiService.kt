package com.sana.app.core.network

import retrofit2.Response
import retrofit2.http.*

/**
 * 🌿 SANA - Servicio API de Groq (Chat IA)
 * 
 * Cliente Retrofit para la API de Groq Cloud.
 * Groq proporciona acceso gratuito a modelos de lenguaje (LLMs)
 * con un límite generoso de requests por minuto.
 * 
 * Modelos disponibles (capa gratuita):
 * - mixtral-8x7b-32768: Modelo general potente (32k tokens)
 * - llama2-70b-4096: Modelo de Meta (4k tokens)
 * - gemma-7b-it: Modelo de Google optimizado
 * 
 * Documentación: https://console.groq.com/docs
 * 
 * ⚠️ IMPORTANTE: Obtén tu API key gratuita en https://console.groq.com
 * La key se configura en app/build.gradle.kts -> GROQ_API_KEY
 */
interface GroqApiService {

    /**
     * Enviar mensaje al chat y obtener respuesta del asistente IA
     * 
     * @param auth Token de autorización "Bearer {API_KEY}"
     * @param request Cuerpo de la solicitud con mensajes y configuración
     * @return Respuesta del modelo con el mensaje generado
     */
    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") auth: String,
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>

    /**
     * Obtener los modelos disponibles (para diagnóstico)
     */
    @GET("openai/v1/models")
    suspend fun getAvailableModels(
        @Header("Authorization") auth: String
    ): Response<GroqModelsResponse>
}

// ============================================
// MODELOS DE SOLICITUD (REQUEST)
// ============================================

/**
 * Cuerpo de solicitud para el chat de Groq
 * 
 * @param model Modelo a utilizar (default: mixtral-8x7b-32768)
 * @param messages Historial de mensajes de la conversación
 * @param temperature Creatividad de las respuestas (0.0-2.0)
 * @param max_tokens Longitud máxima de la respuesta
 * @param top_p Muestreo nucleus (0.0-1.0)
 * @param stream Si se debe transmitir la respuesta en streaming
 * @param stop Secuencias que detienen la generación
 */
data class GroqChatRequest(
    val model: String = "mixtral-8x7b-32768",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024,
    val top_p: Double = 1.0,
    val stream: Boolean = false,
    val stop: List<String>? = null
)

/**
 * Mensaje individual en la conversación
 * 
 * @param role Rol: "system", "user", "assistant"
 * @param content Texto del mensaje
 */
data class GroqMessage(
    val role: String,
    val content: String
)

// ============================================
// MODELOS DE RESPUESTA (RESPONSE)
// ============================================

/**
 * Respuesta del chat de Groq
 */
data class GroqChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<GroqChoice>,
    val usage: GroqUsage?
)

/**
 * Opción de respuesta generada por el modelo
 */
data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    val finish_reason: String? // "stop", "length", "content_filter"
)

/**
 * Estadísticas de uso de tokens
 */
data class GroqUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

/**
 * Respuesta con lista de modelos disponibles
 */
data class GroqModelsResponse(
    val `object`: String,
    val data: List<GroqModel>
)

/**
 * Información de un modelo disponible
 */
data class GroqModel(
    val id: String,
    val `object`: String,
    val created: Long,
    val owned_by: String
)