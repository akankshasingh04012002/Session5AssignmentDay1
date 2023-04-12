
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

class GradeCalculator {

  def parseCsv(filePath: String): Future[List[Map[String, String]]] = {
    Future {
      Try {
        val source = Source.fromFile(filePath)
        val lines = source.getLines().toList
        val header = lines.head
        val data = lines.tail
        val keys = header.split(",").map(_.trim)
        data.map(line => {
          val values = line.split(",").map(_.trim)
          keys.zip(values).toMap
        })
      } match {
        // If parsing is successful, return the parsed data wrapped in a Success
        case Success(parsedData) => parsedData
        case Failure(exception) => throw new Exception(s"Failed to parse CSV file: ${exception.getMessage}")
      }
    }
  }

  def calculateStudentAverages(parsedData: Future[List[Map[String, String]]]): Future[List[(String, Double)]] = {
    parsedData.map { data =>
      data.map { studentData =>
        val studentId = studentData.getOrElse("StudentID", "")
        val grades = List(
          studentData.getOrElse("English", "0").toDouble,
          studentData.getOrElse("Physics", "0").toDouble,
          studentData.getOrElse("Chemistry", "0").toDouble,
          studentData.getOrElse("Maths", "0").toDouble
        )
        val average = grades.sum / grades.length
        (studentId, average)
      }
    }
  }

  def calculateClassAverage(studentAverages: Future[List[(String, Double)]]): Future[Double] = {
    studentAverages.map(averages => {
      val total = averages.map(_._2).reduce(_ + _)
      total / averages.length
    })
  }

  def calculateGrades(filePath: String): Future[Double] = {
    val parsedData = parseCsv(filePath)
    val studentAverages = calculateStudentAverages(parsedData)
    // Use recoverWith to handle exceptions and return a failed Future
    calculateClassAverage(studentAverages).recoverWith {
      case exception => Future.failed(new Exception(s"Failed to calculate class average: ${exception.getMessage}"))
    }
  }
}
