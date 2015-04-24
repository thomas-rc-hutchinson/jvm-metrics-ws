package controllers

import java.lang.management.{ManagementFactory, MemoryMXBean}
import javax.management.MBeanServerConnection
import javax.management.remote.{JMXConnectorFactory, JMXServiceURL}

import play.api.mvc._
import play.api.libs.json._

import scala.collection.mutable

object Application extends Controller {

  lazy val jmxAddresses = mutable.MutableList("10.1.28.36:7214")


  def jxmConnection(address:String) : MBeanServerConnection = {
    JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%s/jmxrmi".format(address)), null).getMBeanServerConnection
  }

  def getMemory(connection: MBeanServerConnection) : MemoryMXBean = {
    ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.MEMORY_MXBEAN_NAME,
      classOf[MemoryMXBean])
  }

  def json(host:String, memoryMXBean: MemoryMXBean) = Json.obj("host" -> host, "used" -> memoryMXBean.getHeapMemoryUsage.getUsed)


  def jvm(host:String) = Action {
    val j = json(host, getMemory(jxmConnection(host)))
    Ok(j).as("application/json")
  }

  def hostsAsJson(jmxAddresses:mutable.MutableList[String]) : JsArray =
    jmxAddresses.map(address => Json.obj("address" -> address)).foldLeft(Json.arr())((array, json) => array ++ Json.arr(json))



  def hosts = Action {
    Ok(hostsAsJson(jmxAddresses)).as("application/json")
  }


  def getAddress(json:JsValue) = json(0).\("address").as[String]

  def addHost(any:Option[JsValue]) : mutable.MutableList[String] = {
    jmxAddresses ++= mutable.MutableList(getAddress(any.get))
    jmxAddresses
  }

  def add = Action {
    implicit request => Ok(hostsAsJson(addHost(request.body.asJson))).as("application/json")
  }

}