package com.twincom.chatgpt.requestbody

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestBody(
    @SerializedName("model")
    @Expose
    val model: String,
    @SerializedName("prompt")
    @Expose
    val prompt: String,
    @SerializedName("max_tokens")
    @Expose
    val max_tokens: Int,
    @SerializedName("temperature")
    @Expose
    val temperature: Int
)
