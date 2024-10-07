package utils

expect val CurrentPlatform: Platform

enum class Platform {
  Ios,
  Desktop, // soon
  Android,
}
