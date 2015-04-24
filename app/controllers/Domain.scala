package controllers

import java.lang.management.MemoryMXBean
import management.OperatingSystemMXBean
import management.ThreadMXBean

/**
 * Created by thomash on 4/24/15.
 */
object Domain {
  case class JVMMetrics(memoryBean : Option[MemoryMXBean], threadMXBean: Option[ThreadMXBean], osMXBean : Option[OperatingSystemMXBean])
}
