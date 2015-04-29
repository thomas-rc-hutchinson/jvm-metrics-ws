package controllers

import management.MemoryMXBean
import management.OperatingSystemMXBean
import management.ThreadMXBean

import controllers.Domain.JVMMetrics
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

/**
 * Created by thomash on 4/24/15.
 */
object JSONImplicits {

  implicit class JVMMetricsJson(metrics:JVMMetrics){
    def toJson = {

      val json = Json.obj(
        List(
            metrics.osMXBean.map(osToJson),
            metrics.memoryBean.map(heapToJson),
            metrics.threadMXBean.map(threadToJson))
          .filter(_.isDefined).map(_.get) ++ List[(String, JsValueWrapper)]("timestamp" -> System.nanoTime()): _*
      )
      json
  }

  def osToJson(os: OperatingSystemMXBean) : (String, JsValueWrapper) =
    "os" -> Json.obj("name" -> os.getName, "arch" -> os.getArch, "processors" -> os.getAvailableProcessors, "version" -> os.getVersion)

  def heapToJson(memory: MemoryMXBean) : (String, JsValueWrapper) =
    "heap" -> (Json.obj("init" -> memory.getHeapMemoryUsage.getInit,
        "used" -> memory.getHeapMemoryUsage.getUsed, "commited" -> memory.getHeapMemoryUsage.getCommitted,
        "max" -> memory.getHeapMemoryUsage.getMax))

  def threadToJson(threads: ThreadMXBean) : (String, JsValueWrapper) =
    "thread" -> Json.obj("count" -> threads.getThreadCount)
}

  implicit class HostsJson(hosts : List[Host]){
     def toJson : JsArray =
       hosts.map(address => address.toJson).
         foldLeft(Json.arr())((array, json) => array ++ Json.arr(json))
  }

  implicit class HostJson(host : Host){
    def toJson = Json.obj("alias" -> host.alias, "address" -> host.address)
  }


}









