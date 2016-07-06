package skychat.text

import net.minecraft.util.text.{ITextComponent, TextComponentString, TextFormatting}
import net.minecraftforge.fml.relauncher.ReflectionHelper

import scala.collection.mutable
import scala.collection.convert.decorateAsJava._

object TextParser {

  private def getFormat(c: Char): TextFormatting = {
    val field = ReflectionHelper.findField(classOf[TextFormatting], "formattingCode")
    TextFormatting.values().find(field.get(_).asInstanceOf[Char] == c).orNull
  }

  private def applyStyle(current: TextBuilder, format: TextFormatting): Boolean = format match {
    case TextFormatting.RESET =>
      true
    case TextFormatting.OBFUSCATED =>
      current.style.setObfuscated(true)
      false
    case TextFormatting.BOLD =>
      current.style.setBold(true)
      false
    case TextFormatting.STRIKETHROUGH =>
      current.style.setStrikethrough(true)
      false
    case TextFormatting.UNDERLINE =>
      current.style.setUnderlined(true)
      false
    case TextFormatting.ITALIC =>
      current.style.setItalic(true)
      false
    case _ =>
      if (current.style.getColor == null) current.style.setColor(format)
      true
  }

  def parse(in: String, code: Char = '&'): ITextComponent = {
    var next = in.lastIndexOf(code, in.length - 2)
    if (next == -1)
      return new TextComponentString(in)

    var parts = mutable.Buffer[ITextComponent]()
    var pos = in.length
    var current: TextBuilder = null
    var reset = false
    do {
      val format = getFormat(in.charAt(next + 1))
      if (format != null) {
        if (current != null) {
          if (reset) {
            parts += current.build()
            reset = false
            current = TextBuilder()
          } else {
            current = TextBuilder().append(current.build())
          }
        } else {
          current = TextBuilder()
        }
        reset != applyStyle(current, format)
        pos = next
      }
      next = in.lastIndexOf(code, next - 1)
    } while (next != -1)
    if (current != null)
      parts += current.build()
    val cmp: TextComponentString = new TextComponentString(if (pos > 0) in.substring(0, pos) else "")
    cmp.getSiblings.addAll(parts.reverse.asJava)
    cmp
  }
}
