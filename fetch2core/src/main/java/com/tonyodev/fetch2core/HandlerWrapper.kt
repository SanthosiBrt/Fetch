package com.tonyodev.fetch2core

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class HandlerWrapper(val namespace: String) {

    private val lock = Any()
    private var closed = false
    private var usageCounter = 0
    private val handler = {
        val handlerThread = HandlerThread(namespace)
        handlerThread.start()
        Handler(handlerThread.looper)
    }()

    fun post(runnable: () -> Unit) {
        synchronized(lock) {
            if (!closed) {
                handler.post(runnable)
            }
        }
    }

    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        synchronized(lock) {
            if (!closed) {
                handler.postDelayed(runnable, delayMillis)
            }
        }
    }

    fun removeCallbacks(runnable: Runnable) {
        synchronized(lock) {
            if (!closed) {
                handler.removeCallbacks(runnable)
            }
        }
    }

    fun incrementUsageCounter() {
        synchronized(lock) {
            if (!closed) {
                usageCounter += 1
            }
        }
    }

    fun decrementUsageCounter() {
        synchronized(lock) {
            if (!closed) {
                if (usageCounter == 0) {
                    return
                }
                usageCounter -= 1
            }
        }
    }

    fun usageCount(): Int {
        return synchronized(lock) {
            if (!closed) {
                usageCounter
            } else {
                0
            }
        }
    }

    fun getLooper(): Looper {
        return synchronized(lock) {
            handler.looper
        }
    }

    fun close() {
        synchronized(lock) {
            if (!closed) {
                closed = true
                try {
                    handler.removeCallbacksAndMessages(null)
                    handler.looper.quit()
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HandlerWrapper
        if (namespace != other.namespace) return false
        return true
    }

    override fun hashCode(): Int {
        return namespace.hashCode()
    }

}