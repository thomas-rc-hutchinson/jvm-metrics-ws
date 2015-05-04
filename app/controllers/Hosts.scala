package controllers

/**
 * Created by thomash on 4/29/15.
 */
//TODO Remove singleton pattern
object Hosts {

  lazy val jmxAddresses : scala.collection.mutable.MutableList[Host] = scala.collection.mutable.MutableList()

  def add(host: Host) : Host = {
    jmxAddresses ++= scala.collection.mutable.MutableList(host)
    host
  }

  def get : List[Host] = jmxAddresses.toList

  def get(alias:String) : Option[Host] = jmxAddresses.find(_.alias.equals(alias))

}

case class Host(alias:String, address:String)
