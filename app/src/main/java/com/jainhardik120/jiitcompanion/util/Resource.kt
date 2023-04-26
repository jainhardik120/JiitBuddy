package com.jainhardik120.jiitcompanion.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val isOnline: Boolean = false) {
    class Success<T>(data: T?, isOnline: Boolean): Resource<T>(data = data, isOnline = isOnline)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}