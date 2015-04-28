package controllers

import management._
import javax.management.MBeanServerConnection

import controllers.Domain.JVMMetrics
import controllers.JMX._
import controllers.JSONImplicits._
import play.api.libs.json._
import play.api.mvc._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Application extends Controller {

  lazy val jmxAddresses = mutable.MutableList("")

  def jvm(host:String) = Action.async {
    jxmConnection(host).
      flatMap(connection => jvmMetrics(connection)).map(metrics => Ok(metrics.toJson).as("application/json"))
  }

  def jvmMetrics(connection:MBeanServerConnection) : Future[JVMMetrics] = {
    Future.sequence(
      List(
        getBean(connection, ManagementFactory.MEMORY_MXBEAN_NAME, classOf[MemoryMXBean]),
        JMX.getBean(connection, ManagementFactory.THREAD_MXBEAN_NAME, classOf[ThreadMXBean]),
        JMX.getBean(connection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, classOf[OperatingSystemMXBean])
      )
    ).map(beans => JVMMetrics(findBean(beans, classOf[MemoryMXBean]), findBean(beans, classOf[ThreadMXBean]), findBean(beans, classOf[OperatingSystemMXBean])))
  }

  def findBean[T](objects:List[PlatformManagedObject], clazz:Class[T]) : Option[T] =
    objects.find(clazz.isInstance(_))
      .map(_.asInstanceOf[T])




  def hostsAsJson(jmxAddresses:mutable.MutableList[String]) : JsArray =
    jmxAddresses.map(address => Json.obj("address" -> address)).
      foldLeft(Json.arr())((array, json) => array ++ Json.arr(json))


  def hosts = Action {
    Ok(hostsAsJson(jmxAddresses)).as("application/json")
  }


  def getAddress(json:JsValue) = json(0).\("address").as[String]


  //TODO Improve approach
  def addHost(any:Option[JsValue]) : mutable.MutableList[String] = {
    jmxAddresses ++= mutable.MutableList(getAddress(any.get))
    jmxAddresses
  }

  def add = Action {
    implicit request => Ok(hostsAsJson(addHost(request.body.asJson))).as("application/json")
  }

}