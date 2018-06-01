import java.sql._

package object DatabaseInterface {
  // connect to the database named "mysql" on the localhost
  val driver = System.getenv("DATABASE_DRIVER")
  val url = System.getenv("DATABASE_URL")
  val username = "postgres"
  val password = ""

  var connection: Connection = _

  def query(query: String): Option[ResultSet] = {
    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url) // username, password

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)

      return Some(resultSet)
    }
    catch {
      case e: Throwable => e.printStackTrace()
    }
    connection.close()

    None // if nothing is returned, return None
  }
}