package com.example.biotamontchi.data


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
private val Context.dataStore by preferencesDataStore("jardin_datastore")

