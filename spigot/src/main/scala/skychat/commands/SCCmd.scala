package skychat.commands

import org.bukkit.command.{Command, CommandExecutor, CommandSender}

import scala.collection.immutable.TreeMap

abstract class SCCmd(name: String) {
  def execute(sender: CommandSender, args: Seq[String])
}

class SCExecutor(private val cmd: Map[String, SCCmd]) extends CommandExecutor {
  private val commands = TreeMap[String, SCCmd]()(Ordering.by(_.toLowerCase)) ++ cmd

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean =
    commands.get(command.getName).exists(c => {
      c.execute(sender, args)
      true
    })
}