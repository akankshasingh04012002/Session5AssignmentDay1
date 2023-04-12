
import scala.concurrent.Await
import scala.concurrent.duration._

object Driver extends App{

  private val gradeCalculator = new GradeCalculator
  // Calculating class average
  private val csvFile = "/home/knoldus/IdeaProjects/StudentGrades/src/main/scala/grade.csv"
  private val classAvgFuture = gradeCalculator.calculateGrades(csvFile)
  private val classAvg = Await.result(classAvgFuture, 10.seconds)
  println(s"Class average: $classAvg")

  // Print student averages
  private val studentAveragesFuture = gradeCalculator.calculateStudentAverages(gradeCalculator.parseCsv(csvFile))
  val studentAverages = Await.result(studentAveragesFuture, 10.seconds)
  studentAverages.foreach { case (id, avg) =>
    println(s"Student $id average grade: $avg")
  }
}
