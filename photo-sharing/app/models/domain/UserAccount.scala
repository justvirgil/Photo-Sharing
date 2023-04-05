package models.domain


case class UserAccount(
	userId: Int,
	username: String,
	password: String,
	firstName: String,
	lastName: String,
)

