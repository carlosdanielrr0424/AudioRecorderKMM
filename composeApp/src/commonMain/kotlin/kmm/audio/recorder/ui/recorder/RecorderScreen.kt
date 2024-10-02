package kmm.audio.recorder.ui.recorder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.theolm.record.config.OutputLocation
import kmm.audio.recorder.dataClass.AudioAvailable
import kmm.audio.recorder.ui.recorder.viewModel.RecorderViewModel

class RecorderScreen: Screen {
    @Composable
    override fun Content() {
        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val viewModel: RecorderViewModel = viewModel { RecorderViewModel(factory.createPermissionsController()) }

        BindEffect(viewModel.permissionsController)

        ViewContainer(viewModel)
    }
}

@Composable
fun ViewContainer(viewModel: RecorderViewModel){
    //States
    val isRecording: Boolean by viewModel.isRecording.collectAsState()
    val audioAvailable: AudioAvailable by viewModel.audioAvailable.collectAsState()
    val isPlaying: Boolean by viewModel.isPlaying.collectAsState()

    //Configure recorder
    viewModel.configureRecorder(OutputLocation.Cache)

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = viewModel::onClickRecorderButton, modifier = Modifier.align(Alignment.CenterHorizontally)){
            if (isRecording){
                Text(text = "Detener")
            }else{
                Text(text = "Grabar")
            }
        }

        Button(onClick = viewModel::onClickPlayerButton, modifier = Modifier.align(Alignment.CenterHorizontally), enabled = audioAvailable.isAvailable){
            if (isPlaying){
                Text(text = "Pausar")
            }else{
                Text(text = "Reproducir")
            }
        }

    }
}