package id.xxx.module.auth.domain.model

import java.io.Serializable

data class User(
    val id: String,
    val isNew: Boolean,
    val provider: String
) : Serializable