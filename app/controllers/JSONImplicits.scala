package controllers

import management.MemoryMXBean
import management.OperatingSystemMXBean
import management.ThreadMXBean

import controllers.Domain.JVMMetrics
import play.api.libs.json.Json

/**
 * Created by thomash on 4/24/15.
 */
object JSONImplicits {

  implicit class JVMMetricsJson(metrics:JVMMetrics){

    def toJson = {

      val data = List(metrics.osMXBean.map(osToJson),
                      metrics.memoryBean.map(heapToJson),
                      metrics.threadMXBean.map(threadToJson))
                .filter(_.isDefined).map(_.get) ++ List(time)

      Json.toJson(
        Map(data :_*)
      )

    }

    def time = "timestamp" -> Json.toJson(System.nanoTime())

    def osToJson(os: OperatingSystemMXBean) =
      "os" -> Json.obj("name" -> os.getName, "arch" -> os.getArch, "processors" -> os.getAvailableProcessors, "version" -> os.getVersion)

    def heapToJson(memory: MemoryMXBean)  =
      "heap" -> (Json.obj("init" -> memory.getHeapMemoryUsage.getInit,
        "used" -> memory.getHeapMemoryUsage.getUsed, "commited" -> memory.getHeapMemoryUsage.getCommitted,
        "max" -> memory.getHeapMemoryUsage.getMax))

    def threadToJson(threads: ThreadMXBean)  =
      "thread" -> Json.obj("count" -> threads.getThreadCount)

}

  implicit class HostsJson(hosts : List[Host]){
     def toJson  = {
       val hostsJson = hosts.map(address => address.toJson).
         foldLeft(Json.arr())((array, json) => array ++ Json.arr(json))

       Json.obj("hosts" -> hostsJson)
     }

  }

  implicit class HostJson(host : Host){
    def toJson = Json.obj("alias" -> host.alias, "address" -> host.address)
  }


}









