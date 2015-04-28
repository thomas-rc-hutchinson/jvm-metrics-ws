package controllers

import controllers.Domain.JVMMetrics
import play.api.libs.json.Json

/**
 * Created by thomash on 4/24/15.
 */
object JSONImplicits {

  implicit class JVMMetricsJson(metrics:JVMMetrics){
    def toJson = {

      //TODO Use a more flexible way to build the xml taking advantage of None
      val heapMemory = metrics.memoryBean.get.getHeapMemoryUsage
      val threads = metrics.threadMXBean.get
      val os = metrics.osMXBean.get

      val json = Json.obj(
        "os" -> Json.obj("name" -> os.getName, "arch" -> os.getArch, "processors" -> os.getAvailableProcessors, "version" -> os.getVersion),
        "heap" -> (Json.obj("init" -> heapMemory.getInit, "used" -> heapMemory.getUsed, "commited" -> heapMemory.getCommitted, "max" -> heapMemory.getMax)),
        "thread" -> Json.obj("thread" -> Json.obj("count" -> threads.getThreadCount)),
        "timestamp" -> System.nanoTime()
      )
      json
  }
  }
}









