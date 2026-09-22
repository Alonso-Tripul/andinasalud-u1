package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.data.local.RelojSistema
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.repository.Reloj
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerDetalleCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.domain.usecase.PuedeSolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val appModule = module {
    single { CitasSimuladas() }
    single<Reloj> { RelojSistema() }
    single<CitaRepository> { CitaRepositoryFake(get()) }
    factory { ObtenerCitasUseCase(get()) }
    factory { ObtenerDetalleCitaUseCase(get()) }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { ObtenerPacienteUseCase(get()) }
    factory { CancelarCitaUseCase(get(), get()) }
    single { SolicitarCitaUseCase(get(), get()) }
    factory { PuedeSolicitarCitaUseCase(get()) }
    factory { CitasViewModel(get()) }
    factory { DetalleCitaViewModel(get(), get()) }
    factory { SolicitudViewModel(get(), get()) }
    factory { PerfilViewModel(get()) }
}

fun iniciarKoin() {
    if (GlobalContext.getOrNull() == null) startKoin { modules(appModule) }
}
