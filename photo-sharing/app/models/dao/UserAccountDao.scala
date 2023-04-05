package models.dao

import java.time.{Instant, LocalDateTime}

import javax.inject.{Inject, Singleton}
import models.domain.UserAccount
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import java.security.MessageDigest
import java.util.Base64

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAccountDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(
	implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {
	import profile.api._

	protected class UserAccountDao(tag: Tag) extends Table[UserAccount](tag, "user_account") {
		def userId = column[Int]("user_id", O.PrimaryKey, O.AutoInc)
		def username = column[String]("username", O.Unique, O.Length(20, true))
		def password = column[String]("password", O.Length(20, true))
		def firstName = column[String]("first_name", O.Length(20, true))
		def lastName = column[String]("last_name", O.Length(20, true))

		def * = (userId, username, password, firstName, lastName).mapTo[UserAccount]
	}


val query = TableQuery[UserAccountDao]

def hashPassword(password: String): String = {
  val digest = MessageDigest.getInstance("SHA-384")
  val hash = digest.digest(password.getBytes("UTF-8"))
  Base64.getEncoder.encodeToString(hash)
}

def createUser(user: UserAccount) = {
  val hashedPassword = hashPassword(user.password)
  val userWithHashedPassword = user.copy(password = hashedPassword)
  db.run(query += userWithHashedPassword)
}

def findUser(id: Int) = {
	db.run(query.filter(_.userId === id).result.headOption)
}

def signin(username: String, password: String): Future[Option[UserAccount]] = {
  val hashedPassword = hashPassword(password)
  db.run(query.filter(user => user.username === username && user.password === hashedPassword).result.headOption)
}

}