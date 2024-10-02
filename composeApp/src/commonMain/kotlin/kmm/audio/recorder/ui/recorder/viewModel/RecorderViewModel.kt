package kmm.audio.recorder.ui.recorder.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.theolm.record.Record
import dev.theolm.record.config.OutputFormat
import dev.theolm.record.config.OutputLocation
import dev.theolm.record.config.RecordConfig
import kmm.audio.recorder.AudioPlayer
import kmm.audio.recorder.dataClass.AudioAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.DIRECTORY_SEPARATOR
import okio.Path.Companion.toPath
import okio.SYSTEM

class RecorderViewModel(val permissionsController: PermissionsController): ViewModel() {

    private val recorder = Record
    private val audioPlayer = AudioPlayer()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _audioAvailable = MutableStateFlow(AudioAvailable())
    val audioAvailable : StateFlow<AudioAvailable> = _audioAvailable.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun updateIsRecording(newValue: Boolean){
        _isRecording.value = newValue
    }

    fun updateAudioAvailable(newValue: AudioAvailable){
        _audioAvailable.value = newValue
    }

    fun updateIsPlaying(newValue: Boolean){
        _isPlaying.value = newValue
    }

    fun createCacheDirectory(){
        val root = (FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toString() + DIRECTORY_SEPARATOR + "hola").toPath()

        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(root)){
            fileSystem.createDirectory(root)

            println("oooops ... si")
        }else{
            println("oooops ... no")
        }
    }

    fun configureRecorder(outputLocation: OutputLocation = OutputLocation.Cache, outputFormat: OutputFormat = OutputFormat.MPEG_4){
        recorder.setConfig(
            RecordConfig(
                outputLocation = outputLocation,
                outputFormat = outputFormat
            )
        )
    }

    fun onClickRecorderButton(){
        if (isRecording.value){
            stopRecording()
        }else{
            startRecording()
        }
    }

    private fun startRecording(){
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.RECORD_AUDIO)
                // Permission has been granted successfully.
                // Start Recording
                recorder.startRecording()
                //Update recording state
                updateIsRecording(true)

            } catch(deniedAlways: DeniedAlwaysException) {
                // Permission is always denied.
                println("Permission.RECORD_AUDIO is always denied.")
            } catch(denied: DeniedException) {
                // Permission was denied.
                println("Permission.RECORD_AUDIO was denied.")
            }
        }
    }

    private fun stopRecording(){
        if (recorder.isRecording()){
            recorder.stopRecording().also { savedAudioPath ->
                //Update recording state
                updateIsRecording(newValue = false)

                //Update audio available state
                updateAudioAvailable(AudioAvailable(true, savedAudioPath))
            }
        }
    }

    fun onClickPlayerButton(){
        if (isPlaying.value){
            pausePlaying()
        }else{
            startPlaying(audioAvailable.value.file!!)
        }
    }

    private fun startPlaying(file: String){
        audioPlayer.play(file = file)

        //Update playing state
        updateIsPlaying(true)

        CoroutineScope(Dispatchers.Main).launch {
            audioPlayer.onPlaybackFinished().collect{
                //Playback has ended
                //Update playing state
                updateIsPlaying(false)
            }
        }
    }

    private fun pausePlaying(){
        audioPlayer.pause()

        //Update playing state
        updateIsPlaying(false)
    }
}