package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "photo_viewer_prefs")

class Prefs(private val context: Context) {

    companion object {
        private val KEY_FAVS = stringSetPreferencesKey("pv:favs")
        private val KEY_SORT = stringPreferencesKey("pv:sort")
        private val KEY_TILE = intPreferencesKey("pv:tile")
        private val KEY_P_STORAGE = booleanPreferencesKey("pv:p.storage")
        private val KEY_P_FOLDERS = booleanPreferencesKey("pv:p.folders")
        private val KEY_V_STRIP = booleanPreferencesKey("pv:v.strip")
        private val KEY_V_TOOLS = booleanPreferencesKey("pv:v.tools")
        private val KEY_TV = booleanPreferencesKey("pv:tv")
    }

    val favsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVS] ?: emptySet()
    }

    val sortFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SORT] ?: "name-asc"
    }

    val tileFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_TILE] ?: 180
    }

    val storagePanelFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_P_STORAGE] ?: true
    }

    val foldersPanelFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_P_FOLDERS] ?: true
    }

    val stripOpenFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_V_STRIP] ?: true
    }

    val toolsOpenFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_V_TOOLS] ?: true
    }

    val tvModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_TV] ?: true
    }

    suspend fun toggleFav(photoId: String): Boolean {
        var added = false
        context.dataStore.edit { prefs ->
            val cur = prefs[KEY_FAVS] ?: emptySet()
            if (cur.contains(photoId)) {
                prefs[KEY_FAVS] = cur - photoId
                added = false
            } else {
                prefs[KEY_FAVS] = cur + photoId
                added = true
            }
        }
        return added
    }

    suspend fun setSort(sort: String) {
        context.dataStore.edit { it[KEY_SORT] = sort }
    }

    suspend fun setTile(size: Int) {
        context.dataStore.edit { it[KEY_TILE] = size }
    }

    suspend fun setStoragePanel(open: Boolean) {
        context.dataStore.edit { it[KEY_P_STORAGE] = open }
    }

    suspend fun setFoldersPanel(open: Boolean) {
        context.dataStore.edit { it[KEY_P_FOLDERS] = open }
    }

    suspend fun setStripOpen(open: Boolean) {
        context.dataStore.edit { it[KEY_V_STRIP] = open }
    }

    suspend fun setToolsOpen(open: Boolean) {
        context.dataStore.edit { it[KEY_V_TOOLS] = open }
    }

    suspend fun setTvMode(on: Boolean) {
        context.dataStore.edit { it[KEY_TV] = on }
    }
}
