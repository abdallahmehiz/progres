package mehiz.abdallah.progres.i18n

import android.content.Context
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

actual class Localize(private val context: Context) {
  actual fun getString(res: StringResource, vararg args: Any): String {
    return context.getString(res.resourceId, *args)
  }

  actual fun getPlural(res: PluralsResource, amount: Int, vararg args: Any): String {
    return context.resources.getQuantityString(res.resourceId, amount, *args)
  }
}
