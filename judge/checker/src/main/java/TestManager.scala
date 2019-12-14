import org.junit.runner.{JUnitCore, Result}

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import scala.io._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.util.Failure


/**
 * Class used to run and manage JUnit tests
 *
 * @param testClass name of the test class
 */
class TestManager(testClass: Class[_]) {

  private var result: Result = null
  private val config: TestConfig = loadConfig()
  private var isMalicious: Boolean = false;
  private val allowedTime: Int = getAllowedTime()
  private var timeLimitExceeded: Boolean = false

  def start(): Unit = {
    /**
     * Using JUnit to run tests
     * Launching a new future object to handle tests in a separate thread
     */
    lazy val f = Future {
      val sm: CustomSecurityManager = new CustomSecurityManager
      System.setSecurityManager(sm)
      this.result = JUnitCore.runClasses(testClass): Result
      this.isMalicious = sm.securityAlertThrown()
    }
    try {
      Await.result(f, new DurationInt(allowedTime).milliseconds)
    } catch {
      case e: TimeoutException => handleTimeLimit()
    }
    f recover {
      case e: Exception => System.err.println("Caught an exception")
    }

  }

  def handleTimeLimit(): Unit = {
    System.err.print("Tests timed out")
    this.timeLimitExceeded = true
  }

  def getResult(): Result = result

  def getAllowedTime(): Int = {
    config.getTime()
  }

  def loadConfig(): TestConfig = {
    val source = readJson()
    TestConfig(source("time_limit").toInt)
  }

  /**
   * Reading a json config object
   *
   * @return (key, value) pairs
   */
  def readJson(): Map[String, String] = {
    val json = Source.fromFile(Settings.JSON_CONFIG_NAME)
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    val parsedJson = mapper.readValue[Map[String, String]](json.reader())
    parsedJson
  }

  def timedOut(): Boolean = this.timeLimitExceeded

  def isSecure: Boolean = {
    !this.isMalicious
  }

  /**
   * Config that sets the total amount of time allowed for the test manager to run all tests
   *
   * @param time_limit
   */
  case class TestConfig(time_limit: Int) {

    def getTime(): Int = time_limit
  }

}
