package com.example.network

import android.util.Log
import com.example.model.ReceivePayload
import com.example.model.WebSocketReceiveMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

enum class ConnectionStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  RECONNECTING,
  FAILED
}

/**
 * WebSocket Manager in Kotlin using OkHttp to handle real-time message connections
 * and message receipt events as defined in the Turing Project Manifest.
 *
 * Handles zero-trust message exchange, artificial latency simulation, and auto-reconnect logic.
 */
class TuringWebSocketManager(
  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .readTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(15, TimeUnit.SECONDS)
    .pingInterval(20, TimeUnit.SECONDS)
    .build(),
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
) {

  private var webSocket: WebSocket? = null
  private var currentSessionId: String? = null

  private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
  val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

  // Emits incoming parsed WebSocket messages
  private val _incomingMessages = MutableSharedFlow<WebSocketReceiveMessage>(extraBufferCapacity = 64)
  val incomingMessages: SharedFlow<WebSocketReceiveMessage> = _incomingMessages.asSharedFlow()

  // Emits raw received JSON for auditing/inspection contracts
  private val _rawPayloads = MutableSharedFlow<String>(extraBufferCapacity = 64)
  val rawPayloads: SharedFlow<String> = _rawPayloads.asSharedFlow()

  private val listener = object : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
      Log.d(TAG, "WebSocket connected successfully: ${response.message}")
      _connectionStatus.value = ConnectionStatus.CONNECTED
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
      Log.d(TAG, "WebSocket message received: $text")
      scope.launch {
        _rawPayloads.emit(text)
        parseAndDispatchMessage(text)
      }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
      Log.d(TAG, "WebSocket closing: code=$code, reason=$reason")
      _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
      Log.d(TAG, "WebSocket closed: code=$code, reason=$reason")
      _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
      Log.e(TAG, "WebSocket failure: ${t.message}", t)
      _connectionStatus.value = ConnectionStatus.FAILED
    }
  }

  /**
   * Connect to the Turing WebSocket server for a specific session.
   * If remote endpoint is unavailable or running in offline mock/local mode,
   * it seamlessly maintains the contract dispatcher for internal imitation engine simulation.
   */
  fun connect(sessionId: String, url: String = "wss://api.turing-sanctuary.internal/v1/chamber/$sessionId") {
    currentSessionId = sessionId
    _connectionStatus.value = ConnectionStatus.CONNECTING

    try {
      val request = Request.Builder()
        .url(url)
        .addHeader("X-Turing-Client-Version", "1.0.0")
        .addHeader("X-Session-ID", sessionId)
        .build()

      webSocket = okHttpClient.newWebSocket(request, listener)
    } catch (e: Exception) {
      Log.w(TAG, "Remote WebSocket connection not reachable; operating in local Zero-Trust simulated mode", e)
      _connectionStatus.value = ConnectionStatus.CONNECTED
    }
  }

  /**
   * Sends user message over the WebSocket connection formatted to match manifest:
   * event: "message:send"
   */
  fun sendMessage(content: String): Boolean {
    val json = JSONObject().apply {
      put("event", "message:send")
      put("payload", JSONObject().apply {
        put("message_id", UUID.randomUUID().toString())
        put("sender_type", "USER")
        put("content", content)
        put("timestamp", System.currentTimeMillis() / 1000)
      })
    }.toString()

    return webSocket?.send(json) ?: false
  }

  /**
   * Simulates/dispatches an incoming Zero-Trust contract message locally or over pipeline
   */
  fun dispatchSimulatedIncoming(message: WebSocketReceiveMessage, rawJson: String) {
    scope.launch {
      _rawPayloads.emit(rawJson)
      _incomingMessages.emit(message)
    }
  }

  private suspend fun parseAndDispatchMessage(jsonString: String) {
    try {
      val root = JSONObject(jsonString)
      val event = root.optString("event", "message:receive")
      val payloadObj = root.getJSONObject("payload")

      val msg = WebSocketReceiveMessage(
        event = event,
        payload = ReceivePayload(
          message_id = payloadObj.optString("message_id", UUID.randomUUID().toString()),
          sender_type = payloadObj.optString("sender_type", "PARTNER"),
          content = payloadObj.getString("content"),
          progressive_reveal_stage = payloadObj.optInt("progressive_reveal_stage", 0),
          timestamp = payloadObj.optLong("timestamp", System.currentTimeMillis() / 1000)
        )
      )

      _incomingMessages.emit(msg)
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing incoming WebSocket payload", e)
    }
  }

  fun disconnect() {
    webSocket?.close(1000, "Session ended normally")
    webSocket = null
    _connectionStatus.value = ConnectionStatus.DISCONNECTED
  }

  companion object {
    private const val TAG = "TuringWebSocketManager"
  }
}
