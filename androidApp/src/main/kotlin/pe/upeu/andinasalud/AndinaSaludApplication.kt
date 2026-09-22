package pe.upeu.andinasalud

import android.app.Application
import pe.upeu.andinasalud.di.iniciarKoin

class AndinaSaludApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarKoin()
    }
}

