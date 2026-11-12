package com.example.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Device
import com.example.data.Folder
import com.example.data.Formatters
import com.example.data.Photo
import com.example.data.Prefs
import com.example.data.StorageRepository
import com.example.data.VolumeMonitor
import com.example.remote.RemoteAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ModalType {
    NONE,
    DEVICE_PROPERTIES,
    SHORTCUTS
}

enum class PopoverType {
    NONE,
    SORT,
    DEVICE_OPTIONS
}

data class SuggestionItem(
    val photo: Photo,
    val folderName: String,
    val score: Int
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StorageRepository(application)
    private val prefs = Prefs(application)

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    val favorites: StateFlow<Set<String>> = prefs.favsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptySet()
    )

    val sort: StateFlow<String> = prefs.sortFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, "name-asc"
    )

    val tileSize: StateFlow<Int> = prefs.tileFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 180
    )

    val storagePanelOpen: StateFlow<Boolean> = prefs.storagePanelFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )

    val foldersPanelOpen: StateFlow<Boolean> = prefs.foldersPanelFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )

    val tvMode: StateFlow<Boolean> = prefs.tvModeFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )

    private val _currentRoute = MutableStateFlow<Route>(Route.Home)
    val currentRoute: StateFlow<Route> = _currentRoute.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viewerState = MutableStateFlow(ViewerState())
    val viewerState: StateFlow<ViewerState> = _viewerState.asStateFlow()

    private val _activeModal = MutableStateFlow(ModalType.NONE)
    val activeModal: StateFlow<ModalType> = _activeModal.asStateFlow()

    private val _activePopover = MutableStateFlow(PopoverType.NONE)
    val activePopover: StateFlow<PopoverType> = _activePopover.asStateFlow()

    private val _deviceForOptions = MutableStateFlow<Device?>(null)
    val deviceForOptions: StateFlow<Device?> = _deviceForOptions.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _focusedId = MutableStateFlow<String?>(null)
    val focusedId: StateFlow<String?> = _focusedId.asStateFlow()

    private var slideshowJob: Job? = null
    private var toastJob: Job? = null
    private var badgeJob: Job? = null

    private var lastViewedPhotoId: String? = null

    private val volumeMonitor = VolumeMonitor(
        context = application,
        onDeviceMounted = { name ->
            showToast("$name connected")
            refreshDevices()
        },
        onDeviceUnmounted = { name ->
            showToast("$name removed")
            val cur = _currentRoute.value
            if (cur is Route.Device && cur.deviceId != "local" || cur is Route.Folder && cur.deviceId != "local") {
                navigateTo(Route.Home)
            }
            refreshDevices()
        }
    )

    init {
        volumeMonitor.start()
        refreshDevices()
    }

    override fun onCleared() {
        super.onCleared()
        volumeMonitor.stop()
        stopSlideshow()
    }

    fun refreshDevices() {
        viewModelScope.launch {
            val list = repository.loadDevices()
            _devices.value = list
            validateCurrentRoute(list)
        }
    }

    private fun validateCurrentRoute(devs: List<Device>) {
        when (val r = _currentRoute.value) {
            is Route.Device -> {
                val d = devs.find { it.id == r.deviceId }
                if (d == null || !d.connected) {
                    _currentRoute.value = Route.Home
                }
            }
            is Route.Folder -> {
                val d = devs.find { it.id == r.deviceId }
                if (d == null || !d.connected) {
                    _currentRoute.value = Route.Home
                } else if (d.folders.none { it.id == r.folderId }) {
                    _currentRoute.value = Route.Device(r.deviceId)
                }
            }
            else -> {}
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(2400)
            _toastMessage.value = null
        }
    }

    fun flashBadge(text: String) {
        _viewerState.value = _viewerState.value.copy(badgeText = text)
        badgeJob?.cancel()
        badgeJob = viewModelScope.launch {
            delay(900)
            _viewerState.value = _viewerState.value.copy(badgeText = null)
        }
    }

    fun setFocusedId(id: String?) {
        _focusedId.value = id
    }

    fun navigateTo(route: Route) {
        closePopover()
        closeModal()
        closeDrawer()
        _searchQuery.value = ""
        _currentRoute.value = route
        // Default focus for new screen
        when (route) {
            is Route.Home -> _focusedId.value = _devices.value.firstOrNull()?.id
            is Route.Device -> {
                val d = _devices.value.find { it.id == route.deviceId }
                _focusedId.value = d?.folders?.firstOrNull()?.id
            }
            is Route.Folder -> {
                val d = _devices.value.find { it.id == route.deviceId }
                val f = d?.folders?.find { it.id == route.folderId }
                _focusedId.value = f?.photos?.firstOrNull()?.id
            }
            is Route.Fav -> {
                val all = getAllPhotos()
                val favPhotos = all.filter { favorites.value.contains(it.id) }
                _focusedId.value = favPhotos.firstOrNull()?.id
            }
        }
    }

    fun openViewer(photo: Photo, list: List<Photo>) {
        lastViewedPhotoId = photo.id
        val idx = list.indexOfFirst { it.id == photo.id }.coerceAtLeast(0)
        _viewerState.value = ViewerState(
            open = true,
            list = list,
            idx = idx,
            scale = 1.0f,
            tx = 0f,
            ty = 0f,
            rotation = 0f,
            fit = 1.0f,
            naturalW = photo.width,
            naturalH = photo.height,
            isSlideshow = false,
            uiMode = false,
            showInfo = false,
            toolbarOpen = true,
            stripOpen = true,
            badgeText = null
        )
    }

    fun closeViewer() {
        stopSlideshow()
        _viewerState.value = _viewerState.value.copy(open = false, showInfo = false)
        // Restore focus to last viewed photo
        if (lastViewedPhotoId != null) {
            _focusedId.value = lastViewedPhotoId
        }
    }

    fun toggleFavorite(photoId: String) {
        viewModelScope.launch {
            val added = prefs.toggleFav(photoId)
            showToast(if (added) "Added to favorites" else "Removed from favorites")
        }
    }

    fun setSort(sortKey: String) {
        viewModelScope.launch {
            prefs.setSort(sortKey)
            closePopover()
        }
    }

    fun cycleTileSize() {
        viewModelScope.launch {
            val sizes = listOf(130, 180, 250)
            val current = tileSize.value
            val next = sizes[(sizes.indexOf(current) + 1) % sizes.size]
            prefs.setTile(next)
            val label = when (next) {
                130 -> "Small tiles"
                180 -> "Medium tiles"
                else -> "Large tiles"
            }
            showToast(label)
        }
    }

    fun setTileSize(size: Int) {
        viewModelScope.launch {
            prefs.setTile(size)
        }
    }

    fun toggleStoragePanel() {
        viewModelScope.launch {
            prefs.setStoragePanel(!storagePanelOpen.value)
        }
    }

    fun toggleFoldersPanel() {
        viewModelScope.launch {
            prefs.setFoldersPanel(!foldersPanelOpen.value)
        }
    }

    fun toggleTvMode() {
        viewModelScope.launch {
            val next = !tvMode.value
            prefs.setTvMode(next)
            showToast(if (next) "TV remote mode on" else "TV remote mode off")
        }
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun openDeviceOptions(device: Device) {
        _deviceForOptions.value = device
        _activePopover.value = PopoverType.DEVICE_OPTIONS
    }

    fun openSortPopover() {
        _activePopover.value = PopoverType.SORT
    }

    fun closePopover() {
        _activePopover.value = PopoverType.NONE
        _deviceForOptions.value = null
    }

    fun openModal(type: ModalType) {
        closePopover()
        _activeModal.value = type
    }

    fun closeModal() {
        _activeModal.value = ModalType.NONE
    }

    fun openDrawer() {
        closePopover()
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
    }

    fun toggleDrawer() {
        if (_isDrawerOpen.value) closeDrawer() else openDrawer()
    }

    fun safelyEjectDevice(device: Device) {
        closePopover()
        repository.setDeviceEjected(device.id)
        showToast("${device.name} ejected")
        val cur = _currentRoute.value
        if (cur is Route.Device && cur.deviceId == device.id || cur is Route.Folder && cur.deviceId == device.id) {
            navigateTo(Route.Home)
        }
        refreshDevices()
    }

    fun scanDevices() {
        closePopover()
        repository.reconnectAll()
        showToast("Scanning for storage devices…")
        refreshDevices()
    }

    // Viewer actions
    fun nextPhoto(step: Int = 1) {
        val s = _viewerState.value
        if (s.list.isEmpty()) return
        val newIdx = (s.idx + step).coerceIn(0, s.list.size - 1)
        if (newIdx != s.idx) {
            showPhotoAtIndex(newIdx)
        }
    }

    fun prevPhoto(step: Int = 1) {
        val s = _viewerState.value
        if (s.list.isEmpty()) return
        val newIdx = (s.idx - step).coerceIn(0, s.list.size - 1)
        if (newIdx != s.idx) {
            showPhotoAtIndex(newIdx)
        }
    }

    fun showPhotoAtIndex(idx: Int) {
        val s = _viewerState.value
        val photo = s.list.getOrNull(idx) ?: return
        lastViewedPhotoId = photo.id
        _viewerState.value = s.copy(
            idx = idx,
            scale = 1.0f,
            tx = 0f,
            ty = 0f,
            rotation = 0f,
            naturalW = photo.width,
            naturalH = photo.height
        )
        if (s.isSlideshow) {
            restartSlideshowTimer()
        }
    }

    fun toggleZoomCenter() {
        val s = _viewerState.value
        if (s.isZoomed) {
            resetView()
        } else {
            val targetScale = maxOf(2.2f, 1f / s.fit).coerceAtMost(8f)
            zoomTo(targetScale)
        }
    }

    fun zoomTo(newScale: Float) {
        val s = _viewerState.value
        val clamped = newScale.coerceIn(0.25f, 10f)
        val limit = panLimit(clamped, s.rotation)
        val newTx = s.tx.coerceIn(-limit.first, limit.first)
        val newTy = s.ty.coerceIn(-limit.second, limit.second)
        _viewerState.value = s.copy(scale = clamped, tx = newTx, ty = newTy)
        flashBadge("${(clamped * s.fit * 100).toInt()}%")
    }

    fun resetView() {
        val s = _viewerState.value
        _viewerState.value = s.copy(scale = 1.0f, tx = 0f, ty = 0f)
        flashBadge("Fit")
    }

    fun rotate() {
        val s = _viewerState.value
        val newRot = s.rotation + 90f
        _viewerState.value = s.copy(rotation = newRot, scale = 1.0f, tx = 0f, ty = 0f)
        flashBadge("Rotated")
    }

    fun pan(dx: Float, dy: Float) {
        val s = _viewerState.value
        if (!s.isZoomed) return
        val limit = panLimit(s.scale, s.rotation)
        val newTx = (s.tx + dx).coerceIn(-limit.first, limit.first)
        val newTy = (s.ty + dy).coerceIn(-limit.second, limit.second)
        _viewerState.value = s.copy(tx = newTx, ty = newTy)
    }

    private fun panLimit(scale: Float, rotation: Float): Pair<Float, Float> {
        val s = _viewerState.value
        val isOdd = (Math.abs(rotation.toInt() / 90) % 2 == 1)
        val pw = if (isOdd) s.naturalH else s.naturalW
        val ph = if (isOdd) s.naturalW else s.naturalH
        val bw = pw * s.fit * scale
        val bh = ph * s.fit * scale
        val mx = maxOf(0f, (bw + 1920f) / 2f - 100f)
        val my = maxOf(0f, (bh + 1080f) / 2f - 100f)
        return Pair(mx, my)
    }

    fun toggleDetails() {
        val s = _viewerState.value
        _viewerState.value = s.copy(showInfo = !s.showInfo)
    }

    fun toggleToolbar() {
        val s = _viewerState.value
        _viewerState.value = s.copy(toolbarOpen = !s.toolbarOpen)
    }

    fun toggleStrip() {
        val s = _viewerState.value
        _viewerState.value = s.copy(stripOpen = !s.stripOpen)
    }

    fun setUiMode(ui: Boolean) {
        _viewerState.value = _viewerState.value.copy(uiMode = ui)
    }

    fun toggleSlideshow() {
        val s = _viewerState.value
        if (s.isSlideshow) {
            stopSlideshow(announce = true)
        } else {
            if (s.list.size < 2) {
                showToast("Need at least 2 photos")
                return
            }
            startSlideshow()
        }
    }

    private fun startSlideshow() {
        _viewerState.value = _viewerState.value.copy(isSlideshow = true)
        flashBadge("Slideshow started")
        restartSlideshowTimer()
    }

    fun stopSlideshow(announce: Boolean = false) {
        slideshowJob?.cancel()
        slideshowJob = null
        if (_viewerState.value.isSlideshow) {
            _viewerState.value = _viewerState.value.copy(isSlideshow = false)
            if (announce) flashBadge("Slideshow stopped")
        }
    }

    private fun restartSlideshowTimer() {
        slideshowJob?.cancel()
        slideshowJob = viewModelScope.launch {
            while (true) {
                delay(3500)
                val s = _viewerState.value
                if (s.list.isNotEmpty()) {
                    val next = (s.idx + 1) % s.list.size
                    showPhotoAtIndex(next)
                }
            }
        }
    }

    fun downloadCurrentPhoto() {
        val photo = _viewerState.value.currentPhoto ?: return
        viewModelScope.launch {
            showToast("Download started")
            val success = repository.saveToDownloads(photo)
            if (success) {
                showToast("Saved to Downloads")
            } else {
                showToast("Failed to save photo")
            }
        }
    }

    fun getAllPhotos(): List<Photo> {
        return _devices.value.filter { it.connected }.flatMap { d ->
            d.folders.flatMap { f -> f.photos }
        }
    }

    fun getVisiblePhotosForCurrentRoute(): List<Photo> {
        val q = Formatters.normalize(_searchQuery.value.trim())
        val tokens = q.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val rawList = when (val r = _currentRoute.value) {
            is Route.Folder -> {
                val d = _devices.value.find { it.id == r.deviceId }
                d?.folders?.find { it.id == r.folderId }?.photos ?: emptyList()
            }
            is Route.Fav -> {
                val favIds = favorites.value
                getAllPhotos().filter { favIds.contains(it.id) }
            }
            else -> emptyList()
        }

        val filtered = if (tokens.isEmpty()) {
            rawList
        } else {
            rawList.filter { p ->
                val nameNorm = Formatters.normalize(p.name)
                tokens.all { nameNorm.contains(it) }
            }
        }

        val sortKey = sort.value
        return filtered.sortedWith(getPhotoComparator(sortKey))
    }

    fun getVisibleFoldersForDevice(deviceId: String): List<Folder> {
        val d = _devices.value.find { it.id == deviceId } ?: return emptyList()
        val q = Formatters.normalize(_searchQuery.value.trim())
        val tokens = q.split("\\s+".toRegex()).filter { it.isNotEmpty() }

        val filtered = if (tokens.isEmpty()) {
            d.folders
        } else {
            d.folders.filter { f ->
                val folderNorm = Formatters.normalize(f.name)
                val matchesFolder = tokens.all { folderNorm.contains(it) }
                val matchesPhotos = f.photos.any { p ->
                    val photoNorm = Formatters.normalize(p.name)
                    tokens.all { photoNorm.contains(it) }
                }
                matchesFolder || matchesPhotos
            }
        }

        val sortKey = sort.value
        return filtered.sortedWith(getFolderComparator(sortKey))
    }

    private fun getPhotoComparator(sortKey: String): Comparator<Photo> {
        return when (sortKey) {
            "name-desc" -> Comparator { a, b -> Formatters.naturalCompare(b.name, a.name) }
            "new" -> Comparator { a, b -> b.date.compareTo(a.date) }
            "old" -> Comparator { a, b -> a.date.compareTo(b.date) }
            "big" -> Comparator { a, b -> b.size.compareTo(a.size) }
            "small" -> Comparator { a, b -> a.size.compareTo(b.size) }
            else -> Comparator { a, b -> Formatters.naturalCompare(a.name, b.name) }
        }
    }

    private fun getFolderComparator(sortKey: String): Comparator<Folder> {
        return when (sortKey) {
            "name-desc" -> Comparator { a, b -> Formatters.naturalCompare(b.name, a.name) }
            "new" -> Comparator { a, b -> b.date.compareTo(a.date) }
            "old" -> Comparator { a, b -> a.date.compareTo(b.date) }
            "big" -> Comparator { a, b -> b.size.compareTo(a.size) }
            "small" -> Comparator { a, b -> a.size.compareTo(b.size) }
            else -> Comparator { a, b -> Formatters.naturalCompare(a.name, b.name) }
        }
    }

    // Remote D-pad event handling
    fun onRemoteAction(action: RemoteAction): Boolean {
        // 1. Modals & Popovers priority
        if (_activeModal.value != ModalType.NONE) {
            if (action == RemoteAction.BACK || action == RemoteAction.OK) {
                closeModal()
                return true
            }
            return false
        }

        if (_activePopover.value != PopoverType.NONE) {
            if (action == RemoteAction.BACK) {
                closePopover()
                return true
            }
            return false
        }

        if (_isDrawerOpen.value) {
            if (action == RemoteAction.BACK || action == RemoteAction.MENU) {
                closeDrawer()
                return true
            }
            return false
        }

        // 2. Viewer active
        val v = _viewerState.value
        if (v.open) {
            if (v.showInfo) {
                if (action == RemoteAction.BACK || action == RemoteAction.OK || action == RemoteAction.BLUE) {
                    toggleDetails()
                    return true
                }
                return true
            }

            // Stage mode vs UI mode
            if (!v.uiMode) {
                when (action) {
                    RemoteAction.LEFT -> {
                        if (v.isZoomed) pan(1920f * 0.25f, 0f) else prevPhoto()
                        return true
                    }
                    RemoteAction.RIGHT -> {
                        if (v.isZoomed) pan(-1920f * 0.25f, 0f) else nextPhoto()
                        return true
                    }
                    RemoteAction.UP -> {
                        if (v.isZoomed) pan(0f, 1080f * 0.25f) else {
                            _viewerState.value = v.copy(uiMode = true, toolbarOpen = true)
                        }
                        return true
                    }
                    RemoteAction.DOWN -> {
                        if (v.isZoomed) pan(0f, -1080f * 0.25f) else {
                            _viewerState.value = v.copy(uiMode = true, stripOpen = true)
                        }
                        return true
                    }
                    RemoteAction.OK -> {
                        toggleZoomCenter()
                        return true
                    }
                    RemoteAction.BACK -> {
                        if (v.isZoomed) {
                            resetView()
                        } else {
                            closeViewer()
                        }
                        return true
                    }
                    RemoteAction.INFO, RemoteAction.BLUE -> {
                        toggleDetails()
                        return true
                    }
                    RemoteAction.RED -> {
                        v.currentPhoto?.let { toggleFavorite(it.id) }
                        return true
                    }
                    RemoteAction.GREEN, RemoteAction.PLAY -> {
                        toggleSlideshow()
                        return true
                    }
                    RemoteAction.PAUSE, RemoteAction.STOP -> {
                        stopSlideshow(announce = true)
                        return true
                    }
                    RemoteAction.YELLOW -> {
                        rotate()
                        return true
                    }
                    RemoteAction.MENU -> {
                        openDrawer()
                        return true
                    }
                    RemoteAction.CH_UP, RemoteAction.NEXT -> {
                        nextPhoto()
                        return true
                    }
                    RemoteAction.CH_DOWN, RemoteAction.PREV -> {
                        prevPhoto()
                        return true
                    }
                    RemoteAction.FF -> {
                        nextPhoto(5)
                        return true
                    }
                    RemoteAction.RW -> {
                        prevPhoto(5)
                        return true
                    }
                    RemoteAction.ZOOM -> {
                        toggleZoomCenter()
                        return true
                    }
                    RemoteAction.ZOOM_IN, RemoteAction.D3 -> {
                        zoomTo(v.scale * 1.4f)
                        return true
                    }
                    RemoteAction.ZOOM_OUT, RemoteAction.D1 -> {
                        zoomTo(v.scale / 1.4f)
                        return true
                    }
                    RemoteAction.D5, RemoteAction.D0 -> {
                        resetView()
                        return true
                    }
                    RemoteAction.D7 -> {
                        rotate()
                        return true
                    }
                    RemoteAction.D9 -> {
                        toggleSlideshow()
                        return true
                    }
                    RemoteAction.D4 -> {
                        prevPhoto()
                        return true
                    }
                    RemoteAction.D6 -> {
                        nextPhoto()
                        return true
                    }
                    RemoteAction.D2 -> {
                        pan(0f, 1080f * 0.25f)
                        return true
                    }
                    RemoteAction.D8 -> {
                        pan(0f, -1080f * 0.25f)
                        return true
                    }
                    else -> {}
                }
            } else {
                // UI mode
                if (action == RemoteAction.BACK) {
                    _viewerState.value = v.copy(uiMode = false)
                    return true
                }
            }
            return false
        }

        // 3. App scope (Home / Browser)
        when (action) {
            RemoteAction.MENU -> {
                toggleDrawer()
                return true
            }
            RemoteAction.GREEN -> {
                openSortPopover()
                return true
            }
            RemoteAction.YELLOW -> {
                cycleTileSize()
                return true
            }
            RemoteAction.RED -> {
                // Toggle favorite of focused photo
                val fid = _focusedId.value
                if (fid != null) {
                    val p = getAllPhotos().find { it.id == fid }
                    if (p != null) {
                        toggleFavorite(p.id)
                        return true
                    }
                }
            }
            RemoteAction.INFO -> {
                val fid = _focusedId.value
                if (fid != null) {
                    val dev = _devices.value.find { it.id == fid }
                    if (dev != null) {
                        openDeviceOptions(dev)
                        return true
                    }
                    val photo = getAllPhotos().find { it.id == fid }
                    if (photo != null) {
                        val photos = getVisiblePhotosForCurrentRoute()
                        openViewer(photo, photos)
                        toggleDetails()
                        return true
                    }
                }
            }
            RemoteAction.BACK -> {
                when (val r = _currentRoute.value) {
                    is Route.Folder -> {
                        lastViewedPhotoId = null
                        _focusedId.value = r.folderId
                        navigateTo(Route.Device(r.deviceId))
                        return true
                    }
                    is Route.Device -> {
                        lastViewedPhotoId = null
                        _focusedId.value = r.deviceId
                        navigateTo(Route.Home)
                        return true
                    }
                    is Route.Fav -> {
                        lastViewedPhotoId = null
                        _focusedId.value = "fav"
                        navigateTo(Route.Home)
                        return true
                    }
                    is Route.Home -> {
                        // Let system handle exit
                        return false
                    }
                }
            }
            else -> {}
        }

        return false
    }
}
