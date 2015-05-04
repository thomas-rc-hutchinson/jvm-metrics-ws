package controllers

import management._
import javax.management.MBeanServerConnection

import controllers.Domain.JVMMetrics
import controllers.JMX._
import controllers.JSONImplicits._
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Iteratee
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Application extends Controller {

  def jvm(host:String) = Action.async {
    jxmConnection(lookup(host)).
      flatMap(connection => jvmMetrics(connection)).map(metrics => Ok(metrics.toJson).as("application/json"))
  }

  def lookup(host:String) = Hosts.get(host).map(_.address).getOrElse(host)


  def jvmMetrics(connection:MBeanServerConnection) : Future[JVMMetrics] = {
    Future.sequence(
      List(
        getBean(connection, ManagementFactory.MEMORY_MXBEAN_NAME, classOf[MemoryMXBean]),
        getBean(connection, ManagementFactory.THREAD_MXBEAN_NAME, classOf[ThreadMXBean]),
        getBean(connection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, classOf[OperatingSystemMXBean])
      )
    ).map(beans => JVMMetrics(findBean(beans, classOf[MemoryMXBean]), findBean(beans, classOf[ThreadMXBean]), findBean(beans, classOf[OperatingSystemMXBean])))
  }

  def findBean[T](objects:List[PlatformManagedObject], clazz:Class[T]) : Option[T] =
    objects.find(clazz.isInstance(_))
      .map(_.asInstanceOf[T])


  def hosts = Action {
    Ok(Hosts.get.toJson).as("application/json")
  }


  implicit def parseRequest(json:JsValue) = Host(json(0).\("alias").as[String], json(0).\("address").as[String])

  def add = Action {
    implicit request => Ok(Hosts.add(request.body.asJson.get).toJson).as("application/json")
  }


  //web sockets => pushes JVM metrics to client
  def websocketsjvm(host:String) = WebSocket.using[JsValue] { request =>

    //gets jmx connection and forever pushes the state of the JVM to the client
    val out = Enumerator.flatten(
      jxmConnection(lookup(host)).
        map(connection => Enumerator.repeatM(jvmMetrics(connection).map(metrics => metrics.toJson))))

    //we don't care about input
    val in = Iteratee.ignore[JsValue]

    (in, out)
  }




}