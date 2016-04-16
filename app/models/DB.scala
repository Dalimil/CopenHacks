package models

// Declare a model
case class Game(name: String, config: String)

// Initialize SORM, automatically generating schema
import sorm._
object Db extends Instance(
  entities = Set(Entity[Game]()),
  url = "jdbc:h2:mem:test"
)

