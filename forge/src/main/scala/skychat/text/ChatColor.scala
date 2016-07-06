package skychat.text

object ChatColor {
  final var COLOR_CHAR: Char = '\u00A7'
  final var ALL_CODES: String = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr"

  def translateAlternateColorCodes(altColorChar: Char, textToTranslate: String): String = {
    val b: Array[Char] = textToTranslate.toCharArray
    for (i <- 0 until b.length - 1
         if b(i) == altColorChar && ALL_CODES.indexOf(b(i + 1)) > -1) {
      b(i) = ChatColor.COLOR_CHAR
      b(i + 1) = Character.toLowerCase(b(i + 1))
    }
    new String(b)
  }
}
