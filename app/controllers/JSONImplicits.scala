package controllers

import controllers.Domain.JVMMetrics
import play.api.libs.json.Json

/**
 * Created by thomash on 4/24/15.
 */
object JSONImplicits {

  implicit class JVMMetricsJson(metrics:JVMMetrics){
    def toJson = {

      //TODO Handle None
      val heapMemory = metrics.memoryBean.get.getHeapMemoryUsage
      val heap = Json.obj("heap" -> (Json.obj("init" -> heapMemory.getInit, "used" -> heapMemory.getUsed, "commited" -> heapMemory.getCommitted, "max" -> heapMemory.getMax)))

      val threads = metrics.threadMXBean

      val thread = Json.obj("thread" -> Json.obj("count" -> threads.get.getThreadCount))

      heap


    }
  }


  /**
   *   private final long init;
    private final long used;
    private final long committed;
    private final long max;
   */

  //def json(host:String, memoryMXBean: MemoryMXBean) = Json.obj("host" -> host, "used" -> memoryMXBean.getHeapMemoryUsage.getUsed)



}
