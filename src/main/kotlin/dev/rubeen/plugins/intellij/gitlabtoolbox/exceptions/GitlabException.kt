package dev.rubeen.plugins.intellij.gitlabtoolbox.exceptions

class GitlabException(message: String? = "Generic Gitlab API Exception", cause: Throwable? = null) : Exception(message, cause)