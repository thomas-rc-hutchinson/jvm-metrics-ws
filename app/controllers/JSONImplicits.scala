package controllers

import controllers.Domain.JVMMetrics
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

/**
 * Created by thomash on 4/24/15.
 */
object JSONImplicits {

  implicit class JVMMetricsJson(metrics:JVMMetrics){
    def toJson = {

      //TODO Use a more flexible way to build the xml taking advantage of None
      val osJson  = metrics.osMXBean.
        map[(String, JsValueWrapper)](os => "os" ->
        Json.obj("name" -> os.getName, "arch" -> os.getArch, "processors" -> os.getAvailableProcessors, "version" -> os.getVersion))


      val heapJson = metrics.memoryBean.
        map[(String, JsValueWrapper)](heapMemory => "heap" ->
        (Json.obj("init" -> heapMemory.getHeapMemoryUsage.getInit,
          "used" -> heapMemory.getHeapMemoryUsage.getUsed, "commited" -> heapMemory.getHeapMemoryUsage.getCommitted,
          "max" -> heapMemory.getHeapMemoryUsage.getMax)))

      val threadsJson = metrics.threadMXBean.
        map[(String, JsValueWrapper)](threads => "thread" -> Json.obj("thread" -> Json.obj("count" -> threads.getThreadCount)))


      //filter out beans that could not be retrieved for whatever reason
      val jsonObjs = List(osJson, heapJson, threadsJson).filter(_.isDefined).map(_.get)


      val json = Json.obj(
        jsonObjs : _*
      )
      json
  }


}









