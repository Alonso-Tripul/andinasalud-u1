package pe.upeu.andinasalud

import androidx.compose.ui.window.ComposeUIViewController
import pe.upeu.andinasalud.di.iniciarKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    iniciarKoin()
    return ComposeUIViewController { App() }
}

