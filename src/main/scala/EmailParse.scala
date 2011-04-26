import scala.io.Source
import java.io._

object EmailParse {
  private val pidFile = new PrintWriter(new File("pids.txt"))
  private val epsFile = new PrintWriter(new File("missingEps.txt"))
  // Some visual counters to get an idea
  // of the amount of of different Exceptions.
  private var osError = 0
  private var keyError = 0
  private var entityError = 0
  private var progError = 0
  private var pdfLibError = 0

  val MyMatchPid   = """^(\d*)""".r
  val MyMatchError = """^OSError: Could not find legacy eps file for card front\[Errno 2\] No such file or directory: \'(.*)\'""".r
  val MyMatchKey = """^KeyError:(.*)""".r  
  val MyMatchExcept = """^Exception: Unhandled entityref:(.*)""".r
  val MyMatchProg = """^ProgrammingError: (.*)""".r
  val MyMatchPdfLib = """^PDFlibException: (.*)""".r


  def main(args: Array[String]): Unit = {
    val emailFile = Source.fromFile("email.txt").getLines
    emailFile.foreach{ processLine }

    println("OSErrors: "  + osError.toString)  
    println("KeyErrors: " + keyError.toString)
    println("Entity: " + entityError.toString)
    println("ProgError: " + progError.toString)
    println("PDFLIB: " + pdfLibError.toString)

    pidFile.close()
    epsFile.close()
  }

  def processLine(txt: String): Unit = {
    txt match {
      case MyMatchPid(pid) if (pid != "")  => 
        pidFile write pid + "\n"
      case MyMatchError(msg) if(msg != "") =>
        osError = osError + 1;
        epsFile write msg + "\n"
      case MyMatchKey(keymsg) if(keymsg != "") =>
        keyError = keyError + 1
      case MyMatchExcept(exceptmsg) if(exceptmsg != "") =>
        entityError = entityError + 1
      case MyMatchProg(progmsg) if(progmsg != "") =>
        progError = progError + 1
      // Ugly Hack due to Scala Bug# 1133
      case _ => txt match {
        case MyMatchPdfLib(pdfLibMsg) if(pdfLibMsg != "") =>
          pdfLibError = pdfLibError + 1
        case _ => print("")
      }
    }
  }
}
