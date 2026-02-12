package com.example.myapplication.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.BoardRepo
import com.example.myapplication.data.repo.ForecastRepo
import com.example.myapplication.data.repo.PlacesRepo
import com.example.myapplication.domain.model.BoardItem
import com.example.myapplication.domain.model.Forecast
import com.example.myapplication.domain.model.Place
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUi(
    val q: String = "",
    val items: List<Place> = emptyList(),
    val page: Int = 0,
    val loading: Boolean = false,
    val err: String? = null
)

class SearchVm(private val repo: PlacesRepo) : ViewModel() {
    private val _ui = MutableStateFlow(SearchUi())
    val ui: StateFlow<SearchUi> = _ui.asStateFlow()

    private var job: Job? = null
    private val pageSize = 20

    fun setQuery(s: String) {
        _ui.update { it.copy(q = s, page = 0, items = emptyList(), err = null) }
        debounced(reset = true)
    }

    fun loadMore() = debounced(reset = false)

    private fun debounced(reset: Boolean) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(400)
            val q = _ui.value.q.trim()
            if (q.isEmpty()) return@launch

            val nextPage = if (reset) 0 else _ui.value.page + 1
            val offset = nextPage * pageSize
            _ui.update { it.copy(loading = true, err = null) }

            try {
                val remote = repo.searchRemote(q, pageSize, offset)
                val merged = if (reset) remote else _ui.value.items + remote
                _ui.update { it.copy(items = merged, page = nextPage, loading = false) }
            } catch (_: Exception) {
                val local = repo.searchLocal(q, pageSize, offset)
                val merged = if (reset) local else _ui.value.items + local
                _ui.update { it.copy(items = merged, page = nextPage, loading = false, err = "Offline/cache") }
            }
        }
    }
}

data class DetailsUi(
    val loading: Boolean = false,
    val forecast: Forecast? = null,
    val err: String? = null
)

class DetailsVm(
    private val repo: ForecastRepo,
    private val lat: Double,
    private val lon: Double
) : ViewModel() {
    private val _ui = MutableStateFlow(DetailsUi(loading = true))
    val ui: StateFlow<DetailsUi> = _ui.asStateFlow()

    init { refresh(forceRemote = false) }

    fun refresh(forceRemote: Boolean) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, err = null) }
            try {
                val f = repo.getForecast(lat, lon, forceRemote)
                _ui.update { it.copy(loading = false, forecast = f) }
            } catch (_: Exception) {
                _ui.update { it.copy(loading = false, err = "Failed") }
            }
        }
    }
}

data class BoardUi(
    val uid: String = "",
    val ready: Boolean = false,
    val items: List<BoardItem> = emptyList(),
    val input: String = "",
    val editId: String? = null,
    val err: String? = null
)

class BoardVm(private val repo: BoardRepo) : ViewModel() {
    private val _ui = MutableStateFlow(BoardUi())
    val ui: StateFlow<BoardUi> = _ui.asStateFlow()

    init {
        repo.ensureAnon(
            onOk = { uid ->
                _ui.update { it.copy(uid = uid, ready = true) }
                viewModelScope.launch {
                    repo.observe(uid).collect { xs -> _ui.update { it.copy(items = xs) } }
                }
            },
            onErr = { _ui.update { it.copy(err = "Auth error") } }
        )
    }

    fun setInput(s: String) = _ui.update { it.copy(input = s, err = null) }
    fun pickEdit(x: BoardItem) = _ui.update { it.copy(editId = x.id, input = x.text) }
    fun cancelEdit() = _ui.update { it.copy(editId = null, input = "") }

    fun save() {
        val st = _ui.value
        if (!st.ready) return
        if (st.input.isBlank()) { _ui.update { it.copy(err = "Empty") }; return }
        if (st.editId == null) repo.add(st.uid, st.input) else repo.update(st.uid, st.editId, st.input)
        cancelEdit()
    }

    fun delete(id: String) {
        val st = _ui.value
        if (!st.ready) return
        repo.delete(st.uid, id)
        if (st.editId == id) cancelEdit()
    }
}
