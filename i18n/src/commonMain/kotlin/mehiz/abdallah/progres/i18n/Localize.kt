package mehiz.abdallah.progres.i18n

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

expect class Localize {
  fun getString(res: StringResource, vararg args: Any): String

  fun getPlural(res: PluralsResource, amount: Int, vararg args: Any): String
}
