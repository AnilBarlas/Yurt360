package com.example.yurt360.common.model

import kotlinx.serialization.Serializable

@Serializable
enum class ApplicationType {
    ROOM_CHANGE,
    COMPLAINT,
    SUGGESTION
}