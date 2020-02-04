package ru.skillbranch.kotlinexample

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
                    throw IllegalArgumentException("A user with this phone already exists")
                }
            }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        return User.makeUser(fullName, phone = rawPhone)
            .also { user ->
                if (!map.containsKey(user.login) && checkValidPhone(user.login)) {
                    map[user.login] = user
                } else {
                    throw IllegalArgumentException("A user with this phone already exists")
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
}