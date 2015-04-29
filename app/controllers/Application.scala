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


  def jvm(host:String) = Action.async {
    jxmConnection(lookup(host)).
      flatMap(connection => jvmMetrics(connection)).map(metrics => Ok(metrics.toJson).as("application/json"))
  }

  def lookup(host:String) = Hosts.get(host).getOrElse(Host(host, host)).address


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
    Ok(Hosts.get.toJson).as("application/json")
  }


  //we don't have to implicitly call this. maybe I went to far, we will see ;)
  implicit def parseRequest(json:JsValue) = Host(json(0).\("alias").as[String], json(0).\("address").as[String])

  def add = Action {
    implicit request => Ok(Hosts.add(request.body.asJson.get).toJson).as("application/json")
  }

}