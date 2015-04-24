package controllers

import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.remote.{JMXConnectorFactory, JMXServiceURL}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by thomash on 4/24/15.
 */
object JMX {

  def jxmConnection(address:String) : Future[MBeanServerConnection] = Future {
    JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%s/jmxrmi".format(address)), null).getMBeanServerConnection
  }

  def getBean[T](connection: MBeanServerConnection, beanType:String, clazz:Class[T]) : Future[T] = Future {
    ManagementFactory.newPlatformMXBeanProxy(connection, beanType, clazz)
  }

}
