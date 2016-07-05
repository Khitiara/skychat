package skychat

import org.spongepowered.api.text.TextTemplate

object util {

  sealed trait TemplatePart {
    def process(): Any
  }

  case class Arg(name: String, f: TextTemplate.Arg.Builder => TextTemplate.Arg.Builder = identity) extends TemplatePart {
    override def process(): Any = f(TextTemplate.arg(name)).build()
  }

  case class Raw(what: Any) extends TemplatePart {
    override def process(): Any = what
  }

  def textTempalte(args: TemplatePart*) =
    textTemplate(args, TextTemplate.DEFAULT_OPEN_ARG, TextTemplate.DEFAULT_CLOSE_ARG)

  def textTemplate(args: Seq[TemplatePart], open: String, close: String): TextTemplate =
    TextTemplate.of(open, close, args.filter(_ != null).map(_.process()))
}
