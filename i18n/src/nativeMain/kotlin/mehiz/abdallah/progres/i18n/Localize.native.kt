package mehiz.abdallah.progres.i18n

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

actual class Localize {
  actual fun getString(res: StringResource, vararg args: Any): String {
    return StringDesc.ResourceFormatted(res, args).localized()
  }

  actual fun getPlural(
    res: PluralsResource,
    amount: Int,
    vararg args: Any,
  ): String {
    return StringDesc.PluralFormatted(res, amount, args).localized()
  }
}
