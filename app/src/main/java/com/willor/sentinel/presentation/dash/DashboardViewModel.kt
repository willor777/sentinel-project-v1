package com.willor.sentinel.presentation.dash

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willor.ktstockdata.misc_data.dataobjects.MajorFuturesData
import com.willor.ktstockdata.watchlists_data.WatchlistOptions
import com.willor.ktstockdata.watchlists_data.dataobjects.Watchlist
import com.willor.lib_data.domain.abstraction.Resource
import com.willor.lib_data.domain.abstraction.IRepo
import com.willor.lib_data.utils.handleErrorsToLog
import com.willor.lib_data.data.local.local_preferences.AppPreferences
import com.willor.lib_data.data.local.local_preferences.DatastorePrefsManager
import com.willor.lib_data.utils.printToDEBUGTEMP
import com.willor.sentinel.utils.periodicCoroutineRepeatOnFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: IRepo,
    private val prefsManager: DatastorePrefsManager
): ViewModel(){


    private var _appPreferences = MutableStateFlow<AppPreferences?>(null)
    val appPreferences get() = _appPreferences.asStateFlow()

    private var _curSentinelWatchlist = MutableStateFlow<List<String>?>(null)
    val curSentinelWatchlist get() = _curSentinelWatchlist.asStateFlow()

    private var _futuresDataFlow = MutableStateFlow<MajorFuturesData?>(null)
    val futuresDataFlow get() = _futuresDataFlow.asStateFlow()
    private var futuresUpdater: Job     // Can be used to cancel the Futures Updater

    private var defaultWatchlistName = WatchlistOptions.MOST_ACTIVE
    private var _watchlistDataFlow = MutableStateFlow<Watchlist?>(null)
    val watchlistDataFlow get() = _watchlistDataFlow.asStateFlow()
    private var watchlistUpdater: Job

    init{
        futuresUpdater = periodicCoroutineRepeatOnFailure(
            delayTime = 60000,
            scope = viewModelScope,
            dispatcher = Dispatchers.IO
        ){
            updateFutures()
        }

        watchlistUpdater = periodicCoroutineRepeatOnFailure(
            delayTime = 60000,
            scope = viewModelScope,
            dispatcher = Dispatchers.IO
        ){
            loadYfWatchlist(defaultWatchlistName)
        }


        viewModelScope.launch(Dispatchers.IO){
            getAppPreferences()
            printToDEBUGTEMP("Loaded Preferences" + appPreferences.value.toString())
        }

        loadSentinelWatchlist()
    }

    /**
     * Called to update the _futuresDataFlow.
     */
    private suspend fun updateFutures(): Boolean{

        // Flag showing whether or not task was a success
        var collected = false

        repo.getFuturesData().handleErrorsToLog().collect{
            when(it){
                is Resource.Loading -> {
                    Log.d("INFO", "Received Loading for getFuturesData()")
                }

                is Resource.Success -> {
                    _futuresDataFlow.value = it.data!!
                    collected = true
                    Log.d("INFO", "Collected futures data")
                }

                is Resource.Error -> {
                    Log.d("INFO", "Received Error for getFuturesData()")
                }
            }
        }
        return collected
    }


    private suspend fun getAppPreferences(){
        _appPreferences.value = prefsManager.getFromDatastore().first()
    }


    private fun saveAppPreferences(){
        viewModelScope.launch(Dispatchers.IO){
            prefsManager.saveToDatastore(appPreferences.value!!)
        }
    }


    /**
     * Used to load either "Most Active" "Big Gainers" "Big Losers"
     */
    private fun loadYfWatchlist(wlName: WatchlistOptions? = null): Boolean{
        var collected = false

        viewModelScope.launch(Dispatchers.IO){

            val targetWatchlist = if (wlName == null){
                WatchlistOptions.MOST_ACTIVE
            } else{
                wlName
            }

            repo.getWatchlist(targetWatchlist).collect{
                when(it){
                    is Resource.Loading -> {
                        Log.d("INFO", "Received Loading for getWatchlist()")
                    }

                    is Resource.Success -> {
                        _watchlistDataFlow.value = it.data
                        collected = true
                        Log.d("INFO", "Collected watchlist data")
                    }

                    is Resource.Error -> {
                        Log.d("INFO", "Received Error for getWatchlist()")
                    }
                }
            }
        }
        return collected
    }


    /**
     * Used to change what watchlist is displayed. Cancels the previous updater and creates a new
     * one.
     */
    fun changeYfWatchlist(wl: WatchlistOptions){
        viewModelScope.launch(Dispatchers.IO){

            // Cancel previous updater
            watchlistUpdater.cancelAndJoin()

            // Create new updater
            watchlistUpdater = periodicCoroutineRepeatOnFailure(
                delayTime = 60000,
                scope = viewModelScope,
                dispatcher = Dispatchers.IO
            ){
                loadYfWatchlist(wl)
            }
        }
    }


    fun loadSentinelWatchlist(){
        viewModelScope.launch(Dispatchers.IO){
            val curSettings = prefsManager.getSentinelSettings().first()
            _curSentinelWatchlist.value = curSettings.currentWatchlist
        }
    }


    fun addTickerToSentinelWatchlist(ticker: String, failCallBack: ()->Unit){

        viewModelScope.launch(Dispatchers.IO) {
            val curSettings = prefsManager.getSentinelSettings().first()

            // Add ticker if it's not already on list
            if (!curSettings.currentWatchlist.contains(ticker)){
                curSettings.currentWatchlist.add(ticker)

                prefsManager.updateSentinelSettings(curSettings)

                _curSentinelWatchlist.value = curSettings.currentWatchlist
            }

            // Show toast if ticker is already on watchlist
            else{
                viewModelScope.launch(Dispatchers.Main){
                    failCallBack()
                }
            }
        }

    }

    fun removeTickerFromSentinelWatchlist(ticker: String){
        viewModelScope.launch(Dispatchers.IO) {
            val curSettings = prefsManager.getSentinelSettings().first()
            if (curSettings.currentWatchlist.contains(ticker)){
                curSettings.currentWatchlist.remove(ticker)
                prefsManager.updateSentinelSettings(curSettings)
                _curSentinelWatchlist.value = curSettings.currentWatchlist
            }
        }
    }
}

















