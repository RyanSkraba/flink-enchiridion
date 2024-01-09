package com.skraba.flink.enchiridion.core

import com.skraba.flink.enchiridion.core.FlinkJobGo.InternalDocoptException
import com.skraba.flink.enchiridion.core.FlinkJobGoSpec.withFlinkJobGo
import org.docopt.DocoptExitException
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import scala.reflect.io.Streamable

/** Unit tests for the CLI in the [[FlinkJobGo]] project.
  */
class FlinkJobGoSpec
    extends AnyFunSpecLike
    with Matchers
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  describe("FlinkJobGo docopt check") {
    it("should have less than 80 characters per string for readability") {
      for (line <- FlinkJobGo.Doc.split("\n")) {
        withClue("main" -> line) {
          line.length should be < 80
        }
      }
      for (
        task <- FlinkJobGo.Tasks;
        line <- task.doc.split("\n")
      ) {
        withClue(task.cmd -> line) {
          line.length should be < 80
        }
      }
    }
  }

  describe("FlinkJobGo invalid command lines") {
    it("throw an exception with --version") {
      val t = intercept[DocoptExitException] {
        withFlinkJobGo("--version")
      }
      t.getExitCode shouldBe 0
      t.getMessage shouldBe FlinkJobGo.Version
    }

    it("throw an exception with --help") {
      val t = intercept[DocoptExitException] {
        withFlinkJobGo("--help")
      }
      t.getExitCode shouldBe 0
      t.getMessage shouldBe FlinkJobGo.Doc
    }

    it(s"throws an exception with unknown options") {
      for (
        args <- Seq(
          Seq("--garbage"),
          Seq("--debug", "--garbage"),
          Seq("--garbage", "--debug"),
          Seq("--garbage", "garbage")
        )
      ) withClue(s"Using: $args") {
        val t = intercept[DocoptExitException] {
          withFlinkJobGo(args: _*)
        }
        t.getExitCode shouldBe 1
        t.getMessage shouldBe null
      }
    }

    it("throws an exception with an unknown command") {
      val t = intercept[InternalDocoptException] {
        withFlinkJobGo("garbage")
      }
      t.getMessage shouldBe "Unknown command: garbage"
      t.docopt shouldBe FlinkJobGo.Doc
    }
  }

  for (task <- FlinkJobGo.Tasks) {
    describe(s"FlinkJobGo ${task.cmd} docopt check") {
      it("should have less than 80 characters per string for readability") {
        for (line <- task.doc.split("\n")) {
          withClue(task.cmd -> line) {
            line.length should be < 80
          }
        }
      }
    }

    describe(s"MarkdGo ${task.cmd} invalid command lines") {
      it("throws an exception with --version") {
        val t = intercept[DocoptExitException] {
          withFlinkJobGo(task.cmd, "--version")
        }
        t.getExitCode shouldBe 0
        t.getMessage shouldBe FlinkJobGo.Version
      }

      it("throws an exception with --help") {
        val t = intercept[DocoptExitException] {
          withFlinkJobGo(task.cmd, "--help")
        }
        t.getExitCode shouldBe 0
        t.getMessage shouldBe task.doc
      }

      it(s"throws an exception with unknown options") {
        for (
          args <- Seq(
            Seq(task.cmd, "--garbage"),
            Seq(task.cmd, "--debug", "--garbage"),
            Seq(task.cmd, "--garbage", "--debug"),
            Seq(task.cmd, "--garbage", "garbage")
          )
        ) withClue(s"Using: $args") {
          val t = intercept[InternalDocoptException] {
            withFlinkJobGo(args: _*)
          }
          t.docopt shouldBe task.doc
          t.getMessage shouldBe null
        }
      }
    }
  }

}

object FlinkJobGoSpec {

  /** A helper method used to capture the console and apply it to a partial
    * function.
    * @param thunk
    *   code to execute that may use Console.out and Console.err print streams
    * @param pf
    *   A partial function to apply matchers
    * @tparam T
    *   The return value type of the thunk code to execute
    * @tparam U
    *   The return value type of the partial function to return.
    * @return
    *   The return value of the partial function.
    */
  def withConsoleMatch[T, U](
      thunk: => T
  )(pf: scala.PartialFunction[(T, String, String), U]): U = {
    Streamable.closing(new ByteArrayOutputStream()) { out =>
      Streamable.closing(new ByteArrayOutputStream()) { err =>
        Console.withOut(out) {
          Console.withErr(err) {
            val t = thunk
            Console.out.flush()
            Console.err.flush()
            // The return value
            pf(
              t,
              new String(out.toByteArray, StandardCharsets.UTF_8),
              new String(err.toByteArray, StandardCharsets.UTF_8)
            )
          }
        }
      }
    }
  }

  /** A helper method used to capture the console of a ScalaGo execution and
    * apply it to a partial function.
    * @param args
    *   String arguments to pass to the ScalaGo.go method
    * @param pf
    *   A partial function to apply matchers
    * @tparam T
    *   The return value type of the thunk code to execute
    * @tparam U
    *   The return value type of the partial function to return.
    * @return
    *   The return value of the partial function.
    */
  def withFlinkJobGoMatch[T, U](
      args: String*
  )(pf: scala.PartialFunction[(String, String), U]): U = {
    withConsoleMatch(FlinkJobGo.go(args: _*)) { case (_, stdout, stderr) =>
      pf(stdout, stderr)
    }
  }

  /** A helper method used to capture the console of a ScalaGo execution and
    * return the output.
    * @param args
    *   String arguments to pass to the ScalaGo.go method
    * @return
    *   A tuple of the stdout and stderr
    */
  def withFlinkJobGo(args: String*): (String, String) = {
    withFlinkJobGoMatch(args: _*) { case any => any }
  }
}
