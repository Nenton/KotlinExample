package ru.skillbranch.kotlinexample

import ru.skillbranch.kotlinexample.extensions.dropLastUntil

/**
 * @author Susev Sergey
 */
object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (!map.containsKey(user.login)) {
                    map[user.login] = user
                } else {
                    throw IllegalArgumentException("A user with this email already exists")
                }
            }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        return User.makeUser(fullName, phone = rawPhone)
            .also { user ->
                when{
                    !checkValidPhone(user.login) -> throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                    !map.containsKey(user.login) -> map[user.login] = user
                    else -> throw IllegalArgumentException("A user with this phone already exists")
                }
            }
    }

    private fun checkValidPhone(phone: String): Boolean {
        return "[+]\\d{12}|\\d{11}".toRegex().containsMatchIn(phone)
    }

    fun loginUser(login: String, password: String): String? {
        val user = when {
            map.containsKey(login.trim()) -> map[login.trim()]
            map.containsKey(login.clearLogin()) -> map[login.clearLogin()]
            else -> null
        }
        return user?.run {
            if (checkPassword(password) || checkAccessCode(password)) this.userInfo
            else null
        }
    }

    private fun String.clearLogin(): String {
        return this.replace("[^+\\d]".toRegex(), "")
    }

    fun clearHolder() {
        map.clear()
    }

    fun requestAccessCode(login: String) {
        val user = when {
            map.containsKey(login.trim()) -> map[login.trim()]
            map.containsKey(login.clearLogin()) -> map[login.clearLogin()]
            else -> null
        }

        user?.updateAccessCode()
    }

    // " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;"
    fun importUsers(list: List<String>): List<User>{
        val users: MutableList<User> = mutableListOf()
        users.dropLastUntil { it.login == "2" }
        for (line in list) {
            val split = line.split(";")
            val fullName = split[0].trim().split(" ")
            val phone = when {
                split[3].isNotEmpty() -> split[3]
                else -> null
            }
            val email = when {
                split[1].isNotEmpty() -> split[1]
                else -> null
            }
            val passSalt = split[2].split(":")
            users.add(User(fullName[0], fullName[1], email, phone, passSalt[0], passSalt[1]))
        }
        return users
    }
}