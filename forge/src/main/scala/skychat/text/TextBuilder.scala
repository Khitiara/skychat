package skychat.text

import net.minecraft.util.text.{ITextComponent, Style, TextComponentString}

import scala.collection.mutable
import scala.collection.convert.decorateAsJava._

class TextBuilder {
  private var siblings = mutable.Buffer[ITextComponent]()

  def append(component: ITextComponent): TextBuilder = {
    siblings += component
    this
  }

  val style = new Style
  val text = StringBuilder.newBuilder

  def build(): ITextComponent = {
    val component = new TextComponentString(text.mkString)
    component.getSiblings.addAll(siblings.asJava)
    component
  }

}

object TextBuilder {
  def apply(): TextBuilder = new TextBuilder()
}