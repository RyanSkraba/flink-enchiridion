package com.skraba.flink.enchiridion.core

import scala.sys.SystemProperties

/** Command-line driver for a basic task */
object LocalInfoTask {

  val Cmd = "info"

  val Description = "Print information about the local configuration"

  val Doc: String =
    s"""$Description
      |
      |Usage:
      |  FlinkJobGo $Cmd
      |
      |Options:
      |  -h --help            Show this screen.
      |  --version            Show version.
      |
      |""".stripMargin.trim

  def go(opts: java.util.Map[String, AnyRef]): Unit = {
    System.out.println("System properties")
    System.out.println("=================")
    new SystemProperties().toSeq.sortBy(_._1).foreach { case (key, value) =>
      System.out.println(s"$key\t$value")
    }
  }

  val Task: FlinkJobGo.Task = FlinkJobGo.Task(Doc, Cmd, Description, go)
}
