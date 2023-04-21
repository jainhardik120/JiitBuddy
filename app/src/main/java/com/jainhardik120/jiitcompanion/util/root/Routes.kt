package com.jainhardik120.jiitcompanion.util.root

sealed class Screen(val route: String){
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")

    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}

