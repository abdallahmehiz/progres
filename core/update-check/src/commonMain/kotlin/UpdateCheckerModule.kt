import mehiz.abdallah.progres.update_check.BuildKonfig.BuildKonfig
import org.koin.dsl.module

val UpdateCheckerModule = module {
  single {
    AppUpdateCheck(
      get(),
      BuildKonfig.tag,
      "abdallahmehiz",
      "progres"
    )
  }
}
